/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package profoak.core.ast;

import profoak.core.Scope;
import profoak.core.value.Value;

/**
 *
 * @author Colin Halseth
 */
public class PowerNode implements AST{

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
        Value one = (Value)this.children[0].Execute(next);
        Value two = (Value)this.children[1].Execute(next);
        return one.pow(two); //child[0] ^ child[1]
    }
    
}
