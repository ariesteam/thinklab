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
 * 	//biomass@time(prev) -> find the biomass formal name in the obs tree (any level) and take its value at
 *                          the previous extent of time. 
 *  space                -> take the current spatial extent in the main observation, null if no space context
 *  //elevation@space(nw)-> find the elevation formal name and take its value at the northwest grid cell
 *  
 */
public class ContextExpression {

	static boolean validate(String prefix) {
		return false;
	}
	
	public ContextExpression(String s) {
		
	}
	
}
