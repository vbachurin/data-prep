// ============================================================================
//
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

package org.talend.dataprep.lock.store.file;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.talend.dataprep.lock.store.AbstractLockedResourceRepositoryTest;
import org.talend.dataprep.lock.store.LockedResourceRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { FileSystemLockedResourceRepositoryTest.class })
@ComponentScan(basePackages = "org.talend.dataprep")
@TestPropertySource(properties = { "lock.resource.store=file",
        "lock.resource.store.file.location=target/test/store/lockedResources" })
public class FileSystemLockedResourceRepositoryTest extends AbstractLockedResourceRepositoryTest {

    @Autowired
    public void setRepository(LockedResourceRepository lockedResourceRepository) {
        repository = lockedResourceRepository;
    }

}