package org.integratedmodelling.modelling.visualization.presentation.impl;

import org.integratedmodelling.modelling.visualization.presentation.DefaultPresentation;
import org.integratedmodelling.modelling.visualization.presentation.PresentationTemplate;
import org.integratedmodelling.thinklab.exception.ThinklabException;

public class HtmlPresentation extends DefaultPresentation {
	
	@Override
	public void render() throws ThinklabException {
		// TODO Auto-generated method stub

		for (PresentationTemplate.Page page : template.getSinglePages()) {
			
		}

		for (PresentationTemplate.Page page : template.getPages()) {
			
		}
	}

}
