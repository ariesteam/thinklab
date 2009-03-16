package org.integratedmodelling.thinklab.kbox;

import org.integratedmodelling.thinklab.interfaces.query.IQuery;

/**
 * A kbox that implements ConstrainedKBox constrains its queries with one overall
 * constraint that is defined for the whole kbox.
 * 
 * @author Ferdinando Villa
 *
 */
public interface ConstrainedKBox {

	public void setConstraint(IQuery c);
}
