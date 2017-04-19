/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package profoak;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 *
 * @author Colin
 */
public class JarLoader {
 
    /**
     * Load all classes from a JAR file
     * @param url
     * @return classes
     * @throws Exception 
     */
    public static LinkedList<Class> LoadJar(String url) throws Exception{
        JarFile jarfile = new JarFile(url);
        Enumeration<JarEntry> e = jarfile.entries();
        
        URL[] urls = { new URL("jar:file:" + url+"!/") }; //might be URL[] urls = { new URL("jar:" + pathToJar+"!/") };
        URLClassLoader loader = URLClassLoader.newInstance(urls);
        
        LinkedList<Class> classes = new LinkedList<Class>();
        
        while(e.hasMoreElements()){
            JarEntry je = e.nextElement();
            if(je.isDirectory() || !je.getName().endsWith(".class")){
                //Not a java class
                continue;
            }
            String className = je.getName().substring(0, je.getName().length() - 6);
            className = className.replace("/", ".");
            Class c = loader.loadClass(className);
            classes.add(c);
        }
        
        return classes;
    }
    
    /**
     * Uses reflection to invoke the "main" method of a given class. Usually used after loading the class with the JarLoader 
     * @param class 
     */
    public static void InvokeMain(Class clazz){
        try{
            Method m = clazz.getMethod("main", String[].class);
            String[] params = new String[0];
            if(m != null){
                m.invoke(null, (Object)params);
            }
        }catch(Exception e){}
    }
    
}