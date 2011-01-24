package org.integratedmodelling.modelling.visualization;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import org.integratedmodelling.corescience.context.ObservationContext;
import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.corescience.interfaces.IExtent;
import org.integratedmodelling.corescience.interfaces.IObservationContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.geospace.extents.GridExtent;
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
	
	protected FileArchive archive = null;
	protected IContext context;
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
	private Integer xPlotSize = -1;
	private Integer yPlotSize = -1;
	
	public FileVisualization() {
	}
	
	public FileVisualization(IContext context) throws ThinklabException {
		initialize(context, null);
	}
	
	public FileVisualization(IContext context, File directory) throws ThinklabException {
		this.directory = directory;
		initialize(context, null);
	}
	
	public int getXPlotSize() {
		return xPlotSize;
	}

	public int getYPlotSize() {
		return yPlotSize;
	}

	@Override
	public void initialize(IContext context, Properties properties) throws ThinklabException {
		
		// remove when not needed anymore.
		((ObservationContext)context).collectStates();
		
		this.context = context;
		if (this.archive == null) {
			this.archive = 
				directory == null ?
					new FileArchive(context) :
					new FileArchive(context, directory);
		}	

		if (properties != null) {
			String vpx = properties.getProperty(VIEWPORT_X_PROPERTY);
			String vpy = properties.getProperty(VIEWPORT_Y_PROPERTY);
		
			if (vpx != null && vpy != null) {
				setViewPort(Integer.parseInt(vpx.trim()), Integer.parseInt(vpy.trim()));
			}
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

				if (this.xPlotSize == -1) {
					this.xPlotSize = xy.getFirst();			
					this.yPlotSize = xy.getSecond();			
				}
				
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
	
	public Pair<Double, Double> getGeoCoordinates(int x, int y) {
		
		IExtent sp = context.getSpace();
		
		if (!(sp instanceof GridExtent))
			return null;
		
		GridExtent grid = (GridExtent) sp;
		
		double pcx = (double)(getXPlotSize())/(double)(grid.getXCells());
		double pcy = (double)(getYPlotSize())/(double)(grid.getYCells());
		
		int dx = (int)((double)x/pcx);
		int dy = (int)((double)(getYPlotSize() - y)/pcy);
		
		return new Pair<Double, Double>(
				grid.getWest()  + grid.getEWResolution()*dx + grid.getEWResolution()/2,
				grid.getSouth() + grid.getNSResolution()*dy + grid.getNSResolution()/2);
	}

}
