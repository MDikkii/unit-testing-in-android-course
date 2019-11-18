package com.techyourchance.unittestingfundamentals.exercise2;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;

public class StringDuplicatorTest {

    private StringDuplicator SUT;

    @Before
    public void setUp() throws Exception {
        SUT = new StringDuplicator();
    }

    @Test
    public void duplication_emptyString_emptyStringReturned() {
        String result = SUT.duplicate("");
        Assert.assertThat(result, is(""));
    }

    @Test
    public void duplication_oneCharString_twoCharStringReturned() {
        String result = SUT.duplicate("a");
        Assert.assertThat(result, is("aa"));
    }

    @Test
    public void duplication_manyCharString_duplicateCharStringReturned() {
        String result = SUT.duplicate("abcd efg");
        Assert.assertThat(result, is("abcd efgabcd efg"));
    }
}