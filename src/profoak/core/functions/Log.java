/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package profoak.core.functions;

import profoak.core.CoreException;
import profoak.core.Scope;
import profoak.core.value.Matrix;
import profoak.core.value.Single;
import profoak.core.value.Value;

/**
 *
 * @author Colin Halseth
 */
public class Log extends Function{

    @Override
    public Value Evaluate(Scope scope, Value... params) {
        if(params.length < 2){
            throw new CoreException("Missing parameters for logarithmic function. Required 2");
        }
        
        Value o = params[0];
        Value b = params[1];
        
        Value result = null;
        
        if(!(b instanceof Single))
            throw new CoreException("Second parameter must be single valued");
        
        if(o instanceof Matrix){
            result = ((Matrix)o).operate((in) -> {
                return Log(in,(Single)b);
            });
        }else if(o instanceof Single){
            
        }else{
            throw new CoreException("First parameter cannot be of type: "+b.getClass());
        }
        
        return null;
    }
    
    
    private Single Log(Single s, Single b){
        return null;
    }
}
