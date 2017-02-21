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

package org.talend.services.dataprep;

import org.talend.daikon.annotation.Service;
import org.talend.daikon.version.service.RepositoryService;
import org.talend.dataprep.api.preparation.Preparation;

@Service(name = "dataprep.PreparationRepository")
public interface PreparationRepository extends RepositoryService<Preparation> {
}
