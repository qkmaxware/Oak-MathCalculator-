/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package profoak.core.ast;

import java.util.Arrays;
import profoak.core.CoreException;
import profoak.core.Scope;
import profoak.core.functions.Function;
import profoak.core.value.Value;

/**
 *
 * @author Colin Halseth
 */
public class FunctionCallNode implements AST{

    private AST[] args;
    
    @Override
    public AST[] GetChildren() {
        return args;
    }

    @Override
    public void SetChild(int index, AST child) {
        args[index] = child;
    }

    @Override
    public void SetChildren(AST[] children) {
        args = children;
    }

    @Override
    public Object Execute(Scope scope) {
        if(!(args[0] instanceof NameNode)){
            return null;
        }
        
        Scope next = scope.NextDepth();
        
        String name = ((NameNode)args[0]).name;
        Object result = null;
        
        //Obtain the result
        Function fn = scope.Call(name);
        
        //Create the parameters
        Value[] params = new Value[this.args.length - 1];
        for(int i = 1; i < this.args.length; i++){
            Object o = this.args[i].Execute(next);
            if(o instanceof Value)
                params[i-1] = (Value)o;
            else
                throw new CoreException("Cannot resolve function parameter to a value");
        }
        
        //Call the function if it exists
        if(fn != null){
            result = fn.Evaluate(next, params);
        }else{
            throw new CoreException("No function exists with name '"+ name + "'");
        }
        
        //Return the function results
        return result;
    }
    
}
