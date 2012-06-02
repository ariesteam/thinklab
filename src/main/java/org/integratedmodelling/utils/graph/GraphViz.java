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
package org.integratedmodelling.utils.graph;

//GraphViz.java - a simple API to call dot from Java programs

/*$Id$*/
/*
 ******************************************************************************
 *                                                                            *
 *              (c) Copyright 2003 Laszlo Szathmary                           *
 *                                                                            *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms of the GNU Lesser General Public License as published by   *
 * the Free Software Foundation; either version 2.1 of the License, or        *
 * (at your option) any later version.                                        *
 *                                                                            *
 * This program is distributed in the hope that it will be useful, but        *
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY *
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public    *
 * License for more details.                                                  *
 *                                                                            *
 * You should have received a copy of the GNU Lesser General Public License   *
 * along with this program; if not, write to the Free Software Foundation,    *
 * Inc., 675 Mass Ave, Cambridge, MA 02139, USA.                              *
 *                                                                            *
 ******************************************************************************
 * 
 * Modified by Ferdinando Villa, Ecoinformatics Collaboratory, UVM
 * @date June 18, 2007
 */

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Set;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabIOException;
import org.integratedmodelling.exceptions.ThinklabResourceNotFoundException;
import org.integratedmodelling.thinklab.Thinklab;
import org.integratedmodelling.thinklab.configuration.Configuration;
import org.integratedmodelling.utils.DisplayImage;
import org.integratedmodelling.utils.MiscUtilities;
import org.integratedmodelling.utils.cformat.PrintfFormat;
import org.jgrapht.DirectedGraph;

/**
 * <dl>
 * <dt>Purpose: GraphViz Java API
 * <dd>
 *
 * <dt>Description:
 * <dd> With this Java class you can simply call dot
 *      from your Java programs
 * <dt>Example usage:
 * <dd>
 * <pre>
 *    GraphViz gv = new GraphViz();
 *    gv.addln(gv.start_graph());
 *    gv.addln("A -> B;");
 *    gv.addln("A -> C;");
 *    gv.addln(gv.end_graph());
 *    System.out.println(gv.getDotSource());
 *
 *    File out = new File("out.gif");
 *    gv.writeGraphToFile(gv.getGraph(gv.getDotSource()), out);
 * </pre>
 * </dd>
 *
 * </dl>
 *
 * @version v0.1, 2003/12/04 (Decembre)
 * @author  Laszlo Szathmary (<a href="szathml@delfin.unideb.hu">szathml@delfin.unideb.hu</a>)
 * 
 * Modified by Ferdinando Villa, Ecoinformatics Collaboratory, UVM
 * @date June 18, 2007
 */
public class GraphViz
{
	private String makeImageMap = null;
	private String imageMap = "";
	private String outputFormat = "png";

	// these will be filled in only if layout() is called
	int width;
	int height;

	public static interface NodePropertiesProvider {
		public String getNodeId(Object o);
		public int getNodeWidth(Object o);
		public int getNodeHeight(Object o);
	}
	
	public class NodeLayout {
		int x;
		int y;
		int w;
		int h;
		String node;
	}
	Hashtable<String, NodeLayout> nodes = new Hashtable<String, NodeLayout>();
	
	/**
	 * Call this with a URL pattern if you want an imagemap to be generated for the graph.
	 * @param The URL pattern that you want to attach to each node. Use %s in the pattern
	 * where you want the node name to go. If the node name has spaces in them, they will be replaced by
	 * underscores in the resulting URL.
	 */
	public void makeImageMap(String urlPattern) {
		makeImageMap = urlPattern;
	}
	
	/**
	 * If makeImageMap(true) has been called and getGraph() executed, returns the
	 * image map ready for inclusion in HTML. Otherwise returns an empty string.
	 * @return
	 */
	public String getImageMap() {
		return imageMap;		
	}
	
	/**
	 * Set the output format to the passed string. It must be understood in the -T option
	 * of graphviz executables, and it also becomes the filename extension. Use with caution,
	 * no checking is done.
	 * 
	 * @param outputFormat
	 */
	public void setOutputFormat(String outputFormat) {
		this.outputFormat = outputFormat;
	}
	
	static private String getDefaultDotPath(String exe)  {
		
		String ret = "/usr/bin/" + exe;
		
		switch (Configuration.getOS()) {
		
		case WIN:
			ret = exe + ".exe";
			break;
		case MACOS:
			/* TODO check where it goes */
			ret = 
				"/usr/bin/" + exe;
			break;
		case UNIX:
			/* TODO check where common rpm distributions put it */
			ret = 
				"/usr/bin/" + exe;
			break;
		}
		
		return ret;
		
	}
	
   private String DOT  = null;

   /**
    * The source of the graph written in dot language.
    */
	private StringBuffer graph = new StringBuffer();

  /**
   * Constructor: creates a new GraphViz object that will contain
   * a graph.
   * @throws ThinklabResourceNotFoundException if the dot executable wasn't found.
   */
	public GraphViz() throws ThinklabResourceNotFoundException {
		this("dot");
	}
	
   /**
    * COnstructor that allows to specify the program you want to use.
    * @param graphvizProgram
    * @throws ThinklabResourceNotFoundException
    */
	public GraphViz(String graphvizProgram) throws ThinklabResourceNotFoundException {
	   
	   DOT  = 
		   Thinklab.get().getProperties().getProperty(
				   graphvizProgram + ".path", 
		   		   getDefaultDotPath(graphvizProgram));
	   
//	   if (! (new File(DOT).exists()))
//		   throw new ThinklabResourceNotFoundException(DOT);
	   
   }

   /**
    * Returns the graph's source description in dot language.
    * @return Source of the graph in dot language.
    */
   public String getDotSource() {
      return graph.toString();
   }

   /**
    * Add a node to the graph. 
    * @param node
    * @param shape
    */
   public void addNode(String node, String options) {
	   
	   String s = "\tnode ";
	   String a = options == null ? "" : options;
	   
	   if (makeImageMap != null) {
		   
		   String nnode = node.replace(' ', '_');
		   PrintfFormat fmt = new PrintfFormat(makeImageMap);
		   a += 
			   (a.length() > 0 ? "," : "") +
			   "URL=\"" +
			   fmt.tostr(nnode) +
			   "\"";
	   }
	   
	   /* add attributes if any */
	   if (a.length() > 0) {
		   s += " [" + a + "] ";
	   }
	   
	   s += "\"" + node + "\";";	   
	   addln(s);
   }
   
   /**
    * Add a directed edge between passed nodes.
    * @param vertexName
    * @param vertexName2
    */
   public void addDirectedEdge(String vertexName, String vertexName2) {
		addln("\t\"" + vertexName + "\" -> \"" + vertexName2 + "\"");
   }
		
   public void loadGraph(DirectedGraph<?,?> graph, NodePropertiesProvider npp) {
	   loadGraph(graph, npp, false);
   }

   /**
    * Load a knowledge graph into graphviz representation. Do what you want after that.
    * Use reverse if the relationship shows the inverse of the intended semantics.
    * 
    * @param graph
    */
   public void loadGraph(DirectedGraph graph, NodePropertiesProvider npp, boolean reverse) {
	   
	   addln(start_graph("fontsize=8", "fontname=\"sanserif\"", "overlap=\"scale\""));
	   	   
	   for (Object s : graph.vertexSet()) {
		   addNode(npp.getNodeId(s), "shape=box, fontname=sanserif, fontsize=8, margin=\"0.055\"");
	   }
	   
	   for (Object e : graph.edgeSet()) {
		   
		   Object s = graph.getEdgeSource(e);
		   Object t = graph.getEdgeTarget(e);
		   
		   addDirectedEdge(
				   (reverse ? npp.getNodeId(t) : npp.getNodeId(s)), 
				   (reverse ? npp.getNodeId(s) : npp.getNodeId(t))); 
	   }

	   addln(end_graph());
   
   }
	   
 
   public void show() throws ThinklabException {
	   
		try {

			File o = File.createTempFile("gdi", "gif");
			writeGraphToFile(createImage(writeDotSourceToFile()), o);

			DisplayImage display = new DisplayImage(o.toURI().toURL());
			display.setVisible(true);
			
		} catch (IOException e) {
			throw new ThinklabIOException(e);
		}
   }
   
   /**
    * Adds a string to the graph's source (without newline).
    */
   public void add(String line) {
      graph.append(line);
   }

   /**
    * Adds a string to the graph's source (with newline).
    */
   public void addln(String line) {
      graph.append(line+"\n");
   }

   /**
    * Adds a newline to the graph's source.
    */
   public void addln() {
      graph.append('\n');
   }

   /**
    * Returns the graph as an image in binary format.
    * @param dot_source Source of the graph to be drawn.
    * @return A byte array containing the image of the graph.
 * @throws ThinklabException 
    */
   public byte[] getGraph() throws ThinklabException
   {
      byte[] img_stream = null;
   
      File dot = writeDotSourceToFile();
      if (dot != null)
    	 img_stream = createImage(dot);
      
      return img_stream;
   }

   /**
    * Writes the graph's image in a file.
    * @param img   A byte array containing the image of the graph.
    * @param file  Name of the file to where we want to write.
    * @return Success: 1, Failure: -1
    */
   public int writeGraphToFile(byte[] img, String file)
   {
      File to = new File(file);
      return writeGraphToFile(img, to);
   }

   /**
    * Writes the graph's image in a file.
    * @param img   A byte array containing the image of the graph.
    * @param to    A File object to where we want to write.
    * @return Success: 1, Failure: -1
    */
   public int writeGraphToFile(byte[] img, File to)
   {
      try {
         FileOutputStream fos = new FileOutputStream(to);
         fos.write(img);
         fos.close();
      } catch (java.io.IOException ioe) { return -1; }
      return 1;
   }

   /**
    * Use graphviz to generate a layout for the nodes and return a Layout object
    * that describes it. After that is done, the size of the diagram and coordinates
    * of all nodes can be retrieved using getWidth(), getHeight() and getNode[X/Y]().
    * 
    * Coordinates are top to bottom, left to right if the passed parameter is true,
    * bottom to top otherwise.
    * 
    * @return
    */
   public void layout(NodePropertiesProvider npp, boolean top2bottom) throws ThinklabException {

	     try {
	         
	    	 File img =  File.createTempFile("graph_", ".txt");	         
	         File dot = writeDotSourceToFile();
	         
	         Runtime rt = Runtime.getRuntime();
	         String cmd = DOT;

	         cmd += (top2bottom? " -Tplain -y -o" : " -Tplain -o") + img.getAbsolutePath();
	         cmd += " " + dot.getAbsolutePath();

	         Process p = rt.exec(cmd);
	         p.waitFor();

	         /* parse layout file and set positions */
	         parseLayout(img);
	         
	         if (img.delete() == false || dot.delete() == false) 
	            System.err.println("Warning: "+img.getAbsolutePath()+" could not be deleted!");
	      }
	      catch (Exception ioe) {
	    	  throw new ThinklabIOException(ioe);
	      }
	   
   }
   
   private void parseLayout(File f) throws IOException  {

	   FileInputStream fstream = new FileInputStream(f);	   
	   DataInputStream in = new DataInputStream(fstream);
       BufferedReader br = new BufferedReader(new InputStreamReader(in));
    
       String line;

       while ((line = br.readLine()) != null)   {
    	   
    	   String[] tokens = line.split("\\s+");
    	   
    	   if (tokens.length < 2) 
    		   continue;
    	   
    	   if (tokens[0].equals("graph")) {
    		   
    		   width = (int)(Float.parseFloat(tokens[2]) * 72);
    		   height = (int)(Float.parseFloat(tokens[3]) * 72);
    		   
    	   } else if (tokens[0].equals("node")) {
    		   
    		   NodeLayout nl = new NodeLayout();
    		   nl.node = tokens[1];
    		   nl.x = (int)(Float.parseFloat(tokens[2]) * 72);
       		   nl.y = (int)(Float.parseFloat(tokens[3]) * 72);
       		   nl.w = (int)(Float.parseFloat(tokens[4]) * 72);
       		   nl.h = (int)(Float.parseFloat(tokens[5]) * 72);
       		   
       		   nodes.put(nl.node, nl);
       		    		   
    	   } else if (tokens[0].equals("edge")) {
    		   /* ignore for now */
    	   } 
    	   
    	   
       }
       in.close();
   }
   
   public int getWidth() {
	   return width;
   }
   
   public int getHeight() {
	   return height;
   }
   
   public int getNodeWidth(String node) {
	   return nodes.get(node).w;
   }
   
   public int getNodeHeight(String node) {
	   return nodes.get(node).h;
   }

   public int getNodeX(String node) {
	   return nodes.get(node).x;
   }

   public int getNodeY(String node) {
	   return nodes.get(node).y;
   }
   
   public Set<String> getNodeNames() {
	   return nodes.keySet();
   }
   /**
    * It will call the external dot program, and return the image in
    * binary format.
    * @param dot Source of the graph (in dot language).
    * @return The image of the graph in .gif format.
 * @throws ThinklabException 
    */
   public byte[] createImage(File dot) throws ThinklabException
   {
      File img = null;
      File imap = null;
      
      byte[] img_stream = null;

      try {
         
    	 img =  File.createTempFile("graph_", "." + outputFormat);
    	 imap = File.createTempFile("imap_", ".txt");         
         
         String temp = img.getAbsolutePath();

         Runtime rt = Runtime.getRuntime();
         String cmd = DOT;

         if (makeImageMap != null) {
        	 cmd += " -Tcmap -o" + imap.getAbsolutePath();
         }
         
         cmd += " -T" + outputFormat + " -o" + img.getAbsolutePath();
         cmd += " " + dot.getAbsolutePath();

         Process p = rt.exec(cmd);
         p.waitFor();

         FileInputStream in = new FileInputStream(img.getAbsolutePath());
         img_stream = new byte[in.available()];
         in.read(img_stream);
         // Close it if we need to
         if( in != null ) in.close();

         if (makeImageMap != null) {
        	 imageMap = MiscUtilities.readFileIntoString(imap);
         }
         
         if (img.delete() == false) 
            System.err.println("Warning: "+img.getAbsolutePath()+" could not be deleted!");
      }
      catch (Exception ioe) {
    	  throw new ThinklabIOException(ioe);
      }

      return img_stream;
   }

   /**
    * Writes the source of the graph in a file, and returns the written file
    * as a File object.
    * @param str Source of the graph (in dot language).
    * @return The file (as a File object) that contains the source of the graph.
    */
   private File writeDotSourceToFile() throws ThinklabIOException
   {
      File temp;
      try {
         temp = File.createTempFile("graph_", ".dot.tmp");
         FileWriter fout = new FileWriter(temp);
         fout.write(getDotSource());
         fout.close();
      }
      catch (Exception e) {
    	  throw new ThinklabIOException(e);
      }
      return temp;
   }

   /**
    * Returns a string that is used to start a graph.
    * @return A string to open a graph.
    */
   public String start_graph(String ... options) {
      String ret = "digraph G {";
      if (options != null) {
    	  for (String o : options) {
    		  ret += "\n\t" + o;
    	  }
      }
      return ret;
   }

   /**
    * Returns a string that is used to end a graph.
    * @return A string to close a graph.
    */
   public String end_graph() {
      return "}";
   }

}
