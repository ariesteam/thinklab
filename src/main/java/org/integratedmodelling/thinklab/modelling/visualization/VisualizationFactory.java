package org.integratedmodelling.thinklab.modelling.visualization;

import org.integratedmodelling.thinklab.api.modelling.IState;
import org.integratedmodelling.thinklab.modelling.lang.Classification;

public class VisualizationFactory {

	private static VisualizationFactory _this = null;
	
	public static VisualizationFactory get() {
		if (_this == null) {
			_this = new VisualizationFactory();
		}
		return _this;
	}
	
	/**
	 * 
	 * @param state
	 * @return
	 */
	public DisplayMetadata getDisplayMetadata(IState state) {
		return new ClassificationDisplayMetadata((((Classification)(state.getObserver())).getClassification()));
	}
	
	
}
