package org.integratedmodelling.modelling.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.integratedmodelling.geospace.Geospace;
import org.integratedmodelling.modelling.ModellingPlugin;
import org.integratedmodelling.modelling.annotation.ModelAnnotation;
import org.integratedmodelling.modelling.model.Model;
import org.integratedmodelling.modelling.model.ModelFactory;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.command.InteractiveSubcommandInterface;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabResourceNotFoundException;
import org.integratedmodelling.thinklab.interfaces.annotations.ThinklabCommand;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.interfaces.query.IQueryResult;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;
import org.integratedmodelling.thinklab.interfaces.storage.IKnowledgeImporter;
import org.integratedmodelling.thinklab.kbox.KBoxManager;
import org.integratedmodelling.utils.MiscUtilities;
import org.integratedmodelling.utils.Polylist;

@ThinklabCommand(
		name="annotate",
		description="scan model structure for concepts and create undefined knowledge",
		argumentNames="model",
		argumentDescriptions="model id",
		argumentTypes="thinklab-core:Text")
public class Annotate extends InteractiveSubcommandInterface {
	
	private ModelAnnotation annotation = null;
	private IKBox kbox = KBoxManager.get();
	
	// any newly annotated data we found
	private ArrayList<Polylist> imported = new ArrayList<Polylist>();
	private ArrayList<IKnowledgeImporter> importers = 
		new ArrayList<IKnowledgeImporter>();

	@Override
	public IValue execute(Command command, ISession session)
			throws ThinklabException {

		Model model =
			ModelFactory.get().requireModel(command.getArgumentAsString("model"));
		
		session.pushVariable(
				ModellingPlugin.ANNOTATION_UNDERWAY, new ModelAnnotation(session));
		model.validateConcepts(session);
		this.annotation = 
			(ModelAnnotation) session.popVariable(ModellingPlugin.ANNOTATION_UNDERWAY);
		
		this.annotation.dump(session.getOutputStream());
		
		return super.execute(command, session);
	}

	@Override
	protected IValue cmd(String cmd, String[] arguments)
		throws ThinklabException {
		
		IValue ret = null;
		
		if (cmd.equals("import")) {
			
			// import ontology
			
		} else if (cmd.equals("save")) {
			
			// save ontologies and data so far
			saveResults();
			
		} else if (cmd.equals("annotate")) {
			
			// save ontologies and data so far
			if (annotation.getUndefinedConceptsCount() == 0) {
				say("no undefined concepts.");
			} else {
				annotateMissingConcepts();
			}
			
		} else if (cmd.equals("link")) {
			
			// find data for each unresolved requirements, offer option of creating
			// skeleton kbox.
			// save ontologies and data so far
			if (annotation.getUnresolvedConceptsCount() == 0) {
				say("no undefined concepts.");
			} else {
				findData();
			}
		} else if (cmd.equals("connect")) {
			
			// connect source
			String format = null;
			if (arguments.length > 2) 
				format = arguments[2];
			else format = MiscUtilities.getFileExtension(arguments[1]);
			
			IKnowledgeImporter importer = null;
			if (format != null) {
				importer = KBoxManager.get().getKnowledgeImporter(format);
			}
			if (importer == null)
				throw new ThinklabResourceNotFoundException(
						"knowledge importer for URL " +
						arguments[1] +
						" could not be found");
			
			importer.initialize(arguments[1], Geospace.get().getProperties());
			importers.add(importer);
			say("connected to " + arguments[1]);
			
		} else if (cmd.equals("help")) {
			
			// print usage summary
			say("help\tthis output");
			say("link\tinteractively find or define data for unresolved concepts in linked kboxes");
			say("annotate\tinteractively create missing concepts and ontologies");
			say("connect <url> [format]\tconnect a URL to import data from");
			say("save\twrite ontologies and data specifications to files");
			
		} else {
			say("unknown subcommand: " + cmd);
		}
			
		return ret;
	}

	private void findData() throws ThinklabException {
		
		HashMap<String, IQueryResult> res = annotation.findAllUnresolved(kbox);
		int n = 1;
		
		ArrayList<String> kk = new ArrayList<String>();
		for (String s : res.keySet()) {
			kk.add(s);
		}
		Collections.sort(kk);
		
		for (String s : kk) {
			say(n++ + ". " + s + ": " + res.get(s).getResultCount() + " matches");
		}
		
		/*
		 * subcommand loop: either list <n>, new <n>, or empty (exit)
		 */
		while (true) {
			
			String inp = ask("say list <n>, find <n>, or return to exit: ");
			
			if (inp == null)
				break;
			
			if (inp.startsWith("list")) {
				
				int z = Integer.parseInt(inp.split("\\s+")[1]);
				String zz = kk.get(z-1);
				IQueryResult qr = res.get(zz);
				for (int i = 0; i < qr.getResultCount(); i++) {
					say(z + "." + i + ". " + 
							Polylist.prettyPrint(qr.getResultAsList(i, null)));
				}
				
			} else if (inp.startsWith("find")) {
				
				int z = Integer.parseInt(inp.split("\\s+")[1]);
				String zz = kk.get(z-1);
				Polylist p = findObservation(zz);
				if (p != null) {
					imported.add(p);
				}
			}
		}
	}

	private Polylist findObservation(String zz) {
		
		/*
		 * create a suitable observation for the missing requirement or find one
		 * in any of the connected importers; if successful, return its list
		 * representation so we can save it and write a kbox file later. 
		 */
		// options should be: inline state (just a number), find in importer, 
		// WCS, WFS, data file, etc. - should use the importer
		// interface, which needs to be rediscussed at this point.

		return null;
	}

	private void annotateMissingConcepts() {
		// TODO Auto-generated method stub
		
	}

	private void saveResults() {
		// TODO Auto-generated method stub
		
	}

}