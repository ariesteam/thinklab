package org.integratedmodelling.thinklab.kbox;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.query.IQuery;
import org.integratedmodelling.thinklab.interfaces.query.IQueryResult;
import org.integratedmodelling.utils.LogicalConnector;

/**
 * A kbox that implements ConstrainedKBox constrains its queries with one overall
 * constraint that is defined for the whole kbox.
 * 
 * @author Ferdinando Villa
 *
 */
public class ConstrainedKBox extends KBoxWrapper {

	IQuery constraint = null;

	private IQuery merge(IQuery q) throws ThinklabException {
		return q.merge(constraint, LogicalConnector.INTERSECTION);
	}

	public void setConstraint(IQuery c) {
		this.constraint = c;
	}

	@Override
	public IQueryResult query(IQuery q, int offset, int maxResults)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return super.query(merge(q), offset, maxResults);
	}

	@Override
	public IQueryResult query(IQuery q, String[] metadata, int offset,
			int maxResults) throws ThinklabException {
		// TODO Auto-generated method stub
		return super.query(merge(q), metadata, offset, maxResults);
	}

	@Override
	public IQueryResult query(IQuery q) throws ThinklabException {
		// TODO Auto-generated method stub
		return super.query(merge(q));
	}


}
