package org.integratedmodelling.modelling.visualization.presentation;

import org.integratedmodelling.modelling.interfaces.IPresentation;
import org.integratedmodelling.modelling.interfaces.IVisualization;

public abstract class Presentation implements IPresentation {

	private PresentationLayout layout;
	private IVisualization visual;

	void initialize(IVisualization visual, PresentationLayout layout) {
		this.layout = layout;
	}
	
}
