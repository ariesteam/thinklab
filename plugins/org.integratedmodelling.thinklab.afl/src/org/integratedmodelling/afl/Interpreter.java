package org.integratedmodelling.afl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.swing.tree.DefaultMutableTreeNode;

import org.integratedmodelling.afl.exceptions.ThinklabAFLException;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.command.CommandDeclaration;
import org.integratedmodelling.thinklab.command.CommandManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabResourceNotFoundException;
import org.integratedmodelling.thinklab.interfaces.IConcept;
import org.integratedmodelling.thinklab.interfaces.IInstance;
import org.integratedmodelling.thinklab.interfaces.ISession;
import org.integratedmodelling.thinklab.interfaces.IValue;
import org.integratedmodelling.thinklab.value.ListValue;
import org.integratedmodelling.thinklab.value.NumberValue;
import org.integratedmodelling.thinklab.value.ObjectReferenceValue;
import org.integratedmodelling.thinklab.value.TextValue;
import org.integratedmodelling.thinklab.value.Value;
import org.integratedmodelling.utils.KeyValueMap;
import org.integratedmodelling.utils.Polylist;

/**
 * Interpreters are joined in a tree structure to manage visibility of symbols.
 * There is always one root interpreter that knows globally visible variables
 * and functions.
 * 
 * @author Ferdinando Villa
 * 
 */
public class Interpreter extends DefaultMutableTreeNode {

	private static final long serialVersionUID = 5679645392418895651L;

	HashMap<String, Functor> functors = new HashMap<String, Functor>();
	HashMap<String, FunctionValue> functions = new HashMap<String, FunctionValue>();
	HashMap<String, IValue> literals = new HashMap<String, IValue>();

	private ArrayList<StepListener> listeners = new ArrayList<StepListener>();
	private ISession session = null;

	Interpreter() {
	}

	public Interpreter(Interpreter parent) {
		parent.add(this);
	}

	protected void finalize() {
		removeFromParent();
	}

	public void setSession(ISession session) {
		this.session = session;
	}

	public void addStepListener(StepListener listener) {
		this.listeners.add(listener);
	}

	public void registerFunctor(String id, Functor functor) {
		functors.put(id, functor);
	}

	public IValue eval(Polylist list) throws ThinklabException {
		return eval(list, null);
	}

	public IValue eval(Polylist list, Collection<StepListener> state)
			throws ThinklabException {

		ArrayList<Object> o = list.toArrayList();

		ArrayList<IValue> args = new ArrayList<IValue>();
		KeyValueMap opts = new KeyValueMap();
		IValue ret = null;

		if (list.isEmpty()) {
			return new ListValue(Polylist.list());
		}

		String functor = o.get(0).toString();

		/*
		 * eval arguments - dumb for now; things like conditionals should ensure
		 * that selective evaluation takes place.
		 */
		boolean quoted = false;

		for (int i = 1; i < o.size(); i++) {

			Object arg = o.get(i);
			
			if (arg instanceof Polylist) {

				if (quoted) {
					args.add(new ListValue((Polylist)arg));
					quoted = false;
				} else {
					args.add(eval((Polylist)arg, state));
				}
			} else if (arg.equals("'")) {
				quoted = true;
			} else {

				if (quoted) {
					args.add(new TextValue(arg.toString()));
					quoted = false;
				} else {

					if (arg instanceof IValue) {
						args.add((IValue)arg);
					} else {

						/*
						 * FIXME here we have a problem: a quoted string should
						 * enter the args array, an unbound unquoted symbol
						 * should raise an exception. Problem is, we have no way
						 * of distinguishing a string that was parsed with
						 * quotes.
						 */
						IValue literal = null;
						
						/* see if it's a number */
						try {
							double d = Double.parseDouble(arg.toString());
							literal = new NumberValue(d);
						} catch (NumberFormatException e) {
							// do nothing
						}
						
						if (literal == null) 
							literal = lookupLiteral(o.get(i).toString());

						if (literal == null)
							literal = Value.getValueForObject(arg);

						args.add(literal);
					}
				}
			}
		}

		if (functor.equals("define")) {

			if (args.size() != 2) {
				throw new ThinklabAFLException("define syntax");
			}
			
			// return value is the symbol bound
			ret = args.get(1);

			/*
			 * define new local function or bind value
			 */
			String var = args.get(0).toString();
			
			if (args.get(1) instanceof FunctionValue) {
				functions.put(var, (FunctionValue)ret);
			} else {
				literals.put(var,(IValue)ret);
			}

		} else if (functor.equals("cond")) {

		} else if (functor.equals("lambda")) {

			if (args.size() < 1 || args.size() > 2)
				throw new ThinklabAFLException("lambda expression syntax");
			
			for (int i = 1; i < args.size(); i++)
				if (! (args.get(i) instanceof ListValue))
					throw new ThinklabAFLException("lambda expression syntax");
			
			ListValue body = (ListValue)(args.size() == 1 ? args.get(0) : args.get(1));
			ListValue parm = (ListValue)(args.size() == 1 ? null : args.get(0));
			
			ret = new FunctionValue(body.getList(), parm.getList());
			
		} else if (functor.equals("if")) {

		} else if (functor.equals("car")) {
			
		} else if (functor.equals("cdr")) {

		} else if (functor.equals("cons")) {

		} else if (functor.equals("append")) {

		} else if (functor.equals("loop")) {

		} else {
			ret = evalSymbol(functor, opts, args, listeners);
		}

		return ret;
	}

	private IValue evalSymbol(String functor, KeyValueMap opts,
			ArrayList<IValue> args, ArrayList<StepListener> listeners2)
			throws ThinklabException {

		IValue ret = null;
		boolean found = false;

		Functor primitive = lookupFunctor(functor);

		if (primitive != null) {

			found = true;
			ret = primitive.eval(session, listeners, args.toArray(new IValue[args.size()]));

		} else {

			FunctionValue function = lookupFunction(functor);

			if (function != null) {

				found = true;
				
				/*
				 * execute in its own interpreter
				 */
				ret = function.eval(this, args.toArray(new IValue[args.size()]));

			} else {

				/*
				 * lookup command as last alternative
				 */
				if (KnowledgeManager.get() != null) {

					CommandDeclaration cdl = CommandManager.get()
							.getDeclarationForCommand(functor);

					if (cdl != null) {
						found = true;
					}
				}

			}
		}

		if (!found) {
			throw new ThinklabAFLException("undefined function: " + functor);
		}

		return ret;
	}

	private Functor lookupFunctor(String functor) {

		Functor ret = null;
		Interpreter intp = this;

		while (ret == null && intp != null) {
			ret = intp.functors.get(functor);
			intp = (Interpreter) intp.getParent();
		}

		return ret;
	}

	private FunctionValue lookupFunction(String functor) {

		FunctionValue ret = null;
		Interpreter intp = this;

		/*
		 * TODO if the function is in a parent interpreter, we must exec it
		 * there.
		 */
		while (ret == null && intp != null) {
			ret = intp.functions.get(functor);
			intp = (Interpreter) intp.getParent();
		}

		return ret;
	}

	private IValue lookupLiteral(String functor) throws ThinklabException {

		IValue ret = null;
		Interpreter intp = this;

		while (ret == null && intp != null) {
			ret = intp.literals.get(functor);
			intp = (Interpreter) intp.getParent();
		}

		/*
		 * check if it's an object in current session - of course if we have a
		 * session
		 */
		if (ret == null && functor.startsWith("#") && this.session != null) {

			IInstance inst = session.requireObject(functor.substring(1));
			ret = new ObjectReferenceValue(inst);
		}

		/*
		 * check if it is a concept or a known global instance
		 */
		if (ret == null && functor.contains(":") && KnowledgeManager.get() != null) {
			
			IConcept c = KnowledgeManager.get().retrieveConcept(functor);
			
			if (c != null) {
				ret = new Value(c);
			}
			
			if (ret == null) {
				IInstance i = KnowledgeManager.get().retrieveInstance(functor);
				if (c != null) {
					ret = new ObjectReferenceValue(i);
				}
			}
			
		}

		/* 
		 * last but not least, check if it's a function and return the function 
		 * as a literal.
		 */
		if (ret == null)
			ret =  lookupFunction(functor);
		
		return ret;
	}

	/**
	 * Clean up symbol tables, garbage collect. Doesn't need to do anything at
	 * the current level of sophistication, but it's properly invoked at plugin
	 * unload.
	 */
	public void cleanup() {

	}

	public void bind(String variable, IValue value) {
		literals.put(variable, value);
	}

}
