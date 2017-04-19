/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package profoak.core.functions;

import plus.system.Random;
import profoak.core.CoreException;
import profoak.core.Scope;
import profoak.core.value.Matrix;
import profoak.core.value.Single;
import profoak.core.value.Value;

/**
 *
 * @author Colin Halseth
 */
public class Ones extends Function{

    public Ones(){
        super("r","c");
    }
    
    @Override
    public Value Evaluate(Scope scope, Value... params) {
        if(params.length < 2)
            throw new CoreException("Missing reqired parameters for function: ones");
        
        if(params[0] instanceof Single && params[1] instanceof Single){
            int rows = (int)((Single)params[0]).GetReal().getDecimalValue();
            int cols = (int)((Single)params[1]).GetReal().getDecimalValue();
            
            if(rows < 1 || cols < 1)
                throw new CoreException("The number of rows and columns must be larger than 0");
            
            Matrix m = Matrix.fill(rows, cols, Single.one);
            
            return m;
        }
        
        throw new CoreException("Given parameters not resolved to single values");
    }
    
}
