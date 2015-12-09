package org.talend.dataprep.date;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Arrays;
import java.util.Locale;

import static java.time.Month.*;
import static java.time.temporal.IsoFields.QUARTER_OF_YEAR;
import static org.talend.dataprep.date.DateManipulator.Pace.CENTURY;

public class DateManipulator {
    /**
     * Supported pace for date ranges
     */
    public enum Pace {
        DAY(86400000L), TWO_DAYS(172800000L), WEEK(604800000L), TWO_WEEK(1209600000L), MONTH(2678400000L), TWO_MONTH(5356800000L), QUARTER(7884000000L), HALF_YEAR(15768000000L), YEAR(31536000000L), DECADE(315360000000L), CENTURY(3153600000000L);

        /**
         * Number of milliseconds that represents the pace
         */
        private final long time;

        /**
         * Constructor
         *
         * @param time The number of milliseconds in the pace
         */
        Pace(final long time) {
            this.time = time;
        }

        /**
         * Number of milliseconds getter
         *
         * @return The number of milliseconds in the pace
         */
        public long getTime() {
            return time;
        }
    }

    /**
     * It determine the right pace to use. It depends on the range limit and the maximum number of parts that is allowed
     *
     * @param min              The minimum date limit
     * @param max              The maximum date limit
     * @param maxNumberOfParts The maximum number of parts
     * @return The suitable pace to use in the range
     */
    public DateManipulator.Pace getSuitablePace(final LocalDate min, final LocalDate max, final int maxNumberOfParts) {
        final long allRangeTimetamp = Duration.between(min.atStartOfDay(), max.atStartOfDay()).toMillis();
        return Arrays.stream(Pace.values())
                .filter(pace -> (allRangeTimetamp / pace.getTime()) < (maxNumberOfParts - 1))
                .findFirst()
                .orElse(CENTURY);
    }

    /**
     * Compute the starting date of the range where the given date is included for a given pace.
     * <ul>
     * <li>CENTURY : first day of the date century. Ex : 25/11/2011 --> 01/01/2000</li>
     * <li>DECADE : first day of the date decade. Ex: 25/11/2011 --> 01/01/2010</li>
     * <li>YEAR : first day of the date year. Ex: 25/11/2011 --> 01/01/2011</li>
     * <li>HALF_YEAR : first day of the date half year (01/01 or 01/07). Ex: 25/11/2011 --> 01/07/2011</li>
     * <li>QUARTER : first day of the date quarter. Ex: 25/11/2011 --> 01/10/2011</li>
     * <li>TWO_MONTH : first day of the date odd month (month value is odd). Ex: 25/04/2011 --> 01/03/2011</li>
     * <li>MONTH : first day of the date month. Ex: 25/11/2011 --> 01/11/2011</li>
     * <li>TWO_WEEK : first day of the date odd week (week of year number is odd).</li>
     * <li>WEEK : first day of the date week.</li>
     * <li>TWO_DAYS : odd day (day of year number is odd). Ex: 02/01/2011 --> 01/01/2011</li>
     * <li>DAY : the given day</li>
     * </ul>
     *
     * @param date The date that must be included in the rance
     * @param pace The pace representing the range
     * @return The starting range date
     */
    public LocalDate getSuitableStartingDate(final LocalDate date, final DateManipulator.Pace pace) {
        switch (pace) {
            case CENTURY:
                return getFirstDayOfCentury(date);
            case DECADE:
                return getFirstDayOfDecade(date);
            case YEAR:
                return date.with(TemporalAdjusters.firstDayOfYear());
            case HALF_YEAR:
                return getFirstDayOfHalfYear(date);
            case QUARTER:
                return getFirstDayOfQuarter(date);
            case TWO_MONTH:
                return getFirstDayOfOddMonth(date);
            case MONTH:
                return date.with(TemporalAdjusters.firstDayOfMonth());
            case TWO_WEEK:
                return getFirstDayOfOddWeek(date);
            case WEEK:
                return date.with(DayOfWeek.MONDAY);
            case TWO_DAYS:
                return getOddDay(date);
        }
        return date;
    }

    /**
     * Compute the day after the given pace.
     *
     * @param localDate The starting date
     * @param pace      The pace to add
     * @return The day = localDate + pace
     */
    public LocalDate getNext(final LocalDate localDate, final Pace pace) {
        switch (pace) {
            case CENTURY:
                return localDate.plus(100, ChronoUnit.YEARS);
            case DECADE:
                return localDate.plus(10, ChronoUnit.YEARS);
            case YEAR:
                return localDate.plus(1, ChronoUnit.YEARS);
            case HALF_YEAR:
                return localDate.plus(6, ChronoUnit.MONTHS);
            case QUARTER:
                return localDate.plus(3, ChronoUnit.MONTHS);
            case TWO_MONTH:
                return localDate.plus(2, ChronoUnit.MONTHS);
            case MONTH:
                return localDate.plus(1, ChronoUnit.MONTHS);
            case TWO_WEEK:
                return localDate.plus(2, ChronoUnit.WEEKS);
            case WEEK:
                return localDate.plus(1, ChronoUnit.WEEKS);
            case TWO_DAYS:
                return localDate.plus(2, ChronoUnit.DAYS);
            case DAY:
                return localDate.plus(1, ChronoUnit.DAYS);
        }
        return localDate;
    }

    /**
     * Compute the first day of the date century
     *
     * @param localDate The reference date
     * @return The first day of the date century
     */
    private LocalDate getFirstDayOfCentury(final LocalDate localDate) {
        return localDate.with(temporal -> {
            final int year = localDate.getYear() / 100 * 100;
            return LocalDate.from(temporal)
                    .withYear(year)
                    .with(TemporalAdjusters.firstDayOfYear());
        });
    }

    /**
     * Compute the first day of the date decade
     *
     * @param localDate The reference date
     * @return The first day of the date decade
     */
    private LocalDate getFirstDayOfDecade(final LocalDate localDate) {
        return localDate.with(temporal -> {
            final int year = localDate.getYear() / 10 * 10;
            return LocalDate.from(temporal)
                    .withYear(year)
                    .with(TemporalAdjusters.firstDayOfYear());
        });
    }

    /**
     * Compute the first day of the date half year
     *
     * @param localDate The reference date
     * @return The first day of the date half year
     */
    private LocalDate getFirstDayOfHalfYear(final LocalDate localDate) {
        return localDate.with(temporal -> {
            final Month semesterMonth = localDate.getMonth().getValue() < JULY.getValue() ? JANUARY : JULY;
            return LocalDate.from(temporal)
                    .withMonth(semesterMonth.getValue())
                    .with(TemporalAdjusters.firstDayOfMonth());
        });
    }

    /**
     * Compute the first day of the date quarter
     *
     * @param localDate The reference date
     * @return The first day of the date quarter
     */
    private LocalDate getFirstDayOfQuarter(final LocalDate localDate) {
        return localDate.with(temporal -> {
            int currentQuarter = YearMonth.from(temporal).get(QUARTER_OF_YEAR);
            switch (currentQuarter) {
                case 1:
                    return LocalDate.from(temporal).with(TemporalAdjusters.firstDayOfYear());
                case 2:
                    return LocalDate.from(temporal)
                            .withMonth(APRIL.getValue())
                            .with(TemporalAdjusters.firstDayOfMonth());
                case 3:
                    return LocalDate.from(temporal)
                            .withMonth(JULY.getValue())
                            .with(TemporalAdjusters.firstDayOfMonth());
                default:
                    return LocalDate.from(temporal)
                            .withMonth(OCTOBER.getValue())
                            .with(TemporalAdjusters.firstDayOfMonth());
            }
        });
    }

    /**
     * Compute the first day of the first odd month that is equals or before the given date
     *
     * @param localDate The reference date
     * @return The first day of the odd month
     */
    private LocalDate getFirstDayOfOddMonth(final LocalDate localDate) {
        return localDate.with(temporal -> {
            int monthValue = localDate.getMonthValue();
            monthValue = monthValue % 2 != 0 ? monthValue : monthValue - 1;
            return LocalDate.from(temporal)
                    .withMonth(monthValue)
                    .with(TemporalAdjusters.firstDayOfMonth());
        });
    }

    /**
     * Compute the first day of the first odd week that is equals or before the given date
     *
     * @param localDate The reference date
     * @return The first day of the odd week
     */
    private LocalDate getFirstDayOfOddWeek(final LocalDate localDate) {
        return localDate.with(temporal -> {
            final int week = localDate.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
            final boolean isOddWeek = week % 2 != 0;
            return isOddWeek ?
                    localDate.with(DayOfWeek.MONDAY) :
                    LocalDate.from(temporal).minus(1, ChronoUnit.WEEKS).with(DayOfWeek.MONDAY);
        });
    }

    /**
     * Compute the first odd day that is equals or before the given date
     *
     * @param localDate The reference date
     * @return The odd day
     */
    private LocalDate getOddDay(final LocalDate localDate) {
        return localDate.with(temporal -> {
            final boolean isOddDay = localDate.getDayOfYear() % 2 != 0;
            return isOddDay ?
                    localDate :
                    LocalDate.from(temporal).minus(1, ChronoUnit.DAYS);
        });
    }
}
