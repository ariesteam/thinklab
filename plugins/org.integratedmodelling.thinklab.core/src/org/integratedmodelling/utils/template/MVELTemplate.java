package org.integratedmodelling.utils.template;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.utils.MiscUtilities;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.CompiledTemplate;
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
