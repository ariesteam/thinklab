package org.integratedmodelling.thinklab.interpreter.mvel;

import java.io.Serializable;
import java.util.Map;

import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.mvel2.MVEL;

/**
 * Helper class to manage running an MVEL expression without working too much.
 * 
 * @author Ferdinando
 *
 */
public class MVELExpression {

	private Serializable bytecode;

	public MVELExpression(String s) {
		this.bytecode = MVEL.compileExpression(s); 
	}
	
	public Object eval(Map<?,?> parms) {
		
		Object ret = null;
		ClassLoader clsl = null;
		
		try {
			clsl = Thinklab.get().swapClassloader();			
			ret = MVEL.executeExpression(this.bytecode, parms);
		} catch (Exception e) {
			throw new ThinklabRuntimeException(e);
		} finally {
			Thinklab.get().resetClassLoader(clsl);
		}
		return ret;
	}
	
}
