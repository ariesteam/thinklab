package org.integratedmodelling.modelling.visualization.presentation;

import java.io.File;

import org.integratedmodelling.modelling.interfaces.IPresentation;
import org.integratedmodelling.modelling.interfaces.IVisualization;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.datastructures.IntelligentMap;

public class PresentationFactory {

	IntelligentMap<PresentationLayout> _presentations;
	static PresentationFactory _this = null;
	
	public PresentationLayout getPresentation(IConcept concept) {
		return _presentations.get(concept);
	}
	
	public void scanDirectory(File dir) throws ThinklabException {
		
		for (File f : dir.listFiles()) {
			if (f.toString().endsWith(".xml")) {
				PresentationLayout p = new PresentationLayout();
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

	static PresentationFactory get() {

		if (_this == null) {
			_this = new PresentationFactory();
		}
		return _this;
	}
	
	/**
	 * Create a presentation outside of the factory, use the factory to render it. If will find the
	 * layout and apply it to the presentation. 
	 * 
	 * @param visual
	 * @param presentation
	 * @return
	 */
	public static IPresentation renderPresentation(IVisualization visual, IPresentation presentation) {
	
		
		return presentation;
	}
}
