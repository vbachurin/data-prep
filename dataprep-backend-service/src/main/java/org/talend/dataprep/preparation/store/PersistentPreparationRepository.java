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

package org.talend.dataprep.preparation.store;

import java.util.Collection;
import java.util.stream.Stream;

import org.talend.dataprep.api.preparation.*;
import org.talend.dataprep.conversions.BeanConversionService;

/**
 * A {@link PreparationRepository} implementation that splits {@link Identifiable identifiable} into multiple ones for
 * persistent storage.
 *
 * @see PersistentIdentifiable
 * @see PreparationUtils#scatter(Identifiable)
 */
public class PersistentPreparationRepository implements PreparationRepository {

    private final BeanConversionService beanConversionService;

    private final PreparationRepository delegate;

    public PersistentPreparationRepository(PreparationRepository delegate, BeanConversionService beanConversionService) {
        this.delegate = delegate;
        this.beanConversionService = beanConversionService;
        initContent();
    }

    // Populate underlying repository with expected initial content (root step & root content).
    private void initContent() {
        add(Step.ROOT_STEP);
        add(PreparationActions.ROOT_ACTIONS);
    }

    private static Class<? extends Identifiable> selectPersistentClass(Class<? extends Identifiable> identifiableClass) {
        if (Preparation.class.isAssignableFrom(identifiableClass)) {
            return PersistentPreparation.class;
        } else if (Step.class.isAssignableFrom(identifiableClass)) {
            return PersistentStep.class;
        } else {
            // No need for specific persistent class.
            return identifiableClass;
        }
    }

    @Override
    public <T extends Identifiable> boolean exist(Class<T> clazz, String filter) {
        final Class<? extends Identifiable> targetClass = selectPersistentClass(clazz);
        return delegate.exist(targetClass, filter);
    }

    @Override
    public <T extends Identifiable> Stream<T> list(Class<T> clazz) {
        final Class<T> targetClass = (Class<T>) selectPersistentClass(clazz);
        return delegate.list(targetClass) //
                .map(i -> beanConversionService.convert(delegate.get(i.getId(), targetClass), clazz));
    }

    @Override
    public <T extends Identifiable> Stream<T> list(Class<T> clazz, String filter) {
        final Class<T> targetClass = (Class<T>) selectPersistentClass(clazz);
        return delegate.list(targetClass, filter) //
                .map(i -> beanConversionService.convert(delegate.get(i.getId(), targetClass), clazz));
    }

    @Override
    public void add(Identifiable object) {
        final Collection<Identifiable> identifiableList = PreparationUtils.scatter(object);
        for (Identifiable identifiable : identifiableList) {
            final Class<? extends Identifiable> targetClass = selectPersistentClass(identifiable.getClass());
            final Identifiable storedIdentifiable = beanConversionService.convert(identifiable, targetClass);
            delegate.add(storedIdentifiable);
        }
    }

    @Override
    public <T extends Identifiable> T get(String id, Class<T> clazz) {
        final Class<T> targetClass = (Class<T>) selectPersistentClass(clazz);
        return beanConversionService.convert(delegate.get(id, targetClass), clazz);
    }

    @Override
    public void clear() {
        delegate.clear();
        initContent();
    }

    @Override
    public void remove(Identifiable object) {
        final Class<? extends Identifiable> targetClass = selectPersistentClass(object.getClass());
        delegate.remove(beanConversionService.convert(object, targetClass));
    }
}
