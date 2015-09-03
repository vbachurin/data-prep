package org.talend.dataprep.preparation.store.file;

import static org.junit.Assert.assertEquals;
import static org.talend.dataprep.api.preparation.Step.ROOT_STEP;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.talend.dataprep.api.preparation.Preparation;
import org.talend.dataprep.api.preparation.Step;

/**
 * Unit test for the FileSystemPreparationRepository.
 * 
 * @see FileSystemPreparationRepositoryTest
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = FileSystemPreparationRepositoryTest.class)
@ComponentScan(basePackages = "org.talend.dataprep.preparation.store.file")
@TestPropertySource(inheritLocations = false, inheritProperties = false, properties = { "preparation.store=file",
        "preparation.store.file.location=${java.io.tmpdir}/test/store/preparation" })
public class FileSystemPreparationRepositoryTest {

    /** The preparation repository to test. */
    @Autowired
    private FileSystemPreparationRepository repository;

    /**
     * Clean up repository after each test.
     */
    @After
    public void cleanUpAfterTests() {
        repository.clear();
    }

    @Test
    public void shouldGetPreparationThatWasAdded() {
        Preparation expected = new Preparation("7561486", Step.ROOT_STEP);
        expected.setStep(new Step(ROOT_STEP.id(), expected.getId()));
        repository.add(expected);
        final Preparation actual = repository.get(expected.id(), Preparation.class);
        assertEquals(expected, actual);
    }

    @Test
    public void shouldGetStepThatWasAdded() {
        final Step expected = new Step(ROOT_STEP.id(), "684fdqs638");
        repository.add(expected);
        final Step actual = repository.get(expected.id(), Step.class);
        assertEquals(expected, actual);
    }

}