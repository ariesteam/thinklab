package org.integratedmodelling.currency.units;

import javax.measure.quantity.Dimensionless;
import javax.measure.unit.BaseUnit;

import org.integratedmodelling.corescience.exceptions.ThinklabInexactConversionException;
import org.integratedmodelling.corescience.interfaces.literals.IModifiableUnit;
import org.integratedmodelling.currency.CurrencyPlugin;
import org.integratedmodelling.currency.cpi.CpiConversionFactory;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.time.literals.TimeValue;

public class CurrencyUnit extends BaseUnit<Dimensionless> implements IModifiableUnit { 

	private static final long serialVersionUID = 7776046638475282893L;
	
	TimeValue time = null;

	
	public CurrencyUnit(String annotation) { super(annotation); }
    
	@Override
	public void validateModifier(String modifier) throws ThinklabValidationException {
		time = new TimeValue(modifier);
	}
	
	@Override
	public double convert(IModifiableUnit to, String modFrom, String modTo) 
		throws ThinklabValidationException {
		
		double ret = 1.0;
		
		if (to instanceof CurrencyUnit) {
			CpiConversionFactory conv = CurrencyPlugin.get().getConverter();
			String code = ((CurrencyUnit)to).getSymbol();
			try {
				ret = conv.getConversionFactor(
						this.getSymbol(), this.time, 
						code, ((CurrencyUnit)to).time);
			} catch (ThinklabInexactConversionException e) {
				throw new ThinklabValidationException(e);
			}
		} else {
			throw new ThinklabValidationException(
					"cannot convert currency " + this + 
					" to non-currency " + to);
		}

		return ret;
	} 
}