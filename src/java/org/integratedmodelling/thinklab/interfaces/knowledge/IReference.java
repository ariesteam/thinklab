package org.integratedmodelling.thinklab.interfaces.knowledge;

/**
 * Used internally to tag objects that serve as a reference for another, to avoid
 * endless recursions.
 */
public interface IReference<T> {

	public T getReferencedObject();
}
