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
package org.integratedmodelling.thinklab.constraint;

import java.util.ArrayList;
import java.util.Collection;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabResourceNotFoundException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.lang.LogicalConnector;
import org.integratedmodelling.lang.Quantifier;
import org.integratedmodelling.lang.SemanticType;
import org.integratedmodelling.lang.Semantics;
import org.integratedmodelling.list.PolyList;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IProperty;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.knowledge.query.IOperator;
import org.integratedmodelling.thinklab.api.knowledge.query.IQuery;
import org.integratedmodelling.thinklab.api.lang.IList;

/**
 * Constraints are made up of restrictions. The Restriction class provides an API to easily build constraints
 * programmatically. Restrictions can be:
 * 
 * <ul>
 * <li>connectors (and,or only) for other restrictions</li>
 * <li>object or class property restrictions (both taking a constraint as argument)</li>
 * <li>literal property restrictions (taking an operator and its arguments if any)</li>
 *</ul>  
 * 
 * All restriction can be introduced with a quantifier. Restrictions serialize to lists.
 * 
 * Restrictions are immutable - there are no setting
 * or modifying methods, only constructors. Static methods OR, AND and NOT, not constructors, are used to build connectors.
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
 *					new Restriction("geospace:hasCentroid", "geospace:within", "POLYGON(10 20, 40 50)"),
 *					new Restriction("geospace:hasCentroid", "geospace:within", "POLYGON(1 2, 4 5)")),
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
	IOperator operator = null;
	IConcept classification = null;
	LogicalConnector connector = LogicalConnector.INTERSECTION;
	ArrayList<Restriction> siblings = new ArrayList<Restriction>();	
	String metadataField = null;
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.constraint.Restriction#isConnector()
	 */
	public boolean isConnector() {
		return siblings.size() > 0;
	}
	
	public boolean isMetadataRestriction() {
		
		boolean ret = metadataField != null;
		if (siblings.size() != 0) {
			ret = true;
			for (Restriction r : siblings) {
				if (!((Restriction)r).isMetadataRestriction()) {
					return false;
				}
			}
		}
		return ret;
	}
	
	public boolean hasMetadataRestriction() {
		
		boolean ret = metadataField != null;
		if (!ret && siblings.size() != 0) {
			for (Restriction r : siblings) {
				if (((Restriction)r).isMetadataRestriction()) {
					return true;
				}
			}
		}
		return ret;
	}
	
	public Restriction getRestrictions() {
		
		if (!this.hasMetadataRestriction()) {
			return this;
		}
		
		ArrayList<Restriction> mr = new ArrayList<Restriction>();
		for (Restriction r : siblings) {
			if (!((Restriction)r).isMetadataRestriction())
				mr.add((Restriction)r);
		}
		
		if (mr.size() == 1) 
			return mr.get(0);
		else if (mr.size() > 1)
			return (Restriction)CONNECTOR(getConnector(), (Restriction[])mr.toArray(new Restriction[mr.size()]));
		
		return null;
		
	}
	
	public Restriction getMetadataRestrictions() {
		
		if (this.isMetadataRestriction()) {
			return this;
		}
		ArrayList<Restriction> mr = new ArrayList<Restriction>();
		for (Restriction r : siblings) {
			if (((Restriction)r).isMetadataRestriction())
				mr.add((Restriction)r);
		}
		
		if (mr.size() == 1) 
			return mr.get(0);
		else if (mr.size() > 1)
			return (Restriction)CONNECTOR(getConnector(), (Restriction[])mr.toArray(new Restriction[mr.size()]));
		
		return null;
		
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.constraint.Restriction#getProperty()
	 */
	public IProperty getProperty() {
		return property;
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.constraint.Restriction#getQuantifier()
	 */
	public Quantifier getQuantifier() {
		return quantifier;
	}
	
	public String getMetadataField() {
		return metadataField;
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.constraint.Restriction#isLiteral()
	 */
	public boolean isLiteral() {
		return property != null && operator != null;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.constraint.Restriction#isClassification()
	 */
	public boolean isClassification() {
		return property != null && classification != null;
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.constraint.Restriction#isObject()
	 */
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
	 * make a new restriction that ORs together all the passed ones.
	 */
	public static Restriction CONNECTOR(LogicalConnector connector, Restriction ... restrictions) {
		Restriction ret = new Restriction();
		ret.connector = connector;
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
	 * @throws ThinklabException 
	 */		
	public Restriction(IProperty property, String operator, Object ... values) throws ThinklabException {
		this.property = property;
		this.operator = retrieveOperator(operator);
		this.opArgs = values;
	}

	/**
	 * Create an object restriction passing another constraint.
	 * @param property
	 * @param constraint
	 */
	public Restriction(IProperty property, IQuery constraint) {
		this.property = property;
		this.constraint = (Constraint)constraint;
	}

	/**
	 * Create a classification restriction
	 * @param property
	 * @param classification
	 * @throws ThinklabException 
	 */
	public Restriction(String property, IConcept classification) throws ThinklabException {
		this.property = KnowledgeManager.get().requireProperty(property);
		this.classification = classification;
	}	
	
	/**
	 * Create a literal restriction passing a String for the argument. If property is 
	 * a string not containing a colon, it's a metadata restriction.
	 * 
	 * @param property
	 * @param operator
	 * @param value
	 */		
	public Restriction(String property, String operator, Object ... values) throws ThinklabException {
		
		if (!property.contains(":")) {
			this.metadataField = property;
		} else {
			this.property = KnowledgeManager.get().requireProperty(property);
		}
		this.operator = retrieveOperator(operator);
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
	 * @throws ThinklabException 
	 */		
	public Restriction(Quantifier quantifier, IProperty property, String operator, Object ... values) throws ThinklabException {
		this.quantifier = quantifier;
		this.property = property;
		this.operator = retrieveOperator(operator);
		this.opArgs = values;
	}

	private static IOperator retrieveOperator(String op) throws ThinklabException {
		
		IOperator ret = null;
		if (!SemanticType.validate(op))
			op = "thinklab-core:" + op;
		
//		ISemanticObject o = KnowledgeManager.get().retrieveInstance(op);
//		
//		if (o != null && o.is(KnowledgeManager.OperatorType())) {
//			ret = (IOperator) o.getImplementation();
//		}
//		
//		if (ret == null) {
//			throw new ThinklabValidationException("operator " + op + " has not been declared");
//		}
		
		return ret;
		
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
		this.operator = retrieveOperator(operator);
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
		ret.metadataField = metadataField;
		
		for (Restriction r : siblings) {
			ret.siblings.add(((Restriction)r).duplicate());
		}
		
		return ret;
	}

	public static Restriction parseList(IList content) throws ThinklabException {

		Restriction ret = new Restriction();

		if (content.first() instanceof LogicalConnector || 
				(content.first() instanceof String && 
						LogicalConnector.isLogicalConnector(content.first().toString()))) {

			ret.connector = LogicalConnector.parseLogicalConnector(content.first().toString());
				
				if (!ret.connector.equals(LogicalConnector.INTERSECTION) &&
						!ret.connector.equals(LogicalConnector.UNION))
						throw new ThinklabValidationException(
								content + 
								"restrictions can only be connected in AND and OR; please use quantifiers for remaining cases");
						
					/* all others must be restrictions */
					Object[] def = content.array();
					for (int i = 1; i < def.length; i++) {
						
						if (! (def[i] instanceof IList)) {
							throw new ThinklabValidationException(
									"restriction: " +
									def[i] + 
									": all elements in  " + 
									ret.connector + " " +
									"list must be restrictions");
						}
						ret.siblings.add(parseList((IList)def[i]));
					}
		} else {
			
			Object[] objs = content.array();
			
			int start = 0;
			
			if (objs[0] instanceof Quantifier || Quantifier.isQuantifier(objs[0].toString())) {
				
				ret.quantifier = 
					objs[0] instanceof Quantifier ? 
						(Quantifier)objs[0] :
						Quantifier.parseQuantifier(objs[0].toString());

				
				start = 1;
			}
			
			if (objs[start].toString().contains(":")) {
				ret.property = 
					objs[start] instanceof IProperty ? 
							(IProperty)objs[start] :
							KnowledgeManager.get().requireProperty(objs[start].toString());
			} else {
				ret.metadataField = objs[start].toString();
			}
			
			if (objs.length > (start + 1)) {
				
				start ++;
				Object rest = objs[start];

				if (rest instanceof IList) {
					ret.constraint = new Constraint((IList)rest);
				} else {
					
					/*
					 * should be an operator, possibly with arguments
					 */
					if (rest instanceof ISemanticObject) {
						ret.operator = (IOperator)((ISemanticObject)rest).getObject();
					} else {
						ret.operator = retrieveOperator(rest.toString());
					}
					
					for (int arg = 0, i = start+1; i < objs.length ; i++) {
						
						if (ret.opArgs == null)
							ret.opArgs = new Object[objs.length - start - 1];
						ret.opArgs[arg++] = objs[i];
					}
				}
				
			}	
		}
		
		return ret;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.constraint.Restriction#asList()
	 */
	public IList asList() {

		ArrayList<Object> ret = new ArrayList<Object>();
		
		if (siblings.size() > 0) {
			ret.add(connector.toString());
			for (Restriction r : siblings)
				ret.add(r.asList());
		} else {
			
			ret.add(quantifier);
			
			if (metadataField != null)
				ret.add(metadataField);
			else
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
		
		return PolyList.fromArray(ret.toArray());
	}
	
	private boolean matchConnector(Semantics c) throws ThinklabException {
		
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
	
	private boolean matchRelationships(Semantics c) throws ThinklabException {
		
		boolean ret = false;
        int tot = 0;
        int match = 0;
        
        for (Semantics ipc : c.getRelationships(property)) {

        	tot ++;

//        	if (classification != null && ipc.isClassification() && 
//        			ipc.getValue().getConcept().is(classification)) {
//        		match++;
//        	}  else if (operator != null && ipc.isLiteral()) {
//
//        		if (matchOperator(operator, ipc.getValue(), opArgs))
//        			match ++;
//
//        	} else if (constraint != null && ipc.isObject()) {
//
//        		if (constraint.match(ipc.getObject()))
//        			match++;	
//        
//        	} else {
//        		/* we have no operator and no constraints context, so all
//	               we're checking is the existence of the relationship */
//        		match++;
//        	}
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

	private boolean matchOperator(IOperator operator, ISemanticObject value, Object[] opArgs) throws ThinklabException {

		/* FIXME TLC-31: Implement type declarations for arguments to op() in IValue
				 http://ecoinformatics.uvm.edu:8080/jira/browse/TLC-31
			Once this is done, we should validate anything that's not an IValue to the
			appropriate parameter type, and pass ALL parameters to op()
		 */	
//		return operator.eval(opArgs).asBoolean();
		return false;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.constraint.Restriction#match(org.integratedmodelling.thinklab.api.knowledge.IInstance)
	 */
	public boolean match(Semantics ilist) throws ThinklabException {

		boolean ok = false;
		if (isConnector()) {
			ok = matchConnector(ilist);
		} else {
			ok = matchRelationships(ilist);
		}
		return ok;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.constraint.Restriction#getClassificationConcept()
	 */
	public IConcept getClassificationConcept() {
		return classification;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.constraint.Restriction#getChildren()
	 */
	public Collection<Restriction> getChildren() {
		return siblings;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.constraint.Restriction#getConnector()
	 */
	public LogicalConnector getConnector() {
		return connector;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.constraint.Restriction#getSubQuery()
	 */
	public Constraint getSubQuery() {
		return constraint;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.constraint.Restriction#getOperator()
	 */
	public IOperator getOperator() {
		return operator;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.constraint.Restriction#getOperatorArguments()
	 */
	public Object[] getOperatorArguments() {
		return opArgs;
	}

    public String toString() {
    	return asList().toString();
    }

}