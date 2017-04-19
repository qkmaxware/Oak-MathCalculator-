/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package profoak.core.functions;

import profoak.core.CoreException;
import profoak.core.Scope;
import profoak.core.ast.AST;
import profoak.core.value.Value;

/**
 *
 * @author Colin Halseth
 */
public class UserDefined extends Function{
    
    public AST functionTree;
    public String outputName;
    
    public UserDefined(String...names){
        super(names);
    }

    @Override
    public Value Evaluate(Scope scope, Value... params) {
        if(params.length < this.param_names.length)
            throw new CoreException("Missing required function parameters");
        
        //Set formal parameters in new scope
        for(int i = 0; i < this.param_names.length; i++){
            scope.Set(this.param_names[i], params[i]);
        }
        
        //Function is empty
        if(functionTree == null)
            return null;
        
        //Actually call the function
        Object out = functionTree.Execute(scope);
        
        if(outputName != null){
            Value result = scope.GetLocal(outputName);

            //Return the result
            if(result != null)
                return result;
        }
        
        return null;
    }
    
}
