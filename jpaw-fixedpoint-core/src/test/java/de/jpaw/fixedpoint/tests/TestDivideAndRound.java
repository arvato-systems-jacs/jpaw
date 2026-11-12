package de.jpaw.fixedpoint.tests;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jpaw.fixedpoint.types.MilliUnits;
import de.jpaw.fixedpoint.types.VariableUnits;

public class TestDivideAndRound {
    private static final int RESULT_SCALE = 3;

    private static final RoundingMode[] ROUNDING_MODES = {
        RoundingMode.UP,
        RoundingMode.DOWN,
        RoundingMode.CEILING,
        RoundingMode.FLOOR,
        RoundingMode.HALF_UP,
        RoundingMode.HALF_DOWN,
        RoundingMode.HALF_EVEN
    };

    private static void assertFixedPointDivision(final long dividendMantissa, final long divisorMantissa,
            final int divisorScale, final int desiredDecimals, final RoundingMode rounding) {
        final MilliUnits dividend = MilliUnits.of(dividendMantissa);
        final VariableUnits divisor = VariableUnits.of(divisorMantissa, divisorScale);
        final BigDecimal exactQuotient = BigDecimal.valueOf(dividendMantissa, RESULT_SCALE)
                .divide(BigDecimal.valueOf(divisorMantissa, divisorScale), desiredDecimals, rounding);
        final long expectedMantissa = exactQuotient.movePointRight(RESULT_SCALE).longValueExact();
        final MilliUnits actual = dividend.divideAndRound(divisor, desiredDecimals, rounding);
        Assertions.assertEquals(expectedMantissa, actual.getMantissa(),
                "dividend=" + dividend + ", divisor=" + divisor + ", decimals=" + desiredDecimals + ", rounding=" + rounding);
    }

    private static void assertIntegralDivision(final long dividendMantissa, final long divisor,
            final int desiredDecimals, final RoundingMode rounding) {
        final MilliUnits dividend = MilliUnits.of(dividendMantissa);
        final BigDecimal exactQuotient = BigDecimal.valueOf(dividendMantissa, RESULT_SCALE)
                .divide(BigDecimal.valueOf(divisor), desiredDecimals, rounding);
        final long expectedMantissa = exactQuotient.movePointRight(RESULT_SCALE).longValueExact();
        final MilliUnits actual = dividend.divideAndRound(divisor, desiredDecimals, rounding);
        Assertions.assertEquals(expectedMantissa, actual.getMantissa(),
                "dividend=" + dividend + ", divisor=" + divisor + ", decimals=" + desiredDecimals + ", rounding=" + rounding);
    }

    @Test
    public void testDivideAndRoundByFixedPointAgainstBigDecimal() {
        // Includes the fallback path (divisor scale below digitsToRound) and the direct path.
        final long[][] cases = {
            // dividend mantissa, divisor mantissa, divisor scale, desired decimals
            { 4899L, 20L, 1L, 1L },    // 4.899 / 2.0 = 2.4495; double rounding used to produce 2.5
            { 4900L, 20L, 1L, 1L },    // exact tie: 2.45
            { 4700L, 20L, 1L, 1L },    // exact tie: 2.35, HALF_EVEN rounds up to 2.4
            { -4899L, 20L, 1L, 1L },
            { 4899L, -20L, 1L, 1L },
            { 4899L, 2000L, 3L, 1L },  // direct scaling path
            { 1234L, 3000L, 3L, 2L },
            { 6000L, 3000L, 3L, 0L },  // exact quotient
            { 4899L, Long.MAX_VALUE, 0L, 0L }, // scaled divisor overflows long: BigDecimal fallback
            { 0L, 25L, 1L, 2L }
        };

        for (final long[] testCase : cases) {
            for (final RoundingMode rounding : ROUNDING_MODES) {
                assertFixedPointDivision(testCase[0], testCase[1], (int)testCase[2], (int)testCase[3], rounding);
            }
        }
    }

    @Test
    public void testDivideAndRoundByIntegralAgainstBigDecimal() {
        // The integral-divisor overload exercises the double-rounding fallback whenever desiredDecimals < 3.
        final long[][] cases = {
            // dividend mantissa, divisor, desired decimals
            { 4899L, 2L, 1L },         // 4.899 / 2 = 2.4495; double rounding used to produce 2.5
            { 4900L, 2L, 1L },         // exact tie: 2.45
            { 4700L, 2L, 1L },         // exact tie: 2.35
            { -4899L, 2L, 1L },
            { 4899L, -2L, 1L },
            { 1234L, 3L, 2L },
            { 4899L, 2L, 3L },         // desired scale equals the type scale, so no final scale reduction is needed
            { 6000L, 3L, 0L },         // exact quotient
            { 4899L, Long.MAX_VALUE, 0L }, // scaled divisor overflows long: BigDecimal fallback
            { 0L, -5L, 2L }
        };

        for (final long[] testCase : cases) {
            for (final RoundingMode rounding : ROUNDING_MODES) {
                assertIntegralDivision(testCase[0], testCase[1], (int)testCase[2], rounding);
            }
        }
    }

    @Test
    public void testDoubleRoundingRegressionForBothOverloads() {
        final MilliUnits dividend = MilliUnits.of(4899L); // 4.899
        final MilliUnits expected = MilliUnits.of(2400L);  // 2.4, since 4.899 / 2 = 2.4495

        Assertions.assertEquals(expected, dividend.divideAndRound(2L, 1, RoundingMode.HALF_UP));
        Assertions.assertEquals(expected,
                dividend.divideAndRound(VariableUnits.of(2L, 0), 1, RoundingMode.HALF_UP));
    }
}
