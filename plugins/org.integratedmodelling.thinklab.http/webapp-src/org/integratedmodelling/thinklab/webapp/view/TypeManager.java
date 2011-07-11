/**
 * TypeManager.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of Thinkcap.
 * 
 * Thinkcap is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Thinkcap is distributed in the hope that it will be useful,
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
 * @author    Ferdinando
 * @date      Jan 17, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.thinklab.webapp.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabInternalErrorException;
import org.integratedmodelling.exceptions.ThinklabResourceNotFoundException;
import org.integratedmodelling.thinklab.ConceptVisitor;
import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.api.knowledge.IConcept;
import org.integratedmodelling.thinklab.api.knowledge.IInstance;
import org.integratedmodelling.thinklab.api.knowledge.IProperty;
import org.integratedmodelling.thinklab.api.knowledge.query.IQueryResult;
import org.integratedmodelling.thinklab.literals.BooleanValue;
import org.integratedmodelling.thinklab.webapp.interfaces.IRestrictionComponentConstructor;
import org.integratedmodelling.thinklab.webapp.interfaces.IVisualizationComponentConstructor;
import org.integratedmodelling.utils.xml.XMLDocument;
import org.mvel2.MVEL;
import org.w3c.dom.Node;
import org.zkoss.zhtml.Text;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Window;

/**
 * A singleton that manages decorations, labels, icons etc related to types. It is 
 * configured using the plugin.xml file and the main typedecorations.xml in WEB-INF
 * of the Thinklab application.
 * 
 * @author Ferdinando Villa
 *
 */
public class TypeManager {
	
	static TypeManager _this = new TypeManager();
	
	Hashtable<String, VisualConcept> visualConcepts = 
		new Hashtable<String, VisualConcept>();
	Hashtable<String, VisualProperty> visualProperties = 
		new Hashtable<String, VisualProperty>();
	
	public static TypeManager get() {
		return _this;
	}
	
	public VisualConcept getVisualConcept(String conceptID) throws ThinklabException {		
		return getVisualConcept(KnowledgeManager.get().requireConcept(conceptID));
	}

	public synchronized VisualConcept getVisualConcept(IConcept concept) {
		
		VisualConcept ret = visualConcepts.get(concept.toString());
		if (ret == null) {
			ret = new VisualConcept(concept, this);
			visualConcepts.put(concept.toString(), ret);
		}
		return ret;
	}
	
	
	public VisualProperty getVisualProperty(IProperty p) {
		
		VisualProperty ret = visualProperties.get(p.toString());
		if (ret == null) {
			ret = new VisualProperty(p, this);
			visualProperties.put(p.toString(), ret);
		}
		return ret;
	}
	public class TypeResultVisualizer {
		
		String[] templates = new String[9];
		String[] expressions = new String[9];
		String[] defaults = new String[9];
		String[] widths = new String[9];
		String[] borders = new String[9];
		String[] styles = new String[9];
		
		/**
		 * Process the given result according to what the user specified and return the field after
		 * reprocessing for visualization. Used within the ResultItem component.
		 * 
		 * This is NOT going to be intuitive to anyone but me, so just call me if you need to 
		 * figure it out.
		 * 
		 * @param boxIndex
		 * @param result
		 * @param rIndex
		 * @param manager
		 * @return
		 * @throws ThinklabException 
		 */
		Object getResultField(int boxIndex, IQueryResult result, int rIndex) throws ThinklabException {
			
			Object ret = null;
			
			if (templates[boxIndex] != null) {
				
				ret = result.getResultField(rIndex, templates[boxIndex]);
				
			} else if (expressions[boxIndex] != null) {
				
				Serializable expr = MVEL.compileExpression(expressions[boxIndex]);
				
				HashMap<String, Object> ctx = new HashMap<String, Object>();
				
				ctx.put("index", new Integer(rIndex));
//				ctx.put("thinkcap", Thinkcap.get());
				ctx.put("typeManager", TypeManager.get());
				ctx.put("results", result);
				ctx.put("type", result.getResultField(rIndex, "type"));
				ctx.put("vconcept", getVisualConcept(result.getResultField(rIndex, "type").toString()));
				
				ret = MVEL.executeExpression(expr, ctx);
			}
				
			if (ret == null || ret.toString().equals(""))
				ret = defaults[boxIndex];
			
			if (ret == null)
				ret = "";
			
			return ret;
		}
		
		public void setupWindow(Window w, int boxIndex, IQueryResult result, int rindex) throws ThinklabException {
			
			if (widths[boxIndex] != null)
				w.setWidth(widths[boxIndex]);
			if (borders[boxIndex] != null) 
				w.setBorder(borders[boxIndex]);
			if (styles[boxIndex] != null)
				w.setStyle(styles[boxIndex]);
			
			Object obj = getResultField(boxIndex, result, rindex);
			
			if (obj instanceof Component) {
				w.appendChild((Component)obj);
			} else if (obj instanceof String) {
				/* this should do it */
				Component content = new Text(obj.toString());
				w.appendChild(content);
			}
		}		
	}
	
	public class TypeDecoration {
		
		String id;
		public boolean ignored;
		public String seealso;
		public String comment;
		public String label;
		public String icon;
		public Hashtable<String, String> pIcons = new Hashtable<String, String>();
		public Class<?> selectorConstructor = null;
		public Class<?> visualizerConstructor = null;
		public Class<?> itemResultConstructor = null;
		boolean isConcept;
	
		public TypeDecoration(String id, boolean isC) {
			this.id = id;
			isConcept = isC;
		}

		boolean isConcept() {
			return isConcept;
		}
		
		boolean isProperty() {
			return !isConcept;
		}

		public String getId() {
			return id;
		}

		public boolean isIgnored() {
			return ignored;
		}

		public String getSeeAlso() {
			return seealso;
		}

		public String getComment() {
			return comment;
		}

		public String getLabel() {
			return label;
		}

		public String getIcon(String context) {
			
			String ret = icon;
			if (context != null) {
				// look for specific icon; will return default if not found
				String ic = pIcons.get(context);
				if (ic != null)
					ret = ic;
			}
			return ret;
		}
		
		public IVisualizationComponentConstructor getVisualizerConstructor(IProperty parm) throws ThinklabException {
			try {
				return (IVisualizationComponentConstructor) (visualizerConstructor == null ?
						null :
						visualizerConstructor.newInstance());
			} catch (Exception e) {
				throw new ThinklabInternalErrorException(e);
			}
		}
		
		public IRestrictionComponentConstructor getRestrictionConstructor(IProperty parm) throws ThinklabException {
			try {
				return (IRestrictionComponentConstructor) (selectorConstructor == null ?
						null :
						selectorConstructor.newInstance());
			} catch (Exception e) {
				throw new ThinklabInternalErrorException(e);
			}
		}
		
	}
	
	private Hashtable<String, TypeDecoration> typeDescription = 
		new Hashtable<String, TypeDecoration>();

	private Hashtable<String, TypeResultVisualizer> resultVisualizers =
		new Hashtable<String, TypeResultVisualizer>();
	
	private Hashtable<String, QueryFormStructure> queryStructures = 
		new Hashtable<String, QueryFormStructure>();

	private Hashtable<String, QueryFormStructure> visualStructures =
		new Hashtable<String, QueryFormStructure>();
	
	public void read(Node node) throws ThinklabException {
		
		for (Node n = node.getFirstChild(); n != null; n = n.getNextSibling()) {
			
			if (n.getNodeName().equals("type") || n.getNodeName().equals("property")) {

				String id = XMLDocument.getAttributeValue(n, "id");
				
				TypeDecoration td = 
					new TypeDecoration(id, n.getNodeName().equals("type"));
				
				for (Node nn = n.getFirstChild(); nn != null; nn = nn.getNextSibling()) {
					
					if (nn.getNodeName().equals("icon")) {
						
						String ctx = XMLDocument.getAttributeValue(nn, "context");
						if (ctx == null || ctx.trim().equals(""))
							td.icon = nn.getTextContent().trim();
						else 
							td.pIcons.put(ctx, nn.getTextContent().trim());
						
					} else if (nn.getNodeName().equals("label")) {
						td.label = nn.getTextContent().trim();
					} else if (nn.getNodeName().equals("description")) {
						td.comment = nn.getTextContent();
					} else if (nn.getNodeName().equals("see-also")) {
						td.seealso = nn.getTextContent();
					} else if (nn.getNodeName().equals("ignored")) {
						td.ignored = BooleanValue.parseBoolean(nn.getTextContent().trim());
					} else if (nn.getNodeName().equals("query-form")) {
						
						QueryFormStructure qfs = new QueryFormStructure(id);
						qfs.read(nn, false);
						queryStructures.put(id, qfs);
						
					} else if (nn.getNodeName().equals("visualization-table")) {
						
						QueryFormStructure qfs = new QueryFormStructure(id);
						qfs.read(nn, true);
						visualStructures .put(id, qfs);
						
					} else if (nn.getNodeName().equals("visualizer-constructor")) {
					
						String cname = nn.getTextContent().trim();
						try {
							td.visualizerConstructor = Class.forName(cname);
						} catch (ClassNotFoundException e) {
							throw new ThinklabResourceNotFoundException(
							  "type manager: cannot create visualizer constructor: class" +
							  cname + 
							  " not found");
						}
						
						
					} else if (nn.getNodeName().equals("selector-constructor")) {
						
						String cname = nn.getTextContent().trim();
						try {
							td.selectorConstructor = Class.forName(cname);
						} catch (ClassNotFoundException e) {
							throw new ThinklabResourceNotFoundException(
							  "type manager: cannot create selector constructor: class" +
							  cname + 
							  " not found");
						}
						
					} else if (nn.getNodeName().equals("search-result-visualization")) {
						
						TypeResultVisualizer vis = new TypeResultVisualizer();
						
						for (Node mm = nn.getFirstChild(); mm != null; mm = mm.getNextSibling()) {
							
							String txt = mm.getTextContent().trim();
							String def = XMLDocument.getAttributeValue(mm, "default");
							String sty = XMLDocument.getAttributeValue(mm, "style");
							String wdt = XMLDocument.getAttributeValue(mm, "width");
							String brd = XMLDocument.getAttributeValue(mm, "border");
							String typ = XMLDocument.getAttributeValue(mm, "type");
							
							/*
							 * it is a template string for the schema unless it's been declared to be
							 * an expression. 
							 * TODO we can should the template into a real template by substituting
							 * schema fields as variables.
							 */
							boolean template = !(typ != null && typ.equals("expression"));
							
							int idx =0;
							
							if (mm.getNodeName().equals("n")) {
								idx = 1;
							} else if (mm.getNodeName().equals("ne")) {
								idx = 2;								
							} else if (mm.getNodeName().equals("w")) {
								idx = 3;
							} else if (mm.getNodeName().equals("c")) {
								idx = 4;
							} else if (mm.getNodeName().equals("e")) {
								idx = 5;
							} else if (mm.getNodeName().equals("sw")) {
								idx = 6;
							} else if (mm.getNodeName().equals("s")) {
								idx = 7;
							} else if (mm.getNodeName().equals("se")) {
								idx = 8;
							}
 							
							if (template)
								vis.templates[idx] = txt.equals("") ? null : txt;
							else
								vis.expressions[idx] = txt.equals("") ? null : txt;
							
							vis.defaults[idx] = def;
							vis.styles[idx] = sty;
							vis.borders[idx] = brd;
							vis.widths[idx] = wdt;
						}
						
						resultVisualizers.put(id, vis);
					}
					
				}
				
				typeDescription.put(id, td);
			}
		}
	}
	

	/**
	 * Just return the record if any, without going through the concept
	 * hierarchy at all.
	 * 
	 * @param concept
	 * @return
	 */
	public TypeDecoration getTypeDecoration(IConcept concept) {
		return typeDescription.get(concept.toString());
	}
	
	/**
	 * Return the closest result visualizer for the given concept.
	 * @param concept
	 * @return
	 */
	public TypeResultVisualizer getResultVisualizer(IConcept concept) {
		
        class vmatch implements ConceptVisitor.ConceptMatcher {

            private Hashtable<String, TypeResultVisualizer> coll;
            
            public TypeResultVisualizer result = null;
            public vmatch(Hashtable<String, TypeResultVisualizer> c) {
                coll = c;
            }
            
            public boolean match(IConcept c) {
            	
            	boolean ret = false;
                TypeResultVisualizer td = coll.get(c.toString());
                if (td != null) {
                	result =  td;
                	ret = (result != null);
                }
            	return ret;
            }    
        }
        
        vmatch matcher = new vmatch(resultVisualizers);
        
        IConcept cms = 
            ConceptVisitor.findMatchUpwards(matcher, concept);

		return cms == null ? null : matcher.result;
		
	}
	
	public QueryFormStructure getQueryFormStructure(IConcept concept) {
		
		QueryFormStructure ret = queryStructures.get(concept.toString());
		
		if (ret == null) {
			ret = new QueryFormStructure(concept.toString());
			queryStructures.put(concept.toString(), ret);
		}
		
		return ret;
	}

	/**
	 * Return the closest result visualizer for the given concept.
	 * @param concept
	 * @return
	 */
	public ArrayList<TypeResultVisualizer> getAllResultVisualizers(IConcept concept) {
		
        class vmatch implements ConceptVisitor.ConceptMatcher {

            private Hashtable<String, TypeResultVisualizer> coll;
            
            public TypeResultVisualizer result = null;
            public vmatch(Hashtable<String, TypeResultVisualizer> c) {
                coll = c;
            }
            
            public boolean match(IConcept c) {
            	
            	boolean ret = false;
                TypeResultVisualizer td = coll.get(c.toString());
                if (td != null) {
                	result =  td;
                	ret = (result != null);
                }
            	return ret;
            }    
        }
        
        vmatch matcher = new vmatch(resultVisualizers);
        
        return 
            new ConceptVisitor<TypeResultVisualizer>().
            	findAllMatchesInMapUpwards(resultVisualizers, matcher, concept);
		
	}

	
	/**
	 * Return the closest result visualizer for the given concept.
	 * @param concept
	 * @return
	 */
	public ArrayList<TypeDecoration> getAllTypeDecorations(IConcept concept) {
		
        class vmatch implements ConceptVisitor.ConceptMatcher {

            private Hashtable<String, TypeDecoration> coll;
            
            public TypeDecoration result = null;
            public vmatch(Hashtable<String, TypeDecoration> c) {
                coll = c;
            }
            
            public boolean match(IConcept c) {
            	
            	boolean ret = false;
                TypeDecoration td = coll.get(c.toString());
                if (td != null) {
                	result =  td;
                	ret = (result != null);
                }
            	return ret;
            }    
        }
        
        vmatch matcher = new vmatch(typeDescription);
        
        return 
            new ConceptVisitor<TypeDecoration>().
            	findAllMatchesInMapUpwards(typeDescription, matcher, concept);
		
	}

	/**
	 * Return the closest result visualizer for the given concept.
	 * @param concept
	 * @return
	 */
	public ArrayList<QueryFormStructure> getAllQueryStructures(IConcept concept) {
		
        class vmatch implements ConceptVisitor.ConceptMatcher {

            private Hashtable<String, QueryFormStructure> coll;
            
            public vmatch(Hashtable<String, QueryFormStructure> c) {
                coll = c;
            }
            
            public boolean match(IConcept c) {            	
            	return coll.get(c.toString()) != null;
            }    
        }
        
        vmatch matcher = new vmatch(queryStructures);
        
        return 
            new ConceptVisitor<QueryFormStructure>().
            	findAllMatchesInMapUpwards(queryStructures, matcher, concept);
		
	}
	
	/**
	 * Return a new ZK component that can be used to define a restriction on
	 * the passed object or the range of the property in the passed relationship,
	 * using the component constructor defined in the global catalog connected to
	 * the class tree. The component should implement IRestrictionComponent, although
	 * because of a quirk in ZK it is currently impossible to define that to extend 
	 * Component, so it must be done case by case and there is no type safety for now.
	 * 
	 */
	public IRestrictionComponentConstructor getRestrictionComponentConstructor(IProperty r) {
		
	       class vmatch implements ConceptVisitor.ConceptMatcher {

	            private Hashtable<String, TypeDecoration> coll;
	            private IProperty parm;
	            
	            public IRestrictionComponentConstructor result = null;
	            
	            public vmatch(Hashtable<String, TypeDecoration> c, IProperty parameter) {
	                coll = c;
	                parm = parameter;
	            }
	            
	            public boolean match(IConcept c) {
	            	
	            	boolean ret = false;
	                TypeDecoration td = coll.get(c.toString());
	                if (td != null) {
	                	try {
							result =  td.getRestrictionConstructor(parm);
						} catch (ThinklabException e) {
							// screw it
						}
	                	ret = (result != null);
	                }
	            	return ret;
	            }    
	        }
		
	    Iterator<IConcept> iter = r.getRange().iterator();
		IConcept range = iter.hasNext() ? iter.next() : null;
		
		
		IRestrictionComponentConstructor constructor = null;
		
		if (range != null) {

			vmatch matcher = new vmatch(typeDescription, r);
	    
			IConcept cms = 
				ConceptVisitor.findMatchUpwards(matcher, range);
		
			constructor = cms == null ? null : matcher.result;    
		}
		
		return constructor;
	}
	
	/**
	 * Return a new ZK component that can be used to visualize the passed object,
	 * using the component constructor defined in the global catalog connected to
	 * the class tree. The component should implement IRestrictionComponent, although
	 * because of a quirk in ZK it is currently impossible to define that to extend 
	 * Component, so it must be done case by case and there is no type safety for now.
	 * 
	 * @throws ThinklabException 
	 * 
	 */
	public Component createObjectVisualizerComponent(IInstance instance) throws ThinklabException {
		
	      class vmatch implements ConceptVisitor.ConceptMatcher {

	            private Hashtable<String, TypeDecoration> coll;
	            private IProperty parm;
	            
	            public IVisualizationComponentConstructor result = null;
	            
	            public vmatch(Hashtable<String, TypeDecoration> c, IProperty parameter) {
	                coll = c;
	                parm = parameter;
	            }
	            
	            public boolean match(IConcept c) {
	            	
	            	boolean ret = false;
	                TypeDecoration td = coll.get(c.toString());
	                if (td != null) {
	                	try {
							result =  td.getVisualizerConstructor(parm);
						} catch (ThinklabException e) {
							// screw it
						}
	                	ret = (result != null);
	                }
	            	return ret;
	            }
	        }
		
		IConcept range = instance.getDirectType();
		IVisualizationComponentConstructor constructor = null;
		
		
		// just ignore the second parameter, but may want it later
	    vmatch matcher = new vmatch(typeDescription, null);
	    
	    IConcept cms = 
	    	ConceptVisitor.findMatchUpwards(matcher, range);
		
	    
	    constructor = cms == null ? null : matcher.result;    
	    
		return 
			constructor == null ? 
					null :
					constructor.createVisualizationComponent(null, "", 0);
	}

	public QueryFormStructure getVisualizationStructure(IInstance instance) {
		// TODO Auto-generated method stub
		return null;
	}

	public VisualInstance getVisualInstance(IInstance obj) {
		// TODO Auto-generated method stub
		return new VisualInstance(obj);
	}


}
