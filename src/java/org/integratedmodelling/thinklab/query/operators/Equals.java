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
public class Equals extends Query implements IOperator {

	private Object _operand;
	
	@Override
	public Pair<IConcept, Object[]> getQueryParameters() {
		return new Pair<IConcept, Object[]>(
				Thinklab.c(NS.OPERATION_EQUALS), 
				new Object[]{_operand});
	}

	@Override
	public boolean isLiteral() {
		// TODO Auto-generated method stub
		return false;
	}

}
