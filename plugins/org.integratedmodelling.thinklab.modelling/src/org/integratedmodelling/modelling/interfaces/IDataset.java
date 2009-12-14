package org.integratedmodelling.modelling.interfaces;

import java.util.Collection;

import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;

/**
 * An adapter that helps use and visualize an observation as a dataset with many
 * possible data and uniform context. Should be initializable from a "live" observation or from any kind of persistent 
 * storage. We may also want a factory that gives us a IDataset for any kind of
 * suitable input.
 * 
 * @author Ferdinando Villa
 *
 */
public interface IDataset {

	public abstract boolean isSpatial();
	
	public abstract boolean isTemporal();
	
	public abstract int getStateCount();
	
	public abstract Collection<IConcept> getObservables();
	
	public abstract Collection<IConcept> getStatefulObservables();
	
	public abstract IState getState(IConcept observable);
	
	public abstract String makeContourPlot(
			IConcept observable, String fileOrNull, int x, int y, int ... flags);
	
	public abstract String makeSurfacePlot(
			IConcept observable, String fileOrNull, int x, int y, int ... flags) throws ThinklabException;

	public abstract String makeTimeSeriesPlot(
			IConcept observable, String fileOrNull, int x, int y, int ... flags);

	public abstract String makeHistogramPlot(
			IConcept observable, String fileOrNull, int x, int y, int ... flags);
	
	public abstract void dump(IConcept concept);
	
	public abstract void dumpAll();

}
