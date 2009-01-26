/**
 * CoreSciencePlugin.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabCoreSciencePlugin.
 * 
 * ThinklabCoreSciencePlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabCoreSciencePlugin is distributed in the hope that it will be useful,
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
 * @date      Jan 17, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.corescience;

import java.util.HashMap;

import org.integratedmodelling.corescience.interfaces.IWorkflowConstructor;
import org.integratedmodelling.corescience.interfaces.context.IContextualizationWorkflow;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabMalformedSemanticTypeException;
import org.integratedmodelling.thinklab.exception.ThinklabPluginException;
import org.integratedmodelling.thinklab.exception.ThinklabResourceNotFoundException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.plugin.ThinklabPlugin;

/**
 * @author Ferdinando Villa
 *
 */
public class CoreScience extends ThinklabPlugin {

	private final static String PLUGIN_ID = "org.integratedmodelling.thinklab.corescience";
	
	private HashMap<String, IWorkflowConstructor> modelLoaders = new HashMap<String, IWorkflowConstructor>();

	private IConcept DiscreteNumericRankingSpace;
	private IConcept MeasurementSpace;

	
	// properties
	public static final String DEPENDS_ON = "observation:dependsOn";
	public static final String HAS_DATASOURCE = "observation:hasDataSource";
	public static final String HAS_CONTINGENCY = "observation:isContingentTo";
	public static final String HAS_CONTEXT ="observation:hasObservationContext";
	public static final String HAS_EXTENT ="observation:hasObservationExtent";
	public static final String HAS_OBSERVABLE = "observation:hasObservable";
	public static final String HAS_CONCEPTUAL_MODEL = "observation:hasConceptualModel";
	public static final String HAS_SOURCE_URI = "source:hasSourceURI";
	public static final String HAS_FILTER = "source:hasFilter";
	public static final String HAS_UNIT = "measurement:hasUnit";
	public static final String HAS_CLASS_MAPPING = "observation:hasClassMapping";
	public static final String HAS_SOURCE_VALUE_TYPE = "observation:sourceValueType";
	public static final String HAS_CONCEPTUAL_SPACE = "observation:conceptualSpace";
	public static final String HAS_SOURCE_VALUE = "observation:sourceValue";
	public static final String HAS_TARGET_CONCEPT = "observation:targetConcept";
	public static final String HAS_TARGET_INSTANCE = "observation:targetInstance";

	
	/*
	 * for now we leave state out of the ontology, only as a product of contextualization
	 public static final String HAS_OBSERVATION_STATE = "observation:hasState";
	*/
	// concepts
	public static final String OBSERVATION = "observation:Observation";
	public static final String RANKING = "measurement:Ranking";
	public static final String IDENTIFICATION = "observation:Identification";
	public static final String INDIRECT_OBSERVATION = "observation:IndirectObservation";
	public static final String EXCEL_CSV_DATASOURCE = "source:ExcelCSVDataSource";
	public static final String MEASUREMENT = "measurement:Measurement";
	public static final String CSV_DATASOURCE = "source:CSVDataSource";
	public static final String RANDOM_DATASOURCE = "source:Randomizer";
	public static final String UNIT = "measurement:Unit";
	public static final String PARSED_STRING = "source:ParsedString";
	public static final String COLUMN_EXTRACTOR = "source:ColumnExtractor";
	public static final String PHYSICAL_PROPERTY = "representation:PhysicalProperty";
	public static final String EXTENSIVE_PHYSICAL_PROPERTY = "representation:ExtensivePhysicalProperty";
	public static final String EXTENT_OBSERVATION = "observation:ExtentObservation";
	public static final String DATASOURCE_FUNCTION_LITERAL = "source:hasFunctionLiteral";
	public static final String CLASSIFICATION_MODEL = "observation:ClassificationSpace";
	public static final String DISCRETE_RANKING_MODEL = "observation:DiscreteNumericRankingSpace";
	public static final String CLASS_MAPPING = "observation:ClassMapping";
	
	static final public String GENERIC_OBSERVABLE = "representation:GenericObservable";
	static final public String GENERIC_QUANTIFIABLE = "representation:GenericQuantifiable";

	public static CoreScience get() {
		return (CoreScience) getPlugin(PLUGIN_ID );
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.ima.core.plugin.Plugin#load(org.integratedmodelling.ima.core.KnowledgeManager, java.io.File, java.io.File)
	 */
	@Override
	public void load(KnowledgeManager km) throws ThinklabPluginException {
		
		try {
			DiscreteNumericRankingSpace = km.requireConcept(DISCRETE_RANKING_MODEL);
			MeasurementSpace = km.requireConcept(UNIT);
		} catch (Exception e) {
			throw new ThinklabPluginException(e);
		}
	}


	/* (non-Javadoc)
	 * @see org.integratedmodelling.ima.core.plugin.Plugin#unload(org.integratedmodelling.ima.core.KnowledgeManager)
	 */
	@Override
	public void unload() throws ThinklabPluginException {
	}

	/**
	 * TODO move to extension points
	 * 
	 * Construct a new model loader for passed type and return it. If no such loader type is registered, return
	 * null without complaining.
	 *  
	 * @param id the type of model loader desired. Must match a constructor registered with registerModelLoader.
	 * @return a new model loader of a type matching the passed id.
	 * @see registerModelLoader
	 */
	public IContextualizationWorkflow retrieveWorkflow(String id) {
		
		IContextualizationWorkflow ret = null;
		IWorkflowConstructor mc = modelLoaders.get(id);
		if (mc != null)
			ret = mc.createWorkflow();
		return ret;
	}
	
	/**
	 * Register a constructor for a new model loader.
	 * 
	 * @param id
	 * @param constructor
	 */
	public void registerWorkflow(String id, IWorkflowConstructor constructor) {
		modelLoaders.put(id, constructor);
	}

	public IConcept DiscreteRankingModel() {
		return this.DiscreteNumericRankingSpace;
	}

	public IConcept MeasurementModel() {
		return this.MeasurementSpace;
	}
}
