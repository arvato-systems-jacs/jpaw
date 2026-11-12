package de.jpaw.fixedpoint.demo;

import de.jpaw.api.iso.impl.JavaCurrencyDataProvider;
import de.jpaw.fixedpoint.money.FPAmount;
import de.jpaw.fixedpoint.money.FPCurrency;

/** Small demo how syntax sugar for xtend could be created. */
public final class FPEurCurrencyExtensions {
   private FPEurCurrencyExtensions() { }

    public static FPCurrency EUR = new FPCurrency(JavaCurrencyDataProvider.INSTANCE.get("EUR"));

    // suffix-like methods, as Xtend syntax sugar
    public static FPAmount Euro(long gross) {
        return new FPAmount(EUR, gross * 100);
    }
}
