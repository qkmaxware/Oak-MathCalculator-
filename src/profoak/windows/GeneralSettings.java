/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package profoak.windows;

import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.*;

/**
 *
 * @author Colin Halseth
 */
public class GeneralSettings extends JFrame{
    
    public GeneralSettings(){
        super();
        this.setTitle("General Settings");
        this.setSize(new Dimension(480, 350));
        
        this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));
        
        JCheckBox autoInsertBrackets = new JCheckBox("Add parenthesis in pairs");
        this.add(autoInsertBrackets);
        
        JCheckBox steps = new JCheckBox("Show steps if possible");
        this.add(steps);
        
        //JColorChooser textColor = new JColorChooser();
        //this.add(textColor);
    }
    
}
