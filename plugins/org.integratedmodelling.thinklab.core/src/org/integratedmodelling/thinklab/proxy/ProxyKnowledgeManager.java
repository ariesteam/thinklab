package org.integratedmodelling.thinklab.proxy;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IProperty;
import org.integratedmodelling.thinklab.api.knowledge.IValue;
import org.integratedmodelling.thinklab.api.knowledge.factories.IKnowledgeManager;
import org.integratedmodelling.thinklab.api.knowledge.storage.IKBox;

/**
 * Just a delegate knowledge manager that uses the singleton for 
 * all operations. This allows us to use dependency injection in external
 * libraries that only depend on the API.
 * 
 * @author Ferd
 *
 */
public class ProxyKnowledgeManager implements IKnowledgeManager {

	@Override
	public IProperty getProperty(String prop) {
		return Thinklab.get().getProperty(prop);
	}

	@Override
	public IConcept getConcept(String prop) {
		return Thinklab.get().getConcept(prop);
	}

	@Override
	public IConcept getConceptForClass(Class<?> cls) {
		return Thinklab.get().getConceptForClass(cls);
	}

	@Override
	public Class<?> getClassForConcept(IConcept type) {
		return Thinklab.get().getClassForConcept(type);
	}

	@Override
	public IConcept getLeastGeneralCommonConcept(IConcept... cc) {
		return Thinklab.get().getLeastGeneralCommonConcept(cc);
	}

	@Override
	public IValue validateLiteral(IConcept c, String literal)
			throws ThinklabException {
		return Thinklab.get().validateLiteral(c, literal);
	}

	@Override
	public IKBox getDefaultKbox() {
		return Thinklab.get().getDefaultKbox();
	}

}
