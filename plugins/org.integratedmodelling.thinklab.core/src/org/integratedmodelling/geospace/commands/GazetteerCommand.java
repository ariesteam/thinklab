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
package org.integratedmodelling.geospace.commands;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabIOException;
import org.integratedmodelling.exceptions.ThinklabResourceNotFoundException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.geospace.coverage.CoverageFactory;
import org.integratedmodelling.geospace.interfaces.IGazetteer;
import org.integratedmodelling.thinklab.api.knowledge.ISemanticLiteral;
import org.integratedmodelling.thinklab.api.runtime.ISession;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.command.InteractiveCommandHandler;
import org.integratedmodelling.thinklab.interfaces.annotations.ThinklabCommand;
import org.integratedmodelling.thinklab.kbox.sql.postgres.PostgreSQLServer;

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

	public IGazetteer getGazetteer(String gname, boolean create) throws ThinklabException, ThinklabException {
		
		IGazetteer ret = Geospace.get().retrieveGazetteer(gname);
		
		if (ret == null && create) {
			
			if (!PostgreSQLServer.haveDatabase(gname))
				throw new ThinklabResourceNotFoundException(
						"database not found. " +
						"Please create a postgis database named " + gname);
			
			File gdir = new File(
				System.getProperty("user.home") + 
				File.separator +
				"gazetteers" +
				File.separator +
				gname);
			
			gdir.mkdirs();
			String uri =  PostgreSQLServer.getDefaultURI(gname);
			
			Properties gazprop = new Properties();
			gazprop.setProperty("uri", uri);

			try {
				FileOutputStream fout = new FileOutputStream(gdir + File.separator + "gazetteer.properties");
				gazprop.store(fout, null);
				fout.close();
			} catch (Exception e) {
				throw new ThinklabIOException(e);
			}
			
//			ret = new PostgisGazetteer();
//			ret.initialize(gazprop);
			
			Geospace.get().addGazetteer(gname, ret);
		}
		
		return ret;
	}
	
	@Override
	protected ISemanticLiteral doInteractive(Command command, ISession session)
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
				throw new ThinklabValidationException(
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
				throw new ThinklabValidationException(
						"cannot import into read-only gazetteer " + gname);
			}
			
			/*
			 * create field descriptor interactively
			 */
			String url = command.getArgumentAsString("arg0");
			String arg = command.getArgumentAsString("arg1");
			Properties prop = CoverageFactory.getCoverageProperties(url, arg);
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
	 * @throws ThinklabException 
	 */
	private Properties getImportProperties(Properties prop) throws ThinklabException {
		
		Properties ret = new Properties(prop);

//		ret.put(PostgisGazetteer.CRS_PROPERTY, 
//				prop.getProperty(CoverageFactory.CRS_PROPERTY));
		
		String[] fields = 
			("id," + prop.getProperty(CoverageFactory.FIELD_NAMES_PROPERTY)).split(",");
		
		say("Fields available: ");
		int i = 0;
		for (String f : fields)
			say("  " + (i++) + " " + f);
		say("");
		
		String idt = ask("template for ID field (must be unique in gazetteer) [return = shape id]? ");
//		if (idt != null) {
//			ret.put(PostgisGazetteer.SHAPE_ID_TEMPLATE, idt);
//			ret.put(PostgisGazetteer.FIELD_PROPERTY_PREFIX + "name", idt);
//		} else {
//			ret.put(PostgisGazetteer.FIELD_PROPERTY_PREFIX + "name", "@{id}");
//		}
		
		
		// TODO fields later. Example:
		//		thinklab.gazetteer.projection=EPSG:4326
		//		#thinklab.gazetteer.simplify=0.1
		//		thinklab.gazetteer.field.name=@{NAME}_@{STATE}
		//		thinklab.gazetteer.field.label=@{NAME} County, @{STATE}	
		//		thinklab.gazetteer.field.continent=North America
		//		thinklab.gazetteer.field.geofeature=County

		return ret;
	}

}
