package org.integratedmodelling.corescience.contextualization;

import java.util.Iterator;

import org.integratedmodelling.corescience.interfaces.observation.IObservation;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;

/**
 * It's essentially an iterator of IValues, but can be used to retrieve POD types faster and along specific 
 * context dimensions. 
 * 
 * Also has methods to obtain the state of linked observations instead of the one it's built for, so there is no
 * need for verbose logics and many iterators.
 * 
 * Can efficiently obtain slices of state in appropriate array structures along domain dimensions.
 * 
 * @author Ferdinando
 *
 */
public class StateIterator implements Iterator<IValue> {

	public StateIterator(IObservation obs) {
		
		/*
		 * observation must come from contextualization; if not, complain
		 */
		
		/*
		 * analyze obs to determine what we can return per context dimension
		 */
		
		/*
		 * initialize context counters
		 */
	}
	
	public IConcept getValueType() {
		return null;
	}
	
	public int nextInteger() {
		return 0;
	}

	public boolean isIndexable() {
		return false;
	}
	
	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IValue next() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
		
	}
}
