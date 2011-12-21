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
package org.integratedmodelling.utils.template;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabIOException;
import org.integratedmodelling.utils.MiscUtilities;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRuntime;

public class MVELTemplate {

	private final CompiledTemplate template;
	
	public MVELTemplate(File hsour) throws ThinklabException {
		String tmpl = MiscUtilities.readFileIntoString(hsour);
		this.template = TemplateCompiler.compileTemplate(tmpl);
	}

	public void write(File f, Map<String, String> params) throws ThinklabException {
		String out = (String) TemplateRuntime.execute(template, params);
		Writer writer = null;
		try {
			writer = new OutputStreamWriter(new FileOutputStream(f), "UTF-8");
			writer.write(out);
			writer.close();
	     } catch (Exception e) {
	    	 throw new ThinklabIOException(e);
	     } 
	}

}
