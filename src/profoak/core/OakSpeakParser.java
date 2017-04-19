/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package profoak.core;

import profoak.core.value.Value;
import profoak.core.value.Single;
import java.util.LinkedList;
import profoak.core.ast.*;
import profoak.core.value.Bool;

/**
 *
 * @author Colin Halseth
 */
public class OakSpeakParser {
    
    private Tokenizer tokenizer;
    private ParseToken[] syntax = new ParseToken[]{
            new ParseToken("add","^\\+"),
            new ParseToken("sub","^\\-"),
            new ParseToken("mul","^\\*"),
            new ParseToken("div","^\\/"),
            new ParseToken("pow","^\\^"),
            new ParseToken("def","^def"),
            new ParseToken("booleanliteral","^(?:TRUE|FALSE)"),
            new ParseToken("number","^[0-9]+(?:\\.[0-9]+)?(?:[ij])?"),
            new ParseToken("identifier","^[a-zA-Z][a-zA-Z0-9]*"),
            new ParseToken("open","^\\("),
            new ParseToken("close","^\\)"),
            new ParseToken("smat","^\\{"),
            new ParseToken("emat","^\\}"),
            new ParseToken("comma","^\\,"),
            new ParseToken("row","^\\;"),
            new ParseToken("equals","^\\="),
            new ParseToken("openInd","^\\["),
            new ParseToken("closeInd","^\\]"),
            new ParseToken("mapping","^\\:"),
            new ParseToken("matmul","^\\."),
            new ParseToken("booleanless","^\\<"),
            new ParseToken("booleangreater","^\\>"),
            new ParseToken("booleanand","^\\&"),
            new ParseToken("booleanor","^\\|"),
            new ParseToken("booleannot","^\\!"),
            new ParseToken("comment","^#.*#"),
            new ParseToken("if","^if"),
            new ParseToken("until","^until")
        };
    
    private final int ADD = 0;
    private final int SUB = 1;
    private final int MUL = 2;
    private final int DIV = 3;
    private final int POW = 4;
    private final int DEF = 5;
    private final int NUM = 7;
    private final int ID = 8;
    private final int OPEN_BRACKET = 9;
    private final int CLOSE_BRACKET = 10;
    private final int START_MATRIX = 11;
    private final int END_MATRIX = 12;
    private final int COMMA = 13;
    private final int ROW_BREAK = 14;
    private final int EQUALS_SIGN = 15;
    private final int OPEN_INDEX = 16;
    private final int CLOSE_INDEX = 17;
    private final int MAPPING = 18;
    private final int MATRIX_MULTIPLICATION = 19;
    private final int END_STATEMENT = 14; //TMP
    private final int BOOL_LESS = 20;
    private final int BOOL_GREATER = 21;
    private final int BOOL_AND = 22;
    private final int BOOL_OR = 23;
    private final int BOOL_NOT = 24;
    private final int BOOL_LITERAL = 6;

    public OakSpeakParser(){
        tokenizer = new Tokenizer(
            true,
            syntax
        );
    }

    public ProgramNode Compile(String script){
        //Tokenize
        RQueue<ParseToken.TokenMatch> tokens = new RQueue<ParseToken.TokenMatch>(
                StripComments(tokenizer.Tokenize(script))
        );
        
        //System.out.println(tokens);
        
        //Create parse tree
        AST root = ParseProgram(tokens);
        
        return (ProgramNode)root;
    }
    
    private LinkedList<ParseToken.TokenMatch> StripComments(LinkedList<ParseToken.TokenMatch> tokens){
        LinkedList<ParseToken.TokenMatch> stripped = new LinkedList<ParseToken.TokenMatch>();
        for(ParseToken.TokenMatch match : tokens){
            if(!match.token.Name().equals("comment")){
                stripped.add(match);
            }
        }
        return stripped;
    }
    
    private AST ParseProgram(RQueue<ParseToken.TokenMatch> tokens){
        ProgramNode program = new ProgramNode();
        LinkedList<AST> statements = new LinkedList<AST>();
        
        while(!tokens.isEmpty()){
            AST statement = ParseStatement(tokens);
            if(statement == null)
                break;
            statements.add(statement);
            boolean isAnother = ParseNewStatement(tokens);
            if(!isAnother)
                break;
        }
        
        AST[] states = new AST[statements.size()];
        states = statements.toArray(states);
        program.SetChildren(states);
        
        return program;
    }
    
    // NEWLINE to separate statement lines
    private boolean ParseNewStatement(RQueue<ParseToken.TokenMatch> tokens){
        if(tokens.isEmpty())
            return false;
        
        if(tokens.peek().equals(syntax[this.END_STATEMENT])){
            tokens.pollFirst();
            return true;
        }
        
        return false;
    }
    
    private AST ParseStatement(RQueue<ParseToken.TokenMatch> tokens){
        int restore = tokens.RestorePoint();
        
        AST statement = ParseBooleanExpression(tokens);
        if(statement != null)
            return statement;
        tokens.RestoreTo(restore);
        
        statement = ParseAssignment(tokens);
        if(statement != null)
            return statement;
        tokens.RestoreTo(restore);
        
        statement = ParseExpression(tokens);
        if(statement != null)
            return statement;
        tokens.RestoreTo(restore);
        
        statement = ParseFunctionDefinition(tokens);
        if(statement != null)
            return statement;
        tokens.RestoreTo(restore);
        
        return null; 
    }
    
    //--------------------------------------------------------------------------
    //BOOLEAN EXPRESSION
    
    //term | term OR term
    private AST ParseBooleanExpression(RQueue<ParseToken.TokenMatch> tokens){
        if(tokens.isEmpty())
            return null;
        
        AST or1 = ParseBooleanAnd(tokens);
        
        if(or1 == null)
            return null;
        
        AST value = or1;
        
        //Loop for multiple terms
        while(true){
            if(tokens.isEmpty() || !tokens.peek().equals(syntax[BOOL_OR])){
                return value;
            }
            tokens.pollFirst();
            
            AST or2 = ParseBooleanAnd(tokens);
            if(or2 == null)
                return null;
            
            //Return OR node
            OrNode node = new OrNode();
            node.SetChild(0, value);
            node.SetChild(1, or2);
            
            value = node;
        }
        
    }
    //term | term AND term
    private AST ParseBooleanAnd(RQueue<ParseToken.TokenMatch> tokens){
        if(tokens.isEmpty())
            return null;
        
        AST and1 = ParseBooleanTerm(tokens);
        
        if(and1 == null)
            return null;
        
        AST value = and1;
        
        //Loop for multiple terms
        while(true){
            if(tokens.isEmpty() || !tokens.peek().equals(syntax[BOOL_AND])){
                return value;
            }
            tokens.pollFirst();
            
            AST and2 = ParseBooleanTerm(tokens);
            if(and2 == null)
                return null;
            
            //Return AND node
            AndNode node = new AndNode();
            node.SetChild(0, value);
            node.SetChild(1, and2);
            
            value = node;
        }
    }
    //term | !term
    private AST ParseBooleanTerm(RQueue<ParseToken.TokenMatch> tokens){
        if(tokens.peek().equals(syntax[BOOL_NOT])){
            tokens.pollFirst();
            AST value = ParseBooleanLiteral(tokens);
            
            //return not node
            NotNode node = new NotNode();
            node.SetChild(0, value);
            return node;
        }
        
        AST value = ParseBooleanLiteral(tokens);
        
        return value;
    }
    //literal | comparision | ( expression )
    private AST ParseBooleanLiteral(RQueue<ParseToken.TokenMatch> tokens){
        int point = tokens.RestorePoint();
        //Literal
        if(tokens.peek().equals(syntax[BOOL_LITERAL])){
            //return literal value
            ValueNode node = new ValueNode();
            node.value = Bool.Parse(tokens.peek().match);
            tokens.pollFirst();
            return node;
        }
        tokens.RestoreTo(point);
        
        //Comparision
        AST value = ParseBooleanComparision(tokens);
        if(value != null)
            return value;
        tokens.RestoreTo(point);
        
        //Bracketed exp
        if(tokens.peek().equals(syntax[OPEN_BRACKET])){
           tokens.pollFirst();
           value = ParseBooleanExpression(tokens);
           if(tokens.peek().equals(syntax[CLOSE_BRACKET])){
               return value;
           }
        }
        tokens.RestoreTo(point);
        
        return null;
    }
    
    private AST ParseBooleanComparision(RQueue<ParseToken.TokenMatch> tokens){
        int point = tokens.RestorePoint();
        
        //Parse left hand side
        AST exp1 = ParseExpression(tokens);
        
        if(exp1 == null)
            return null;
        
        ComparisonNode node = new ComparisonNode();
        
        //Parse operator
        if(tokens.peek().equals(syntax[BOOL_LESS])){
            node.mode = ComparisonNode.Mode.LessThan;
        }else if(tokens.peek().equals(syntax[BOOL_GREATER])){
            node.mode = ComparisonNode.Mode.GreaterThan;
        }else if(tokens.peek().equals(syntax[EQUALS_SIGN])){
            tokens.pollFirst();
            if(tokens.peek().equals(syntax[EQUALS_SIGN])){
                node.mode = ComparisonNode.Mode.EqualTo;
            }else{
                return null;
            }
        }else{
            return null;
        }
        tokens.pollFirst();
        
        //Parse right hand side
        AST exp2 = ParseExpression(tokens);
        
        if(exp2 == null)
            return null;
        
        node.SetChild(0, exp1);
        node.SetChild(1, exp2);
        return node;
    }
    
    //--------------------------------------------------------------------------
    //VARIABLE ASSIGNMENT
    
    private AST ParseAssignment(RQueue<ParseToken.TokenMatch> tokens){
        AST name = ParseIdentifier(tokens);
        if(name == null)
            return null;
        boolean equals = ParseEquals(tokens);
        if(!equals)
            return null;
        
        AST exp = ParseExpression(tokens);
        if(exp == null)
            return null;
        
        //Return AST TODO
        AssignmentNode set = new AssignmentNode();
        set.SetChild(0, name);
        set.SetChild(1, exp);
        return set;
    }
    
    private boolean ParseEquals(RQueue<ParseToken.TokenMatch> tokens){
        if(!tokens.isEmpty() && tokens.peek().equals(syntax[this.EQUALS_SIGN])){
            tokens.pollFirst();
            return true;
        }
        
        return false;
    }
    
    //--------------------------------------------------------------------------
    //MATHEMATICAL EXPRESSION
    
    //Expression: Term | Term { +- Term }+
    private AST ParseExpression(RQueue<ParseToken.TokenMatch> tokens){
        AST term1 = ParseTerm(tokens);
        
        if(term1 == null)
            return null;
        
        AST value = term1;
        
        //Loop for multiple terms
        while(true){
            int op = ParseAddSub(tokens);
            if(op == -1)
                return value;
            
            AST term2 = ParseTerm(tokens);
            if(term2 == null)
                return null;
            
            AddNode node = new AddNode();
            node.SetChild(0, value);
            node.SetChild(1, term2);
            if(op == 2)
                node.subtract = true;
            
            value = node;
        }
    }
    
    private int ParseAddSub(RQueue<ParseToken.TokenMatch> tokens){
        if(tokens.isEmpty())
            return -1;
        
        if(ParseAdd(tokens)){
            return 1; //+
        }
        else if(ParseSub(tokens)){
            return 2; //-
        }
        
        return -1;
    }
    
    private boolean ParseAdd(RQueue<ParseToken.TokenMatch> tokens){
        if(!tokens.isEmpty() && tokens.peek().equals(syntax[ADD])){
            tokens.pollFirst();
            return true; //+
        }
        return false;
    }
    
    private boolean ParseSub(RQueue<ParseToken.TokenMatch> tokens){
        if(!tokens.isEmpty() &&tokens.peek().equals(syntax[SUB])){
            tokens.pollFirst();
            return true; //+
        }
        return false;
    }
    
    //Term: Factor | Factor { */ Factor } + | Factor { . Factor}+
    private AST ParseTerm(RQueue<ParseToken.TokenMatch> tokens){
        int restore = tokens.RestorePoint();
        //AST fn = ParseFunction(tokens);
        
        //if(fn != null){
            //tokens.RestoreTo(restore);
            //return fn;
        //}
        
        AST factor = ParseFactor(tokens);
        
        if(factor == null){
            tokens.RestoreTo(restore);
            return null;
        }
        
        AST value = factor;
        
        while(true){
            int op = ParseMulDiv(tokens);
            if(op == -1)
                return value;
            
            AST factor2 = ParseFactor(tokens);
            if(factor2 == null){
                tokens.RestoreTo(restore);
                return null;
            }
            
            MultiplyNode mul = new MultiplyNode();
            mul.SetChild(0, value);
            mul.SetChild(1, factor2);
            if(op == 1){
                mul.mode = MultiplyNode.Mode.ScalarMultiply;
            }else if(op == 2){
                mul.mode = MultiplyNode.Mode.ScalarDivide;
            }else if (op == 3){
                mul.mode = MultiplyNode.Mode.MatrixMultiply;
            }
            
            value = mul;
        }
    }
    
    private int ParseMulDiv(RQueue<ParseToken.TokenMatch> tokens){
        if(tokens.isEmpty())
            return -1;
        
        if(tokens.peek().equals(syntax[MUL])){
            tokens.pollFirst();
            return 1; //*
        }else if(tokens.peek().equals(syntax[DIV])){
            tokens.pollFirst();
            return 2; //\
        }else if(tokens.peek().equals(syntax[this.MATRIX_MULTIPLICATION])){
            tokens.pollFirst();
            return 3;
        }
        
        return -1;
    }
    
    //Factor: Exponent | Exponent ^ Exponent
    private AST ParseFactor(RQueue<ParseToken.TokenMatch> tokens){
        AST exp1 = ParseExponent(tokens);
        
        if(exp1 == null)
            return null;
        
        //^
        if(!tokens.isEmpty() && tokens.peek().equals(syntax[POW])){
            tokens.pollFirst();
            AST exp2 = ParseExponent(tokens);
            if(exp2 == null){
                return null;
            }
            
            PowerNode power = new PowerNode();
            power.SetChild(0, exp1);
            power.SetChild(1, exp2);
            return power;
        }
        
        return exp1;
    }
    
    //Exponent: Primitive, -Primitive
    private AST ParseExponent(RQueue<ParseToken.TokenMatch> tokens){
        int restore = tokens.RestorePoint();
        
        AST value = ParseNegation(tokens);
        if(value != null){
            return value;
        }
        tokens.RestoreTo(restore);
        
        value = ParsePrimitive(tokens);
        if(value != null){
            return value;
        }
        tokens.RestoreTo(restore);
        
        return null;
    }
    
    //Negation: -PRIMITIVE
    private AST ParseNegation(RQueue<ParseToken.TokenMatch> tokens){
        if(ParseSub(tokens)){
            MultiplyNode multiply = new MultiplyNode();
            multiply.mode = MultiplyNode.Mode.ScalarMultiply;
            ValueNode number = new ValueNode();
            number.value = new Single(-1,0);
            multiply.SetChild(0, number);
            
            AST value = ParsePrimitive(tokens);
            if(value != null) {
                multiply.SetChild(1, value);
                return multiply;
            }
            
            return null;
        }else{
            return null;
        }
    }
    
    //Primitive: Number | Function | Indexer | Identifier | Matrix | ( EXPRESSION )
    private AST ParsePrimitive(RQueue<ParseToken.TokenMatch> tokens){
        int restore = tokens.RestorePoint();
        
        if(tokens.isEmpty())
            return null;
        
        //PARSE NUMBER
        AST value = ParseNumber(tokens);
        if(value != null)
            return value;
        tokens.RestoreTo(restore);
        
        //PARSE FUNCTION
        value = ParseFunctionCall(tokens);
        if(value != null)
            return value;
        tokens.RestoreTo(restore);
        
        //PARSE INDEXER
        value = ParseIndexer(tokens);
        if(value != null)
            return value;
        tokens.RestoreTo(restore);
        
        //PARSE IDENTIFIER
        value = ParseIdentifier(tokens);
        if(value != null)
            return value;
        tokens.RestoreTo(restore);
        
        //PAARSE MATRIX
        value = ParseMatrix(tokens);
        if(value != null)
            return value;
        tokens.RestoreTo(restore);
        
        //OR PARSE BRACKETS
        value = ParseBracketedExpression(tokens);
        if(value != null)
            return value;
        tokens.RestoreTo(restore);
        
        //OR PARSE REGULAR EXPRESSION?
        //WHY IS THIS HERE...WTF
        //value = ParseExpression(tokens);
        //if(value != null)
            //return value;
        //tokens.RestoreTo(restore);
        
        return null;
    }
    
    //name(arglist)
    private AST ParseFunctionCall(RQueue<ParseToken.TokenMatch> tokens){
        //name
        AST ident = ParseIdentifier(tokens);
        if(ident == null)
            return null;
        
        //(
        if(!tokens.isEmpty() && tokens.peek().equals(syntax[OPEN_BRACKET])){
            tokens.pollFirst();
        }else{
            return null;
        }
        
        //arglist
        LinkedList<AST> args = ParseList(tokens);
        
        //)
        if(!tokens.isEmpty() && tokens.peek().equals(syntax[CLOSE_BRACKET])){
            tokens.pollFirst();
        }else{
            return null;
        }
        
        args.addFirst(ident);
        
        AST[] ar = new AST[args.size()];
        ar = args.toArray(ar);
        
        FunctionCallNode fn = new FunctionCallNode();
        fn.SetChildren(ar);
        
        return fn;
    }
    
    //Index a matrix. {matrix}[r,c], name[r,c], ( exp )[r,c]
    private AST ParseIndexer(RQueue<ParseToken.TokenMatch> tokens){
        int restore = tokens.RestorePoint();
        
        //Parse matrix to index
        AST ident = ParseIdentifier(tokens);
        if(ident == null){
            tokens.RestoreTo(restore);
            ident = ParseMatrix(tokens);
            if(ident == null){
                tokens.RestoreTo(restore);
                ident = ParseBracketedExpression(tokens);
                if(ident == null){
                    return null;
                }
            }
        }
        
        //Parse [
        if(!tokens.isEmpty() && tokens.peek().equals(syntax[OPEN_INDEX])){
            tokens.pollFirst();
        }else{
            return null;
        }
        
        //Parse Expression 1
        AST exp1 = ParseExpression(tokens);
        if(exp1 == null)
            return null;
        
        //Parse Comma
        if(!tokens.isEmpty() && tokens.peek().equals(syntax[COMMA])){
            tokens.pollFirst();
        }else{
            return null;
        }
        
        //Parse Expression 2
        AST exp2 = ParseExpression(tokens);
        if(exp2 == null)
            return null;
        
        //Parse ]
        if(!tokens.isEmpty() && tokens.peek().equals(syntax[CLOSE_INDEX])){
            tokens.pollFirst();
        }else{
            return null;
        }
        
        IndexerNode node  = new IndexerNode();
        node.SetChild(0, ident);
        node.SetChild(1, exp1);
        node.SetChild(2, exp2);
        
        return node;
    }
    
    //( Expression )
    private AST ParseBracketedExpression(RQueue<ParseToken.TokenMatch> tokens){
        if(tokens.isEmpty())
            return null;
        
        if(tokens.isEmpty() || !tokens.peek().equals(syntax[OPEN_BRACKET])){
            return null;
        }
        tokens.pollFirst();

        AST value = ParseExpression(tokens);
        
        if(tokens.isEmpty()|| !tokens.peek().equals(syntax[CLOSE_BRACKET])){
            return null;
        }
        tokens.pollFirst();
        
        return value;
    }
    
    //Number
    private AST ParseNumber(RQueue<ParseToken.TokenMatch> tokens){
        if(tokens.isEmpty())
            return null;
        
        if(tokens.peek().equals(syntax[NUM])){
            ValueNode value = new ValueNode();
            Value v = Single.Parse(tokens.peek().match);
            value.value = v;
            tokens.pollFirst();
            return value;
        }
        return null;
    }
    
    //Identifier
    private AST ParseIdentifier(RQueue<ParseToken.TokenMatch> tokens){
        if(tokens.isEmpty())
            return null;
        
        if(tokens.peek().equals(syntax[ID])){
            NameNode value = new NameNode();
            value.name = tokens.peek().match;
            tokens.pollFirst();
            return value;
        }
        return null;
    }
    
    private AST ParseMatrix(RQueue<ParseToken.TokenMatch> tokens){
        // {
        if(tokens.isEmpty() || !tokens.peek().equals(syntax[START_MATRIX])){
            return null;
        }
        tokens.pollFirst();
        
        //Create rows placeholder
        LinkedList<LinkedList<AST>> rows = new LinkedList<LinkedList<AST>>();

        while(true){
            LinkedList<AST> row = ParseRow(tokens);
            rows.add(row);
            
            if(!tokens.isEmpty() && tokens.peek().equals(syntax[ROW_BREAK])){
                tokens.pollFirst();
            }else{
                break;
            }
        }
        
        //Convert LinkedLists to Arrays
        AST[][] values = new AST[rows.size()][]; int i = 0;
        for(LinkedList<AST> row : rows){
            values[i] = new AST[row.size()];
            values[i] = row.toArray(values[i]);
            for(int j = 0; j < values[i].length; j++){
            }
            i++;
        }
        
        
        MatrixConstructor cons = new MatrixConstructor();
        cons.SetComponents(values);
        
        // }
        if(tokens.isEmpty() || !tokens.peek().equals(syntax[END_MATRIX])){
            return null;
        }
        tokens.pollFirst();
        
        return cons;
    }
    
    private LinkedList<AST> ParseRow(RQueue<ParseToken.TokenMatch> tokens){
        LinkedList<AST> row = ParseList(tokens);
        return row;
    }
    
    private LinkedList<AST> ParseList(RQueue<ParseToken.TokenMatch> tokens){
        LinkedList<AST> args = new LinkedList<AST>();
        boolean repeat = true;
        
        while(repeat){
            AST expression = ParseExpression(tokens);
            
            //Fail fast, empty matrix
            if(expression == null)
                break;
            
            args.add(expression);
            
            //If comma, continue row, else stop
            if(!tokens.isEmpty() && tokens.peek().equals(syntax[COMMA])){
                tokens.pollFirst();
            }else{
                repeat = false;
            }
        }
        
        return args;
    }
    
    private LinkedList<AST> ParseNameList(RQueue<ParseToken.TokenMatch> tokens){
        LinkedList<AST> args = new LinkedList<AST>();
        boolean repeat = true;
        
        while(repeat){
            AST expression = ParseIdentifier(tokens);
            
            //Fail fast, empty matrix
            if(expression == null)
                break;
            
            args.add(expression);
            
            //If comma, continue row, else stop
            if(!tokens.isEmpty() && tokens.peek().equals(syntax[COMMA])){
                tokens.pollFirst();
            }else{
                repeat = false;
            }
        }
        
        return args;
    }
    
    //def <name> ( params ) -> <output> { Statment-list } //TODO
    public AST ParseFunctionDefinition(RQueue<ParseToken.TokenMatch> tokens){
        
        if(!ParseDef(tokens))
            return null;
        
        //<name>
        AST name = ParseIdentifier(tokens);
        if(name == null)
            return null;
        
        // (
        if(!tokens.isEmpty() && tokens.peek().equals(syntax[OPEN_BRACKET])){
            tokens.pollFirst();
        }else{
            return null;
        }
        
        // Params
        LinkedList<AST> params = ParseNameList(tokens);
        
        
        // : <return name>
        AST returnName = null;
        if(!tokens.isEmpty() && tokens.peek().equals(syntax[MAPPING])){
            tokens.pollFirst();
            //<return name>
            returnName = ParseIdentifier(tokens); //Can be null
        }
        
        // )
        if(!tokens.isEmpty() && tokens.peek().equals(syntax[CLOSE_BRACKET])){
            tokens.pollFirst();
        }else{
            return null;
        }
        
        // {
        if(tokens.isEmpty() || !tokens.peek().equals(syntax[START_MATRIX])){
            return null;
        }
        tokens.pollFirst();
        
        //Statements
        AST prog = this.ParseProgram(tokens);
        
        // }
        if(tokens.isEmpty() || !tokens.peek().equals(syntax[END_MATRIX])){
            return null;
        }
        tokens.pollFirst();
        
        FunctionDefinitionNode def = new FunctionDefinitionNode();
        def.name = (NameNode)name;
        def.output = (NameNode)returnName;
        def.params = new NameNode[params.size()];
        def.params = params.toArray(def.params);
        def.SetChild(0, prog);
        
        return def;
    }
    
    //Def
    public boolean ParseDef(RQueue<ParseToken.TokenMatch> tokens){
        if(!tokens.isEmpty() && tokens.peek().equals(syntax[DEF])){
            tokens.pollFirst();
            return true;
        }
        return false;
    }
    
}
