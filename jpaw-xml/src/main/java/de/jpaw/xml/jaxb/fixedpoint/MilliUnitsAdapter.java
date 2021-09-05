package de.jpaw.xml.jaxb.fixedpoint;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import de.jpaw.fixedpoint.types.MilliUnits;

public class MilliUnitsAdapter extends XmlAdapter<String, MilliUnits> {

    @Override
    public MilliUnits unmarshal(String v) throws Exception {
        return MilliUnits.valueOf(v);
    }

    @Override
    public String marshal(MilliUnits v) throws Exception {
        return v.toString();
    }
}