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
package org.integratedmodelling.modelling.visualization.wiki;

import java.io.StringWriter;

import net.java.textilej.parser.MarkupParser;
import net.java.textilej.parser.builder.HtmlDocumentBuilder;
import net.java.textilej.parser.markup.Dialect;
import net.java.textilej.parser.markup.confluence.ConfluenceDialect;
import net.java.textilej.parser.markup.mediawiki.MediaWikiDialect;
import net.java.textilej.parser.markup.textile.TextileDialect;

import org.integratedmodelling.utils.StringUtils;


public class WikiFactory {

	public final static String test = "%rdf:type toto:Document\r\n"
			+ "\r\n"
			+ "%title Hello World\r\n"
			+ "\r\n"
			+ "%summary This is a short description\r\n"
			+ "should this come as a property-value of summary??\r\n"		//TODO check.
			+ "%locatedIn (((\r\n"
			+ "    %type [City]\r\n"		// onReference for the square brackets.
			+ "    %name [Paris]\r\n"		// onReference
			+ "    %address (((\r\n"
			+ "      %building 10\r\n"
			+ "      %street Cité Nollez\r\n"
							+ "      %anotherprop (((\r\n"
							+ "         %property1 value1\r\n"
							+ "         %property2 value2\r\n"
			+ "      ))) \r\n"
			+ "    ))) \r\n"
			+ ")))\r\n"
			+ "= Hello World =\r\n"
			+ "\r\n"
			+ "* item one\r\n"
			+ "  * sub-item a\r\n"
			+ "  * sub-item b\r\n"
			+ "    + ordered X \r\n"
			+ "    + ordered Y\r\n"
			+ "  * sub-item c\r\n"
			+ "* item two\r\n"
			+ "\r\n"
			+ "\r\n"
			+ "The table below contains \r\n"
			+ "an %seeAlso(embedded document). \r\n";
	
	public final static String test2 = "* This is a bullet\n" +
						"*# this is a numbered list\n" +
						"*# this is another numbered list\n" +
						"* This is another bullet";

	public static String confluenceToHTML(String source) {
		
		StringWriter sr = new StringWriter();
		HtmlDocumentBuilder builder = new HtmlDocumentBuilder(sr);
		builder.setEmitAsDocument(false);
		Dialect dialect = new ConfluenceDialect();
		MarkupParser parser = new MarkupParser(dialect);
		parser.setBuilder(builder);
		parser.parse(StringUtils.pack(source));
		return sr.toString();
	}
	
	public static String textileToHTML(String source) {

		StringWriter sr = new StringWriter();
		HtmlDocumentBuilder builder = new HtmlDocumentBuilder(sr);
		builder.setEmitAsDocument(false);
		MarkupParser parser = new MarkupParser(new TextileDialect());
		parser.setBuilder(builder);
		parser.parse(StringUtils.pack(source));
		return sr.toString();
	}

	public static String mediawikiToHTML(String source) {
		
		StringWriter sr = new StringWriter();
		HtmlDocumentBuilder builder = new HtmlDocumentBuilder(sr);
		builder.setEmitAsDocument(false);
		MarkupParser parser = new MarkupParser(new MediaWikiDialect());
		parser.setBuilder(builder);
		parser.parse(StringUtils.pack(source));
		return sr.toString();
	}

}
