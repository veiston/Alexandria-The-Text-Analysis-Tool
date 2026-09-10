package com.alexandria.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JsonMapperTest {

    private static class TestObject {

        private String name;
        private int value;

        public TestObject() {
        }

        public TestObject(String name, int value) {
            this.name = name;
            this.value = value;
        }
    }

    @Test
    public void toJsonSerializesPrivateFields() {

        TestObject object = new TestObject("Alexandria", 42);

        String json = JsonMapper.toJson(object);

        assertTrue(json.contains("\"name\":\"Alexandria\""));
        assertTrue(json.contains("\"value\":42"));
    }

    @Test
    public void fromJsonDeserializesPrivateFields() {

        String json = "{\"name\":\"Alexandria\",\"value\":42}";

        TestObject object = JsonMapper.fromJson(json, TestObject.class);

        assertEquals("Alexandria", object.name);
        assertEquals(42, object.value);
    }

    @Test
    public void toJsonAndFromJsonPreserveValues() {

        TestObject original = new TestObject("Test", 123);

        String json = JsonMapper.toJson(original);

        TestObject restored = JsonMapper.fromJson(json, TestObject.class);

        assertEquals(original.name, restored.name);
        assertEquals(original.value, restored.value);
    }

    @Test(expected = RuntimeException.class)
    public void fromJsonThrowsRuntimeExceptionForInvalidJson() {

        JsonMapper.fromJson(
                "{invalid json}",
                TestObject.class);
    }

    @Test
    public void toJsonSerializesNull() {

        String json = JsonMapper.toJson(null);

        assertEquals("null", json);
    }

}