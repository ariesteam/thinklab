package org.integratedmodelling.thinkscape;

import java.io.File;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.integratedmodelling.growl.GraphUtil;
import org.integratedmodelling.growl.OWLMetadata;
import org.integratedmodelling.ograph.OEdge;
import org.integratedmodelling.ograph.OGraph;
import org.integratedmodelling.ograph.ONode;
import org.integratedmodelling.policy.ApplicationFrame;
import org.integratedmodelling.policy.KRPolicy;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.command.Command;
import org.integratedmodelling.thinklab.command.CommandDeclaration;
import org.integratedmodelling.thinklab.command.CommandManager;
import org.integratedmodelling.thinklab.command.CommandParser;
import org.integratedmodelling.thinklab.configuration.LocalConfiguration;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabMalformedCommandException;
import org.integratedmodelling.thinklab.exception.ThinklabNoKMException;
import org.integratedmodelling.thinklab.interfaces.ICommandOutputReceptor;
import org.integratedmodelling.thinklab.interfaces.ISessionManager;
import org.integratedmodelling.thinklab.interfaces.ISession;
import org.integratedmodelling.thinklab.interfaces.IValue;
import org.integratedmodelling.thinkscape.interfaces.ICommandReceptor;

public class KRPolicyThinkLab implements KRPolicy, ISessionManager,
		ICommandReceptor {

	public static final int OWLRDF = 0;

	public static final int Abstract = 1;

	public static final int JenaRDF = 2;

	protected OGraph cgraph = null;
	protected OGraph rgraph = null;
	protected OGraph graph = null;

	Set includeOntologies = null;

	protected OWLMetadata metadata;

	boolean multiplePropertyCopies = false;

	public boolean processIncludeOntologies = true;

	/** Creates a new instance of KRPolicyOWL */
	public KRPolicyThinkLab(ISession session) {

		metadata = new OWLMetadata();
		metadata.setDefaults();
		this.session = session;
	}

	public OGraph getCGraph() {
		return cgraph;
	}

	public OGraph getRGraph() {
		return rgraph;
	}

	public OGraph getGraph() {
		return graph;
	}

	public void resetGraph() {
		cgraph = new OGraph();
		rgraph = new OGraph();
		graph = cgraph;
	}

	public void setCGraphMode(boolean concepts) {
		if (concepts)
			graph = cgraph;
		else
			graph = rgraph;
	}

	public void read(String fileName) throws Exception {
		read(fileName, null);
	}

	public void read(URL xmlURL) throws Exception {
		read(xmlURL, null);
	}

	public void read(String fileName, Thread afterReading) throws Exception {
		URI uri = null;
		File f = new File(fileName);
		/*
		 * try { uri =f.toURI(); } catch (URISyntaxException e) {
		 * e.printStackTrace(); }
		 */
		read(f.toURI(), afterReading);

	}

	/**
	 * Reads data from a URL <tt>url</tt>, executing the
	 * <tt>afterReading</tt> Thread after the data is read in.
	 */
	public void read(URL url, Thread afterReading) throws Exception {
		URI uri = null;
		try {
			uri = new URI(url.toString());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		read(uri, afterReading);
	}

	public void read(URI uri, Thread afterReading) throws Exception {

		if (afterReading != null)
			afterReading.start();
	}

	public void write(OutputStream out, int format) throws Exception {

	}

	public String getBaseURI() {
		return metadata.getBaseURI().toString();
	}

	public void setBaseURI(String s) throws URISyntaxException {
		metadata.setBaseURI(new URI(s));
	}

	/**
	 * @return
	 */
	//	public OWLOntology getOntology() {
	//		return currentOntology;
	//	}
	/**
	 * @return
	 */
	public Set getIncludeOntologies() {
		return includeOntologies;
	}

	/**
	 * Method getMetadata
	 * 
	 * @return
	 */

	/**
	 * Method getMetadata
	 * 
	 * @return
	 */
	public OWLMetadata getMetadata() {
		return metadata;
	}

	/*
	 * ground all classes with no superclasses by making them a subclass of
	 * owl:Thing
	 */
	public void addGrounding(OGraph ograph) {
		String thing = //currentOntModel.getNsPrefixURI("owl") + "Thing";
		metadata.uriFromShortForm("owl:Thing").toString();

		ONode thingNode = (ONode) ograph.findNode("id", thing.toString());
		if (thingNode == null) // add an owl:Thing node if none exists
		{
			thingNode = (ONode) ograph.addNode();
			thingNode.init(thing, "owl:Thing", ONode.OBJ_CLASS);
			//frame.renPolicy.initObject(thingNode, ONode.OBJ_CLASS);
			thingNode.setInt("X", 250);
			thingNode.setInt("Y", 250);
			thingNode.setNamespace("owl");

			thingNode.setInferred(true); // prevent Thing from being rendered
		}
		GraphUtil.setThingNode(thingNode);
		Set owlObjects = ograph.getONodes(ONode.OBJ_CLASS);
		for (Iterator i = owlObjects.iterator(); i.hasNext();) // iterate
		// through all
		// named classes
		{
			ONode node = (ONode) i.next();
			if (!node.equals(thingNode)) {
				Vector outputs = node.getOutputs();
				if (!OGraph.hasSuperclass(node)) // any class with no superclasses is
				// linked to Thing
				{
					ograph.createEdge(node, thingNode, OEdge.SUBCLASS_OF_EDGE);
				}
			}
		}

	}

	public ISession session;
	/* (non-Javadoc)
	 * @see org.integratedmodelling.ima.core.IKnowledgeInterface#RegisterCommand(org.integratedmodelling.ima.core.CommandDeclaration)
	 */
	public void registerCommand(CommandDeclaration declaration)
			throws ThinklabException {
		// TODO Auto-generated method stub   
	}

	public void printStatusMessage() {

		System.out.println("ThinkScape shell 0.1alpha");
		System.out.println("System path: " + LocalConfiguration.getSystemPath());
		System.out.println("Data path: " + LocalConfiguration.getDataPath());								System.out.println();
		System.out.println("Enter \'help\' for a list of commands; \'exit\' quits");
		System.out.println();
	}

	/**
	 * Collect commands from stdin and pass them to KM to execute.
	 */
	public void start() {

		/* greet user */
		printStatusMessage();

	}

	public ICommandOutputReceptor getICommandOutputReceptor() {
		return (ICommandOutputReceptor) ApplicationFrame.getApplicationFrame().guiPolicy;
	}

	public IValue submitCommand(Command cmd) {

		IValue result = null;
		try {
			result = CommandManager.get().submitCommand(cmd,
					getICommandOutputReceptor(), session);
			if (result != null)
				getICommandOutputReceptor().displayOutput(result.toString());
		} catch (ThinklabNoKMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ThinklabException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public IValue submitCommand(String input) {

		if (!("".equals(input))) {

			try {
				Command cmd = CommandParser.parse(input);

				if (cmd != null)
					return submitCommand(cmd);
			} catch (ThinklabException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	public void importPreferences() {
	}

	public void savePreferences() {
	}

	public ISession createNewSession() throws ThinklabException {
		return session;
	}

	public void notifySessionDeletion(ISession session) {
	}

	public ISession getCurrentSession()  {
		return session;
	}

}
