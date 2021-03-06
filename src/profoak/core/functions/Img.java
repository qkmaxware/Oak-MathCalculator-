/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package profoak.core.functions;

import profoak.core.CoreException;
import profoak.core.Scope;
import profoak.core.value.Fraction;
import profoak.core.value.Matrix;
import profoak.core.value.Single;
import profoak.core.value.Value;

/**
 *
 * @author Colin Halseth
 */
public class Img extends Function{

    @Override
    public Value Evaluate(Scope scope, Value... params) {
        if(params.length < 1)
            throw new CoreException("Missing reqired parameters for function: img");
         
         if(params[0] instanceof Matrix){
             return ((Matrix)params[0]).operate((in) -> {
                 return GetImaginary(in);
             });
         }else if(params[0] instanceof Single){
             return GetImaginary((Single)params[0]);
         }
         
         throw new CoreException("Parameter is not of type 'Matrix' or 'Single'");
    }
    
    private Single GetImaginary(Single s){
        return new Single(s.GetImaginary(), Fraction.zero);
    }
}
