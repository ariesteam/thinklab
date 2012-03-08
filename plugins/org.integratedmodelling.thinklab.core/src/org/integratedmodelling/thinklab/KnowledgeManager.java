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
package org.integratedmodelling.thinklab;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabIOException;
import org.integratedmodelling.exceptions.ThinklabResourceNotFoundException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.lang.SemanticType;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IProperty;
import org.integratedmodelling.thinklab.api.modelling.INamespace;
import org.integratedmodelling.thinklab.command.CommandManager;
import org.integratedmodelling.thinklab.interfaces.IKnowledgeRepository;
import org.integratedmodelling.thinklab.modelling.ModelManager;
import org.integratedmodelling.thinklab.plugin.IPluginLifecycleListener;
import org.java.plugin.PluginManager;

/**
 * Main knowledge manager functionalities. Should be merged into Thinklab which appropriately
 * implements IKnowledgeManaged. Must clean up first.
 * 
 * @author Ferd
 * 
 */
public class KnowledgeManager {

	/** 
	 * <p>The Knowledge Manager is a singleton. This is created by the initializer and an exception 
	 * is thrown if a second one has been initialized. </p>
     * 
     * <p>Note that this may change in the future. Having multiple kms is interesting for sophisticated
     * applications (e.g. cross-reasoning) but that's for another time.</p>
     * 
	 */
	public static KnowledgeManager KM = null;
	
	IConcept integerType;
	IConcept floatType;	
	IConcept textType;
	IConcept numberType;
	IConcept booleanType;
	IConcept longType;
	IConcept doubleType;
	IConcept ordinalRankingType;
	IConcept booleanRankingType;
	IConcept ordinalRangeMappingType;
 
    private IProperty classificationProperty;
    private IProperty abstractProperty;
	private IProperty additionalRestrictionsProperty;
    
    private SemanticType rootTypeID;

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

	/**
	 * This should become the default constructor: the class of knowledge repository and session
	 * manager is stated in the properties, defaulting to the ones we trust.
	 * 
	 * @param fileKnowledgeRepository
	 * @throws ThinklabIOException
	 */
	public KnowledgeManager() throws ThinklabException {
		
		KM = this;

		String krClass = 
			Thinklab.get().getProperties().getProperty(
				"thinklab.repository.class",
				"org.integratedmodelling.thinklab.owlapi.FileKnowledgeRepository");
		
		IKnowledgeRepository kr = null;
		
		Class<?> cls = null;
		try {
			cls = Thinklab.get().getClassLoader().loadClass(krClass);
			kr =  (IKnowledgeRepository) cls.newInstance();

		} catch (Exception e) {
			throw new ThinklabValidationException(e);
		}
		
		knowledgeRepository = kr;
	}

	public IKnowledgeRepository getKnowledgeRepository() {
		return knowledgeRepository;
	}
	
    public IConcept getRootConcept() {
        return knowledgeRepository.getRootConcept();
    }
    
	public IProperty getAbstractProperty() throws ThinklabException {
		if (!typesInitialized)
			initializeThinklabTypes();
		return abstractProperty;
	}

	public IProperty getClassificationProperty() throws ThinklabException {
		if (!typesInitialized)
			initializeThinklabTypes();
		return classificationProperty;
	}

	private void initializeThinklabTypes() throws ThinklabException {

		integerType  = requireConcept(NS.INTEGER);
		floatType    = requireConcept(NS.FLOAT);
		textType     = requireConcept(NS.TEXT);
		longType     = requireConcept(NS.LONG);
		doubleType   = requireConcept(NS.DOUBLE);
		numberType   = requireConcept(NS.NUMBER);
		booleanType  = requireConcept(NS.BOOLEAN);
		booleanRankingType  = requireConcept(NS.BOOLEAN_RANKING);
		ordinalRankingType  = requireConcept(NS.ORDINAL_RANKING);
		ordinalRangeMappingType  = requireConcept(NS.ORDINAL_RANGE_MAPPING);
		
		classificationProperty = requireProperty(NS.CLASSIFICATION_PROPERTY);
		abstractProperty = requireProperty(NS.ABSTRACT_PROPERTY);
				
		typesInitialized = true;
	}
	
	public void initialize() throws ThinklabException {
	
		/* link and initialize knowledge repository */
	    knowledgeRepository.initialize();
	    
	    // TODO  this must be put outside but only after the KM is first initialized, or better, in Thinklab.
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
		
		if (KM == null) {
			try {
				KM = new KnowledgeManager();
				KM.initialize();
			} catch (ThinklabException e) {
				throw new ThinklabRuntimeException(e);
			}
		}
		return KM;
	}

	public IConcept requireConcept(String id) throws ThinklabException {
    	
    	SemanticType st = new SemanticType(id);
    	return requireConcept(st);
    }
 
	public IProperty requireProperty(String id) throws ThinklabException {
 
    	SemanticType st = new SemanticType(id);
        return requireProperty(st);
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
    
	public void clear() {
		knowledgeRepository.releaseAllOntologies();
	}

	public void clear(String id) {
		knowledgeRepository.releaseOntology(id);
	}
	
	public IConcept retrieveConcept(SemanticType t) {

		IConcept ret = null;
		INamespace ns = ModelManager.get().getNamespace(t.getConceptSpace());
	    if (ns != null)
	    	ret = ns.getOntology().getConcept(t.getLocalName());
	    if (ret == null && t.toString().equals(rootTypeID.toString()))	{
	    	ret = getRootConcept();
	    }
	    return ret;
	}

	public IProperty retrieveProperty(SemanticType t) {
		
		IProperty ret = null;
		INamespace ns = ModelManager.get().getNamespace(t.getConceptSpace());
	    if (ns != null)
	    	ret = ns.getOntology().getProperty(t.getLocalName());
	    return ret;
	}
	
	public IProperty retrieveProperty(String prop) {
		return retrieveProperty(new SemanticType(prop));
	}
	
	public IConcept retrieveConcept(String conc) {
				
		IConcept ret = null;

		return 
			ret == null ?
				retrieveConcept(new SemanticType(conc)) :
				ret;
	}

	public IConcept getLeastGeneralCommonConcept(String semanticType, String otherConcept) throws ThinklabException {
		return getLeastGeneralCommonConcept(requireConcept(semanticType), requireConcept(otherConcept));
	}

	public IConcept getLeastGeneralCommonConcept(IConcept concept1, IConcept c) {
		return concept1.getLeastGeneralCommonConcept(c);
	}
	
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
	/**
	 * Provided to simplify access to core types when we are sure that we have a knowledge
	 * manager.
	 * @return
	 */
	public static IConcept NumberType() {
		return KM.numberType;
	}
	
	public static IConcept ThingType() {
		return KM.knowledgeRepository.getRootConcept();
	}


	public IProperty getAdditionalRestrictionProperty() {
		// TODO Auto-generated method stub
		return additionalRestrictionsProperty;
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

}
