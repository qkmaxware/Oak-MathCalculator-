/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package profoak.core.functions;

import plus.math.Mathx;
import profoak.core.CoreException;
import profoak.core.Scope;
import profoak.core.value.Matrix;
import profoak.core.value.Single;
import profoak.core.value.Value;

/**
 *
 * @author Colin Halseth
 */
public class Row extends Function{

    private boolean isColumn = false;
    
    public Row(boolean isColumn){
        this.isColumn = isColumn;
    }
    
    @Override
    public Value Evaluate(Scope scope, Value... params) {
        if(params.length < 3)
            throw new CoreException("Missing reqired parameters for function: "+(isColumn?"column":"row"));
        
        if(params[0] instanceof Single && params[1] instanceof Single && params[2] instanceof Single){
            //TODO Allow complex
            Single startInd = ((Single)params[0]);
            Single endInd = ((Single)params[2]);
            int iterations = (int)((Single)params[1]).GetReal().getDecimalValue();

            Single[] values = new Single[iterations];
            for(int i = 0; i < values.length; i++){
                values[i] = Single.Lerp(startInd, endInd, i/(iterations-1.0));
            }
            
            if(isColumn){
                return new Matrix(values.length, 1, values);
            }else{
                return new Matrix(1, values.length, values);
            }
        }else{
            throw new CoreException("Function 'row' requires all arguments to be numeric");
        }
    }
    
}
