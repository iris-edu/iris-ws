package edu.iris.dmc.service.response;

import java.util.Iterator;

public class AutoCloseableIterator<T> implements IterableIterator<T>, AutoCloseable {

	private final Iterator<T> wrapped;

	public AutoCloseableIterator(final Iterator<T> wrapped, AutoCloseable closeHook) {
		this.wrapped = wrapped;
	}

	@Override
	public boolean hasNext() {
		return wrapped.hasNext();
	}

	@Override
	public T next() {
		return wrapped.next();
	}

	@Override
	public void remove() {
		wrapped.remove();
	}

	@Override
	public Iterator<T> iterator() {
		return wrapped;
	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		
	}
}
