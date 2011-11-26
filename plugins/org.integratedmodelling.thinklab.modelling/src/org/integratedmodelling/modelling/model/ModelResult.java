package org.integratedmodelling.modelling.model;

import java.util.ArrayList;
import java.util.HashMap;

import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.metadata.Metadata;
import org.integratedmodelling.modelling.ObservationFactory;
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
import org.integratedmodelling.thinklab.kbox.GroupingQueryResult;
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
	private IContext contextExt = null;
	private IContext _externalExtents = null;

	public ModelResult(IModel model, IKBox kbox, ISession session, IContext context) {
		_model = model;
		_kbox = kbox;
		_session = session;
		_externalExtents  = context;
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
				if (((DefaultAbstractModel)((ModelResult)mr)._model).isStateful()) {
					hasStateful = true;
				} else {
					hasStateless = true;
				}
			}
			
			if (hasStateful && hasStateless) {
				throw new ThinklabValidationException(
						"model: cannot combine stateful and stateless models as contingencies of the same model");
			}
			
			/* 
			 * if stateless, it will act like a transformer, building all contingencies separately and 
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
				
				IObservationContext cContext = 
					ObservationFactory.contextualize(cobs, _session, contextExt);
								
				if (hasStateless) {

					ArrayList<Pair<Keyword, IState>> cdata = new ArrayList<Pair<Keyword,IState>>();
					
					for (IState cstate : cContext.getStates()) {

						String id = cstate.getObservableClass().getLocalName();
						IModel mo = (IModel) cstate.getMetadata().get(
								Metadata.DEFINING_MODEL);
						if (mo != null) {
							id = mo.getName();
						}

						cdata.add(new Pair<Keyword, IState>(
								Keyword.intern(null, id),
								cstate));
					}

					/*
					 * the observation needs these to compute the switch layer, but we don't
					 * want to build another context so we don't do it here.
					 */
					if (cdata.size() > 0) {
						ret = ObservationFactory.addReflectedField(ret, "contextStates", cdata);
					}
				} else {
					ret = ObservationFactory.addReflectedField(ret, "contextState", cContext);
				}
			}
					
		} else {
			ret = _model.buildDefinition(_kbox, _session, _externalExtents, 0);
		}
		
		/*
		 * formal name is the name of the model if any
		 */
		ret = ObservationFactory.addFormalName(ret, ((DefaultAbstractModel)_model).getLocalFormalName());
		
		if (_mediated != null) {
			
			Polylist med = null;
			
			if (_mediated instanceof GroupingQueryResult &&
				((GroupingQueryResult)_mediated).getResultMultiplicity(ofs[0]) > 1) {
				
				GroupingQueryResult gc = ((GroupingQueryResult)_mediated);
				IConcept c = _model.getObservableClass();
				med = ObservationFactory.createStatefulContingencyMerger(Polylist.list(c));

				for (int i = 0; i < gc.getResultMultiplicity(ofs[0]); i++) {
					Polylist dl = gc.getResultAsList(ofs[0], i, null);
					dl = ObservationFactory.addReflectedField(dl, "contingencyOrder", new Integer(i));
					med = ObservationFactory.addDependency(med, dl);
				}
				
			} else {
				med = _mediated.getResultAsList(ofs[0], null);
			}
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

	public int getAlternativeDependencyCount() {
		return _dependents.size() + _contingents.size() + (contextModel == null ? 0 : 1);
	}
	
	public int[] getAlternativeDependencyDimensions() {
		int[] dims = new int[getAlternativeDependencyCount()];
		int i = 0;
		for (IQueryResult r : _dependents)
			dims[i++] = r.getTotalResultCount();
		if (contextModel != null) 
			dims[i++] = contextModel.getTotalResultCount();
		for (IQueryResult r : _contingents)
			dims[i++] = r.getTotalResultCount();
		return dims;
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
		} else if (getAlternativeDependencyCount() > 0) {
			ticker = new MultidimensionalCursor(MultidimensionalCursor.StorageOrdering.ROW_FIRST);
			ticker.defineDimensions(getAlternativeDependencyDimensions());
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
	public void setContextModel(IQueryResult cm, IContext exts) {
		this.contextModel = cm;
		this.contextExt = exts;
	}


}