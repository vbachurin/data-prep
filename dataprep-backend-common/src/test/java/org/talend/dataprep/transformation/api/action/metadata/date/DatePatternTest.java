package org.talend.dataprep.transformation.api.action.metadata.date;

import static java.time.Month.JANUARY;
import static java.time.Month.JULY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.time.LocalDateTime;

import org.junit.Test;

public class DatePatternTest {
    @Test
    public void datePattern_should_create_DateFormatter() {
        //given
        final String pattern = "d/MM/yyyy";
        final DatePattern dp = new DatePattern(pattern);

        final LocalDateTime date = LocalDateTime.of(2016, JANUARY, 25, 0, 0);

        //when
        final String formattedDate = date.format(dp.getFormatter());

        //then
        assertThat(formattedDate, is("25/01/2016"));
    }

    @Test
    public void datePattern_should_set_DateFormatter_local_to_ENGLISH() {
        //given
        final String pattern = "d-MMM-yyyy";
        final DatePattern dp = new DatePattern(pattern);

        final LocalDateTime date = LocalDateTime.of(2016, JULY, 25, 0, 0);

        //when
        final String formattedDate = date.format(dp.getFormatter());

        //then
        assertThat(formattedDate, is("25-Jul-2016"));
    }
}