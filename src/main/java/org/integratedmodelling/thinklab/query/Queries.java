package org.integratedmodelling.thinklab.query;

import java.util.HashSet;
import java.util.Set;

import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.lang.Quantifier;
import org.integratedmodelling.thinklab.NS;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IProperty;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.knowledge.query.IOperator;
import org.integratedmodelling.thinklab.api.knowledge.query.IQuery;
import org.integratedmodelling.thinklab.api.modelling.IExtent;
import org.integratedmodelling.thinklab.geospace.literals.ShapeValue;
import org.integratedmodelling.thinklab.query.operators.TableSelect;

/**
 * Convenience class to obtain common operators without creating new objects, 
 * sometimes even in a smart way.
 * 
 * @author Ferd
 *
 */
public class Queries {

	static final public int EQ = 0;
	static final public int LT = 1;
	static final public int GT = 2;
	static final public int LE = 3;
	static final public int GE = 4;
	static final public int NE = 5;
	
	static final public int CROSSES = 6;
	static final public int INTERSECTS = 7;
	static final public int INTERSECTS_ENVELOPE = 8;
	static final public int COVERS = 9;
	static final public int COVERED_BY = 10;
	static final public int OVERLAPS = 11;
	static final public int TOUCHES = 12;
	static final public int CONTAINS = 13;
	static final public int CONTAINED_BY = 14;
	static final public int NEAREST_NEIGHBOUR = 15;
	static final public int WITHIN_DISTANCE = 16;


	/**
	 * Produce a query that will select objects that incarnate the passed concept.
	 * 
	 * @param c
	 * @return
	 */
	public static Query select(IConcept c) {
		return new Query(c);
	}

	/**
	 * Produce a query that will select objects that incarnate the passed concept and 
	 * are in the range of passed property within targetConcept - i.e., suitable 
	 * objects to serve in that role.
	 * 
	 * @param c
	 * @return
	 */
	public static Query select(IConcept c, IProperty restricting, IConcept targetConcept) {
		try {
			return new Query(c, targetConcept.getPropertyRange(restricting));
		} catch (ThinklabException e) {
			throw new ThinklabRuntimeException(e);
		}
	}
	/**
	 * Produce a query that will select objects that incarnate the passed concept, using a
	 * string for the concept.
	 * 
	 * @param c
	 * @return
	 */
	public static Query select(String c) {
		return new Query(Thinklab.c(c));
	}
	
	/**
	 * Produce a query that ANDs all the passed queries, selecting only
	 * objects that match them all.
	 *  
	 * @param queries
	 * @return
	 */
	public static Query and(IQuery ... queries) {
		return new Query(Quantifier.ALL(), queries);
	}

	/**
	 * Produce a query that ORs all the passed queries, selecting objects
	 * that match one or more of them.
	 *  
	 * @param queries
	 * @return
	 */
	public static Query or(IQuery ... queries) {
		return new Query(Quantifier.ANY(), queries);
	}

	/**
	 * Produce a query that selects objects that do not
	 * match any of the passed queries.
	 * 
	 * @param queries
	 * @return
	 */
	public static Query no(IQuery ... queries) {
		return new Query(Quantifier.NONE(), queries);
	}
	
	/**
	 * Produce a query that selects object that match exactly n of the passed
	 * queries.
	 * 
	 * @param n
	 * @param queries
	 * @return
	 */
	public static Query exactly(int n, IQuery ... queries) {
		return new Query(Quantifier.EXACTLY(n), queries);
	}

	/**
	 * Produce a query that selects objects that match at least n of
	 * the passed queries.
	 * 
	 * @param n
	 * @param queries
	 * @return
	 */
	public static Query atLeast(int n, IQuery ... queries) {
		return new Query(Quantifier.RANGE(n, Quantifier.INFINITE), queries);
	}

	/**
	 * Produce a query that selects objects that match at most n of
	 * the passed queries.
	 * 
	 * @param n
	 * @param queries
	 * @return
	 */
	public static Query atMost(int n, IQuery ... queries) {
		return new Query(Quantifier.RANGE(Quantifier.INFINITE, n), queries);
	}

	/**
	 * Produce a query that selects objects that match at between min and max of
	 * the passed queries.
	 * 
	 * @param min
	 * @param max
	 * @param queries
	 * @return
	 */
	public static Query between(int min, int max, IQuery ... queries) {
		return new Query(Quantifier.RANGE(min, max), queries);
	}
	
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
		if (match instanceof ISemanticObject<?> && !((ISemanticObject<?>)match).isLiteral()) {
			return getConformanceQuery((ISemanticObject<?>)match, new HashSet<ISemanticObject<?>>());
		} else if (match instanceof IConcept) {
			return Queries.select((IConcept) match);
		}
		return compare(match, EQ);
	}
	
	static public IQuery intersects(Object match) {
		return new SpatialCompare(match, INTERSECTS);
	}

	static public IQuery contains(Object match) {
		return new SpatialCompare(match, CONTAINS);
	}

	static public IQuery containedBy(Object match) {
		return new SpatialCompare(match, CONTAINED_BY);
	}

	/**
	 * Coverage can be used on spatial and temporal literals or on 
	 * models, contexts, and extents, all with matching arguments 
	 * (argument types can be either of model, context and extent for
	 * the same set of subjects).
	 * 
	 * @param match
	 * @return
	 */
	static public IQuery coveredBy(Object match) {
		if (match instanceof IExtent) {
			return new ExtentCoverageQuery((IExtent)match, COVERED_BY);
		}	
		return new SpatialCompare(match, COVERED_BY);
	}

	/**
	 * Coverage can be used on spatial and temporal literals or on 
	 * models, contexts, and extents, all with matching arguments 
	 * (argument types can be either of model, context and extent for
	 * the same set of subjects).
	 * 
	 * @param match
	 * @return
	 */
	static public IQuery covers(Object match) {
		if (match instanceof IExtent) {
			return new ExtentCoverageQuery((IExtent)match, COVERS);
		}
		return new SpatialCompare(match, COVERS);
	}

	/**
	 * Spatial query for objects that cross the target
	 * 
	 * @param match
	 * @return
	 */
	static public IQuery crosses(Object match) {
		return new SpatialCompare(match, CROSSES);
	}

	static public IQuery intersectsEnvelope(Object match) {
		return new SpatialCompare(match, INTERSECTS_ENVELOPE);
	}

	static public IQuery overlaps(Object match) {
		return new SpatialCompare(match, OVERLAPS);
	}

	static public IQuery touches(Object match) {
		return new SpatialCompare(match, TOUCHES);
	}

	static public IQuery isNeighbour(Object match, double metersWithinCentroid) {
		return new SpatialCompare(match, NEAREST_NEIGHBOUR, metersWithinCentroid);
	}
	
	private static IQuery getConformanceQuery(ISemanticObject<?> match, Set<ISemanticObject<?>> refs) {

		if (refs.contains(match))
			throw new ThinklabRuntimeException("query: cannot match an object with circular dependencies");
		
		refs.add(match);
		
		IQuery ret = Queries.select(match.getDirectType());
		for (Pair<IProperty, ISemanticObject<?>> rel : match.getRelationships()) {
			if (rel.getSecond().isLiteral()) {
				ret = ret.restrict(rel.getFirst(), Queries.compare(rel.getSecond().demote(), EQ));
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
		if (match instanceof ShapeValue && operator == EQ) {
			return new SpatialCompare(match,operator);
		}
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
			case Queries.GE:
				return NS.OPERATION_GREATER_OR_EQUAL;
			case Queries.GT:
				return NS.OPERATION_GREATER_THAN;
			case Queries.LE:
				return NS.OPERATION_LESS_OR_EQUAL;
			case Queries.LT:
				return NS.OPERATION_LESS_THAN;
			case Queries.EQ:
				return NS.OPERATION_EQUALS;
			case Queries.NE:
				return NS.OPERATION_NOT_EQUALS;
			}
			return null;
		}

		@Override
		public boolean isLiteral() {
			return true;
		}

	}
	
	public static class SpatialCompare extends Query implements IOperator {

		private Object _operand;
		private Object _operand2;
		int _operation;
		
		public SpatialCompare(Object what, int operation) {
			_operand = what;
			_operation = operation;
		}
		
		public SpatialCompare(Object match, int operation,
				double distance) {
			this(match, operation);
			_operand2 = distance;
		}

		@Override
		public Pair<IConcept, Object[]> getQueryParameters() {
			return new Pair<IConcept, Object[]>(
					Thinklab.c(getConcept()), 
					_operand2 == null ? 
						new Object[]{_operand} :
						new Object[]{_operand, _operand2});
		}

		private String getConcept() {
			switch (_operation) {
			case Queries.INTERSECTS:
				return NS.OPERATION_INTERSECTS;
			case Queries.CONTAINS:
				return NS.OPERATION_CONTAINS;
			case Queries.CONTAINED_BY:
				return NS.OPERATION_CONTAINED_BY;
			case Queries.COVERED_BY:
				return NS.OPERATION_COVERED_BY;
			case Queries.COVERS:
				return NS.OPERATION_COVERS;
			case Queries.CROSSES:
				return NS.OPERATION_CROSSES;
			case Queries.INTERSECTS_ENVELOPE:
				return NS.OPERATION_INTERSECTS_ENVELOPE;
			case Queries.OVERLAPS:
				return NS.OPERATION_OVERLAPS;
			case Queries.TOUCHES:
				return NS.OPERATION_TOUCHES;
			case Queries.NEAREST_NEIGHBOUR:
				return NS.OPERATION_NEAREST_NEIGHBOUR;
			}
			return null;
		}

		@Override
		public boolean isLiteral() {
			return true;
		}
	}

	public static class ExtentCoverageQuery extends Query {

		private IExtent _operand;
		private int _operator;
		
		public ExtentCoverageQuery(IExtent what, int covers) {
			
			_operand = what;
			_operator = covers;
			
			/*
			 * TODO build query.
			 */
		}
	}
}
