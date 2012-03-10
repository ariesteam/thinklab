package org.integratedmodelling.thinklab.query;
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
//package org.integratedmodelling.thinklab.constraint;
//
//import org.integratedmodelling.exceptions.ThinklabException;
//import org.integratedmodelling.lang.RelationshipAnnotation;
//import org.integratedmodelling.lang.SemanticAnnotation;
//import org.integratedmodelling.thinklab.api.knowledge.IConcept;
//import org.integratedmodelling.thinklab.api.knowledge.IProperty;
//import org.integratedmodelling.thinklab.api.knowledge.ISemanticLiteral;
//import org.integratedmodelling.thinklab.api.knowledge.query.IConformance;
//import org.integratedmodelling.thinklab.api.knowledge.query.IQuery;
//import org.integratedmodelling.thinklab.api.knowledge.query.IRestriction;
//
//public abstract class DefaultAbstractConformance  {
//
//	public abstract IConcept getMatchingConcept(IConcept concept);
//
//	/**
//	 * Set the extent of the conformance for a classification property.
//	 * Basically the conceptual limit of the match between instances.
//	 * 
//	 * @param property
//	 * @param extent
//	 */
//	public abstract Restriction setConformance(IProperty property, IConcept extent);
//
//	/**
//	 * Set the extent of the comparison for a literal property.
//	 * 
//	 * @param property
//	 * @param extent
//	 */
//	public abstract Restriction setConformance(IProperty property, ISemanticLiteral extent);
//
//
//	@Override
//	public IQuery getQuery(SemanticAnnotation instance) throws ThinklabException {
//
//		Constraint constraint = new Constraint(getMatchingConcept(instance.getDirectType()));
//		IRestriction res = null;
//		
//		for (RelationshipAnnotation r : instance.getRelationships()) {
//
//			Restriction rr = null;
//			
//			if (r.isClassification()) {				
//				rr = setConformance(r.getProperty(), r.getValue().getConcept());
//			} else if (r.isObject()) {
//				rr = new Restriction(r.getProperty(), 
//						getQuery(r.getObject()));
//			} else {		
//				rr = setConformance(r.getProperty(), r.getValue());
//			}
//			
//			if (rr != null) {
//				if (res == null)
//					res = rr;
//				else
//					res = Restriction.AND(rr);
//			}
//		}
//		
//		if (res != null)
//			constraint.restrict(res);
//		
//		return constraint;
//	}
//
//}
