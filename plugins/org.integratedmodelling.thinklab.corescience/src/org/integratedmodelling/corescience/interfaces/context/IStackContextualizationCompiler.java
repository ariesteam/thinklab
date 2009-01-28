package org.integratedmodelling.corescience.interfaces.context;


/**
 * Adds functions that enable compilation on a simple stack machine with few basic types, static
 * and dynamic invocation. No function, returns, etc.
 * 
 */
public interface IStackContextualizationCompiler extends IContextualizationCompiler {
	
	public int setLabel(IContextualizer ctx);
	
	public int jump(int label, IContextualizer ctx);
	
	public int makeInstance(IContextualizer ctx);
	
	public int callInstanceMethod(int instance, int method, IContextualizer ctx);
	
	public int callStaticMethod(int method, IContextualizer ctx);
	
	public void pushInteger(int val, IContextualizer ctx);
	
	public void pushDouble(double val, IContextualizer ctx);
	
	public void pushString(String val, IContextualizer ctx);
	
	public void pushObject(Object val, IContextualizer ctx);

	public int popInteger(IContextualizer ctx);
	
	public double popDouble(IContextualizer ctx);
	
	public String popString(IContextualizer ctx);
	
	public Object popObject(IContextualizer ctx);

	public void dup(IContextualizer ctx);
	
	public void throwException(IContextualizer ctx);

}
