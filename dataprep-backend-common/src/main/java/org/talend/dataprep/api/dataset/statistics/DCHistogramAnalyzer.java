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

import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.talend.dataquality.statistics.numeric.NumericalStatisticsAnalyzer;
import org.talend.datascience.common.inference.Analyzer;
import org.talend.dataquality.statistics.numeric.histogram.HistogramParameter;
import org.talend.dataquality.statistics.numeric.histogram.HistogramColumnParameter;
import org.talend.datascience.common.inference.ResizableList;
import org.talend.datascience.common.inference.type.DataType.Type;
import org.talend.datascience.common.inference.type.TypeInferenceUtils;

/**
 * Created by bdiouf on 27/10/15.
 */
public class DCHistogramAnalyzer extends NumericalStatisticsAnalyzer<DCHistogramStatistics> {

    private static final long serialVersionUID = -3756520692420812485L;

    private ResizableList<DCHistogramStatistics> stats = new ResizableList<>(DCHistogramStatistics.class);

    private HistogramParameter histogramParameter = null;

    /**
     *
     * @param types data types
     * @param histogramParameter Histogram analzyer's parameter
     */
    public DCHistogramAnalyzer(Type[] types, HistogramParameter histogramParameter) {
        super(types);
        if (histogramParameter == null) {
            throw new IllegalArgumentException("Histogram analyzer's parameter should is null.");
        }
        setParameters(histogramParameter);
    }

    /**
     * Set histogram analyzer's parameters
     *
     * @param histogramParameter Histogram analzyer's parameter
     */
    private void setParameters(HistogramParameter histogramParameter) {
        this.histogramParameter = histogramParameter;
    }

    @Override
    public boolean analyze(String... record) {
        Type[] types = getTypes();

        if (record.length != types.length)
            throw new IllegalArgumentException("Each column of the record should be declared a DataType.Type corresponding! \n"
                    + types.length + " type(s) declared in this histogram analyzer but " + record.length
                    + " column(s) was found in this record. \n"
                    + "Using method: setTypes(DataType.Type[] types) to set the types. ");

        if (stats.resize(record.length)) {
            int colIdx = 0;
            for (DCHistogramStatistics stat : stats) {
                HistogramColumnParameter columnParameter = histogramParameter.getColumnParameter(colIdx);
                // Set column parameters to histogram statistics.
                double max = histogramParameter.getDefaultMax();
                double min = histogramParameter.getDefaultMin();
                int numBins = histogramParameter.getDefaultNumBins();
                if (columnParameter != null) {
                    min = columnParameter.getMin();
                    max = columnParameter.getMax();
                    numBins = columnParameter.getNumBins();
                }
                //stat.setParameters(max, min, numBins);
                stat.setParameters(numBins);
                colIdx++;
            }
        }

        for (int id : this.getStatColIdx()) { // analysis each numerical column in the record
            if (!TypeInferenceUtils.isValid(types[id], record[id])) {
                continue;
            }
            analyzerHistogram(id, record);
        }
        return true;
    }

    private void analyzerHistogram(int index, String... record) {
        DCHistogramStatistics histStats = stats.get(index);
        histStats.add(Double.valueOf(record[index]));
    }

    @Override
    public Analyzer<DCHistogramStatistics> merge(Analyzer<DCHistogramStatistics> another) {
        throw new NotImplementedException();
    }

    @Override
    public void end() {
    }

    @Override
    public List<DCHistogramStatistics> getResult() {
        return stats;
    }

}
