package org.integratedmodelling.afl;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.tree.DefaultMutableTreeNode;

import org.integratedmodelling.afl.exceptions.ThinklabAFLException;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.IValue;
import org.integratedmodelling.utils.KeyValueMap;
import org.integratedmodelling.utils.Polylist;

/**
 * Interpreters are joined in a tree structure to manage visibility of symbols. There is
 * always one root interpreter that knows globally visible variables and functions.
 * 
 * @author Ferdinando Villa
 *
 */
public class Interpreter extends DefaultMutableTreeNode {
	
	private static final long serialVersionUID = 5679645392418895651L;
	static HashMap<String, String> functorClass = new HashMap<String, String>();
	
	HashMap<String, Polylist> functions = new HashMap<String, Polylist>();
	HashMap<String, IValue> literals = new HashMap<String, IValue>();
	
	private Polylist funct;
	private StepListener model;
	
	public void initialize(StepListener model, Polylist funct) {
		this.model = model;
		this.funct = funct;
	}
	
	public static void registerFunctor(String id, String functorClass) {
		
	}
	
	public IValue run() throws ThinklabException {

		return eval(model, funct);
	}
	
	protected Functor decodeStepName(String stepName) throws ThinklabValidationException {

		Functor ret = null;
		
		String fClass = functorClass.get(stepName);
		Class<?> cl = null;
			
			try {
				cl = Class.forName(fClass);
			} catch (ClassNotFoundException e) {
			}
			
			if (cl != null) {
				try {
					ret = (Functor) cl.newInstance();
				} catch (Exception e) {
				}
			}
			
			if (ret != null)
				return ret;

		throw new ThinklabValidationException(
				"application: cannot resolve step " + stepName + " to a step class");
	}


	public IValue run_debug() throws ThinklabException {

		/*
		 * TODO add debug features
		 */
		return run();
	}
	
	public IValue eval(StepListener state, Polylist list) throws ThinklabException {
		
		ArrayList<Object> o = list.toArrayList();
		
		ArrayList<Object> args = new ArrayList<Object>();
		KeyValueMap opts = new KeyValueMap();
		IValue ret = null;
		
		String functor = o.get(0).toString();
		
		/* eval arguments - dumb for now; things like conditionals should ensure that
		 * selective evaluation takes place. */
		boolean quoted = false;
		
		for (int i = 1; i < o.size(); i++) {
	
			
			if (o.get(i) instanceof Polylist) {
				if (quoted) {
				
					args.add(o.get(i));
					quoted = false;	
					
				} else {
					args.add(eval(state, (Polylist)o.get(i)));
				}
			} else if (o.get(i).equals("'")) {
				
				quoted = true;
				
			} else {
				
				if (quoted) {
		
					args.add(o.get(i));
					quoted = false;
					
				} else {
					
				}
			}
		}
		
		if (functor.equals("function") || functor.equals("step")) {
			
			 /*
			  * define new local function
			  */
			
		} else if (functor.equals("cond")) {
			
		} else if (functor.equals("if")) {
			
		} else if (functor.equals("car")) {
			
		} else if (functor.equals("cdr")) {
			
		} else if (functor.equals("cons")) {
			
		} else if (functor.equals("append")) {
			
		} else if (functor.equals("loop")) {
			
		} else {
			
			IValue val = resolveSymbol(functor);
			
			/*
			 * TODO if functor was a step, notify result to model
			 */
			
		}
		
		return ret;
	}

	private IValue resolveSymbol(String functor) throws ThinklabException {
		
		IValue ret = null;
		
		/*
		 * resolve functor: lookup order should be built-ins, primitives, local defines, 
		 * global defines, and applications */
		IValue literal = lookupLiteral(functor);
		
		if (literal != null) {
			
			throw new ThinklabAFLException("invalid function: " + functor);
			
		} else {
			
			Polylist function = lookupFunction(functor); 
			
			if (function != null) {
				
				/*
				 * replace arguments
				 */
				
				/*
				 * execute
				 */
				
			} else {
				
				
			}
			
		}
		
		return ret;
	}

	private Polylist lookupFunction(String functor) {

		Polylist ret = null;
		Interpreter intp = this;

		while (ret == null && intp != null) {
			ret = intp.functions.get(functor);
			intp = (Interpreter) intp.getParent();
		}
		
		return ret;
	}

	private IValue lookupLiteral(String functor) {

		IValue ret = null;
		Interpreter intp = this;

		while (ret == null && intp != null) {
			ret = intp.literals.get(functor);
			intp = (Interpreter) intp.getParent();
		}
		
		return ret;
	}
	


}
