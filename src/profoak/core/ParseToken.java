/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package profoak.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Colin Halseth
 */
public class ParseToken {
 
    private static final String EMPTY_STRING = "";
    
    public static class TokenMatch{
        public ParseToken token;
        public String match;
        public int startPos;
        public int endPos;
        
        protected TokenMatch(){}
        
        public boolean equals(Object other){
            if(other instanceof TokenMatch){
                return ((TokenMatch)other).token.name.equals(this.token.name);
            }
            else if(other instanceof ParseToken){
                return this.token.name.equals(((ParseToken)other).name);
            }
            return false;
        }
        
        public String toString(){
            return token.name;
        }
        
    }
    
    private String name;
    private Pattern pattern;
    
    public ParseToken(String name, String regex){
        this.pattern = Pattern.compile(regex);
        this.name = name;
    }

    public String Name(){
        return name;
    }
    
    public TokenMatch Extract(String in, boolean trimNewlines){
        Matcher m = pattern.matcher(in);
        boolean found = m.find();
        if(found){
            TokenMatch match = new TokenMatch();
            
            String matchText = Trim(in.substring(m.start(),m.end()), trimNewlines);
            match.match = matchText;
            match.startPos = m.start();
            match.endPos = m.end();
            match.token = this;
            
            return match;
        }
        else 
            return null;
    }
    
    private String Trim(String in, boolean includeNewline){
        if(includeNewline)
            return in.trim(); //Trim all whitespace
        else
            return in.replaceFirst("^[\\r\\t\\f ]+", EMPTY_STRING); //Trim all starting whitespace except newline characters
    }
    
}