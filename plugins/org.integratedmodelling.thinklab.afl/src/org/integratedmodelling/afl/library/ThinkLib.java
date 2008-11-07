package org.integratedmodelling.afl.library;

import java.util.Collection;

import org.integratedmodelling.afl.AFLLibrary;
import org.integratedmodelling.afl.Functor;
import org.integratedmodelling.afl.Interpreter;
import org.integratedmodelling.afl.StepListener;
import org.integratedmodelling.afl.exceptions.ThinklabAFLException;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValueConversionException;
import org.integratedmodelling.thinklab.interfaces.IInstance;
import org.integratedmodelling.thinklab.interfaces.ISession;
import org.integratedmodelling.thinklab.interfaces.IValue;
import org.integratedmodelling.thinklab.value.ListValue;
import org.integratedmodelling.thinklab.value.ObjectReferenceValue;
import org.integratedmodelling.thinklab.value.TextValue;
import org.integratedmodelling.utils.Polylist;

/**
 * AFL functions to deal with objects and concepts
 * @author Ferdinando
 *
 */
public class ThinkLib implements AFLLibrary {

	class Expand implements Functor {

		@Override
		public IValue eval(ISession session,
				Collection<StepListener> listeners, IValue... args)
				throws ThinklabAFLException {
			
			if (args == null || args.length != 1 || !(args[0] instanceof ObjectReferenceValue))
				throw new ThinklabAFLException("expand: wrong arguments");
			
			Polylist l = null;
			try {
				l = args[0].asObjectReference().getObject().toList(null);
			} catch (ThinklabException e) {
				throw new ThinklabAFLException(e);
			}
			
			return new ListValue(l);
			
		}
		
	}
	
	/**
	 * Load a set of objects from a URL, return them in a list
	 * @author Ferdinando
	 *
	 */
	class Load implements Functor {

		@Override
		public IValue eval(ISession session,
				Collection<StepListener> listeners, IValue... args)
				throws ThinklabAFLException {

			IValue ret = null;
			
			if (args == null || args.length != 1 || !(args[0] instanceof TextValue))
				throw new ThinklabAFLException("load: wrong arguments");
			
			if (session == null)
				throw new ThinklabAFLException("load: no session to load objects into");
				
			try {
				Collection<IInstance> objs = session.loadObjects(args[0].toString());
				Object[] os = new Object[objs.size()];
				int i = 0;
				
				for (IInstance inst : objs) 
					os[i++] = new ObjectReferenceValue(inst);
				
				ret = new ListValue(Polylist.PolylistFromArray(os));
					
			} catch (ThinklabException e) {
				throw new ThinklabAFLException(e);
			}
			
			return ret;
			
		}
		
	}
	
	@Override
	public void installLibrary(Interpreter intp) {

		intp.registerFunctor("expand", new Expand());
		intp.registerFunctor("load", new Load());
	}

}
