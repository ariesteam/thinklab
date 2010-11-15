package org.integratedmodelling.modelling.interfaces;

import java.io.File;
import java.net.URL;
import java.util.Collection;

import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.utils.image.ColorMap;

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
	
	public abstract void initialize(IObservation observation) throws ThinklabException;

	public abstract void initialize(URL resource) throws ThinklabException;
	
	public abstract void persist(File resource) throws ThinklabException;

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

	/**
	 * Call with same parameter as makeSurfacePlot (except the file name) and it
	 * will return an uncertainty mask for the concept if any uncertainty data are
	 * in the corresponding state.
	 * 
	 * @param observable
	 * @param fileOrNull
	 * @param x
	 * @param y
	 * @param flags
	 * @return
	 * @throws ThinklabException
	 */
	public abstract String makeUncertaintyMask(
			IConcept observable, String fileOrNull, int x, int y, int ... flags) throws ThinklabException;

	public abstract String makeTimeSeriesPlot(
			IConcept observable, String fileOrNull, int x, int y, int ... flags);

	public abstract String makeHistogramPlot(
			IConcept observable, String fileOrNull, int x, int y, int ... flags);
	
	public abstract void dump(IConcept concept);
	
	public abstract void dumpAll();

	/**
	 * Used to select the appropriate colormap for the numeric states of an observable.
	 * The number of colors/levels to show can be computed as (maxIndex-minIndex)
	 * 
	 * @param observable
	 * @param nlevels the number of levels to be represented
	 * @return
	 * @throws ThinklabException 
	 */
	public abstract ColorMap chooseColormap(IConcept observable, int nlevels, boolean isCategorical) throws ThinklabException;

}
