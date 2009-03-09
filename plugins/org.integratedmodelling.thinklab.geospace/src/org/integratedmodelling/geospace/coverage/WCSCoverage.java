package org.integratedmodelling.geospace.coverage;

import java.io.File;

import org.deegree.ogcwebservices.wcs.getcoverage.Output;
import org.geotools.referencing.CRS;
import org.integratedmodelling.corescience.interfaces.cmodel.IConceptualModel;
import org.integratedmodelling.geospace.extents.ArealExtent;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.opengis.geometry.BoundingBox;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class WCSCoverage implements ICoverage {

	CoordinateReferenceSystem crs = null;
	
	public WCSCoverage(Output output) throws ThinklabValidationException {
		try {
			crs = CRS.decode(output.getCrs().getCode());
		} catch (Exception e) {
			throw new ThinklabValidationException(
					"wcs coverage: reference system unknown: " + 
					output.getCrs().getCode());
		}
	}

	@Override
	public BoundingBox getBoundingBox() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CoordinateReferenceSystem getCoordinateReferenceSystem() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCoordinateReferenceSystemCode() throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getLatLowerBound() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getLatUpperBound() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getLayerName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getLonLowerBound() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getLonUpperBound() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getSourceUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getSubdivisionValue(int subdivisionOrder,
			IConceptualModel conceptualModel, ArealExtent extent)
			throws ThinklabValidationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void loadData() {
		// TODO read data from output

	}

	@Override
	public VectorCoverage requireCRS(CoordinateReferenceSystem crs)
			throws ThinklabException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ICoverage requireMatch(ArealExtent arealExtent,
			boolean allowClassChange) throws ThinklabException {
		// TODO return a new coverage from service to reflect new extent
		return null;
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

	@Override
	public void write(File f) throws ThinklabException {
		// TODO Auto-generated method stub

	}

	@Override
	public void writeImage(File outfile, String format)
			throws ThinklabIOException {
		// TODO Auto-generated method stub

	}

}
