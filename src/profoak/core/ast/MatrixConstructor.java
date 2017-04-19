/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package profoak.core.ast;

import profoak.core.value.Matrix;
import profoak.core.Scope;
import profoak.core.value.Single;

/**
 *
 * @author Colin Halseth
 */
public class MatrixConstructor implements AST{

    //private AST[] childExpressions;
    private AST[][] childExpressions;
    
    @Override
    public AST[] GetChildren() {
        return null;
    }

    @Override
    public void SetChild(int index, AST child) {}

    @Override
    public void SetChildren(AST[] children) {}

    public void SetComponents(AST[][] children){
        this.childExpressions = children;
    }
    
    @Override
    public Object Execute(Scope scope) {
        Scope next = scope;
        //Create matrix and return it
        Single[][] v = new Single[childExpressions.length][];
        for(int r = 0; r < childExpressions.length; r++){
            v[r] = new Single[childExpressions[r].length];
            for(int c = 0; c < this.childExpressions[r].length; c++){
                Object j = this.childExpressions[r][c].Execute(next);
                if(j == null)
                    return null;
                
                if(j instanceof Single)
                    v[r][c] = (Single)j;
                else
                    return null;
            }
        }
        
        Matrix m = new Matrix(v);
        
        return m;
    }
    
}
