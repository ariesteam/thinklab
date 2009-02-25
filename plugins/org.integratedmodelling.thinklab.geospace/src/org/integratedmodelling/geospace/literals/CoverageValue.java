package org.integratedmodelling.geospace.literals;

import org.integratedmodelling.thinklab.literals.Value;

/**
 * Represents the coverage of a class within an extent of values distributed over a grid.
 * 
 * @author Ferdinando
 *
 */
public class CoverageValue extends Value {
	
	Integer nCovered = null;
	private int[] mask;
	private Integer classValue;

	/**
	 * Use this one when the total amount of covered values is unknown; it will cause
	 * their count the first time it's asked for. Pass null for the class value if 
	 * you want all classes.
	 * 
	 * @param mask
	 * @param xdivs
	 * @param ydivs
	 * @param classValue
	 */
	public CoverageValue(int[] mask, int xdivs, int ydivs, Integer classValue) {
		// TODO Auto-generated constructor stub
		this.mask = mask;
		this.classValue = classValue;
	}
	
	/**
	 * Use this one when the total amount of covered values is known. Pass null for the class value if 
	 * you want all classes.
	 * 
	 * @param mask
	 * @param xdivs
	 * @param ydivs
	 * @param classValue
	 * @param nCovered
	 */
	public CoverageValue(int[] mask, int xdivs, int ydivs, Integer classValue, int nCovered) {
		this.nCovered = nCovered;
		this.mask = mask;
		this.classValue = classValue;
	}
	
	public int numerosity() {

		if (nCovered == null) {
			count();
		}
		return nCovered;
	}

	private void count() {

		nCovered = 0;
		for (int i = 0; i < mask.length; i++) {
			if (mask[i] == classValue) {
				nCovered ++;
			}
		}
	}

}
