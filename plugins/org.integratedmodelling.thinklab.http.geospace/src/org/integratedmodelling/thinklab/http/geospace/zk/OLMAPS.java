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
package org.integratedmodelling.thinklab.http.geospace.zk;

import org.integratedmodelling.olmaps.OLmaps;
import org.integratedmodelling.olmaps.layer.GoogleMapsLayer;
import org.integratedmodelling.olmaps.layer.VectorLayer;
import org.integratedmodelling.olmaps.layer.WMSLayer;
import org.integratedmodelling.thinklab.webapp.ZK;
import org.integratedmodelling.thinklab.webapp.ZK.ZKComponent;
import org.zkoss.zk.ui.HtmlBasedComponent;

/**
 * Helper class to assist component creation, just like ZK in core thinkcap
 * @author Ferdinando
 *
 */
public class OLMAPS {
	
	static public class OLComponent extends ZK.ZKComponent {

		OLComponent(HtmlBasedComponent c) {
			super(c);
		}
		
		public OLComponent zoom(int n) {
			if (c instanceof OLmaps) 
				((OLmaps)c).setZoom(n);
			return this;
		}
		
		public OLComponent center(double lon, double lat) {
			if (c instanceof OLmaps) 
				((OLmaps)c).setCenter(lon, lat);
			return this;
		}
		
		public OLComponent zindex(int n) {
			if (c instanceof OLmaps) 
				((OLmaps)c).setZIndex(n);
			return this;
		}
		
		public OLComponent maptype(String type) {
			if (c instanceof GoogleMapsLayer) 
				((GoogleMapsLayer)c).setMapType(type);
			return this;
		}
		
		public OLComponent editcontrol(String type) {
			if (c instanceof VectorLayer)
				((VectorLayer)c).setEditControl(type);
			return this;
		}
		
		public OLComponent drawcontrols(boolean yes) {
			if (c instanceof VectorLayer)
				((VectorLayer)c).setToolbar(yes ? "true" : "false");
			return this;
		}

		public OLComponent format(String string) {
			if (c instanceof WMSLayer) {
				((WMSLayer)c).setFormat(string);
			}
			return this;
		}

		public OLComponent transparent(boolean b) {
			if (c instanceof WMSLayer) {
				((WMSLayer)c).setTransparent(b ? "true" : "false");
			}
			return this;
		}
	}
	
	public static class LayerSwitchComp extends ZK.TreeComponent {

		protected LayerSwitchComp(HtmlBasedComponent c) {
			super(c);
		}
	}
	
	public static OLComponent map(ZK.ZKComponent ...components) {
		
		OLmaps ret = new OLmaps();

		for (ZKComponent c : components)
			ret.appendChild(c.get());
		
		return new OLComponent(ret);
	}
	
	public static OLComponent wmslayer(String serviceUri, String layer) {
		
		WMSLayer ret = new WMSLayer();
		ret.setUri(serviceUri);
		ret.setLayers(layer);
		return new OLComponent(ret);
	}
	
	public static OLComponent googlelayer() {
		
		GoogleMapsLayer ret = new GoogleMapsLayer();
		return new OLComponent(ret);
	}
	
	public static OLComponent googlelayer(String mapType) {
		
		GoogleMapsLayer ret = new GoogleMapsLayer();
		ret.setMapType(mapType);
		return new OLComponent(ret);
	}

	public static OLComponent vectorlayer() {
		
		VectorLayer ret = new VectorLayer();
		return new OLComponent(ret);
	}
	
//	public static LayerSwitchComp layerswitcher(OLComponent map) {
//		
//	}

}
