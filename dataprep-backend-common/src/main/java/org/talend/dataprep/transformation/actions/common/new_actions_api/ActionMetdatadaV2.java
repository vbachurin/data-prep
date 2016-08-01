package org.talend.dataprep.transformation.actions.common.new_actions_api;

import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.api.dataset.DataSetRow;
import org.talend.dataprep.api.dataset.RowMetadata;
import org.talend.dataprep.parameters.Parameter;
import org.talend.dataprep.transformation.actions.category.ScopeCategory;
import org.talend.dataprep.transformation.actions.common.ActionMetadata;
import org.talend.dataprep.transformation.api.action.context.ActionContext;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public interface ActionMetdatadaV2 {
    // adapt i useless as every action implementing it jus does a switch on column type which is available anyway.

    // DESCRIPTIVE METHODS
    String getName();

    String getCategory();

    String getLabel();

    String getDescription();

    String getDocUrl();
    // The dataset metadata and context holding focus information. Should be enough
    // This one is used in JSon serialization... Not really changeable...

    /**
     * We may have to use more specific objects to pass here and at least immutable data types.
     *
     * @param dataSetMetadata needed to use dataset information as column names or data type as default values
     * @param context         needed to have user selection-related information as selected cell, row or column data.
     */
    List<Parameter> getParameters(DataSetMetadata dataSetMetadata, ActionContext context);

    /** We may remove this method as it can be deduced from the parameters. **/
    Collection<ScopeCategory> getScopes();

    // LIFECYCLE METHODS

    /**
     * Called just before the first line is coming. To prepare the Action.
     * It can be used to modify the dataset metadata.
     **/
    void compile(DataSetMetadata metadata, ActionContext context);

    /**
     * This should allow iteration only on row that pass this filter.
     **/
    // We could supply utility class to provide basic filters as the one for cell/row actions in ActionFactory
    // Maybe a bi-filer on row metadata and context is be better
    default Predicate<RowMetadata> getRowFilter(Predicate<RowMetadata> userFiltering) {
        return userFiltering;
    }

    /**
     * Applied on each row that passes the filter.
     * This can also be used to change dataset metadata but It shouldn't change after the first line as gone through undeleted (but
     * maybe it can be change in some ways after, we should check).
     **/
    // First row to pass undeleted define the matadata?
    // Or should dataset metadata should be the first to pass and the rest does not hold metadata...
    void applyOnRow(DataSetRow row, ActionContext context);

    // Behavior descriptive methods for optimizations
    default Set<ActionMetadata.Behavior> getBehavior() {
        return Collections.emptySet();
    }

}
