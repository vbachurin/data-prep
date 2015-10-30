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

import org.apache.commons.math3.special.Gamma;
import org.talend.dataquality.statistics.numeric.histogram.Range;

/**
 * This class implements the Dynamic Compressed (DC) Histogram as described in the paper:
 * Dynamic Compressed Histograms: Capturing Evolving Data Sets of Donko et.al.
 * We do not consider singular buckets in our implementation.
 *
 * @see <a href="http://pages.cs.wisc.edu/~donjerko/hist.pdf>Donko et.al</a>
 *
 */
public class DCHistogramStatistics {

    /**
     * Internal representation of the equi-depth histogram
     */
    private TreeMap<Double, Long> bins = new TreeMap<>();

    /**
     * Minimum value know to this histogram
     */
    private double min = Double.NaN;

    /**
     * Maximum value known to this histogram
     */
    private double max = Double.NaN;

    /**
     * The number of bins of this histogram
     */
    private int numberOfBins;

    /**
     * the number of values added, so far, to this histogram
     */
    private long numberOfValues;

    /**
     * The arithmetic mean of values contained in the histogram
     */
    private double sum = Double.NaN;

    /**
     * A parameter used value used in the original algorithm
     */
    private static final double ALPHA_MIN = 10E-6;

    /**
     *
     */
    private int tresholdQuotient = 100;

    /**
     *
     * @param numberOfBins the number of bins of this histogram
     */
    public void setParameters( int numberOfBins) {
        if (numberOfBins <= 0){
            throw new IllegalArgumentException("The number of bin must be a positive integer");
        }
        this.numberOfBins = numberOfBins;
    }

    /**
     * Add the specified value to this histogram
     * @param d the new value to add to this histogram
     */
    public void add(double d) {
        // So far, we have not met n different values
        if (bins.size() < numberOfBins){
            Long count = bins.get(d);
            if (count == null){
                bins.put(d, 1L);
            } else{
                bins.put(d, count + 1);
            }
            // update minimum and maximum values
            if (Double.isNaN(min) || d < min){
                min = d;
            }
            if (Double.isNaN(max) || max < d){
                max = d;
            }
            // increment the number of values stored in this histogram
            numberOfValues++;
            sum = Double.isNaN(sum)?d: sum +d;
        }
        // We have already met n different values
        else{
            // d is the min
            if (Double.isNaN(min) || d <= min) {
                Long count = bins.get(min);
                bins.remove(min);
                bins.put(d, count + 1);
                min  = d;
            }
            // d is the max
            else if (Double.isNaN(max) || max <= d){
                Double lastBin = bins.lastKey();
                Long count = bins.get(lastBin);
                bins.put(lastBin, count + 1);
                max = d;
            } // d is not min nor max
            else{
                Double bin = bins.lowerKey(d);

                Long binCount = bins.get(bin);
                bins.put(bin, binCount + 1);
            }
            // increment the number of values stored in this histogram
            numberOfValues++;
            sum = Double.isNaN(sum)?d: sum +d;
            // redistribute values whether it is needed
            redistributeBucketsIfNeeded();
        }
    }

    /**
     * Redistribute the bins if needed, i.e., if the criterion defined in the paper of Donko et al.
     * "Dynamic Histograms: Capturing Evolving Data Sets" and in "Numerical Recipe in C (page 645)"
     *
     */
    private void redistributeBucketsIfNeeded(){
        double c = numberOfValues / numberOfBins;
        double chiSquare = 0D;
        for (double C: bins.values()){
            chiSquare += Math.pow(c - C, 2)/c;
        }
        // degrees of freedom
        double degreesOfFreedom = numberOfBins - 1;
        double probability = Gamma.regularizedGammaQ(0.5 * degreesOfFreedom, 0.5 * chiSquare);
        if (probability < ALPHA_MIN){
            redistributeDC();
        }
    }

    /**
     * Redistribute bins when the relaxation DC histogram  becomes too much ...
     */
    private void redistributeDC(){
        // Nothing to do if the histogram is empty
        if (bins.isEmpty()){
            return ;
        }
        final Long averageCount = numberOfValues / numberOfBins;
        long newBinCount = 0;
        double newBinStart = min;
        TreeMap<Double, Long> newBins = new TreeMap<>();
        Double currentBin = null;
        long currentBinCount = 0L;

        for(Map.Entry<Double, Long> entry :bins.entrySet()){
            Double nextBin = entry.getKey();
            Long nextBinCount = entry.getValue();
            if ( currentBin != null) {
                if (averageCount <= newBinCount + currentBinCount) {
                    newBins.put(newBinStart,  averageCount);
                    newBinStart = currentBin + (nextBin - currentBin) * (averageCount - newBinCount) / currentBinCount;
                    newBinCount = currentBinCount + newBinCount - averageCount;
                }
                else{
                    newBinCount += currentBinCount;
                }
            }
            currentBin = nextBin;
            currentBinCount = nextBinCount;
        }
        // if the last bin contains a single value
       if (max == currentBin){
            newBins.put(newBinStart, currentBinCount + newBinCount);
        }
        else {
           // The last bin has not been treated yet
           while (newBins.size() < bins.size()) {
               newBins.put(newBinStart, averageCount);
               newBinStart = newBinStart + (max - currentBin) * averageCount / currentBinCount;
           }

           // assign the good count to the last bin of the new distribution
           long newLastBinCount = numberOfValues - (numberOfBins - 1) * averageCount;
           Double newLastBin = newBins.lastKey();
           newBins.put(newLastBin, newLastBinCount);
       }
        bins = newBins;
    }


    /**
     * Get histograms as a map
     *
     * @return the histogram map where Key is the range and value is the freqency. <br>
     * Note that the returned ranges are in pattern of [Min,
     * Min+binSize),[Min+binSize,Min+binSize*2)...[Max-binSize,Max<b>]</b>
     */
    public Map<Range, Long> getHistogram() {

        TreeMap<Double, Long> bins = this.bins;
        /*if ( numberOfValues > numberOfBins && !allmostEquiWidthHistogram()) {
            bins = redistributeEquiWidth();
        }*/

        Map<Range, Long> histogramMap = new LinkedHashMap<>();
        double currentMin = Double.NaN;
        long count = 0L;
        for (Double d: bins.keySet()) {
            if (Double.isNaN(currentMin)){
                currentMin = d;
                count = (long) ((double)bins.get(d));
            }
            else{
                double currentMax = d;
                Range r = new Range(currentMin, currentMax);
                histogramMap.put(r, count);
                //histogramMap.put(r, 10L);
                currentMin = currentMax;
                count = (long) ((double)bins.get(d));
            }

        }
        Range r = new Range(currentMin, max);
        //Range r = new Range(currentMin, 10L);
        histogramMap.put(r, count);
        return histogramMap;
    }

    public double getMin() {
        return Double.isNaN(min)? 0: min;
    }

    public double getMax() {
        return Double.isNaN(max)? 0: max;
    }

    public double getMean() {
        return Double.isNaN(sum)? 0: sum/numberOfValues;
    }

    public int getNumberOfBins() {
        return numberOfBins;
    }

    public long getNumberOfValues() {
        return numberOfValues;
    }
}