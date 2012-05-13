package org.integratedmodelling.thinklab.query.operators;

import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.query.IOperator;
import org.integratedmodelling.thinklab.query.Query;

/**
 * Check for equality of literals.
 * 
 * @author Ferd
 *
 */
public class GreaterOrEqual extends Query implements IOperator {

	private Object _operand;
	
	public GreaterOrEqual(Object what) {
		_operand = what;
	}
	
	@Override
	public Pair<IConcept, Object[]> getQueryParameters() {
		return new Pair<IConcept, Object[]>(
				Thinklab.c(NS.OPERATION_GREATER_OR_EQUAL), 
				new Object[]{_operand});
	}

	@Override
	public boolean isLiteral() {
		return true;
	}

}
