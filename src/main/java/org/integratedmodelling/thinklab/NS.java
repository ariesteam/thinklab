package org.integratedmodelling.thinklab;


/**
 * One and only holder of semantic concept IDs. These will all be expected to exist.
 * 
 * TODO complete refactoring of all core types. 
 * 
 * @author Ferd
 *
 */
public class NS {
	
	/**
	 * Ontologies created by loading knowledge from thinklab modeling
	 * language files get URLs with this prefix unless a different one
	 * is specified in project properties.
	 */
	public static final String DEFAULT_THINKLAB_ONTOLOGY_PREFIX = 
			"http://www.integratedmodelling.org/ks";
	
	// the semantic basics
	public static final String THING   = "owl:Thing";
	public static final String NOTHING = "owl:Nothing";

	// core objects that everyone will need
	public static final String INTEGER = "thinklab:ShortInteger";
	public static final String FLOAT = "thinklab:FloatingPoint";
	public static final String TEXT = "thinklab:Text";
	public static final String LONG = "thinklab:LongInteger";
	public static final String DOUBLE = "thinklab:LongFloatingPoint";
	public static final String BOOLEAN = "thinklab:Boolean";
	public static final String NUMBER = "thinklab:Number";

	// standard operations
	public static final String OPERATION_EQUALS = "thinklab:Equal";
	public static final String OPERATION_GREATER_OR_EQUAL = "thinklab:GreaterEqual";
	public static final String OPERATION_GREATER_THAN = "thinklab:GreaterThan";
	public static final String OPERATION_LESS_OR_EQUAL = "thinklab:LowerEqual";
	public static final String OPERATION_LESS_THAN = "thinklab:LowerThan";
	public static final String OPERATION_NOT_EQUALS = "thinklab:NotEqual";
	public static final String OPERATION_LIKE = "thinklab:MatchesPattern";

	// observable types
	public static final String BOOLEAN_RANKING = "thinklab:BooleanRanking";
	public static final String ORDINAL_RANKING = "thinklab:OrdinalRanking";
	public static final String NUMERIC_INTERVAL = "thinklab:NumericInterval";
	public static final String ORDINAL_RANGE_MAPPING = "thinklab:OrderedRangeMapping";
	
	// convenience collections
	public static final String PAIR = "thinklab:Pair";
	public static final String TRIPLE = "thinklab:Triple";
	public static final String KEY_VALUE_PAIR = "thinklab:KeyValuePair";
	
	// core and convenience properties
	public static final String CLASSIFICATION_PROPERTY = "thinklab:isClassification";
	public static final String ABSTRACT_PROPERTY = "thinklab:isAbstract";
	public static final String HAS_FIRST_FIELD = "thinklab:hasFirst";
	public static final String HAS_SECOND_FIELD = "thinklab:hasSecond";
	public static final String HAS_THIRD_FIELD = "thinklab:hasThird";
	public static final String METADATA_HAS_FIELD = "metadata:hasField";

	// spatial convenience relationships
	public static final String GEOSPACE_HAS_SERVICE_URL = "geospace:hasServiceUrl";
	public static final String GEOSPACE_HAS_COVERAGE_ID = "geospace:hasCoverageId";
	public static final String GEOSPACE_HAS_VALUE_ATTRIBUTE = "geospace:hasValueAttribute";
	public static final String GEOSPACE_HAS_VALUE_TYPE = "geospace:hasValueType";
	public static final String GEOSPACE_HAS_VALUE_EXPRESSION = "geospace:hasValueExpression";
	public static final String GEOSPACE_HAS_VALUE_DEFAULT = "geospace:hasValueDefault";
	
	// model beans
	public static final String METADATA = "modelling.thinklab:Metadata";
	public static final String NAMESPACE = "modelling.thinklab:Namespace";
	public static final String AGENT_MODEL = "modelling.thinklab:AgentModel";
	public static final String CONTEXT = "modelling.thinklab:Context";
	public static final String OBSERVER = "modelling.thinklab:Observer";
	public static final String CATEGORIZING_OBSERVER = "modelling.thinklab:CategorizingObserver";
	public static final String MEASURING_OBSERVER = "modelling.thinklab:MeasuringObserver";
	public static final String CLASSIFYING_OBSERVER = "modelling.thinklab:ClassifyingObserver";
	public static final String RANKING_OBSERVER = "modelling.thinklab:RankingObserver";
	public static final String VALUING_OBSERVER = "modelling.thinklab:ValuingObserver";
	public static final String CONDITIONAL_OBSERVER = "modelling.thinklab:ConditionalObserver";
	public static final String MODEL = "modelling.thinklab:Model";
	public static final String SCENARIO = "modelling.thinklab:Scenario";
	public static final String STORYLINE = "modelling.thinklab:Storyline";
	public static final String UNIT_DEFINITION = "modelling.thinklab:UnitDefinition";
	public static final String DATASOURCE_DEFINITION = "modelling.thinklab:DataSourceDefinition";
	public static final String CONCEPT_DEFINITION = "modelling.thinklab:ConceptDefinition";
	public static final String PROPERTY_DEFINITION = "modelling.thinklab:PropertyDefinition";
	public static final String EXPRESSION_DEFINITION = "modelling.thinklab:ExpressionDefinition";
	public static final String FUNCTION_DEFINITION = "modelling.thinklab:FunctionDefinition";
	public static final String LANGUAGE_ELEMENT = "modelling.thinklab:LanguageElement";
	public static final String MODEL_OBJECT = "modelling.thinklab:ModelObject";
	public static final String OBSERVING_OBJECT = "modelling.thinklab:ObservingObject";	
	public static final String OBSERVATION = "modelling.thinklab:Observation";

	public static final String HAS_OBSERVABLE = "observation:hasObserver";


}
