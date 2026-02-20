package de.jpaw.util.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jpaw.util.CharTestsASCII;

/** Tests for the various CharTestASCII checks. */
public class ASCIITest {

    private void runTest(String data, boolean isDigits, boolean isUpper, boolean isLower, boolean isPrintable, boolean isPrintableOrTab) {
        Assertions.assertEquals(isDigits,        CharTestsASCII.isDigit(data));
        Assertions.assertEquals(isDigits,        CharTestsASCII.isDigitByPattern(data));
        Assertions.assertEquals(isUpper,         CharTestsASCII.isUpperCase(data));
        Assertions.assertEquals(isUpper,         CharTestsASCII.isUpperCaseByPattern(data));
        Assertions.assertEquals(isLower,         CharTestsASCII.isLowerCase(data));
        Assertions.assertEquals(isLower,         CharTestsASCII.isLowerCaseByPattern(data));
        Assertions.assertEquals(isPrintable,     CharTestsASCII.isPrintable(data));
        Assertions.assertEquals(isPrintable,     CharTestsASCII.isPrintableByPattern(data));
        Assertions.assertEquals(isPrintableOrTab, CharTestsASCII.isPrintableOrTab(data));
        Assertions.assertEquals(isPrintableOrTab, CharTestsASCII.isPrintableOrTabByPattern(data));
    }

    @Test
    public void testStrings() throws Exception {
        runTest("42",           true,  false, false, true,  true);
        runTest("Hello, world", false, false, false, true,  true);
        runTest("Hello\tWorld", false, false, false, false, true);
        runTest("hello",        false, false, true,  true,  true);
        runTest("HELLO",        false, true,  false, true,  true);
        runTest("HELLO\r\n",    false, false, false, false, false);
    }
}
