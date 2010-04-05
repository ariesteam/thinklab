package org.integratedmodelling.modelling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.integratedmodelling.corescience.context.ContextMapper;
import org.integratedmodelling.corescience.metadata.Metadata;
import org.integratedmodelling.corescience.storage.SwitchLayer;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.interfaces.query.IQueriable;
import org.integratedmodelling.thinklab.interfaces.query.IQuery;
import org.integratedmodelling.thinklab.interfaces.query.IQueryResult;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.thinklab.literals.ObjectReferenceValue;
import org.integratedmodelling.utils.Polylist;
import org.integratedmodelling.utils.multidimensional.MultidimensionalCursor;

import clojure.lang.IFn;

/**
 * The query() operation on a IModel produces one of these objects, which acts as a lazy generator of result
 * observations. It reflects the tree of models with their realization options (corresponding to hits from a
 * kbox for each unresolved source).
 * 
 * @author Ferdinando
 */
public class ModelResult implements IQueryResult  {

	private static final long serialVersionUID = 5204113167121644188L;
	
	// we need these later
	IKBox _kbox;
	ISession _session;
	
	/*
	 * each node contains the model, which is used to build each observation
	 */
	IModel _model = null;
	
	/*
	 * we may have been generated from a single list representing a cached result;
	 * in that case we only have one result
	 */
	Polylist cached = null;
	
	/*
	 * querying each dependent must have returned another query result, each a dimension in
	 * our final result options.
	 */
	ArrayList<IQueryResult> _dependents = new ArrayList<IQueryResult>();
	
	
	/*
	 * querying each dependent must have returned another query result, each a dimension in
	 * our final result options.
	 */
	ArrayList<IQueryResult> _contingents = new ArrayList<IQueryResult>();
		
	/*
	 * the conditional closures corresponding to each contingent model, taken
	 * from :when statements (null if none is defined)
	 */
	ArrayList<IFn> _conditionals = new ArrayList<IFn>();

	
	/*
	 * extent specifications we may need to add
	 */
	ArrayList<Polylist> _extents = new ArrayList<Polylist>();
	
	/*
	 * the mediated model, if any, is either an external query or a model query.
	 */
	IQueryResult _mediated = null;
	
	int resultCount = 1;
	int type = 0;
	
	/**
	 * The ticker tells us what results are retrieved
	 */
	MultidimensionalCursor ticker = null;

	/*
	 * the Model may give us one of these.
	 */
	private SwitchLayer<?> switchLayer = null;

	private Model contextModel;
	private Map<?, ?> contextStateMap;
		
	public ModelResult(IModel model, IKBox kbox, ISession session) {
		_model = model;
		_kbox = kbox;
		_session = session;
	}

	/**
	 * Create a result from one know lists
	 * @param generateObservableQuery
	 * @param res
	 */
	public ModelResult(Polylist list) {
		cached = list;
	}

	@Override
	public IValue getBestResult(ISession session) throws ThinklabException {
		return null;
	}

	@Override
	public IQueriable getQueriable() {
		throw new ThinklabRuntimeException("getQueriable called on ModelResult");
	}

	@Override
	public IQuery getQuery() {
		throw new ThinklabRuntimeException("getQuery called on ModelResult");
	}

	@Override
	public IValue getResult(int n, ISession session) throws ThinklabException {
		Polylist lst = getResultAsList(n, null);
		return new ObjectReferenceValue(session.createObject(lst));
	}

	@Override
	public Polylist getResultAsList(int n, HashMap<String, String> references)
			throws ThinklabException {

		if (cached != null) {
			return cached;
		}
		
		int[] ofs = null;
		if (ticker != null) {
			ofs = ticker.getElementIndexes(n);
		} else {
			ofs = new int[1];
			ofs[0] = n;
		}
		
		
		Polylist ret = null;
		ArrayList<IQueryResult> chosen = new ArrayList<IQueryResult>();
		
		if (_model instanceof Model) {
		
			if (switchLayer != null) {

				int modelId = 0;

				for (int i = 0; i < _contingents.size(); i++) {

					if (switchLayer.isCovered())
						break;

					modelId++;

					// TODO initialize with the global ctx = to and the
					// contingent model's one = from
					ContextMapper cmap = null;
					boolean wasActive = false;
					
					IFn where = _conditionals.get(i);
					for (int st = 0; st < switchLayer.size(); st++) {

						boolean active = cmap.isCovered(st);

						if (active && where != null && contextStateMap != null) {
							
							/*
							 * get the state map for context i and eval the
							 * closure
							 */
							Map<?, ?> state = cmap.getLocalState(
									contextStateMap, st);
							try {
								active = (Boolean) where.invoke(state);
							} catch (Exception e) {
								throw new ThinklabValidationException(e);
							}
							
							if (!wasActive && active)
								wasActive = true;
							
						}

						if (active) {
							switchLayer.set(st, modelId);
						}
					}
					
					if (wasActive)
						chosen.add(_contingents.get(i));
				}
			}

			
			/*
			 * CHECK CONTINGENCIES AND SWITCH LAYER; IF 1 CONTINGENCY, JUST BUILD THAT, ELSE BUILD AN
			 * OBSERVATION MERGER WITH ALL CONTINGENCIES.
			 * 
			 * TODO ALL THIS GOES IN THE DATASOURCE - JUST BUILD THE OBS AND
			 * CONTEXTUALIZE IT.  WILL HAVE TO HANDLE CONTINGENCIES IN COMPILER.
			 */
			if (chosen.size() == 1) {
				
				ret = chosen.get(0).getResultAsList(ofs[0], references);

				/*
				 * TODO the switchlayer should mask the extent if not null.
				 */
				
			} else if (chosen.size() > 1) {
				
				ret = ObservationFactory.createMerger(((DefaultAbstractModel)_model).observableSpecs);
				
				// TODO we must pass them as an array, so that we can reconstruct the order in the
				// switchlayer. Also we must pass only those that 
				for (int i = 0; i < chosen.size(); i++) {
					Polylist dep = chosen.get(i).getResultAsList(ofs[i], null);
					ret = ObservationFactory.addContingency(ret, dep);
				}
				
				if (switchLayer != null)
					ret = ObservationFactory.addReflectedField(ret, "switchLayer", this.switchLayer);
			} else {
				
				/*
				 * TODO decide what to do with the empty result that can only be decided after
				 * the query has returned a result.
				 */
			}
			
		} else {
			 ret = _model.buildDefinition(_kbox, _session);
		}
		
		/*
		 * add the model to the resulting observation
		 */
		HashMap<String,Object> metadata = ((DefaultAbstractModel)_model).metadata;
		if (metadata == null) {
			metadata = new HashMap<String, Object>();
		}
		
		// TODO anything else by default?
		metadata.put(Metadata.DEFINING_MODEL, _model);

		ret = ObservationFactory.addReflectedField(ret, "metadata", metadata);

		if (_mediated != null) {
			
			Polylist med = _mediated.getResultAsList(ofs[0], null);
			ret = ObservationFactory.addMediatedObservation(ret, med);
			
		} else if (_dependents.size() > 0) {
			
			for (int i = 0; i < _dependents.size(); i++) {
				Polylist dep = _dependents.get(i).getResultAsList(ofs[i], null);
				ret = ObservationFactory.addDependency(ret, dep);
			}
		} 
		
		/*
		 * if we had any extents added, add them too
		 */
		for (Polylist ext : _extents) {
			ret = ObservationFactory.addExtent(ret, ext);
		}

		return ret;
	}

	@Override
	public int getResultCount() {
		return 
			cached != null ? 
				1 :
				(ticker == null ? 1 : (int) ticker.getMultiplicity());
	}

	@Override
	public IValue getResultField(int n, String schemaField)
			throws ThinklabException {
		throw new ThinklabRuntimeException("getResultField called on ModelResult");

	}

	@Override
	public int getResultOffset() {
		throw new ThinklabRuntimeException("getResultOffset called on ModelResult");
	}

	@Override
	public float getResultScore(int n) {
		return 1.0f;
	}

	@Override
	public int getTotalResultCount() {
		return getResultCount();
	}

	@Override
	public void moveTo(int currentItem, int itemsPerPage)
			throws ThinklabException {
		throw new ThinklabRuntimeException("moveTo called on ModelResult");
	}

	@Override
	public float setResultScore(int n, float score) {
		throw new ThinklabRuntimeException("setResultScore called on ModelResult");
	}

	public void initialize() {

		if (cached != null)
			return;
		
		// initialize the ticker in this (root) node and give all nodes their dimension ID.
		if (_mediated != null) {
			ticker = new MultidimensionalCursor(MultidimensionalCursor.StorageOrdering.COLUMN_FIRST);
			ticker.defineDimensions(_mediated.getTotalResultCount());
		} else if (_dependents.size() > 0) {
			
			/* 
			 * TODO add dimensions for the context model and contingencies
			 */
			
			ticker = new MultidimensionalCursor(MultidimensionalCursor.StorageOrdering.COLUMN_FIRST);
			int[] dims = new int[_dependents.size()];
			int i = 0;
			for (IQueryResult r : _dependents)
				dims[i++] = r.getTotalResultCount();
			ticker.defineDimensions(dims);
		}
	}

	public void addMediatedResult(IQueryResult res) throws ThinklabException {
		_mediated = res;
	}

	public void addDependentResult(IQueryResult res) throws ThinklabException {
		_dependents.add(res);
	}

	public void addContingentResult(IModel m, ModelResult res) {
		_contingents.add(res);
		_conditionals.add(((DefaultAbstractModel)m).whenClause);
	}

	public void addExtentObservation(Polylist list) {
		_extents.add(list);
	}

	/*
	 * communicate that this result will have to build its contingencies using this
	 * context model and states, and define the switchlayer for the observation merger.
	 */
	public void setContextModel(Model cm, Map<?, ?> statemap, SwitchLayer<IModel> switchLayer) {
		this.contextModel = cm;
		this.contextStateMap = statemap;
		this.switchLayer = switchLayer;
	}


}