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
package org.integratedmodelling.thinkscape;

import org.integratedmodelling.ograph.ONode;

public class LabelFactory {

	public static String stripLabel(String lbl) {
		if (lbl == null)
			return null;
		//String lbl = rn.getLabel();
		int pos = lbl.indexOf(':');
		if (pos > 0 && !Character.isLetter(lbl.charAt(0))) {
			return lbl.substring(pos + 1);
		}
		return lbl;
	}

	public static void addLabelPrefix(ONode rn, String lbl) {
		String label;
		switch (rn.getType()) {
	
		case ONode.REL_OBJECT_PROPERTY_RESTRICT_SOME:
		case ONode.REL_DATA_PROPERTY_RESTRICT_SOME:
			label = "\u2203:" + lbl;
			break;
		case ONode.REL_OBJECT_PROPERTY_RESTRICT_ALL:
		case ONode.REL_DATA_PROPERTY_RESTRICT_ALL:
			label = "\u2200:" + lbl;
			;
			break;
		case ONode.REL_OBJECT_PROPERTY_RESTRICT_CARD:
		case ONode.REL_DATA_PROPERTY_RESTRICT_CARD:
			if (rn.getMinCardinality() == rn.getMaxCardinality()
					&& rn.getMaxCardinality() > -1)
				label = String.valueOf(rn.getMaxCardinality()) + ':' + lbl;
			else if (rn.getMaxCardinality() > 0 && rn.getMinCardinality() > 0)
				label = "[" + String.valueOf(rn.getMinCardinality()) + ","
						+ String.valueOf(rn.getMaxCardinality()) + "]:" + lbl;
			else if (rn.getMinCardinality() >= 0 && rn.getMaxCardinality() == -1)
				label = "\u2265" + String.valueOf(rn.getMinCardinality()) + ':'
						+ lbl;
			else if (rn.getMinCardinality() <= 0 && rn.getMaxCardinality() > 0)
				label = "\u2264" + String.valueOf(rn.getMaxCardinality()) + ':'
						+ lbl;
			else
				label = lbl;
			break;
		
		default: {
			//label = lbl;
			rn.setLabel(lbl);
			return;
		}
	
		}
	
		rn.setRDFLabel(label);
	}

	public static String stripNamespace(String lbl) {
		if (lbl == null)
			return null;
		//String lbl = rn.getLabel();
		int pos = lbl.indexOf(':');
		if (pos > 0 && Character.isLetter(lbl.charAt(0))) {
			return lbl.substring(pos + 1);
		}
		return lbl;
	}

}
