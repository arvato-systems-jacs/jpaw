package de.jpaw.fixedpoint.tests;

import java.math.BigInteger;
import java.math.RoundingMode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jpaw.fixedpoint.FixedPointBase;

public class TestDivideLongsBigInteger {
    private static final BigInteger TWO = BigInteger.valueOf(2L);

    /** Reference implementation using arbitrary precision quotient and remainder arithmetic. */
    private static long divideAndRound(final long a, final long b, final RoundingMode rounding) {
        final BigInteger dividend = BigInteger.valueOf(a);
        final BigInteger divisor = BigInteger.valueOf(b);
        BigInteger[] qr = dividend.divideAndRemainder(divisor); // BigInteger division truncates toward zero
        final BigInteger quotient = qr[0];
        final BigInteger remainder = qr[1];
        if (remainder.signum() == 0) {
            return quotient.longValueExact();
        }

        final int resultSign = dividend.signum() * divisor.signum();
        final BigInteger step = BigInteger.valueOf(resultSign);
        switch (rounding) {
        case UP:
            return quotient.add(step).longValueExact();
        case DOWN:
            return quotient.longValueExact();
        case CEILING:
            return (resultSign > 0 ? quotient.add(BigInteger.ONE) : quotient).longValueExact();
        case FLOOR:
            return (resultSign < 0 ? quotient.subtract(BigInteger.ONE) : quotient).longValueExact();
        case HALF_UP:
        case HALF_DOWN:
        case HALF_EVEN:
            final int halfComparison = remainder.abs().multiply(TWO).compareTo(divisor.abs());
            final boolean roundAwayFromZero = halfComparison > 0
                    || (halfComparison == 0 && (rounding == RoundingMode.HALF_UP
                            || (rounding == RoundingMode.HALF_EVEN && quotient.testBit(0))));
            return (roundAwayFromZero ? quotient.add(step) : quotient).longValueExact();
        case UNNECESSARY:
            throw new ArithmeticException("Rounding necessary");
        default:
            throw new AssertionError("Unhandled rounding mode " + rounding);
        }
    }

    @Test
    public void testAgainstBigIntegerReference() {
        final long oddLarge = Long.MAX_VALUE;
        final long evenLarge = Long.MAX_VALUE - 1;
        final long[][] tuples = {
            // exact, zero, and unit divisor cases
            { 0L, 3L }, { 12L, 3L }, { 7L, 1L }, { 7L, -1L },
            // Odd divisor: below half, above half, and odd/even truncated quotients
            { 4L, 3L }, { 5L, 3L }, { 8L, 3L },
            // Even divisor: below half, exact half with even and odd quotients, and above half
            { 5L, 4L }, { 6L, 4L }, { 10L, 4L }, { 7L, 4L },
            // The same odd/even boundary cases with a negative divisor
            { 4L, -3L }, { 6L, -4L }, { 7L, -4L },
            // Large odd/even divisors exercise half comparisons without overflowing 2 * remainder
            { oddLarge / 2L, oddLarge }, { oddLarge / 2L + 1L, oddLarge },
            { evenLarge / 2L, evenLarge }, { evenLarge / 2L - 1L, evenLarge },
            // Large dividend values, including a half tie with an odd quotient
            { Long.MAX_VALUE, 2L }, { Long.MAX_VALUE, -2L }
        };

        for (final long[] tuple : tuples) {
            final long a = tuple[0];
            final long b = tuple[1];
            // a is chosen so negating it is representable; Long.MIN_VALUE is outside the method's supported domain.
            for (final long dividend : new long[] { a, -a }) {
                for (final RoundingMode rounding : RoundingMode.values()) {
                    if (rounding == RoundingMode.UNNECESSARY) {
                        continue;
                    }
                    final long expected = divideAndRound(dividend, b, rounding);
                    final long actual = FixedPointBase.divide_longs(dividend, b, rounding);
                    Assertions.assertEquals(expected, actual,
                            "a=" + dividend + ", b=" + b + ", rounding=" + rounding);
                }
            }
        }
    }
}
