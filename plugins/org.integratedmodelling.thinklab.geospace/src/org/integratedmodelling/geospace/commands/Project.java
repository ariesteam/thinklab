package org.integratedmodelling.geospace.commands;

import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.thinklab.api.knowledge.IValue;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.interfaces.annotations.ThinklabCommand;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Coordinate;

@ThinklabCommand(
		name="project",
		description="project a point from a coordinate system to another",
		argumentNames="source-srid,destination-srid,x-coordinate,y-coordinate",
		argumentDescriptions="source EPSG code,destination EPSG code, X coordinate, Y coordinate",
		argumentTypes="thinklab-core:Text,thinklab-core:Text,thinklab-core:Text,thinklab-core:Text,")
public class Project implements ICommandHandler {

	@Override
	public IValue execute(Command command, ISession session)
			throws ThinklabException {

		String ssrid = command.getArgumentAsString("source-srid");
		String dsrid = command.getArgumentAsString("destination-srid");
		
		String zx = command.getArgumentAsString("x-coordinate");
		String zy = command.getArgumentAsString("y-coordinate");
		
		if (zx.startsWith("\\"))
			zx = zx.substring(1);
		if (zy.startsWith("\\"))
			zy = zy.substring(1);

		double xcoor = Double.parseDouble(zx);
		double ycoor = Double.parseDouble(zy);
		
		CoordinateReferenceSystem sr = Geospace.getCRSFromID("EPSG:" + ssrid);
		CoordinateReferenceSystem dr = Geospace.getCRSFromID("EPSG:" + dsrid);
		
		Coordinate nc = null; 
		try {
			nc = JTS.transform(new Coordinate(xcoor, ycoor), null, CRS.findMathTransform(sr, dr));
		} catch (Exception e) {
			throw new ThinklabException(e);
		}
		
		session.getOutputStream().println("" + nc.x + " " + nc.y);
		
		return null;
	}

}
