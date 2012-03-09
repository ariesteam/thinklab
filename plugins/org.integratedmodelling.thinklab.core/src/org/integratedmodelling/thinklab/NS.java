package org.integratedmodelling.thinklab;

/**
 * One and only holder of semantic concept IDs. These will all be expected to exist.
 * 
 *  TODO complete refactoring of all core types. 
 * 
 * @author Ferd
 *
 */
public class NS {

	public static final String METADATA = "metadata:Metadata";
	public static final String METADATA_HAS_FIELD = "metadata:hasField";
	public static final String INTEGER = "thinklab:ShortInteger";
	public static final String FLOAT = "thinklab:FloatingPoint";
	public static final String TEXT = "thinklab:Text";
	public static final String LONG = "thinklab:Long";
	public static final String DOUBLE = "thinklab:LongFloatingPoint";
	public static final String BOOLEAN = "thinklab:Boolean";
	public static final String NUMBER = "thinklab:Number";
	public static final String BOOLEAN_RANKING = "thinklab:BooleanRanking";
	public static final String ORDINAL_RANKING = "thinklab:OrdinalRanking";
	public static final String NUMERIC_INTERVAL = "thinklab:NumericInterval";
	public static final String ORDINAL_RANGE_MAPPING = "thinklab:OrderedRangeMapping";
	public static final String PAIR = "thinklab:Pair";
	public static final String TRIPLE = "thinklab:Triple";
	public static final String KEY_VALUE_PAIR = "thinklab:KeyValuePair";
	
	public static final String CLASSIFICATION_PROPERTY = "thinklab:isClassification";
	public static final String ABSTRACT_PROPERTY = "thinklab:isAbstract";
	public static final String HAS_FIRST_FIELD = "thinklab:hasFirstField";
	public static final String HAS_SECOND_FIELD = "thinklab:hasSecondField";
	public static final String HAS_THIRD_FIELD = "thinklab:hasThirdField";

	public static final String OBSERVATION = "observation:Observation";
	
	public static final String MODELLING_MODEL = "observation.modelling:Model";
	public static final String MODELLING_NAMESPACE = "observation.modelling:Namespace";
	public static final String MODELLING_HAS_OBSERVABLE = "observation.modelling:hasObservable";
	public static final String MODELLING_HAS_ID = "observation.modelling:hasId";
	public static final String MODELLING_HAS_TIMESTAMP = "observation.modelling:hasTimestamp";
	public static final String MODELLING_HAS_DATASOURCE = "observation.modelling:hasDataSource";

	public static final String GEOSPACE_HAS_SERVICE_URL = "geospace:hasServiceUrl";
	public static final String GEOSPACE_HAS_COVERAGE_ID = "geospace:hasCoverageId";
	public static final String GEOSPACE_HAS_VALUE_ATTRIBUTE = "geospace:hasValueAttribute";
	public static final String GEOSPACE_HAS_VALUE_TYPE = "geospace:hasValueType";
	public static final String GEOSPACE_HAS_VALUE_EXPRESSION = "geospace:hasValueExpression";
	public static final String GEOSPACE_HAS_VALUE_DEFAULT = "geospace:hasValueDefault";
	
}
