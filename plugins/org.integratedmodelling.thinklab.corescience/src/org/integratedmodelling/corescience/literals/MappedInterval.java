package org.integratedmodelling.corescience.literals;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.literals.IntervalValue;
import org.integratedmodelling.thinklab.literals.ParsedLiteralValue;

public class MappedInterval extends ParsedLiteralValue {

	IntervalValue interval = null;
	String sform = null;
	
	public MappedInterval(String s) throws ThinklabValidationException {
		parseLiteral(s);
	}

	public MappedInterval(IConcept concept, IntervalValue val) {
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
		try {
			setConceptWithoutValidation(KnowledgeManager.get().requireConcept(cname));
		} catch (ThinklabException e) {
			throw new ThinklabValidationException(
					"invalid mapped interval: " + s + ": " + e.getMessage());
		}

	}

	public IntervalValue getInterval() {
		return interval;
	}
	
	public boolean contains(double d) {
		return interval.contains(d);
	}

}
