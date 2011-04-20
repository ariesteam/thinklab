package org.integratedmodelling.thinklab.http.geospace.view;

import org.integratedmodelling.olmaps.layer.GoogleMapsLayer;
import org.integratedmodelling.olmaps.layer.VectorLayer;
import org.integratedmodelling.thinklab.http.geospace.model.IGeolocatedModel;
import org.integratedmodelling.thinklab.http.geospace.zk.OLMAPS;
import org.integratedmodelling.thinklab.webapp.ZK;
import org.integratedmodelling.thinklab.webapp.view.components.ThinkcapComponent;
import org.zkoss.zk.ui.event.Event;

public class GeoLocator extends ThinkcapComponent {

	final static String INITIAL_STATE = "empty";
	
	// these should be automatically wired to the components
	VectorLayer vlayer = null;
	GoogleMapsLayer google = null;
	
	private static final long serialVersionUID = -6614669915166864967L;
	IGeolocatedModel model = null;

	public void onChange$geofeature(Event e) {
		// TODO check if events are passed
		System.out.println(vlayer + " got " + e);
	}

	public void onClick$drawmode(Event e) {
		vlayer.setEditControl("polygon");
	}

	public void onClick$mapview(Event e) {
		google.setMapType("normal");
	}

	public void onClick$satview(Event e) {
		google.setMapType("satellite");
	}

	public void onClick$hybview(Event e) {
		google.setMapType("hybrid");
	}
	
	@Override
	public void initialize() {

		this.setContent(
			ZK.div(
					ZK.vbox(
							ZK.separator(false).height(15),
							OLMAPS.map(
									OLMAPS.googlelayer().maptype("normal").id("google"),
									OLMAPS.vectorlayer().editcontrol("navigate").id("vlayer")
									).width(540).height(400),
							ZK.hbox(
									ZK.div(
										ZK.textbox().width("100").id("geofeature"),
										ZK.combobox("Countries", "Regions", "Watersheds", "Features").value("Countries")
										  ).align("left").width(60),
									ZK.div(
										ZK.imagebutton("images/icons/drawmodw.png").id("drawmode"),
										ZK.imagebutton("images/icons/mapview.png").id("mapview"),
										ZK.imagebutton("images/icons/satview.png").id("satview"),
										ZK.imagebutton("images/icons/hybview.png").id("hybview")
										  ).align("right")
									).width("100%"),
							ZK.separator(false)
						).id("map")
				).align("center").height(440));
	}

	@Override
	public void postInitialize() {
		// TODO Auto-generated method stub
		
	}
}
