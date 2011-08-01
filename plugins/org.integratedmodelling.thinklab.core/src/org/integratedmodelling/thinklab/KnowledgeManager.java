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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabIOException;
import org.integratedmodelling.exceptions.ThinklabResourceNotFoundException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.lang.SemanticType;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IInstance;
import org.integratedmodelling.thinklab.api.knowledge.IInstanceImplementation;
import org.integratedmodelling.thinklab.api.knowledge.IOntology;
import org.integratedmodelling.thinklab.api.knowledge.IProperty;
import org.integratedmodelling.thinklab.api.knowledge.IValue;
import org.integratedmodelling.thinklab.api.lang.IParseable;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.command.CommandDeclaration;
import org.integratedmodelling.thinklab.command.CommandManager;
import org.integratedmodelling.thinklab.configuration.LocalConfiguration;
import org.integratedmodelling.thinklab.extensions.KnowledgeLoader;
import org.integratedmodelling.thinklab.interfaces.IKnowledgeRepository;
import org.integratedmodelling.thinklab.interfaces.applications.ISessionManager;
import org.integratedmodelling.thinklab.plugin.IPluginLifecycleListener;
import org.integratedmodelling.utils.MiscUtilities;
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
public class KnowledgeManager {

    /** default core ontologies loaded at startup. Can be changed using thinklab.ontology.default property, but don't. */
	private static final String DEFAULT_CORE_ONTOLOGIES = 
		"thinklab-core.owl,user.owl,observation.owl,measurement.owl,representation.owl,source.owl,modelling.owl";

    /** 
	 * <p>The Knowledge Manager is a singleton. This is created by the initializer and an exception 
	 * is thrown if a second one has been initialized. </p>
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
    private IConcept operatorType;
    private IConcept booleanType;
    private IConcept longType;
    private IConcept doubleType;
    private IConcept ordinalRankingType;
    private IConcept booleanRankingType;
    private IConcept ordinalRangeMappingType;
    private IConcept literalType;

    private IProperty classificationProperty;
    private IProperty reifiedLiteralProperty;
    private IProperty abstractProperty;
	private IProperty additionalRestrictionsProperty;
    private IProperty importedProperty;
    
    private SemanticType integerTypeID;
    private SemanticType floatTypeID;    
    private SemanticType textTypeID;
    private SemanticType numberTypeID;
    private SemanticType booleanTypeID;
    private SemanticType literalTypeID;
    private SemanticType doubleTypeID;
    private SemanticType longTypeID;
    private SemanticType operatorTypeID;
    private SemanticType rootTypeID;
    private SemanticType classificationPropertyID;
    private SemanticType reifiedLiteralPropertyID;
    private SemanticType additionalRestrictionsPropertyID;
	private SemanticType abstractPropertyID;
	private SemanticType importedPropertyID;
	private SemanticType ordinalRankingTypeID;
	private SemanticType booleanRankingTypeID;
	private SemanticType ordinalRangeMappingTypeID;

	protected IKnowledgeRepository knowledgeRepository;

	protected PluginManager pluginManager = null;
	
	protected CommandManager commandManager;
	
	public static final String EXCLUDE_ONTOLOGY_PROPERTY = "thinklab.ontology.exclude";
	
	/**
	 * colon-separated path to find resources
	 */
	public static final String RESOURCE_PATH_PROPERTY = "thinklab.resource.path";
	
	/*
	 * map URIs to concept space names 
	 */
	HashMap<String, String> uri2cs = new HashMap<String, String>();
	
	/*
	 * map concept space names to URIs 
	 */
	HashMap<String, String> cs2uri = new HashMap<String, String>();
	
	/*
	 * listeners for plugin load/unload can be added through registerPluginListener
	 */
	static ArrayList<IPluginLifecycleListener> pluginListeners =
		new ArrayList<IPluginLifecycleListener>();

    /*
     * true when thinklab extended types have been initialized.
     */
	private boolean typesInitialized = false;

	@Deprecated
	private HashMap<String, KnowledgeLoader> knowledgeLoaders =
		new HashMap<String, KnowledgeLoader>();

	private HashMap<String, Class<?>> sessionListeners = 
		new HashMap<String, Class<?>>();


	private Date start;

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
		
		Class<?> cls = null;
		try {

			cls = Thinklab.get().getClassLoader().loadClass(smClass);

			cls = Thinklab.get().getClassLoader().loadClass(krClass);
			kr =  (IKnowledgeRepository) cls.newInstance();

		} catch (Exception e) {
			throw new ThinklabValidationException(e);
		}
		
		knowledgeRepository = kr;
        
        this.start = new Date();
	}

	/**
	 * Time of boot of Thinklab.
	 * 
	 * @return
	 */
	public Date activeSince() {
		return start;
	}
	
	public IKnowledgeRepository getKnowledgeRepository() {
		return knowledgeRepository;
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
	
	public IConcept getOperatorType() throws ThinklabValidationException {
		if (!typesInitialized)
			initializeThinklabTypes();
		return operatorType;
	}

	public void setOperatorType(IConcept type) {
		this.operatorType = type;
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
			literalTypeID = 
				new SemanticType(p.getProperty("type.class.literal", "thinklab-core:LiteralValued"));
			textTypeID = 
				new SemanticType(p.getProperty("type.class.text",       "thinklab-core:Text"));
			operatorTypeID = 
				new SemanticType(p.getProperty("type.class.operator",   "thinklab-core:Operation"));
			rootTypeID = 
				new SemanticType(p.getProperty("type.class.thing",       "owl:Thing"));
			ordinalRankingTypeID = 
				new SemanticType(p.getProperty("type.class.ordinal-ranking",    
						"thinklab-core:OrdinalRanking"));
			booleanRankingTypeID = 
				new SemanticType(p.getProperty("type.class.boolean-ranking",    
						"thinklab-core:BooleanRanking"));
			ordinalRangeMappingTypeID = 
				new SemanticType(p.getProperty("type.class.ordered-range-mapping",
						"thinklab-core:OrderedRangeMapping"));


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
			
			importedPropertyID = 
				new SemanticType(p.getProperty("type.property.imported",
        			 			 "thinklab-core:importedFrom"));

			/*
			 * define XSD mappings for simple types.
			 * 
			 * TODO we should also have additional IValue types with validation for negative and 
			 * positive numbers, URL, ID etc, just like in XSD.
			 */
			Thinklab.get().registerXSDTypeMapping(XSDVocabulary.STRING.toString(),  textTypeID.toString());
			Thinklab.get().registerXSDTypeMapping(XSDVocabulary.FLOAT.toString(),   floatTypeID.toString());
			Thinklab.get().registerXSDTypeMapping(XSDVocabulary.DOUBLE.toString(),  doubleTypeID.toString());
			Thinklab.get().registerXSDTypeMapping(XSDVocabulary.LONG.toString(),    longTypeID.toString());
			Thinklab.get().registerXSDTypeMapping(XSDVocabulary.INT.toString(),     integerTypeID.toString());
			Thinklab.get().registerXSDTypeMapping(XSDVocabulary.INTEGER.toString(), integerTypeID.toString());
			Thinklab.get().registerXSDTypeMapping(XSDVocabulary.SHORT.toString(),   integerTypeID.toString());
			Thinklab.get().registerXSDTypeMapping(XSDVocabulary.STRING.toString(),  textTypeID.toString());
			Thinklab.get().registerXSDTypeMapping(XSDVocabulary.BOOLEAN.toString(), booleanTypeID.toString());
			
		} catch (ThinklabRuntimeException e1) {
			throw new ThinklabValidationException("configuration error: " + e1.getMessage());
		}

	    /* retrieve actual concepts from semantic types */
	    try {
			integerType  = requireConcept(integerTypeID);
			floatType    = requireConcept(floatTypeID);
			textType     = requireConcept(textTypeID);
            longType     = requireConcept(longTypeID);
            doubleType   = requireConcept(doubleTypeID);
            numberType   = requireConcept(numberTypeID);
            booleanType  = requireConcept(booleanTypeID);
            literalType  = requireConcept(literalTypeID);
            booleanRankingType  = requireConcept(booleanRankingTypeID);
            ordinalRankingType  = requireConcept(ordinalRankingTypeID);
            ordinalRangeMappingType  = requireConcept(ordinalRangeMappingTypeID);
            operatorType  = requireConcept(operatorTypeID);
            
            classificationProperty = requireProperty(classificationPropertyID);
            reifiedLiteralProperty = requireProperty(reifiedLiteralPropertyID);
            additionalRestrictionsProperty = requireProperty(additionalRestrictionsPropertyID);
            abstractProperty = requireProperty(abstractPropertyID);
            importedProperty = requireProperty(importedPropertyID);
            
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
        			DEFAULT_CORE_ONTOLOGIES);

        for (String ccont : cont.split(",")) {
        	URL tco = Thinklab.get().getResourceURL(ccont);  	
            knowledgeRepository.refreshOntology(tco, MiscUtilities.getNameFromURL(ccont), true);
        }
        
        /* initialize types before we register plugins */
        initializeThinklabTypes();
                
        commandManager = new CommandManager();
        				
		Thinklab.get().logger().info("knowledge manager initialized successfully");
		
	}

	
	public void shutdown() {
	
		/* TODO flush knowledge repository */

		/* TODO any other cleanup actions */

	}

	
	/** 
	 * This one is used by all classes to access the knowledge manager.
	 * @return The one and only knowledge manager for this application.
	 * @exception throws an informative exception if KM does not exist or has not been initialized.
	 */
	public static KnowledgeManager get() {
		
		if (KM == null)
		   throw new ThinklabRuntimeException("no knowledge manager!");
		return KM;
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

	
    /* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.IKnowledgeBase#requireConcept(java.lang.String)
	 */
    public IConcept requireConcept(String id) throws ThinklabException {
    	
    	SemanticType st = new SemanticType(id);
    	return requireConcept(st);
    }
    
    /* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.IKnowledgeBase#requireProperty(java.lang.String)
	 */
    public IProperty requireProperty(String id) throws ThinklabException {
 
    	SemanticType st = new SemanticType(id);
        return requireProperty(st);
    }
    
    /* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.IKnowledgeBase#requireInstance(java.lang.String)
	 */
    public IInstance requireInstance(String id) throws ThinklabException {
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
    

    public IInstanceImplementation newInstanceImplementation(IConcept type) throws ThinklabException{

        Class<?> cms = 
    	  Thinklab.get().getClassForConcept(type);
        
        if (cms != null) {
        	try {
				return (IInstanceImplementation) cms.newInstance();
			} catch (Exception e) {
				throw new ThinklabValidationException("cannot create implementation: " + e.getMessage());
			}        	
        }
        
        return null;
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
	public IInstance retrieveInstance(String resultID) throws ThinklabValidationException {
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
	public IProperty retrieveProperty(String prop) {
		return retrieveProperty(new SemanticType(prop));
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.IKnowledgeBase#retrieveConcept(java.lang.String)
	 */
	public IConcept retrieveConcept(String conc) {
				
		IConcept ret = null;

		return 
			ret == null ?
				retrieveConcept(new SemanticType(conc)) :
				ret;
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
	 * @see org.integratedmodelling.thinklab.IKnowledgeBase#getLeastGeneralCommonConcept(org.integratedmodelling.thinklab.SemanticType, org.integratedmodelling.thinklab.SemanticType)
	 */
	public IConcept getLeastGeneralCommonConcept(String semanticType, String otherConcept) throws ThinklabException {
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
		  
		  if (ret != null)
			while (ii.hasNext()) {
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
	public IValue validateLiteral(IConcept c, String literal) throws ThinklabException {
		
		IValue ret = Thinklab.get().getRawLiteral(c);
		if (ret != null && ret instanceof IParseable)
			((IParseable)ret).parse(literal);
		else 
			throw 
				new ThinklabValidationException("don't know how to validate a literal of type " + c.toString());

		return ret;
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

	
	public CommandManager getCommandManager() {
		return commandManager;
	}

	public PluginManager getPluginManager() {
		return pluginManager;
	}
	
	public void setPluginManager(PluginManager pman) {
		pluginManager = pman;
	}


	public static void registerPluginListener(IPluginLifecycleListener listener) {
		pluginListeners.add(listener);
	}

	public static Collection<IPluginLifecycleListener> getPluginListeners() {
		return pluginListeners;
	}

	public static IConcept OperatorType() {
		return KM.operatorType;
	}

	public static IConcept LiteralValue() {
		// TODO Auto-generated method stub
		return KM.literalType;
	}

	public IProperty getImportedProperty() throws ThinklabValidationException {
		if (!typesInitialized)
			initializeThinklabTypes();
		return importedProperty;
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


	public static IConcept BooleanRanking() {
		return KM.booleanRankingType;
	}
	
	public static IConcept OrdinalRanking() {
		return KM.ordinalRankingType;
	}

	public static IConcept OrderedRangeMapping() {
		// TODO Auto-generated method stub
		return KM.ordinalRangeMappingType;
	}
	

	public static IConcept Nothing() {
		// TODO Auto-generated method stub
		return KM.knowledgeRepository.getNothingType();
	}

	/**
	 * Return the literal concept that represents the POD type of the
	 * object passed. Anything non-POD will return null.
	 * 
	 * @param value
	 * @return
	 */
	public static IConcept getConceptForObject(Object value) {

		IConcept ret = null;

        if (value instanceof Integer) {
            ret = KnowledgeManager.Integer();
        } else if (value instanceof Float) {
            ret = KnowledgeManager.Float();
        } else if (value instanceof Double) {
            ret = KnowledgeManager.Double();
        } else if (value instanceof Long) {
            ret = KnowledgeManager.Long();            
        } else if (value instanceof String) {
            ret = KnowledgeManager.Text();
        } else if (value instanceof Boolean) {
            ret = KnowledgeManager.Boolean();
        } 
    
        return ret;
	}

	/**
	 * Static and throws a runtime exception if not found. For simpler usage when you know the KM is 
	 * there and the concept should be.
	 * 
	 * @param string
	 * @return
	 */
	public static IConcept getConcept(String string) {
		try {
			return get().requireConcept(string);
		} catch (ThinklabException e) {
			throw new ThinklabRuntimeException(e);
		}
	}
}
