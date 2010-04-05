package org.integratedmodelling.corescience.context;

import java.util.Map;

import org.integratedmodelling.corescience.interfaces.IState;


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

	
	private ObservationContext _from;
	private ObservationContext _to;

	public ContextMapper(IState from, IState to) {
		this._from = from.getObservationContext();
		this._to = to.getObservationContext();
	}
	
	public ContextMapper(ObservationContext from, ObservationContext to) {
		this._from = from;
		this._to = to;
	}
	
	/**
	 * the sequential index of the subdivision of the "from" context that maps to the passed
	 * subdivision index of the "to" context.
	 * 
	 * @param n
	 * @return
	 */
	public int getIndex(int n) {
		return 0;
	}
	
	/**
	 * true if extent subdivision n of the "to" context is defined in all dimensions of the
	 * "from" context.
	 * 
	 * @param n
	 * @return
	 */
	public boolean isCovered(int n) {
		return false;
	}

	/**
	 * Return the local state (keyword->Object) of "to" model at subdivision i ("from" model is ignored) by remapping
	 * all context values in all states.
	 * 
	 * @param statemap
	 * @param i
	 * @return
	 */
	public Map<?, ?> getLocalState(Map<?, ?> statemap, int i) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
