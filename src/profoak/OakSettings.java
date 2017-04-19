/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package profoak;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import plus.JSON.JSONitem;
import plus.JSON.JSONobject;
import plus.JSON.JSONparser;
import plus.JSON.JSONproperty;

/**
 *
 * @author Colin Halseth
 */
public class OakSettings implements JSONproperty{
    
    public int X = 640;
    public int Y = 480;
    
    public Color FontColor = Color.BLACK;
    public Color BackgroundColor = Color.WHITE;
    
    public OakSettings(){
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        d = new Dimension((int)(d.width * 0.9), (int)(d.height * 0.9));
        X = d.width; 
        Y = d.height;
    }
    
    public Dimension GetSize(){
        return new Dimension(X,Y);
    }
    
    public void Save(){
        FileWriter settingsWriter = new FileWriter("app.json",false);
        settingsWriter.Write(this.ToJSON());
        settingsWriter.Save();
    }
    
    public void FromJSON(String json){
        JSONparser parser = new JSONparser();
        JSONobject settings = (JSONobject)parser.Parse(json);
        
        Long width = (Long)(((JSONitem)settings.Get("width")).Get());
        Long height = (Long)(((JSONitem)settings.Get("height")).Get());
        
        Color fcolor = ParseColorJSON((JSONobject)settings.Get("fontcolor"));
        Color bcolor = ParseColorJSON((JSONobject)settings.Get("bgcolor"));
        
        X = width.intValue();
        Y = height.intValue();
        
        FontColor = fcolor;
        BackgroundColor = bcolor;
    }
    
    private Color ParseColorJSON(JSONobject color){
        int r = ((Long)((JSONitem)color.Get("r")).Get()).intValue();
        int g = ((Long)((JSONitem)color.Get("g")).Get()).intValue();
        int b = ((Long)((JSONitem)color.Get("b")).Get()).intValue();
        
        return new Color(r, g, b);
    }
    
    @Override
    public String ToJSON() {
        StringBuffer str = new StringBuffer();
        str.append("{");
        
        str.append("\"width\": "+X+",");
        str.append("\"height\": "+Y+",");
        str.append("\"fontcolor\": "+ColorJSON(FontColor)+",");
        str.append("\"bgcolor\": "+ColorJSON(BackgroundColor));
        
        str.append("}");
        return str.toString();
    }
    
    private String ColorJSON(Color color){
        return "{"+
                "\"r\":"+color.getRed()+"," +
                "\"g\":"+color.getGreen()+"," +
                "\"b\":"+color.getBlue()
                +"}";
    }
    
}
