package org.talend.dataprep.transformation.pipeline.builder;

import org.talend.dataprep.api.dataset.ColumnMetadata;

import java.util.function.Predicate;

public class ActionsProfile {

    private final boolean needFullAnalysis;
    private final boolean needOnlyInvalidAnalysis;
    private final Predicate<ColumnMetadata> filterForFullAnalysis;
    private final Predicate<ColumnMetadata> filterForInvalidAnalysis;

    public ActionsProfile(final boolean needFullAnalysis, final boolean needOnlyInvalidAnalysis, final Predicate<ColumnMetadata> filterForFullAnalysis, final Predicate<ColumnMetadata> filterForInvalidAnalysis) {
        this.needFullAnalysis = needFullAnalysis;
        this.needOnlyInvalidAnalysis = needOnlyInvalidAnalysis;
        this.filterForFullAnalysis = filterForFullAnalysis;
        this.filterForInvalidAnalysis = filterForInvalidAnalysis;
    }

    public Predicate<ColumnMetadata> getFilterForFullAnalysis() {
        return filterForFullAnalysis;
    }

    public Predicate<ColumnMetadata> getFilterForInvalidAnalysis() {
        return filterForInvalidAnalysis;
    }

    public boolean needFullAnalysis() {
        return needFullAnalysis;
    }

    public boolean needOnlyInvalidAnalysis() {
        return needOnlyInvalidAnalysis;
    }
}
