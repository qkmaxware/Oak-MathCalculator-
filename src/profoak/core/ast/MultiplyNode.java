/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package profoak.core.ast;

import profoak.core.CoreException;
import profoak.core.Scope;
import profoak.core.value.Matrix;
import profoak.core.value.Value;

/**
 * component-wise multiplication
 * @author Colin Halseth
 */
public class MultiplyNode implements AST{
    
    public static enum Mode{
        ScalarMultiply,
        ScalarDivide,
        MatrixMultiply
    }
    
    private AST[] children = new AST[2];
    public Mode mode = Mode.MatrixMultiply;
    
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
        
        if(mode == Mode.ScalarMultiply){
            return one.mul(two);
        }else if(mode == Mode.ScalarDivide){
            return one.div(two);
        }else if(mode == Mode.MatrixMultiply){
            if(one instanceof Matrix && two instanceof Matrix){
                return ((Matrix)one).matMul(((Matrix)two));
            }else{
                throw new CoreException("Cannot perform a matrix multiplcation if the two terms are not matrices.");
            }
        }
        
        return null;
    }
    
}
