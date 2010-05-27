package org.integratedmodelling.corescience.units;

import javax.measure.unit.Dimension;
import javax.measure.unit.ProductUnit;

import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.utils.MiscUtilities;
import org.integratedmodelling.utils.Pair;

public class Unit {

	javax.measure.unit.Unit<?> _unit; 
	
	public Unit(String s) throws ThinklabValidationException {
		
		Pair<Double, String> pd = MiscUtilities.splitNumberFromString(s);
		
		// TEMPORARY - REMOVE
		if (pd == null)
			pd = new Pair<Double,String>(null, s);
		
		double factor = 1.0;
		if (pd.getFirst() != null) {
			factor = pd.getFirst();
		}
		
		try {
			_unit = javax.measure.unit.Unit.valueOf(pd.getSecond());
		} catch (Exception e) {
			throw new ThinklabValidationException(e);
		}
		
		if (factor != 1.0) {
			_unit = _unit.times(factor);
		}
	}
	
	public double convert(double value, Unit unit) {
		return 0;
	}
	
	public javax.measure.unit.Unit<?> getUnit() {
		return _unit;
	}
	
	public boolean isRate() {
		
		boolean ret = false;
		if (_unit instanceof ProductUnit<?>) {
			ProductUnit<?> pu = (ProductUnit<?>)_unit;
			for (int i = 0; i < pu.getUnitCount(); i++) {
				javax.measure.unit.Unit<?> su = pu.getUnit(i);
				int power = pu.getUnitPow(i);
				if (su.getDimension().equals(Dimension.TIME) && power == -1) {
					ret = true;
					break;
				}
			}
		}
		return ret;
	}

	public boolean isLengthDensity() {
		boolean ret = false;
		if (_unit instanceof ProductUnit<?>) {
			ProductUnit<?> pu = (ProductUnit<?>)_unit;
			for (int i = 0; i < pu.getUnitCount(); i++) {
				javax.measure.unit.Unit<?> su = pu.getUnit(i);
				int power = pu.getUnitPow(i);
				if (su.getDimension().equals(Dimension.LENGTH.pow(1)) && power == -1) {
					ret = true;
					break;
				}
			}
		}
		return ret;
	}
	
	public boolean isArealDensity() {
		boolean ret = false;
		if (_unit instanceof ProductUnit<?>) {
			ProductUnit<?> pu = (ProductUnit<?>)_unit;
			for (int i = 0; i < pu.getUnitCount(); i++) {
				javax.measure.unit.Unit<?> su = pu.getUnit(i);
				int power = pu.getUnitPow(i);
				if (su.getDimension().equals(Dimension.LENGTH.pow(2)) && power == -1) {
					ret = true;
					break;
				}
			}
		}
		return ret;
	}

	public boolean isVolumeDensity() {
		boolean ret = false;
		if (_unit instanceof ProductUnit<?>) {
			ProductUnit<?> pu = (ProductUnit<?>)_unit;
			for (int i = 0; i < pu.getUnitCount(); i++) {
				javax.measure.unit.Unit<?> su = pu.getUnit(i);
				int power = pu.getUnitPow(i);
				if (su.getDimension().equals(Dimension.LENGTH.pow(3)) && power == -1) {
					ret = true;
					break;
				}
			}
		}
		return ret;
	}
	
	@Override
	public String toString() {
		return _unit.toString();
	}
	
	public boolean isUnitless() {

		boolean ret = false;
		
		if (_unit instanceof ProductUnit<?>) {
			
			// assume no unitless unit without a distribution
			ret = true;
			ProductUnit<?> pu = (ProductUnit<?>)_unit;
			for (int i = 0; i < pu.getUnitCount(); i++) {
				int power = pu.getUnitPow(i);
				if (power > 0) {
					ret = false;
					break;
				}
			}
		}
		return ret;
		
	}
}
