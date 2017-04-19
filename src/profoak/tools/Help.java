/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package profoak.tools;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import javax.swing.JOptionPane;

/**
 *
 * @author Colin Halseth
 */
public class Help {
    
    public static File helpFile = new File("help/index.html");
    
    public static void ShowHelp(){
        OpenWebsite(helpFile.toURI().toString());
    }
    
    private static void OpenWebsite(String url){
        Runtime rt = Runtime.getRuntime();
        
        String os = System.getProperty("os.name").toLowerCase();
        boolean alternate = false;
        
        //Try using desktop first -- cleanest
        if(Desktop.isDesktopSupported()){
            Desktop desktop = Desktop.getDesktop();
            try{
                desktop.browse(new URI(url));
            }catch(Exception e){
                alternate = true;
            }
        }else{
            alternate = true;
        }
        
        if(!alternate)
            return;
        
        //Otherwise try direct terminal cmd
        try{
            if(os.contains("win")){
                //Windows
                rt.exec("start " + url);
            }else if(os.contains("mac")){
                //Macintosh
                rt.exec("open " + url);
            }else if(os.contains("nix") || os.contains("nux")){
                //Linux
                rt.exec("xdg-open " + url);
            }
        }catch(Exception e){
            //Everything fails
            JOptionPane.showMessageDialog(null, "Failed to open internet browser. The files are in question are located at: "+url);
        }
    }
    
}
