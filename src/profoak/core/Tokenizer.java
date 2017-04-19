/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package profoak.core;

import java.util.LinkedList;

/**
 *
 * @author Colin Halseth
 */
public class Tokenizer {
    
    private ParseToken[] matchTokens;
    
    private static final String EMPTY_STRING = "";
    
    private boolean trimNewlines = true;
    
    public Tokenizer(ParseToken...tokens){
        this.matchTokens = tokens;
    }
    
    public Tokenizer(boolean trimNewlines, ParseToken...tokens){
        this.matchTokens = tokens;
        this.trimNewlines = trimNewlines;
    }
    
    public LinkedList<ParseToken.TokenMatch> Tokenize(String in){
        in = Trim(in, this.trimNewlines);
        LinkedList<ParseToken.TokenMatch> list = new LinkedList<ParseToken.TokenMatch>();
        
        while(!in.isEmpty()){
            boolean matched = false;
            
            //Search for token match
            for(ParseToken tok: matchTokens){
                ParseToken.TokenMatch match = tok.Extract(in, this.trimNewlines);
                if(match == null)
                    continue;
                
                in = Trim(in.substring(match.endPos),trimNewlines);
                list.add(match);
                matched = true;
                break;
            }
            
            //No match found
            if(!matched){
                throw new RuntimeException("Unrecognized symbol for "+in);
            }
        }
        
        return list;
    }
    
    private String Trim(String in, boolean includeNewline){
        if(includeNewline)
            return in.trim(); //Trim all whitespace
        else
            return in.replaceFirst("^[\\r\\t\\f ]+", EMPTY_STRING); //Trim all starting whitespace except newline characters
    }
    
}