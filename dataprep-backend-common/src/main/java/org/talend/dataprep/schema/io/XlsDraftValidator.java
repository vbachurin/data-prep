package org.talend.dataprep.schema.io;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.schema.DraftValidator;

@Service("draftValidator#xls")
public class XlsDraftValidator implements DraftValidator {

    @Override
    public Result validate(DataSetMetadata dataSetMetadata) {
        if (StringUtils.isEmpty(dataSetMetadata.getSheetName())) {
            return new DraftValidator.Result(false);
        }
        return new DraftValidator.Result(false);
    }
}
