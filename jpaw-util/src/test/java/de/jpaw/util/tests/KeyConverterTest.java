package de.jpaw.util.tests;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import de.jpaw.api.iso.CountryKeyConverter;
import de.jpaw.api.iso.CurrencyKeyConverter;
import de.jpaw.api.iso.LanguageKeyConverter;
import de.jpaw.api.iso.impl.JavaCurrencyDataProvider;

public class KeyConverterTest {

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    public void testA2ToInt(boolean initCache) throws Exception {
        if (initCache)
            CountryKeyConverter.populateCache();
        Assertions.assertEquals(5,                 CountryKeyConverter.countryCodeA2ToInt("DE"));
        Assertions.assertEquals(100 + 4 * 26 + 18, CountryKeyConverter.countryCodeA2ToInt("ES"));
        Assertions.assertEquals(1,                 CountryKeyConverter.countryCodeA2ToInt("XX"));
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    public void testIntToA2(boolean initCache) throws Exception {
        if (initCache)
            CountryKeyConverter.populateCache();
        Assertions.assertEquals("DE", CountryKeyConverter.intToCountryCodeA2(5));
        Assertions.assertEquals("ES", CountryKeyConverter.intToCountryCodeA2(100 + 4 * 26 + 18));
        Assertions.assertEquals("XX", CountryKeyConverter.intToCountryCodeA2(1));
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    public void testA3ToInt(boolean initCache) throws Exception {
        if (initCache)
            CurrencyKeyConverter.populateCache(JavaCurrencyDataProvider.INSTANCE);
        Assertions.assertEquals(2,                           CurrencyKeyConverter.currencyCodeA3ToInt("USD"));
        Assertions.assertEquals(100 + 19 * 676 + 13 * 26 + 3, CurrencyKeyConverter.currencyCodeA3ToInt("TND"));
        Assertions.assertEquals(1,                           CurrencyKeyConverter.currencyCodeA3ToInt("XXX"));
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    public void testIntToA3(boolean initCache) throws Exception {
        if (initCache)
            CurrencyKeyConverter.populateCache(JavaCurrencyDataProvider.INSTANCE);
        Assertions.assertEquals("USD", CurrencyKeyConverter.intToCurrencyCodeA3(2));
        Assertions.assertEquals("TND", CurrencyKeyConverter.intToCurrencyCodeA3(100 + 19 * 676 + 13 * 26 + 3));
        Assertions.assertEquals("XXX", CurrencyKeyConverter.intToCurrencyCodeA3(1));
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    public void testLangToInt(boolean initCache) throws Exception {
        if (initCache)
            LanguageKeyConverter.populateCache();
        Assertions.assertEquals(2,                   LanguageKeyConverter.languageCodeToInt("es"));               // frequent
        Assertions.assertEquals(60 + 2 * 32 + 2,    LanguageKeyConverter.languageCodeToInt("bb")); // uncached
        Assertions.assertEquals(1,                   LanguageKeyConverter.languageCodeToInt("xx"));               // default, smallest value
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    public void testIntToLang(boolean initCache) throws Exception {
        if (initCache)
            LanguageKeyConverter.populateCache();
        Assertions.assertEquals("es", LanguageKeyConverter.intToLanguageCode(2));
        Assertions.assertEquals("bb", LanguageKeyConverter.intToLanguageCode(60 + 2 * 32 + 2));
        Assertions.assertEquals("xx", LanguageKeyConverter.intToLanguageCode(1));
    }
}
