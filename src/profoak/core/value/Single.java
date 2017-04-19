/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package profoak.core.value;

import java.math.BigDecimal;
import java.math.RoundingMode;
import plus.math.Mathx;

/**
 *
 * @author Colin Halseth
 */
public class Single implements Value{
    
    public static final Single zero = new Single(0,0);
    public static final Single one = new Single(1,0);
    public static final Single negative = new Single(-1,0);
    
    private Fraction real = Fraction.zero;
    private Fraction img = Fraction.zero;
    
    public Single(){}
    public Single(double real){
        this.real = new Fraction(real);
    }
    public Single(double real, double img){
        this.real = new Fraction(real);
        this.img = new Fraction(img);
    }
    public Single(Fraction real, Fraction img){
        this.real = (real);
        this.img = (img);
    }
    
    
    public static Single Parse(String value){
        boolean isImg = value.contains("i") || value.contains("j");
        String remainder = value.replaceAll("[ij]", "");
        double v = Double.parseDouble(remainder);
        Fraction f = new Fraction(v);
        Single s = new Single();
        if(isImg){
            s.img = f;
        }
        else{
            s.real = f;
        }
        return s;
    }
    
    public Fraction GetReal(){
        return this.real;
    }
    
    public Fraction GetImaginary(){
        return this.img;
    }
    
    /**
     * The complex argument
     * @return 
     */
    public Single Arg(){
        return new Single(Math.atan2(this.img.getDecimalValue(), this.real.getDecimalValue()));
    }
    
    /**
     * Add a complex number to this one
     * @param c
     * @return 
     */
    public Single add(Single c){
        return new Single(this.real.add(c.real), this.img.add(c.img));
    }
    
    /**
     * Subtract a complex number from this one
     * @param c
     * @return 
     */
    public Single sub(Single c){
        return new Single(this.real.sub(c.real), this.img.sub(c.img));
    }
    
    /**
     * Multiply this complex number by another
     * @param c
     * @return 
     */
    public Single mul(Single c){
        return new Single((this.real.mul(c.real)).sub(this.img.mul(c.img)), (this.real.mul(c.img)).add(this.img.mul(c.real)));
    }
    
    /**
     * Divide this complex number by another
     * @param c
     * @return 
     */
    public Single div(Single c){
        Fraction d = (c.real.mul(c.real)).add(c.img.mul(c.img));
        Fraction r = ((this.real.mul(c.real)).add(this.img.mul(c.img))).div(d);
        Fraction i = ((this.img.mul(c.real)).sub(this.real.mul(c.img))).div(d);
        return new Single(r,i);
    }
    
    public Single scale(double d){
        return this.mul(new Single(d));
    }
    
    /**
     * Compute z^c where z and c are both complex
     * @param c
     * @return 
     */
    public Single pow(Single c){
        //Fix this so I dont convert to decimal values (loses accuracy)
        
        //In exponential form 
        //(a+ib)^(c+id) = e^(ln(r)(c+id)+i theta (c+id))
        // -> ln(r)c + ln(r)id + i0c - 0d
        //e^(i theta) = cos0 + isin0
        //e^(ln(r)c - 0d) * e^(i(ln(r)*d + 0c))
        double r = Math.sqrt(this.real.mul(this.real).getDecimalValue() + this.img.mul(this.img).getDecimalValue());
        double theta = this.Arg().real.getDecimalValue();
        double lnr = Math.log(r);
        
        //e^(ln(r)c - 0d)
        double scalar = Math.pow(Math.E, lnr*c.real.getDecimalValue() - theta*c.img.getDecimalValue());
        
        //e^(i(ln(r)*d + 0c)) = e^(i a) = cos(a) + isin(a)
        double real = Math.cos(lnr*c.img.getDecimalValue() + theta*c.real.getDecimalValue());
        double img =  Math.sin(lnr*c.img.getDecimalValue() + theta*c.real.getDecimalValue());
        
        return new Single(scalar * real, scalar * img);
    }

    //exp(z)
    public static Single exp(Single s){
        //exp(x + iy) = exp(x)*exp(iy)
        //exp(iy) = cos(y) + isin(y)
        //exp(x +iy) = exp(x)*(cos(y) + isin(y))
        
        //lossy calculation NOTE
        
        double scalar = Math.pow(Math.E, s.real.getDecimalValue());
        double cos = Math.cos(s.img.getDecimalValue());
        double imgSin = Math.sin(s.img.getDecimalValue());
        
        return new Single((scalar*cos), (scalar*imgSin));
    }
    
    public static Single negative(Single s){
        return s.mul(Single.negative);
    }
    
    public static Single sin(Single s){
        //sin(z) = (exp(iz)-exp(-iz)) / (2i)
        double sin_x_e_ny = Math.sin(s.real.getDecimalValue()) * Math.pow(Math.E, -s.img.getDecimalValue());
        double sin_nx_e_y = Math.sin(-s.real.getDecimalValue()) * Math.pow(Math.E, s.img.getDecimalValue());
        double real = (1.0/2.0) * (sin_x_e_ny - sin_nx_e_y);
        
        double cos_x_e_ny = Math.cos(s.real.getDecimalValue()) * Math.pow(Math.E, -s.img.getDecimalValue());
        double cos_nx_e_y = Math.cos(-s.real.getDecimalValue()) * Math.pow(Math.E, s.img.getDecimalValue());
        double img = (-1.0/2.0) * (cos_x_e_ny - cos_nx_e_y);
        
        return new Single(real, img);
    }
    
    public static Single cos(Single s){
        
        double cos_x_e_ny = Math.cos(s.real.getDecimalValue()) * Math.pow(Math.E, -s.img.getDecimalValue());
        double cos_ny_e_y = Math.cos(-s.real.getDecimalValue()) * Math.pow(Math.E, s.img.getDecimalValue());
        double real = (1.0/2.0) * (cos_x_e_ny + cos_ny_e_y);
        
        double sin_x_e_ny = Math.sin(s.real.getDecimalValue()) * Math.pow(Math.E, -s.img.getDecimalValue());
        double sin_nx_e_y = Math.sin(-s.real.getDecimalValue()) * Math.pow(Math.E, s.img.getDecimalValue());
        
        double img = (1.0/2.0) * (sin_x_e_ny + sin_nx_e_y);
        
        return new Single(real, img);
    }
    
    @Override
    public Value add(Value other) {
        if(other instanceof Single){
            return this.add((Single)other);
        }
        return null;
    }

    @Override
    public Value sub(Value other) {
        if(other instanceof Single){
            return this.sub((Single)other);
        }
        return null;
    }

    public static Single Lerp(Single a, Single b, double t){
        return new Single(
                Mathx.Lerp(a.real.getDecimalValue(), b.real.getDecimalValue(), t),
                Mathx.Lerp(a.img.getDecimalValue(), b.img.getDecimalValue(), t)
        );
    }
    
    @Override
    public Value div(Value other) {
        if(other instanceof Single){
            return this.div((Single)other);
        }
        return null;
    }

    @Override
    public Value mul(Value other) {
        if(other instanceof Single){
            return this.mul((Single)other);
        }else if(other instanceof Matrix){
            return ((Matrix)other).mul(this);
        }
        return null;
    }

    @Override
    public Value pow(Value other) {
        if(other instanceof Single){
            return this.pow((Single)other);
        }
        return null;
    }
    
    public Bool Greater(Value other){
        if(other instanceof Single){
            Single s = (Single)other;
            return new Bool(this.real.Greater(s.real) || this.img.Greater(s.img));
        }
        return Bool.False;
    }
    
    public Bool Less(Value other){
        if(other instanceof Single){
            Single s = (Single)other;
            return new Bool(this.real.Less(s.real) || this.img.Less(s.img));
        }
        return Bool.False;
    }
    
    public Bool Equal(Value other){
        if(other instanceof Single){
            Single s = (Single)other;
            return new Bool(this.real.equals(s.real) && this.img.equals(s.img));
        }
        return Bool.False;
    }
    
    @Override //Rounding is performed for visual output
    public String toString(){
        String str = new BigDecimal(this.real.toString()).setScale(6,RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
        if(!this.img.equals(Fraction.zero)){
            str += (this.img.IsPositive() ? " + " : " ") + (new BigDecimal(this.img.toString()).setScale(6,RoundingMode.HALF_UP).stripTrailingZeros().toPlainString()) + "i";
        }
        return  str;
    }
   
    @Override
    public String size() {
        return "1x1";
    }

    @Override //No rounding, but the same
    public String encoding() {
         String str = this.real.toString();
        if(!this.img.equals(Fraction.zero)){
            str += (this.img.IsPositive() ? " + " : " - ") + this.img.toString() + "i";
        }
        return  str;
    }
    
}
