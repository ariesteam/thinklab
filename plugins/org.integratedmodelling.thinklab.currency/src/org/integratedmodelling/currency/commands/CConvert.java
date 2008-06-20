/**
 * CConvert.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 21, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabCurrencyPlugin.
 * 
 * ThinklabCurrencyPlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabCurrencyPlugin is distributed in the hope that it will be useful,
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
 * @date      Jan 21, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.currency.commands;

import org.integratedmodelling.corescience.CoreSciencePlugin;
import org.integratedmodelling.corescience.exceptions.ThinklabContextualizationException;
import org.integratedmodelling.corescience.interfaces.IContextualizationWorkflow;
import org.integratedmodelling.corescience.interfaces.IObservation;
import org.integratedmodelling.corescience.workflow.DefaultWorkflow;
import org.integratedmodelling.corescience.workflow.debug.DebugWorkflow;
import org.integratedmodelling.currency.CurrencyPlugin;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.command.CommandDeclaration;
import org.integratedmodelling.thinklab.command.CommandPattern;
import org.integratedmodelling.thinklab.constraint.Conformance;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabPluginException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.IAction;
import org.integratedmodelling.thinklab.interfaces.ICommandOutputReceptor;
import org.integratedmodelling.thinklab.interfaces.IConformance;
import org.integratedmodelling.thinklab.interfaces.IInstance;
import org.integratedmodelling.thinklab.interfaces.IKnowledgeSubject;
import org.integratedmodelling.thinklab.interfaces.ISession;
import org.integratedmodelling.thinklab.interfaces.IValue;
import org.integratedmodelling.thinklab.value.NumberValue;
import org.integratedmodelling.thinklab.value.TextValue;
import org.integratedmodelling.time.TimePlugin;
import org.integratedmodelling.time.values.TimeValue;

/**
 * The link command should take two observations as parameters, ensure their observables are
 * conformant (possibly allowing to use a specified conformance policy, creating a default
 * one if not) and if so, create a link between the two, so that the next contextualization
 * will use the linked observation and mediate as needed.
 * 
 * @author Ferdinando Villa
 *
 */
public class CConvert extends CommandPattern {

	class ConvertAction implements IAction {

		public IValue execute(Command command, ICommandOutputReceptor outputDest, ISession session, KnowledgeManager km) throws ThinklabException {
			
			NumberValue amnt = (NumberValue) command.getArgument("amount");
			TextValue cur1 = (TextValue) command.getArgument("currency1");
			TextValue cur2 = (TextValue) command.getArgument("currency2");
			TimeValue dat1 = (TimeValue) command.getArgument("date1");
			TimeValue dat2 = (TimeValue) command.getArgument("date2");

			double ret =
				CurrencyPlugin.get().getConverter().convert(
						amnt.asDouble(),
						cur1.toString(),
						dat1,
						cur2.toString(),
						dat2);
					

			return new NumberValue(ret);
		}
		
	}

	@Override
	public CommandDeclaration createCommand()  throws ThinklabException  {
		
		CommandDeclaration cd = new CommandDeclaration("cconvert", "convert currencies and adjust for inflation");

		cd.addMandatoryArgument("amount", "amount to convert", 
				KnowledgeManager.Double().getSemanticType());
		cd.addMandatoryArgument("currency1", "source currency", 
				KnowledgeManager.Text().getSemanticType());
		cd.addMandatoryArgument("date1", "date of source value", 
				TimePlugin.DateTime().getSemanticType());
		cd.addMandatoryArgument("currency2", "destination currency", 
				KnowledgeManager.Text().getSemanticType());
		cd.addMandatoryArgument("date2", "date of destination value", 
				TimePlugin.DateTime().getSemanticType());

		return cd;
	}

	@Override
	public IAction createAction() {
		return new ConvertAction();
	}
	
}
