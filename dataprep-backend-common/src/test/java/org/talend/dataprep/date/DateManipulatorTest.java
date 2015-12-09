package org.talend.dataprep.date;

import org.junit.Test;

import java.time.LocalDate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.talend.dataprep.date.DateManipulator.Pace.*;

public class DateManipulatorTest {
    final DateManipulator dateManipulator = new DateManipulator();

    //------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------getSuitablePace------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------
    @Test
    public void getSuitablePace_should_return_computed_pace() throws Exception {
        testGetSuitablePaceWith(LocalDate.of(2015, 1, 1), LocalDate.of(2015, 1, 15), 20, DAY);
        testGetSuitablePaceWith(LocalDate.of(2015, 1, 1), LocalDate.of(2015, 2, 1),  20, TWO_DAYS);
        testGetSuitablePaceWith(LocalDate.of(2015, 1, 1), LocalDate.of(2015, 4, 15), 20, WEEK);
        testGetSuitablePaceWith(LocalDate.of(2015, 1, 1), LocalDate.of(2015, 5, 15), 20, TWO_WEEK);
        testGetSuitablePaceWith(LocalDate.of(2015, 1, 1), LocalDate.of(2016, 3, 15), 20, MONTH);
        testGetSuitablePaceWith(LocalDate.of(2015, 1, 1), LocalDate.of(2017, 1, 15), 20, TWO_MONTH);
        testGetSuitablePaceWith(LocalDate.of(2015, 1, 1), LocalDate.of(2019, 1, 15), 20, QUARTER);
        testGetSuitablePaceWith(LocalDate.of(2015, 1, 1), LocalDate.of(2022, 1, 15), 20, HALF_YEAR);
        testGetSuitablePaceWith(LocalDate.of(2015, 1, 1), LocalDate.of(2032, 1, 15), 20, YEAR);
        testGetSuitablePaceWith(LocalDate.of(2015, 1, 1), LocalDate.of(2200, 1, 15), 20, DECADE);
        testGetSuitablePaceWith(LocalDate.of(2015, 1, 1), LocalDate.of(4015, 1, 15), 20, CENTURY);
    }

    private void testGetSuitablePaceWith(final LocalDate min, final LocalDate max, final int maxBins, final DateManipulator.Pace expectedPace) throws Exception {
        //when
        final DateManipulator.Pace pace = dateManipulator.getSuitablePace(min, max, maxBins);

        //then
        assertThat(pace, is(expectedPace));
    }

    //------------------------------------------------------------------------------------------------------------------
    //------------------------------------------------getSuitableStartingDate-------------------------------------------
    //------------------------------------------------------------------------------------------------------------------
    @Test
    public void getSuitableStartingDate_should_return_computed_starting_range_date() throws Exception {
        testGetSuitableStartingDateWith(LocalDate.of(2015, 1, 2), DAY,          LocalDate.of(2015, 1, 2));
        testGetSuitableStartingDateWith(LocalDate.of(2015, 1, 2), TWO_DAYS,     LocalDate.of(2015, 1, 1));
        testGetSuitableStartingDateWith(LocalDate.of(2015, 1, 2), WEEK,         LocalDate.of(2014, 12, 29));
        testGetSuitableStartingDateWith(LocalDate.of(2015, 1, 10), TWO_WEEK,    LocalDate.of(2014, 12, 29));
        testGetSuitableStartingDateWith(LocalDate.of(2015, 1, 2), MONTH,        LocalDate.of(2015, 1, 1));
        testGetSuitableStartingDateWith(LocalDate.of(2015, 2, 2), TWO_MONTH,    LocalDate.of(2015, 1, 1));
        testGetSuitableStartingDateWith(LocalDate.of(2015, 3, 2), QUARTER,      LocalDate.of(2015, 1, 1));
        testGetSuitableStartingDateWith(LocalDate.of(2015, 8, 2), HALF_YEAR,    LocalDate.of(2015, 7, 1));
        testGetSuitableStartingDateWith(LocalDate.of(2015, 9, 2), YEAR,         LocalDate.of(2015, 1, 1));
        testGetSuitableStartingDateWith(LocalDate.of(2015, 1, 2), DECADE,       LocalDate.of(2010, 1, 1));
        testGetSuitableStartingDateWith(LocalDate.of(2015, 1, 2), CENTURY,      LocalDate.of(2000, 1, 1));
    }

    public void testGetSuitableStartingDateWith(final LocalDate date, final DateManipulator.Pace pace, final LocalDate expectedDate) throws Exception {
        //when
        final LocalDate computedDate = dateManipulator.getSuitableStartingDate(date, pace);

        //then
        assertThat(computedDate, is(expectedDate));
    }

    //------------------------------------------------------------------------------------------------------------------
    //--------------------------------------------------------getNext---------------------------------------------------
    //------------------------------------------------------------------------------------------------------------------
    @Test
    public void testGetNext_should_return_date_with_added_pace() throws Exception {
        testGetNextWith(LocalDate.of(2015, 1, 1), DAY,          LocalDate.of(2015, 1, 2));
        testGetNextWith(LocalDate.of(2015, 1, 1), TWO_DAYS,     LocalDate.of(2015, 1, 3));
        testGetNextWith(LocalDate.of(2015, 1, 1), WEEK,         LocalDate.of(2015, 1, 8));
        testGetNextWith(LocalDate.of(2015, 1, 1), TWO_WEEK,     LocalDate.of(2015, 1, 15));
        testGetNextWith(LocalDate.of(2015, 1, 1), MONTH,        LocalDate.of(2015, 2, 1));
        testGetNextWith(LocalDate.of(2015, 1, 1), TWO_MONTH,    LocalDate.of(2015, 3, 1));
        testGetNextWith(LocalDate.of(2015, 1, 1), QUARTER,      LocalDate.of(2015, 4, 1));
        testGetNextWith(LocalDate.of(2015, 1, 1), HALF_YEAR,    LocalDate.of(2015, 7, 1));
        testGetNextWith(LocalDate.of(2015, 1, 1), YEAR,         LocalDate.of(2016, 1, 1));
        testGetNextWith(LocalDate.of(2015, 1, 1), DECADE,       LocalDate.of(2025, 1, 1));
        testGetNextWith(LocalDate.of(2015, 1, 1), CENTURY,      LocalDate.of(2115, 1, 1));
    }

    public void testGetNextWith(final LocalDate date, final DateManipulator.Pace pace, final LocalDate expectedDate) throws Exception {
        //when
        final LocalDate computedDate = dateManipulator.getNext(date, pace);

        //then
        assertThat(computedDate, is(expectedDate));
    }
}