package org.integratedmodelling.modelling.visualization.presentation;

import org.integratedmodelling.modelling.interfaces.IPresentation;
import org.integratedmodelling.modelling.interfaces.IVisualization;

public abstract class DefaultPresentation implements IPresentation {

	protected PresentationTemplate template;
	protected IVisualization visualization;

	@Override
	public void initialize(IVisualization visual, PresentationTemplate layout) {
		this.visualization = visual;
		this.template = layout;
	}
	
}
