/**
 * ParsedDataSource.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabCoreSciencePlugin.
 * 
 * ThinklabCoreSciencePlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabCoreSciencePlugin is distributed in the hope that it will be useful,
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
package org.integratedmodelling.corescience.implementations.datasources;

import java.util.ArrayList;

import org.integratedmodelling.corescience.exceptions.ThinklabInconsistentDataSourceException;
import org.integratedmodelling.corescience.interfaces.cmodel.IConceptualModel;
import org.integratedmodelling.corescience.interfaces.context.IObservationContext;
import org.integratedmodelling.corescience.interfaces.data.IDataSource;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.literals.IValue;
import org.integratedmodelling.thinklab.literals.TextValue;

/**
 * Implements a datasource that simply parses inline text. Multiple values in text can be
 * separated by commas. 0 or 1 dimensions are supported.
 * 
 * @author Ferdinando Villa
 *
 */
public class ParsedDataSource<T> extends TextValue implements IDataSource<T> {
	
	ArrayList<String> data = new ArrayList<String>();
	
	public ParsedDataSource(String s) throws ThinklabException {
		super(s);
		// someone's got to do it
		initialize();
	}
	
//	/*
//	 * TODO reinterpret as necessary, merge with the real one below
//	 */
//	public IValue getValue(int idx, IConcept concept)
//			throws ThinklabValidationException {
//
//		return KnowledgeManager.get().validateLiteral(concept, data.get(idx), null);
//	}
//
	public void initialize() throws ThinklabInconsistentDataSourceException {
		/* ignore arguments, we're just a stupid literal. */
		
		if (value.trim().equals(""))
			throw new ThinklabInconsistentDataSourceException("parsed data literal is empty");
		
		String[] values = value.split(",");
		for (String s : values) {
			data.add(s);
		}
	}


	public boolean handshake(IConceptualModel cm, IObservationContext observationContext, IObservationContext overallContext) throws ThinklabValidationException {
		
		/* 
		 * If only one value, must make sure that we don't depend on any extent, or we won't be able to 
		 * satisfy the context request.
		 */

		return false;
	}

	public T getInitialValue() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public T getValue(int index, Object[] parameters) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IConcept getValueType() {
		// TODO Auto-generated method stub
		return null;
	}

}
