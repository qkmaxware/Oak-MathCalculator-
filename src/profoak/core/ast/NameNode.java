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
public class NameNode implements AST{

    public AST[] children = new AST[0];
    public String name;
    
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
        Object value = scope.Get(name);
        return (value == null)? name : value;
    }
    
}