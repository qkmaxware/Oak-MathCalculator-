/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package profoak.core.ast;

import profoak.core.CoreException;
import profoak.core.Scope;
import profoak.core.functions.Function;
import profoak.core.value.Bool;
import profoak.core.value.Value;

/**
 *
 * @author Colin Halseth
 */
public class IfNode implements AST{

    private AST[] children = new AST[1];
    public AST boolExp;
    
    @Override
    public AST[] GetChildren() {
        return children;
    }

    @Override
    public void SetChild(int index, AST child) {
        children[index] = child;
    }

    @Override
    public void SetChildren(AST[] children) {
        this.children = children;
    }

    @Override
    public Object Execute(Scope scope) {
        if(boolExp == null)
            throw new CoreException("Expecting the condition of an if statement to evaluate to a boolean value.");
        
        Scope nextScope = scope;
        
        Object conditional = boolExp.Execute(nextScope);
        
        if(conditional instanceof Bool){
            if(((Bool)conditional).IsTrue() && this.children[0] != null){
                return this.children[0].Execute(nextScope);
            }
        }else{
            throw new CoreException("Expecting the condition of an if statement to evaluate to a boolean value.");
        }
        
        return null;
    }
    
}
