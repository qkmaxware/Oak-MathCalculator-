/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package profoak;

import plus.JSON.JSONproperty;
import profoak.core.OakSpeakParser;
import profoak.core.ast.AST;

/**
 *
 * @author Colin Halseth
 */
public class ScriptInterface implements JSONproperty{
    
    private String script;
    private String name = "untitled";
    private String saveLocation = null;
    
    private static final OakSpeakParser parser = new OakSpeakParser();
    
    public void SetName(String name){
        this.name = name;
    }
    
    public void Save(){
        String name = this.name.replace(".oak", "");
        FileWriter writer = new FileWriter(saveLocation+".oak");
        writer.Write(script);
        writer.Save();
    }
    
    public String GetLocation(){
        return this.saveLocation;
    }
    
    public void SetLocation(String url){
        this.saveLocation = url;
    }
    
    public String GetName(){
        return name;
    }
    
    public String GetText(){
        return this.script;
    }
    
    public void SetText(String script){
        this.script = script;
    }

    public AST Compile(){
        return parser.Compile(this.GetText());
    }
    
    @Override
    public String ToJSON() {
        return "{"+"\"name\":\""+name+"\","+"\"script\":\""+this.script+"\""+"}";
    }
    
}
