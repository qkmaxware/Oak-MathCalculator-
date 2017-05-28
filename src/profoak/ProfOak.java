/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package profoak;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import plus.async.AsyncPool;
import plus.swing.CodeEditor;
import plus.swing.Console;
import plus.system.Debug;
import profoak.core.OakSpeakParser;
import profoak.core.Scope;
import profoak.core.ast.*;
import profoak.core.functions.*;
import profoak.core.value.Single;
import profoak.tools.*;
import profoak.windows.*;

/**
 * @author Colin Halseth
 */
public class ProfOak {
    
    private LinkedList<String> last_cmds = new LinkedList<String>(); 
    private LinkedList<ToolInterface> tools = new LinkedList<ToolInterface>();
    private LinkedList<ScriptInterface> scripts = new LinkedList<ScriptInterface>();
    private AsyncPool processors = new AsyncPool(2);
    private ScriptInterface activeScript = null;  
    private JFileChooser fileBrowser = new JFileChooser();
    private JList recentList;
    private profoak.core.Runtime runtime;
    private JTable workspaceTable;
    
    private OakSettings settings;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //Start gui on swing thread
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ProfOak oak = new ProfOak();
            }
        });
    }
    
    public static void Log(String msg){
        LocalDateTime time = LocalDateTime.now();
        int min = time.getMinute();
        System.out.println("["+(time.getHour()%12)+":"+(min < 10 ? "0"+min : min)+"] "+msg);
    }
    
    public ProfOak(){
        //Load oak-settings
        settings = new OakSettings();
        try{
            File f = new File("app.json");
            if(f.exists()){
                String json = String.join("\n", Files.readAllLines(f.toPath()));
                settings.FromJSON(json);
                System.out.println("Settings loaded from app.json");
            }
        }catch(Exception e){ e.printStackTrace(); }
        settings.Save();
        
        //Main frame
        JFrame frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.setTitle("Professor Oak - 0.02a");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(settings.GetSize());
        
        Dimension leftSize = new Dimension(300, 600);
        
        //Workspace
        runtime = new profoak.core.Runtime();
        //Temporarily add functions to the global scope (above root)
        Scope.global.Define("rand", new Rand()); //random function
        Scope.global.Define("ident", new Ident()); //identity matrix
        Scope.global.Define("ones", new Ones()); //ones matrix
        Scope.global.Define("zeros", new Zeros()); //zeros matrix
        Scope.global.Define("exp", new Exp()); //exponential function
        Scope.global.Define("sin", new Sin()); //sin function
        Scope.global.Define("cos", new Cos()); //cos function
        Scope.global.Define("plot", new Plot()); //Plot a function
        Scope.global.Define("row", new Row(false)); //Create row matrix
        Scope.global.Define("column", new Row(true)); //Create column matrix
        Scope.global.Define("re", new Re()); //extract real values
        Scope.global.Define("img", new Img()); //extract imaginary values
        Scope.global.Set("e", new Single(Math.E, 0)); //eulers constant
        Scope.global.Set("pi", new Single(Math.PI, 0)); //pi
        Scope.global.Set("c", new Single(299792458,0)); //speed of light
        Scope.global.Set("i", new Single(0,1)); //imaginary value
        
        fileBrowser.setCurrentDirectory(new File(Paths.get("").toAbsolutePath().toString())); //Current directory
        SyntaxHighlighter editor = new SyntaxHighlighter();
        Console console = new Console(">>");
        JTabbedPane openFileTabs = new JTabbedPane();
        openFileTabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        OakSpeakParser parser = new OakSpeakParser();
        workspaceTable = new JTable();
        recentList = new JList();
        
        //Load in colors
        console.SetFontColor(settings.FontColor);
        editor.SetFontColor(settings.FontColor);
        workspaceTable.setForeground(settings.FontColor);
        recentList.setForeground(settings.FontColor);
        
        console.setBackground(settings.BackgroundColor);
        editor.SetBackgroundColor(settings.BackgroundColor);
        workspaceTable.setBackground(settings.BackgroundColor);
        recentList.setBackground(settings.BackgroundColor);
        
        //Workspace
        TitledBorder workspaceBounds = new TitledBorder("Workspace");
        workspaceBounds.setTitleJustification(TitledBorder.CENTER);
        workspaceBounds.setTitlePosition(TitledBorder.TOP);
        
        UpdateWorkspace();
        workspaceTable.setPreferredSize(leftSize);
        JScrollPane workscroll = new JScrollPane(workspaceTable);
        workscroll.setPreferredSize(leftSize);
        workscroll.setBorder(workspaceBounds);
        
        JPanel left = new JPanel();
        left.setLayout(new GridLayout(0,1));
        left.add(workscroll);
        
        //Menu bar
        JMenuBar menubar = new JMenuBar();
        frame.setJMenuBar(menubar);
        
        JMenu file = new JMenu("File");
        menubar.add(file);
        file.setMnemonic(KeyEvent.VK_F);
        
        JMenu tools = new JMenu("Tools");
        menubar.add(tools);
        tools.setMnemonic(KeyEvent.VK_T);
        
        JMenu options = new JMenu("Settings");
        menubar.add(options);
        options.setMnemonic(KeyEvent.VK_O);
        
        //File Menu
        JMenuItem saveFile = new JMenuItem("Save File");
        saveFile.addActionListener((evt) -> {
            if(this.activeScript == null)
                return;
            
            String name = null;
            String loc = null;
            if(this.activeScript.GetLocation() == null){
                int returnVal = fileBrowser.showSaveDialog(null);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    name = fileBrowser.getSelectedFile().getName();
                    loc = fileBrowser.getSelectedFile().getAbsolutePath();
                }
            }
            else{
                loc = this.activeScript.GetLocation();
                name = this.activeScript.GetName();
            }
            
            if(name == null)
                return;
            
            this.activeScript.SetText(editor.GetText());
            this.activeScript.SetName(name);
            this.activeScript.SetLocation(loc);
            this.activeScript.Save();
        });
        file.add(saveFile);
        JMenuItem saveAllFile = new JMenuItem("Save All Files");
        saveAllFile.addActionListener((evt) -> {
            if(this.activeScript != null)
                this.activeScript.SetText(editor.GetText());
            for(ScriptInterface script : this.scripts){
                String name = null;
                String loc = null;
                
                if(script.GetLocation() == null){
                    int returnVal = fileBrowser.showSaveDialog(null);
                    if(returnVal == JFileChooser.APPROVE_OPTION) {
                        name = fileBrowser.getSelectedFile().getName();
                        loc = fileBrowser.getSelectedFile().getAbsolutePath();
                    }
                }
                else{
                    loc = script.GetLocation();
                    name = script.GetName();
                }
                
                if(name == null)
                    continue;
                
                script.SetName(name);
                script.SetLocation(loc);
                script.Save();
            }
        });
        file.add(saveAllFile);
        JMenuItem loadFile = new JMenuItem("Open File");
        loadFile.addActionListener((evt) -> {
            int returnVal = fileBrowser.showOpenDialog(null);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                try{
                    File f = fileBrowser.getSelectedFile();
                    List<String> lines = Files.readAllLines(Paths.get(f.getAbsolutePath()),Charset.defaultCharset());
                    String script = String.join("\n", lines);
                    
                    ScriptInterface in = new ScriptInterface();
                    in.SetName(f.getName());
                    in.SetLocation(f.getAbsolutePath().replace("(?:\\.oak)$", ""));
                    in.SetText(script);
                    
                    this.scripts.add(in);
                    this.RefreshEditorFiles(openFileTabs);
                }catch(Exception e){
                    System.out.println(e);
                }
            }
        });
        file.add(loadFile);
        file.add(new JSeparator());
        JMenuItem clearTerminal = new JMenuItem("Clear Terminal");
        clearTerminal.addActionListener((evt) -> {
            console.Clear();
        });
        file.add(clearTerminal);
        file.add(new JSeparator());
        JMenuItem saveEnv = new JMenuItem("Save Workspace");
        saveEnv.addActionListener((evt) -> {
            int returnVal = fileBrowser.showSaveDialog(null);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                String name = fileBrowser.getSelectedFile().getName();
                FileWriter writer = new FileWriter(name+".workspace");
                writer.WriteLn(runtime.global.ToJSON());
                writer.Save();
            }
        });
        file.add(saveEnv); 
        JMenuItem loadEnv = new JMenuItem("Load Workspace");
        loadEnv.addActionListener((evt) -> {
            int returnVal = fileBrowser.showOpenDialog(null);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                try{
                    String name = fileBrowser.getSelectedFile().getAbsolutePath();
                    String json = String.join("\n", Files.readAllLines(Paths.get(name)));
                    runtime.global.FromJSON(json);
                    UpdateWorkspace();
                }catch(Exception e){
                    Debug.Log(e);
                }
            }
        });
        file.add(loadEnv);
        JMenuItem clearEnv = new JMenuItem("Clear Workspace");
        clearEnv.addActionListener((evt) -> {
            if(JOptionPane.showConfirmDialog(null, "Are you sure you wish to delete all variables in the current workspace?") == JOptionPane.YES_OPTION){
                runtime.global.Clear();
                UpdateWorkspace();
            }
        });
        file.add(clearEnv);
        file.add(new JSeparator());
        JMenuItem close = new JMenuItem("Quit Application");
        close.addActionListener((evt) -> {
            System.exit(0);
        });
        file.add(close);
        
        //Tools Menu
        JMenuItem help = new JMenuItem("Help");
        help.addActionListener((evt) -> {
            profoak.tools.Help.ShowHelp();
        });
        UnitConverter conv = new UnitConverter();
        JMenuItem converter = new JMenuItem("Unit Converter");
        converter.addActionListener((evt) -> { 
            conv.setVisible(true);
        });
        tools.add(help);
        tools.add(converter);
        
        //Load up all tools
        LoadTools();
        for(ToolInterface tool : this.tools){
            JMenuItem item = new JMenuItem(tool.GetName());
            ToolInterface myTool = tool;
            item.addActionListener((evt) -> {
                myTool.Launch(this, runtime.global);
            });
            tools.add(item);
        }
        
        //Options Menu
        ColorSettings themes = new ColorSettings(settings);
        JMenuItem themeOptions = new JMenuItem("Theme");
        themeOptions.addActionListener((evt) -> {
            themes.setVisible(true);
        });
        options.add(themeOptions);
        
        GeneralSettings general = new GeneralSettings();
        JMenuItem progOptions = new JMenuItem("General");
        progOptions.addActionListener((evt) -> {
            general.setVisible(true);
        });
        options.add(progOptions);
        
        //Recent
        TitledBorder recentBounds = new TitledBorder("Recent Commands");
        recentBounds.setTitleJustification(TitledBorder.CENTER);
        recentBounds.setTitlePosition(TitledBorder.TOP);
        
        JScrollPane recentcroll = new JScrollPane(recentList);
        recentList.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent evt){
                JList list = (JList)evt.getSource();
                if(evt.getClickCount() == 2){
                    int index = list.locationToIndex(evt.getPoint());
                    String value = list.getModel().getElementAt(index).toString();
                    //add cmd to the terminal
                    console.append(value);
                }
            }
        });
        recentcroll.setPreferredSize(leftSize);
        recentcroll.setBorder(recentBounds);
        left.add(recentcroll);
        
        frame.add(left, BorderLayout.WEST);
        
        //Command console TERMINAL
        JTabbedPane center = new JTabbedPane();
        frame.add(center, BorderLayout.CENTER);
        
        console.AddSubmitListener((text)-> {
            this.last_cmds.add(text);
            while(this.last_cmds.size() > 50)
                this.last_cmds.pollFirst(); //Limit the size of my recent list to 50
            
            Object[] last = last_cmds.toArray();
            this.recentList.setListData(last);
            
            try{
                ProgramNode root = parser.Compile(text);
                Object value = runtime.Execute(root);
                String result = (value == null) ? "NULL" : value.toString();
                console.append("\n\t"+result.replaceAll("\n", "\n\t"));
                UpdateWorkspace();
            }catch(Exception e){
                if(true){
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);
                    console.append("\n\t"+sw.toString());
                }else{
                    console.append("\n\t"+e.toString());
                }
            }
        });
        center.addTab("Terminal", new JScrollPane(console));
        
        //Code editor
        JPanel editor_subpanel = new JPanel(new BorderLayout());
        JPanel editor_top = new JPanel();
        
        //editor.AddStylingRule("[a-zA-Z][a-zA-Z0-9]*\\(.*\\)", Color.blue);      //Function call
        editor.AddStylingRule("[0-9]+(?:\\.[0-9]*)?(?:[ij])?", Color.red);      //Number
        editor.AddStylingRule("def(?=\\W)", Color.blue);                               //Definition
        editor.AddStylingRule("if(?=\\W)", Color.blue);                               //Definition
        editor.AddStylingRule("while(?=\\W)", Color.blue);                               //Definition
        editor.AddStylingRule("(?:true|false)(?=\\W)", Color.blue); 
        editor.AddStylingRule("#.*#", new Color(26,145,27));                    //Comment
        editor_subpanel.add(editor_top, BorderLayout.NORTH);
        editor_subpanel.add(editor, BorderLayout.CENTER);
        
        editor.setVisible(false);
        
        ConfigureEditor(editor_top, openFileTabs, editor, runtime);
        center.addTab("Editor", new JScrollPane(editor_subpanel));
        
        //Log
        JTextArea log = new JTextArea();
        log.setEditable(false);
        System.setOut(new PrintStream(new OutputStream(){
            @Override
            public void write(int i) throws IOException {
                log.append(""+(char)i);
            }
        }));
        center.addTab("Log", new JScrollPane(log));
        
        //Memory Manager
        MemoryInfo memInfo = new MemoryInfo(5000); //5s update time
        frame.add(memInfo, BorderLayout.SOUTH);
        
        frame.setVisible(true);
        
        Log("Application Started");
    }
    
    public void UpdateWorkspace(){
        workspaceTable.setModel(runtime.global.GetTabulatedStore());
    }
    
    private void ConfigureEditor(JPanel bar, JTabbedPane openFiles,SyntaxHighlighter editor, profoak.core.Runtime runtime){
        bar.setLayout(new BoxLayout(bar,BoxLayout.Y_AXIS));
        
        openFiles.setPreferredSize(new Dimension(bar.getWidth(), 28));
        
        JButton run; JButton news; JButton delete;
        try {
            //Load icons
            ImageIcon newIcon = new ImageIcon(ImageIO.read(new File("resources/new.png")));
            news = new JButton(newIcon);
            news.setBorder(BorderFactory.createEmptyBorder());
            news.setContentAreaFilled(false);
            
            ImageIcon runIcon = new ImageIcon(ImageIO.read(new File("resources/run.png")));
            run = new JButton(runIcon);
            run.setBorder(BorderFactory.createEmptyBorder());
            run.setContentAreaFilled(false);
            
            ImageIcon deleteIcon = new ImageIcon(ImageIO.read(new File("resources/delete.png")));
            delete = new JButton(deleteIcon);
            delete.setBorder(BorderFactory.createEmptyBorder());
            delete.setContentAreaFilled(false);
            
        } catch (IOException ex) {
            run = new JButton("Run");
            news = new JButton("New");
            delete = new JButton("Remove");
        }
        
        news.addActionListener((evt) -> {
            ScriptInterface scr = new ScriptInterface();
            this.scripts.add(scr);
            if(this.activeScript == null)
                this.activeScript = this.scripts.get(0);
            RefreshEditorFiles(openFiles);
        });
        
        run.addActionListener((evt) -> {
            if(this.activeScript == null)
                return;
            this.activeScript.SetText(editor.GetText());
            this.processors.Enqueue(() -> {
                Object out = runtime.Execute((ProgramNode)this.activeScript.Compile()); 
                //JOptionPane.showMessageDialog(null, "A script completed with result: "+String.valueOf(out));
                UpdateWorkspace();
                Log("SUCCESS: A script has completed with result "+String.valueOf(out));
            }, (ex) -> {
                ((Exception)ex).printStackTrace();
                Log("FAILURE: A script has failed with error "+String.valueOf(ex));
                //JOptionPane.showMessageDialog(null, "A script failed to complete with error: "+ex.toString());
            });      
        });
        
        delete.addActionListener((evt) -> {
            if(this.activeScript != null){
                this.scripts.remove(this.activeScript);
                RefreshEditorFiles(openFiles);
                if(this.scripts.size() == 0){
                    editor.setVisible(false);
                }
            }
        });
        
        JScrollPane scroller = new JScrollPane(openFiles);
        
        RefreshEditorFiles(openFiles);
        
        openFiles.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent ce) {
                int selected = openFiles.getSelectedIndex();
                if(selected < 0)
                    return;
                if(activeScript != null){
                    activeScript.SetText(editor.GetText()); //Set text to the current script
                }
                ScriptInterface n = scripts.get(selected);
                editor.SetText(n.GetText());
                activeScript = n;
                editor.setVisible(true);
            }
        });
        
        bar.add(scroller);
        
        JPanel operations = new JPanel(new FlowLayout(FlowLayout.LEFT));
        operations.setBackground(Color.darkGray);
        operations.add(news);
        operations.add(run);
        operations.add(delete);
        
        bar.add(operations);
    }
    
    private void RefreshEditorFiles(JTabbedPane openFiles){
        openFiles.removeAll();
        for(ScriptInterface script : this.scripts){
            openFiles.addTab(script.GetName(), null);
        }
    }
    
    public void LoadTools(){
        try{
            File folder = new File("tools");
            if(!folder.exists())
                return;

            File[] tools = folder.listFiles();

            this.tools.clear();

            for(File file : tools){
                if(file.getName().endsWith(".jar")){
                    LinkedList<Class> classes = JarLoader.LoadJar(file.getAbsolutePath());
                    for(Class clazz : classes){
                        if(ToolInterface.IsToolable(clazz)){
                            this.tools.add(new ToolInterface(clazz));
                        }
                    }
                }
            }
        }catch(Exception e){}
    }
}
