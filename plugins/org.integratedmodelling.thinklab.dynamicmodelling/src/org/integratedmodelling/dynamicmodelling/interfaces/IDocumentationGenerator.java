/**
 * IDocumentationGenerator.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 21, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of ThinklabDynamicModellingPlugin.
 * 
 * ThinklabDynamicModellingPlugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ThinklabDynamicModellingPlugin is distributed in the hope that it will be useful,
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
package org.integratedmodelling.dynamicmodelling.interfaces;

import java.io.File;
import java.io.Writer;

import org.integratedmodelling.dynamicmodelling.annotation.ModelAnnotation;
import org.integratedmodelling.dynamicmodelling.model.Flow;
import org.integratedmodelling.dynamicmodelling.model.Model;
import org.integratedmodelling.dynamicmodelling.model.Stock;
import org.integratedmodelling.dynamicmodelling.model.Variable;
import org.integratedmodelling.thinklab.exception.ThinklabException;

/**
 * Interface for anything that generates documentation for a model in textual/linear form. A 
 * convenience interface to make it easier to implement another documentation generator if
 * needed.
 * 
 * FIXME docs are awful, but who wants them after all.
 * 
 * @author Ferdinando Villa
 * @date June 20, 2007
 */
public interface IDocumentationGenerator {

	/**
	 * This and the others can be overridden to support something other than HTML. No HTML-specific
	 * detail is contained outside of these functions. 
	 * @param writer
	 * @param model
	 */
	public void outputHeader(Writer writer, Model model, File outputDir, ModelAnnotation annotation)
		throws ThinklabException;
	/**
	 * 
	 * @param writer
	 * @param model
	 */
	public void outputFooter(Writer writer, Model model, File outputDir, ModelAnnotation annotation)
		throws ThinklabException;

	/**
	 * 
	 * @param writer
	 * @param model
	 */
	public void outputTitleDiv(Writer writer, Model model, File outputDir, ModelAnnotation annotation)
		throws ThinklabException;

	/**
	 * 
	 * @param writer
	 * @param model
	 * @throws ThinklabException 
	 */
	public void outputOverallModelDiv(Writer writer, Model model, File outputDir, ModelAnnotation annotation)
		throws ThinklabException; 

	/**
	 * 
	 * @param writer
	 * @param model
	 */
	public void outputModelAnnotationDiv(Writer writer, Model model, File outputDir, ModelAnnotation annotation)
		throws ThinklabException;

	/**
	 * 
	 * @param writer
	 * @param stock
	 * @param outputDir
	 * @param index the index of the object (stocks are multiple)
	 */
	public void outputStockDiv(Writer writer, Stock stock, File outputDir, int index, ModelAnnotation annotation)
		throws ThinklabException; 
	
	/**
	 * 
	 * @param writer
	 * @param flow
	 * @param outputDir
	 * @param index the index of the object (flows are multiple)
	 * @throws ThinklabException
	 */
	public void outputFlowDiv(Writer writer, Flow flow, File outputDir, int index, ModelAnnotation annotation)
		throws ThinklabException;
	
	/**
	 * 
	 * @param writer
	 * @param variable
	 * @param outputDir
	 * @param index the index of the object (variables are multiple)
	 * @throws ThinklabException
	 */
	public void outputVariableDiv(Writer writer, Variable variable, File outputDir, int index, ModelAnnotation annotation)  
		throws ThinklabException;
	
	/**
	 * Override this one too, and we're good to go. You can e.g. pass a servlet response
	 * writer, and there's no need for files.
	 * 
	 * @param model
	 * @return
	 */
	public Writer createModelDocumentationWriter(Model model, File outputDir)  
		throws ThinklabException;
	
}
