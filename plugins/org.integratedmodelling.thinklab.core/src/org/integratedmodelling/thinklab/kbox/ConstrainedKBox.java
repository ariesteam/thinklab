/**
 * Copyright 2011 The ARIES Consortium (http://www.ariesonline.org) and
 * www.integratedmodelling.org. 

   This file is part of Thinklab.

   Thinklab is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published
   by the Free Software Foundation, either version 3 of the License,
   or (at your option) any later version.

   Thinklab is distributed in the hope that it will be useful, but
   WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Thinklab.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.integratedmodelling.thinklab.kbox;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.lang.LogicalConnector;
import org.integratedmodelling.thinklab.api.knowledge.query.IQuery;
import org.integratedmodelling.thinklab.api.knowledge.query.IQueryResult;

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
