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
public class WhileNode implements AST{

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
            throw new CoreException("Expecting the condition of an while statement to evaluate to a boolean value.");
        
        Scope nextScope = scope;
        
            Object oj = null;
            while(this.children[0] != null){
                Object conditional = boolExp.Execute(nextScope);
                
                if(conditional instanceof Bool && ((Bool)conditional).IsTrue()){
                    oj = this.children[0].Execute(nextScope);
                }else{
                    break;
                }
            }
            return oj;
    }
    
}