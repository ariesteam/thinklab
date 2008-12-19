/**
 * Restriction.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of Thinklab.
 * 
 * Thinklab is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Thinklab is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the software; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * ----------------------------------------------------------------------------------
 * 
 * @copyright 2008 www.integratedmodelling.org
 * @author    Ferdinando Villa (fvilla@uvm.edu)
 * @author    Ioannis N. Athanasiadis (ioannis@athanasiadis.info)
 * @date      Jan 17, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.thinklab.constraint;

import java.util.ArrayList;
import java.util.Collection;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.SemanticType;
import org.integratedmodelling.thinklab.exception.ThinklabConstraintValidationException;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabInappropriateOperationException;
import org.integratedmodelling.thinklab.exception.ThinklabMalformedSemanticTypeException;
import org.integratedmodelling.thinklab.exception.ThinklabNoKMException;
import org.integratedmodelling.thinklab.exception.ThinklabResourceNotFoundException;
import org.integratedmodelling.thinklab.exception.ThinklabValueConversionException;
import org.integratedmodelling.thinklab.interfaces.IConcept;
import org.integratedmodelling.thinklab.interfaces.IKnowledgeSubject;
import org.integratedmodelling.thinklab.interfaces.IProperty;
import org.integratedmodelling.thinklab.interfaces.IRelationship;
import org.integratedmodelling.thinklab.interfaces.IValue;
import org.integratedmodelling.utils.LogicalConnector;
import org.integratedmodelling.utils.MalformedLogicalConnectorException;
import org.integratedmodelling.utils.MalformedQuantifierException;
import org.integratedmodelling.utils.Polylist;
import org.integratedmodelling.utils.Quantifier;

/**
 * Constraints are made up of restrictions. The Restriction class provides an API to easily build constraints
 * programmatically. Restrictions can be:
 * 
 * <ul>
 * <li>connectors for other restrictions</li>
 * <li>connectors for other restrictions</li>
 * <li>connectors for other restrictions</li>
 * <li>connectors for other restrictions</li>
 *</ul>  
 * 
 * Restrictions serialize to lists.
 * 
 * Restrictions are entirely defined when constructed, and have many constructors. There are no setting
 * or modifying methods. Static methods OR, AND and NOT, not constructors, are used to build connectors.
 * 
 * Example of use (makes no sense ontology-wise):
 * 
 * <pre>
 * 
 * 		Constraint query = new Constraint("observation:Observation").restrict(
 *	            
 *              // new literal restriction: property, operator and argument(s). The argument can be
 *              // an IValue or is turned into one using the operator declaration.				
 *				Restriction.OR(
 *					new Restriction("geospace:hasCentroid", "within", "POLYGON(10 20, 40 50)"),
 *					new Restriction("geospace:hasCentroid", "within", "POLYGON(1 2, 4 5)")),
 * 
 *              // new object restriction, passing a new constraint.
 *				new Restriction("observation:dependsOn", 
 *						new Constraint("geospace:SpatialCoverageObservation")));
 * </pre>
 * 
 * @author Ferdinando Villa
 *
 */
public class Restriction  {
	
	Quantifier quantifier = new Quantifier(Quantifier.ANY);
	IProperty  property = null;
	Constraint constraint = null;
	Object[] opArgs = null;
	String operator = null;
	IConcept classification = null;
	LogicalConnector connector = LogicalConnector.INTERSECTION;
	ArrayList<Restriction> siblings = new ArrayList<Restriction>();	
	
	public boolean isConnector() {
		return siblings.size() > 0;
	}
	
	public IProperty getProperty() {
		return property;
	}
	
	public Quantifier getQuantifier() {
		return quantifier;
	}
	
	public boolean isLiteral() {
		return property != null && operator != null;
	}

	public boolean isClassification() {
		return property != null && classification != null;
	}
	
	public boolean isObject() {
		return property != null && (constraint != null || property.isObjectProperty());
	}
	
	/**
	 * make a new restriction that ANDS together all the passed ones.
	 */
	public static Restriction AND(Restriction ... restrictions) {
		Restriction ret = new Restriction();
		ret.connector = LogicalConnector.INTERSECTION;
		for (Restriction restriction : restrictions)
			ret.siblings.add(restriction);
		return ret;
	}

	/**
	 * make a new restriction that ORs together all the passed ones.
	 */
	public static Restriction OR(Restriction ... restrictions) {
		Restriction ret = new Restriction();
		ret.connector = LogicalConnector.UNION;
		for (Restriction restriction : restrictions)
			ret.siblings.add(restriction);
		return ret;
	}

	/**
	 * Create a restriction on having a value for the passed property
	 * @param property
	 * @throws ThinklabException if the property is not found or there is no KM
	 */
	public Restriction(String property) throws ThinklabException {
		this.property = KnowledgeManager.get().requireProperty(property);
	}

	/**
	 * Create a restriction on having a value for the passed property
	 * @param property
	 */
	public Restriction(IProperty property) {
		this.property = property;
	}

	/**
	 * Create a restriction on having a specified number of values for the passed property
	 * @param q 
	 * @param property
	 */
	public Restriction(Quantifier q, IProperty p) {
		this.quantifier = q;
		this.property = p;
	}

	/**
	 * Create a restriction on having a specified number of values for the passed property
	 * @param q 
	 * @param property
	 */
	public Restriction(Quantifier q, String property) throws ThinklabException {
		this.quantifier = q;
		this.property = KnowledgeManager.get().requireProperty(property);
	}

	
	/**
	 * Create a classification restriction
	 * @param property
	 * @param classification
	 */
	public Restriction(IProperty property, IConcept classification) {
		this.property = property;
		this.classification = classification;
	}	
	
	/**
	 * Create a literal restriction passing a String for the argument 
	 * @param property
	 * @param operator
	 * @param value
	 */		
	public Restriction(IProperty property, String operator, Object ... values) {
		this.property = property;
		this.operator = operator;
		this.opArgs = values;
	}

	/**
	 * Create an object restriction passing another constraint.
	 * @param property
	 * @param constraint
	 */
	public Restriction(IProperty property, Constraint constraint) {
		this.property = property;
		this.constraint = constraint;
	}

	/**
	 * Create a classification restriction
	 * @param property
	 * @param classification
	 * @throws ThinklabNoKMException 
	 * @throws ThinklabResourceNotFoundException 
	 * @throws ThinklabMalformedSemanticTypeException 
	 */
	public Restriction(String property, IConcept classification) throws ThinklabException {
		this.property = KnowledgeManager.get().requireProperty(property);
		this.classification = classification;
	}	
	
	/**
	 * Create a literal restriction passing a String for the argument 
	 * @param property
	 * @param operator
	 * @param value
	 */		
	public Restriction(String property, String operator, Object ... values) throws ThinklabException {
		this.property = KnowledgeManager.get().requireProperty(property);
		this.operator = operator;
		this.opArgs = values;
	}

	/**
	 * Create an object restriction passing another constraint.
	 * @param property
	 * @param constraint
	 */
	public Restriction(String property, Constraint constraint) throws ThinklabException {
		this.property = KnowledgeManager.get().requireProperty(property);
		this.constraint = constraint;
	}

	/**
	 * Create a classification restriction
	 * @param property
	 * @param classification
	 */
	public Restriction(Quantifier quantifier, IProperty property, IConcept classification) {
		this.quantifier = quantifier;
		this.property = property;
		this.classification = classification;
	}	
	
	/**
	 * Create a literal restriction passing a String for the argument 
	 * @param property
	 * @param operator
	 * @param value
	 */		
	public Restriction(Quantifier quantifier, IProperty property, String operator, Object ... values) {
		this.quantifier = quantifier;
		this.property = property;
		this.operator = operator;
		this.opArgs = values;
	}

	/**
	 * Create an object restriction passing another constraint.
	 * @param property
	 * @param constraint
	 */
	public Restriction(Quantifier quantifier, IProperty property, Constraint constraint) {
		this.quantifier = quantifier;
		this.property = property;
		this.constraint = constraint;
	}

	/**
	 * Create a classification restriction
	 * @param property
	 * @param classification
	 * @throws ThinklabNoKMException 
	 * @throws ThinklabResourceNotFoundException 
	 * @throws ThinklabMalformedSemanticTypeException 
	 */
	public Restriction(Quantifier quantifier, String property, IConcept classification) throws ThinklabException {
		this.quantifier = quantifier;
		this.property = KnowledgeManager.get().requireProperty(property);
		this.classification = classification;
	}	
	
	/**
	 * Create a literal restriction passing a String for the argument 
	 * @param property
	 * @param operator
	 * @param value
	 */		
	public Restriction(Quantifier quantifier, String property, String operator, Object ... values) throws ThinklabException {
		this.quantifier = quantifier;
		this.property = KnowledgeManager.get().requireProperty(property);
		this.operator = operator;
		this.opArgs = values;
	}

	/**
	 * Create an object restriction passing another constraint.
	 * @param property
	 * @param constraint
	 */
	public Restriction(Quantifier quantifier, String property, Constraint constraint) throws ThinklabException {
		this.quantifier = quantifier;
		this.property = KnowledgeManager.get().requireProperty(property);
		this.constraint = constraint;
	}

	
	/* create a naked connector, but only for internal purposes. */
	private Restriction() {
	}

	Restriction(LogicalConnector connector) {
		this.connector = connector;
	}

	public Restriction duplicate() {
		
		Restriction ret = new Restriction();
		
		ret.quantifier = quantifier;
		ret.property = property;
		ret.constraint = constraint;
		ret.opArgs = opArgs;
		ret.operator = operator;
		ret.classification = classification;
		ret.connector = connector;
		
		for (Restriction r : siblings) {
			ret.siblings.add(r.duplicate());
		}
		
		return ret;
	}

	public static Restriction parseList(Polylist content) throws ThinklabException {

		Restriction ret = new Restriction();
		
		/* inspect first elements: can be a lone connector */
		if (content.first() instanceof String && 
			LogicalConnector.isLogicalConnector(content.first().toString())) {

			try {
				ret.connector = LogicalConnector.parseLogicalConnector(content.first().toString());
			} catch (MalformedLogicalConnectorException e) {
				/* won't happen */
			}
			
			if (!ret.connector.equals(LogicalConnector.INTERSECTION) &&
				!ret.connector.equals(LogicalConnector.UNION))
				throw new ThinklabConstraintValidationException(
						content + 
						"restrictions can only be connected in AND and OR; please use quantifiers for remaining cases");
				
			/* all others must be restrictions */
			Object[] def = content.array();
			for (int i = 1; i < def.length; i++) {
				
				if (! (def[i] instanceof Polylist)) {
					throw new ThinklabConstraintValidationException(
							"restriction: " +
							def[i] + 
							": all elements in  " + 
							ret.connector + " " +
							"list must be restrictions");
				}
				ret.siblings.add(parseList((Polylist)def[i]));
			}
			return ret;
		}
		
		/* otherwise we must have a quantifier, a property, or both. Count the elements to 
		 * use for the scope */
		Object[] def = content.array();

		int nn;
		
		for (nn = 0; nn < def.length && !(def[nn] instanceof Polylist) && nn < 2; nn++) {
			
			if (def[nn] instanceof IProperty) {	
				ret.property = (IProperty)def[nn];	
			} else if (def[nn] instanceof Quantifier)  {
				ret.quantifier = (Quantifier)def[nn];
			} else if (Quantifier.isQuantifier(def[nn].toString())) {
				try {
					ret.quantifier = Quantifier.parseQuantifier(def[nn].toString());
				} catch (MalformedQuantifierException e) {
				}
			} else if (SemanticType.validate(def[nn].toString())) {
				if (ret.property == null)
					ret.property = KnowledgeManager.get().requireProperty(def[nn].toString());
				else 	
					ret.classification = KnowledgeManager.get().requireConcept(def[nn].toString());
			} else if (def[nn] instanceof String){
				/* can only be an operator as second argument */
				ret.operator = (String)def[nn];
			} else {
				throw new ThinklabConstraintValidationException(
						"restriction: can't recognize element " + 
						def[nn] +
						" in " +
						content);
			}
		}
		
		/* the rest can be an operator specification for literals, a concept for classifications, or
		 * a constraint list for object properties */
		int remaining = def.length - nn;
		
		if (remaining == 1 && def[nn] instanceof Polylist) {
			/* object restriction */
			ret.constraint = new Constraint((Polylist)def[nn]);
		} else if (remaining == 1 && ret.classification == null && SemanticType.validate(def[nn].toString())) {
			/* class restriction */
			ret.classification = KnowledgeManager.get().requireConcept(def[nn].toString());
		} else if (remaining >= 1) {
			
			/* 
			 * operator: must be an initial string with no strange stuff in it, and an optional
			 * number of parameters, to be stored as they come.
			 * 
			 * TODO operators should be instances of thinklab-core:Operator, or string IDs names of such
			 * instances. This messes with the concept recognition above.
			 */
			if ( !(def[nn] instanceof String)) 
				throw new ThinklabConstraintValidationException("invalid restriction operator at " + def[nn]);
			
			if (ret.operator == null) {
				ret.operator = def[nn].toString();
				remaining --;
				nn++;
			}
			
			if (remaining > 0) {
				
				ret.opArgs = new Object[remaining];

				int i = 0;
				for (; nn < def.length; nn++) {
					ret.opArgs[i++] = def[nn];
				}
			}
			
		} else {
			throw new ThinklabConstraintValidationException(
					"invalid restriction specification in " +
					content);
		}
		
		if (ret.property == null)
			throw new ThinklabConstraintValidationException(
					"invalid restriction specification: missing property in " +
					content);
		
		return ret;
	}

	public Polylist asList() {

		ArrayList<Object> ret = new ArrayList<Object>();
		
		if (siblings.size() > 0) {
			ret.add(connector.toString());
			for (Restriction r : siblings)
				ret.add(r.asList());
		} else {
			
			ret.add(quantifier);
			ret.add(property);
			
			if (operator != null) {
				ret.add(operator);
				if (opArgs != null)
					for (int i = 0; i < opArgs.length; i++)
						ret.add(opArgs[i]);
			} else if (classification != null) {
				ret.add(classification);
			} else if (constraint != null) {
				ret.add(constraint.asList());
			}
		}
		
		return Polylist.PolylistFromArray(ret.toArray());
	}
	
	private boolean matchConnector(IKnowledgeSubject c) throws ThinklabException {
		
		boolean ret = false;
		
        int tot = 0;
        int match = 0;
        
        for (Restriction restriction : siblings) {
            
            ret = restriction.match(c);
            
            if (connector.equals(LogicalConnector.INTERSECTION) && !ret)
                return false;
            if (connector.equals(LogicalConnector.UNION) && ret)
                return true;
            
            if (ret)
                match++;
            tot ++;
        }
        if (connector.equals(LogicalConnector.DISJOINT_UNION))
            ret = (tot > 0 && match == 1);
        else if (connector.equals(LogicalConnector.EXCLUSION))
            ret = (match == 0 && tot > 0);
        
    	return ret;
	}
	
	private boolean matchRelationships(IKnowledgeSubject c) throws ThinklabException {
		
		boolean ret = false;
        int tot = 0;
        int match = 0;
        
        for (IRelationship ipc : c.getRelationshipsTransitive(property.toString())) {

        	tot ++;

        	if (classification != null && ipc.isClassification() && 
        			ipc.getValue().getConcept().is(classification)) {
        		match++;
        	}  else if (operator != null && ipc.isLiteral()) {

        		if (matchOperator(operator, ipc.getValue(), opArgs))
        			match ++;

        	} else if (constraint != null && ipc.isObject()) {

        		if (constraint.match(ipc.getValue().asObjectReference().getObject()))
        			match++;	
        
        	} else {
        		/* we have no operator and no constraints context, so all
	               we're checking is the existence of the relationship */
        		match++;
        	}
        }


        switch (quantifier.type) {

        case Quantifier.ALL:
        	ret = match == tot && tot > 0;
        	break;

        case Quantifier.ANY:
        	ret = match > 0;
        	break;

        case Quantifier.RANGE:
        	ret = (match >= quantifier.max) && (match <= quantifier.min);
        	break;

        case Quantifier.EXACT:
        	ret = match == quantifier.min;
        	break;

        case Quantifier.ERROR:
        	ret = false;
        	break;
        }
        
        return ret;
	}

	private boolean matchOperator(String operator, IValue value, Object[] opArgs) throws ThinklabValueConversionException, ThinklabInappropriateOperationException {

		/* FIXME TLC-31: Implement type declarations for arguments to op() in IValue
				 http://ecoinformatics.uvm.edu:8080/jira/browse/TLC-31
			Once this is done, we should validate anything that's not an IValue to the
			appropriate parameter type, and pass ALL parameters to op()
		 */
		
		IValue arg = null;
		if (opArgs != null && opArgs.length >= 1 && opArgs[0] instanceof IValue)
			arg = (IValue)opArgs[0];
		
		return value.op(operator, arg).asBoolean().value;
		
	}

	public boolean match(IKnowledgeSubject c) throws ThinklabException {

		boolean ok = false;
		if (isConnector()) {
			ok = matchConnector(c);
		} else {
			ok = matchRelationships(c);
		}
		return ok;
	}

	public IConcept getClassificationConcept() {
		return classification;
	}

	public Collection<Restriction> getChildren() {
		return siblings;
	}

	public LogicalConnector getConnector() {
		return connector;
	}

	public Constraint getSubConstraint() {
		return constraint;
	}

	public String getOperator() {
		return operator;
	}

	public Object[] getOperatorArguments() {
		return opArgs;
	}

    public String toString() {
    	return asList().toString();
    }

}