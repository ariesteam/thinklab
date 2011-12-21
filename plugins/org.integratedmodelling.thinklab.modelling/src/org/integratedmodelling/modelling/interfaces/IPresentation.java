/**
 * Copyright 2011 The ARIES Consortium (http://www.ariesonline.org) and
 * www.integratedmodelling.org. 

   This file is part of Thinklab.

   Thinklab is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published
   by the Free Software Foundation, either version 3 of the License,
   or (at your option) any later version.

   Thinklab is distributed in the hope that it will be useful, but
   WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Thinklab.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.integratedmodelling.modelling.interfaces;

import java.util.Properties;

import org.integratedmodelling.modelling.storyline.Storyline;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;

public interface IPresentation {

	/**
	 * Render the full storyline we've been initialized with, appropriately
	 * handling its hierarchical structure for the medium.
	 * 
	 * @throws ThinklabException
	 */
	public abstract void render() throws ThinklabException;

	/**
	 * Render only the given concept within the storyline - e.g. a single
	 * page in the associated visualization.
	 * 
	 * @param concept
	 * @throws ThinklabException
	 */
	public abstract void render(IConcept concept) throws ThinklabException;
	
	/**
	 * Communicate the storyline we'll have to work with. It should not be
	 * modified by us - if there's no visualization in it, just do not
	 * visualize data.
	 * 
	 * @param storyline
	 * @param properties TODO
	 */
	public abstract void initialize(Storyline storyline, Properties properties);
	
}
