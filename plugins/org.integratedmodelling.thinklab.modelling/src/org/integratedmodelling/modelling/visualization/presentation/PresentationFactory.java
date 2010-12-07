package org.integratedmodelling.modelling.visualization.presentation;

import java.io.File;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.datastructures.IntelligentMap;

public class PresentationFactory {

	IntelligentMap<Presentation> _presentations;
	static PresentationFactory _this = null;
	
	public Presentation getPresentation(IConcept concept) {
		return _presentations.get(concept);
	}
	
	public void scanDirectory(File dir) throws ThinklabException {
		
		for (File f : dir.listFiles()) {
			if (f.toString().endsWith(".xml")) {
				Presentation p = new Presentation();
				try {
					p.read(f.toURI().toURL());
				} catch (Exception e) {
					p = null;
				}
				if (p != null)
					_presentations.put(KnowledgeManager.get().requireConcept(p.getConcept()), p);
			}
				
		}
	}

	public static PresentationFactory get() {

		if (_this == null) {
			_this = new PresentationFactory();
		}
		return _this;
	}
}
