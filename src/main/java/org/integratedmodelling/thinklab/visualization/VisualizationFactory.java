package org.integratedmodelling.thinklab.visualization;

import org.integratedmodelling.thinklab.api.modelling.IClassifyingObserver;
import org.integratedmodelling.thinklab.api.modelling.IMeasuringObserver;
import org.integratedmodelling.thinklab.api.modelling.IState;

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
	public DisplayAdapter getDisplayAdapter(IState state) {
		
		if (state.getObserver() instanceof IClassifyingObserver) {
			return new ClassificationDisplayAdapter(state);			
		} else if (state.getObserver() instanceof IMeasuringObserver && 
					((IMeasuringObserver)(state.getObserver())).getDiscretization() != null) {
			
		}
		
		return new DisplayAdapter(state);
	}
}
