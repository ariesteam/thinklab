package org.integratedmodelling.modelling.visualization.storyline;

import org.integratedmodelling.modelling.interfaces.IPresentation;
import org.integratedmodelling.modelling.interfaces.IVisualization;

public abstract class DefaultPresentation implements IPresentation {

	protected StorylineTemplate template;
	protected IVisualization visualization;

	@Override
	public void initialize(IVisualization visual, StorylineTemplate layout) {
		this.visualization = visual;
		this.template = layout;
	}
	
}
