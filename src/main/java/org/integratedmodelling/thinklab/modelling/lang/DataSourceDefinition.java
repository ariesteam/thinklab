//package org.integratedmodelling.thinklab.modelling.lang;
//
//import java.util.Map;
//
//import org.integratedmodelling.thinklab.NS;
//import org.integratedmodelling.thinklab.api.annotations.Concept;
//import org.integratedmodelling.thinklab.api.knowledge.IExpression;
//import org.integratedmodelling.thinklab.api.modelling.parsing.IDataSourceDefinition;
//
//@Concept(NS.DATASOURCE_DEFINITION)
//public class DataSourceDefinition extends ModelObject<DataSourceDefinition> implements IDataSourceDefinition {
//
//	String _type;
//	IExpression _expression;
//	Map<String, Object> _parameters;
//	
//	@Override
//	public void setType(String type) {
//		_type = type;
//	}
//
//	@Override
//	public void setResolvedExpression(IExpression expression) {
//		_expression = expression;
//	}
//
//	@Override
//	public void setParameters(Map<String, Object> parameters) {
//		_parameters = parameters;
//	}
//
//	@Override
//	public DataSourceDefinition demote() {
//		return this;
//	}
//
//}
