package org.integratedmodelling.modelling.interfaces;

import org.integratedmodelling.modelling.visualization.presentation.PresentationTemplate;
import org.integratedmodelling.thinklab.exception.ThinklabException;

public interface IPresentation {

	public abstract void render() throws ThinklabException;

	public abstract void initialize(IVisualization visual, PresentationTemplate layout);
	
}
