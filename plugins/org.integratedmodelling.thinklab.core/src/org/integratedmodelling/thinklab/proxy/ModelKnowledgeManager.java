/**
 * Copyright 2011 The ARIES Consortium (http://www.ariesonline.org) and
 * www.integratedmodelling.org. 

   This file is part of Thinklab.

   Thinklab is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published
   by the Free Software Foundation, either version 3 of the License,
   or (at your option) any later version.

   Thinklab is distributed in the hope that it will be useful, but
   WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Thinklab.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.integratedmodelling.thinklab.proxy;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.api.factories.IKnowledgeManager;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IProperty;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticObject;
import org.integratedmodelling.thinklab.api.knowledge.ISemantics;
import org.integratedmodelling.thinklab.api.knowledge.kbox.IKbox;

/**
 * Just a delegate knowledge manager that uses the singleton for 
 * all operations. This allows us to use dependency injection in external
 * libraries that only depend on the API.
 * 
 * Use KnowledgeManager, not Thinklab, as Thinkab may not be fully 
 * instantiated by the time this is needed.
 * 
 * @author Ferd
 *
 */
public class ModelKnowledgeManager implements IKnowledgeManager {

	@Override
	public IProperty getProperty(String prop) {
		return KnowledgeManager.get().getProperty(prop);
	}

	@Override
	public IConcept getConcept(String prop) {
		return KnowledgeManager.get().getConcept(prop);
	}

	@Override
	public IConcept getLeastGeneralCommonConcept(IConcept... cc) {
		return KnowledgeManager.get().getLeastGeneralCommonConcept(cc);
	}

	@Override
	public IKbox createKbox(String uri) throws ThinklabException {
		return KnowledgeManager.get().createKbox(uri);
	}

	@Override
	public void dropKbox(String uri) throws ThinklabException {
		KnowledgeManager.get().dropKbox(uri);
	}

	@Override
	public IKbox requireKbox(String uri) throws ThinklabException {
		return KnowledgeManager.get().requireKbox(uri);
	}

	@Override
	public ISemanticObject parse(String literal, IConcept c)
			throws ThinklabException {
		return KnowledgeManager.get().parse(literal, c);
	}

	@Override
	public ISemanticObject annotate(Object object) throws ThinklabException {
		return KnowledgeManager.get().annotate(object);
	}

	@Override
	public ISemantics conceptualize(Object object) throws ThinklabException {
		return KnowledgeManager.get().conceptualize(object);
	}

	@Override
	public Object instantiate(ISemantics a) throws ThinklabException {
		return KnowledgeManager.get().instantiate(a);
	}

	@Override
	public void registerAnnotatedClass(Class<?> cls, IConcept concept) {
		// TODO Auto-generated method stub
		
	}


}
