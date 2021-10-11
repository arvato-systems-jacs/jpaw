package de.jpaw.fixedpoint.types;

import java.math.BigDecimal;
import java.math.RoundingMode;

import de.jpaw.fixedpoint.FixedPointBase;

public class Units extends FixedPointBase<Units> {
    private static final long serialVersionUID = -466464673376366000L;
    public static final int DECIMALS = 0;
    public static final long UNIT_MANTISSA = 1L;
    public static final double UNIT_SCALE = UNIT_MANTISSA;       // cast to double at class initialization time
    public static final double UNIT_SCALE_AS_DOUBLE_FACTOR = 1.0 / UNIT_MANTISSA;  // multiplication is much faster than division
    public static final Units ZERO = new Units(0);
    public static final Units ONE = new Units(UNIT_MANTISSA);

    // external callers use valueOf factory method, which returns existing objects for 0 and 1. This constructor is used by the factory methods
    private Units(long mantissa) {
        super(mantissa);
    }

    /** Constructs an instance with a specified mantissa. See also valueOf(long value), which constructs an integral instance. */
    public static Units parse(String value) {
        return ZERO.newInstanceOf(parseMantissa(value, DECIMALS));
    }

    /** Constructs an instance with a specified mantissa. See also valueOf(long value), which constructs an integral instance. */
    public static Units of(long mantissa) {
        return ZERO.newInstanceOf(mantissa);
    }

    /** Constructs an instance with a specified integral value. See also of(long mantissa), which constructs an instance with a specified mantissa. */
    public static Units valueOf(long value) {
        return ZERO.newInstanceOf(value);
    }

    /** Constructs an instance with a specified value specified via floating point. Take care for rounding issues! */
    public static Units valueOf(double value) {
        return ZERO.newInstanceOf(Math.round(value));
    }

    /** Constructs an instance with a specified value specified via string representation. */
    public static Units valueOf(String value) {
        return ZERO.newInstanceOf(parseMantissa(value, DECIMALS));
    }

    /** Returns a re-typed instance of that. Loosing precision is not supported. */
    public static Units of(FixedPointBase<?> that) {
        int scaleDiff = DECIMALS - that.scale();
        if (scaleDiff >= 0)
            return Units.of(that.getMantissa() * powersOfTen[scaleDiff]);
        throw new ArithmeticException("Retyping with reduction of scale requires specfication of a rounding mode");
    }

    /** Returns a re-typed instance of that. */
    public static Units of(FixedPointBase<?> that, RoundingMode rounding) {
        int scaleDiff = DECIMALS - that.scale();
        if (scaleDiff >= 0)
            return Units.of(that.getMantissa() * powersOfTen[scaleDiff]);
        // rescale
        return  Units.of(divide_longs(that.getMantissa(), powersOfTen[-scaleDiff], rounding));
    }

    public static Units of(BigDecimal number) {
        final int scaleOfBigDecimal = number.scale();
        if (scaleOfBigDecimal <= 0) {
            // the value of the BigDecimal is integral
            final long valueOfBigDecimal = number.longValue();
            return of(valueOfBigDecimal * powersOfTen[-scaleOfBigDecimal]);
        }
        // This is certainly not the most efficient implementation, as it involves the construction of up to one new BigDecimal and a BigInteger
        // TODO: replace it by a zero GC version
        // blame JDK, there is not even a current method to determine if a BigDecimal is integral despite a scale > 0, nor to get its mantissa without creating additional objects
        return of(number.setScale(DECIMALS, RoundingMode.UNNECESSARY).unscaledValue().longValue());
    }

    @Override
    public Units newInstanceOf(long mantissa) {
        // caching checks...
        if (mantissa == 0)
            return ZERO;
        if (mantissa == UNIT_MANTISSA)
            return ONE;
        if (mantissa == this.mantissa)
            return this;
        return new Units(mantissa);
    }

    @Override
    public int scale() {
        return DECIMALS;
    }

    @Override
    public Units getZero() {
        return ZERO;
    }

    @Override
    public Units getUnit() {
        return ONE;
    }

    @Override
    public long getUnitAsLong() {
        return UNIT_MANTISSA;
    }

    @Override
    public Units getMyself() {
        return this;
    }

    // provide code for the bonaparte adapters, to avoid separate adapter classes
    public long marshal() {
        return mantissa;
    }

    public static Units unmarshal(Long mantissa) {
        return mantissa == null ? null : ZERO.newInstanceOf(mantissa.longValue());
    }

    @Override
    public double getScaleAsDouble() {
        return UNIT_SCALE_AS_DOUBLE_FACTOR;
    }
}
