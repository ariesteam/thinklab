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
	public static final String INTEGER = "thinklab:Integer";
	public static final String FLOAT = "thinklab:Float";
	public static final String TEXT = "thinklab:Text";
	public static final String LONG = "thinklab:Long";
	public static final String DOUBLE = "thinklab:Double";
	public static final String BOOLEAN = "thinklab:Boolean";
	public static final String NUMBER = "thinklab:Number";
	public static final String BOOLEAN_RANKING = "thinklab:BooleanRanking";
	public static final String ORDINAL_RANKING = "thinklab:OrdinalRanking";
	public static final String NUMERIC_INTERVAL = "thinklab:NumericInterval";
	
	public static final String ORDINAL_RANGE_MAPPING = "thinklab:OrdinalRangeMapping";
	public static final String CLASSIFICATION_PROPERTY = "thinklab:isClassification";
	public static final String ABSTRACT_PROPERTY = "thinklab:isAbstract";

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
