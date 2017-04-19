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
public class OrNode implements AST{
    
    private AST[] children = new AST[2];
    
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
        Object j = this.children[1].Execute(next);
        
        if((i instanceof Bool) && (j instanceof Bool)){
            Bool one = (Bool)i;
            Bool two = (Bool)j;
            return Bool.Or(one, two);
        }
        
        throw new CoreException("Cannot perform boolean OR on non-boolean values");
    }
    
}
