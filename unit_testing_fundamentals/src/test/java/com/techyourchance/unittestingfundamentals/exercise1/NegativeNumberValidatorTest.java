package com.techyourchance.unittestingfundamentals.exercise1;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;

public class NegativeNumberValidatorTest {
    private NegativeNumberValidator SUT;

    @Before
    public void setup() {
        SUT = new NegativeNumberValidator();
    }

    @Test
    public void testThatNegativeValueReturnsTrue() {
        boolean result = SUT.isNegative(-1);
        Assert.assertThat(result, is(true));
    }

    @Test
    public void testThatPositiveValueReturnsFalse() {
        boolean result = SUT.isNegative(1);
        Assert.assertThat(result, is(false));
    }

    @Test
    public void testThatZeroValueReturnsTrue() {
        boolean result = SUT.isNegative(0);
        Assert.assertThat(result, is(false));
    }
}