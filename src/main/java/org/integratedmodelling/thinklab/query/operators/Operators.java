package org.integratedmodelling.thinklab.query.operators;

import java.util.HashSet;
import java.util.Set;

import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IProperty;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.knowledge.query.IOperator;
import org.integratedmodelling.thinklab.api.knowledge.query.IQuery;
import org.integratedmodelling.thinklab.query.Query;

/**
 * Convenience class to obtain common operators without creating new objects, 
 * sometimes even in a smart way.
 * 
 * @author Ferd
 *
 */
public class Operators {

	static final public int EQ = 0;
	static final public int LT = 1;
	static final public int GT = 2;
	static final public int LE = 3;
	static final public int GE = 4;
	static final public int NE = 5;
	
	/**
	 * 
	 * @param field
	 * @param match
	 * @return
	 */
	static public IQuery tableSelect(String field, IQuery match) {
		return new TableSelect(field, match);
	}
	
	/**
	 * Return an appropriate operator to express identity for the
	 * passed object. If a semantic object, return a default semantic 
	 * matcher; if a POD, return an appropriate equality operator.
	 * 
	 * Will throw a runtime exception if we try to create a query to match
	 * a semantic object whose properties create cyclic references.
	 * 
	 * @param match
	 */
	static public IQuery is(Object match) {
		if (match instanceof ISemanticObject<?>) {
			return getConformanceQuery((ISemanticObject<?>)match, new HashSet<ISemanticObject<?>>());
		} else if (match instanceof IConcept) {
			return Query.select((IConcept) match);
		}
		return compare(match, EQ);
	}
	
	private static IQuery getConformanceQuery(ISemanticObject<?> match, Set<ISemanticObject<?>> refs) {

		if (refs.contains(match))
			throw new ThinklabRuntimeException("query: cannot match an object with circular dependencies");
		
		refs.add(match);
		
		IQuery ret = Query.select(match.getDirectType());
		for (Pair<IProperty, ISemanticObject<?>> rel : match.getRelationships()) {
			if (rel.getSecond().isLiteral()) {
				ret = ret.restrict(rel.getFirst(), Operators.compare(rel.getSecond().demote(), EQ));
			} else {
				ret = ret.restrict(rel.getFirst(), getConformanceQuery(rel.getSecond(), refs));
			}
		}

		return ret;
	}

	/**
	 * Return an appropriate comparison operator for the object
	 * passed and the chosen comparison, which should be one of
	 * the constants defined in this class.
	 * 
	 * @param match
	 * @param operator
	 * @return
	 */
	static public IQuery compare(Object match, int operator) {
		return new Compare(match, operator);
	}
	
	/*
	 * --------------------------------------------------------------------------
	 * Some of the actual IOperators we dispense
	 * -------------------------------------------------------------------------- 
	 */
	
	public static class Compare extends Query implements IOperator {

		private Object _operand;
		int _operation;
		
		public Compare(Object what, int operation) {
			_operand = what;
			_operation = operation;
		}
		
		@Override
		public Pair<IConcept, Object[]> getQueryParameters() {
			return new Pair<IConcept, Object[]>(
					Thinklab.c(getConcept()), 
					new Object[]{_operand});
		}

		private String getConcept() {
			switch (_operation) {
			case Operators.GE:
				return NS.OPERATION_GREATER_OR_EQUAL;
			case Operators.GT:
				return NS.OPERATION_GREATER_THAN;
			case Operators.LE:
				return NS.OPERATION_LESS_OR_EQUAL;
			case Operators.LT:
				return NS.OPERATION_LESS_THAN;
			case Operators.EQ:
				return NS.OPERATION_EQUALS;
			case Operators.NE:
				return NS.OPERATION_NOT_EQUALS;
			}
			return null;
		}

		@Override
		public boolean isLiteral() {
			return true;
		}

	}

	
}
