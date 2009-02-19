package org.integratedmodelling.corescience.contextualization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javolution.context.ConcurrentContext;

import org.integratedmodelling.corescience.interfaces.context.IContextualizer;
import org.integratedmodelling.corescience.interfaces.data.IDataSource;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;

public class ConcurrentContextualizer implements IContextualizer {
	
	ObservationStructure structure = null;

	private static class RunContext implements Runnable {

		StackVMContextualizer<?>  ctx = null;
		Map<IConcept, IDataSource<?>> result = null;
		ISession session = null;
		
		public RunContext(StackVMContextualizer<?>  ctx, ISession session) {
			this.ctx = ctx;
			this.session = session;
			
			ctx.dump(session.getOutputStream());
		}
		
		@Override
		public void run() {
			
			try {
				result = ctx.run();
			} catch (ThinklabValidationException e) {
				throw new ThinklabRuntimeException(e);
			}
		}
		
		public Map<IConcept, IDataSource<?>> getResult() {
			return result;
		}
		
	}
	
	ArrayList<StackVMContextualizer<?> > runnables = new ArrayList<StackVMContextualizer<?>>();
	
	/**
	 * Pass the original observation structure so we can reconstruct the final instance using it
	 * as a model. We will build sub-structures in parallel and will need to reconstruct them 
	 * later.
	 * 
	 * @param observation
	 */
	public ConcurrentContextualizer(ObservationStructure structure) {
		this.structure = structure;
	}
	
	public void addContextualizer(StackVMContextualizer<?> ctxer) {
		runnables.add(ctxer);
	}
	
	@Override
	public IInstance run(ISession session) throws ThinklabException {

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

	private IInstance mergeResults(RunContext[] runs, ISession session) throws ThinklabException {
	
		Map<IConcept, IDataSource<?>> allStates = new HashMap<IConcept, IDataSource<?>>();
		
		for (RunContext run : runs) {
			allStates.putAll(run.getResult());
		}
		
		return structure.buildObservation(session, allStates);
	}

}
