/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package profoak.core.functions;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import javax.swing.JFrame;
import javax.swing.JPanel;
import plus.math.Mathx;
import profoak.core.CoreException;
import profoak.core.Scope;
import profoak.core.value.Matrix;
import profoak.core.value.Value;

/**
 *
 * @author Colin Halseth
 */
public class Plot extends Function{

    public Color backColor = Color.WHITE;
    public Color axisColor = Color.BLACK;
    public Color lineColor = Color.RED;
    
    @Override
    public Value Evaluate(Scope scope, Value... params) {
        if(params.length < 2)
            throw new CoreException("Missing reqired parameters for function: plot");
        
        if(params[0] instanceof Matrix && params[1] instanceof Matrix){
            JFrame frame = ConstructViewport((Matrix)params[0], (Matrix)params[1]);
            frame.setSize(640, 480);
            frame.setVisible(true);
        }else{
            throw new CoreException("Function 'plot' requires both arguments to be row matrices.");
        }
        
        return null;
    }
    
    public JFrame ConstructViewport(Matrix Xs, Matrix Ys){
        JFrame view = new JFrame();
        view.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        view.setTitle("Plot");
        
        double[] msX = FindMinMax(Xs);
        double[] msY = FindMinMax(Ys);
        
        double minX = msX[0];
        double maxX = msX[1];
        double minY = msY[0] - 2;
        double maxY = msY[1] + 2;
        
        double XDifference = maxX - minX;
        double YDifference = maxY - minY;
        
        JPanel port = new JPanel(){
            @Override
            public void paintComponent(Graphics g){
                Graphics2D g2 = (Graphics2D)g;
                
                int pixelHeight = this.getHeight();
                int pixelWidth = this.getWidth();
                
                int fontHeight = g2.getFontMetrics().getHeight();
                
                //Background Fill
                g2.setColor(backColor);
                g2.fillRect(0, 0, pixelWidth, pixelHeight);
                
                //Draw axis (x,y)
                g2.setColor(axisColor);
                g2.drawString("("+minX+","+minY+")", 3, pixelHeight - 6);
                g2.drawString("("+maxX+")", pixelWidth - g2.getFontMetrics().stringWidth("("+maxX+")") - 2, pixelHeight - 6);
                g2.drawString("("+maxY+")", 3, 0 + fontHeight + 3);
                g2.drawLine(0, pixelHeight-1, pixelWidth, pixelHeight-1);
                g2.drawLine(1, 0, 1, pixelHeight-2);
                
                int iterations = Math.min(Xs.GetColumns(), Ys.GetColumns());
                
                Path2D.Double path = new Path2D.Double();
                
                for(int i = 0; i < iterations; i++){
                    double x = Xs.Get(0, i).GetReal().getDecimalValue();
                    double y = Ys.Get(0, i).GetReal().getDecimalValue();
                    
                    //Convert real x,y to screen x,y
                    double rX = Mathx.Lerp(0, pixelWidth, Mathx.Clamp((maxX - x)/XDifference,0.0,1.0));
                    double rY = (y - minY)*pixelHeight/YDifference; //wrong?
                    
                    if(i == 0){
                        path.moveTo(rX, rY);
                    }else{
                        path.lineTo(rX, rY);
                    }
                }
                
                g2.setColor(lineColor);
                g2.draw(path);
            }
        };
        
        view.add(port);
        
        return view;
    }

    private static double[] FindMinMax(Matrix m){
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        
        for(int i = 0; i < m.GetColumns(); i++){
            double v = m.Get(0, i).GetReal().getDecimalValue();
            if(v > max)
                max = v;
            if(v < min)
                min = v;
        }
        
        return new double[]{min,max};
    }
    
}
