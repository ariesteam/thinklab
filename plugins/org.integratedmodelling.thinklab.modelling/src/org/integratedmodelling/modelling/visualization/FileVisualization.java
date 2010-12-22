package org.integratedmodelling.modelling.visualization;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.integratedmodelling.corescience.context.ObservationContext;
import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.modelling.ModellingPlugin;
import org.integratedmodelling.modelling.interfaces.IVisualization;
import org.integratedmodelling.modelling.storage.FileArchive;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.utils.Pair;

/**
 * Uses a FileArchive inside, or it can be initialized from one, so it shares the same
 * file structure. If will build pngs in each state directory to match the context of
 * the states.
 * 
 * @author ferdinando.villa
 *
 */
public class FileVisualization implements IVisualization {

	FileArchive archive = null;
	private IContext context;
	boolean visualized = false;
	
	/**
	 * largest edge of plot in pixels, used to define the dimensions of any visual 
	 * unless the max viewport is given.
	 */
	private int maxEdgeLength = 1000;
	
	/**
	 * if these are set, the plots will be the largest possible for the given viewport.
	 */
	private int maxWidth  = -1;
	private int maxHeight = -1;
	private File directory;
	
	public FileVisualization() {
	}
	
	public FileVisualization(IContext context) throws ThinklabException {
		initialize(context);
	}
	
	
	public FileVisualization(IContext context, File directory) throws ThinklabException {
		this.directory = directory;
		initialize(context);
	}
	
	@Override
	public void initialize(IContext context) throws ThinklabException {
		
		// remove when not needed anymore.
		((ObservationContext)context).collectStates();
		
		this.context = context;
		if (this.archive == null) {
			this.archive = 
				directory == null ?
					new FileArchive(context) :
					new FileArchive(context, directory);
		}	
	}
	
	/**
	 * Use this if you need the visuals to fit a given viewport.
	 * 
	 * @param width
	 * @param height
	 */
	public void setViewPort(int width, int height) {
		maxWidth  = width;
		maxHeight = height;
	}

	@Override
	public void visualize() throws ThinklabException {
				
		if (visualized)
			return;
		
		for (IState state : context.getStates()) {
			
			for (String plotType : VisualizationFactory.get().getPlotTypes(state, context)) {
		
				Pair<Integer,Integer> xy = 
					maxHeight < 0 ?
						VisualizationFactory.get().getPlotSize(maxEdgeLength, (IContext) context) :
						VisualizationFactory.get().getPlotSize(maxWidth, maxHeight, (IContext) context);
				
				File dir = archive.getStateDirectory(state.getObservableClass());
				File out = new File(dir + File.separator + plotType);
				VisualizationFactory.get().
					plot(state, context, plotType, xy.getFirst(), xy.getSecond(), out);
			}
		}
		
		visualized = true;
		
		ModellingPlugin.get().logger().info(
				"visualization of " + 
				((IObservationContext)context).getObservation().getObservableClass() + 
				" created in " +
				archive.getDirectory());
				
	}

	@Override
	public IConcept getObservableClass() {
		return ((IObservationContext)context).getObservation().getObservableClass();
	}
	
	public File getStateDirectory(IConcept c) {
		return archive.getStateDirectory(c);
	}

	public Collection<File> getStateImages(IConcept c) {
		
		ArrayList<File> ret = new ArrayList<File>();
		for (String s : new String[] {	
				VisualizationFactory.PLOT_SURFACE_2D, VisualizationFactory.PLOT_CONTOUR_2D, 
				VisualizationFactory.PLOT_GEOSURFACE_2D, VisualizationFactory.PLOT_UNCERTAINTYSURFACE_2D, 
				VisualizationFactory.PLOT_GEOCONTOUR_2D, VisualizationFactory.PLOT_TIMESERIES_LINE, 
				VisualizationFactory.PLOT_TIMELAPSE_VIDEO}) {
			
			File f = new File(getStateDirectory(c) + File.separator + s);
			if (f.exists())
				ret.add(f);
		}
		return ret;
	}
}
