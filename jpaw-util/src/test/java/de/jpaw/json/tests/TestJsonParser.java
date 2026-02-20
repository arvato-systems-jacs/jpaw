package de.jpaw.json.tests;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.jpaw.json.BaseJsonComposer;
import de.jpaw.json.JsonEscaper;
import de.jpaw.json.JsonParser;

public class TestJsonParser {

    @Test
    public void testParseNumObject() throws Exception {
        Assertions.assertEquals(Integer.valueOf(-65656565),          new JsonParser("-65656565", true).parseElement());
        Assertions.assertEquals(Long.valueOf(-656565628282828285L),   new JsonParser("-656565628282828285", true).parseElement());
        Assertions.assertEquals(new BigDecimal("3.14"),               new JsonParser("3.14", false).parseElement());
        Assertions.assertEquals(Double.valueOf(3.14),                 new JsonParser("3.14", true).parseElement());
    }

    @Test
    public void testParseStringObject() throws Exception {
        Assertions.assertEquals("hello", new JsonParser("\"hello\"", true).parseElement());
        Assertions.assertNull(new JsonParser("null",      true).parseElement());
    }

    @Test
    public void testParseBooleanObject() throws Exception {
        Assertions.assertEquals(Boolean.TRUE,  new JsonParser("true",  true).parseElement());
        Assertions.assertEquals(Boolean.FALSE, new JsonParser("false", true).parseElement());
    }

    private static Map<String, Object> createMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("hello", 42);
        map.put("what", true);
        map.put("why", "bla");
        map.put("none", null);
        map.put("submap", new HashMap<String, Object>());
        return map;
    }

    @Test
    public void testParseMap() throws Exception {
        String in = "  { \"what\"   : true, \"why\":\"bla\"   , \"hello\":  +42 , \"none\": null , \"submap\": {} } ";
        System.out.println("012345678901234567890123456789012345678901234567890123456789");
        System.out.println(in);
        Assertions.assertEquals(createMap(), new JsonParser(in, false).parseObject());
    }

    @Test
    public void testOutputMap() throws Exception {
        StringBuilder sb = new StringBuilder();
        JsonEscaper out = new BaseJsonComposer(sb);
        out.outputJsonObject(createMap());
        // difficult to check the output as the ordering of the fields is not defined.
        // {"submap":{},"what":true,"why":"bla","hello":42,"none":null}
        Assertions.assertEquals(60, sb.length());       // the length should be constant
        System.out.println(sb.toString());

        sb.setLength(0);
        out = new BaseJsonComposer(sb, false, false);
        out.outputJsonObject(createMap());
        // difficult to check the output as the ordering of the fields is not defined.
        // {"submap":{},"what":true,"why":"bla","hello":42}
        Assertions.assertEquals(48, sb.length());       // the length should be constant
        System.out.println(sb.toString());
    }
}
