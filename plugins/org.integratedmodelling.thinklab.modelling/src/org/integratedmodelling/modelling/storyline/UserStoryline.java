package org.integratedmodelling.modelling.storyline;

import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.modelling.visualization.presentation.PresentationTemplate;

public class UserStoryline extends Storyline {

	private static final long serialVersionUID = 5589275748375141235L;
	
	public UserStoryline(IContext context) {
		this.context = context;
	}

	@Override
	protected void processTemplate(PresentationTemplate template) {
	}

}
