package org.talend.dataprep.api.preparation;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.fail;
import static org.talend.dataprep.api.preparation.PreparationActions.ROOT_CONTENT;
import static org.talend.dataprep.api.preparation.Step.ROOT_STEP;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.output.NullOutputStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.talend.dataprep.preparation.store.PreparationRepository;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PreparationUtilsTest.class)
@Configuration
@ComponentScan(basePackages = "org.talend.dataprep")
@EnableAutoConfiguration
public class PreparationUtilsTest {

    @Autowired
    private PreparationRepository repository;

    @Test
    public void should_list_steps_ids_history_from_root() {
        //given
        final List<Action> actions = getSimpleAction("uppercase", "column_name", "lastname");
        final PreparationActions newContent1 = ROOT_CONTENT.append(actions);
        final PreparationActions newContent2 = newContent1.append(actions);

        final Step step1 = new Step(ROOT_STEP.id(), newContent1.id());
        final Step step2 = new Step(step1.id(), newContent2.id());

        repository.add(newContent1);
        repository.add(newContent2);
        repository.add(step1);
        repository.add(step2);

        //when
        List<String> ids = PreparationUtils.listStepsIds(step1.id(), repository);

        //then
        assertThat(ids, hasItem(ROOT_STEP.id()));
        assertThat(ids, hasItem(step1.id()));
        assertThat(ids, not(hasItem(step2.id())));

        //when
        ids = PreparationUtils.listStepsIds(step2.id(), repository);

        //then
        assertThat(ids, hasItem(ROOT_STEP.id()));
        assertThat(ids, hasItem(step1.id()));
        assertThat(ids, hasItem(step2.id()));
    }

    @Test
    public void should_list_steps_ids_history_from_limit() {
        //given
        final List<Action> actions = getSimpleAction("uppercase", "column_name", "lastname");
        final PreparationActions newContent1 = ROOT_CONTENT.append(actions);
        final PreparationActions newContent2 = newContent1.append(actions);

        final Step step1 = new Step(ROOT_STEP.id(), newContent1.id());
        final Step step2 = new Step(step1.id(), newContent2.id());

        repository.add(newContent1);
        repository.add(newContent2);
        repository.add(step1);
        repository.add(step2);

        //when
        List<String> ids = PreparationUtils.listStepsIds(step2.id(), step1.getId(), repository);

        //then
        assertThat(ids, not(hasItem(ROOT_STEP.id())));
        assertThat(ids, hasItem(step1.id()));
        assertThat(ids, hasItem(step2.id()));

        //when
        ids = PreparationUtils.listStepsIds(step2.id(), step2.getId(), repository);

        //then
        assertThat(ids, not(hasItem(ROOT_STEP.id())));
        assertThat(ids, not(hasItem(step1.id())));
        assertThat(ids, hasItem(step2.id()));
    }

    @Test
    public void should_list_steps_history_from_root() {
        //given
        final List<Action> actions = getSimpleAction("uppercase", "column_name", "lastname");
        final PreparationActions newContent1 = ROOT_CONTENT.append(actions);
        final PreparationActions newContent2 = newContent1.append(actions);

        final Step step1 = new Step(ROOT_STEP.id(), newContent1.id());
        final Step step2 = new Step(step1.id(), newContent2.id());

        repository.add(newContent1);
        repository.add(newContent2);
        repository.add(step1);
        repository.add(step2);

        //when
        List<Step> steps = PreparationUtils.listSteps(step1.id(), repository);

        //then
        assertThat(steps, hasItem(ROOT_STEP));
        assertThat(steps, hasItem(step1));
        assertThat(steps, not(hasItem(step2)));

        //when
        steps = PreparationUtils.listSteps(step2.id(), repository);

        //then
        assertThat(steps, hasItem(ROOT_STEP));
        assertThat(steps, hasItem(step1));
        assertThat(steps, hasItem(step2));
    }

    @Test
    public void should_list_steps_history_from_limit() {
        //given
        final List<Action> actions = getSimpleAction("uppercase", "column_name", "lastname");
        final PreparationActions newContent1 = ROOT_CONTENT.append(actions);
        final PreparationActions newContent2 = newContent1.append(actions);

        final Step step1 = new Step(ROOT_STEP.id(), newContent1.id());
        final Step step2 = new Step(step1.id(), newContent2.id());

        repository.add(newContent1);
        repository.add(newContent2);
        repository.add(step1);
        repository.add(step2);

        //when
        List<Step> steps = PreparationUtils.listSteps(step2, step1.getId(), repository);

        //then
        assertThat(steps, not(hasItem(ROOT_STEP)));
        assertThat(steps, hasItem(step1));
        assertThat(steps, hasItem(step2));

        //when
        steps = PreparationUtils.listSteps(step2, step2.getId(), repository);

        //then
        assertThat(steps, not(hasItem(ROOT_STEP)));
        assertThat(steps, not(hasItem(step1)));
        assertThat(steps, hasItem(step2));
    }

    @Test
    public void should_return_empty_list_with_null_last_step() throws Exception {
        //when
        final List<Step> steps = PreparationUtils.listSteps(null, "limit", repository);

        //then
        assertThat(steps, hasSize(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_with_null_limit() throws Exception {
        //when
        PreparationUtils.listSteps(ROOT_STEP, null, repository);

        //then
        fail("Should have thrown IllegalArgumentException because limit step is null");
    }

    @Test
    public void prettyPrint() throws Exception {
        //given
        final List<Action> actions = getSimpleAction("uppercase", "column_name", "lastname");
        final PreparationActions newContent = new PreparationActions(actions);
        final Step step = new Step(ROOT_STEP.id(), newContent.id());
        final Preparation preparation = new Preparation("1234", step.id());

        repository.add(newContent);
        repository.add(step);
        repository.add(preparation);

        //when
        PreparationUtils.prettyPrint(repository, preparation, new NullOutputStream());

        // Basic walk through code, no assert.
    }

    public static List<Action> getSimpleAction(final String actionName, final String paramKey, final String paramValue) {
        final Action action = new Action();
        action.setAction(actionName);
        action.getParameters().put(paramKey, paramValue);

        final List<Action> actions = new ArrayList<>();
        actions.add(action);

        return actions;
    }
}
