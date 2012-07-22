package org.integratedmodelling.thinklab.modelling.datasets;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.integratedmodelling.collections.ContextIndex;
import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabIOException;
import org.integratedmodelling.thinklab.api.modelling.IContext;
import org.integratedmodelling.thinklab.api.modelling.IDataset;
import org.integratedmodelling.thinklab.api.modelling.IExtent;
import org.integratedmodelling.thinklab.api.modelling.IState;
import org.integratedmodelling.thinklab.geospace.extents.ArealExtent;
import org.integratedmodelling.thinklab.geospace.extents.GridExtent;
import org.integratedmodelling.thinklab.geospace.literals.ShapeValue;
import org.integratedmodelling.thinklab.modelling.lang.Context;
import org.integratedmodelling.thinklab.visualization.geospace.GeoImageFactory;
import org.integratedmodelling.utils.image.ImageUtil;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

/**
 * A dataset residing in a directory on the filesystem, with an index, pre-visualized objects, a full NetCDF
 * for grids and any other non-grid data in subdirectories. Index is a serialized ContextIndex
 * bean from the API library.
 * 
 * File structure after persist() is called:
 * 
 * <main dir>
 *      index.xml // use to reconstruct the ContextIndex bean
 *
 * 		thumbnails/
 * 			th_n.png*
 *
 *      images/
 *          im_n.xxx*
 *
 *      data.nc  // netcdf file with all grid data
 *      
 *      data/ xxx  // any additional non-grid data file (e.g. shapefiles), indexed in netcdf attributes.
 *      
 * @author Ferd
 *
 */
public class FileDataset implements IDataset {

	public static final int DEFAULT_THUMBNAIL_WIDTH = 360;
	public static final int DEFAULT_THUMBNAIL_HEIGHT = 180;
	public static final int DEFAULT_IMAGE_WIDTH = 800;
	public static final int DEFAULT_IMAGE_HEIGHT = 600;

	IContext _context;
	ContextIndex _index = new ContextIndex();
	
	int _tboxX = 40, _tboxY = 40;
	int _iboxX = 580, _iboxY = 580;
	
	public FileDataset() {
	}
	
	public FileDataset(Context ctx) {
		setContext(ctx);
	}

	public void setThumbnailBoxSize(int x, int y) {
		_tboxX = x;
		_tboxY = y;
	}
	
	public void setImageBoxSize(int x, int y) {
		_iboxX = x;
		_iboxY = y;
	}
	
	public static interface StateDescriptor {
		String getName();
		String getObservableDefinition();
	}
	
	@Override
	public void setContext(IContext context)  {
		_context = context;
	}

	@Override
	public IContext getContext() {
		return _context;
	}

	public ContextIndex getIndex() {
		return _index;
	}
	
	@Override
	public String persist(String location) throws ThinklabException {
		
		if (_context == null)
			return null;
		
		_index.setMultiplicity(_context.getMultiplicity());
		
		File locDir = new File(location);
		locDir.mkdirs();
		
		new File(locDir + File.separator + "images").mkdirs();
		new File(locDir + File.separator + "thumbnails").mkdirs();
		
		/*
		 * create the NetCDF for anything that can be stored in it.
		 * TODO this should not whine about non-grid data, but return
		 * a list of states that could not be serialized in it.
		 * 
		 * TODO switch to CFdataset when it's ready.
		 */
		new NetCDFDataset(_context).write(locDir + File.separator + "data.nc");
		_index.setDataFile("data.nc");
		
		/*
		 * serialize extents
		 */
		IExtent space = _context.getSpace();
		if (space instanceof GridExtent) {
			
			ShapeValue shape = ((GridExtent)space).getShape();
			Pair<Integer, Integer> pst = GeoImageFactory.getPlotSize(_tboxX, _tboxY, 
					((GridExtent)space).getXCells(), ((GridExtent)space).getYCells());
			Pair<Integer, Integer> psi = GeoImageFactory.getPlotSize(_iboxX, _iboxY, 
					((GridExtent)space).getXCells(), ((GridExtent)space).getYCells());

			/*
			 * TODO use a raster image function that paints the actual grid
			 */
			ImageUtil.saveImage(
					GeoImageFactory.get().getImagery(shape.getEnvelope(), shape, pst.getFirst(), pst.getSecond(), 0),
					locDir + File.separator + "thumbnails" + File.separator + "space.png");
		
			ImageUtil.saveImage(
					GeoImageFactory.get().getImagery(shape.getEnvelope(), shape, psi.getFirst(), psi.getSecond(), 0),
					locDir + File.separator + "images" + File.separator + "space.png");
			
			_index.addExtent(
					ContextIndex.SPACE_ID, space.getMultiplicity(), 
					"thumbnails" + File.separator + "space.png", 
					"images" + File.separator + "space.png",
					space.getDomainConcept().toString());
			
		} /* TODO other space extents */
		
		IExtent time = _context.getTime();
		if (time != null && time.getMultiplicity() > 1) {
		
			/*
			 * TODO create image of time grid if multiple - should be easy.
			 */
		}
		
		int i = 0;
		for (final IState s : _context.getStates()) {
			

			/*
			 * produce images, set name in index
			 */
			
		}
		
		/*
		 * serialize index to xml
		 */
		XStream xstream = new XStream(new StaxDriver());
		String xml = xstream.toXML(_index);
		try {
			FileUtils.writeStringToFile(new File(locDir + File.separator + "index.xml"), xml);
		} catch (IOException e1) {
			throw new ThinklabIOException(e1);
		}
		
				
		return location;
	}

	@Override
	public void restore(String location) throws ThinklabException {
		// TODO Auto-generated method stub

	}

}
