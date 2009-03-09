package org.integratedmodelling.corescience.expr;

/**
 * A context expression extracts state from an identified expression and has a syntax to extract
 * state of dependencies, contingencies, and extents; the latter can be parameterized according
 * to their dimensionality so that states in different contexts can be accessed.
 * 
 * @author Ferdinando Villa
 * 
 * examples:
 * 
 * 	self//biomass@time(prev)
 *  context/space
 *  self//elevation@space(nw)
 *  
 */
public class ContextExpression {

	static boolean validate(String prefix) {
		return false;
	}
	
	public ContextExpression(String s) {
		
	}
	
}
