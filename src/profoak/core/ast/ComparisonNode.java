/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package profoak.core.ast;

import profoak.core.CoreException;
import profoak.core.Scope;
import profoak.core.value.Value;

/**
 *
 * @author Colin Halseth
 */
public class ComparisonNode implements AST{

    private AST[] children = new AST[2];
    public Mode mode = Mode.EqualTo;
    public static enum Mode{
        LessThan, GreaterThan, EqualTo
    }
    
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
        Object one = children[0].Execute(scope);
        Object two = children[1].Execute(scope);
        
        if(one instanceof Value && two instanceof Value){
            if(mode == Mode.LessThan){
                return ((Value)one).Less((Value)two);
            }else if(mode == Mode.GreaterThan){
                return ((Value)one).Greater((Value)two);
            }else{
                return ((Value)one).Equal((Value)two);
            }
        }
        
        throw new CoreException("Cannot compare non value types");
    }
    
}
