/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package profoak.core;

import profoak.core.ast.ProgramNode;

/**
 *
 * @author Colin Halseth
 */
public class Runtime {
    
    public final Scope global = new Scope();
    
    public Object Execute(ProgramNode program){
        return program.Execute(global);
    }
    
}
