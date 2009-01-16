package org.integratedmodelling.geospace.tasks.combine;

import java.util.Collection;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.applications.ITask;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;

/**
 * Given a list of grid observation in increasing order of priority, use as much as possible of
 * the highest-priority one to create a final observation, then use the others in order of
 * priority to cover the areas that have not been covered by the previous ones. Return one
 * spatial observation with the max detail and inactive pixels only where all observations 
 * had no-data.
 * 
 * @author Ferdinando
 *
 */
public class CombineObservations implements ITask {

	
	IInstance result = null;
	
	public void setObservations(Collection<IInstance> observations) {
		
	}
	
	@Override
	public void run(ISession session) throws ThinklabException {
		// TODO Auto-generated method stub

	}
	
	public IInstance getCombinedObservation() {
		return result;
	}

}
