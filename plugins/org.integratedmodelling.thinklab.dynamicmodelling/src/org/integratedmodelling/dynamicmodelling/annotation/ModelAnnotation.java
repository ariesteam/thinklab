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
package org.integratedmodelling.dynamicmodelling.annotation;

import java.io.File;
import java.io.PrintStream;

import org.integratedmodelling.dynamicmodelling.model.Flow;
import org.integratedmodelling.dynamicmodelling.model.Model;
import org.integratedmodelling.dynamicmodelling.model.Stock;
import org.integratedmodelling.dynamicmodelling.model.Variable;
import org.integratedmodelling.utils.KList;
import org.integratedmodelling.utils.Polylist;

/**
 * A model annotation is paired with a model loaded from a known input source (such as Simile or Stella) and 
 * serves the purpose of specifying semantics that can't be or shouldn't be embedded in the model. An annotation
 * object is the API peer of an annotation XML file, which is generated from the model if it is not present in the same
 * directory where the model is, or is read in if present. Users can add semantics to their models by editing the
 * annotation file generated automatically the first time.
 * 
 * @author Ferdinando Villa
 * @date June 10, 2007
 */
public class ModelAnnotation {

	private File sourceFile = null;
	boolean isnew = false;
	
	public void setSourceFile(File savePL) {
		sourceFile = savePL;
	}

	public File getSourceFile() {
		return sourceFile;
	}

	public boolean isNew() {
		return isnew;
	}

	public void serialize() {
		// TODO Auto-generated method stub
		
	}

	public Polylist getObservableForModel(Model mdl, String defaultModelObservableType) {
		// TODO Auto-generated method stub
		return new KList(defaultModelObservableType, mdl.getName()).list();
	}

	public Polylist getObservableForVariable(Variable variable,	String defaultObservableType) {
		// TODO Auto-generated method stub
		return new KList(defaultObservableType, variable.getName()).list();
	}

	public Polylist getObservableForStock(Stock stock, String defaultObservableType) {
		// TODO Auto-generated method stub
		return new KList(defaultObservableType, stock.getName()).list();
	}

	public Polylist getObservableForFlow(Flow flow, String defaultObservableType) {
		// TODO Auto-generated method stub
		return new KList(defaultObservableType, flow.getName()).list();
	}

	/**
	 * If we have a conceptual model we must return the whole property list, including
	 * the relationships to the main observation. Otherwise we should return null.
	 * @param variable
	 * @return
	 */
	public Polylist getConceptualModelPropertyListForVariable(Variable variable) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * If we have a conceptual model we must return the whole property list, including
	 * the relationships to the main observation. Otherwise we should return null.
	 * @param variable
	 * @return
	 */
	public Polylist getConceptualModelPropertyListForStock(Stock stock) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * If we have a conceptual model we must return the whole property list, including
	 * the relationships to the main observation. Otherwise we should return null.
	 * @param variable
	 * @return
	 */
	public Polylist getConceptualModelPropertyListForFlow(Flow flow) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTypeForVariable(Variable variable, String defaultType) {
		// TODO Auto-generated method stub
		return defaultType;
	}

	public String getTypeForStock(Stock stock, String defaultType) {
		// TODO Auto-generated method stub
		return defaultType;
	}

	public String getTypeForFlow(Flow flow, String defaultType) {
		// TODO Auto-generated method stub
		return defaultType;
	}

	/**
	 * Must return a whole property list if any context is there, or null
	 * @param model
	 * @return
	 */
	public Polylist getDefaultContextStatementForModel(Model model) {
		// TODO Auto-generated method stub
		return null;
	}

	public void dump(PrintStream outputStream) {
		// TODO Auto-generated method stub
		
	}
}
