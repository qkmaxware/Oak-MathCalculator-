/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package profoak.core.value;

/**
 *
 * @author Colin Halseth
 */
public interface Value {
    
    public String size();
    
    public String encoding();
    
    public Value add(Value other);
    public Value sub(Value other);
    public Value div(Value other);
    public Value mul(Value other);
    public Value pow(Value other);
    
    public Bool Greater(Value other);
    public Bool Less(Value other);
    public Bool Equal(Value other);
    
}
