package org.integratedmodelling.thinklab.modelling.visualization;

import org.integratedmodelling.thinklab.api.modelling.IClassifyingObserver;
import org.integratedmodelling.thinklab.api.modelling.IMeasuringObserver;
import org.integratedmodelling.thinklab.api.modelling.IRankingObserver;
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
		
		if (state.getObserver() instanceof IClassifyingObserver) {
			return new ClassificationDisplayMetadata((((Classification)(state.getObserver())).getClassification()));			
		} else if (state.getObserver() instanceof IMeasuringObserver && 
					((IMeasuringObserver)(state.getObserver())).getDiscretization() != null) {
			
		}
					
		
		return new DisplayMetadata();
	}
	
	
}
