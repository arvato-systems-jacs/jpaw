package de.jpaw.enums;

import java.util.Iterator;
import java.util.NoSuchElementException;

/** An alternate implementation of EnumSet, but with the ability to obtain the resulting bitmap, for either transfer or storing in a database.
 * The underlying object is a long, therefore the maximum number of enum tokens is 63 (as we don't want negative values). */
public abstract class AbstractLongEnumSet<E extends Enum<E>> extends AbstractFreezableEnumSet<E> {
    private static final long serialVersionUID = 34398390989170000L + 63;
    private static final long BIT = 1;
    /** The maximum number of tokens this enumset can store. */
    public static final int MAX_TOKENS = 63;
    private long bitmap;

    /** This method returns the number of instances (max Ordinal + 1), the name is misleading!!!!
     * This definition has been made to avoid a negative return code in the case of empty enums. */
    protected abstract int getMaxOrdinal();

    public AbstractLongEnumSet() {
        bitmap = 0;
    }

    public AbstractLongEnumSet(final long bitmap) {
        this.bitmap = bitmap;
    }


    public final long getBitmap() {
        return bitmap;
    }

    /** Constructs a String with one digit / letter representing a bit position. */
    public String asStringMap() {
        final StringBuilder sb = new StringBuilder(MAX_TOKENS);
        long rotmap = bitmap;
        for (int i = 0; i < MAX_TOKENS; ++i) {
            if ((rotmap & BIT) != 0)
                sb.append(STANDARD_TOKENS.charAt(i));
            rotmap >>= 1;
        }
        return sb.toString();
    }

    /** Constructs a bitmap from a standard string map. */
    public static long fromStringMap(final String s) {
        long work = 0;
        for (int i = 0; i < s.length(); ++i) {
            final char c = s.charAt(i);
            final int pos = STANDARD_TOKENS.indexOf(c);
            if (pos >= 0 && pos < MAX_TOKENS) {
                work |= BIT << pos;
            } else {
                throw new IllegalArgumentException("Invalid enum set character: " + Character.valueOf(c));
            }
        }
        return work;
    }

    /** Creates a bitmap from an array of arbitrary enums. */
    public static long bitmapOf(final Enum<?>[] arg) {
        long val = 0;
        for (int i = 0; i < arg.length; ++i) {
            val |= BIT << arg[i].ordinal();
        }
        return val;
    }

    @Override
    public final int size() {
        return Long.bitCount(bitmap);
    }

    @Override
    public final boolean isEmpty() {
        return bitmap == 0;
    }

    @Override
    public final boolean contains(final Object o) {
        if (o == null || !(o instanceof Enum))
            return false;
        final int q = ((Enum<?>)o).ordinal();
        return q < MAX_TOKENS && (bitmap & (BIT << q)) != 0;
    }

    @Override
    public final boolean add(final E e) {
        final int q = e.ordinal();   // may throw NPE
        if (q >= MAX_TOKENS || q >= getMaxOrdinal())
            throw new IllegalArgumentException(e.getClass().getCanonicalName() + "." + e.name() + " has ordinal " + e.ordinal());
        final long b = (long) (BIT << q);
        if ((bitmap & b) != 0)
            return false;
        verify$Not$Frozen();            // check if modification is allowed
        bitmap |= b;
        return true;
    }

    @Override
    public final boolean remove(final Object o) {
        if (o == null || !(o instanceof Enum))      // preliminary check for "not contained"
            return false;
        final int q = ((Enum<?>)o).ordinal();
        if (q >= MAX_TOKENS || q >= getMaxOrdinal())
            throw new IllegalArgumentException(o.getClass().getCanonicalName() + "." + o.toString() + " has ordinal " + q + " which is too big for this set");
        final long b = (long) (BIT << q);
        if ((bitmap & b) == 0)
            return false;
        verify$Not$Frozen();                // check if modification is allowed
        bitmap &= ~b;
        return true;
    }

    @Override
    public final void clear() {
        if (bitmap != 0) {
            verify$Not$Frozen();            // check if modification is allowed
            bitmap = 0;
        }
    }

    @Override
    public final int hashCode() {
        return (int)(bitmap ^ (bitmap >>> 32));
    }

    @Override
    public final boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass())
            return false;
        return bitmap == ((AbstractLongEnumSet<?>)o).getBitmap();
    }

    /** Merges (boolean OR) another bitmap into this one. */
    public final void unifyWith(final AbstractLongEnumSet<E> that) {
        verify$Not$Frozen();                // check if modification is allowed
        bitmap |= that.bitmap;
    }

    /** Merges (boolean AND) another bitmap into this one. */
    public final void intersectWith(final AbstractLongEnumSet<E> that) {
        verify$Not$Frozen();                // check if modification is allowed
        bitmap &= that.bitmap;
    }

    /** Subtracts another bitmap from this one. */
    public final void exclude(final AbstractLongEnumSet<E> that) {
        verify$Not$Frozen();                // check if modification is allowed
        bitmap &= ~that.bitmap;
    }

    /** Merges (XOR) another bitmap into this one. Provided for completeness */
    public final void exactlyOneOf(final AbstractLongEnumSet<E> that) {
        verify$Not$Frozen();                // check if modification is allowed
        bitmap ^= that.bitmap;
    }

    /** Returns the bitmap of the full set with all elements included. */
    public final long bitmapFullSet() {
        return (long) ((BIT << getMaxOrdinal()) - BIT);      // Java looses type on binary operations such that a cast is required for byte and long
    }

    /** Negates a set. */
    public final void complement() {
        verify$Not$Frozen();                // check if modification is allowed
        bitmap = (long) (~bitmap & bitmapFullSet());      // Java looses type on binary operations such that a cast is required for byte and long
    }

    protected static final class SetOfEnumsIterator<E extends Enum<E>> implements Iterator<E> {
        private final E[] values;
        private long bitmap;
        private int index;

        public SetOfEnumsIterator(final E[] values, final long bitmap) {
            this.values = values;
            this.bitmap = bitmap;
            this.index = -1;
        }

        @Override
        public boolean hasNext() {
            return bitmap != 0;
        }

        @Override
        public E next() {
            if (bitmap == 0) {
                // by contract, NoSuchElement exception should be thrown
                throw new NoSuchElementException();
            }
            ++index;                                    // index was at previous position. At least increment once
            while ((bitmap & (BIT << index)) == 0) {
                ++index;
            }
            bitmap &= ~(BIT << index);
            return values[index];
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
