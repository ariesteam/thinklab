package org.integratedmodelling.modelling;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.tree.DefaultMutableTreeNode;

import org.integratedmodelling.corescience.utils.Ticker;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.interfaces.query.IQueriable;
import org.integratedmodelling.thinklab.interfaces.query.IQuery;
import org.integratedmodelling.thinklab.interfaces.query.IQueryResult;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.utils.Pair;
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
	 * realizing each dependent must have returned another query result, each a dimension in
	 * our final result options.
	 */
	ArrayList<Pair<IConcept,IQueryResult>> _realizedDependents;
	
	
	int type = 0;
	
	/**
	 * The ticker tells us what dependents are retrieved next
	 */
	Ticker ticker = null;
	
	/*
	 * 
	 */
	int id = 0;
	
	
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
		return (int) ticker.size();
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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void moveTo(int currentItem, int itemsPerPage)
			throws ThinklabException {
		// TODO Auto-generated method stub

	}

	@Override
	public float setResultScore(int n, float score) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void initialize() {
		// initialize the ticker in this (root) node and give all nodes their dimension ID.
	}

}
