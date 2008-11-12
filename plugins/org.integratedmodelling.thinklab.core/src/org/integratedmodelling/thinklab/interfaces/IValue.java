/**
 * IValue.java
 * ----------------------------------------------------------------------------------
 * 
 * Copyright (C) 2008 www.integratedmodelling.org
 * Created: Jan 17, 2008
 *
 * ----------------------------------------------------------------------------------
 * This file is part of Thinklab.
 * 
 * Thinklab is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Thinklab is distributed in the hope that it will be useful,
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
 * @author    Ferdinando Villa (fvilla@uvm.edu)
 * @author    Ioannis N. Athanasiadis (ioannis@athanasiadis.info)
 * @date      Jan 17, 2008
 * @license   http://www.gnu.org/licenses/gpl.txt GNU General Public License v3
 * @link      http://www.integratedmodelling.org
 **/
package org.integratedmodelling.thinklab.interfaces;

import org.integratedmodelling.thinklab.exception.ThinklabException;
import org.integratedmodelling.thinklab.exception.ThinklabInappropriateOperationException;
import org.integratedmodelling.thinklab.exception.ThinklabNoKMException;
import org.integratedmodelling.thinklab.exception.ThinklabNotANumberException;
import org.integratedmodelling.thinklab.exception.ThinklabValidationException;
import org.integratedmodelling.thinklab.exception.ThinklabValueConversionException;
import org.integratedmodelling.thinklab.extensions.LiteralValidator;
import org.integratedmodelling.thinklab.value.AlgorithmValue;
import org.integratedmodelling.thinklab.value.BooleanValue;
import org.integratedmodelling.thinklab.value.NumberValue;
import org.integratedmodelling.thinklab.value.ObjectReferenceValue;
import org.integratedmodelling.thinklab.value.ObjectValue;
import org.integratedmodelling.thinklab.value.TextValue;

/**
 * <p>
 * A generalized container for a value that always has a concept associated. The
 * value may be defined from a literal or a basic type, or be a concept or an
 * instance in itself. It's the most general idea of a value, but it has an
 * unbreakable association to the knowledge base.
 * </p>
 * 
 * <p>
 * The Value can have a value (oh yes), which simply means that a Java object
 * can be associated to the concept and represent an instance implementation,
 * substituting a full Instance object with properties. Typically this applies
 * to objects that are created from literals and belong to simple types, such as
 * text or numbers, although more complex ones (e.g. JTS shapes) are also
 * possible.
 * </p>
 * 
 * <p>
 * The value may be as multiple as necessary, e.g. a Collection or a Polylist,
 * as long as the associated concept is unambiguous in suggesting that. If the
 * multiplicity depends on the properties, then a full Instance should be used.
 * </p>
 * 
 * <p>
 * The least a Value can do is to be a Concept, so the base Value class is
 * exactly that, and even the trivial constructor assigns a concept (the most
 * general one) to it.
 * </p>
 * 
 * <p>
 * By its own nature, a Value is perfect to implement a stack for a complex
 * language or a return type for any action. In fact, it's used exactly for
 * these purposes in JIMT, along with storage of literals in relationships.
 * </p>
 * 
 * <p>
 * Efficiency of the approach is not maximal, obviously, but if you want it
 * efficient, use the C++ implementation, not this one. On the other hand, the
 * C++ implementation WILL drive you crazy, and this probably won't, unless
 * you're crazy already.
 * </p>
 * 
 * <p>
 * The IMA core provides implementations of numbers, text, booleans, concepts
 * and object(instance) values. These should be enough for a lot of
 * applications, given that some degree of polymorphism is provided by the
 * concept side. Other packages (e.g. time and space) provide more. Note that
 * implementing a Value subclass, unless limited to specialization of the
 * associated concept, isn't trivial due to handling of operators, cloning, and
 * proper checking methods, so should be done carefully.
 * </p>
 * 
 * @author Ferdinando Villa
 */
public interface IValue {

	/**
	 * set the concept to the least general common one between ours and the
	 * passed, making sure the resulting concept is still the second argument.
	 * 
	 * @param setTo
	 *            what to set to
	 * @param mustBe
	 *            what we must remain after the operation
	 */
	public abstract void setToCommonConcept(IConcept setTo, IConcept mustBe)
			throws ThinklabValueConversionException;

	/**
	 * <p>
	 * Apply passed operator to this and return a new Value containing the
	 * result of the operation.
	 * </p>
	 * <p>
	 * Operators don't have a fixed model but are available for subclasses to
	 * redefine.
	 * </p>
	 * <p>
	 * Note that any operation that combines two Values will set the Value's
	 * concept to the least general common concept between the two. If that does
	 * not exist or it's too general for the Value type, a conversion exception
	 * will be thrown.
	 * </p>
	 * <p>
	 * The base class only supports an assignment operator (=), which creates
	 * the "common ground" conceptual value, and equality/inequality ops. These
	 * should be supported appropriately by all subclasses.
	 * </p>
	 * 
	 * @param op
	 *            a string representing an operator
	 * @param other
	 * @return a new Value with the result of the operation
	 * @throws ThinklabInappropriateOperationException
	 *             if operator does not apply to type
	 * @throws ThinklabValueConversionException
	 *             if value or its concept does not convert
	 */
	public abstract IValue op(String op, IValue ... other)
			throws ThinklabInappropriateOperationException,
			ThinklabValueConversionException;

	/**
	 * 
	 * @param other
	 * @return
	 * @throws ThinklabInappropriateOperationException
	 * @throws ThinklabValueConversionException
	 */
	public abstract int compareTo(IValue other)
			throws ThinklabInappropriateOperationException,
			ThinklabValueConversionException;

	/**
	 * 
	 * @param other
	 * @return
	 */
	public abstract boolean equals(IValue other);

	/**
	 * 
	 * @param index
	 * @return
	 * @throws ThinklabInappropriateOperationException
	 */
	public abstract IValue getAt(int index)
			throws ThinklabInappropriateOperationException;

	/**
	 * 
	 * @param value
	 * @param index
	 * @return
	 * @throws ThinklabInappropriateOperationException
	 */
	public abstract IValue putAt(IValue value, int index)
			throws ThinklabInappropriateOperationException;

	/**
	 * Bridge for embedded languages and for convenience. Typically mapped to
	 * op() with the appropriate operator.
	 * 
	 * @param v
	 * @return
	 * @throws ThinklabValueConversionException
	 * @throws ThinklabInappropriateOperationException
	 */
	public IValue plus(IValue v) throws ThinklabValueConversionException,
			ThinklabInappropriateOperationException;

	/**
	 * Bridge for embedded languages and for convenience. Typically mapped to
	 * op() with the appropriate operator.
	 * 
	 * @param v
	 * @return
	 * @throws ThinklabValueConversionException
	 * @throws ThinklabInappropriateOperationException
	 */
	public IValue minus(IValue v) throws ThinklabValueConversionException,
			ThinklabInappropriateOperationException;

	/**
	 * Bridge for embedded languages and for convenience. Typically mapped to
	 * op() with the appropriate operator.
	 * 
	 * @param v
	 * @return
	 * @throws ThinklabValueConversionException
	 * @throws ThinklabInappropriateOperationException
	 */
	public IValue multiply(IValue v) throws ThinklabValueConversionException,
			ThinklabInappropriateOperationException;

	/**
	 * Bridge for embedded languages and for convenience. Typically mapped to
	 * op() with the appropriate operator.
	 * 
	 * @param v
	 * @return
	 * @throws ThinklabValueConversionException
	 * @throws ThinklabInappropriateOperationException
	 */
	public IValue divide(IValue v) throws ThinklabValueConversionException,
			ThinklabInappropriateOperationException;

	/**
	 * Bridge for embedded languages and for convenience. Typically mapped to
	 * op() with the appropriate operator.
	 * 
	 * @param v
	 * @return
	 * @throws ThinklabValueConversionException
	 * @throws ThinklabInappropriateOperationException
	 */
	public IValue or(IValue v) throws ThinklabValueConversionException,
			ThinklabInappropriateOperationException;

	/**
	 * Bridge for embedded languages and for convenience. Typically mapped to
	 * op() with the appropriate operator.
	 * 
	 * @param v
	 * @return
	 * @throws ThinklabValueConversionException
	 * @throws ThinklabInappropriateOperationException
	 */
	public IValue and(IValue v) throws ThinklabValueConversionException,
			ThinklabInappropriateOperationException;

	/**
	 * Bridge for embedded languages and for convenience. Typically mapped to
	 * op() with the appropriate operator.
	 * 
	 * @param v
	 * @return
	 * @throws ThinklabValueConversionException
	 * @throws ThinklabInappropriateOperationException
	 */
	public IValue next() throws ThinklabValueConversionException,
			ThinklabInappropriateOperationException;

	/**
	 * Bridge for embedded languages and for convenience. Typically mapped to
	 * op() with the appropriate operator.
	 * 
	 * @param v
	 * @return
	 * @throws ThinklabValueConversionException
	 * @throws ThinklabInappropriateOperationException
	 */
	public IValue previous() throws ThinklabValueConversionException,
			ThinklabInappropriateOperationException;

	/**
	 * Bridge for embedded languages and for convenience. Typically mapped to
	 * op() with the appropriate operator.
	 * 
	 * @param v
	 * @return
	 * @throws ThinklabValueConversionException
	 * @throws ThinklabInappropriateOperationException
	 */
	public IValue leftShift(IValue v) throws ThinklabValueConversionException,
			ThinklabInappropriateOperationException;

	public abstract IValue clone();

	public abstract boolean isNumber();

	public abstract boolean isText();

	public abstract boolean isBoolean();

	public abstract boolean isClass();

	public abstract boolean isObject();

	public abstract boolean isObjectReference();

	public abstract boolean isList();

	public abstract boolean isLiteral();

	/**
	 * 
	 * @return
	 * @throws ThinklabValueConversionException
	 */
	public abstract NumberValue asNumber()
			throws ThinklabValueConversionException;

	/**
	 * 
	 * @return
	 * @throws ThinklabValueConversionException
	 */
	public abstract TextValue asText() throws ThinklabValueConversionException;

	/**
	 * 
	 * @return
	 * @throws ThinklabValueConversionException
	 */
	public abstract ObjectValue asObject()
			throws ThinklabValueConversionException;

	/**
	 * 
	 * @return
	 * @throws ThinklabValueConversionException
	 */
	public abstract ObjectReferenceValue asObjectReference()
			throws ThinklabValueConversionException;

	/**
	 * 
	 * @return
	 * @throws ThinklabValueConversionException
	 */
	public abstract BooleanValue asBoolean()
			throws ThinklabValueConversionException;

	/**
	 * Reset the concept associated with a value, making sure that it is
	 * compatible with the current concept in there (e.g. it's not possible to
	 * reset a Number to something that is not a Number).
	 * 
	 * @param concept
	 *            the concept to set into the value
	 * @throws ThinklabValidationException
	 *             if concepts don't match
	 */
	public abstract void setConceptWithValidation(IConcept concept)
			throws ThinklabValidationException;

	/**
	 * Reset the concept without checking. Use only if you're God.
	 * 
	 * @param concept
	 *            the concept to set into the value
	 */
	public abstract void setConceptWithoutValidation(IConcept concept);

	/**
	 * Check if value can be represented by a plain old data type literal. Note
	 * that the concept can be any concept derived from the base concepts
	 * installed for Text, Number, or Boolean.
	 * 
	 * @return true if POD type.
	 */
	public abstract boolean isPODType();

	/**
	 * In some case we need to be able to tag values with a name.
	 * 
	 * @param localName
	 *            the name for the value.
	 */
	public abstract void setID(String localName);

	/**
	 * Return the name of the value, if any.
	 * 
	 * @return a name previously set with setID(), or null.
	 * @see setID
	 */
	public abstract String getID();

	/**
	 * Return the concept expressed in the value.
	 * 
	 * @return
	 */
	public abstract IConcept getConcept();
	
	/**
	 * 
	 * @param operator
	 * @return
	 */
	public boolean hasOperator(String operator);
	
	/**
	 * 
	 * @param operator
	 * @return
	 * @throws ThinklabValidationException 
	 */
	public int getNOfArguments(String operator) throws ThinklabValidationException;
	
	/**
	 * 
	 * @param operator
	 * @return
	 * @throws ThinklabException 
	 */
	public IConcept getReturnType(String operator) throws ThinklabException;
	
	/**
	 * 
	 * @param operator
	 * @return
	 * @throws ThinklabException 
	 */
	public LiteralValidator getReturnTypeValidator(String operator) throws ThinklabException;

	/**
	 * 
	 * @param operator
	 * @param argument
	 * @return
	 * @throws ThinklabException 
	 */
	public LiteralValidator getArgumentValidator(String operator, int argument) throws ThinklabException;

	/**
	 * 
	 * @param operator
	 * @param argument
	 * @return
	 * @throws ThinklabException 
	 */
	public IConcept getArgumentType(String operator, int argument) throws ThinklabException;
	
	
	/**
	 * Execute the passed algorithm in the context of the value, which is 
	 * known in the algorithm as "self". Return whatever the interpreter for
	 * the algorithm returns.
	 * @param aa
	 * @return
	 */
	public abstract IValue execute(AlgorithmValue aa, ISession session) throws ThinklabException;

	/**
	 * Return the literal we're wrapping stripped of its semantics, with the most appropriate type.
	 * @return
	 */
	public abstract Object demote();
	
}