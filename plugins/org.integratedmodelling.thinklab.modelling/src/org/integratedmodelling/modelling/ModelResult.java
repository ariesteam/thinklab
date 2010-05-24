package org.integratedmodelling.modelling;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.interfaces.internal.Topology;
import org.integratedmodelling.corescience.metadata.Metadata;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.multidimensional.MultidimensionalCursor;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.interfaces.query.IQueriable;
import org.integratedmodelling.thinklab.interfaces.query.IQuery;
import org.integratedmodelling.thinklab.interfaces.query.IQueryResult;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.thinklab.literals.ObjectReferenceValue;
import org.integratedmodelling.utils.Pair;
import org.integratedmodelling.utils.Polylist;

import clojure.lang.IFn;
import clojure.lang.Keyword;

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
	protected IKBox _kbox;
	protected ISession _session;
	
	/*
	 * each node contains the model, which is used to build each observation
	 */
	protected IModel _model = null;
	
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

	// from computing the context model
	private IQueryResult contextModel = null;
	private ArrayList<Topology> contextExt = null;

	private Collection<Topology> _externalExtents = null;

	public ModelResult(IModel model, IKBox kbox, ISession session, Collection<Topology> externalExtents) {
		_model = model;
		_kbox = kbox;
		_session = session;
		_externalExtents  = externalExtents;
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

		/*
		 * contingencies are a dependency on the OR of the contingents, so just make them all 
		 * dependent, but use an obs class that will merge them.
		 */
		if (_contingents.size() > 0) {
			
			boolean hasStateful  = false;
			boolean hasStateless = false;
			
			/* they need to be all stateful or all stateless, the contextualization strategy is
			 * very different for the two.
			 */
			for (IQueryResult mr : _contingents) {
				if (((ModelResult)mr)._model instanceof DefaultStatefulAbstractModel) {
					hasStateful = true;
				} else {
					hasStateless = true;
				}
			}
			
			if (hasStateful && hasStateless) {
				throw new ThinklabValidationException(
						"model: cannot combine stateful and stateless models as contingencies of the same model");
			}
			
			/* if stateless, it will act like a transformer, building all contingencies separately and 
			 * merging states after contextualization. Otherwise, it will embed the switching strategy in
			 * the compiled code.
			 */
			ret =
				hasStateless ?
					ObservationFactory.createStatelessContingencyMerger(((DefaultAbstractModel)_model).observableSpecs) :
					ObservationFactory.createStatefulContingencyMerger(((DefaultAbstractModel)_model).observableSpecs);
			
			for (int i = 0; i < _contingents.size(); i++) {
				Polylist dep = _contingents.get(i).getResultAsList(
						ofs[_dependents.size() + (contextModel == null ? 0 : 1) + i], null);
				dep = ObservationFactory.addReflectedField(dep, "contingencyOrder", new Integer(i));
				ret = ObservationFactory.addDependency(ret, dep);
			}
			
			/*
			 * give it the conditionals and the context model if any
			 */
			if (_conditionals != null) {
				ret = ObservationFactory.addReflectedField(ret, "conditionals", _conditionals);
			}
			
			if (contextModel != null) {
				
				/*
				 * compute context model and pass it to merging observation
				 */
				IInstance cobs = contextModel.getResult(ofs[_dependents.size()], _session).
					asObjectReference().getObject();
				
				IInstance result = ObservationFactory.
					contextualize(cobs, _session, contextExt.toArray(new Topology[contextExt.size()]));
				IObservation contextObs = ObservationFactory.getObservation(result);
								
				if (hasStateless) {

					ArrayList<Pair<Keyword, IState>> cdata = new ArrayList<Pair<Keyword,IState>>();
					
					for (Entry<IConcept, IState> ee : ObservationFactory.getStateMap(
							contextObs).entrySet()) {

						String id = ee.getKey().getLocalName();
						IModel mo = (IModel) ee.getValue().getMetadata().get(
								Metadata.DEFINING_MODEL);
						if (mo != null) {
							id = mo.getId();
						}

						cdata.add(new Pair<Keyword, IState>(
								Keyword.intern(null, id),
								ee.getValue()));
					}

					/*
					 * the observation needs these to compute the switch layer, but we don't
					 * want to build another context so we don't do it here.
					 */
					if (cdata.size() > 0) {
						ret = ObservationFactory.addReflectedField(ret, "contextStates", cdata);
					}

				}
			}
					
		} else {
			ret = _model.buildDefinition(_kbox, _session, _externalExtents);
		}
		
		/*
		 * formal name is the name of the model if any
		 */
		ret = ObservationFactory.addFormalName(ret, _model.getId());
		
		/*
		 * add the model to the resulting observation
		 */
		Metadata metadata = new Metadata(((DefaultAbstractModel)_model).metadata);

		// TODO anything else by default?
		metadata.put(Metadata.DEFINING_MODEL, _model);
		
		// will be added to whatever the obs has already
		ret = ObservationFactory.addReflectedField(ret, "additionalMetadata", metadata);

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
			ticker = new MultidimensionalCursor(MultidimensionalCursor.StorageOrdering.ROW_FIRST);
			ticker.defineDimensions(_mediated.getTotalResultCount());
		} else if (_dependents.size() > 0 || _contingents.size() > 0 || contextModel != null) {

			ticker = new MultidimensionalCursor(MultidimensionalCursor.StorageOrdering.ROW_FIRST);
			int[] dims = new int[_dependents.size() + _contingents.size() + (contextModel == null ? 0 : 1)];
			int i = 0;
			for (IQueryResult r : _dependents)
				dims[i++] = r.getTotalResultCount();
			if (contextModel != null) 
				dims[i++] = contextModel.getTotalResultCount();
			for (IQueryResult r : _contingents)
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
	public void setContextModel(IQueryResult cm, ArrayList<Topology> exts) {
		this.contextModel = cm;
		this.contextExt = exts;
	}


}