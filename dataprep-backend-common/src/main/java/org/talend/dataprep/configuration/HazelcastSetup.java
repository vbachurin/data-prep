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
package org.talend.dataprep.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

/**
 * Setup Hazelcast client instance.
 *
 * Configuration is only activated when profile "standalone" is active. Standalone indicates each data prep service
 * runs in its own JVM, serving multiple concurrent requests thus the need for a distributed lock system.
 */
@Configuration
@Profile("standalone")
public class HazelcastSetup {

    @Bean
    public HazelcastInstance hazelcastInstance() {
        Config cfg = new Config();
        cfg.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
        cfg.getNetworkConfig().getJoin().getTcpIpConfig().setEnabled(true);
        cfg.getNetworkConfig().getJoin().getTcpIpConfig().getMembers().add("127.0.0.1"); //$NON-NLS-1$
        return Hazelcast.newHazelcastInstance(cfg);
    }

}
