package org.integratedmodelling.modelling.annotation;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.SemanticType;
import org.integratedmodelling.thinklab.constraint.Constraint;
import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.interfaces.applications.ISession;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.query.IQueryResult;
import org.integratedmodelling.thinklab.interfaces.storage.IKBox;

public class ModelAnnotation {

	public HashSet<String> mconc = new HashSet<String>();
	public HashSet<String> ustates = new HashSet<String>();
	public HashSet<String> cspaces = new HashSet<String>();
	public HashMap<String, IConcept> obstypes = new HashMap<String, IConcept>();
	public HashMap<String, Constraint> constraints = new HashMap<String, Constraint>();
	
	private ISession session;

	public ModelAnnotation(ISession session) {
		this.session = session;
	}
	
	public void dump(PrintStream out) {
		
		out.println("UNDEFINED CONCEPT SPACES");
		for (String s : cspaces) {
			out.println("  " + s);
		}
		out.println(mconc.size() + " undefined concept spaces");

		out.println("UNDEFINED CONCEPTS");
		for (String s : getMissingConcepts()) {
			out.println("  " + s);
		}
		out.println(mconc.size() + " undefined concepts");
	
		out.println("UNRESOLVED STATES");
		for (String s : ustates) {
			IConcept c = KnowledgeManager.get().retrieveConcept(s);
			out.println("  " + obstypes.get(s) + " for " + s + (c == null ? " (concept missing)" : " (semantics ok)"));
		}
		out.println(ustates.size() + " unresolved states");
	}
	
	private ArrayList<String> getMissingConcepts() {
		
		ArrayList<String> ret = new ArrayList<String>();
		for (String s : mconc) {
			ret.add(s);
		}
		Collections.sort(ret);
		return ret;
	}

	public void addConcept(String c, String parent) {
		
		SemanticType ss = new SemanticType(c);
		mconc.add(c);
		cspaces.add(ss.getConceptSpace());
		
		if (parent != null) {
			addConcept(parent, null);
		}
	}

	public void addUnresolvedState(
			String observableId,
			IConcept observationType,
			Constraint constraint) {
		ustates.add(observableId);
		obstypes.put(observableId, observationType);
		if (constraint != null)
			constraints.put(observableId, constraint);
	}

	public int getUnresolvedConceptsCount() {
		return ustates.size();
	}
	
	public int getUndefinedConceptsCount() {
		return mconc.size();
	}
	
	public int getUndefinedConceptSpacesCount() {
		return cspaces.size();
	}

	public HashMap<String, IQueryResult> findAllUnresolved(IKBox kbox) throws ThinklabException {
		
		HashMap<String,IQueryResult> ret = new HashMap<String, IQueryResult>();
		for (String cid : constraints.keySet()) {
			IQueryResult qr = kbox.query(constraints.get(cid));
			ret.put(cid, qr);
		}
		return ret;
	}
	
}