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
public class Fraction {

    public static final Fraction zero = new Fraction(0);
    public static final Fraction infinity = new Fraction(1,0);
    public static final Fraction one = new Fraction(1);
    //Rational approximations for E and PI
    public static final Fraction E = new Fraction(Math.E);
    public static final Fraction PI = new Fraction(Math.PI);
    
    private double top;
    private double bottom;

    /**
     * Create a new integer valued rational number
     * @param i 
     */
    public Fraction(long i){
        top = i;
        bottom = 1;
    }
    
    public Fraction(Fraction rt){
        top = rt.top;
        bottom = rt.bottom;
    }
    
    /**
     * Create a ration who's value is 0
     */
    public Fraction (){
        top = 0;
        bottom = 1;
    }
    
    /**
     * Creates a new rational from a fraction
     * @param top
     * @param bottom 
     */
    public Fraction(long top, long bottom){
        this.top = top;
        this.bottom = bottom;
    }
    
    /**
     * Create a new rational with a decimal value
     * @param ft 
     */
    public Fraction(double d){
        top = d;
        bottom = 1;
    }
    
    /**
     * Tests if the value of this fraction is positive or negative overall
     * @return 
     */
    public boolean IsPositive(){
        return (this.top < 0 && this.bottom < 0) || (this.top > 0 && this.bottom > 0);
    }
    
    /**
     * Add this rational to another
     * @param rt
     * @return rational
     */
    public Fraction add(Fraction rt){
        Fraction r = new Fraction();
        
        r.top = (this.top*rt.bottom) + (rt.top*this.bottom);
        r.bottom = (this.bottom*rt.bottom);
        
        return r;
    }
    
    /**
     * Subtract this rational by another
     * @param rt
     * @return rational
     */
    public Fraction sub(Fraction rt){
        Fraction r = new Fraction();
        
        r.top = (this.top*rt.bottom) - (rt.top*this.bottom);
        r.bottom = (this.bottom*rt.bottom);
        
        return r;
    }
    
    /**
     * Multiply this rational by another
     * @param rt
     * @return rational
     */
    public Fraction mul(Fraction rt){
        Fraction r = new Fraction();
        
        r.top = this.top * rt.top;
        r.bottom = this.bottom * rt.bottom;
        
        return r;
    }
    
    /**
     * Multiply this rational by an integer
     * @param rt
     * @return 
     */
    public Fraction mul(int rt){
        Fraction r = new Fraction();
        
        r.top = this.top * rt;
        r.bottom = this.bottom;
        
        return r;
    }
    
    /**
     * Divides this rational by another
     * @param rt
     * @return rational
     */
    public Fraction div(Fraction rt){
        Fraction r = new Fraction();
        
        r.top = this.top * rt.bottom;
        r.bottom = this.bottom * rt.top;
        
        return r;
    }
    
    /**
     * Divides this number by an integer
     * @param rt
     * @return 
     */
    public Fraction div(int rt){
        Fraction r = new Fraction();
        
        r.top = this.top;
        r.bottom = this.bottom * rt;
        
        return r;
    }
    
    /**
     * Computes a rational number to the power of a whole number
     * @param i
     * @return rational
     */
    public Fraction pow(int i){
        Fraction r = new Fraction();
        
        r.top = (int)Math.pow(this.top, i);
        r.bottom = (int)Math.pow(this.bottom, i);
        
        return r;
    }
    /**
     * Computes a rational number to a decimal power. Loss of precision can occur here
     * @param i
     * @return rational
     */
    public Fraction pow(float i){
        Fraction r = new Fraction();
        
        r.top = (int)Math.pow(this.top, i);
        r.bottom = (int)Math.pow(this.bottom, i);
        
        return r;
    }
    
    /**
     * Computes a rational number to a rational power. Loss of precision can occur here
     * @param i
     * @return rational
     */
    public Fraction pow(Fraction i){
        Fraction r = new Fraction();
        
        r.top = (int)Math.pow(this.top, i.getDecimalValue());
        r.bottom = (int)Math.pow(this.bottom, i.getDecimalValue());
        
        return r;
    }
    
    /**
     * Computes the inverse of this rational
     * @return 
     */
    public Fraction inverse(){
        Fraction r = new Fraction();
        
        r.top = this.bottom;
        r.bottom = this.top;
        
        return r;
    }
    
    /**
     * Gets the decimal value of this rational
     * @return 
     */
    public double getDecimalValue(){
        return (double)top/(double)bottom;
    }
    
    /**
     * This rational represented as a fraction
     * @return 
     */
    public String realString(){
        return top + "/" + bottom;
    }
    
    @Override
    public boolean equals(Object b){
        if(b instanceof Fraction){
            Fraction rt = (Fraction)b;
            return (this.getDecimalValue() == rt.getDecimalValue());
        }
        return false;
    }
    
    public boolean Greater(Fraction other){
        return this.getDecimalValue() > other.getDecimalValue();
    }
    
    public boolean Less(Fraction other){
        return this.getDecimalValue() < other.getDecimalValue();
    }
    
    /**
     * This rational represented as a decimal
     * @return 
     */
    public String toString(){
        return ""+getDecimalValue();
    }
    
}