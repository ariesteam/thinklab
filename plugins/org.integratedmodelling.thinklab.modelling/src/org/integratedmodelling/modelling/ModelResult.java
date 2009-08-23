package org.integratedmodelling.modelling;

import java.util.ArrayList;
import java.util.HashMap;

import org.integratedmodelling.corescience.utils.Ticker;
import org.integratedmodelling.modelling.exceptions.ThinklabModelException;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.interfaces.query.IQueriable;
import org.integratedmodelling.thinklab.interfaces.query.IQuery;
import org.integratedmodelling.thinklab.interfaces.query.IQueryResult;
import org.integratedmodelling.utils.Polylist;

/**
 * The realize() operation on a IModel produces one of these objects, which acts as a lazy generator of result
 * observations. It reflects the tree of models with their realization options (corresponding to hits from a
 * kbox for each unresolved source).
 * 
 * @author Ferdinando
 *
 */
public class ModelResult implements IQueryResult  {

	private static final long serialVersionUID = 5204113167121644188L;

	public static final int MEDIATOR = 0;
	public static final int TRANSFORMER = 1;
	public static final int EXTERNAL = 2;
	
	/*
	 * each node contains the model, which is used to build each observation
	 */
	IModel _model = null;
	
	/*
	 * querying each dependent must have returned another query result, each a dimension in
	 * our final result options.
	 */
	ArrayList<IQueryResult> _dependents = new ArrayList<IQueryResult>();
	
	/*
	 * the mediated model, if any, is either an external query or a model query.
	 */
	IQueryResult _mediated = null;
	
	int resultCount = 1;
	int type = 0;
	
	/**
	 * The ticker tells us what dependents are retrieved next
	 */
	Ticker ticker = null;
		
	public ModelResult(IModel model) {
		_model = model;
	}

	@Override
	public IValue getBestResult(ISession session) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IQueriable getQueriable() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IQuery getQuery() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IValue getResult(int n, ISession session) throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Polylist getResultAsList(int n, HashMap<String, String> references)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getResultCount() {
		return ticker == null ? 1 : (int) ticker.size();
	}

	@Override
	public IValue getResultField(int n, String schemaField)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getResultOffset() {
		return 0;
	}

	@Override
	public float getResultScore(int n) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTotalResultCount() {
		return getResultCount();
	}

	@Override
	public void moveTo(int currentItem, int itemsPerPage)
			throws ThinklabException {
		
		// TODO Auto-generated method stub

	}

	@Override
	public float setResultScore(int n, float score) {
		return 1.0f;
	}

	public void initialize() {

		// initialize the ticker in this (root) node and give all nodes their dimension ID.
		if (_mediated != null) {
			ticker = new Ticker();
			ticker.addDimension(_mediated.getTotalResultCount());
		} else if (_dependents.size() > 0) {
			ticker = new Ticker();
			for (IQueryResult r : _dependents)
				ticker.addDimension(r.getTotalResultCount());
		}
	}

	public void addMediatedResult(IQueryResult res) throws ThinklabException {
		if (res.getTotalResultCount() <= 0)
			throw new ThinklabModelException(
					"mediated model for " + _model.getId() + " returned no results" );
		_mediated = res;
	}

	public void addDependentResult(IQueryResult res) throws ThinklabException {
		if (res.getTotalResultCount() <= 0)
			throw new ThinklabModelException(
					"dependent model for " + _model.getId() + " returned no results" );
		_dependents.add(res);
	}


}