package org.integratedmodelling.corescience.compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javolution.context.ConcurrentContext;

import org.integratedmodelling.corescience.context.ObservationContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.runtime.ISession;

public class Contextualizer  {
	
	ObservationContext context = null;

	private static class RunContext implements Runnable {

		VMContextualizer<?>  ctx = null;
		Map<IConcept, IState> result = null;
		ISession session = null;
		
		public RunContext(VMContextualizer<?>  ctx, ISession session) {
			this.ctx = ctx;
			this.session = session;
			
			if (this.session.getVariable(ISession.DEBUG) != null)
				ctx.dump(System.out);
		}
		
		@Override
		public void run() {
			
			try {
				result = ctx.run();
			} catch (ThinklabValidationException e) {
				throw new ThinklabRuntimeException(e);
			}
		}
		
		public Map<IConcept, IState> getResult() {
			return result;
		}
		
	}
	
	ArrayList<VMContextualizer<?> > runnables = new ArrayList<VMContextualizer<?>>();
	
	/**
	 * Pass the original observation structure so we can reconstruct the final instance using it
	 * as a model. We will build sub-structures in parallel and will need to reconstruct them 
	 * later.
	 * 
	 * @param observation
	 */
	public Contextualizer(ObservationContext context) {
		this.context = context;
	}
	
	public void addContextualizer(VMContextualizer<?> ctxer) {
		runnables.add(ctxer);
	}
	
	public ObservationContext run(ISession session) throws ThinklabException {

		RunContext[] runs = new RunContext[runnables.size()];
		
		for (int i = 0; i < runnables.size(); i++) {
			runs[i] = new RunContext(runnables.get(i), session);
		}
		
		ConcurrentContext.enter();
		try {
			for (int i = 0; i < runs.length; i++)
				ConcurrentContext.execute(runs[i]);
		} finally {
			ConcurrentContext.exit();
		}
		
		return mergeResults(runs, session);
	}

	private ObservationContext mergeResults(RunContext[] runs, ISession session) throws ThinklabException {
	
		Map<IConcept, IState> allStates = new HashMap<IConcept, IState>();
		for (RunContext run : runs) {
			allStates.putAll(run.getResult());
		}
		
		for (IConcept c : allStates.keySet())
			context.addState(allStates.get(c));
		
		return context;
	}

}
