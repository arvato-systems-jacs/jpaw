package de.jpaw.enums;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/** An alternate implementation of EnumSet, which stores the contained elements as a component inside Strings.
 * The class is not thread safe. Implementations which intend to perform parallel modification must use external locking mechanisms.
 * Further requirements of this implementation:
 * All tokens must be of length 1. */
public abstract class AbstractStringEnumSet<E extends Enum<E> & TokenizableEnum> extends AbstractStringAnyEnumSet<E> implements GenericEnumSetMarker<E> {
    private static final long serialVersionUID = 34398390989170000L + 99;

    protected AbstractStringEnumSet() {
        super(EMPTY);
    }

    protected AbstractStringEnumSet(final String bitmap) {
        super(bitmap);
    }

    @Override
    public boolean add(final E e) {
        return addEnum(e);
    }

    /** Let this instance have the same contents as that. */
    @Override
    public void assign(final Collection<E> that) {
        clear();
        if (that != null) {
            for (final E o : that) {
                add(o);
            }
        }
    }

    /** Iterator which returns the elements of the set in order of tokens sorted ascending. */
    protected static class SetOfEnumsIterator<E extends TokenizableEnum> implements Iterator<E> {
        private final String bitmap;
        // private static final ConcurrentHashMap<String, TokenizableEnum> lookupTable = new ConcurrentHashMap<String, TokenizableEnum>();
        private final E[] values;
        private int index = 0;

        public SetOfEnumsIterator(final E[] values, final String bitmap) {
            this.bitmap = bitmap;
            this.values = values;
        }

        private E getValue(final String token) {
            for (final E e: values) {
                if (token.equals(e.getToken())) {
                    return e;
                }
            }
            return null;
        }

        @Override
        public boolean hasNext() {
            return index < bitmap.length();
        }

        @Override
        public E next() {
            if (bitmap.length() <= index) {
                // by contract, NoSuchElement exception should be thrown
                throw new NoSuchElementException();
            }
            ++index;
            final E nextEnum = getValue(bitmap.substring(index - 1, index));   // GC overhead due to new String. But a Character would be as well...
            if (nextEnum == null) {
                throw new NoSuchElementException();
            }
            return nextEnum;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
