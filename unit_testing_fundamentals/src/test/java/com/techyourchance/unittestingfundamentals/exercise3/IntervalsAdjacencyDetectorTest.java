package com.techyourchance.unittestingfundamentals.exercise3;

import com.techyourchance.unittestingfundamentals.example3.Interval;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;

public class IntervalsAdjacencyDetectorTest {

    private IntervalsAdjacencyDetector SUT;

    @Before
    public void setUp() throws Exception {
        SUT = new IntervalsAdjacencyDetector();
    }

    // interval 1 before and not adjacency interval2\

    @Test
    public void adjacencyDetection_interval1BeforeInterval2_falseReturned() {
        Interval interval1 = new Interval(-1, 2);
        Interval interval2 = new Interval(3, 4);

        boolean result = SUT.isAdjacent(interval1, interval2);

        Assert.assertThat(result, is(false));
    }


    // interval 1 before and adjacency interval2

    @Test
    public void adjacencyDetection_interval1BeforeAdjacentInterval2_trueReturned() {
        Interval interval1 = new Interval(-1, 2);
        Interval interval2 = new Interval(2, 4);

        boolean result = SUT.isAdjacent(interval1, interval2);

        Assert.assertThat(result, is(true));
    }

    // interval 1 after and not adjacency interval2

    @Test
    public void adjacencyDetection_interval1AfterInterval2_falseReturned() {
        Interval interval1 = new Interval(6, 8);
        Interval interval2 = new Interval(3, 4);

        boolean result = SUT.isAdjacent(interval1, interval2);

        Assert.assertThat(result, is(false));
    }

    // interval 1 after and adjacency interval2

    @Test
    public void adjacencyDetection_interval1AfterAdjacentInterval2_trueReturned() {
        Interval interval1 = new Interval(4, 6);
        Interval interval2 = new Interval(2, 4);

        boolean result = SUT.isAdjacent(interval1, interval2);

        Assert.assertThat(result, is(true));
    }

    // interval 1 overlap start of interval2
    @Test
    public void adjacencyDetection_interval1OverlapStartInterval2_falseReturned() {
        Interval interval1 = new Interval(4, 6);
        Interval interval2 = new Interval(5, 7);

        boolean result = SUT.isAdjacent(interval1, interval2);

        Assert.assertThat(result, is(false));
    }

    // interval 1 overlap end of interval2
    @Test
    public void adjacencyDetection_interval1OverlapEndInterval2_falseReturned() {
        Interval interval1 = new Interval(4, 6);
        Interval interval2 = new Interval(1, 5);

        boolean result = SUT.isAdjacent(interval1, interval2);

        Assert.assertThat(result, is(false));
    }

    // interval 1 contains interval2
    @Test
    public void adjacencyDetection_interval1ContainsInterval2_falseReturned() {
        Interval interval1 = new Interval(3, 6);
        Interval interval2 = new Interval(4, 5);

        boolean result = SUT.isAdjacent(interval1, interval2);

        Assert.assertThat(result, is(false));
    }

    // interval 1 is contained in interval2
    @Test
    public void adjacencyDetection_interval1IsContainedInterval2_falseReturned() {
        Interval interval1 = new Interval(4, 6);
        Interval interval2 = new Interval(2, 8);

        boolean result = SUT.isAdjacent(interval1, interval2);

        Assert.assertThat(result, is(false));
    }

    // interval 1 is contained in interval2
    @Test
    public void adjacencyDetection_interval1IsTheSameInterval2_falseReturned() {
        Interval interval1 = new Interval(2, 8);
        Interval interval2 = new Interval(2, 8);

        boolean result = SUT.isAdjacent(interval1, interval2);

        Assert.assertThat(result, is(false));
    }


}