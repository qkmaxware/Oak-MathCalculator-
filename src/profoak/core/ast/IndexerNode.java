/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package profoak.core.ast;

import profoak.core.value.Matrix;
import profoak.core.Scope;
import profoak.core.value.Single;
import profoak.core.value.Value;

/**
 *
 * @author Colin Halseth
 */
public class IndexerNode implements AST{

    private AST[] children = new AST[3];
    
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
        Object thing = children[0].Execute(scope);
        
        Object ind1 = this.children[1].Execute(scope);
        Object ind2 = this.children[2].Execute(scope);
        
        if(ind1 instanceof Single && ind2 instanceof Single){
            if(thing instanceof Matrix){
                Matrix m = (Matrix)thing;
                int r = (int)((Single)ind1).GetReal().getDecimalValue();
                int c = (int)((Single)ind2).GetReal().getDecimalValue();
                return m.Get(
                        r, //Row 
                        c  //Column
                );
            }
            else{
                return null;
            }
        }
        return null;
    }
    
}
