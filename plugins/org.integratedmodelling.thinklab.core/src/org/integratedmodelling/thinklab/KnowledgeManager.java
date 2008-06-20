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
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.command.CommandDeclaration;
import org.integratedmodelling.thinklab.configuration.LocalConfiguration;
import org.integratedmodelling.thinklab.constraint.Constraint;
import org.integratedmodelling.thinklab.exception.ThinklabAmbiguousResultException;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabMalformedCommandException;
import org.integratedmodelling.thinklab.exception.ThinklabMalformedSemanticTypeException;
import org.integratedmodelling.thinklab.exception.ThinklabMissingResourceException;
import org.integratedmodelling.thinklab.exception.ThinklabNoKMException;
import org.integratedmodelling.thinklab.exception.ThinklabResourceNotFoundException;
import org.integratedmodelling.thinklab.exception.ThinklabStorageException;
import org.integratedmodelling.thinklab.exception.ThinklabUndefinedKBoxException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.impl.APIOnlyKnowledgeInterface;
import org.integratedmodelling.thinklab.interfaces.IAction;
import org.integratedmodelling.thinklab.interfaces.ICommandOutputReceptor;
import org.integratedmodelling.thinklab.interfaces.IConcept;
import org.integratedmodelling.thinklab.interfaces.IInstance;
import org.integratedmodelling.thinklab.interfaces.IInstanceImplementationConstructor;
import org.integratedmodelling.thinklab.interfaces.IKBox;
import org.integratedmodelling.thinklab.interfaces.IKBoxPlugin;
import org.integratedmodelling.thinklab.interfaces.IKnowledgeProvider;
import org.integratedmodelling.thinklab.interfaces.IKnowledgeInterface;
import org.integratedmodelling.thinklab.interfaces.IKnowledgeLoaderPlugin;
import org.integratedmodelling.thinklab.interfaces.IKnowledgeRepository;
import org.integratedmodelling.thinklab.interfaces.IKnowledgeSubject;
import org.integratedmodelling.thinklab.interfaces.ILiteralValidator;
import org.integratedmodelling.thinklab.interfaces.IOntology;
import org.integratedmodelling.thinklab.interfaces.IPlugin;
import org.integratedmodelling.thinklab.interfaces.IProperty;
import org.integratedmodelling.thinklab.interfaces.ISession;
import org.integratedmodelling.thinklab.interfaces.IThinklabSessionListener;
import org.integratedmodelling.thinklab.interfaces.IValue;
import org.integratedmodelling.thinklab.kbox.KBoxManager;
import org.integratedmodelling.thinklab.kbox.KBoxWrapper;
import org.integratedmodelling.thinklab.kbox.OntologyKBox;
import org.integratedmodelling.thinklab.plugin.PluginRegistry;
import org.integratedmodelling.thinklab.validators.NumberValidator;
import org.integratedmodelling.thinklab.validators.TextValidator;
import org.integratedmodelling.utils.MiscUtilities;
import org.integratedmodelling.utils.Polylist;

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
 * @see IKnowledgeInterface
 * @see IKnowledgeRepository
 * @see CommandDeclaration
 * @see Command
 */
public class KnowledgeManager implements IKnowledgeProvider {

    /** default core ontology URL. It really is small - just some POD data types and a couple properties. */
	private static final String DEFAULT_CORE_ONTOLOGY = 
        "http://www.integratedmodelling.org/ks/thinklab/thinklab-core.owl";

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
	protected IKnowledgeInterface  knowledgeInterface;
	protected PluginRegistry       pluginRegistry;
	protected KBoxManager kboxManager;
	
	private Logger log = Logger.getLogger(this.getClass());
	
	//
	public static final String EXCLUDE_ONTOLOGY_PROPERTY = "thinklab.ontology.exclude";
	
	// this one in any config file will add properties to the blacklist.
	public static final String IGNORE_PROPERTY_PROPERTY = "thinklab.property.ignore";

	public static final String IGNORE_CONCEPT_PROPERTY = "thinklab.concept.ignore";
	
	/**
	 * After initialization, we create a class tree for the repository, so that
	 * we can give an option to the API of using it for simple transitive subsumption 
	 * checking instead of the reasoner.
	 */
	private static KnowledgeTree classTree = null;
		
	/**
	 * command declarations are kept in a hash, indexed by command ID
	 */
	HashMap<String, CommandDeclaration> commands;
	
	/**
	 * each command has an action associated, also kept in a hash indexed by command ID
	 */
	HashMap<String, IAction> actions;
	
	/*
	 * map URIs to concept space names 
	 */
	HashMap<String, String> uri2cs;
	
	/*
	 * map concept space names to URIs 
	 */
	HashMap<String, String> cs2uri;

    /*
     * a registry of plugins that handle KBox creation.
     * TODO move to kbox manager
     */
    HashMap<String, IKBoxPlugin> kboxPlugins;
    
    /*
     * A registry of installed KBoxes, indexed by their URL.
     * TODO move to kbox manager
     */
    HashMap<String, IKBox> kBoxes;
    /*
     * true when thinklab extended types have been initialized.
     */
	private boolean typesInitialized = false;

	private HashMap<String, IInstanceImplementationConstructor> instanceConstructors;

	private HashMap<String, ILiteralValidator> literalValidators;

	private ArrayList<String> sessionListeners = new ArrayList<String>();

	private File pluginClassPath;

	private File pluginJarPath;

	private HashSet<String> propertyBlacklist = new HashSet<String>();
	private HashSet<String> conceptBlacklist = new HashSet<String>();


	public static KnowledgeTree getClassTree() {
		return classTree;
	}
	
	/*
	 * TODO move to kbox manager
	 */
	private void installDefaultKboxes() throws ThinklabException {
		
		String kboxes = LocalConfiguration.getProperties().getProperty("thinklab.kbox.list");
		
		if (kboxes != null && !kboxes.trim().equals("")) {
			
			String[] kboxx = kboxes.split(",");
			
			for (String kbox : kboxx) {
				/* just retrieve it, initializing what needs to */
				IKBox kb = retrieveGlobalKBox(kbox);
				if (kb == null) {
					log.info("error: failed to open configured kbox " + kbox);
				} else {
					log.info("successfully opened kbox " + kbox);
				}
			}
		}
	}
	
	public KnowledgeManager(IKnowledgeRepository kr, IKnowledgeInterface ki) throws ThinklabException {

        /* set KM */
        KM = this;
        
        /* if any preferences must be loaded, better do it as soon as possible. */
        ki.importPreferences();
        
        /* create stuff */
        commands = new HashMap<String, CommandDeclaration>();
        actions  = new HashMap<String, IAction>();
        uri2cs   = new HashMap<String, String>();
        cs2uri   = new HashMap<String, String>();
        kboxPlugins = new HashMap<String, IKBoxPlugin>();
        kBoxes = new HashMap<String, IKBox>();

        instanceConstructors = new HashMap<String, IInstanceImplementationConstructor>();
        literalValidators = new HashMap<String, ILiteralValidator>();
        
        pluginRegistry = new PluginRegistry();
    
        knowledgeRepository = kr;
		knowledgeInterface  = ki;
		
		pluginClassPath = LocalConfiguration.getDataDirectory("plugins/load/classes");
		pluginJarPath = LocalConfiguration.getDataDirectory("plugins/load/lib");
	}
	
	/**
	 * If the km is only meant to be used within an application, use this one.
	 * @param fileKnowledgeRepository
	 * @throws ThinklabIOException
	 */
	public KnowledgeManager(IKnowledgeRepository knowledgeRepository) throws ThinklabException {
		this(knowledgeRepository, new APIOnlyKnowledgeInterface());
	}

	public IKnowledgeRepository getKnowledgeRepository() {
		return knowledgeRepository;
	}
	
	public IKnowledgeInterface getKnowledgeInterface() {
		return knowledgeInterface;
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
            
			// just leave types null, and complain when we have to.
		}

		/* register core concept managers, so we can declare classes as being Text or
		 * Number and their literals are handled properly.
		 *  
		 * TODO add a ConstraintManager (to be written) 
		 */
		registerLiteralValidator(textTypeID.toString(), new TextValidator());
		registerLiteralValidator(numberTypeID.toString(), new NumberValidator());
		
		typesInitialized = true;
	}
	
	public void initialize() throws ThinklabException {
	
		
		/* we always have at least the default plugin dir */
        pluginRegistry.addPluginDirectory(LocalConfiguration.getSystemDirectory("plugins"));

		/* link and initialize knowledge repository */
	    knowledgeRepository.initialize();
        
        /* see if the preferences override the thinklab core ontology URL */
        String cont = 
        	LocalConfiguration.getProperties().getProperty(
        			"thinklab.ontology.core", 
        			DEFAULT_CORE_ONTOLOGY);
 
        // FV have to put it back, as initializeThinklabTypes() needs the base types to work
        try {
            /* load the thinklab core ontology URL from preferences */
            knowledgeRepository.refreshOntology(new URL(cont), MiscUtilities.getNameFromURL(cont));
        } catch (MalformedURLException e1) {
        	throw new ThinklabIOException(e1);
        }
        
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
        
		pluginRegistry.initialize(pluginJarPath, pluginClassPath);
		
		// create class tree
		classTree = new KnowledgeTree();
		
		// open any kboxes installed in global properties
		installDefaultKboxes();
		
		// print the properties to the log.
		log.info("Properties used: "+ LocalConfiguration.getProperties());
	}

	public void run() {
		/* start the interface */
		knowledgeInterface.start();
	}
	
	public void shutdown() {
	
		/* finalize all plug-ins */
		
		/* flush knowledge repository */
        
        /* flush preferences */
        knowledgeInterface.savePreferences();
		
	}
    
	public void printBanner() {
			
			System.out.println("ThinkLab shell 0.1alpha");
			System.out.println("System path: " + LocalConfiguration.getSystemPath());
			System.out.println("Data path: " + LocalConfiguration.getDataPath());
			
			System.out.println();
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
	 * Register a plugin to handle a particular KBox protocol. Called upon initialization by
	 * KBoxPlugins.
	 * @param protocol
	 * @param plugin
	 */
	public void registerKBoxProtocol(String protocol, IKBoxPlugin plugin) {
		kboxPlugins.put(protocol, plugin);
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.IKnowledgeBase#retrieveGlobalKBox(java.lang.String)
	 */
	public IKBox retrieveGlobalKBox(String kboxURI) throws ThinklabException {
	    
		/* get the KBox URL */
		int dot = kboxURI.indexOf("#");
		if (dot >= 0)
			kboxURI = kboxURI.substring(0, dot);
		
		/* see if we have it already */
		IKBox ret = null;
		if (kboxURI.contains(":")) {
			ret = kBoxes.get(kboxURI);
		} else {
			String uri = null;
			/* see if we're using an unambiguous kbox name */
			for (String kb : kBoxes.keySet()) {
				if (MiscUtilities.getNameFromURL(kb).equals(kboxURI)) {
					if (uri == null) {
						uri = kb;
					} else {
						throw new ThinklabAmbiguousResultException(
								"identifier " + kboxURI + " specifies more than one kbox");
					}
				}
			}
			if (uri != null)
				ret = kBoxes.get(uri);
		}
		
		if (ret == null && kboxURI.contains(":")) {
			
			if (MiscUtilities.getFileExtension(kboxURI).equals("owl")) {
				return retrieveOntologyKBox(kboxURI);
			} 
			
			String protocol;
			
			try {
				protocol = new URI(kboxURI).getScheme();

				if (protocol.equals("kbox")) {
					
					ret = retrieveGenericKBox(kboxURI);

				} else {

					IKBoxPlugin plu = kboxPlugins.get(protocol);
					if (plu == null)
						return null;
					ret = plu.createKBoxFromURL(new URI(kboxURI));					
				}

				kBoxes.put(kboxURI.toString(), ret);

			} catch (Exception e) {
				throw new ThinklabStorageException(e);
			}
		}
		
		return ret;
	}

	/**
	 * Create an ontology kbox from a .owl file.
	 * TODO move to KBoxManager
	 * @param kboxURI
	 * @return
	 * @throws ThinklabException 
	 */
	private IKBox retrieveOntologyKBox(String kboxURI) throws ThinklabException {

		String kURI = 
			MiscUtilities.changeProtocol(kboxURI, "owl");

		IKBox ret = kBoxes.get(kURI); 
		
		if (ret == null) {

			ret = new OntologyKBox(kboxURI);
			kBoxes.put(kURI, ret);
		}
		
		return ret;
	
	}

	/**
	 * Retrieve a KBox identified by generic protocol "kbox", which requires the URL to point
	 * to a metadata (properties) document. This document is looked for first in the filesystem
	 * by changing "kbox" to "file"; if such a file does not exist, "http" is tried. The document
	 * must contain at least the "protocol" and "uri" properties. Any other property is considered
	 * a parameter. All are passed to the kbox initialize() function, after the kbox is initialized
	 * with the empty constructor.
	 * 
	 * TODO move to KBoxManager
	 * 
	 * @param kboxURI
	 * @return
	 */
	private IKBox retrieveGenericKBox(String kboxURI) throws ThinklabException {
	
		IKBox ret = null;
		URL sourceURL = null;
		InputStream input = null;
		
		try {
		
			/* see if we have a metadata document in the corresponding file: url */
			sourceURL = new URL("file" + kboxURI.substring(4));
			
			try {
				input = sourceURL.openStream();
			} catch (IOException e) {
				input = null;
			}
			
			if (input == null) {

				/* try http: */
				sourceURL =  new URL("http" + kboxURI.substring(4));

				try {
					input = sourceURL.openStream();
				} catch (IOException e) {
					input = null;
				}				
			}
		
		} catch (MalformedURLException e) {
		}
		
		if (input == null) {
			throw new ThinklabUndefinedKBoxException("url " + kboxURI + " does not point to a valid metadata document");
		}

		
		/* we have a metadata document; extract protocol, url, and all parameters */
		Properties properties = new Properties();
		
		try {
			properties.load(input);
		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}
		
		String protocol = properties.getProperty(IKBox.KBOX_PROTOCOL_PROPERTY);
		String dataUri = properties.getProperty(IKBox.KBOX_URI_PROPERTY);
		String ontologies = properties.getProperty(IKBox.KBOX_ONTOLOGIES_PROPERTY);
		String wrapperCls = properties.getProperty(IKBox.KBOX_WRAPPER_PROPERTY);
		
		log.info("opening kbox " + kboxURI + " with data uri " + dataUri);
		
		if (protocol == null || protocol.equals(""))
			throw new ThinklabUndefinedKBoxException("kbox metadata for " + kboxURI + " don't specify a protocol");
		
		/* handle "internal" protocol for OWL kboxes separately */
		if (protocol.equals("owl")) {
			return new OntologyKBox(dataUri);
		}
		
		/* load plugin for protocol; create kbox */
		IKBoxPlugin plu = kboxPlugins.get(protocol);
		if (plu == null)
			throw new ThinklabUndefinedKBoxException("kbox protocol " 
					+ protocol + " referenced in " +
					kboxURI + " is undefined");
		
		/* 
		 * see if kbox requires ontologies that are not loaded and import them as necessary. 
		 * FIXME these should probably be considered temporary and loaded in the current session, not
		 * imported. 
		 */
		if (ontologies != null && !ontologies.trim().equals("")) {
			String[] onts = ontologies.split(",");
			for (String ourl : onts) {
				try {
					this.knowledgeRepository.refreshOntology(new URL(ourl), null);
				} catch (MalformedURLException e) {
					throw new ThinklabIOException(e);
				}
			}
		}
		
		ret = plu.createKBox(protocol, dataUri, properties);
		
		if (ret != null && wrapperCls != null) {
			
			try {
				Class<?> cls = Class.forName(wrapperCls);
				KBoxWrapper wrapper = (KBoxWrapper) cls.newInstance();
				if (wrapper != null) {
					wrapper.initialize(ret);
					ret = wrapper;
				}
			} catch (Exception e) {
				throw new ThinklabIOException("kbox wrapper error: " + e.getMessage());
			}
			
		}
		
		return ret;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.IKnowledgeBase#requireGlobalKBox(java.lang.String)
	 */
	public IKBox requireGlobalKBox(String kboxURI) throws ThinklabException {
		
		IKBox ret = retrieveGlobalKBox(kboxURI);
		if (ret == null)
			throw new ThinklabResourceNotFoundException("URI " + kboxURI + " does not identify a valid kbox");
		return ret;
	}
	
	/**
	 * Register a command for use in the Knowledge Manager. The modality of invocation and execution of commands depends on the 
	 * particular IKnowledgeInterface installed. 
	 * @param command the CommandDeclaration to register
	 * @param action the Action executed in response to the command
	 * @throws ThinklabException
	 * @see CommandDeclaration
	 * @see IKnowledgeInterface
	 */
	public void registerCommand(CommandDeclaration command, IAction action) throws ThinklabException {
	    
		// TODO throw exception if command is installed
		commands.put(command.ID, command);
		actions.put(command.ID, action);
	}
	
	/**
	 * Register the class name of a session listener that we want to pass to any new session.

	 * @param sessionListenerClass
	 */
	public void registerSessionListenerClass(String sessionListenerClass) {
		sessionListeners.add(sessionListenerClass);
	}

	
	public void registerInstanceConstructor(String conceptID, IInstanceImplementationConstructor constructor) {
		
		this.instanceConstructors.put(conceptID, constructor);
		
	}
	
	public void registerLiteralValidator(String conceptID, ILiteralValidator validator) {
		
		validator.declareType();
		this.literalValidators.put(conceptID, validator);
	}
	
	public CommandDeclaration getDeclarationForCommand(String tok) {
			return commands.get(tok);
	}

	public CommandDeclaration requireDeclarationForCommand(String tok) throws ThinklabMalformedCommandException {
		CommandDeclaration cd = commands.get(tok);
		if (cd == null)
			throw new ThinklabMalformedCommandException("unknown command " + tok);
		return cd;
	}

	
	/**
	 * Check if a command with a particular name has been registered.
	 * @param commandID
	 * @return
	 */
	public boolean hasCommand(String commandID) {
		return actions.get(commandID) != null;
	}
	
	
	/**
	 * Submit and execute the passed command. Command is assumed validated so no checking
	 * is done. Returns a Value as result value, answered by execute() called on the corresponding
	 * action. 
	 * 
	 * @param cmd the command
	 * @param outputWriter
	 * @param session the session t
	 * @return a literal containing a result value and the associated concept, or null if the command is void.
	 * @throws ThinklabException if anything happens in command execution
	 */
	public IValue submitCommand(Command cmd, ICommandOutputReceptor outputWriter, ISession session) 
		throws ThinklabException {

		/* happens at times with botched commands (e.g., strange eof from shutting down the VM) */
		if (cmd == null || cmd.getDeclaration() == null)
			return null;

		IAction a = actions.get(cmd.getDeclaration().ID);		
		return a.execute(cmd, outputWriter, session, this);
		// TODO transfer to knowledge manager logging policy
	}
    
	/**
	 * Return the named plugin, or null if not found.
	 * 
	 * @param name
	 * @return
	 */
	public IPlugin retrievePlugin(String name) {
		return pluginRegistry.retrievePlugin(name);
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
    public ILiteralValidator getValidator(IConcept type) throws ThinklabException {

        class vmatch implements ConceptVisitor.ConceptMatcher {

            private HashMap<String, ILiteralValidator> coll;

            public ILiteralValidator ret = null;
            
            public vmatch(HashMap<String, ILiteralValidator> c) {
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
    public IInstanceImplementationConstructor getInstanceConstructor(IConcept type) throws ThinklabException{

        class vmatch implements ConceptVisitor.ConceptMatcher {

            private HashMap<String, IInstanceImplementationConstructor> coll;
            
            public vmatch(HashMap<String, IInstanceImplementationConstructor> c) {
                coll = c;
            }
            
            public boolean match(IConcept c) {
                IInstanceImplementationConstructor cc = coll.get(c.getSemanticType().toString());
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
        IInstanceImplementationConstructor cms = 
    	  new ConceptVisitor<IInstanceImplementationConstructor>().findMatchingInMapUpwards(instanceConstructors, new vmatch(instanceConstructors), type);
        
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

    
	public Collection<CommandDeclaration> getCommandDeclarations() {
		return commands.values();
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
		return knowledgeRepository.importOntology(u, name);
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

//	/** 
//	 * <p>We can only express base literal types in OWL, and even if we can extend RDF/S or framework such as Jena
//	 * to express other types, it's not easy to do so in a consistent and general way. For this reason, the IMA
//	 * has a notion of "reified literal": basically, complex types that need to be expressed as literals and have
//	 * a complex Java translation (such as polygons, time objects, etc.) can be made subclasses of the configured
//	 * ReifiedLiteral class. This gives them a text property (e.g. literal-value, also configured in the KM) that
//	 * can be linked to their text representation. Each time an instance of these is encountered, it is passed to
//	 * this function, which translates it into a Value and assigns the parent concept to it.</p>
//	 * @return the Value that results from the validation. Can't be null: either a good value is returned or a validation
//	 * 		   exception is thrown.
//	 * @throws ThinklabValidationException if anything is wrong with the instance
//	 */
//    @Deprecated
//	public Value translateReifiedLiteral(IInstance inst) throws ThinklabValidationException {
//		Value ret = null;
//		try {
//			IValue v = inst.getProperty(ReifiedLiteralPropertyID);
//			ret = validateLiteral(inst.getParent(), v.toString());
//		} catch (ThinklabException e) {
//			// translate any exception to validation
//			throw new ThinklabValidationException(e);
//		}
//		return ret;
//	}

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
		ILiteralValidator cm = null;
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
		
		ISession session = knowledgeInterface.createNewSession();

		for (String s : sessionListeners) {
			
			IThinklabSessionListener listener = null;
			
			try {
				Class<?> lcl = Class.forName(s, true, PluginRegistry.get().getClassLoader());
				listener = (IThinklabSessionListener) lcl.newInstance();
			} catch (ClassNotFoundException e) {
				throw new ThinklabMissingResourceException("cannot create requested session listener " + s + ": class not found");
			} catch (Exception e) {
				throw new ThinklabMissingResourceException("cannot create requested session listener " + s + ": error during creation");
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
		
		knowledgeInterface.notifySessionDeletion(session);
	}

	
    /**
     * Get the plugin registry.
     * @return
     */
	public PluginRegistry getPluginRegistry() {
		return pluginRegistry;
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

	public Collection<IPlugin> retrievePlugins() {
		return pluginRegistry.getPlugins();
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.IKnowledgeBase#getInstalledKboxes()
	 */
	public Collection<String> getInstalledKboxes() {
		return kBoxes.keySet();
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
	public IKnowledgeLoaderPlugin getLoaderPlugin(String format) {

		IKnowledgeLoaderPlugin ret = null;
		
		for (IPlugin plu : pluginRegistry.getPlugins()) {
			if (plu instanceof IKnowledgeLoaderPlugin &&
					((IKnowledgeLoaderPlugin)plu).handlesFormat(format)) {
				ret = (IKnowledgeLoaderPlugin) plu;
				break;
			}
		}
		
		return ret;
	}
	
	/**
	 * Use before initialize() to redefine the locations where class and 
	 * jar files in plugins are unpacked by the plugin manager. Thinklab has
	 * its own classloader that is extended to look into these locations, but 
	 * if the VM the application runs under needs to link plugin classes, this
	 * will need to be redefined appropriately before initialize() is called.
	 * 
	 * @param classPath
	 * @param jarClassPath
	 */
	public void setPluginClasspaths(File classPath, File jarClassPath) {
		pluginClassPath = classPath;
		pluginJarPath = jarClassPath;
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

}