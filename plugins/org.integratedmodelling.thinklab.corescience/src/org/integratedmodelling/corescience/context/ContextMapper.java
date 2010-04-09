package org.integratedmodelling.corescience.context;

import org.integratedmodelling.corescience.exceptions.ThinklabContextualizationException;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;

/**
 * A helper object that maps state indexes from a context to a compatible other. The "to" context
 * must have at least the same dimensions than the "from", as it's impossible to map unknown
 * dimensions, and it's expected to work on contextualized trees so that if there is the same
 * dimension, its multiplicity will be either 1 or the same.
 * 
 * The getIndex(from) function returns the index of state in  from that maps to the "same" state in to.
 * 
 * @author Ferdinando
 *
 */
public class ContextMapper {
	
	private IObservationContext _from;
	private IObservationContext _to;

	public ContextMapper(IState from, IState to) {
		this._from = from.getObservationContext();
		this._to = to.getObservationContext();
	}
	
	public ContextMapper(IObservationContext from, IObservationContext to) throws ThinklabException {
		this._from = from;
		this._to = to;
		
		int[] indexesFrom = from.getDimensionSizes();
		int[] indexesTo = new int[indexesFrom.length];
		int i = 0;
		for (IConcept c : from.getDimensions()) {
			IConcept theDim = to.getDimension(c);
			int dim = theDim == null ? 1 : to.getMultiplicity(theDim);
			if (!(dim == 1 || dim == indexesFrom[i]))
				throw new ThinklabContextualizationException(
						"dimension mismatch in concept mapper: " + dim +
						" should be 1 or " + indexesFrom[i] +
						"; check datasource transformation");
			
			indexesTo[i++] = dim;			
		}
		
	}
	
	/**
	 * the sequential index of the subdivision of the "from" context that maps to the passed
	 * subdivision index of the "to" context. 
	 * 
	 * Returns a negative value only if the subdivision
	 * doesn't match one that is "seen" in the target context; the contextualizer will store it
	 * for later aggregation.
	 * 
	 * @param n
	 * @return
	 */
	public int getIndex(int n) {
		// TODO only works with identical contexts
		return n;
	}
	
	/**
	 * true if extent subdivision n of the "to" context is defined and visible in all dimensions of the
	 * "from" context.
	 * 
	 * @param n
	 * @return
	 */
	public boolean isCovered(int n) {
		return false;
	}

	
}
