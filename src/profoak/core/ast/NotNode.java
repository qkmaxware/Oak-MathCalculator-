/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package profoak.core.ast;

import profoak.core.CoreException;
import profoak.core.Scope;
import profoak.core.value.Bool;

/**
 *
 * @author Colin Halseth
 */
public class NotNode implements AST{
    
    private AST[] children = new AST[1];
    
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
        Scope next = scope;
        
        Object i = this.children[0].Execute(next);
        
        if((i instanceof Bool) ){
            Bool one = (Bool)i;
            return one.Negate();
        }
        
        throw new CoreException("Cannot perform boolean NOT on non-boolean values");
    }
    
}
