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
