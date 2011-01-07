package org.integratedmodelling.modelling.visualization.wiki;

import java.io.StringReader;

import org.integratedmodelling.modelling.ModellingPlugin;
import org.xwiki.component.embed.EmbeddableComponentManager;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.rendering.converter.ConversionException;
import org.xwiki.rendering.converter.Converter;
import org.xwiki.rendering.renderer.printer.DefaultWikiPrinter;
import org.xwiki.rendering.renderer.printer.WikiPrinter;
import org.xwiki.rendering.syntax.Syntax;

public class WikiFactory {

	public static String wikiToHtml(String wikitext) {
		
		// Convert input in XWiki Syntax 2.0 into XHTML. The result is stored in
		// the printer.
		WikiPrinter printer = null;
		ClassLoader clsl = null;
		
		try {

			clsl = ModellingPlugin.get().swapClassloader();			
				
			EmbeddableComponentManager ecm = new EmbeddableComponentManager();
			ecm.initialize(ModellingPlugin.get().getClassLoader());
			
			// Use a the Converter component to convert between one syntax to
			// another.
			Converter converter = ecm.lookup(Converter.class);
			printer = new DefaultWikiPrinter();
			converter.convert(new StringReader(wikitext), Syntax.XWIKI_2_0,
					Syntax.XHTML_1_0, printer);
			
		} catch (ComponentLookupException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConversionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			ModellingPlugin.get().resetClassLoader(clsl);
		}


		return printer.toString();
	}
}
