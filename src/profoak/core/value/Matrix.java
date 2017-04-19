/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package profoak.core.value;

import java.util.Arrays;
import plus.system.functional.Func1;
import plus.system.functional.Func2;
import profoak.core.CoreException;

/**
 *
 * @author Colin Halseth
 */
public class Matrix implements Value{
    
    private Single[] values;
    private int rows = 0;
    private int columns = 0;
    
    public static Matrix identity(int row, int column){
        Matrix a = new Matrix(row, column);
        for(int i = 0; i < Math.min(row, column); i++){
            a.Set(i, i, Single.one);
        }
        return a;
    }
    
    public static Matrix fill(int row, int column, Single value){
        Matrix a = new Matrix(row, column);
        for(int i = 0; i < a.values.length; i++){
            a.values[i] = value;
        }
        return a;
    }
    
    public Matrix(int rows, int columns, Single...values){
        this.rows = rows;
        this.columns = columns;
        
        this.values = new Single[rows * columns];
        Arrays.fill(this.values, 0, this.values.length, Single.zero);
        if(values.length > 0)   
        System.arraycopy(values, 0, this.values, 0, Math.min(this.values.length, values.length));
    }

    public Matrix(double[][] dvalues){
        Single[][] values = new Single[dvalues.length][];
        for(int i = 0; i < dvalues.length; i++){
            values[i] = new Single[dvalues[i].length];
            for(int j = 0; j < values[i].length; j++){
                values[i][j] = new Single(dvalues[i][j]);
            }
        }
        
        int maxColumnSize = 0;
        int maxRowSize = values.length;

        //For each row, find largest #'s of columns
        for (int i = 0; i < values.length; i++) {
            if (values[i].length > maxColumnSize) {
                maxColumnSize = values[i].length;
            }
        }

        //Copy
        this.rows = maxRowSize;
        this.columns = maxColumnSize;
        this.values = new Single[rows * columns];
        
        Arrays.fill(this.values, 0, this.values.length, Single.zero);
        
        //For each row
        for (int i = 0; i < values.length; i++) {
            //For each column
            for (int j = 0; j < values[i].length; j++) {
                this.Set(i, j, values[i][j]);
            }
        }
    }
    
    public Matrix(Single[][] values){
        int maxColumnSize = 0;
        int maxRowSize = values.length;

        //For each row, find largest #'s of columns
        for (int i = 0; i < values.length; i++) {
            if (values[i].length > maxColumnSize) {
                maxColumnSize = values[i].length;
            }
        }

        //Copy
        this.rows = maxRowSize;
        this.columns = maxColumnSize;
        this.values = new Single[rows * columns];
        
        Arrays.fill(this.values, 0, this.values.length, Single.zero);
        
        //For each row
        for (int i = 0; i < values.length; i++) {
            //For each column
            for (int j = 0; j < values[i].length; j++) {
                this.Set(i, j, values[i][j]);
            }
        }
    }
    
    public int GetRows(){
        return this.rows;
    }
    
    public int GetColumns(){
        return this.columns;
    }
    
    private int GetIndex2d(int row, int column) {
        return row * columns + column;
    }

    /**
     * Set a value in this matrix
     * @param row
     * @param column
     * @param v 
     */
    public void Set(int row, int column, Single v) {
        values[GetIndex2d(row, column)] = v;
    }

    /**
     * Get a value from this matrix
     * @param row
     * @param column
     * @return 
     */
    public Single Get(int row, int column) {
        return values[GetIndex2d(row, column)];
    }
    
    @Override
    public Value add(Value other) {
        Matrix o = (Matrix)other;
        Matrix m = new Matrix(this.rows, this.columns);
        for(int i = 0; i < m.values.length; i++){
            m.values[i] = this.values[i].add(o.values[i]);
        }
        return m;
    }

    @Override
    public Value sub(Value other) {
        Matrix o = (Matrix)other;
        Matrix m = new Matrix(this.rows, this.columns);
        for(int i = 0; i < m.values.length; i++){
            m.values[i] = this.values[i].sub(o.values[i]);
        }
        return m;
    }

    @Override
    public Value div(Value other) {
        if(other instanceof Single){
            return this.operate((in) -> {
                return in.div((Single)other);
            });
        }else{
            return Matrix.operate(this, (Matrix)other, (a,b)->{
                return a.div(b);
            });
        }
    }

    public Matrix matMul(Matrix other){
            if (this.columns != other.rows) {
                throw new RuntimeException("Cannot multiply matrices. Dimentions do not match.");
            }

            Matrix r = new Matrix(this.rows, other.columns);

            for (int i = 0; i < this.rows; i++) {
                for (int j = 0; j < other.columns; j++) {
                    Single sum = Single.zero;
                    for (int k = 0; k < this.columns; k++) {
                        sum = sum.add(this.Get(i, k).mul(other.Get(k, j)));
                    }
                    r.Set(i, j, sum);
                }
            }

            return r;
    }
    
    @Override
    public Value mul(Value o) {
        if(o instanceof Single){
            return this.operate((in) -> {
                return in.mul((Single)o);
            });
        }
        else{  
            //Replace with componentWise
            return Matrix.operate(this, (Matrix)o, (a,b) -> { return a.mul(b); });
            //return this.matMul((Matrix)o);
        }
    }

    @Override
    public Value pow(Value other) {
        if(other instanceof Single){
            Single s = (Single)other;
            return this.operate((in) -> {
                return in.pow(s);
            });
        }else{
            throw new CoreException("Cannot perform the power function where the exponent is a matrix.");
        }
    }
    
    public Bool Greater(Value other){
        
        return Bool.False;
    }
    
    public Bool Less(Value other){

        return Bool.False;
    }
    
    public Bool Equal(Value other){

        return Bool.False;
    }
    
    public static Matrix operate(Matrix a, Matrix b, Func2<Single,Single, Single> fn){
        if(a.rows != b.rows || a.columns != b.columns)
            throw new CoreException("Cannot perform element-wise operations on matrices of differing sizes.");
        Matrix m = new Matrix(a.rows, a.columns);
        for(int i = 0; i < a.values.length; i++){
            m.values[i] = fn.Invoke(a.values[i], b.values[i]);
        }
        return m;
    }
    
    public Matrix operate(Func1<Single,Single> fn){
        Matrix m = new Matrix(this.rows, this.columns);
        for(int i = 0; i < this.values.length; i++){
            m.values[i] = fn.Invoke(this.values[i]);
        }
        return m;
    }
    
    public String toString(){
        String str = "";
        for(int r = 0; r < this.rows; r++){
            String start = (r == 0 ? "" : "\n")+"| ";
            for(int c = 0; c < this.columns; c++){
                start += (c == 0 ? "" : ", ") + this.Get(r, c).toString();
            }
            start += " |";
            str += start;
        }
        return str;
    }

    @Override
    public String size() {
        return this.rows+"x"+this.columns;
    }

    @Override
    public String encoding() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        for(int r = 0; r < this.rows; r++){
            if(r != 0)
                builder.append(";");
            for(int c = 0; c < this.columns; c++){
                if(c != 0)
                    builder.append(",");
                builder.append(this.Get(r, c).encoding());
            }
        }
        builder.append("}");
        return builder.toString();
    }
    
}
