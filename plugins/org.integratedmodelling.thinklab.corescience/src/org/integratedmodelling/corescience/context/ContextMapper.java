package org.integratedmodelling.corescience.context;

import org.integratedmodelling.corescience.exceptions.ThinklabContextualizationException;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.multidimensional.MultidimensionalCursor;
import org.integratedmodelling.multidimensional.MultidimensionalCursor.StorageOrdering;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValueConversionException;
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
	MultidimensionalCursor fromCursor = 
		new MultidimensionalCursor(StorageOrdering.ROW_FIRST);
	MultidimensionalCursor toCursor = 
		new MultidimensionalCursor(StorageOrdering.ROW_FIRST);
	int[] cdims = null;
	boolean identical = false;
	private IState state;
	
	public ContextMapper(IState state, IObservationContext to) throws ThinklabException {
		this(state.getObservationContext(), to);
		this.state = state;
	}
	
	/**
	 * When a cursor over the dimensions of a context is needed, this one should be used
	 * to obtain it in order to use the same layout.
	 * 
	 * @param ctx
	 * @return
	 */
	public static MultidimensionalCursor getCursor(IObservationContext ctx) {
		MultidimensionalCursor ret = 
			new MultidimensionalCursor(StorageOrdering.ROW_FIRST);
		ret.defineDimensions(ctx.getDimensionSizes());
		return ret;
	}
	
	public ContextMapper(IObservationContext from, IObservationContext to) throws ThinklabException {
		
		this._from = from;
		this._to = to;
		int td = 0;
		int[] indexesFrom = from.getDimensionSizes();
		this.cdims = new int[indexesFrom.length];
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
			
			indexesTo[i] = dim;	
			cdims[i] =  (indexesFrom[i] == indexesTo[i]) ? 0 : 1;
			td += cdims[i];
			i++;
		}
		
		this.identical = td == 0;
		this.fromCursor.defineDimensions(indexesFrom);
		this.toCursor.defineDimensions(indexesTo);
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
		
		if (identical)
			return n;
		
		int[] ofss = fromCursor.getElementIndexes(n);
		for (int i = 0; i < cdims.length; i++)
			if(cdims[i] > 0)
				ofss[i] = 0;
		return toCursor.getElementOffset(ofss);
	}

	public IState getState() {
		return state;
	}

	public Object getValue(int index) {
		return state.getValue(getIndex(index));
	}

	public double getDoubleValue(int i) throws ThinklabValueConversionException {
		return state.getDoubleValue(getIndex(i));
	}	

}
