// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprep.api.dataset.statistics;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import org.talend.dataquality.statistics.numeric.histogram.Range;

/**
 * This class implements implements a new kind of histogram suitable for evolving data sets like streams.
 *
 */
public class StreamHistogramStatistics {

    /**
     * Internal representation of the histogram.
     */
    private TreeMap<Double, Long> bins = new TreeMap<>();

    /**
     * The minimum value know to this histogram.
     */
    private double min = Double.NaN;

    /**
     * The maximum value known to this histogram.
     */
    private double max = Double.NaN;

    /**
     * The lower bound (start point of the minimum bin). It may be different from the minimum value.
     */
    private double lowerBound = Double.NaN;

    /**
     * The currently bin size which may change depending on how the values are inserted into this histogram.
     */
    private double binSize = Double.NaN;

    /**
     * The default bin number which is 32.
     *
     */
    private static final int DEFAULT_BIN_NUMBER = 32;

    /**
     * The number of bins of this histogram which must be power of 2.
     */
    public int numberOfBins = DEFAULT_BIN_NUMBER;

    /**
     * The number of values added, so far, to this histogram.
     */
    private long numberOfValues;

    /**
     * A flag to know whether or not all bins of this histogram are singular. A bin is singular if it contains one or
     * many occurrences of a unique value.
     *
     */
    private boolean allBinsAreSingular = true;

    /**
     * The sum of values contained in the histogram.
     */
    private double sum = Double.NaN;

    /**
     *
     * @param numberOfBins the number of bins of this histogram.
     */
    public void setParameters(int numberOfBins) {
        if (numberOfBins <= 0) {
            throw new IllegalArgumentException("The number of bin must be a positive integer and a power of ");
        }
        if ((numberOfBins & (numberOfBins - 1)) != 0) {
            throw new IllegalArgumentException("The number of bin which is " + numberOfBins + " must be a power of 2");
        }
        this.numberOfBins = numberOfBins;
        // this.numberOfBins = 32;
    }

    /**
     * Add the specified value to this histogram.
     * 
     * @param d the new value to add to this histogram
     */
    public void add(double d) {
        // So far, we have not met n different values
        if (bins.size() < numberOfBins || allBinsAreSingular && bins.containsKey(d)) {
            Long count = bins.get(d);
            if (count == null) {
                bins.put(d, 1L);
            } else {
                bins.put(d, count + 1);
            }
            // update minimum and maximum values
            if (Double.isNaN(min) || d < min) {
                min = d;
                lowerBound = min;
            }
            if (Double.isNaN(max) || max < d) {
                max = d;
            }
            // increment the number of values stored in this histogram
            numberOfValues++;
            sum = Double.isNaN(sum) ? d : sum + d;
        }
        // We have already met n different values
        else {
            // We now have in the histogram more than numberOfBins different values and
            // we have to transform it to an histogram of equal size
            if (allBinsAreSingular) {
                transformValuesToBin();
                allBinsAreSingular = false;
            }
            if (d < lowerBound) {
                extendToLeft(d);
            } else // if d is higher than the upper bound
                if (lowerBound + (numberOfBins * binSize) < d) {
                extendToRight(d);
            }
            // d is the min
            if (d <= min) {
                min = d;
            }
            // d is the max
            if (max <= d) {
                max = d;
            }
            Double bin = bins.floorKey(d);
            Long binCount = bins.get(bin);
            bins.put(bin, binCount + 1);
            // increment the number of values stored in this histogram
            numberOfValues++;
            sum += d;
        }
    }

    /**
     * This method transform values to bin. Prior to calling this method, all the bins are singular and after its call
     * all the singular bins become regular (each bin may contains many values).
     */
    private void transformValuesToBin() {
        binSize = (max - min) * 2 / numberOfBins;
        lowerBound = min;
        TreeMap<Double, Long> newBins = new TreeMap<>();
        for (double bin = min; bin < min + binSize * numberOfBins; bin += binSize) {
            newBins.put(bin, 0L);
        }

        for (Map.Entry<Double, Long> entry : bins.entrySet()) {
            Map.Entry<Double, Long> bin = newBins.floorEntry(entry.getKey());
            newBins.put(bin.getKey(), bin.getValue() + entry.getValue());
        }
        bins = newBins;
    }

    /**
     * This method first merges previously existing bins up to the factor determined and set the new bin size to a
     * multiple of the previous one. It then set the new lower bound and create new bins to go contiguously from the
     * lower bound to the new upper bound (the upper bound of the last bin).
     * 
     * @param d the new value to add to this histogram
     */
    private void extendToLeft(double d) {
        double histogramWidth = numberOfBins * binSize;
        int factor = 2;
        while (d < lowerBound - histogramWidth * (factor >>> 1)) {
            factor <<= 1;
        }
        double newBinSize = binSize * factor;
        // merge previously existing bins
        mergeBins(newBinSize);
        lowerBound = this.lowerBound - histogramWidth * (factor >>> 1);
        // create new bins
        createNewBins();
    }

    /**
     * This method first merges previously existing bins up to the factor determined and set the new bin size to a
     * multiple of the previous one. It then creates new bins to go contiguously from the lower bound to the new upper
     * bound (the upper bound of the last bin).
     * 
     * @param d the new value to add to this histogram
     */
    private void extendToRight(double d) {
        double histogramWidth = numberOfBins * binSize;
        int factor = 2;
        while (lowerBound + histogramWidth * (factor) < d) {
            factor <<= 1;
        }
        double newBinSize = binSize * factor;
        // merge previously existing bins
        mergeBins(newBinSize);
        // create new bins
        createNewBins();
    }

    /**
     * Merge previously existing bins to have new bins of width newBinSize. After this method is called the histogram
     * now have bins of width newBinSize.
     * 
     * @param newBinSize the new binSize of this histogram.
     */
    private void mergeBins(double newBinSize) {
        double currentBin = lowerBound;
        double upperBound = lowerBound + numberOfBins * binSize;
        // merge previous bins to form new bins of newBinSize width
        while (currentBin < upperBound) {
            long count = 0L;
            for (double bin = currentBin; bin < currentBin + newBinSize && bin < upperBound; bin += binSize) {
                count += bins.remove(bin);
            }
            bins.put(currentBin, count);
            currentBin += newBinSize;
        }
        binSize = newBinSize;
    }

    /**
     * Creates new bins of size binSize starting from the lower bound of this histogram. New bins are only created when
     * need.
     */
    private void createNewBins() {
        double currentBin = lowerBound;
        double upperBound = lowerBound + numberOfBins * binSize;
        // set freshly created bins
        while (currentBin < upperBound) {
            if (!bins.containsKey(currentBin)) {
                bins.put(currentBin, 0L);
            }
            currentBin = currentBin + binSize;
        }
    }

    /**
     * Get histograms as a map.
     *
     * @return the histogram map where Key is the range and value is the frequency. <br>
     * Note that the returned ranges are in pattern of [Min, Min+binSize),[Min+binSize,Min+binSize*2)...[Max-binSize,Max
     * <b>]</b>
     */
    public Map<Range, Long> getHistogram() {

        if (allBinsAreSingular) {
            return getSingularHisogram();
        }

        TreeMap<Double, Long> bins = (TreeMap<Double, Long>) this.bins.clone();

        removeLeadingAndTrailingEmptyBins(bins);

        Map<Range, Long> result = new LinkedHashMap<>();
        for (Double d : bins.keySet()) {
            Range r = new Range(d, d + binSize);
            result.put(r, bins.get(d));
        }

        return result;
    }

    private Map<Range, Long> getSingularHisogram() {
        Map<Range, Long> result = new LinkedHashMap<>();
        for (Double d : bins.keySet()) {
            Range r = new Range(d, d);
            result.put(r, bins.get(d));
        }
        return result;
    }

    /**
     * This method removes the lower bins that are empty, i.e., that no value is within the range of the bin, up to the
     * non empty one. It also removes higher bins that are empty up to (backward) a non empty one.
     * 
     * @param bins
     */
    private void removeLeadingAndTrailingEmptyBins(TreeMap<Double, Long> bins) {
        Double currentBin = bins.firstKey();
        while (currentBin != null && bins.get(currentBin) == 0L) {
            bins.remove(currentBin);
            currentBin = bins.firstKey();
        }
        currentBin = bins.lastKey();

        while (currentBin != null && bins.get(currentBin) == 0L) {
            bins.remove(currentBin);
            currentBin = bins.lastKey();
        }
    }

    public double getMin() {
        return Double.isNaN(min) ? 0 : min;
    }

    public double getMax() {
        return Double.isNaN(max) ? 0 : max;
    }

    public double getMean() {
        return Double.isNaN(sum) ? 0 : sum / numberOfValues;
    }

    public int getNumberOfBins() {
        return numberOfBins;
    }

    public long getNumberOfValues() {
        return numberOfValues;
    }
}