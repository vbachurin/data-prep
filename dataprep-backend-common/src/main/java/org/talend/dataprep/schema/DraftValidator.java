package org.talend.dataprep.schema;

import org.talend.dataprep.api.dataset.DataSetMetadata;

/**
 * class responsible to validate if the {@link DataSetMetadata} is still a draft TODO return more reasons (i.e field
 * etc..)
 */
public interface DraftValidator {

    Result validate(DataSetMetadata dataSetMetadata);

    class Result {

        private boolean draft;

        public Result(boolean draft) {
            this.draft = draft;
        }

        public boolean isDraft() {
            return draft;
        }

    }

}
