/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package profoak;

import java.awt.Color;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JTextPane;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

/**
 *
 * @author Colin Halseth
 */
public class SyntaxHighlighter extends JTextPane{
    
    private class SytlingRule{
        Pattern regex;
        Color color;
    }
    
    public class AsyncStyler extends SwingWorker<Void,Object>{
        private LinkedList<SytlingRule> patterns = new LinkedList<SytlingRule>();
        private JTextPane editor;
        
        protected AsyncStyler(JTextPane pane, LinkedList<SytlingRule> patterns){
            editor = pane;
            this.patterns = patterns;
        }
        
        @Override
        protected Void doInBackground() throws Exception {
            StyleContext style = StyleContext.getDefaultStyleContext();
            AttributeSet defaultStyle = style.addAttribute(style.getEmptySet(), StyleConstants.Foreground, Color.BLACK);
            
            int length = editor.getDocument().getLength();
            String text = editor.getDocument().getText(0, length);
            
            //Apply default style
            editor.getStyledDocument().setCharacterAttributes(
                    0, 
                    length, 
                    defaultStyle, 
            true);
            //System.out.println("open");
            
            for(SytlingRule pattern : patterns){
                Matcher matcher = pattern.regex.matcher(text);
                AttributeSet textStyle = style.addAttribute(style.getEmptySet(), StyleConstants.Foreground, pattern.color);
                while(matcher.find()){
                    int s = matcher.start();
                    int e = matcher.end();
                    editor.getStyledDocument().setCharacterAttributes(
                            s, 
                            e - s, 
                            textStyle, 
                            true
                    );
                }
            }
            
            return null;
        }
    }

    private LinkedList<SytlingRule> patterns = new LinkedList<SytlingRule>();
    
    public SyntaxHighlighter(){
        SyntaxHighlighter ME = this;
        this.getDocument().addDocumentListener(new DocumentListener(){
            @Override
            public void insertUpdate(DocumentEvent de) {
                AsyncStyler worker = new AsyncStyler(ME, patterns);
                worker.execute();
            }

            @Override
            public void removeUpdate(DocumentEvent de) {
                AsyncStyler worker = new AsyncStyler(ME, patterns);
                worker.execute();
            }

            @Override
            public void changedUpdate(DocumentEvent de) {
                //THIS IS A PROBLEM
                
                //if(de.getLength() > 0){
                    //AsyncStyler worker = new AsyncStyler(ME, patterns);
                    //worker.execute();
                //}
            }
        });
    }
    
    public void AddStylingRule(String pattern, Color color){
        SytlingRule rule = new SytlingRule();
        rule.regex = Pattern.compile(pattern);
        rule.color = color;
        patterns.add(rule);
    }
    
    public String GetText(){
        return this.getText();
    }
    
    public void SetText(String text){
        this.setText(text);
    }
    
    public void SetBackgroundColor(Color color){
        this.setBackground(color);
    }
    
    public void SetFontColor(Color color){
        
    }
}
