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
package org.integratedmodelling.modelling.commands;

import java.io.File;

import org.integratedmodelling.modelling.visualization.VisualizationFactory;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.SemanticType;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.annotations.ThinklabCommand;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.commands.ICommandHandler;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.utils.image.ColorMap;
import org.integratedmodelling.utils.image.Picture;

@ThinklabCommand(
		name="colormap",
		description="create and display a colormap from a Thinklab colormap descriptor",
		argumentNames="cmap",
		argumentDescriptions="colormap description, e.g. greyscale(12), or a concept",
		argumentTypes="thinklab-core:Text",
		optionalArgumentNames="levels",
		optionalArgumentDefaultValues="-1",
		optionalArgumentDescriptions="number of colors in colormap (if looking up a concept)",
		optionalArgumentTypes="thinklab-core:Integer",
		optionArgumentLabels="levels")
public class Cmap implements ICommandHandler {

	@Override
	public IValue execute(Command command, ISession session)
			throws ThinklabException {

		String cmdef = command.getArgumentAsString("cmap");
		int levels = command.getArgument("levels").asNumber().asInteger();

		ColorMap cmap = null;
		if (SemanticType.validate(cmdef)) {
			if (levels <= 0)
				throw new ThinklabValidationException("must specify number of levels > 0");
			cmap =
				VisualizationFactory.get().getColormap(
						KnowledgeManager.get().requireConcept(cmdef), levels, false);
		} else {
			cmap = ColorMap.getColormap(cmdef, levels, null);
		}
		
		if (cmap == null) {
			session.getOutputStream().println("no valid colormap for this definition and number of levels");
		} else {
			File f = cmap.getColorbar(48, null);
			new Picture(f).show();
		}
		
		return null;
	}

}
