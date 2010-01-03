package org.integratedmodelling.modelling;

import java.util.ArrayList;
import java.util.HashMap;

import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.interfaces.query.IQueriable;
import org.integratedmodelling.thinklab.interfaces.query.IQuery;
import org.integratedmodelling.thinklab.interfaces.query.IQueryResult;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.thinklab.literals.ObjectReferenceValue;
import org.integratedmodelling.utils.Polylist;
import org.integratedmodelling.utils.multidimensional.MultidimensionalCursor;

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
		
		Polylist ret = _model.buildDefinition(_kbox, _session);
		
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

	public void addContingentResult(ModelResult contingentRes) {
		// TODO figure out how to manage the multiplicity
		throw new ThinklabRuntimeException("unimplemented addContingentResult called on ModelResult");
	}

	public void addExtentObservation(Polylist list) {
		_extents.add(list);
	}


}