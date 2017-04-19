/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package profoak.core.functions;

import profoak.core.Scope;
import profoak.core.value.Value;

/**
 *
 * @author Colin Halseth
 */
public abstract class Function {
    
    protected String[] param_names;
    
    public Function(String... param_names){
        this.param_names = param_names;
    }
    
    public abstract Value Evaluate(Scope scope, Value ... params);
    
}
