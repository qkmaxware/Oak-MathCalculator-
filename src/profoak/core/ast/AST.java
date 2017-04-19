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
public interface AST {
    
    public AST[] GetChildren();
    public void SetChild(int index, AST child);
    public void SetChildren(AST[] children);
    public Object Execute(Scope scope);
    
}
