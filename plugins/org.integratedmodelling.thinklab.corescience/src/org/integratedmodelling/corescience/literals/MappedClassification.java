package org.integratedmodelling.corescience.literals;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.literals.IntervalValue;
import org.integratedmodelling.thinklab.literals.ParsedLiteralValue;

/**
 * A mapped classification remaps any concept within a specified set of concepts to a single concept.
 * This one can be specified as a literal value of the form c<-c1,c2,c3. The concept of the value
 * is the mapped concept. 
 * 
 * @author Ferdinando
 *
 */
public class MappedClassification extends ParsedLiteralValue {

	IntervalValue interval = null;
	Set<IConcept> cset = new HashSet<IConcept>();
	
	public MappedClassification(String s) throws ThinklabValidationException {
		parseLiteral(s);
	}

	@Override
	public void parseLiteral(String s) throws ThinklabValidationException {

		int idx = s.indexOf("<-");
		if (idx < 0)
			throw new ThinklabValidationException("invalid mapped classification syntax: " + s);
		
		String cname = s.substring(0, idx).trim();
		String[] classes = s.substring(idx+2).trim().split(",");
		
		try {
			setConceptWithoutValidation(KnowledgeManager.get().requireConcept(cname));
			for (String ss : classes) {
				cset.add(KnowledgeManager.get().requireConcept(ss));
			}
		} catch (ThinklabException e) {
			throw new ThinklabValidationException(
					"invalid mapped classification: " + s + ": " + e.getMessage());
		}

	}

	public Collection<IConcept> getConcepts() {
		return cset;
	}
	
	public boolean contains(IConcept c) {
		return cset.contains(c);
	}

}
