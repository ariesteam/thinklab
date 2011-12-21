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
package org.integratedmodelling.dynamicmodelling.loaders;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import org.integratedmodelling.dynamicmodelling.DynamicModellingPlugin;
import org.integratedmodelling.dynamicmodelling.annotation.ModelAnnotation;
import org.integratedmodelling.dynamicmodelling.interfaces.IDocumentationGenerator;
import org.integratedmodelling.dynamicmodelling.model.Flow;
import org.integratedmodelling.dynamicmodelling.model.FlowEdge;
import org.integratedmodelling.dynamicmodelling.model.Model;
import org.integratedmodelling.dynamicmodelling.model.Stock;
import org.integratedmodelling.dynamicmodelling.model.Submodel;
import org.integratedmodelling.dynamicmodelling.model.Variable;
import org.integratedmodelling.dynamicmodelling.simile.SimilePrologReader;
import org.integratedmodelling.dynamicmodelling.utils.SimileModelExtractor;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabIOException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.graph.GraphViz;
import org.integratedmodelling.utils.CopyURL;
import org.integratedmodelling.utils.MiscUtilities;
import org.integratedmodelling.utils.Polylist;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Vertex;

/**
 * Model loader that does nothing with the actual model but create documentation for it. Ideally docs should be in
 * docbook or something like that, which we can later process to HTML, PDF etc. This version outputs
 * nice, CSS-compliant HTML; all output is carefully encapsulated, so any other format can be supported
 * by overriding five functions.
 * 
 * @author Ferdinando Villa
 * @date June 18, 2007
 */
public class ModelDocumentationGenerator extends ModelOWLLoader implements IDocumentationGenerator {

	static File docPath = null;
	
	static final String stockOptions = "shape=square, color=blue, fillcolor=white";
	static final String flowOptions = "shape=diamond, color=red, fillcolor=white";
	static final String varOptions = "shape=square, color=lightgrey, fillcolor=lightgrey";
	static final String submodelOptions = "shape=doubleoctagon, color=black, style=filled, fillcolor=grey";
	
	/* div CSS class IDs for each component. */
	static final String titleClass = "title";
	static final String modelClass = "model";
	static final String annotationClass = "annotation";
	static final String stockClass = "stock";
	static final String flowClass = "flow";
	static final String varClass = "variable";
	
	static String[] graphOptions = { 
		"graph [fontname=\"sanserif\", fontsize=\"10\", overlap=\"scale\"];", 
		"edge  [fontname=\"sanserif\", fontsize=\"9\"];",
		"node  [fontname=\"sanserif\", fontsize=\"9\", margin=\"0.055\"];"
	};

	private static String styleSheet;
	
	// just a table of known symbols to help us find and hyperlink known words
	private Vector<String> symbols = new Vector<String>();
	
	private String fillSpaces(String name) {
		return name.replace(' ', '_');
	}
	
	/*
	 * FIXME this shouldn't be necessary - all three vertex types should be derived by a
	 * common vertex subclass with the common metadata.
	 */
	private static String getVertexName(Vertex v) {

		String ret = "unknown";
		
		if (v instanceof Stock)
			ret = ((Stock)v).getName();
		else if (v instanceof Flow)
			ret = ((Flow)v).getName();
		else if (v instanceof Variable)
			ret = ((Variable)v).getName();
		else if (v instanceof Submodel)
			ret = ((Submodel)v).getModel().getName();
		
		return ret;
	}
	
	/**
	 * Use the symbol table to hyperlink object names to point to the respective anchors.
	 * @param text
	 * @return
	 */
	private String hyperlink(String text) {
		
		String ret = text;
		
		// FIXME this is quite slow on large models - should find a smarter way.
//		for (String s : symbols) {
//			ret = ret.replaceAll(s, "<a href=\"#" + fillSpaces(s) + "\">" + s + "</a>");
//		}
		
		return ret;
	}
	
	private String getStockEquation(Stock stock) {

		String ret = "";
		
		for (Object edge : stock.getInEdges()) {

			if (edge instanceof FlowEdge) {
				
				ret += 
					(ret.equals("") ? "" : " +") +
					((Flow)((FlowEdge)edge).getSource()).getName();
			}
		}

		for (Object edge : stock.getOutEdges()) {
			if (edge instanceof FlowEdge) {
				ret += 
					(ret.equals("") ? "-" : " -") +
					// wow
					((Flow)(((FlowEdge)edge).getDest())).getName();
			}
		}

		return ret;
	
	}

	
	/**
	 * Create a graphviz PNG drawing of the passed model structure. Optionally create and 
	 * return an image map specification for the same image.
	 * 
	 * @param model the model being documented
	 * @param outFile the output file (will be overwritten if existing)
	 * @param imageMap pass true if you want an image map to be generated and returned
	 * @return an HTML image map specification, or an empty string if imageMap=false was passed 
	 * @throws ThinklabException if shit happens
	 */
	public String makeGraph(Model model, String outFile, boolean imageMap) throws ThinklabException {
		
		 GraphViz gv = new GraphViz(DynamicModellingPlugin.get().getProperties().
				 	getProperty("dynamicmodelling.graphviz.application","neato"));
		 
		 if (imageMap)
			 gv.makeImageMap("#%s");
		 
		 gv.addln(gv.start_graph(graphOptions));
		 
		 for (Iterator<?> iter = model.getVertices().iterator(); iter.hasNext(); ) {
	           
			 	Vertex v = (Vertex) iter.next();

			 	if (v instanceof Stock) {
	            	gv.addNode(((Stock)v).getName(), stockOptions); 
	            	symbols.add(((Stock)v).getName());
	            } else if (v instanceof Flow) {
	            	gv.addNode(((Flow)v).getName(), flowOptions); 
	            	symbols.add(((Flow)v).getName());
	            } else if (v instanceof Variable) {
	            	gv.addNode(((Variable)v).getName(), varOptions); 
	            	symbols.add(((Variable)v).getName());
	            } else if (v instanceof Submodel) {
	            	// switch the link template so that we link to the submodel's own HTML file instead
	            	// of the node's anchor
	            	if (imageMap)
	            		gv.makeImageMap("%s.html");
	            	gv.addNode(((Submodel)v).getModel().getName(), submodelOptions); 
	            	if (imageMap)
	            		gv.makeImageMap("#%s");
	            }

	        }
	        		 
	    for (Iterator<?> iter = model.getEdges().iterator(); iter.hasNext(); ) {
	    	
	            Edge e = (Edge) iter.next();
	            Vertex src = (Vertex)e.getEndpoints().getFirst();
	            Vertex dst = (Vertex)e.getEndpoints().getSecond(); 

	            gv.addDirectedEdge(getVertexName(src), getVertexName(dst));
	            
	        }
		 		 
		 gv.addln(gv.end_graph());

		 System.out.println(gv.getDotSource());
		 
		 File out = new File(outFile);
		 gv.writeGraphToFile(gv.getGraph(), out);
		 
		 return gv.getImageMap();

	}
	
	/* just return the parent result, except we intercept the call and save the model
	 * to a file, linking it to the result page of the main model. 
	 * @see org.integratedmodelling.dynamicmodelling.loaders.ModelOWLLoader#loadModel(java.lang.String)
	 */
	public Collection<Polylist> loadModel(String msource) throws ThinklabException {
		
		URL modelURL = MiscUtilities.getURLForResource(msource);
		
		DynamicModellingPlugin.get().logger().info(
				"Creating documentation for " +
				msource + 
				" into " +
				(docPath + "/" + MiscUtilities.getURLBaseName(msource)) + 
				"...");
		
		File savePL = new File(docPath + "/" + MiscUtilities.getURLBaseName(msource) + ".simile");
		InputStream in = null;
		
		if (msource.endsWith(".simile")) {
			CopyURL.copy(modelURL, savePL);
			try {
				in = new FileInputStream(savePL);
			} catch (FileNotFoundException e) {
				throw new ThinklabIOException(e);
			}
		} else if (msource.endsWith(".sml")) {
			in = SimileModelExtractor.extractPrologModel(modelURL, savePL);
		}
				
		Model model = null;
		
		/* get graph from Simile parser */
		try {
			model = new SimilePrologReader(in).generateModel();
		} catch (Exception e) {
			throw new ThinklabValidationException(e);
		}
		
		/* load or create annotation 
		 */
		ModelAnnotation annotation = loadAnnotation(msource);
		
		annotation.setSourceFile(savePL);
		
		/* read models in */
		documentModel(model, annotation);
		
		DynamicModellingPlugin.get().logger().info(
				"Finished creating documentation for " +
				msource);
		
		return null;
		
	}

	
	public static void initialize(DynamicModellingPlugin plugin) throws ThinklabException {
		
		docPath = new File(
				plugin.getProperties().getProperty("dynamicmodelling.doc.path", 
													plugin.getScratchPath() + "/doc"));
		docPath.mkdirs();
		
		/*
		 * copy stylesheet and other resources into docpath
		 */
		plugin.copyHTMLResources(docPath);

		styleSheet = plugin.getProperties().getProperty("dynamicmodelling.doc.style", "css/model.css");
	}
	
	/**
	 * This and the others can be overridden to support something other than HTML. No HTML-specific
	 * detail is contained outside of these outputXXX() functions. 
	 * @param writer
	 * @param model
	 */
	public void outputHeader(Writer writer, Model model, File outputDir, ModelAnnotation annotation)  
		throws ThinklabException {

		try {
			writer.write("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">\r\n" + 
					"<html>\r\n" + 
					"\r\n" + 
					"<head>\r\n" + 
					"  <link rel=\"stylesheet\" type=\"text/css\" href=\"" + styleSheet + "\">\r\n" + 
					"  <meta http-equiv=\"content-type\" content=\"text/html; charset=ISO-8859-1\">\r\n" + 
					"  <meta name=\"author\" content=\"ThinkLab, Gary W. Johson Jr., Ferdinando Villa\">\r\n" + 
					"  <title>" + model.getName() + " (" + model.getDate() + ")</title>\r\n" + 
					"  <meta name=\"description\" content=\"Page generated by the ThinkLab Dynamic Modelling plugin.\">\r\n" + 
					"  <meta name=\"keywords\" content=\"thinklab dynamic modelling simile stella\">\r\n" + 
					"</head>\n" + 
					"\n" + 
					"\n" + 
					"<body>\n");

		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}
	}

	/**
	 * 
	 * @param writer
	 * @param model
	 */
	public void outputFooter(Writer writer, Model model, File outputDir, ModelAnnotation annotation)  
		throws ThinklabException {
		try {
			writer.write(
					"</body>\n" +
					"</html>\n");
		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}
	}

	/**
	 * 
	 * @param writer
	 * @param model
	 */
	public void outputTitleDiv(Writer writer, Model model, File outputDir, ModelAnnotation annotation) 
		throws ThinklabException {

		String fn = MiscUtilities.getFileName(annotation.getSourceFile().toString());
		
		try {
			
			writer.write(
					"<div id=\"title\" class=\"title\">\n" + 
					"<h1>" + model.getName() + "</h1>\n" + 
					"<p style=\"font-size: smaller\">Translated by ThinkLab on " + new Date() + 
					" from <a href=\"" + fn + "\"/>" + fn + "</a>" + 
					"</p>\n" + 
					"</div>\n" + 
					"");
			
		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}
		
	}

	/**
	 * 
	 * @param writer
	 * @param model
	 * @throws ThinklabException 
	 */
	public void outputOverallModelDiv(Writer writer, Model model, File outputDir, ModelAnnotation annotation) 
		throws ThinklabException {

		String picFile = fillSpaces(model.getName()) + ".png";
		
		/* make model graph */
		String imap = makeGraph(model, outputDir + "/" + picFile, true);

		try {
			
			writer.write(
					"<div id=\"model\" class=\"model\">\r\n" + 
					"<img src=\"" + picFile + "\" usemap=\"#modelmap\" alt=\"\"/>\r\n" + 
					"<map id=\"modelmap\" name=\"modelmap\">\r\n" + 
					imap +
					"</map>\r\n" + 
					"</div>");
			
		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}
		

		
	}

	/**
	 * 
	 * @param writer
	 * @param model
	 */
	public void outputModelAnnotationDiv(Writer writer, Model model, File outputDir, ModelAnnotation annotation)
		throws ThinklabException {

		Vector<String> annotations = model.getAnnotations();
		
		if (annotations == null || annotations.size() <= 0)
			return;
		
		try {
			
			writer.write("<div id=\"annotation\" class=\"annotation\">\n");
			
			for (String ann : annotations) {
				writer.write("  <p>" + ann + "</p>\n");
			}
			
			writer.write("</div>\n");
			
		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}
	
	}

	/**
	 * 
	 * @param writer
	 * @param stock
	 */
	public void outputStockDiv(Writer writer, Stock stock, File outputDir, int index, ModelAnnotation annotation)
		throws ThinklabException {

		String name = fillSpaces(stock.getName());

		try {
			/*
			 * anchor
			 */
			writer.write("<a name=\"" + name + "\"/>");

			/* stock stuff */
			writer.write(
					"<div id=\"stock" + index + "\" class=\"stock\">\n" + 
					"<h1><img style=\"border: 0px\" src=\"images/stock.jpg\" alt=\"Stock\"/>&nbsp;" + stock.getName() + "</h1>\n" + 
					"<p class=\"equation\">" + hyperlink(getStockEquation(stock))+ "</p>\n" + 
					"<p class=\"initialstate\">" + stock.getState()+ "</p>\n" + 
					( stock.getComment() == null ? 
							"" :
								"<p class=\"annotation\">" + stock.getComment() + "</p>\n") + 
					"</div>");
			
		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}
		
	}
	

	public void outputFlowDiv(Writer writer, Flow flow, File outputDir, int index, ModelAnnotation annotation)
		throws ThinklabException {

		String name = fillSpaces(flow.getName());

		try {
			/*
			 * anchor
			 */
			writer.write("<a name=\"" + name + "\"/>");
			
			writer.write(
					"<div id=\"flow" + index + "\" class=\"flow\">\n" + 
					"<h1><img style=\"border: 0px\" src=\"images/flow.gif\" alt=\"Flow\"/>&nbsp;" + flow.getName() + "</h1>\n" + 
					"<p class=\"equation\">" + hyperlink(flow.getRate())+ "</p>\n" + 
					( flow.getComment() == null ? 
							"" :
							"<p class=\"annotation\">" + flow.getComment() + "</p>\n") +
					"</div>");
			
		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}
		
		
	}
	
	public void outputVariableDiv(Writer writer, Variable variable, File outputDir, int index, ModelAnnotation annotation)
		throws ThinklabException {


		String name = fillSpaces(variable.getName());
		
		try {
			/*
			 * anchor
			 */
			writer.write("<a name=\"" + name + "\"/>");
			
			writer.write(
					"<div id=\"variable" + index + "\" class=\"variable\">\n" + 
					"<h1><img style=\"border: 0px\" src=\"images/variable.gif\" alt=\"Variable\"/>&nbsp;" + variable.getName() + "</h1>\n" + 
					"<p class=\"equation\">" + hyperlink(variable.getValue())+ "</p>\n" + 
					( variable.getComment() == null ? 
							"" :
							"<p class=\"annotation\">" + variable.getComment() + "</p>\n") + 
					"</div>");
			
		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}
		
		
	}
	
	/**
	 * Override this one too, and we're good to go. You can e.g. pass a servlet response
	 * writer, and there's no need for files.
	 * 
	 * @param model
	 * @return
	 */
	public Writer createModelDocumentationWriter(Model model, File outputDir)  throws ThinklabException {	

		FileWriter ret = null;
		try {
			ret = new FileWriter(new File(outputDir + "/" + fillSpaces(model.getName()) + ".html"));
		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}
		return ret;
	}

	


	public void documentModel(Model model, ModelAnnotation annotation) throws ThinklabException {
		
		/**
		 * We use the main path for all models. Initially I had a subdir per model, 
		 * but that is redundant (at least for HTML) and makes it hard to manage
		 * common resources. This may change eventually.
		 */
		File modelDir = docPath;		
		
		/* make HTML document linking to image and submodels */
		Writer writer = createModelDocumentationWriter(model, modelDir);
		
		/* output fixed sections */
		outputHeader(writer, model, modelDir, annotation);
		outputTitleDiv(writer, model, modelDir, annotation);
		outputOverallModelDiv(writer, model, modelDir, annotation);
		outputModelAnnotationDiv(writer, model, modelDir, annotation);

		for (Iterator<?> iter = model.getVertices().iterator(); iter.hasNext(); ) {
	           
		 	Vertex v = (Vertex) iter.next();
		 	if (v instanceof Submodel) {
		 		documentModel(((Submodel)v).getModel(), annotation);
		 	}
		 }
		
		/* for now we group stocks, flows and variables together in this order, and we 
		 * don't have a classification section or sort anything. 
		 */
		int idx = 0;
		for (Iterator<?> iter = model.getVertices().iterator(); iter.hasNext(); ) {
	           
			 	Vertex v = (Vertex) iter.next();
			 	if (v instanceof Stock) {
			 		outputStockDiv(writer, (Stock)v, modelDir, idx++, annotation);
			 	}
		 }
		 
		 idx = 0;
		 for (Iterator<?> iter = model.getVertices().iterator(); iter.hasNext(); ) {
	           
			 	Vertex v = (Vertex) iter.next();
			 	if (v instanceof Flow) {
			 		outputFlowDiv(writer, (Flow)v, modelDir, idx++, annotation);
			 	}
		 }
		 
		 idx = 0;
		 for (Iterator<?> iter = model.getVertices().iterator(); iter.hasNext(); ) {
	           
			 	Vertex v = (Vertex) iter.next();
			 	if (v instanceof Variable) {
			 		outputVariableDiv(writer, (Variable)v, modelDir, idx++, annotation);
			 	}
		 }
		 
		 outputFooter(writer, model, modelDir, annotation);
		 
		 try {
			writer.close();
		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}
		
	}

	@Override
	public Polylist graphToList(Model model, ModelAnnotation annotation) {
		return null;
	}


}
