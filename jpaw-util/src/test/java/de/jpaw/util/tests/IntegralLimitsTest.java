package de.jpaw.util.tests;


import org.junit.jupiter.api.Test;

import de.jpaw.util.IntegralLimits;

public class IntegralLimitsTest {

    @Test
    public void testByteLimits() throws Exception {
        byte n = 1;
        for (int i = 1; i <= 2; ++i) {
            // increase exponent
            n = (byte)(10 * n);
            // check upper bound
            assert (IntegralLimits.BYTE_MAX_VALUES[i] == n - 1);
            // check lower bound
            assert (IntegralLimits.BYTE_MIN_VALUES[i] == -IntegralLimits.BYTE_MAX_VALUES[i]);
        }
    }

    @Test
    public void testShortLimits() throws Exception {
        short n = 1;
        for (int i = 1; i <= 4; ++i) {
            // increase exponent
            n = (short)(10 * n);
            // check upper bound
            assert (IntegralLimits.SHORT_MAX_VALUES[i] == n - 1);
            // check lower bound
            assert (IntegralLimits.SHORT_MIN_VALUES[i] == -IntegralLimits.SHORT_MAX_VALUES[i]);
        }
    }

    @Test
    public void testIntLimits() throws Exception {
        int n = 1;
        for (int i = 1; i <= 9; ++i) {
            // increase exponent
            n = 10 * n;
            // check upper bound
            assert (IntegralLimits.INT_MAX_VALUES[i] == n - 1);
            // check lower bound
            assert (IntegralLimits.INT_MIN_VALUES[i] == -IntegralLimits.INT_MAX_VALUES[i]);
        }
    }

    @Test
    public void testLongLimits() throws Exception {
        long l = 1L;
        for (int i = 1; i <= 18; ++i) {
            // increase exponent
            l = 10L * l;
            // check upper bound
            assert (IntegralLimits.LONG_MAX_VALUES[i] == l - 1L);
            // check lower bound
            assert (IntegralLimits.LONG_MIN_VALUES[i] == -IntegralLimits.LONG_MAX_VALUES[i]);
        }
    }

    @Test
    public void testExponents() throws Exception {
        double n = 1.0;
        for (int i = 0; i <= 18; ++i) {
            assert (IntegralLimits.EXPONENTS[i] == n);
            n *= 10.0;
        }
    }

    @Test
    public void testFractions() throws Exception {
        double n = 1.0;
        double minDev = 1.0;
        double maxDev = 1.0;
        for (int i = 0; i <= 18; ++i) {
            double rel = IntegralLimits.IMPLICIT_SCALES[i] / n; // this does not match perfectly due to rounding, we check the relative error
            if (rel < minDev)
                minDev = rel;
            if (rel > maxDev)
                maxDev = rel;
            n *= 0.1;
        }
        System.out.println("Min deviation = " + minDev + ", max = " + maxDev);
        assert (maxDev == 1.0);
        assert (minDev >= 0.999999999999999);
    }
}
