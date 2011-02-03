package org.integratedmodelling.modelling.commands;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;

import org.integratedmodelling.corescience.context.DatasourceStateAdapter;
import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.corescience.interfaces.IState;
import org.integratedmodelling.corescience.metadata.Metadata;
import org.integratedmodelling.modelling.context.Context;
import org.integratedmodelling.modelling.data.CategoricalDistributionDatasource;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.modelling.interfaces.IVisualization;
import org.integratedmodelling.modelling.literals.ContextValue;
import org.integratedmodelling.modelling.model.Model;
import org.integratedmodelling.modelling.model.ModelFactory;
import org.integratedmodelling.modelling.storyline.Storyline;
import org.integratedmodelling.modelling.storyline.StorylineFactory;
import org.integratedmodelling.modelling.visualization.FileVisualization;
import org.integratedmodelling.modelling.visualization.knowledge.TypeManager;
import org.integratedmodelling.modelling.visualization.knowledge.VisualConcept;
import org.integratedmodelling.modelling.visualization.storyline.StorylineTemplate;
import org.integratedmodelling.modelling.visualization.storyline.impl.TemplateEditor;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.command.InteractiveCommandHandler;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.annotations.ThinklabCommand;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.interfaces.query.IQueryResult;
import org.integratedmodelling.thinklab.kbox.KBoxManager;
import org.integratedmodelling.utils.CamelCase;
import org.integratedmodelling.utils.MiscUtilities;
import org.integratedmodelling.utils.exec.ITaskScheduler;
import org.integratedmodelling.utils.exec.SerialTaskScheduler;

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
@ThinklabCommand(name="storyline",
		argumentNames="action,path",
		argumentTypes="thinklab-core:Text,thinklab-core:Text",
		argumentDescriptions="action {create|update|run|test|copy},storyline namespace",
		optionalArgumentNames="arg0,arg1,arg2",
		optionalArgumentDefaultValues=" , , ",
		optionalArgumentTypes="thinklab-core:Text,thinklab-core:Text,thinklab-core:Text",
		optionalArgumentDescriptions=" , , ",
		optionNames="c,extends",
		optionLongNames="concept,extends",
		optionDescriptions="concept to use in storyline,storyline to derive from",
		optionArgumentLabels="concept,storyline path",
		optionTypes="thinklab-core:Text,thinklab-core:Text")
public class StorylineCommand extends InteractiveCommandHandler {

	@Override
	protected IValue doInteractive(final Command command, final ISession session)
			throws ThinklabException {
		
		String action = command.getArgumentAsString("action");
		String path = command.getArgumentAsString("path");
		final ITaskScheduler scheduler = new SerialTaskScheduler();
		
		IConcept concept = null;
		String   importd = null;
		IModel model = null;
		IContext context = null;

		if (command.hasOption("concept")) {
			concept = KnowledgeManager.getConcept(command.getOptionAsString("concept"));
		}
		if (command.hasOption("extends")) {
			importd = command.getOptionAsString("extends");
		}
		
		class Listener implements Storyline.Listener {

			@Override
			public IVisualization createVisualization(IModel model,
					IContext iContext) {
				return new FileVisualization();
			}

			@Override
			public void onStatusChange(Storyline storyline, int original, int newstatus) {
				session.print(
						storyline +
						" changed status from " + 
						Storyline.statusLabels[original] + 
						" to " + 
						Storyline.statusLabels[newstatus]);			
			}

			@Override
			public ITaskScheduler getScheduler() {
				return scheduler;
			}

			@Override
			public ISession getSession() {
				return session;
			}
		}
		
		if (action.equals("create")) {
			
			if (concept == null)
				concept = KnowledgeManager.getConcept(ask("concept? "));
			
			StorylineTemplate st = StorylineFactory.createTemplate(path, concept);
			if (importd != null) {
				st.addField("inherit", importd, null);
				st.save();
			} else {
				st.createInfoPage(path, concept);
			}

			if (command.hasArgument("arg0")) {
				model = ModelFactory.get().requireModel(
						command.getArgumentAsString("arg0"));
			}
			if (command.hasArgument("arg1")) {
				context = ModelFactory.get().requireContext(
						command.getArgumentAsString("arg1"));
			}
			
			if (model != null) {
				syncModels(path, model, context, session);
			}
			
			say("created storyline template at " + st.getSourceFile());
			
		} else if (action.equals("update")) {
			
			if (command.hasArgument("arg0")) {
				model = ModelFactory.get().requireModel(
						command.getArgumentAsString("arg0"));
			}
			if (command.hasArgument("arg1")) {
				context = ModelFactory.get().requireContext(
						command.getArgumentAsString("arg1"));
			} else {
				throw new ThinklabValidationException(
						"please specify a context for " + model + " to run");
			}
			
			if (model != null) {
				syncModels(path, model, context, session);
			}
			
		} else if (action.equals("enable") || action.equals("disable")) {
			
			Storyline sl = StorylineFactory.getStoryline(path);
			sl.getTemplate().remove("disabled");
			sl.getTemplate().addField("disabled", 
					action.equals("enable") ? "false"  : "true", 
					null);
			sl.getTemplate().save();
			
		}else if (action.equals("run")) {
			
			context = 
				ModelFactory.get().requireContext(command.getArgumentAsString("arg0"));
			Storyline storyline = StorylineFactory.getStorylines(path);
			storyline.setContext(context);
			storyline.compute(new Listener());
			scheduler.start();
			
		} else if (action.equals("test")) {
			
			
		}  else if (action.equals("view")) {
			
			TemplateEditor.run(StorylineFactory.getStoryline(path));
			
		} else if (action.equals("copy")) {
			
			String path2 = command.getArgumentAsString("arg0");
			Storyline sl = StorylineFactory.getStoryline(path);
			if (concept == null)
				concept = sl.getObservable();
			StorylineTemplate st = StorylineFactory.createTemplate(path2, concept);
			st.addField("inherit", path, null);
			st.save();
			
		}  else if (action.equals("list")) {
			
			Storyline storyline = StorylineFactory.getStorylines(path);
			listStoryline(storyline, session.getOutputStream(), 0);
			
		} 
		
		return null;
	}

	private void syncModels(String path, IModel model, IContext context, ISession session) throws ThinklabException {

		Storyline sl = StorylineFactory.getStoryline(path);
		boolean present = false;
		for (StorylineTemplate.Model ms : sl.getTemplate().getModelSpecifications()) {
			if (ms.getModel().getName().equals(model.getName()) && 
				((Context)(ms.getContext())).getName().equals(((Context)context).getName())) {
				present = true;
				break;
			}
		}
		
		if (!present) {
			StorylineTemplate.Model nm = new StorylineTemplate.Model();
			nm.addField("id", model.getName(), null);
			nm.addField("context", ((Context)context).getName(), null);
			sl.getTemplate().addChild("model", nm, null);
		}
		
		/*
		 * Run all models and create pages for all the missing
		 * results.
		 * If no error running models, save template
		 */
		HashMap<IConcept, IState> states = new HashMap<IConcept, IState>();
		HashSet<IConcept> knownst = new HashSet<IConcept>();

		for (StorylineTemplate.Page pg : sl.getTemplate().getPages()) {
			knownst.add(pg.getConcept());
		}
		
		for (StorylineTemplate.Model ms : sl.getTemplate().getModelSpecifications()) {

			IModel   mod = ms.getModel();
			IContext con = ms.getContext();

			try {	
				IQueryResult r = 
					ModelFactory.get().run((Model) mod, KBoxManager.get(), session, null, con);		
			
				if (r.getTotalResultCount() > 0) {
					
					IValue res = r.getResult(0, session);
					IContext result = ((ContextValue)res).getObservationContext();
					
					for (IState s : result.getStates())
						if (!knownst.contains(s.getObservableClass()))
							states.put(s.getObservableClass(), s);
				}
			} catch (ThinklabException e) {
				session.print("error running " + mod.getName() + " in " + 
						((Context)con).getName() + 
						": skipping page creation for it");
			}
		}
		
		for (IConcept c : states.keySet()) {
			
			VisualConcept vc = TypeManager.get().getVisualConcept(c);
			StorylineTemplate.Page pg = new StorylineTemplate.Page();

			HashMap<String,String> attr1 = new HashMap<String, String>();
			HashMap<String,String> attr2 = new HashMap<String, String>();
			
			IState state = states.get(c);
			if (state instanceof DatasourceStateAdapter)
				state = ((DatasourceStateAdapter)state).getOriginalState();
			
			String units = (String) state.getMetadata().get(Metadata.UNIT_SPECS);
			
			pg.addField("concept", c.toString(), null);
			pg.addField("name", vc.getLabel(), null);
			pg.addField("title", vc.getLabel(), null);
			pg.addField("description", vc.getDescription(), null);
			pg.addField("runninghead", vc.getLabel(), null);
			pg.addField("see-also", "", null);
			pg.addField("credits", "", null);
			pg.addField("group", "", null);
			pg.addField("disabled", "false", null);
			attr1.put("default", "true");
			pg.addField("plot-type", "geosurface-2d", attr1);
			pg.addField("plot-type", "geocontour-2d", null);
			
			if (state instanceof CategoricalDistributionDatasource)
				pg.addField("plot-type", "uncertainty-2d", null);
			if (units != null) 
				pg.addField("units", units, null);
			
			attr2.put("id", CamelCase.toLowerCase(c.getLocalName(), '-'));
			sl.getTemplate().addChild("page", pg, attr2);
		}
		
		sl.getTemplate().save();
	}

	private void listStoryline(Storyline storyline, PrintStream out, int spaces) {
		out.println(MiscUtilities.spaces(spaces) + storyline.toString());
		for (Storyline s : storyline.getChildren())
			listStoryline(s, out, spaces + 2);
	}

}
