/**
 * KnowledgeManager.java
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
package org.integratedmodelling.thinklab;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;

import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.command.CommandDeclaration;
import org.integratedmodelling.thinklab.command.CommandManager;
import org.integratedmodelling.thinklab.configuration.LocalConfiguration;
import org.integratedmodelling.thinklab.constraint.Constraint;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabMalformedSemanticTypeException;
import org.integratedmodelling.thinklab.exception.ThinklabMissingResourceException;
import org.integratedmodelling.thinklab.exception.ThinklabNoKMException;
import org.integratedmodelling.thinklab.exception.ThinklabPluginException;
import org.integratedmodelling.thinklab.exception.ThinklabResourceNotFoundException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.extensions.InstanceImplementationConstructor;
import org.integratedmodelling.thinklab.extensions.KnowledgeLoader;
import org.integratedmodelling.thinklab.extensions.LiteralValidator;
import org.integratedmodelling.thinklab.interfaces.IConcept;
import org.integratedmodelling.thinklab.interfaces.IInstance;
import org.integratedmodelling.thinklab.interfaces.IKBox;
import org.integratedmodelling.thinklab.interfaces.IKnowledgeProvider;
import org.integratedmodelling.thinklab.interfaces.IKnowledgeRepository;
import org.integratedmodelling.thinklab.interfaces.IKnowledgeSubject;
import org.integratedmodelling.thinklab.interfaces.IOntology;
import org.integratedmodelling.thinklab.interfaces.IProperty;
import org.integratedmodelling.thinklab.interfaces.ISession;
import org.integratedmodelling.thinklab.interfaces.ISessionManager;
import org.integratedmodelling.thinklab.interfaces.IThinklabSessionListener;
import org.integratedmodelling.thinklab.interfaces.IValue;
import org.integratedmodelling.thinklab.kbox.KBoxManager;
import org.integratedmodelling.thinklab.session.SingleSessionManager;
import org.integratedmodelling.utils.MiscUtilities;
import org.integratedmodelling.utils.Polylist;
import org.java.plugin.PluginManager;
import org.semanticweb.owl.vocab.XSDVocabulary;

/**
 * <p>The Knowledge Manager is the main actor in Thinklab. It gives API access to a knowledge repository and to all the
 * Thinklab extended functionalities. It can also load a generalized interface object, which enables 
 * session management and control of the KM according to its implementation.</p>
 * 
 * <p>The Knowledge Manager supports a command abstraction that allows a "user reaction" pattern to be implemented independent from
 * the actual interface, which can be a command line as well as a servlet or SOAP server. Commands are registered with the
 * knowledge manager by passing their declarations. These are exposed to the chosen KnowledgeInterface, which implements its way
 * to create commands based on interface events.</p>
 * 
 * <p>In order to be created, a KM must be passed implementations of a knowledge repository and a knowledge interface. A dummy 
 * knowledge interface is provided with the core package if the KM is only expected to be driven through the API.</p>
 *	
 * <p>The knowledge manager gives access to concepts, individuals and properties, but (intentionally)
 * not to ontologies. This is because the ontology is not relevant to its users, and we may want to
 * support a more sophisticated notion of "concept space" when we come to it. Ontologies pertain to
 * the physical model of knowledge, and they're accessible through the Knowledge Repository.</p>
 *
 * @author Ferdinando Villa
 * @author Ioannis N. Athanasiadis
 * @see ISessionManager
 * @see IKnowledgeRepository
 * @see CommandDeclaration
 * @see Command
 */
public class KnowledgeManager implements IKnowledgeProvider {

    /** default core ontology URL. It really is small - just some POD data types and a couple properties. */
	private static final String DEFAULT_CORE_ONTOLOGY = "thinklab-core.owl";

    /** 
	 * <p>The Knowledge Manager is a singleton. This is created by the initializer and an exception 
	 * is thrown if a second one has been initialized. Note that you can create as many as you
	 * want, but you can only initialize one. DANGER: professional driver on closed circuit. Do not attempt.</p>
     * 
     * <p>Note that this may change in the future. Having multiple kms is interesting for sophisticated
     * applications (e.g. cross-reasoning) but that's for another time.</p>
     * 
	 */
	public static KnowledgeManager KM = null;
	
	private IConcept integerType;
	private IConcept floatType;	
	private IConcept textType;
    private IConcept numberType;
    private IConcept booleanType;
    private IConcept longType;
    private IConcept doubleType;

    private IProperty classificationProperty;
    private IProperty reifiedLiteralProperty;
    private IProperty abstractProperty;
	private IProperty additionalRestrictionsProperty;
    
    private SemanticType integerTypeID;
    private SemanticType floatTypeID;    
    private SemanticType textTypeID;
    private SemanticType numberTypeID;
    private SemanticType booleanTypeID;
    private SemanticType doubleTypeID;
    private SemanticType longTypeID;
    private SemanticType rootTypeID;
    private SemanticType classificationPropertyID;
    private SemanticType reifiedLiteralPropertyID;
    private SemanticType additionalRestrictionsPropertyID;
	private SemanticType abstractPropertyID;
	
	protected IKnowledgeRepository knowledgeRepository;
	protected ISessionManager  sessionManager = null;

	protected PluginManager pluginManager = null;
	
	protected KBoxManager kboxManager;
	protected CommandManager commandManager;
	
	public static final String EXCLUDE_ONTOLOGY_PROPERTY = "thinklab.ontology.exclude";
	
	// this one in any config file will add properties to the blacklist.
	public static final String IGNORE_PROPERTY_PROPERTY = "thinklab.property.ignore";

	public static final String IGNORE_CONCEPT_PROPERTY = "thinklab.concept.ignore";
	
	/*
	 * map URIs to concept space names 
	 */
	HashMap<String, String> uri2cs = new HashMap<String, String>();
	
	/*
	 * map concept space names to URIs 
	 */
	HashMap<String, String> cs2uri = new HashMap<String, String>();


    /*
     * true when thinklab extended types have been initialized.
     */
	private boolean typesInitialized = false;

	private HashMap<String, InstanceImplementationConstructor> instanceConstructors =
		new HashMap<String, InstanceImplementationConstructor>();

	private HashMap<String, LiteralValidator> literalValidators =
		new HashMap<String, LiteralValidator>();

	private HashMap<String, KnowledgeLoader> knowledgeLoaders =
		new HashMap<String, KnowledgeLoader>();

	private HashMap<String, Class<?>> sessionListeners = 
		new HashMap<String, Class<?>>();

	private HashSet<String> propertyBlacklist = new HashSet<String>();
	private HashSet<String> conceptBlacklist = new HashSet<String>();

	/*
	 * maps XSD URIs to thinklab types for translation of literals.
	 */
	private Hashtable<String, String> xsdMappings = new Hashtable<String, String>();

	public KnowledgeManager(IKnowledgeRepository kr, ISessionManager ki) {

        /* set KM */
        KM = this;
        
        /* create stuff */
        knowledgeRepository = kr;
		sessionManager  = ki;
	}
	
	/**
	 * If the km is only meant to be used within an application, use this one.
	 * @param fileKnowledgeRepository
	 * @throws ThinklabIOException
	 */
	public KnowledgeManager(IKnowledgeRepository knowledgeRepository) throws ThinklabException {
		this(knowledgeRepository, new SingleSessionManager());
	}

	/**
	 * This should become the default constructor: the class of knowledge repository and session
	 * manager is stated in the properties, defaulting to the ones we trust.
	 * 
	 * @param fileKnowledgeRepository
	 * @throws ThinklabIOException
	 */
	public KnowledgeManager() throws ThinklabException {
		
		KM = this;
		
		String smClass = 
			Thinklab.get().getProperties().getProperty(
				"thinklab.sessionmanager.class",
				"org.integratedmodelling.thinklab.session.SingleSessionManager");

		String krClass = 
			Thinklab.get().getProperties().getProperty(
				"thinklab.repository.class",
				"org.integratedmodelling.thinklab.owlapi.FileKnowledgeRepository");
		
		IKnowledgeRepository kr = null;
		ISessionManager sm = null;
		
		Class<?> cls = null;
		try {

			cls = Thinklab.get().getClassLoader().loadClass(smClass);
			sm = (ISessionManager) cls.newInstance();

			cls = Thinklab.get().getClassLoader().loadClass(krClass);
			kr =  (IKnowledgeRepository) cls.newInstance();

		} catch (Exception e) {
			throw new ThinklabValidationException(e);
		}
		
		knowledgeRepository = kr;
		sessionManager = sm;
	}

	
	public IKnowledgeRepository getKnowledgeRepository() {
		return knowledgeRepository;
	}
	
	public ISessionManager getSessionManager() {
		return sessionManager;
	}
	
    /* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.IKnowledgeBase#getRootConcept()
	 */
    public IConcept getRootConcept() {
        return knowledgeRepository.getRootConceptType();
    }
    
	public IProperty getAbstractProperty() throws ThinklabValidationException {
		if (!typesInitialized)
			initializeThinklabTypes();
		return abstractProperty;
	}

	public void setAbstractProperty(IProperty abstractProperty) {
		this.abstractProperty = abstractProperty;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.IKnowledgeBase#getBooleanType()
	 */
	public IConcept getBooleanType() throws ThinklabValidationException {
		if (!typesInitialized)
			initializeThinklabTypes();
		return booleanType;
	}

	public void setBooleanType(IConcept booleanType) {
		this.booleanType = booleanType;
	}

	public IProperty getClassificationProperty() throws ThinklabValidationException {
		if (!typesInitialized)
			initializeThinklabTypes();
		return classificationProperty;
	}

	public void setClassificationProperty(IProperty classificationProperty) {
		this.classificationProperty = classificationProperty;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.IKnowledgeBase#getDoubleType()
	 */
	public IConcept getDoubleType() throws ThinklabValidationException {
		if (!typesInitialized)
			initializeThinklabTypes();
		return doubleType;
	}

	public void setDoubleType(IConcept doubleType) {
		this.doubleType = doubleType;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.IKnowledgeBase#getFloatType()
	 */
	public IConcept getFloatType() throws ThinklabValidationException {
		if (!typesInitialized)
			initializeThinklabTypes();
		return floatType;
	}

	public void setFloatType(IConcept floatType) {
		this.floatType = floatType;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.IKnowledgeBase#getIntegerType()
	 */
	public IConcept getIntegerType() throws ThinklabValidationException {
		if (!typesInitialized)
			initializeThinklabTypes();
		return integerType;
	}

	public void setIntegerType(IConcept integerType) {
		this.integerType = integerType;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.IKnowledgeBase#getLongType()
	 */
	public IConcept getLongType() throws ThinklabValidationException {
		if (!typesInitialized)
			initializeThinklabTypes();
		return longType;
	}

	public void setLongType(IConcept longType) {
		this.longType = longType;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.IKnowledgeBase#getNumberType()
	 */
	public IConcept getNumberType() throws ThinklabValidationException {
		if (!typesInitialized)
			initializeThinklabTypes();
		return numberType;
	}

	public void setNumberType(IConcept numberType) {
		this.numberType = numberType;
	}

	public IProperty getReifiedLiteralProperty() throws ThinklabValidationException {
		if (!typesInitialized)
			initializeThinklabTypes();
		return reifiedLiteralProperty;
	}

	public void setReifiedLiteralProperty(IProperty reifiedLiteralProperty) {
		this.reifiedLiteralProperty = reifiedLiteralProperty;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.IKnowledgeBase#getTextType()
	 */
	public IConcept getTextType() throws ThinklabValidationException {
		if (!typesInitialized)
			initializeThinklabTypes();
		return textType;
	}

	public void setTextType(IConcept textType) {
		this.textType = textType;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.IKnowledgeBase#getURIFromConceptSpace(java.lang.String)
	 */
	public String getURIFromConceptSpace(String cs) throws ThinklabResourceNotFoundException {
		return cs2uri.get(cs);
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.IKnowledgeBase#getConceptSpaceFromURI(java.lang.String)
	 */
	public String getConceptSpaceFromURI(String cs) throws ThinklabResourceNotFoundException {
		return uri2cs.get(cs);
	}
	
	
	/**
	 * If a mapping between the URI of an XSD type and a thinklab semantic type has been
	 * defined, return the correspondent type; otherwise return null.
	 * 
	 * @param XSDUri
	 * @return
	 */
	public String getXSDMapping(String XSDUri) {
		return xsdMappings.get(XSDUri);
	}

	private void initializeThinklabTypes() throws ThinklabValidationException {

		Properties p = LocalConfiguration.getProperties();
	    
	    // TODO all this stuff should be left to a specified plugin if installed, and done this way
	    // only as a last resort. Also plugin properties should be used.
		/* store references to commonly used concepts. These come from system config, defaulted to core IMA ontology. */	    
	    try {
			integerTypeID = 
				new SemanticType(p.getProperty("type.class.integer", "thinklab-core:Integer"));
			floatTypeID = 
				new SemanticType(p.getProperty("type.class.float",     "thinklab-core:FloatingPoint"));
			longTypeID = 
				new SemanticType(p.getProperty("type.class.long",       "thinklab-core:LongInteger"));
			doubleTypeID = 
				new SemanticType(p.getProperty("type.class.double",   "thinklab-core:LongFloatingPoint"));
			numberTypeID = 
				new SemanticType(p.getProperty("type.class.number",   "thinklab-core:Number"));
			booleanTypeID = 
				new SemanticType(p.getProperty("type.class.boolean", "thinklab-core:Boolean"));
			textTypeID = 
				new SemanticType(p.getProperty("type.class.text",       "thinklab-core:Text"));
			rootTypeID = 
				new SemanticType(p.getProperty("type.class.thing",       "owl:Thing"));

			reifiedLiteralPropertyID = 
				new SemanticType(p.getProperty("type.property.reified-literal",
        						 "thinklab-core:ExtendedLiteralProperty"));

			classificationPropertyID = 
				new SemanticType(p.getProperty("type.property.classification",
        					     "thinklab-core:ClassificationProperty"));

			additionalRestrictionsPropertyID = 
				new SemanticType(p.getProperty("type.property.restrictions",
        					     "thinklab-core:AdditionalRestrictions"));
			
			abstractPropertyID = 
				new SemanticType(p.getProperty("type.property.abstract",
        			 			 "thinklab-core:AbstractClass"));

			/*
			 * define XSD mappings for simple types.
			 * 
			 * TODO we should also have additional IValue types with validation for negative and 
			 * positive numbers, URL, ID etc, just like in XSD.
			 */
			xsdMappings.put(XSDVocabulary.STRING.toString(),  textTypeID.toString());
			xsdMappings.put(XSDVocabulary.FLOAT.toString(),   floatTypeID.toString());
			xsdMappings.put(XSDVocabulary.DOUBLE.toString(),  doubleTypeID.toString());
			xsdMappings.put(XSDVocabulary.LONG.toString(),    longTypeID.toString());
			xsdMappings.put(XSDVocabulary.INT.toString(),     integerTypeID.toString());
			xsdMappings.put(XSDVocabulary.INTEGER.toString(), integerTypeID.toString());
			xsdMappings.put(XSDVocabulary.SHORT.toString(),   integerTypeID.toString());
			xsdMappings.put(XSDVocabulary.STRING.toString(),  textTypeID.toString());
			xsdMappings.put(XSDVocabulary.BOOLEAN.toString(), booleanTypeID.toString());
			
		} catch (ThinklabMalformedSemanticTypeException e1) {
			throw new ThinklabValidationException("configuration error: " + e1.getMessage());
		}

	    /* retrieve actual concepts from semantic types. We operate in admin mode only if 
	     * any of these is not present, passing mode to interface. */
	    try {
			integerType  = requireConcept(integerTypeID);
			floatType    = requireConcept(floatTypeID);	
			textType     = requireConcept(textTypeID);
            longType     = requireConcept(longTypeID);
            doubleType   = requireConcept(doubleTypeID);
            numberType   = requireConcept(numberTypeID);
            booleanType  = requireConcept(booleanTypeID);
            
            classificationProperty = requireProperty(classificationPropertyID);
            reifiedLiteralProperty = requireProperty(reifiedLiteralPropertyID);
            additionalRestrictionsProperty = requireProperty(additionalRestrictionsPropertyID);
            abstractProperty = requireProperty(abstractPropertyID);
            
		} catch (ThinklabResourceNotFoundException e) {
			throw new ThinklabValidationException("core type specifications are incomplete: " + e.getMessage());
		}
		
		typesInitialized = true;
	}
	
	public void initialize() throws ThinklabException {
	

		/* link and initialize knowledge repository */
	    knowledgeRepository.initialize();
        
        /* see if the preferences override the thinklab core ontology URL */
        String cont = 
        	LocalConfiguration.getProperties().getProperty(
        			"thinklab.ontology.core", 
        			DEFAULT_CORE_ONTOLOGY);

        	URL tco = Thinklab.get().getResourceURL(cont);  	
            knowledgeRepository.refreshOntology(tco, MiscUtilities.getNameFromURL(cont), true);
        
        /* initialize types before we register plugins */
        initializeThinklabTypes();
                
        /* initialize default blacklists */
		String blk = LocalConfiguration.getProperties().getProperty(IGNORE_PROPERTY_PROPERTY);

		if (blk != null) {
			String[] bk = blk.trim().split(",");
			for (String s : bk) {
				KnowledgeManager.get().blacklistProperty(s);
			}
		}

		blk = LocalConfiguration.getProperties().getProperty(IGNORE_CONCEPT_PROPERTY);

		if (blk != null) {
			String[] bk = blk.trim().split(",");
			for (String s : bk) {
				KnowledgeManager.get().blacklistConcept(s);
			}
		}

        /* create the kbox manager now that we have the type system set up */
        kboxManager = new KBoxManager();
        commandManager = new CommandManager();
        		
		// open any kboxes installed in global properties
		kboxManager.initialize();
		
		Thinklab.get().logger().info("knowledge manager initialized successfully");
		
	}

	
	public void shutdown() {
	
		/* finalize all plug-ins */
		
		/* flush knowledge repository */
        
	}

	
	/**
	 * Launch a query in a separate thread for each installed kbox, unless the constraint contains a target kbox
     * specification. Merge results. The set of different kboxes is seen
	 * by the user as a single knowledge base.
	 * @param query
	 * @param format
	 * @param offset
	 * @param maxResults
	 * @return A list describing the result, with a sublist containing the URI of the resulting instance and 
	 * all the informations required in the schema (with nulls where the information requested could not be
	 * found).
	 */
	public Polylist query(Constraint query, Polylist resultSchema, int offset, int maxResults) {
		// TODO	
		Polylist ret = new Polylist();
		return ret;
	}
	
	/** 
	 * This one is used by all classes to access the knowledge manager.
	 * @return The one and only knowledge manager for this application.
	 * @exception throws an informative exception if KM does not exist or has not been initialized.
	 */
	public static KnowledgeManager get() throws ThinklabNoKMException {
		
		if (KM == null)
		   throw new ThinklabNoKMException();
		return KM;
	}
	
	public static boolean exists() {
		return KM != null;
	}
	
	/**
	 * Register the class of a session listener that we want to pass to any new session. We store
	 * classes directly because plugins use private classloaders. 
	 * 
	 * @param sessionListenerClass
	 */
	public void registerSessionListenerClass(Class<?> sessionListenerClass) {
		sessionListeners.put(sessionListenerClass.getCanonicalName(), sessionListenerClass);
	}

	public void unregisterSessionListenerClass(String className) {
		sessionListeners.remove(className);
	}

	public void registerInstanceConstructor(String conceptID, InstanceImplementationConstructor constructor) {
		this.instanceConstructors.put(conceptID, constructor);
	}

	public void unregisterInstanceConstructor(String conceptID) {
		this.instanceConstructors.remove(conceptID);
	}
	
	public void registerLiteralValidator(String conceptID, LiteralValidator validator) {
		validator.declareType();
		this.literalValidators.put(conceptID, validator);
	}

	public void unregisterLiteralValidator(String conceptID) {
		this.literalValidators.remove(conceptID);
	}
	
	public void registerKnowledgeLoader(String format, KnowledgeLoader loader) {
		knowledgeLoaders.put(format, loader);
	}

	public void unregisterKnowledgeLoader(String format) {
		knowledgeLoaders.remove(format);
	}
	
    /* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.IKnowledgeBase#requireConcept(java.lang.String)
	 */
    public IConcept requireConcept(String id) throws ThinklabMalformedSemanticTypeException, ThinklabResourceNotFoundException {
        SemanticType st = new SemanticType(id);
        return requireConcept(st);
    }
    
    /* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.IKnowledgeBase#requireProperty(java.lang.String)
	 */
    public IProperty requireProperty(String id) throws ThinklabMalformedSemanticTypeException, ThinklabResourceNotFoundException {
 
    	SemanticType st = new SemanticType(id);
        return requireProperty(st);
    }
    
    /* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.IKnowledgeBase#requireInstance(java.lang.String)
	 */
    public IInstance requireInstance(String id) throws ThinklabMalformedSemanticTypeException, ThinklabResourceNotFoundException {
        SemanticType st = new SemanticType(id);
        IInstance ret = knowledgeRepository.requireOntology(st.getConceptSpace()).getInstance(st.getLocalName());
        if (ret == null) {
        	throw new ThinklabResourceNotFoundException("instance " + id + " is unknown");
        }
        return ret;
    }
    
    /**
     * Return concept from semantic type. Concept must exist.
     * @param id the semantic type
     * @return the concept.
     * @throws ThinklabResourceNotFoundException if concept not found.
     */
    public IConcept requireConcept(SemanticType id) throws ThinklabResourceNotFoundException {
    	
        IConcept ret = retrieveConcept(id);
        
        if (ret == null) {
        	throw new ThinklabResourceNotFoundException("concept " + id + " is unknown");
        }
        return ret;
    }

    /**
     * Return property from semantic type. Concept must exist.
     * @param id the semantic type
     * @return the property.
     * @throws ThinklabResourceNotFoundException if property not found.
     */
    public IProperty requireProperty(SemanticType id) throws ThinklabResourceNotFoundException {	
    	
        IProperty ret =  retrieveProperty(id);
        
        if (ret == null) {
        	throw new ThinklabResourceNotFoundException("property " + id + " is unknown");
        }
        return ret;
    }
    
    /**
     * Return instance from semantic type. Concept must exist.
     * @param id the semantic type
     * @return the concept.
     * @throws ThinklabResourceNotFoundException if concept not found.
     */
    public IInstance requireInstance(SemanticType id) throws ThinklabResourceNotFoundException {
  
    	IInstance ret = retrieveInstance(id);
  
    	if (ret == null) {
        	throw new ThinklabResourceNotFoundException("property " + id + " is unknown");
        }
        return ret;
    }
    
    /**
     * Return the concept manager that provides a literal validator for the
     * passed concept.
     * @param type the concept
     * @return a concept manager or null
     * @throws ThinklabException if there is ambiguity
     */
    public LiteralValidator getValidator(IConcept type) throws ThinklabException {

        class vmatch implements ConceptVisitor.ConceptMatcher {

            private HashMap<String, LiteralValidator> coll;

            public LiteralValidator ret = null;
            
            public vmatch(HashMap<String, LiteralValidator> c) {
                coll = c;
            }
            
            public boolean match(IConcept c) {
                ret = coll.get(c.getSemanticType().toString());
                return(ret != null);	
            }    
        }
        
        vmatch matcher = new vmatch(literalValidators);
        
        IConcept cms = 
            ConceptVisitor.findMatchUpwards(matcher, type);

        return cms == null ? null : matcher.ret;
    }

    /**
     * Return the concept manager that provides instance implementation
     * @param type
     * @return
     * @throws ThinklabException
     */
    public InstanceImplementationConstructor getInstanceConstructor(IConcept type) throws ThinklabException{

        class vmatch implements ConceptVisitor.ConceptMatcher {

            private HashMap<String, InstanceImplementationConstructor> coll;
            
            public vmatch(HashMap<String, InstanceImplementationConstructor> c) {
                coll = c;
            }
            
            public boolean match(IConcept c) {
                InstanceImplementationConstructor cc = coll.get(c.getSemanticType().toString());
                return (cc != null);
            }    
        }
        
//        Collection<IInstanceImplementationConstructor> cms = 
//            new ConceptVisitor<IInstanceImplementationConstructor>().findAllMatchesInMapUpwards(instanceConstructors, new vmatch(instanceConstructors), type);
//        
//        if (cms.size() > 1)
//            throw new ThinklabAmbiguousResultException("more than one concepts can validate a " + 
//                                              type.getSemanticType().toString() + 
//                                              "literal. Class structure should be revised.");
//        
//        return cms.size() > 0 ? cms.iterator().next() : null;
        
        /*
         * I may be wrong, but there's no problem finding more than one constructor - just return the
         * least general one... 
         * There IS a problem if the ambiguity comes from a logical union - this should be checked, but
         * not now.
         */
        InstanceImplementationConstructor cms = 
    	  new ConceptVisitor<InstanceImplementationConstructor>().findMatchingInMapUpwards(instanceConstructors, new vmatch(instanceConstructors), type);
        
        return cms;
    }
    
    /* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.IKnowledgeBase#getConceptFromURI(java.lang.String)
	 */
    public IConcept getConceptFromURI(String uri) {
 
        IConcept ret = null;

        String[] ss = uri.split("#");
        if (ss.length != 2)
           return ret;
        
        IOntology o = null;
        try {
            String csp = getConceptSpaceFromURI(ss[0] + "#");
            if (csp == null)
                return ret;
            o = knowledgeRepository.retrieveOntology(csp);
            if (o != null)
                ret = o.getConcept(ss[1]);
        } catch (ThinklabResourceNotFoundException e) {
        }
        return ret;
    }

    /* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.IKnowledgeBase#getPropertyFromURI(java.lang.String)
	 */
    public IProperty getPropertyFromURI(String uri) {
        
        IProperty ret = null;

        String[] ss = uri.split("#");
        if (ss.length != 2)
           return ret;
        
        IOntology o = null;
        try {
            String csp = getConceptSpaceFromURI(ss[0] + "#");
            if (csp == null)
                return ret;
            o = knowledgeRepository.retrieveOntology(csp);
            if (o != null)
                ret = o.getProperty(ss[1]);
        } catch (ThinklabResourceNotFoundException e) {
        }
        return ret;
    }


	public String registerOntology(String url, String name) throws ThinklabException {
		
		URL u = null;
		
		try {
			u = new URL(url);
		} catch (MalformedURLException e) {
			// try a local file
			File f = new File(url);	
			if (f.canRead()) {
				try {
					u = f.toURL();
				} catch (MalformedURLException e1) {
					throw new ThinklabIOException(e1);
				}
			} else {
				throw new ThinklabIOException(url);
			}
		}
		return knowledgeRepository.importOntology(u, name, true);
	}

	public void clear() {
		// clear all knowledge. For admin use only, I would suggest.
		knowledgeRepository.releaseAllOntologies();
	}

	public void clear(String id) {
		// clear specific ontology. For admin use only, I would suggest.
		knowledgeRepository.releaseOntology(id);
	}
	
	public IInstance retrieveInstance(SemanticType t) {
		IInstance ret = null;
	    IOntology o = knowledgeRepository.retrieveOntology(t.getConceptSpace());
	    if (o != null)
	    	ret = o.getInstance(t.getLocalName());
	    return ret;
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.IKnowledgeBase#retrieveInstance(java.lang.String)
	 */
	public IKnowledgeSubject retrieveInstance(String resultID) throws ThinklabMalformedSemanticTypeException {
		return retrieveInstance(new SemanticType(resultID));
	}

	public IConcept retrieveConcept(SemanticType t) {
		
		IConcept ret = null;
	    IOntology o = knowledgeRepository.retrieveOntology(t.getConceptSpace());
	    if (o != null)
	    	ret = o.getConcept(t.getLocalName());
	    if (ret == null && t.toString().equals(rootTypeID.toString()))	{
	    	ret = getRootConcept();
	    }
	    return ret;
	}

	public IProperty retrieveProperty(SemanticType t) {
		
		IProperty ret = null;
	    IOntology o = knowledgeRepository.retrieveOntology(t.getConceptSpace());
	    if (o != null)
	    	ret = o.getProperty(t.getLocalName());
	    return ret;
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.IKnowledgeBase#retrieveProperty(java.lang.String)
	 */
	public IProperty retrieveProperty(String prop) throws ThinklabMalformedSemanticTypeException {
		return retrieveProperty(new SemanticType(prop));
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.IKnowledgeBase#retrieveConcept(java.lang.String)
	 */
	public IConcept retrieveConcept(String prop) throws ThinklabMalformedSemanticTypeException {
		return retrieveConcept(new SemanticType(prop));
	}

	/**
	 * Register a mapping between an ontology URI and a concept space name. Note: should be called by
	 * whatever creates the ontologies in the Knowledge Repository. Users won't need to know this one,
	 * but repositories need to use it.
	 * @param u the URI
	 * @param label the concept space name
	 */
	public void registerURIMapping(String u, String label) {
		uri2cs.put(u, label);
		cs2uri.put(label, u);
	}

	/**
	 * Get the concepts space from a URI that also contains a fragment after it.
	 * @param uri
	 * @return
	 */
	public String getConceptSpaceOfURI(String uri) {
		String u = uri.substring(0, uri.indexOf("#")+1);
		return uri2cs.get(u);
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.IKnowledgeBase#getInstanceFromURI(java.lang.String, org.integratedmodelling.thinklab.interfaces.ISession)
	 */
	public IInstance getInstanceFromURI(String uri, ISession session) throws ThinklabException {
		
			IInstance ret = null;
		
	        String[] ss = uri.split("#");
	        if (ss.length != 2)
	           return ret;
	        
	        IOntology o = null;

	        String csp = getConceptSpaceFromURI(ss[0] + "#");

	        if (csp != null) {
	        	o = knowledgeRepository.retrieveOntology(csp);
	        	if (o != null) {
	        		
	        		ret = o.getInstance(ss[1]);
	        		if (ret != null) {
	        			ret = session.createObject(ret);
	            	}
	        	}	
	        }
	        
	        /* not an ontology, so must be a kbox */
	        if (ret == null) {
	        	
	        	IKBox kbox = session.retrieveKBox(ss[0]);
	        	
	        	if (kbox != null) {
	        		ret = kbox.getObjectFromID(ss[1], session);
	        	}
	        }
	        return ret;
	}


	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.IKnowledgeBase#getLeastGeneralCommonConcept(org.integratedmodelling.thinklab.SemanticType, org.integratedmodelling.thinklab.SemanticType)
	 */
	public IConcept getLeastGeneralCommonConcept(SemanticType semanticType, SemanticType otherConcept) 
		throws ThinklabResourceNotFoundException {
		return getLeastGeneralCommonConcept(requireConcept(semanticType), requireConcept(otherConcept));
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.IKnowledgeBase#getLeastGeneralCommonConcept(org.integratedmodelling.thinklab.interfaces.IConcept, org.integratedmodelling.thinklab.interfaces.IConcept)
	 */
	public IConcept getLeastGeneralCommonConcept(IConcept concept1, IConcept c) {
		return concept1.getLeastGeneralCommonConcept(c);
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.IKnowledgeBase#getLeastGeneralCommonConcept(java.util.Collection)
	 */
	public IConcept getLeastGeneralCommonConcept(Collection<IConcept> cc) {

		IConcept ret = null;
		Iterator<IConcept> ii = cc.iterator();

		if (ii.hasNext()) {		
			
		  ret = ii.next();
		  while (ii.hasNext())
		    {
		      ret = ret.getLeastGeneralCommonConcept(ii.next());
		      if (ret == null)
		    	  break;
		    }
		}
		
		return ret;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.IKnowledgeBase#validateLiteral(org.integratedmodelling.thinklab.interfaces.IConcept, java.lang.String, org.integratedmodelling.thinklab.interfaces.IOntology)
	 */
	public IValue validateLiteral(IConcept c, String literal, IOntology ontology) throws ThinklabValidationException {
		IValue ret = null;
		LiteralValidator cm = null;
		try {
			cm = getValidator(c);
		} catch (ThinklabException e) {
			throw new ThinklabValidationException(e);
		}
		if (cm != null)
			ret = cm.validate(literal, c, ontology);
		else 
			throw new ThinklabValidationException("don't know how to validate a literal of type " + c.toString());

		return ret;
	}
	
	/**
	 * Request a user session to put stuff into. You can later make the session permanent by adding it to the
	 * knowledge base. Note that all operations on sessions are typically synchronized. 
	 */
	public ISession requestNewSession() throws ThinklabException {
		
		ISession session = sessionManager.createNewSession();

		for (Class<?> lcl : sessionListeners.values()) {
			
			IThinklabSessionListener listener = null;
			
			try {
				listener = (IThinklabSessionListener) lcl.newInstance();
			} catch (Exception e) {
				throw new ThinklabMissingResourceException("cannot create requested session listener: " + lcl + ": error during creation");
			}
			listener.sessionCreated(session);
			session.addListener(listener);
		}
		
		return session;
	}

	public void notifySessionDeletion(ISession session) throws ThinklabException {
		
		Collection<IThinklabSessionListener> listeners = session.getListeners();
		
		if (listeners != null)
			for (IThinklabSessionListener listener : listeners)
				listener.sessionDeleted(session);
		
		sessionManager.notifySessionDeletion(session);
	}

	/**
	 * Provided to simplify access to core types when we are sure that we have a knowledge
	 * manager.
	 * @return
	 */
	public static IConcept Double() {
		return KM.doubleType;
	}

	/**
	 * Provided to simplify access to core types when we are sure that we have a knowledge
	 * manager.
	 * @return
	 */
	public static IConcept Boolean() {
		return KM.booleanType;
	}

	/**
	 * Provided to simplify access to core types when we are sure that we have a knowledge
	 * manager.
	 * @return
	 */
	public static IConcept Text() {
		return KM.textType;
	}

	/**
	 * Provided to simplify access to core types when we are sure that we have a knowledge
	 * manager.
	 * @return
	 */
	public static IConcept Long() {
		return KM.longType;
	}

	/**
	 * Provided to simplify access to core types when we are sure that we have a knowledge
	 * manager.
	 * @return
	 */
	public static IConcept Integer() {
		return KM.integerType;
	}
	
	/**
	 * Provided to simplify access to core types when we are sure that we have a knowledge
	 * manager.
	 * @return
	 */
	public static IConcept Float() {
		return KM.floatType;
	}

	/**
	 * Provided to simplify access to core types when we are sure that we have a knowledge
	 * manager.
	 * @return
	 */
	public static IConcept Number() {
		return KM.numberType;
	}
	
	public static IConcept Thing() {
		return KM.knowledgeRepository.getRootConceptType();
	}

	
	/**
	 * Provided to simplify access to core types when we are sure that we have a knowledge
	 * manager.
	 * @return
	 */
	public static String DoubleType() {
		return KM.doubleTypeID.toString();
	}

	/**
	 * Provided to simplify access to core types when we are sure that we have a knowledge
	 * manager.
	 * @return
	 */
	public static String BooleanType() {
		return KM.booleanTypeID.toString();
	}

	/**
	 * Provided to simplify access to core types when we are sure that we have a knowledge
	 * manager.
	 * @return
	 */
	public static String TextType() {
		return KM.textTypeID.toString();
	}

	/**
	 * Provided to simplify access to core types when we are sure that we have a knowledge
	 * manager.
	 * @return
	 */
	public static String LongType() {
		return KM.longTypeID.toString();
	}

	/**
	 * Provided to simplify access to core types when we are sure that we have a knowledge
	 * manager.
	 * @return
	 */
	public static String IntegerType() {
		return KM.integerTypeID.toString();
	}
	
	/**
	 * Provided to simplify access to core types when we are sure that we have a knowledge
	 * manager.
	 * @return
	 */
	public static String FloatType() {
		return KM.floatTypeID.toString();
	}

	/**
	 * Provided to simplify access to core types when we are sure that we have a knowledge
	 * manager.
	 * @return
	 */
	public static IConcept NumberType() {
		return KM.numberType;
	}
	
	public static IConcept ThingType() {
		return KM.knowledgeRepository.getRootConceptType();
	}


	public IProperty getAdditionalRestrictionProperty() {
		// TODO Auto-generated method stub
		return additionalRestrictionsProperty;
	}

	/**
	 * Return the IO plugin that declares to be capable of handling a particular format.
	 * @param format
	 * @return
	 */
	public KnowledgeLoader getKnowledgeLoader(String format) {

		return knowledgeLoaders.get(format);
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.IKnowledgeBase#blacklistProperty(java.lang.String)
	 */
	public void blacklistProperty(String semType) {
		propertyBlacklist.add(semType);
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.IKnowledgeBase#blacklistConcept(java.lang.String)
	 */
	public void blacklistConcept(String semType) {
		conceptBlacklist.add(semType);
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.IKnowledgeBase#isConceptBlacklisted(java.lang.String)
	 */
	public boolean isConceptBlacklisted(String c) {
		return conceptBlacklist.contains(c);
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.IKnowledgeBase#isPropertyBlacklisted(java.lang.String)
	 */
	public boolean isPropertyBlacklisted(String c) {
		return propertyBlacklist.contains(c);
	}

	public KBoxManager getKBoxManager() {
		return kboxManager;
	}

	public CommandManager getCommandManager() {
		return commandManager;
	}

	public PluginManager getPluginManager() {
		return pluginManager;
	}
	
	public void setPluginManager(PluginManager pman) {
		pluginManager = pman;
	}

	/**
	 * Set a new session manager to redefine the types of sessions you want created.
	 * @param sessionManager
	 */
	public void setSessionManager(ISessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

	public void registerXSDTypeMapping(String xsd, String type) {
		xsdMappings.put(xsd, type);	
	}


}