package nz.ac.canterbury.seng302.gardenersgrove.unittests.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Tag;

class TagTest {
    private Tag tag1;
    private Tag tag1Copy;
    private Tag tag1AllCaps;
    private Tag tag2;

    @BeforeEach
    void setUp() {
        tag1 = new Tag("tag1");
        tag1Copy = new Tag("tag1");
        tag1AllCaps = new Tag("TAG1");
        tag2 = new Tag("tag2");
    }

    @Test
    void whenDefaultConstructorCalled_thenNameIsNull() {
        Tag tag = new Tag();
        assertNull(tag.getName());
    }

    @Test
    void whenComparingIdentical_thenReturnsEqual() {
        assertEquals(0, tag1.compareTo(tag1));
        assertEquals(tag1, tag1);
    }

    @Test
    void whenComparingEqual_thenReturnsEqual() {
        assertEquals(0, tag1.compareTo(tag1Copy));
        assertEquals(tag1,tag1Copy);
    }

    @Test
    void whenComparingDifferentCase_thenSortsUppercaseFirst() {
        assertTrue(tag1.compareTo(tag1AllCaps) > 0);
        assertTrue(tag1AllCaps.compareTo(tag1) < 0);
        assertNotEquals(tag1,tag1AllCaps);
    }

    @Test
    void whenComparingDifferent_thenSortsInDictionaryOrder() {
        assertTrue(tag1.compareTo(tag2) < 0);
        assertTrue(tag2.compareTo(tag1) > 0);
        assertNotEquals(tag1,tag2);
        assertNotEquals(tag2,tag1);
    }

    @Test
    void whenComparingToNull_thenReturnsFalse() {
        // Explicitly call the equals method as its the method we want to test
        assertFalse(tag1.equals(null));
    }

    @Test
    void whenComparingToAnotherClass_thenReturnsFalse() {
        assertNotEquals(tag1, "tag1");
    }

    @Test
    void whenHashCodeCalled_thenReturnsNameHashCode() {
        assertEquals("tag1".hashCode(), tag1.hashCode());
    }
}
