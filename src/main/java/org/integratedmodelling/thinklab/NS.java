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

	public static final String POLYGON = "geospace:PolygonValue";
	public static final String POINT = "geospace:PointValue";
	public static final String LINE = "geospace:LineValue";
	
	// standard operations
	public static final String OPERATION_EQUALS = "thinklab:Equal";
	public static final String OPERATION_GREATER_OR_EQUAL = "thinklab:GreaterEqual";
	public static final String OPERATION_GREATER_THAN = "thinklab:GreaterThan";
	public static final String OPERATION_LESS_OR_EQUAL = "thinklab:LowerEqual";
	public static final String OPERATION_LESS_THAN = "thinklab:LowerThan";
	public static final String OPERATION_NOT_EQUALS = "thinklab:NotEqual";
	public static final String OPERATION_LIKE = "thinklab:MatchesPattern";

	// topological operators and other specifically for space and time
	static final public String OPERATION_CROSSES = "thinklab:Crosses";
	static final public String OPERATION_INTERSECTS = "thinklab:Intersects";
	static final public String OPERATION_INTERSECTS_ENVELOPE = "thinklab:IntersectsEnvelope";
	static final public String OPERATION_COVERS = "thinklab:Covers";
	static final public String OPERATION_COVERED_BY = "thinklab:IsCoveredBy";
	static final public String OPERATION_OVERLAPS = "thinklab:Overlaps";
	static final public String OPERATION_TOUCHES = "thinklab:Touches";
	static final public String OPERATION_CONTAINS = "thinklab:Contains";
	static final public String OPERATION_CONTAINED_BY = "thinklab:IsContainedBy";
	static final public String OPERATION_NEAREST_NEIGHBOUR = "thinklab:NearestNeighbour";
	static final public String OPERATION_WITHIN_DISTANCE = "thinklab:WithinDistance";

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

	// conceptualized extents
	public static final String GRID_EXTENT = "geospace:GridExtent";
	
	// spatial convenience relationships
	public static final String GEOSPACE_HAS_SERVICE_URL = "geospace:hasServiceUrl";
	public static final String GEOSPACE_HAS_COVERAGE_ID = "geospace:hasCoverageId";
	public static final String GEOSPACE_HAS_VALUE_ATTRIBUTE = "geospace:hasValueAttribute";
	public static final String GEOSPACE_HAS_VALUE_TYPE = "geospace:hasValueType";
	public static final String GEOSPACE_HAS_VALUE_EXPRESSION = "geospace:hasValueExpression";
	public static final String GEOSPACE_HAS_VALUE_DEFAULT = "geospace:hasValueDefault";
	public static final String GEOSPACE_HAS_MINX = "geospace:hasMinX";
	public static final String GEOSPACE_HAS_MAXX = "geospace:hasMaxX";
	public static final String GEOSPACE_HAS_MAXY = "geospace:hasMaxY";
	public static final String GEOSPACE_HAS_MINY = "geospace:hasMinY";
	public static final String GEOSPACE_HAS_XDIVS = "geospace:hasXDivs";
	public static final String GEOSPACE_HAS_YDIVS = "geospace:hasYDivs";
	public static final String GEOSPACE_HAS_SHAPE = "geospace:hasShape";
	public static final String GEOSPACE_HAS_CRSCODE = "geospace:hasCRSCode";

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

	// properties for model beans
	public static final String HAS_DEPENDENCY = "modelling.thinklab:hasDependency";
	public static final String HAS_OBSERVABLE = "modelling.thinklab:hasObservable";
	public static final String HAS_TIMESTAMP = "modelling.thinklab:hasTimeStamp";
	public static final String HAS_OBSERVER = "modelling.thinklab:hasObserver";
	public static final String HAS_ID = "modelling.thinklab:hasId";
	public static final String HAS_NAMESPACE_ID = "modelling.thinklab:hasNamespaceId";
	public static final String HAS_EXPRESSION = "modelling.thinklab:hasExpression";
	public static final String HAS_DATASOURCE_DEFINITION = "modelling.thinklab:hasDatasourceDefinition";
	public static final String HAS_MEDIATED_OBSERVER = "modelling.thinklab:hasMediatedObserver";
	public static final String HAS_TYPE = "modelling.thinklab:hasType";
	public static final String HAS_PARAMETERS = "modelling.thinklab:hasParameters";
	public static final String HAS_INLINE_STATE = "modelling.thinklab:hasInlineState";
	public static final String HAS_UNIT_DEFINITION = "modelling.thinklab:hasUnitDefinition";
	public static final String HAS_MODEL = "modelling.thinklab:hasModel";
	public static final String HAS_EXTENT_FUNCTION = "modelling.thinklab:hasExtentFunction";
	public static final String HAS_ACCESSOR_FUNCTION = "modelling.thinklab:hasAccessorFunction";
	public static final String HAS_DIRECT_DATA = "modelling.thinklab:hasDirectData";
	public static final String HAS_ALLOWED_COVERAGE = "modelling.thinklab:hasAllowedCoverage";



}
