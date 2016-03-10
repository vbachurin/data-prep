//  ============================================================================
//
//  Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
//  This source code is available under agreement available at
//  https://github.com/Talend/data-prep/blob/master/LICENSE
//
//  You should have received a copy of the agreement
//  along with this program; if not, write to Talend SA
//  9 rue Pages 92150 Suresnes, France
//
//  ============================================================================

package org.talend.dataprep.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "security.mode", havingValue = "none", matchIfMissing = true)
public class NoOpSecurity implements Security {

    /**
     * @see Security#getUserId()
     */
    @Override
    public String getUserId() {
        return System.getProperty("user.name");
    }

    /**
     * @see Security#getAuthenticationToken()
     */
    @Override
    public String getAuthenticationToken() {
        // no token in the Free Desktop edition
        return null;
    }
}
