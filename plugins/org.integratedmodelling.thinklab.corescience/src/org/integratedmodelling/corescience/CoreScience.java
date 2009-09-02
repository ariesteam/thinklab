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

import java.util.Arrays;
import java.util.HashMap;

import org.integratedmodelling.corescience.interfaces.context.IContextualizationCompiler;
import org.integratedmodelling.corescience.interfaces.observation.IObservation;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabPluginException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.plugin.ThinklabPlugin;

import umontreal.iro.lecuyer.probdistmulti.MultinomialDist;

/**
 * @author Ferdinando Villa
 */
public class CoreScience extends ThinklabPlugin {

	private final static String PLUGIN_ID = "org.integratedmodelling.thinklab.corescience";
	
	/**
	 * TODO fix this, we need to build a new one per request
	 */
	private HashMap<String, String> compilerClasses = new HashMap<String,String>();

	private IConcept DiscreteNumericRankingSpace;
	private IConcept MeasurementSpace;
	private IConcept MeasurementType;
	private IConcept RankingType;
	private IConcept RandomValueType;
	private IConcept ContinuousDistributionType;
	private IConcept DiscreteDistributionType;
	private IConcept ObservationType;
	
	// properties
	public static final String DEPENDS_ON = "observation:dependsOn";
	public static final String HAS_DATASOURCE = "observation:hasDataSource";
	public static final String HAS_CONTINGENCY = "observation:isContingentTo";
	public static final String HAS_CONTEXT ="observation:hasObservationContext";
	public static final String HAS_EXTENT ="observation:hasObservationExtent";
	public static final String MEDIATES_OBSERVATION ="observation:mediates";
	public static final String HAS_OBSERVABLE = "observation:hasObservable";
	public static final String HAS_CONCEPTUAL_MODEL = "observation:hasConceptualModel";
	public static final String HAS_SOURCE_URI = "source:hasSourceURI";
	public static final String HAS_FILTER = "source:hasFilter";
	public static final String HAS_UNIT = "measurement:hasUnit";
	public static final String HAS_CLASS_MAPPING = "observation:hasClassMapping";
	public static final String HAS_SOURCE_VALUE_TYPE = "observation:sourceValueType";
	public static final String HAS_CONCEPTUAL_SPACE = "observation:hasConceptSpace";
	public static final String HAS_SOURCE_VALUE = "observation:sourceValue";
	public static final String HAS_TARGET_CONCEPT = "observation:targetConcept";
	public static final String HAS_TARGET_INSTANCE = "observation:targetInstance";
	public static final String HAS_TARGET_CLASS = "observation:targetClass";
	public static final String HAS_INTERVAL = "observation:hasInterval";
	public static final String HAS_FORMAL_NAME = "observation:hasFormalName";


	// concepts
	public static final String OBSERVATION = "observation:Observation";
	public static final String RANKING = "measurement:Ranking";
	public static final String IDENTIFICATION = "observation:Identification";
	public static final String INDIRECT_OBSERVATION = "observation:IndirectObservation";
	public static final String EXCEL_CSV_DATASOURCE = "source:ExcelCSVDataSource";
	public static final String MEASUREMENT = "measurement:Measurement";
	public static final String CSV_DATASOURCE = "source:CSVDataSource";
	public static final String RANDOM_DATASOURCE = "source:Randomizer";
	public static final String RANDOM_VALUE = "observation:RandomValue";
	public static final String RANDOM_OBSERVATION = "observation:RandomObservation";
	public static final String CONTINUOUS_DISTRIBUTION = "observation:ContinuousDistribution";
	public static final String DISCRETE_DISTRIBUTION = "observation:DiscreteDistribution";
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
	public static final String CONTEXTUALIZED_DATASOURCE = "observation:ContextualizedDataSource";
	public static final String RANKING_SET_REMAPPER = "measurement:RankingSetRemapper";
	public static final String RANKING_INTERVAL_REMAPPER = "measurement:RankingIntervalRemapper";
	public static final String PROBABILISTIC_CLASSIFICATION = "observation:ProbabilisticClassification";
	
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
			RandomValueType = km.requireConcept(RANDOM_VALUE);
			ContinuousDistributionType = km.requireConcept(CONTINUOUS_DISTRIBUTION);
			DiscreteDistributionType = km.requireConcept(DISCRETE_DISTRIBUTION);
			ObservationType = km.requireConcept(OBSERVATION);
			MeasurementType = km.requireConcept(MEASUREMENT);
			RankingType = km.requireConcept(RANKING);
			
		} catch (Exception e) {
			throw new ThinklabPluginException(e);
		}
		
		registerCompiler(
				"default", 
				"org.integratedmodelling.corescience.contextualization.VMCompiler");
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.ima.core.plugin.Plugin#unload(org.integratedmodelling.ima.core.KnowledgeManager)
	 */
	@Override
	public void unload() throws ThinklabPluginException {
	}

	/**
	 * TODO define as extension
	 * @see registerModelLoader
	 */
	public IContextualizationCompiler getContextualizationCompiler(String id, IObservation obs) throws ThinklabPluginException {
		
		IContextualizationCompiler ret = null;
		String cclass = null;
		
		if (id != null) {
			cclass = compilerClasses.get(id);			
		} else if (obs != null){
			for (String s : compilerClasses.values()) {
				
				IContextualizationCompiler cc;
				try {
					cc = (IContextualizationCompiler) Class.forName(s, true, getClassLoader()).newInstance();
				} catch (Exception e) {
					throw new ThinklabPluginException(
							"corescience: cannot create compiler for class " + s);
				}
				if (cc.canCompile(obs)) {
					ret = cc;
					break;
				}
			}
		}
		if (ret == null && cclass != null)
			try {
				ret =
					(IContextualizationCompiler) Class.forName(cclass, true, getClassLoader()).newInstance();
			} catch (Exception e) {
				throw new ThinklabPluginException("corescience: cannot create compiler for class " + cclass);
			}
		return ret;
	}
	
	/**
	 * Register a constructor for a new model loader.
	 * 
	 * @param id
	 * @param constructor
	 */
	public void registerCompiler(String id, String compilerClass) {
		compilerClasses.put(id, compilerClass);
	}

	public static IConcept DiscreteRankingModel() {
		return get().DiscreteNumericRankingSpace;
	}

	public static IConcept MeasurementModel() {
		return get().MeasurementSpace;
	}

	public static IConcept RandomValue() {
		return get().RandomValueType;
	}

	public static IConcept ContinuousDistribution() {
		return get().ContinuousDistributionType;
	}

	public static IConcept DiscreteDistribution() {
		return get().DiscreteDistributionType;
	}

	public static IConcept Observation() {
		return get().ObservationType;
	}

	public static IConcept Measurement() {
		return get().MeasurementType;
	}

	public static IConcept Ranking() {
		return get().RankingType;
	}

}
