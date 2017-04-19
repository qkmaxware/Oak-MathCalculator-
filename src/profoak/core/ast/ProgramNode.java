/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package profoak.core.ast;

import profoak.core.Scope;

/**
 *
 * @author Colin Halseth
 */
public class ProgramNode implements AST{

    private AST[] children;
    
    @Override
    public AST[] GetChildren() {
        return children;
    }

    @Override
    public void SetChild(int index, AST child) {
        this.children[index] = child;
    }

    @Override
    public Object Execute(Scope scope) {
        Object result = null;
        for(int i = 0; i < this.children.length; i++){
            result = (this.children[i].Execute(scope));
        }
        return result;
    }

    @Override
    public void SetChildren(AST[] children) {
        this.children = children;
    }
    
}
