package org.integratedmodelling.modelling.corescience;

import java.util.Collection;
import java.util.Iterator;

import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.utils.Polylist;

public class ClassificationModel implements IModel {

	IModel classified = null;
	IConcept toClassify = null;
	
	public void define(Object concOrMod, Collection<?> classdefs) throws ThinklabException {
	
		if (concOrMod instanceof IModel) {
			classified = (IModel) concOrMod;
		} else if (concOrMod instanceof IConcept) {
			toClassify = KnowledgeManager.get().requireConcept(concOrMod.toString());
		} 		
		
		/*
		 * analyze the sequence of classdefs
		 */
		for (Iterator<?> it = classdefs.iterator(); it.hasNext(); ) {
			
			Object classSpec = it.next();
			Object classRet  = it.next();
			
			/*
			 * class def should be a string specification or a list of concepts
			 */
			if (classSpec instanceof String) {
				
				/*
				 * should be a numeric range
				 */
				
			} else if (classSpec instanceof Polylist) {
				
			}
			
			/*
			 * class target should be a number, a concept or an instance 
			 */
			if (classRet instanceof Integer) {
				
			} else if (classRet instanceof Double) {
				
			} else if (classRet instanceof Float) {
				
			} else if (classRet instanceof Long) {
				
			} else if (classRet instanceof IConcept) {
				
			} else if (classRet instanceof Polylist) {
				
			} else {
				
				/*
				 * last try: use the string value as a semantic type
				 */
			}
			/*
			 * define the type of observation we need to build according
			 * to the classification specs
			 */
		}
	}
	
	@Override
	public IInstance buildObservation(IKBox kbox, ISession session)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IConcept getCompatibleObservationType(ISession session) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IConcept getObservable() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isResolved() {
		// TODO Auto-generated method stub
		return false;
	}

}
