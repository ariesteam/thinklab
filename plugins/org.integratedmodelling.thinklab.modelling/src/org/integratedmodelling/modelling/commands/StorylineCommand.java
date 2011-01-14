package org.integratedmodelling.modelling.commands;

import java.io.File;
import java.io.PrintStream;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.integratedmodelling.corescience.interfaces.IContext;
import org.integratedmodelling.modelling.interfaces.IModel;
import org.integratedmodelling.modelling.interfaces.IVisualization;
import org.integratedmodelling.modelling.model.ModelFactory;
import org.integratedmodelling.modelling.storyline.Storyline;
import org.integratedmodelling.modelling.storyline.StorylineFactory;
import org.integratedmodelling.modelling.visualization.FileVisualization;
import org.integratedmodelling.modelling.visualization.storyline.StorylineTemplate;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.command.InteractiveCommandHandler;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.annotations.ThinklabCommand;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
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
		optionalArgumentDefaultValues="_,_,_",
		optionalArgumentTypes="thinklab-core:Text,thinklab-core:Text,thinklab-core:Text",
		optionalArgumentDescriptions=" , , ")
public class StorylineCommand extends InteractiveCommandHandler {

	@Override
	protected IValue doInteractive(final Command command, final ISession session)
			throws ThinklabException {
		
		String action = command.getArgumentAsString("action");
		String path = command.getArgumentAsString("path");
		final ITaskScheduler scheduler = new SerialTaskScheduler();
		
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
		
		List<File> fpath = StorylineFactory.getTemplatePath(path);
		
		if (action.equals("create")) {
			
		} else if (action.equals("update")) {
			
		} else if (action.equals("run")) {
			
			IContext context = 
				ModelFactory.get().requireContext(command.getArgumentAsString("arg0"));
			Storyline storyline = StorylineFactory.getStoryline(fpath, true);
			storyline.setContext(context);
			storyline.compute(new Listener());
			scheduler.start();
			
		} else if (action.equals("test")) {
			
			
		} else if (action.equals("copy")) {
			
		}  else if (action.equals("list")) {
			
			Storyline storyline = StorylineFactory.getStorylines(path);
			listStoryline(storyline, session.getOutputStream(), 0);
			
		} 
		
		return null;
	}

	private void listStoryline(Storyline storyline, PrintStream out, int spaces) {
		out.println(MiscUtilities.spaces(spaces) + storyline.toString());
		for (Storyline s : storyline.getChildren())
			listStoryline(s, out, spaces + 2);
	}

}
