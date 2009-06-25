/**
 * ArealLocation.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabGeospacePlugin.
 * 
 * ThinklabGeospacePlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabGeospacePlugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the software; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * ----------------------------------------------------------------------------------
 * 
 * @copyright 2008 www.integratedmodelling.org
 * @author    Ferdinando Villa (fvilla@uvm.edu)
 * @date      Jan 17, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.geospace.implementations.observations;

import org.integratedmodelling.corescience.implementations.observations.Observation;
import org.integratedmodelling.corescience.interfaces.cmodel.IConceptualModel;
import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.geospace.implementations.cmodels.ArealLocationConceptualModel;
import org.integratedmodelling.geospace.interfaces.IGeolocatedObject;
import org.integratedmodelling.geospace.literals.ShapeValue;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.annotations.InstanceImplementation;
import org.integratedmodelling.thinklab.interfaces.knowledge.IInstance;
import org.integratedmodelling.thinklab.interfaces.knowledge.IParseable;

import com.vividsolutions.jts.geom.Geometry;

/**
 * This one is technically unnecessary, as ArealFeatureSet subsumes it, but we keep it 
 * for now as only ArealLocation admits literals, and this makes things easier.
 * @author UVM Affiliate
 *
 */
@InstanceImplementation(concept="geospace:ArealLocation")
public class ArealLocation extends Observation implements IParseable, IGeolocatedObject {

	ShapeValue boundingBox = null;
	ShapeValue shape = null;
	ShapeValue centroid = null;
	
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.corescience.observation.Observation#validate(org.integratedmodelling.thinklab.interfaces.IInstance)
	 */
	@Override
	public void validate(IInstance i) throws ThinklabException {
		
		/*
		 * add properties with bounding box and centroid if they're not in the
		 * OWL model.
		 */
		try {
			this.shape = (ShapeValue)getDataSource();

			if (i.getRelationships(Geospace.hasBoundingBox()).size() == 0) {
			
				Geometry bbox = shape.getGeometry().getEnvelope();
				i.addLiteralRelationship(Geospace.hasBoundingBox(),
						(boundingBox = new ShapeValue(bbox)));
			}
			
			if (i.getRelationships(Geospace.hasCentroid()).size() == 0) {
				i.addLiteralRelationship(Geospace.hasCentroid(),
						(centroid = new ShapeValue(shape.getGeometry().getCentroid())));	
			}

		} catch (ThinklabException e) {
			throw new ThinklabValidationException(e);
		}
		
		super.validate(i);
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.corescience.observation.Observation#createMissingConceptualModel()
	 */
	@Override
	public IConceptualModel createMissingConceptualModel()
			throws ThinklabException {
		return new ArealLocationConceptualModel((ShapeValue)getDataSource());
	}

	@Override
	public void parseSpecifications(IInstance inst, String literal) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ShapeValue getBoundingBox() {
		return boundingBox;
	}

	@Override
	public ShapeValue getCentroid() {
		return centroid;
	}

	@Override
	public ShapeValue getShape() {
		return shape;
	}

	
	
	
}
