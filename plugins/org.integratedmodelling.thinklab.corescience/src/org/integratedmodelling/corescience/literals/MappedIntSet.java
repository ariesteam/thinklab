package org.integratedmodelling.corescience.literals;

import java.util.HashSet;
import java.util.Set;

import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.literals.ParsedLiteralValue;

/**
 * A mapped classification remaps any concept within a specified set of concepts to a single concept.
 * This one can be specified as a literal value of the form c<-c1,c2,c3. The concept of the value
 * is the mapped concept. 
 * 
 * @author Ferdinando
 *
 */
public class MappedIntSet extends ParsedLiteralValue {

	Set<Integer> cset = new HashSet<Integer>();
	Integer mapped = null;
	
	public MappedIntSet(String s) throws ThinklabValidationException {
		parseLiteral(s);
	}

	@Override
	public void parseLiteral(String s) throws ThinklabValidationException {

		int idx = s.indexOf("<-");
		if (idx < 0)
			throw new ThinklabValidationException("invalid mapped classification syntax: " + s);
		
		String iset = s.substring(0, idx).trim();
		String[] classes = s.substring(idx+2).trim().split(",");
		
		for (String ss : classes) {
			cset.add(Integer.parseInt(ss));
		}
		
		mapped = Integer.parseInt(iset);

	}

	public boolean contains(Integer c) {
		return cset.contains(c);
	}

	public int getValue() {
		return mapped;
	}
	
}
