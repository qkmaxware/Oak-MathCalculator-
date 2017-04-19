/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package profoak.windows;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.JFrame;
import javax.swing.*;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import profoak.OakSettings;

/**
 *
 * @author Colin Halseth
 */
public class ColorSettings extends JFrame{
    
    private OakSettings settings;
    int selection = -1;
    
    public ColorSettings(OakSettings settings){
        super();
        this.settings = settings;
        this.setTitle("Theme Settings");
        this.setSize(new Dimension(480, 440));
        
        JPanel pn = new JPanel();
        pn.setLayout(new BoxLayout(pn, BoxLayout.Y_AXIS));
        
        JPanel colors = new JPanel(new GridLayout(2,2));
        JLabel l = new JLabel("Font Color"); l.setHorizontalAlignment(JLabel.CENTER);
        colors.add(l);
        l = new JLabel("Background Color"); l.setHorizontalAlignment(JLabel.CENTER);
        colors.add(l);
        JButton fontColor = new JButton(); fontColor.setBackground(settings.FontColor);
        fontColor.addActionListener((evt) -> {
            selection = 0;
        });
        JButton bgColor = new JButton(); bgColor.setBackground(settings.BackgroundColor);
        bgColor.addActionListener((evt) -> {
            selection = 1;
        });
        colors.add(fontColor); colors.add(bgColor);
        
        JColorChooser textColor = new JColorChooser();
        
        ColorSelectionModel model = textColor.getSelectionModel();
        ChangeListener changeListener = new ChangeListener() {
            public void stateChanged(ChangeEvent changeEvent) {
                Color c = textColor.getColor();
                switch(selection){
                    case 0:
                        fontColor.setBackground(c);
                        settings.FontColor = c;
                        break;
                    case 1:
                        bgColor.setBackground(c);
                        settings.BackgroundColor = c;
                        break;
                }
                selection = -1;
            }
        };
        model.addChangeListener(changeListener);
        
        pn.add(colors);
        pn.add(textColor);
        
        JButton apply = new JButton("Apply");
        apply.addActionListener((evt) -> {
            settings.Save();
            this.setVisible(false);
        });
        pn.add(apply);
        
        this.add(pn);
        
    }
    
}
