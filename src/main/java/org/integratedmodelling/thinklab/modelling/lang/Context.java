package org.integratedmodelling.thinklab.modelling.lang;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.multidimensional.MultidimensionalCursor;
import org.integratedmodelling.multidimensional.MultidimensionalCursor.StorageOrdering;
import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IExpression;
import org.integratedmodelling.thinklab.api.listeners.IListener;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IExtent;
import org.integratedmodelling.thinklab.api.modelling.IModel;
import org.integratedmodelling.thinklab.api.modelling.IObservation;
import org.integratedmodelling.thinklab.api.modelling.IState;
import org.integratedmodelling.thinklab.api.modelling.parsing.IContextDefinition;
import org.integratedmodelling.thinklab.api.modelling.parsing.IFunctionDefinition;
import org.integratedmodelling.thinklab.api.modelling.parsing.IModelDefinition;

@Concept(NS.CONTEXT)
public class Context extends ModelObject<Context> implements IContextDefinition {

	ArrayList<IModel> _models = new ArrayList<IModel>();
	ArrayList<IObservation> _observations = new ArrayList<IObservation>();
	
	ArrayList<IExtent> _order = new ArrayList<IExtent>();
	HashMap<IConcept, IExtent> _extents = new HashMap<IConcept, IExtent>();
	HashMap<IConcept, IState> _states = new HashMap<IConcept, IState>();
	
	int _multiplicity = 0;
	boolean _isNull = false;
	
	/*
	 * just store them if we have them, for speed.
	 */
	IExtent _space = null;
	IExtent _time = null;
	
	// only used for the isCovered op
	MultidimensionalCursor _cursor = null;

	/**
	 * Shallow copy everything in the context passed.
	 * @param context
	 */
	public Context(Context context) {
		this._models.addAll(context._models);
		this._observations.addAll(context._observations);
		this._order.addAll(context._order);
		this._states.putAll(context._states);
		this._extents.putAll(context._extents);
		this._multiplicity = context._multiplicity;
		this._isNull = context._isNull;
	}

	public Context() {
		// TODO Auto-generated constructor stub
	}

	public void addObservation(IModel o) {
		_models.add(o);
	}
	
	public List<IObservation> getObservations() {
		return _observations;
	}

	@Override
	public int getMultiplicity() {
		return _multiplicity;
	}

	@Override
	public IContext intersection(IContext other) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IContext union(IContext other) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean contains(IContext o) throws ThinklabException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean overlaps(IContext o) throws ThinklabException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean intersects(IContext o) throws ThinklabException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void listen(IListener... listeners) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addModel(IModelDefinition observation) {
		_models.add(observation);
	}

	@Override
	public List<IExtent> getExtents() {
		return _order;
	}

	@Override
	public int getMultiplicity(IConcept concept) throws ThinklabException {
		return _multiplicity;
	}

	@Override
	public IExtent getExtent(IConcept observable) {
		return _extents.get(observable);
	}

	@Override
	public boolean isCovered(int index) {

		if (_cursor == null) {
			_cursor = 	
				new MultidimensionalCursor(StorageOrdering.ROW_FIRST);
			_cursor.defineDimensions(getDimensionSizes());
		}

		int i = 0;
		for (IExtent e : getExtents())
			if (!e.isCovered(_cursor.getElementIndexes(index)[i++]))
				return false;

		return true;
	}


	@Override
	public IState getState(IConcept observable) {
		return _states.get(observable);
	}

	@Override
	public void merge(IObservation observation) throws ThinklabException {

		_cursor = null;
		
		if (observation instanceof IExtent) {
			mergeExtent((IExtent)observation);
		} else {
			
		}
	}

	@Override
	public void merge(IContext context) throws ThinklabException {

		_cursor = null;

		for (IExtent e : context.getExtents()) {
			merge(e);
		}
		
		sort();
		
		for (IState s : context.getStates()) {
			merge(s);
		}
	}

	@Override
	public IExtent getTime() {
		return _time;
	}

	@Override
	public IExtent getSpace() {
		return _space;
	}

	@Override
	public Collection<IState> getStates() {
		return _states.values();
	}

	@Override
	public IContext collapse(IConcept dimension) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Context demote() {
		return this;
	}

	@Override
	public void addObservationGeneratorFunction(IFunctionDefinition function) throws ThinklabValidationException {

		// find function and validate parameters
		IExpression func = Thinklab.get().resolveFunction(function.getId(), function.getParameters().keySet());
		
		if (func == null) {
			throw new ThinklabValidationException("function " + function.getId() + " is unknown");
		}
		Observation o = null;
		
		// run function and store observation
		try {
			 o = (Observation) func.eval(function.getParameters());
			 if (o == null)
				 throw new ThinklabValidationException("function " + function.getId() + " does not return any value");
			 
		} catch (ThinklabException e) {
			throw new ThinklabValidationException(e);
		}
		
		
		_observations.add(o);
		
	}
	
	@Override
	public void initialize() throws ThinklabException {
				
		_extents.clear();
		_states.clear();
		
		for (IObservation o : _observations) {
			if (o instanceof IExtent)
				merge(o);
		}
		
		for (IObservation o : _observations) {
			if (! (o instanceof IExtent))
				merge(o);
		}
		
		sort();

		for (IModel m : _models) {
			merge(m.observe(this));
		}
		
	}
	
	/*
	 * 
	 */

	private void sort() {

		_order.clear();
		
		for (IExtent e : _extents.values()) {
			_order.add(e);
		}
		
		/* sort. Is it fair to think that if two extent concepts have an ordering 
		 * relationship, they should know about each other? So that we can implement the
		 * ordering as a relationship between extent observation classes?
		 * 
		 * For now, all we care about is that time, if present, comes first. We just 
		 * check for that using the ontology name, which of course sucks.
		 */
		Collections.sort(_order, new Comparator<IExtent>() {

			@Override
			public int compare(IExtent o1, IExtent o2) {
				// neg if o1 < o2
				boolean o1t = o1.getObservable().getDirectType().getConceptSpace().equals("time");
				boolean o2t = o2.getObservable().getDirectType().getConceptSpace().equals("time");
				if (o1t && !o2t) return -1;
				if (!o1t && o2t) return 1;
				return 0;
			}
		});
	}
	
	private void mergeExtent(IExtent itsExtent) throws ThinklabException {

		IConcept dimension = itsExtent.getObservable().getDirectType();
		IExtent myExtent  = _extents.get(dimension);

		if (myExtent == null) {

			/* just add the extent */
			_extents.put(dimension, itsExtent);

		} else {

			/* ask CM to modify the current extent record in order to represent the
			   new one as well. */
			IExtent merged = myExtent.force(itsExtent);
			if (merged == null) {
				_isNull = true;
			} else {
				_extents.put(dimension, merged);
			}
		}		
	}

	private int[] getDimensionSizes() {

		int i = 0;
		int[] ret = new int[_order.size()];
		for (IExtent e : _order)
			ret[i++] = e.getMultiplicity();
		return ret;
	}

	
}
