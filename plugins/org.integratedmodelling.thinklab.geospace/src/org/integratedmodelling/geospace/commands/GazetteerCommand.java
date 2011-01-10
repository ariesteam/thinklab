package org.integratedmodelling.geospace.commands;

import java.net.URL;
import java.util.Properties;

import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.geospace.coverage.CoverageFactory;
import org.integratedmodelling.geospace.coverage.ICoverage;
import org.integratedmodelling.geospace.coverage.VectorCoverage;
import org.integratedmodelling.geospace.interfaces.IGazetteer;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.command.InteractiveCommandHandler;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabInappropriateOperationException;
import org.integratedmodelling.thinklab.interfaces.annotations.ThinklabCommand;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.utils.MiscUtilities;

/**
 * Driver for everything that can be done with storylines. Subcommands are
 * 
 * 	create <namespace> [model context ...]
 *  update <namespace> [model context ...]
 *  run    {-o <outfile>|-v|-s <scenario>} <namespace> [<context>]
 *  test   {-o <outfile>|-v|-s <scenario>|-r <report>|-e <email>} <namespace> [<context>]
 *  copy   <namespace-from> <namespace-to> [model context ...]
 *  
 * @author Ferdinando
 *
 */
@ThinklabCommand(name="gazetteer",
		argumentNames="action",
		argumentTypes="thinklab-core:Text",
		argumentDescriptions="action {create|delete|reset|import}",
		optionalArgumentNames="arg0,arg1,arg2",
		optionalArgumentDefaultValues="_,_,_",
		optionalArgumentTypes="thinklab-core:Text,thinklab-core:Text,thinklab-core:Text",
		optionalArgumentDescriptions=" , , ",
		optionNames="g",
		optionLongNames="gazetteer",
		optionTypes="thinklab-core:Text",
		optionDescriptions="name of gazetteer to use (default to personal gazetteer)",
		optionArgumentLabels="gazetteer"
)
public class GazetteerCommand extends InteractiveCommandHandler {

	public IGazetteer getGazetteer(String gname, boolean create) {
		
		IGazetteer ret = Geospace.get().retrieveGazetteer(gname);
		
		if (ret == null && create) {
			
		}
		return ret;
	}
	
	@Override
	protected IValue doInteractive(Command command, ISession session)
			throws ThinklabException {

		String action = command.getArgumentAsString("action");

		String gname = System.getProperty("user.name");
		if (gname == null)
			gname = "default";
		if (command.hasOption("gazetteer")) {
			gname = command.getOptionAsString("gazetteer");
		}
		
		IGazetteer gaz = null;
		
		if (action.equals("create")) {
			
			if (getGazetteer(gname, false) != null) {
				throw new ThinklabInappropriateOperationException(
						"gazetteer " + gname + " already exists");
			}
			
			gaz = getGazetteer(gname, true);
			
		} else if (action.equals("delete")) {
			
			
			
		} else if (action.equals("reset")) {
			
			gaz = getGazetteer(gname, true);
			gaz.resetToEmpty();
			
		} else if (action.equals("import")) {
			
			gaz = getGazetteer(gname, true);
			if (gaz.isReadOnly()) {
				throw new ThinklabInappropriateOperationException(
						"cannot import into read-only gazetteer " + gname);
			}
			
			/*
			 * create field descriptor interactively
			 */
			String url = command.getArgumentAsString("arg0");
			Properties prop = CoverageFactory.getCoverageProperties(url);
			Properties gprop = getImportProperties(prop);
			
			gaz.importLocations(url, gprop);
		}
	
		return null;
	
	}

	/**
	 * Interactively define the import properties for the given coverage based
	 * on the coverage properties passed.
	 * 
	 * @param prop
	 * @return
	 */
	private Properties getImportProperties(Properties prop) {
		
//		thinklab.gazetteer.projection=EPSG:4326
//		#thinklab.gazetteer.simplify=0.1
//		thinklab.gazetteer.field.name=@{NAME}_@{STATE}
//		thinklab.gazetteer.field.label=@{NAME} County, @{STATE}
//		thinklab.gazetteer.field.continent=North America
//		thinklab.gazetteer.field.geofeature=County

		return null;
	}

}
