/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package profoak;

import java.lang.reflect.Method;

/**
 *
 * @author Colin Halseth
 */
public class ToolInterface {
    
    private Class clazz;
    private Method main = null;
    private String name;
    
    private static String mainMethod = "start";//"main";
    
    public ToolInterface(Class clazz){
        this.clazz = clazz;
        this.name = clazz.getSimpleName().replaceAll("(.)([A-Z])", "$1 $2");
        try{
            main = clazz.getMethod(mainMethod, Object[].class);
        }catch(Exception e){
        
        }
    }
    
    public static boolean IsToolable(Class clazz){
        try{
            Method m = clazz.getMethod(mainMethod, Object[].class);
            if(m == null)
                return false;
            return true;
        }catch(Exception e){
            return false;
        }
    }
    
    public String GetName(){
        return this.name;
    }
    
    public void Launch(Object program, profoak.core.Scope rootScope){
        if(main != null)
        try{
            main.invoke(null, (Object)(new Object[]{program, rootScope}));
        }catch(Exception e){
        
        }
    }
    
}
