package org.integratedmodelling.thinklab.modelling.units;

import java.io.PrintStream;

import javax.measure.converter.UnitConverter;
import javax.measure.unit.Dimension;
import javax.measure.unit.ProductUnit;

import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabRuntimeException;
import org.integratedmodelling.exceptions.ThinklabValidationException;
import org.integratedmodelling.thinklab.api.modelling.observation.IModifiableUnit;
import org.integratedmodelling.thinklab.api.modelling.observation.IUnit;
import org.integratedmodelling.utils.MiscUtilities;

public class Unit implements IUnit {
	
	javax.measure.unit.Unit<?> _unit; 
	String _modifier = null;
	
	@Override
	public void parse(String string) throws ThinklabException {
		
		Pair<Double, String> pd = MiscUtilities.splitNumberFromString(string);
		
		double factor = 1.0;
		if (pd.getFirst() != null) {
			factor = pd.getFirst();
		}
		
		/*
		 * if we have a modifier, main unit must be a IModifiableUnit and we
		 * process it independently.
		 */
		String unit = pd.getSecond();
		
		if (unit.contains("@")) {
			int idat = unit.indexOf('@');
			int idsl = unit.indexOf('/');
			
			String pre = unit.substring(0, idat);
			_modifier = 
				idsl > 0 ?
					unit.substring(idat+1, idsl) :
					unit.substring(idat+1);
			unit = 
				idsl > 0 ?
					(pre + unit.substring(idsl)) :
					pre;
		}
		try {
			_unit = javax.measure.unit.Unit.valueOf(unit);
		} catch (Exception e) {
			throw new ThinklabValidationException(e);
		}
		
		if (_modifier != null) {
			
			/*
			 * must be a modifiable unit
			 */
			javax.measure.unit.Unit<?> uu = getPrimaryUnit(_unit);
			if (! (uu instanceof IModifiableUnit)) {
				throw new ThinklabValidationException("unit " + string + " has @ modifier but is not registered as modifiable");				
			}
			
			/*
			 * validate the modifier
			 */
			((IModifiableUnit)uu).validateModifier(_modifier);
		}
		
		if (factor != 1.0) {
			_unit = _unit.times(factor);
		}

	}

	
	public Unit(javax.measure.unit.Unit<?> unit) {
		_unit = unit;
	}
	
	public Unit(String s) {
		try {
			parse(s);
		} catch (ThinklabException e) {
			throw new ThinklabRuntimeException(e);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.modelling.units.IUnit#convert(double, org.integratedmodelling.thinklab.api.modelling.observation.IUnit)
	 */
	@Override
	public double convert(double value, IUnit unit) {
		
		UnitConverter converter = ((Unit)unit).getUnit().getConverterTo(_unit);
		double ret = converter.convert(value);
		
		if (getPrimaryUnit() instanceof IModifiableUnit) {
			if (((Unit)unit).getPrimaryUnit() instanceof IModifiableUnit) {
				
				try {
					ret *=
						((IModifiableUnit)(((Unit)unit).getPrimaryUnit())).
							convert((IModifiableUnit) getPrimaryUnit(), ((Unit)unit)._modifier, _modifier);
				} catch (ThinklabValidationException e) {
					throw new ThinklabRuntimeException(e);
				}
				
			} else {
				throw new ThinklabRuntimeException(
					new ThinklabValidationException(
							"unit " + 
							this + 
							" has modifiers and can only be converted into another modifiable unit"));
			}
		}
		return ret;
	}
	
	public javax.measure.unit.Unit<?> getUnit() {
		return _unit;
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.modelling.units.IUnit#isRate()
	 */
	@Override
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

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.modelling.units.IUnit#getTimeExtentUnit()
	 */
	@Override
	public IUnit getTimeExtentUnit() {
		
		if (_unit instanceof ProductUnit<?>) {
			ProductUnit<?> pu = (ProductUnit<?>)_unit;
			for (int i = 0; i < pu.getUnitCount(); i++) {
				javax.measure.unit.Unit<?> su = pu.getUnit(i);
				int power = pu.getUnitPow(i);
				if (su.getDimension().equals(Dimension.TIME) && power == -1) {
					return new Unit(su);
				}
			}
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.modelling.units.IUnit#isLengthDensity()
	 */
	@Override
	public boolean isLengthDensity() {
		boolean ret = false;
		if (_unit instanceof ProductUnit<?>) {
			ProductUnit<?> pu = (ProductUnit<?>)_unit;
			for (int i = 0; i < pu.getUnitCount(); i++) {
				javax.measure.unit.Unit<?> su = pu.getUnit(i);
				int power = pu.getUnitPow(i);
				if (su.getDimension().equals(Dimension.LENGTH) && power == -1) {
					ret = true;
					break;
				}
			}
		}
		return ret;
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.modelling.units.IUnit#getLengthExtentUnit()
	 */
	@Override
	public IUnit getLengthExtentUnit() {

		if (_unit instanceof ProductUnit<?>) {
			ProductUnit<?> pu = (ProductUnit<?>)_unit;
			for (int i = 0; i < pu.getUnitCount(); i++) {
				javax.measure.unit.Unit<?> su = pu.getUnit(i);
				int power = pu.getUnitPow(i);
				if (su.getDimension().equals(Dimension.LENGTH) && power == -1) {
					return new Unit(su);
				}
			}
		}
		return null;
	}
	
	public static javax.measure.unit.Unit<?> getPrimaryUnit(javax.measure.unit.Unit<?> uu) {
		
		if (uu instanceof ProductUnit<?>) {
			ProductUnit<?> pu = (ProductUnit<?>)uu;
			return pu.getUnit(0);
		}
		return uu;
	}
	
	public javax.measure.unit.Unit<?> getPrimaryUnit() {
		return getPrimaryUnit(_unit);
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.modelling.units.IUnit#isArealDensity()
	 */
	@Override
	public boolean isArealDensity() {
		boolean ret = false;
		if (_unit instanceof ProductUnit<?>) {
			ProductUnit<?> pu = (ProductUnit<?>)_unit;
			for (int i = 0; i < pu.getUnitCount(); i++) {
				javax.measure.unit.Unit<?> su = pu.getUnit(i);
				int power = pu.getUnitPow(i);
				if ((su.getDimension().equals(Dimension.LENGTH.pow(2)) && power == -1) ||
					(su.getDimension().equals(Dimension.LENGTH) && power == -2)) {
					ret = true;
					break;
				}
			}
		}
		return ret;
	}

	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.modelling.units.IUnit#getArealExtentUnit()
	 */
	@Override
	public IUnit getArealExtentUnit() {

		if (_unit instanceof ProductUnit<?>) {
			ProductUnit<?> pu = (ProductUnit<?>)_unit;
			for (int i = 0; i < pu.getUnitCount(); i++) {
				javax.measure.unit.Unit<?> su = pu.getUnit(i);
				int power = pu.getUnitPow(i);
				if (su.getDimension().equals(Dimension.LENGTH.pow(2)) && power == -1) {
					return new Unit(su);
				} else if (su.getDimension().equals(Dimension.LENGTH) && power == -2) {
					return new Unit(su.pow(2));
				}
			}
		}
		return null;
	}
	
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.modelling.units.IUnit#isVolumeDensity()
	 */
	@Override
	public boolean isVolumeDensity() {
		boolean ret = false;
		if (_unit instanceof ProductUnit<?>) {
			ProductUnit<?> pu = (ProductUnit<?>)_unit;
			for (int i = 0; i < pu.getUnitCount(); i++) {
				javax.measure.unit.Unit<?> su = pu.getUnit(i);
				int power = pu.getUnitPow(i);
				if (su.getDimension().equals(Dimension.LENGTH.pow(3)) && power == -1||
						(su.getDimension().equals(Dimension.LENGTH) && power == -3)) {
					ret = true;
					break;
				}
			}
		}
		return ret;
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.modelling.units.IUnit#getVolumeExtentUnit()
	 */
	@Override
	public IUnit getVolumeExtentUnit() {
		
		if (_unit instanceof ProductUnit<?>) {
			ProductUnit<?> pu = (ProductUnit<?>)_unit;
			for (int i = 0; i < pu.getUnitCount(); i++) {
				javax.measure.unit.Unit<?> su = pu.getUnit(i);
				int power = pu.getUnitPow(i);
				if (su.getDimension().equals(Dimension.LENGTH.pow(3)) && power == -1||
						(su.getDimension().equals(Dimension.LENGTH) && power == -3)) {
					return new Unit(su);
				}
			}
		}
		return null;
	}
	
	
	@Override
	public String toString() {
		return _unit.toString() + (_modifier == null ? "" : "@" + _modifier);
	}
	
	/* (non-Javadoc)
	 * @see org.integratedmodelling.thinklab.modelling.units.IUnit#isUnitless()
	 */
	@Override
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

	public void dump(PrintStream out) {

		out.println("unit " + _unit);

		if (_modifier != null)
			out.println("modifier: " + _modifier);
		
		out.println("is" + (isUnitless() ? " " : " not ") +  "unitless");
		out.println("is" + (isRate() ? " " : " not ") +  "a rate");
		out.println("is" + (isLengthDensity() ? " " : " not ") +  "a lenght density");
		out.println("is" + (isArealDensity() ? " " : " not ") +  "an areal density");
		out.println("is" + (isVolumeDensity() ? " " : " not ") +  "a volumetric density");
	}


	@Override
	public String asText() {
		return toString();
	}

}
