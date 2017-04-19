/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package profoak.core;

import profoak.core.value.Value;
import java.lang.reflect.Method;
import java.util.HashMap;
import javax.swing.table.DefaultTableModel;
import plus.JSON.JSONarray;
import plus.JSON.JSONitem;
import plus.JSON.JSONobject;
import plus.JSON.JSONparser;
import profoak.core.functions.Function;

/**
 *
 * @author Colin Halseth
 */
public class Scope {
    
    private static JSONparser parser = new JSONparser();
    
    private Scope parent = null;
    private Scope root = null; 
    
    public static final Scope global = new Scope();
    private HashMap<String, Data> values = new HashMap<String, Data>();
    private HashMap<String, Function> definitions = new HashMap<String, Function>();
    private int depth = 0;
    
    public static class Data{
        public String name;
        public Value value;
        public Class type;
        public String size;
        public Data(){}
        public Data(String name, Value value, Class type, String size){
            this.name = name; this.value = value; this.type = type; this.size = size;
        }
    }
    
    public void Set(String name, Value value){
        Data data = new Data();
        data.name = name;
        data.type = value.getClass();
        data.value = value;
        data.size = value.size();
        values.put(name, data);
    }
    
    public void Define(String name, Function fn){
        definitions.put(name, fn);
    }
    
    public Value Get(String name){
        if(values.containsKey(name)){
            return values.get(name).value;
        }else if(parent != null){
            return parent.Get(name);
        }else if(global.values.containsKey(name)){
            return global.values.get(name).value;
        }else{
            return null;
        }
    }
    
    public Value GetLocal(String name){
        if(values.containsKey(name)){
            return values.get(name).value;
        }else{
            return null;
        }
    }
    
    public Function Call(String name){
        if(definitions.containsKey(name)){
            return definitions.get(name);
        }else if(parent != null){
            return parent.Call(name);
        }else if(global.definitions.containsKey(name)){
            return global.definitions.get(name);
        }else{
            return null;
        }
    }
    
    public Function CallLocal(String name){
        if(definitions.containsKey(name)){
            return definitions.get(name);
        }else{
            return null;
        }
    }
    
    public void Clear(){
        this.values.clear();
    }
    
    public Scope NextDepth(){
        Scope s = new Scope();
        s.parent = this;
        s.depth = this.depth + 1;
        s.root = (this.root == null)? this : this.root;
        return s;
    }
    
    public int GetDepth(){
        return depth;
    }
    
    public DefaultTableModel GetTabulatedStore(){
        Object[] vals = this.values.values().toArray();
        Object[][]  obj = new Object[vals.length][4];
        int k = 0;
        for(Object data : vals){
            Data d = (Data)data;
            obj[k++] = new Object[]{d.name, d.type.getSimpleName(), d.size, (d.value == null ? "null" : d.value.encoding())};
        }

        String[] columns = new String[]{"Name","Type","Size","Value"};  
        DefaultTableModel tableModel = new DefaultTableModel(obj, columns){
            @Override
            public boolean isCellEditable(int row, int col){
                return false;
            }
        };
        return tableModel;
    }
    
    public String ToJSON() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        
        boolean first = true;
        for(Data data : this.values.values()){
            if(!first){
                builder.append(",");
            }
            first = false;
            
            builder.append("{");
            
            builder.append("\"name\": \""+data.name+"\",");
            builder.append("\"type\": \""+data.type.getCanonicalName()+"\",");
            builder.append("\"size\": \""+data.size+"\",");
            builder.append("\"value\": \""+data.value.encoding()+"\"");
            
            builder.append("}");
        }
        
        builder.append("]");
        return builder.toString();
    }
    
    public void FromJSON(String json) throws Exception{
            JSONarray array = (JSONarray)parser.Parse(json);
            for(int i = 0; i < array.Count(); i++){
                JSONobject data = (JSONobject)array.Get(i);
                Data sdat = new Data();

                String name = (String)((JSONitem)data.Get("name")).Get();
                String type = (String)((JSONitem)data.Get("type")).Get();
                String size = (String)((JSONitem)data.Get("size")).Get();
                String encoded_value = (String)((JSONitem)data.Get("value")).Get();

                sdat.name = name;
                sdat.size = size;
                sdat.type = Class.forName(type);
                //Try to decode encoded_value and store as sdat.value
                try{
                    Method m = sdat.type.getMethod("Parse", String.class);
                    sdat.value = (Value)m.invoke(null, (Object)encoded_value);
                }catch(Exception e){
                    System.out.println(e);
                    sdat.value = null;
                }
                
                this.values.put(name, sdat);
            }
    }
    
}
