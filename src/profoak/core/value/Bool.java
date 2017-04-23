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
public class Bool {
    
    public static final Bool True = new Bool(true);
    public static final Bool False = new Bool(false);
    
    private boolean value;
    
    public Bool(){
        this.value = false;
    }
    
    public Bool(Bool b){
        this.value = b.value;
    }
    
    public Bool(boolean v){
        this.value = v;
    }
    
    public static Bool Parse(String value){
        return new Bool(Boolean.parseBoolean(value));
    }
    
    public boolean IsTrue(){
        return this.value == true;
    }
    
    public Bool Equals(Bool b){
        return new Bool(this.value == b.value);
    }
    
    public Bool Negate(){
        return new Bool(!this.value);
    }
    
    public static Bool Or(Bool a, Bool b){
        return new Bool(a.value || b.value);
    }
    
    public static Bool Xor(Bool a, Bool b){
        return new Bool((a.value || b.value) && (!a.value || !b.value));
    }
    
    public static Bool And(Bool a, Bool b){
        return new Bool(a.value && b.value);
    }
    
    public String toString(){
        return String.valueOf(value).toUpperCase();
    }
    
}
