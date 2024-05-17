package nz.ac.canterbury.seng302.gardenersgrove.unittests.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nz.ac.canterbury.seng302.gardenersgrove.entity.Tag;

public class TagTest {
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
        assertEquals(null, tag.getName());
    }

    @Test
    void whenComparingIdentical_thenReturnsEqual() {
        assertEquals(0, tag1.compareTo(tag1));
        assertTrue(tag1.equals(tag1));
    }

    @Test
    void whenComparingEqual_thenReturnsEqual() {
        assertEquals(0, tag1.compareTo(tag1Copy));
        assertTrue(tag1.equals(tag1Copy));
    }

    @Test
    void whenComparingDifferentCase_thenSortsUppercaseFirst() {
        assertTrue(tag1.compareTo(tag1AllCaps) > 0);
        assertTrue(tag1AllCaps.compareTo(tag1) < 0);
        assertFalse(tag1.equals(tag1AllCaps));
    }

    @Test
    void whenComparingDifferent_thenSortsInDictionaryOrder() {
        assertTrue(tag1.compareTo(tag2) < 0);
        assertTrue(tag2.compareTo(tag1) > 0);
        assertFalse(tag1.equals(tag2));
        assertFalse(tag2.equals(tag1));
    }
}
