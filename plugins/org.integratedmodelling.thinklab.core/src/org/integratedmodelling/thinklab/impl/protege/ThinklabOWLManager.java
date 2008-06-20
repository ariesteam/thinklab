/**
 * ThinklabOWLManager.java
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
package org.integratedmodelling.thinklab.impl.protege;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.integratedmodelling.thinklab.KnowledgeTree;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.SemanticType;
import org.integratedmodelling.thinklab.constraint.Constraint;
import org.integratedmodelling.thinklab.exception.ThinklabConstraintValidationException;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.impl.protege.Ontology.ReferenceRecord;
import org.integratedmodelling.thinklab.interfaces.IConcept;
import org.integratedmodelling.thinklab.interfaces.IInstance;
import org.integratedmodelling.thinklab.interfaces.IInstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.IInstanceImplementationConstructor;
import org.integratedmodelling.thinklab.interfaces.IOntology;
import org.integratedmodelling.thinklab.interfaces.IProperty;
import org.integratedmodelling.thinklab.interfaces.IRelationship;
import org.integratedmodelling.thinklab.interfaces.IValue;
import org.integratedmodelling.thinklab.value.BooleanValue;
import org.integratedmodelling.thinklab.value.NumberValue;
import org.integratedmodelling.thinklab.value.ObjectReferenceValue;
import org.integratedmodelling.thinklab.value.TextValue;
import org.integratedmodelling.thinklab.value.Value;
import org.integratedmodelling.utils.LogicalConnector;
import org.integratedmodelling.utils.MalformedListException;
import org.integratedmodelling.utils.Pair;
import org.integratedmodelling.utils.Polylist;

import edu.stanford.smi.protegex.owl.model.OWLDatatypeProperty;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLObjectProperty;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.RDFResource;
import edu.stanford.smi.protegex.owl.model.RDFSLiteral;

/**
 * Manages all aspects of the translation between the OWL model and the Thinklab extensions. 
 * Ontology can have one of these so it does not need to worry internally.
 * 
 * @author Ferdinando Villa
 * @author Ioannis N. Athanasiadis
 * TODO check if we can should put this at a higher level with abstract methods, or make it an
 * interface that Ontology can implement.
 */
public class ThinklabOWLManager {
	
	static final String EMPTY_STRING = "";
	static Logger log = Logger.getLogger(ThinklabOWLManager.class);
	
	/**
	 * Reified literals are born as individuals in the original ontologies, so we can cache with the ID
	 * of the original individual. This makes it easy to create and destroy relationships without
	 * creating literals more than once, and allows us to modify them and write their values
	 * back as annotations. We cache the IValues here.
     * 
     * This one serves also to hold class literals.
	 */
	private Hashtable<String, IValue> reifiedLiterals = new Hashtable<String, IValue>();
	private Hashtable<String, IInstance> classLiterals = new Hashtable<String, IInstance>();

	// we need a HashMap here because we need nulls in it, but we need to make sure we
	// synchronize.
	private HashMap<String, IInstanceImplementation> instanceImplementations = 
		new HashMap<String, IInstanceImplementation>();
		
	// same for cached thinklab constraints
	private HashMap<String, Constraint> thinklabConstraints = 
		new HashMap<String, Constraint>();
	
	static ThinklabOWLManager owlManager;

	public static RDFProperty abstractAnnotationProperty;
	public static RDFProperty classLiteralAnnotationProperty;
	public static RDFProperty extendedLiteralAnnotationProperty;

	
	public static ThinklabOWLManager get() throws ThinklabException {

		if (owlManager == null) {
			
			owlManager = new ThinklabOWLManager();

			/* TODO set properties */
			abstractAnnotationProperty = 
				((Property)KnowledgeManager.get().getAbstractProperty()).property;
			classLiteralAnnotationProperty = 
				((Property)KnowledgeManager.get().getClassificationProperty()).property;
			extendedLiteralAnnotationProperty = 
				((Property)KnowledgeManager.get().getReifiedLiteralProperty()).property;
			
		}
		return owlManager;
	}
	
	static public int getNRelationships(RDFResource cl, RDFProperty p) {
		
        if (!(p instanceof OWLObjectProperty || p instanceof OWLDatatypeProperty)) {
        	return 0;
        }

        return cl.getPropertyValueCount(p);
	}
	

	/**
	 * The workhorse of translation from OWL to Thinklab: basically it needs to
	 * recognize properties and/or property values that Thinklab handles in
	 * specialized ways, and create the proper relationship value, populating
	 * the cache as necessary. Any object property that is recognized as
	 * "special" leads us to ignore the object and produce a relationship with a
	 * class or literal IValue. The others produce either a "standard" literal
	 * or a standard object value.
	 * 
	 * When the value has been generated by translating an OWL individual, it retains
	 * the ID of the individual so we can track modifications and write them back to
	 * the OWL model when saved.
	 * 
	 * @param cl
	 * @param p
	 * @return
	 */
	Collection<IValue> translateRelationship(RDFResource cl, RDFProperty p)
            throws ThinklabException {

        ArrayList<IValue> ret = new ArrayList<IValue>();
        boolean isDataProperty = false;
        
        if (p instanceof OWLDatatypeProperty)
            isDataProperty = true;
        else if (!(p instanceof OWLObjectProperty)) {
        	// just return anything else with no error
        	return ret;
        }

        for (Iterator<?> it = cl.getPropertyValues(p).iterator(); it.hasNext();) {
        	
            Object o = it.next();

            /*
             * get property type. If Datatype, we must translate to a standard
             * IValue that retains no ID.
             * FIXME we will need to put that away, too, if we want changes to reflect
             * to the OWL model.
             */
            if (isDataProperty) {

                // cross fingers
                IValue val = Value.getValueForObject(o);
                /* add to return values */
                ret.add(val);

            } else {
            	
                /* it's an object property. Get the object */
                if (!(o instanceof OWLIndividual))
                    throw new ThinklabException(
                            "object property "
                                    + new Property(p) + 
                                    " in " + 
                                    cl.getLocalName() 
                                    + " does not link to an individual. OWL full is not supported.");

                OWLIndividual ind = (OWLIndividual) o;

                /* if we have cached this, just return it */
                IValue val = reifiedLiterals.get(ind.getURI().toString());

                if (val != null)
                    ret.add(val);
                else {

                    /* get annotation for class literal, if any */
                	if (new Property(p).isClassification()) {
                		
                		Instance cin = new Instance(ind);
                		
                        /*
                         * classAnnotation must be the semantic type or URL of a
                         * concept known to the KM
                         */
                        val = new Value(cin.getDirectType());

                        /* retain ID in value */
                        val.setID(ind.getURI().toString());

                        /* cache value */
                        reifiedLiterals.put(val.getID(), val);

                        /* remember instance created for this type */
                        classLiterals.put(val.getConcept().toString(), cin);
                        
                        /* add to return collection */
                        ret.add(val);

                    } else {

                        String literAnnotation = 
                        	getAnnotationAsString(ind, extendedLiteralAnnotationProperty, null);
                        
                        if (literAnnotation != null) {

                            /*
                             * figure out how to construct literal using the
                             * concept manager closest to the object's class
                             */
                            IConcept cc = new Instance(ind).getDirectType();

                            val = KnowledgeManager.get().validateLiteral(cc, literAnnotation, null);

                            /* retain ID */
                            val.setID(ind.getURI().toString());

                            /* cache value */
                            reifiedLiterals.put(val.getID(), val);

                            /* return */
                            ret.add(val);
                        } else {
                        	
                        	/* it's just a stupid object property */
                        	val = new ObjectReferenceValue(new Instance(ind));
                        	ret.add(val);
                        	
                        }
                    }
                }
            }
        }
        
        return ret;
    }
	
	private synchronized void addImpl(String uri, IInstanceImplementation impl) {
		instanceImplementations.put(uri, impl);
	}

	/**
	 * Obtain the instance implementation for given instance, using cached one if present.
	 * @param instance
	 * @return
	 * @throws ThinklabException
	 */
	public IInstanceImplementation getInstanceImplementation(Instance instance) throws ThinklabException {

		IInstanceImplementation ret = null;
		
		// check if this uri passed here before
		if (!instanceImplementations.containsKey(instance.getURI())) {

			IInstanceImplementationConstructor cm = KnowledgeManager.get().getInstanceConstructor(instance.getDirectType());
			
			if (cm != null) {
				ret = cm.construct(instance);
			}
			
			/*
			 * use a synchronized function because this is a singleton and hashmap isn't
			 * synchronized.
			 */
			addImpl(instance.getURI(), ret);

			if (ret != null)
				ret.initialize(instance);
		
		} else {
			ret = instanceImplementations.get(instance.getURI()); 
		}
		return ret;
	}
	
	public void setInstanceImplementation(Instance instance, IInstanceImplementation impl) {
		addImpl(instance.getURI(), impl);
	}
	
	/**
	 * A universal method for returning comments from RDF resources.
	 * Note that there can be more than one comments attached to a resource.
	 * Also we assume that the default language will be english. If a no languageCode 
	 * is given, then all comments with English ("en") and "null" languageCodes are returned.
	 *   
	 * @param resource
	 * @param languageCode
	 * @return
	 */
	public static String getComment(RDFResource resource, String languageCode) {

		if (resource == null || resource.getComments() == null)
			return EMPTY_STRING;
		
		Iterator<?> values = resource.getComments().iterator();
		ArrayList<String> rvals = new ArrayList<String>(); // the values to return
		while (values.hasNext()) {
			Object v = values.next();
			if (v instanceof String)
				rvals.add((String) v);
			else { 
				if(v instanceof RDFSLiteral){
					RDFSLiteral l = (RDFSLiteral) v;
				    if (languageCode == null && (l.getLanguage() == null || l.getLanguage().equals("en")) 
				    		|| l.getLanguage().equals(languageCode))
				    	rvals.add(l.getString());
				} else
					log.warn("Unknown comment value for " +resource.getURI());
				}
		}
		return rvals.isEmpty()? EMPTY_STRING : 
			(rvals.size() == 1 ? rvals.get(0) : rvals.toString());
	}

	public static String getLabel(RDFResource resource, String languageCode) {

		if (resource == null || resource.getLabels() == null)
			return EMPTY_STRING;
		
		Iterator<?> values = resource.getLabels().iterator();
		ArrayList<String> rvals = new ArrayList<String>(); // the values to return
		while (values.hasNext()) {
			Object v = values.next();
			if (v instanceof String)
				rvals.add((String) v);
			else { 
				if(v instanceof RDFSLiteral){
					RDFSLiteral l = (RDFSLiteral) v;
				    if (languageCode == null && (l.getLanguage() == null || l.getLanguage().equals("en")) 
				    		|| l.getLanguage().equals(languageCode))
				    	rvals.add(l.getString());
				} else
					log.warn("Unknown comment value for " +resource.getURI());
				}
		}
		return rvals.isEmpty()? EMPTY_STRING :
			(rvals.size() == 1 ? rvals.get(0) : rvals.toString());

	}

	/**
	 * Return value of passed annotation property or default if not found.
	 * @param resource resource to check	
	 * @param annotation property to look for
	 * @param defvalue default value to return if no property or no annotation
	 * @return the annotation as a String
	 */
	public static String getAnnotationAsString(RDFResource resource,
											   RDFProperty annotation,
											   String defvalue) 
	{
		String ret = defvalue;
		if (annotation != null) {
			RDFSLiteral  s = resource.getPropertyValueLiteral(annotation);
			if (s != null)
				ret = s.getString();
		}
		return ret;
	}
	
	/** 
	 * Return whether the passed resource has an annotation specifying that it is 
	 * abstract. 
	 * @param concept the resource to check
	 * @param annotation the RDFProperty that specifies abstract status
	 * @param b the default to specify if there is no annotation
	 * @return true if the annotation contains a valid truth value expressed as text
	 * @see BooleanValue.parseBoolean
	 */
	public static boolean getAnnotationAsBoolean(RDFResource resource, 
												 RDFProperty annotation, 
												 boolean b) 
	
	{
		boolean ret = b;
		
		if (annotation != null) {
			RDFSLiteral  s = resource.getPropertyValueLiteral(annotation);
			if (s != null)
				ret = BooleanValue.parseBoolean(s.getString());
		}
		return ret;
	}

	public static Pair<IConcept, String> getConceptFromListObject(Object o) throws ThinklabException {
		
		IConcept concept = null;
		String ID = null;
		
		if (o instanceof IConcept) 
			concept = (IConcept)o;
		else if (o instanceof String) {
			
			String co = (String)o;
			
			if (co.contains("#")) {
				String[] cco = co.split("#");
				co = cco[0];
				ID = cco[1];
			}
			
			try {
				concept = KnowledgeManager.get().requireConcept(co);
			} catch (ThinklabException e) {
				throw new ThinklabValidationException(e);
			}
			
		} else {
			throw new ThinklabValidationException("invalid concept " + o + " in instance list");
		}
		
		
		return new Pair<IConcept, String>(concept, ID);
	}
	
	/* FIXME there must be something wrong here, or maybe not. In that case, don't FIXME. */
	public Instance getClassLiteralInstance(IConcept concept) throws ThinklabException {
		
		IInstance ret = classLiterals.get(concept.toString());
		
		if (ret == null) {
		
			IOntology ont =
				KnowledgeManager.get().getKnowledgeRepository().requireOntology(concept.getConceptSpace());
			
			ret = ont.createInstance(null, concept);
		
			((Instance)ret).instance.addPropertyValue(classLiteralAnnotationProperty, concept.toString());
			
			/* remember instance created for this type */
			classLiterals.put(concept.toString(), ret);

		}
		
		return (Instance)ret;
		
	}
	
	/* FIXME there must be something wrong here, or maybe not. In that case, FIXME but don't FIXIT. */
	public Instance getExtendedLiteralInstance(String id, IValue literal, IOntology ont) throws ThinklabException {
	
		Instance ret = null;
		IValue io    = null;
		
		if (id == null)
			id = literal.getID();
		
		if (id != null)	
			io = reifiedLiterals.get(id);
	
		if (io == null) {
			
			/* create instance */
			IInstance rr = (Instance)ont.createInstance(id, literal.getConcept());
			
			ret = (Instance)rr;
			
			/* set literal value */
			ret.instance.addPropertyValue(extendedLiteralAnnotationProperty, literal.toString());

			reifiedLiterals.put(rr.getURI(), literal);
			
		} else {
			
			if (!(literal.toString().equals(io.toString())))
				throw new ThinklabValidationException("internal: incompatible literals being stored with same ID");
		}
		
		/* 
		 * 
		 * 
		 * - check ID in literal if passed ID is null; create if necessary.
		 * - check if literal is there, if not create it. If it's there
		 *   it must have that value.
		 * - Set ID into literal
		 */

		
		return ret;
	}

	
	public void interpretPropertyList (Polylist l, Ontology ont, IInstance inst, Collection<ReferenceRecord> reftable) throws ThinklabException {

		KnowledgeTree ctr = KnowledgeManager.getClassTree();
		
		/*
		 * List must contain exactly two elements
		 */
		if (l.length() != 2)
			throw new ThinklabValidationException("property list is invalid: " + l);
		
		/*
		 * first element must be a property or a semantic type corresponding to
		 * one
		 */
		IProperty property = null;
		
		Object o1 = l.first();
		if (o1 instanceof IProperty) 
			property = (IProperty)o1;
		else if (o1 instanceof String) {
			try {
				
				/* filter out comment and label without looking for the annotation */
				if (o1.toString().equals("rdfs:comment")) {
					
					inst.addDescription(l.second().toString());
					return;
					
				} else if (o1.toString().equals("rdfs:label")) {
					
					inst.addLabel(l.second().toString());
					return;
				} else if (o1.toString().equals("#")) {
					inst.setImplementation((IInstanceImplementation) l.second());
				}
				
				property = KnowledgeManager.get().requireProperty((String)o1);
				
			} catch (ThinklabException e) {
				throw new ThinklabValidationException(e);
			}
		} else {
			throw new ThinklabValidationException("invalid property " + o1 + " in property list");
		}
		
		/*
		 * second element can either be a list, or something that evaluates to a
		 * valid string - a String, Concept, or IValue
		 */
		Object o2 = l.second();
		
		if (o2 instanceof Polylist) {
			
			Polylist lvalue = (Polylist)o2;
			
			/* 
			 * must have at least one element or we don't know what to do
			 */
			if (lvalue.length() < 1)
				throw new ThinklabValidationException("list defining property value for " + property + " has no elements");
			

			if (lvalue.length() == 1 && lvalue.first().toString().startsWith("#")) {
				/* reference - just add to table and return */
				reftable.add(
						ont.new ReferenceRecord(inst, 
								property, 
								lvalue.first().toString().substring(1)));
				return;
			}
			
			if (lvalue.length() < 2 || 
				(lvalue.length() >= 2 && lvalue.second() instanceof Polylist)) {

				/* 
				 * it's an object definition: create the object and set it as value. 
				 */
				IInstance instance = ont.createInstanceInternal(lvalue, reftable);
				inst.addObjectRelationship(property, instance);

			} else {
				
				/*
				 * literal with no explicit type. First value must identify a concept, possibly with ID attached
				 */
				Pair<IConcept, String> cid = getConceptFromListObject(lvalue.first());
				
				/*
				 * second element must be a string or an IValue
				 */
				Object second = lvalue.second();
				
				if (!(second instanceof String || second instanceof IValue)) {
					throw new ThinklabValidationException("invalid literal specification in list: " + second);
				}
				
				String svalue = second.toString();
				
				/*
				 * second element must be last one
				 */
				if (lvalue.length() != 2)
					throw new ThinklabValidationException("literal list must have two elements");
				
				log.debug("validating \"" + svalue + "\" as a " + cid.getFirst() + " literal for " + property);
				
				/* 
				 * must be a string value for the extended literal, and the first 
				 * value must be its concept.
				 */
				IValue value = KnowledgeManager.get().validateLiteral(cid.getFirst(), svalue, ont);
				
				/*
				 * If the validator creates an object, we set this as an object reference and the property must
				 * be an object property.
				 */
				if (value.isObjectReference()) {
					inst.addObjectRelationship(property, value.asObjectReference().getObject());
				} else {
					inst.addLiteralRelationship(property, value);
				}
			}
			
			
		} else if (o2 instanceof IInstance) {
			
			/*
			 * a direct instance was stuck in the list - why not.
			 */
			inst.addObjectRelationship(property, (IInstance)o2);
			
		} else {
			
			/* second argument is not a list */
			String svalue = o2.toString();
			
			boolean canTestRange = false;
			
			if (((Property)property).isDataProperty()) {

				/* 
				 * Must be POD type: store as data property value according to range.
				 */
				Object toAdd = null;

				if (!(o2 instanceof IValue)) {
				
					Collection<IConcept> range = property.getRange();

					canTestRange = range.size() > 0;
										
					/* stop at the first concept in range that validates the object. */
					for (IConcept c : range) {
						
						String cst = c.getSemanticType().toString();
						
						if (
								(ctr.is(cst, KnowledgeManager.TextType()) && o2 instanceof String) ||
								(ctr.is(cst, KnowledgeManager.DoubleType()) && 
										(o2 instanceof Double || o2 instanceof Float)) ||
								(ctr.is(cst, KnowledgeManager.FloatType()) && 
										(o2 instanceof Float || o2 instanceof Double)) ||
								(ctr.is(cst, KnowledgeManager.IntegerType()) && o2 instanceof Integer) ||
								(ctr.is(cst, KnowledgeManager.LongType()) && o2 instanceof Long) ||
								(ctr.is(cst, KnowledgeManager.BooleanType()) && o2 instanceof Boolean) 
							) 
						{
							toAdd = o2;
							break;
						}
					}
					
					if (toAdd == null) {

						/* 
						 * if nothing works directly, try converting string representation to the first type in range
						 * that doesn't complain. 
						 */
						String so2 = o2.toString();
						
						for (IConcept c : range) {

							String cst = c.getSemanticType().toString();

							/*
							 * FIXME there's a lot more XSD types to support. Would be good to
							 * use functions in protege' for this, or anywhere else, rather than
							 * trying them all out. 
							 */
							if (ctr.is(cst, KnowledgeManager.TextType())) {
							
								/* this should catch URIs etc for now */
								toAdd = so2;
								
							} else 	if (ctr.is(cst, KnowledgeManager.DoubleType())) {

								// FIXME actually it's FIXIT, protege wants a float or it will crash
								Float d = null;
								try {
									d = new Float(Float.parseFloat(so2));
								} catch (NumberFormatException e) {
								}
								if (d != null)
									toAdd = d;
								
							} else 	if (ctr.is(cst, KnowledgeManager.IntegerType())) {

								Integer d = null;
								try {
									d = new Integer(Integer.parseInt(so2));
								} catch (NumberFormatException e) {
								}
								if (d != null)
									toAdd = d;
								
							} else 	if (ctr.is(cst, KnowledgeManager.FloatType())) {

								Float d = null;
								try {
									d = new Float(Float.parseFloat(so2));
								} catch (NumberFormatException e) {
								}
								if (d != null)
									toAdd = d;
								
							} else 	if (ctr.is(cst, KnowledgeManager.LongType())) {

								Long d = null;
								try {
									d = new Long(Long.parseLong(so2));
								} catch (NumberFormatException e) {
								}
								if (d != null)
									toAdd = d;
								
							} else 	if (ctr.is(cst, KnowledgeManager.BooleanType())) {

								Boolean d = null;
								try {
									d = new Boolean(Boolean.parseBoolean(so2));
								} catch (NumberFormatException e) {
								}
								if (d != null)
									toAdd = d;
							}
							
							if (toAdd != null)
								break;
						}
						
					}
					
					
				} else {
					
					/*
					 * must be POD type; get POD type as object
					 */
					IValue ivalue = (IValue)o2;
					
					if (!ivalue.isPODType()) {
						throw new ThinklabValidationException("property list tries to assign extended literal " +
							o2 + 
							" to plain data property");
					}
					
					if (ivalue.isText()) {
						toAdd = ivalue.toString();
					} else if (ivalue.isBoolean()) {
						toAdd = new Boolean(((BooleanValue)ivalue).value);
					} else if (ivalue.isNumber()) {

						String cst = ivalue.getConcept().getSemanticType().toString();

						
						if (ctr.is(cst, KnowledgeManager.DoubleType())) {
							// FIXME actually FIXIT, protege wants a Float here, a Double will crash it
							toAdd = new Float(((NumberValue)ivalue).asFloat());
						} else if (ctr.is(cst, KnowledgeManager.FloatType())) {
							toAdd = new Float(((NumberValue)ivalue).asFloat());
						} else if (ctr.is(cst, KnowledgeManager.LongType())) {
							toAdd = new Long(((NumberValue)ivalue).asLong());
						} else if (ctr.is(cst, KnowledgeManager.IntegerType())) {
							toAdd = new Integer(((NumberValue)ivalue).asInteger());
						}	
					} else {
						throw new ThinklabValidationException("internal: POD type not recognized for " + ivalue);
					}
				}
				
				/*
				 * We only complain if the property has a range and we couldn't find a
				 * match. If property has no range, we take whatever we get and cross
				 * fingers.
				 */
				if (toAdd == null) {
					if (canTestRange)
						throw new ThinklabValidationException("plain data property can't use value " + o2);
					else
						toAdd = o2;
				}
				
				inst.addLiteralRelationship(property, toAdd);
				
				
			} else if (svalue.startsWith("#")) {

				/* 
				 * reference: lookup referenced object and complain if it's not there. Use KM if it's a full
				 * semantic type. It must be in the same ontology we're adding to, as it must reference something
				 * that was just built.
				 */
				IInstance instance = ont.getInstance(svalue.substring(1));
				
				if (instance == null) {
					throw new ThinklabValidationException("named reference " + svalue + " not found in knowledge base");
				}

				inst.addObjectRelationship(property, instance);
				
			} else if (SemanticType.validate(svalue)) {				
				
				/*
				 * class literal
				 */
				IConcept concept = getConceptFromListObject(o2).getFirst();
				inst.addClassificationRelationship(property, concept);

			} else {
				
				/* if we get here, we have a string value for an extended literal that validates to a 
				 * non-POD type and is linked through an object property. In that case, the range must be
				 * unambiguous, and we try to validate using that.
				 */
				Collection<IConcept> range = property.getRange();
				if (range.size() != 1) {
					throw new ThinklabValidationException("can't determine range of property " + property + 
									" to validate literal \"" + o2 + "\"");
				}
				
				IConcept r = range.iterator().next();
				IValue val = KnowledgeManager.get().validateLiteral(r, o2.toString(), ont);
				
				if (val != null) {
					if (val.isObjectReference()) {
						inst.addObjectRelationship(property, val.asObjectReference().getObject());
					} else {
						inst.addLiteralRelationship(property, val);
					}
				}
			}
		}
	}
	

	/**
	 * TODO
	 * Write changes back to ontology - need to be on an ontology scope, so probably have one
	 * manager per ontology, or keep separate hashes.
	 * @throws ThinklabException 
	 */
	public void writeLiterals(IOntology ontology) throws ThinklabException {
			
		for (IInstance inst : ontology.getInstances()) {
			
			for (IRelationship rel : inst.getRelationships()) {
				
				if (rel.isLiteral()) {
										
					if ((Property)rel.getProperty() instanceof OWLDatatypeProperty) {
						
					} else {
						
					}
				}
			}
		}
		
	}

	
	/** 
	 * Return a list of all constraints embedded as additional restrictions in the
	 * given class. Cache the created constraints for efficiency.
	 * @param c
	 * @return
	 * @throws ThinklabException 
	 */
	public synchronized Constraint getAdditionalConstraints(IConcept c) throws ThinklabException {
		
		Constraint ret = null;

		if (thinklabConstraints.containsKey(c.toString()))
			return thinklabConstraints.get(c.toString());
		
		ArrayList<Constraint> rlist = new ArrayList<Constraint>();
		
		getAdditionalConstraintsInternal(c, rlist);

		if (rlist.size() == 1) {
			ret = rlist.get(0);
		} else if (rlist.size() > 1) {
			ret = rlist.get(0);
			for (int i = 1; i < rlist.size(); i++)
				ret.merge(rlist.get(i), LogicalConnector.INTERSECTION);
		}
		
		thinklabConstraints.put(c.toString(), ret);
		
		return ret;
		
	}

	private static void getAdditionalConstraintsInternal(IConcept c, ArrayList<Constraint> ret) throws ThinklabException {

		String cn = 
			getAnnotationAsString(
					((Concept)c).concept, 
					((Property)KnowledgeManager.get().getAdditionalRestrictionProperty()).property, 
					null);
		
		if (cn != null)
			try {
				ret.add(new Constraint(Polylist.parse(cn)));
			} catch (MalformedListException e) {
				throw new ThinklabConstraintValidationException(
						"invalid constraint \"" +
						cn +
						"\" specified in ontology for class " + 
						c);
			}
	}
	
	/*
	 * We are passed an IValue but we need to set an OWL dataproperty from it. Return the 
	 * POD object that matches the type, or throw an exception if no POD type does.
	 */
	public Object translateIValueToDatatype(IValue value) throws ThinklabValidationException {

		if (value.isText()) {
			return ((TextValue)value).value;
		} else if (value.isNumber()) {
			return ((NumberValue)value).getPODValue();
		} else if (value.isBoolean()) {
			return ((BooleanValue)value).truthValue();
		}
		
		throw new ThinklabValidationException("internal: non-POD value being assigned to data property");
		
	}

	public void removeInstanceData(String uri) {
		
		if (instanceImplementations.containsKey(uri))
			instanceImplementations.remove(uri);
		if (reifiedLiterals.containsKey(uri)) {
			reifiedLiterals.remove(uri);
		}
		
	}
	
	public boolean isReifiedLiteral(String uri) {
		return reifiedLiterals.containsKey(uri);
	}
	
}
