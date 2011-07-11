package org.integratedmodelling.corescience.literals;

import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.literals.IntervalValue;
import org.integratedmodelling.thinklab.literals.ParsedLiteralValue;

/**
 * Maps numbers to other numbers
 * @author Ferdinando
 *
 */
public class MappedDoubleInterval extends ParsedLiteralValue {

	IntervalValue interval = null;
	String sform = null;
	Double mapped = null;
	
	public MappedDoubleInterval(String s) throws ThinklabValidationException {
		parseLiteral(s);
	}

	public MappedDoubleInterval(IConcept concept, IntervalValue val) {
		setConceptWithoutValidation(concept);
		interval = val;
	}

	@Override
	public void parseLiteral(String s) throws ThinklabValidationException {

		sform = s;
		
		int idx = s.indexOf(":");
		if (idx < 0)
			throw new ThinklabValidationException("invalid mapped interval syntax: " + s);
		
		String cname = s.substring(0, idx).trim();
		String intvs = s.substring(idx+1).trim();
		
		interval = new IntervalValue(intvs);
		mapped = Double.parseDouble(cname);

	}

	public IntervalValue getInterval() {
		return interval;
	}
	
	public boolean contains(double d) {
		return interval.contains(d);
	}
	
	public double getValue() {
		return mapped;
	}
	
	public String toString() {
		return sform;
	}

}
