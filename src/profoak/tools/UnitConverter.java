/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package profoak.tools;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.*;
import plus.math.ConversionTools;

/**
 *
 * @author Colin Halseth
 */
public class UnitConverter extends JFrame{

    int enumStart = 0;
    int enumEnd = 0;
    
    public UnitConverter(){
        super();
        this.setTitle("Unit Converter");
        this.setSize(new Dimension(300, 180));
        
        GridBagLayout layout = new GridBagLayout();
        this.setLayout(layout);
        
        JComboBox<String> types = new JComboBox<String>();
        types.addItem("Length");
        types.addItem("Area");
        types.addItem("Volume");
        types.addItem("Mass");
        types.addItem("Speed");
        types.addItem("Temperature");
        types.addItem("Angle");
        
        GridBagConstraints con = new GridBagConstraints();
        con.gridwidth = 1;
        con.gridheight = 1;
        con.fill = GridBagConstraints.HORIZONTAL;
        this.add(new JLabel("Convert: "), con);
        con.gridwidth = 3;
        this.add(types, con);
        
        JComboBox<String> from = new JComboBox<String>();
        JComboBox<String> to = new JComboBox<String>();
        
        GridBagConstraints con2 = new GridBagConstraints();
        con2.gridwidth = 1;
        con2.gridheight = 1;
        con2.gridy = 2;
        con2.gridx = 0;
        con2.fill = GridBagConstraints.HORIZONTAL;
        
        this.add(new JLabel("From: "), con2);
        con2.gridx = 1;
        this.add(from, con2);
        
        con2.gridx = 2;
        this.add(new JLabel("To: "), con2);
        con2.gridx = 3;
        this.add(to, con2);
        
        con2.gridwidth = 1;
        con2.gridx = 1;
        JTextField input = new JTextField();
        con2.gridy = 3;
        this.add(input, con2);
        
        JTextField output = new JTextField();
        output.setEditable(false);
        con2.gridy = 3;
        con2.gridx = 3;
        this.add(output, con2);
        
        JButton convert = new JButton("Convert");
        con.gridy = 4;
        con.gridx = 0;
        con.gridwidth = 4;
        this.add(convert, con);
        
        LoadDistance(from, to);
        
        convert.addActionListener((evt) -> {
            try{
            String s = String.valueOf(types.getSelectedItem());
            Double d = Double.parseDouble(input.getText());
            int i1 = from.getSelectedIndex();
            int i2 = to.getSelectedIndex();
            switch(s){
                case "Length":
                    Double o = ConversionTools.Convert(d, ConversionTools.Distance.values()[i1], ConversionTools.Distance.values()[i2]);
                    output.setText(o.toString());
                    break;
                case "Area":
                    o = ConversionTools.Convert(d, ConversionTools.Area.values()[i1], ConversionTools.Area.values()[i2]);
                    output.setText(o.toString());
                    break;
                case "Volume":
                    o = ConversionTools.Convert(d, ConversionTools.Volume.values()[i1], ConversionTools.Volume.values()[i2]);
                    output.setText(o.toString());
                    break;
                case "Mass":
                    o = ConversionTools.Convert(d, ConversionTools.Mass.values()[i1], ConversionTools.Mass.values()[i2]);
                    output.setText(o.toString());
                    break;
                case "Speed":
                    o = ConversionTools.Convert(d, ConversionTools.Speed.values()[i1], ConversionTools.Speed.values()[i2]);
                    output.setText(o.toString());
                    break;
                case "Temperature":
                    o = ConversionTools.Convert(d, ConversionTools.Temperature.values()[i1], ConversionTools.Temperature.values()[i2]);
                    output.setText(o.toString());
                    break;
                case "Angle":
                    o = ConversionTools.Convert(d, ConversionTools.Angle.values()[i1], ConversionTools.Angle.values()[i2]);
                    output.setText(o.toString());
                    break;
            }
            }
            catch(Exception e){
                output.setText("Undefined");
            }
        });
        
        types.addActionListener((evt) -> {
            String s = String.valueOf(types.getSelectedItem());
            switch(s){
                case "Length":
                    LoadDistance(to, from);
                    break;
                case "Area":
                    LoadArea(to, from);
                    break;
                case "Volume":
                    LoadVolume(to, from);
                    break;
                case "Mass":
                    LoadMass(to, from);
                    break;
                case "Speed":
                    LoadSpeed(to, from);
                    break;
                case "Temperature":
                    LoadTemp(to, from);
                    break;
                case "Angle":
                    LoadAngle(to, from);
                    break;
            }
        });
    }
    
    public void LoadAngle(JComboBox<String> from, JComboBox<String> to){
        from.removeAllItems();
        to.removeAllItems();
        for(ConversionTools.Angle dis : ConversionTools.Angle.values()){
            from.addItem(dis.toString());
            to.addItem(dis.toString());
        }
    }
    
    public void LoadTemp(JComboBox<String> from, JComboBox<String> to){
        from.removeAllItems();
        to.removeAllItems();
        for(ConversionTools.Temperature dis : ConversionTools.Temperature.values()){
            from.addItem(dis.toString());
            to.addItem(dis.toString());
        }
    }
    
    public void LoadSpeed(JComboBox<String> from, JComboBox<String> to){
        from.removeAllItems();
        to.removeAllItems();
        for(ConversionTools.Speed dis : ConversionTools.Speed.values()){
            from.addItem(dis.toString());
            to.addItem(dis.toString());
        }
    }
    
    public void LoadMass(JComboBox<String> from, JComboBox<String> to){
        from.removeAllItems();
        to.removeAllItems();
        for(ConversionTools.Mass dis : ConversionTools.Mass.values()){
            from.addItem(dis.toString());
            to.addItem(dis.toString());
        }
    }
    
    public void LoadDistance(JComboBox<String> from, JComboBox<String> to){
        from.removeAllItems();
        to.removeAllItems();
        for(ConversionTools.Distance dis : ConversionTools.Distance.values()){
            from.addItem(dis.toString());
            to.addItem(dis.toString());
        }
    }
    
    public void LoadArea(JComboBox<String> from, JComboBox<String> to){
        from.removeAllItems();
        to.removeAllItems();
        for(ConversionTools.Area dis : ConversionTools.Area.values()){
            from.addItem(dis.toString());
            to.addItem(dis.toString());
        }
    }
    
    public void LoadVolume(JComboBox<String> from, JComboBox<String> to){
        from.removeAllItems();
        to.removeAllItems();
        for(ConversionTools.Volume dis : ConversionTools.Volume.values()){
            from.addItem(dis.toString());
            to.addItem(dis.toString());
        }
    }
    
}
