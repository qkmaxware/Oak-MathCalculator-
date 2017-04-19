/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package profoak.core.ast;

import profoak.core.Scope;
import profoak.core.functions.UserDefined;

/**
 *
 * @author Colin Halseth
 */
public class FunctionDefinitionNode implements AST{

    private AST[] children = new AST[1];
    
    public NameNode name;
    public NameNode[] params;
    public NameNode output;
    
    @Override
    public AST[] GetChildren() {
        return children;
    }

    @Override
    public void SetChild(int index, AST child) {
        this.children[index] = child; 
    }

    @Override
    public void SetChildren(AST[] children) {
        this.children = children;
    }

    @Override
    public Object Execute(Scope scope) {
        String fname = name.name;
        String oname = (output == null) ? null : output.name;
        
        String[] params = new String[this.params.length];
        for(int i = 0; i < this.params.length; i++)
            params[i] = this.params[i].name;
        
        UserDefined fn = new UserDefined(params);
        fn.functionTree = this.children[0];
        fn.outputName = oname;
        
        scope.Define(fname, fn);
        return null;
    }
    
}
