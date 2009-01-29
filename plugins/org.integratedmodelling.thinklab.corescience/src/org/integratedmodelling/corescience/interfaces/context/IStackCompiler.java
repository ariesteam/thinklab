package org.integratedmodelling.corescience.interfaces.context;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;


/**
 * A simple stack for a machine with few basic types, static
 * and dynamic invocation. No function, returns, etc.
 * 
 */
public interface IStackCompiler  extends IContextualizationCompiler {
	
	public int encodeLabel(IContextualizer ctx);
	
	public int encodeJump(int label, IContextualizer ctx);
	
	/**
	 * Add an object to the local memory, return an integer ID that we can use
	 * in call instructions. If the stack is serializable, the object should
	 * be serializable and get serialized with it.
	 * 
	 * @param ctx
	 * @return
	 */
	public int encodeMemorizeInstance(Object ctx) throws ThinklabException;
	
	/**
	 * Add a class to local memory so we can use it later. Return an ID to use
	 * it in bytecode. If serialization is enables, it should remember the
	 * cl
	 * @param cls
	 * @return
	 */
	public int encodeMemorizeClass(Class cls);
	
	public int encodeCallInstanceMethod(int instance, int method, IContextualizer ctx);
	
	public int encodeCallStaticMethod(int method, IContextualizer ctx);
	
	public void encodePushInteger(int val, IContextualizer ctx);
	
	public void encodePushDouble(double val, IContextualizer ctx);
	
	public void encodePushString(String val, IContextualizer ctx);
	
	public void encodePushValue(IValue val, IContextualizer ctx);
	
	public void encodePushObject(Object val, IContextualizer ctx);

	public int encodePopInteger(IContextualizer ctx);
	
	public double encodePopDouble(IContextualizer ctx);
	
	public String encodePopString(IContextualizer ctx);

	public IValue encodePopValue(IContextualizer ctx);
	
	public Object encodePopObject(IContextualizer ctx);

	public void encodeDup(IContextualizer ctx);
	
	public void encodeThrowException(IContextualizer ctx);

}
