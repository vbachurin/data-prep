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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

//import org.talend.dataquality.statistics.numeric.histogram.*;
//import org.talend.dataprep.api.dataset.statistics.Range;
import org.apache.commons.math3.special.Gamma;
import org.talend.dataquality.statistics.numeric.histogram.Range;

/**
 * Histogram statistics bean.
 *
 * @author bdiouf
 *
 */
public class DCHistogramStatistics {

    //private Map<SingleBin, Long> bins = new TreeMap();

    private TreeMap<Double, Double> bins = new TreeMap<>();

    private double min, max;

    private int numBins, numValues;

    private static final double ALPHA_MIN = 10E-6;

    public void setParameters( int numBins) {
        this.numBins = numBins;
    }

/*    public void add(double d) {

        double bin = ((d - min) / binSize);
        if (bin < 0) { *//* this data is smaller than min *//*
            // Omit
        } else if (bin > numBins) { *//* this data point is bigger than max *//*
            // omit
        } else {
            if (bin == numBins) {
                result[(int) bin - 1] += 1; // Include count of the upper boundary.
            } else {
                result[(int) bin] += 1;
            }
        }
    }*/

    /**
     * Adds the specified value to this histogram
     * @param d the new value to add to this histogram
     */
    public void add(double d) {

        System.out.println("adding for the  "+numValues +" times and the value added is: "+ d);

        // We do not have n different values so far
        if (bins.size() < numBins){
            Double count = bins.get(d);
            if (count == null){
                bins.put(d, 1D);
            } else{
                bins.put(d, count+1);
            }
            // update minimum and maximum values
            if (d < min){
                min = d;
            }
            if (max < d){
                max = d;
            }
            // increment the number of values in this histogram
            numValues++;
        }
        // We have n different values
        else{
            // if d becomes min
            if (d < min){
                Double count = bins.get(min);
                bins.remove (min);
                bins.put(d, count+1);
                min  = d;
            }
            // if d becomes max
            else if (max < d){
                Double lastBin = bins.lastKey();
                Double count = bins.get(lastBin);
                bins.put(lastBin, count+1);
                max = d;
            } // add d to its bucket
            else{
                Double bin = bins.lowerKey(d);
                double binCount = bins.get(bin);
                bins.put(bin, binCount+1);
            }
            // increment the number of values in this histogram
            numValues++;
            // redistribute values whether it is needed
            redistributeBucketsIfNeeded();
        }
    }

    /**
     * Redistribute the buckets if needed, i.e., if the criterion defined in the paper of Donko et al.
     * "Dynamic Histograms: Capturing Evolving Data Sets" and in "Numerical Recipe in C: P645"
     *
     */
    private void redistributeBucketsIfNeeded(){
        double c = numValues/ numBins;
        double chiSquare = 0D;
        for (double C:bins.values()){
            chiSquare += Math.pow(c - C, 2)/c;
        }
        // degrees of freedom
        double degreesOfFreedom = numBins - 1;
        double probability = Gamma.regularizedGammaQ(0.5 * degreesOfFreedom, 0.5 * chiSquare);
        if (probability < ALPHA_MIN){
            redistributeBuckets();
        }


    }

    /**
     * Redistribute buckets when the criterion holds ...
     */
    private void redistributeBuckets(){

        double averageCount = numValues / numBins;
        double currentCount = 0;
        double currentStart = min;
        TreeMap<Double, Double> newBins = new TreeMap<>();
        Iterator<Double> iterator = bins.keySet().iterator();
        while(iterator.hasNext()){
            Double bin = iterator.next();
            Double binCount = bins.get(bin);
            if ( averageCount < currentCount + binCount){
                // find new start
                Double nextBin = bins.lowerKey(bin);
                if (nextBin != null){
                    newBins.put(currentStart,  averageCount);
                    currentStart = (nextBin - bin) * (averageCount - currentCount) / binCount;
                    currentCount = binCount + currentCount - averageCount;
                }
                else{
                    // break out of this loop this is the last bin
                    break;
                }
            }
            else{
                currentCount += binCount;
            }

        }
        // we did not insert the last bucket
        if (newBins.size() < bins.size()){
            newBins.put(currentStart, averageCount);
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
    /*public Map<Range, Long> getHistogram() {
        Map<Range, Long> histogramMap = new LinkedHashMap<Range, Long>();
        double currentMin = min;
        for (int i = 0; i < numBins; i++) {
            double currentMax = currentMin + binSize;
            if ((i + 1) == numBins) {
                currentMax = max;
            }
            Range r = new Range(currentMin, currentMax);
            histogramMap.put(r, result[i]);
            currentMin = currentMin + binSize;
        }
        return histogramMap;
    }*/
    /**
     * Get histograms as a map
     *
     * @return the histogram map where Key is the range and value is the freqency. <br>
     * Note that the returned ranges are in pattern of [Min,
     * Min+binSize),[Min+binSize,Min+binSize*2)...[Max-binSize,Max<b>]</b>
     */
    public Map<Range, Long> getHistogram() {
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

}

/*class SingleBin implements Comparable<SingleBin>{

    private double start;

    private double end;

    private int order;

    private int count;

    public SingleBin(double start, double end, int order, int count) throws IllegalArgumentException{

        if (start <0 || end <0 || count < 0 || order < 0){
            throw new IllegalArgumentException("start, end, count or order must be positive");
        }

        if (start > end ){
            throw new IllegalArgumentException("start cannot be greater than end");
        }

        this.start = start;
        this.end = end;
        this.order = order;
        this.count = count;
    }

    public double getStart() {
        return start;
    }

    public double getEnd() {
        return end;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        SingleBin singleBin = (SingleBin) o;

        if (Double.compare(singleBin.start, start) != 0)
            return false;
        return Double.compare(singleBin.end, end) == 0;

    }

    @Override public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(start);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(end);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override public int compareTo(SingleBin bin) {
        if (bin == null){
            return -1;
        }

        if (getStart() == bin.getStart() && getEnd() == bin.getEnd()){
            return 0;
        }
        else if (getEnd() <= bin.getEnd() && bin.getStart() <= getStart() ){
                return -1;
        }
        else{
            return 1;
        }
    }


}*/