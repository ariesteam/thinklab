package org.integratedmodelling.thinklab.modelling.lang;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.multidimensional.MultidimensionalCursor;
import org.integratedmodelling.multidimensional.MultidimensionalCursor.StorageOrdering;
import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.annotations.Concept;
import org.integratedmodelling.thinklab.api.annotations.Property;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IExpression;
import org.integratedmodelling.thinklab.api.knowledge.IProperty;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.listeners.IListener;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IExtent;
import org.integratedmodelling.thinklab.api.modelling.IModel;
import org.integratedmodelling.thinklab.api.modelling.IObservation;
import org.integratedmodelling.thinklab.api.modelling.IState;
import org.integratedmodelling.thinklab.api.modelling.ITopologicallyComparable;
import org.integratedmodelling.thinklab.api.modelling.parsing.IContextDefinition;
import org.integratedmodelling.thinklab.api.modelling.parsing.IFunctionDefinition;
import org.integratedmodelling.thinklab.api.modelling.parsing.IModelDefinition;
import org.integratedmodelling.thinklab.geospace.Geospace;
import org.integratedmodelling.thinklab.interfaces.IStorageMetadataProvider;
import org.integratedmodelling.thinklab.time.Time;

@Concept(NS.CONTEXT)
public class Context extends ModelObject<Context> implements IContextDefinition {

	@Property(NS.HAS_MODEL)
	ArrayList<IModel> _models = new ArrayList<IModel>();
	
	@Property(NS.HAS_EXTENT_FUNCTION)
	ArrayList<IFunctionDefinition> _generatorFunctions = new ArrayList<IFunctionDefinition>();
	
	ArrayList<IObservation> _observations = new ArrayList<IObservation>();
	
	ArrayList<IExtent> _order = new ArrayList<IExtent>();
	HashMap<IConcept, IExtent> _extents = new HashMap<IConcept, IExtent>();
	HashMap<ISemanticObject<?>, IState> _states = new HashMap<ISemanticObject<?>, IState>();
	
//	int     _multiplicity = 0;
	boolean _isNull = false;
	
	private boolean _initialized = false;
	
	// only used for the isCovered op
	MultidimensionalCursor _cursor = null;

	/**
	 * Shallow copy everything in the context passed. Assumes we are
	 * passed an INITIALIZED context.
	 * 
	 * @param context
	 * @throws ThinklabException 
	 */
	public Context(Context context) throws ThinklabException {
		
		this._models.addAll(context._models);
		this._observations.addAll(context._observations);
		this._order.addAll(context._order);
		this._states.putAll(context._states);
		this._extents.putAll(context._extents);
		this._isNull = context._isNull;
		this._namespaceId = context._namespaceId;
		
		initialize();
	}

	public Context() {}
	
	public void addStateUnchecked(IState state) {
		_states.put(state.getObservable(), state);
	}
	
	/**
	 * Scan all extents and return the properties and values, if any, 
	 * that describe their coverage for search and retrieval of
	 * compatible extents. 
	 * 
	 * It works by asking each extent for its storage metadata and
	 * returning any metadata that is indexed by a known property and
	 * points to a topologically comparable object.
	 * 
	 * Relies on the fact that each extent has only one topologically
	 * comparable storage metadata. Throws an unchecked exception if not so.
	 * 
	 * @return
	 */
	public List<Pair<IProperty, ITopologicallyComparable<?>>> getCoverageProperties() {
		ArrayList<Pair<IProperty, ITopologicallyComparable<?>>> ret = 
				new ArrayList<Pair<IProperty,ITopologicallyComparable<?>>>();
		for (IExtent ext : getExtents()) {
			int ncov = 0;
			if (ext instanceof IStorageMetadataProvider) {
				Metadata md = new Metadata();
				((IStorageMetadataProvider)ext).addStorageMetadata(md);
				for (String pid : md.getKeys()) {
					if (Thinklab.get().getProperty(pid) != null &&
						md.get(pid) instanceof ITopologicallyComparable<?>) {

						if (ncov > 0) {
							
							/*
							 * this is an obscure one for sure, but it should not really happen unless the
							 * implementation is screwed up and untested.
							 */
							throw new ThinklabRuntimeException(
									"internal: extent provides more than one topologically comparable storage metadata");
						}
						
						ret.add(new Pair<IProperty, ITopologicallyComparable<?>>(
								Thinklab.p(pid),
								(ITopologicallyComparable<?>)md.get(pid)));
						ncov++;
					}
				}
			}
		}
		return ret;
	}
	
	public void addObservation(IModel o) {
		_models.add(o);
	}
	
	public List<IObservation> getObservations() {
		return _observations;
	}

	@Override
	public int getMultiplicity() {
		
		if (_cursor == null) {
			sort();
		}
		return _cursor.getMultiplicity();
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
		for (int i = 0; i < _order.size(); i++) {
			if (concept.is(_order.get(i).getDomainConcept())) {
				return _cursor.getDimensionSize(i);
			}
		}
		return 0;
	}

	@Override
	public IExtent getExtent(IConcept observable) {
		return _extents.get(observable);
	}

	@Override
	public boolean isCovered(int index) {

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

		if (observation instanceof IExtent) {
			mergeExtent((IExtent)observation);
		} else {
			/*
			 * TODO must be a state with a datasource that we can redefine
			 * for this context, or a model we can recompute in this context.
			 */
		}
		
		sort();

	}

	@Override
	public void merge(IContext context) throws ThinklabException {

		_order.clear(); // in case we have nothing in the context

		for (IExtent e : context.getExtents()) {
			merge(e);
		}
		for (IState s : context.getStates()) {
			merge(s);
		}
	}

	@Override
	public IExtent getTime() {
		return _extents.get(Time.get().TimeDomain());
	}

	@Override
	public IExtent getSpace() {
		return _extents.get(Geospace.get().SpatialDomain());
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
		_generatorFunctions.add(function);
	}
	
	@Override
	public void initialize() throws ThinklabException {

		if (_initialized)
			return;
		
		if (_namespace == null && _namespaceId != null) {
			_namespace = Thinklab.get().getNamespace(_namespaceId);
		}
		
		/*
		 * we only need it in models and contexts for now.
		 */
		_namespaceId = _namespace.getId();

		for (IFunctionDefinition function : _generatorFunctions) {
			
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
		
		for (IModel m : _models) {
			merge(m.observe(this));
		}
		
		_initialized = true;
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
				boolean o1t = o1.getDomainConcept().getConceptSpace().equals("time");
				boolean o2t = o2.getDomainConcept().getConceptSpace().equals("time");
				if (o1t && !o2t) return -1;
				if (!o1t && o2t) return 1;
				return 0;
			}
		});
		
		_cursor = new MultidimensionalCursor(StorageOrdering.ROW_FIRST);
		_cursor.defineDimensions(getDimensionSizes());
	}
	
	private void mergeExtent(IExtent itsExtent) throws ThinklabException {

		IConcept dimension = itsExtent.getDomainConcept();
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

	@Override
	public int hasEqualExtents(IContext second) {
		// TODO Auto-generated method stub
		return 0;
	}

	
}
