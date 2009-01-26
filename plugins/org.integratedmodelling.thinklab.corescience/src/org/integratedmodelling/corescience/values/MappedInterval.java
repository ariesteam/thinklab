package org.integratedmodelling.corescience.values;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.value.IntervalValue;
import org.integratedmodelling.thinklab.value.ParsedLiteralValue;


public class MappedInterval extends ParsedLiteralValue {

	IntervalValue interval = null;
	IConcept mapping = null;
	
	public MappedInterval(String s) throws ThinklabValidationException {
		parseLiteral(s);
	}

	@Override
	public void parseLiteral(String s) throws ThinklabValidationException {

		int idx = s.indexOf("<-");
		if (idx < 0)
			throw new ThinklabValidationException("invalid mapped interval syntax: " + s);
		
		String cname = s.substring(0, idx);
		String intvs = s.substring(idx+2);
		
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
