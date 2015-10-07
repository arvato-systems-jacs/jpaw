package de.jpaw.json;

import java.io.IOException;

import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import de.jpaw.enums.AbstractByteEnumSet;
import de.jpaw.enums.AbstractIntEnumSet;
import de.jpaw.enums.AbstractLongEnumSet;
import de.jpaw.enums.AbstractShortEnumSet;
import de.jpaw.enums.AbstractStringEnumSet;
import de.jpaw.enums.AbstractStringXEnumSet;
import de.jpaw.enums.EnumSetMarker;
import de.jpaw.enums.TokenizableEnum;
import de.jpaw.util.DefaultJsonEscaperForAppendables;

public class ExtendedJsonEscaperForAppendables extends DefaultJsonEscaperForAppendables {

    // if instantInMillis is true, Instants will be written as integral values in milliseconds, otherwise as second + optional fractional parts
    // see DATE_TIMESTAMPS_AS_NANOSECONDS in https://github.com/FasterXML/jackson-datatype-jsr310 for similar setting 
    protected final boolean instantInMillis;
    
    public ExtendedJsonEscaperForAppendables(Appendable appendable) {
        super(appendable);
        instantInMillis = true;
    }

    public ExtendedJsonEscaperForAppendables(Appendable appendable, boolean writeNulls, boolean escapeNonASCII, boolean instantInMillis) {
        super(appendable, writeNulls, escapeNonASCII);
        this.instantInMillis = instantInMillis;
    }
    
    private String toDay(int [] values) {
        return String.format("%04d-%02d-%02d", values[0], values[1], values[2]);
    }

    private String toTimeOfDay(int millis) {
        int tmpValue = millis / 60000; // minutes and hours
        int frac = millis % 1000;
        String fracs = (frac == 0) ? "" : String.format(".%03d", frac);
        return String.format("%02d:%02d:%02d%s", tmpValue / 60, tmpValue % 60, millis / 1000, fracs);
    }

    @Override
    public void outputJsonElement(Object obj) throws IOException {
        if (obj == null) {
            appendable.append("null");
            return;
        }
        // add Joda-Time types and enum types 
        if (obj instanceof Enum) {
            // distinguish Tokenizable
            if (obj instanceof TokenizableEnum) {
                outputUnicodeNoControls(((TokenizableEnum)obj).getToken()); // this includes Xenum
            } else {
                outputNumber(((Enum<?>)obj).ordinal());
            }
            return;
        }
        if (obj instanceof EnumSetMarker) {
            if (obj instanceof AbstractStringEnumSet<?>) {
                outputUnicodeNoControls(((AbstractStringEnumSet<?>)obj).getBitmap());
            } else if (obj instanceof AbstractStringXEnumSet<?>) {
                outputUnicodeNoControls(((AbstractStringXEnumSet<?>)obj).getBitmap());
            } else if (obj instanceof AbstractIntEnumSet<?>) {
                appendable.append(Integer.toString(((AbstractIntEnumSet<?>)obj).getBitmap()));
            } else if (obj instanceof AbstractLongEnumSet<?>) {
                appendable.append(Long.toString(((AbstractLongEnumSet<?>)obj).getBitmap()));
            } else if (obj instanceof AbstractByteEnumSet<?>) {
                appendable.append(Byte.toString(((AbstractByteEnumSet<?>)obj).getBitmap()));
            } else if (obj instanceof AbstractShortEnumSet<?>) {
                appendable.append(Short.toString(((AbstractShortEnumSet<?>)obj).getBitmap()));
            } else {
                throw new IOException("Cannot transform enum set of type " + obj.getClass().getSimpleName() + " to JSON");
            }
            return;
        }
        if (obj instanceof Instant) {
            long millis = ((Instant)obj).getMillis();
            if (instantInMillis) {
                appendable.append(Long.toString(millis));
            } else {
                appendable.append(Long.toString(millis / 1000));
                millis %= 1000;
                if (millis > 0)
                    appendable.append(String.format(".%03d", millis));
            }
            return;
        }
        if (obj instanceof LocalDate) {
            int [] values = ((LocalDate)obj).getValues();   // 3 values: year, month, day
            outputAscii(toDay(values));
            return;
        }
        if (obj instanceof LocalTime) {
            outputAscii(toTimeOfDay(((LocalTime)obj).getMillisOfDay()));
            return;
        }
        if (obj instanceof LocalDateTime) {
            int [] values = ((LocalDateTime)obj).getValues();   // 4 values: year, month, day, millis
            outputAscii(toDay(values) + "T" + toTimeOfDay(values[3]) + "Z");
            return;
        }
        super.outputJsonElement(obj);
    }
}
