///**
// * Copyright 2011 The ARIES Consortium (http://www.ariesonline.org) and
// * www.integratedmodelling.org. 
//
//   This file is part of Thinklab.
//
//   Thinklab is free software: you can redistribute it and/or modify
//   it under the terms of the GNU General Public License as published
//   by the Free Software Foundation, either version 3 of the License,
//   or (at your option) any later version.
//
//   Thinklab is distributed in the hope that it will be useful, but
//   WITHOUT ANY WARRANTY; without even the implied warranty of
//   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
//   General Public License for more details.
//
//   You should have received a copy of the GNU General Public License
//   along with Thinklab.  If not, see <http://www.gnu.org/licenses/>.
// */
//package org.integratedmodelling.thinklab.opal;
//
//import java.io.File;
//import java.util.HashSet;
//
//import org.integratedmodelling.exceptions.ThinklabException;
//import org.integratedmodelling.lang.RelationshipAnnotation;
//import org.integratedmodelling.lang.SemanticAnnotation;
//import org.integratedmodelling.thinklab.Thinklab;
//import org.integratedmodelling.thinklab.api.knowledge.IInstance;
//import org.integratedmodelling.utils.MiscUtilities;
//import org.integratedmodelling.utils.xml.XMLDocument;
//import org.w3c.dom.Node;
//
///**
// * @author villa
// *
// */
//public class OPALWriter {
//	
//	
//	private static void writeInstance(SemanticAnnotation i, XMLDocument document, OPALProfile profile, HashSet<String> refs) throws ThinklabException {
//		
//		if (refs == null)
//			refs = new HashSet<String>();
//		
//		writeInstanceInternal(i, document, document.root(), profile, refs);
//		
//	}
//	
//	
//	private static Node writeInstanceInternal(SemanticAnnotation instance, XMLDocument document, Node parent,
//			OPALProfile profile, HashSet<String> refs) throws ThinklabException {
//
//		
//		/* 
//		 * if this has been written before, just write out a ref for it. Make sure
//		 * we have all prefixes right.
//		 */
//		if (refs.contains(instance.getLocalName())) { 
//			
//			/* we just do nothing if it's being appended to the root node and we 
//			 * already have it. 
//			 */
//			if (parent.getNodeName().equals(profile.getDefaultRootNodeID())) {
//				return null;
//			}
//			
//			/*
//			 * write the ref if the parent is a relationship
//			 */
//			return document.appendTextNode(
//					profile.getDefaultReferenceTag(),
//					instance.getLocalName(), 
//					parent);
//		}
//		
//		/* have profile determine node name, ID etc. appropriately */
//		Node ret = 
//			document.createNode(
//					profile.getOPALConceptID(instance.getDirectType(), document),
//					parent);
//		
//		document.addAttribute(
//				ret, 
//				profile.getDefaultNameTag(), 
//				instance.getLocalName());
//		
//		refs.add(instance.getLocalName());
//		
//		/* write labels and comments if any, plus any annotations */
//		String label = instance.getLabel();
//		
//		if (label != null && !label.equals(""))
//			document.appendTextNode(profile.getDefaultLabelTag(), label, ret);
//		
//		label = instance.getDescription();
//		
//		if (label != null && !label.equals(""))
//			document.appendTextNode(profile.getDefaultDescriptionTag(), label, ret);
//		
//		/* scan properties */
//		for (RelationshipAnnotation r : instance.getRelationships()) {
//
//			/* 
//			 * see the relationship id, and ignore the whole thing if it's one of those 
//			 * that don't appear in the KM, such as protege metadata.
//			 */
//			String cid = profile.getOPALConceptID(r.getProperty(), document);
//			
//			if (cid == null)
//				continue;
//			
//			/*
//			 * object relationship: write rel node unless implicit, and recurse
//			 */
//			if (r.isObject()) {
//				
//				/* see if we can default the relationship */
//				if (profile.getDefaultRelationship(
//						instance.getDirectType(),
//						r.getObject().getDirectType())
//						== null) {
//				
//					/* nope, write it up as is */
//					Node reln = document.createNode(cid, ret);
//				
//					writeInstanceInternal(
//						r.getObject(),
//						document,
//						reln,
//						profile,
//						refs);
//					
//				} else {
//					
//					/* 
//					 * default relationship, just write the instance directly
//					 * within the main instance.
//					 */
//					writeInstanceInternal(
//							r.getObject(),
//							document,
//							ret,
//							profile,
//							refs);
//				}
//				
//				
//			} else if (r.isClassification()) {
//				
//				document.appendTextNode(
//						cid,
//						r.getValue().getConcept().toString(), 
//						ret);
//				
//			} else {
//
//				document.appendTextNode(
//						cid,
//						r.getValue().toString(), 
//						ret);
//			}
//		}
//		
//		return ret;
//		
//	}
//
//	/**
//	 * Serialize the passed instances to the given file using the default profile or
//	 * any profile that corresponds to the outfile extension if it is not .xml.
//	 * 
//	 * @param outfile
//	 * @param instances
//	 * @throws ThinklabException
//	 */
//	public static void writeInstances(File outfile, IInstance ... instances) throws ThinklabException {
//		
//		String profile = null;
//		if (!outfile.toString().endsWith(".xml")) {
//			profile = MiscUtilities.getFileExtension(outfile.toString());
//		}
//		writeInstances(outfile, profile, instances);
//	}
//	
//	/**
//	 * Serialize the passed instances to the given outfile using the specified
//	 * OPAL profile. If null is passed for the profile name, use the default
//	 * profile. 
//	 * 
//	 * @param outfile
//	 * @param profile
//	 * @param instances
//	 * @throws ThinklabException
//	 */
//	public static void writeInstances(File outfile, String profile, IInstance ... instances) throws ThinklabException {
//		
//		/* 
//		 * fetch profile
//		 */
//		OPALProfile prof = OPALProfileFactory.get().getProfile(profile, true);
//		
//		/* 
//		 * create xml document; if file name ends with .xml, add profile processing
//		 * instruction unless profile is null.
//		 */
//		XMLDocument doc = new XMLDocument(prof.getDefaultRootNodeID());
//		
//		HashSet<String> refs = new HashSet<String>();
//		
//		/* write down the instances */
//		for (IInstance inst : instances) {
//			writeInstance(inst.conceptualize(), doc, prof, refs);
//		}
//
//		if (!prof.isDefault()) {
//			/* add processing instruction to set profile - do it anyway even if
//			 * we have given a different extension. */
//			doc.addProcessingInstruction("OPAL", "profile=" + prof.getName());
//		}
//		
//		/* serialize document to file */
//		doc.writeToFile(outfile);
//	}
//	
//}
