package org.integratedmodelling.modelling.commands;

import java.io.File;

import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.exception.ThinklabException;
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
		argumentDescriptions="colormap description, e.g. greyscale(12)",
		argumentTypes="thinklab-core:Text")
public class Cmap implements ICommandHandler {

	@Override
	public IValue execute(Command command, ISession session)
			throws ThinklabException {

		ColorMap cmap = ColorMap.getColormap(command.getArgumentAsString("cmap"), -1);
		if (cmap == null) {
			session.getOutputStream().println("colormap is null; exiting");
		} else {
			File f = cmap.getColorbar(48, null);
			new Picture(f).show();
		}
		
		return null;
	}

}
