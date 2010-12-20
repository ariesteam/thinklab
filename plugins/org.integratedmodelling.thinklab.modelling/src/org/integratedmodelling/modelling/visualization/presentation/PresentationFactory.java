package org.integratedmodelling.modelling.visualization.presentation;

import java.io.File;

import org.integratedmodelling.modelling.ModellingPlugin;
import org.integratedmodelling.modelling.interfaces.IPresentation;
import org.integratedmodelling.modelling.interfaces.IVisualization;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.datastructures.IntelligentMap;

public class PresentationFactory {

	IntelligentMap<PresentationTemplate> _presentations = new IntelligentMap<PresentationTemplate>();
	static PresentationFactory _this = null;
	
	public static PresentationTemplate getPresentation(IConcept concept) {
		return get()._presentations.get(concept);
	}
	
	public static void scanDirectory(File dir) throws ThinklabException {
		
		for (File f : dir.listFiles()) {
			if (f.toString().endsWith(".xml")) {
				PresentationTemplate p = new PresentationTemplate();
				try {
					p.read(f.toURI().toURL());
				} catch (Exception e) {
					ModellingPlugin.get().logger().error(e.getMessage());
					p = null;
				}
				if (p != null) {
					get()._presentations.put(KnowledgeManager.get().requireConcept(p.getConcept()), p);
					ModellingPlugin.get().logger().info("presentation template " + p + " read successfully");
				}
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
	 * When the presentation applies to a visualization, this is all that needs to be called.
	 * Create a presentation outside of the factory, use the factory to render it. If will find the
	 * layout for the , initialize the presentation and call render() on it. 
	 * 
	 * @param visual
	 * @param presentation
	 * @return
	 * @throws ThinklabException 
	 */
	public static IPresentation render(IVisualization visual, IPresentation presentation) throws ThinklabException {
	
		PresentationTemplate template = getPresentation(visual.getObservableClass());
		presentation.initialize(visual, template);
		presentation.render();
		
		return presentation;
	}
}
