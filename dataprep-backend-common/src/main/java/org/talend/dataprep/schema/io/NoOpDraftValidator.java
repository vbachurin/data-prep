package org.talend.dataprep.schema.io;

import org.springframework.stereotype.Service;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.schema.DraftValidator;

@Service("draftValidator#noop")
public class NoOpDraftValidator
    implements DraftValidator {

    private static final DraftValidator.Result FALSE_RESULT = new DraftValidator.Result(false);

    public NoOpDraftValidator() {
        // no op
    }

    @Override
    public Result validate(DataSetMetadata dataSetMetadata) {
        return FALSE_RESULT;
    }
}
