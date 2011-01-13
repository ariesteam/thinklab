package org.integratedmodelling.modelling.visualization.presentation.impl;

import org.integratedmodelling.modelling.visualization.storyline.DefaultPresentation;
import org.integratedmodelling.modelling.visualization.storyline.StorylineTemplate;
import org.integratedmodelling.thinklab.exception.ThinklabException;

public class HtmlPresentation extends DefaultPresentation {
	
	@Override
	public void render() throws ThinklabException {
		// TODO Auto-generated method stub

		for (StorylineTemplate.Page page : template.getSinglePages()) {
			
		}

		for (StorylineTemplate.Page page : template.getPages()) {
			
		}
	}

}
