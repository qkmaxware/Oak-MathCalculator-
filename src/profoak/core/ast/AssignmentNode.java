/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package profoak.core.ast;

import profoak.core.CoreException;
import profoak.core.Scope;
import profoak.core.value.Matrix;
import profoak.core.value.Single;
import profoak.core.value.Value;

/**
 *
 * @author Colin Halseth
 */
public class AssignmentNode implements AST{

    private AST[] children = new AST[2];
    
    @Override
    public AST[] GetChildren() {
        return children;
    }

    @Override
    public void SetChild(int index, AST child) {
        children[index] = child;
    }

    @Override
    public void SetChildren(AST[] children) {
        this.children = children;
    }

    @Override
    public Object Execute(Scope scope) {
        Scope next = scope;
        Object value = children[1].Execute(next);
        if((value instanceof Value)){
            AST name = children[0];
            if(name instanceof NameNode){
                scope.Set(((NameNode)name).name, (Value)value);
            }
            if (name instanceof IndexerNode && value instanceof Single){
                IndexerNode ind = (IndexerNode)name;
                int[] index = ind.GetIndex(scope);
                String nameValue = ind.GetIndexedName(scope);
                if(nameValue != null && index != null){
                    Object mat = scope.Get(nameValue);
                    if(mat instanceof Matrix){
                        ((Matrix)mat).Set(index[0], index[1], (Single)value);
                        return mat;
                    }else{
                        throw new CoreException("Cannot index anything other than a matrix.");
                    }
                }else{
                    throw new CoreException("Invalid indexing parameters.");
                }
            }
        }
        
        return value;
    }
 
    
    
}
