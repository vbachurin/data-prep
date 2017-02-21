// ============================================================================
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// https://github.com/Talend/data-prep/blob/master/LICENSE
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package org.talend.services.dataprep.api;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.talend.daikon.annotation.Service;
import org.talend.dataprep.metrics.Timed;

@Service(name = "dataprep.SearchAPI")
public interface SearchAPI {

    /**
     * Search dataprep folders, preparations and datasets.
     *
     * @param name the name searched.
     * @param filter the types of items to search. It can be (dataset, preparation, folder).
     * @param strict strict mode means that the name should be the full name (still case insensitive).
     */
    @RequestMapping(value = "/api/search", method = GET, produces = APPLICATION_JSON_VALUE)
    @Timed
    StreamingResponseBody search(@RequestParam(defaultValue = "", required = false, name = "name") String name,
            @RequestParam(required = false, name = "filter") List<String> filter,
            @RequestParam(defaultValue = "false", required = false, name = "strict") boolean strict);
}
