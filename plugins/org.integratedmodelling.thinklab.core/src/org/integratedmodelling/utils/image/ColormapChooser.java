package org.integratedmodelling.utils.image;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.datastructures.ConfigurableIntelligentMap;

/**
 * Maps concepts to colormaps. The modelling plugin has one of these and an API + commands to access it.
 * 
 * @author Ferdinando
 *
 */
public class ColormapChooser extends ConfigurableIntelligentMap<ColorMap> {

	public ColormapChooser(String propertyPrefix) {
		super(propertyPrefix);
	}

	@Override
	protected ColorMap getObjectFromPropertyValue(String pvalue, Object[] parameters) throws ThinklabException {
	
		if (parameters == null || parameters.length == 0 || !(parameters[0] instanceof Integer)) {
			throw new ThinklabValidationException("ColormapChooser: must pass the number of levels");
		}
		
		int levels = (Integer)parameters[0];
		Boolean isz = parameters.length > 1 ? (Boolean)parameters[1] : null;
		
		ColorMap ret = null;
		String[] pdefs = pvalue.split(","); 
		       
		for (String pdef : pdefs) {
			
			pdef = pdef.trim();
			String[] ppd = pdef.split("\\s+");
			
			String cname = ppd[0] + "(";
			
			for (int i = 1; i < ppd.length; i++) {
				cname += (ppd[i] + (i == (ppd.length-1) ? "" : ","));
			}
			cname += ")";
			
			ret = ColorMap.getColormap(cname, levels, isz);
			if (ret != null)
				break;
		}
		       
		return ret;
	}


}
