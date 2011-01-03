package org.integratedmodelling.modelling.storyline;

import java.util.ArrayList;
import java.util.Collection;

import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.modelling.visualization.presentation.PresentationTemplate;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabRuntimeException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.utils.xml.XMLDocument;
import org.w3c.dom.Node;

public class ModuleStoryline extends Storyline {

	private static final long serialVersionUID = 5675523894812933228L;
	ArrayList<IConcept> modelConcepts = new ArrayList<IConcept>();

	public ModuleStoryline(IContext context) {
		this.context = context;
	}

	@Override
	protected void processTemplate(PresentationTemplate template) {
		
		for (Node node : template.getCustomNodes()) {
			if (node.getNodeName().equals("model-storyline")) {
				try {
					modelConcepts.add(KnowledgeManager.get().requireConcept(XMLDocument.getNodeValue(node)));
				} catch (Exception e) {
					throw new ThinklabRuntimeException(e);
				}
			}
		}
	}

	/*
	 * Create all the model storylines indicated in the presentation file. They will be
	 * used only if any of their models cover the context. Compute the union of their
	 * coverage in the meantime. Should be called only once by the user model.
	 * 
	 * @param context
	 * @return
	 */
	Collection<ModelStoryline> getModelStorylines() {
		
		ArrayList<ModelStoryline> ret = new ArrayList<ModelStoryline>();
		
		// FIXME used for the side-effects; rename or refactor
		template();
		
		for (IConcept c : modelConcepts) {
			ret.add(new ModelStoryline(c));
		}
		
		return ret;
	}
}


