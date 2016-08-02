package org.talend.dataprep.transformation.actions.common.new_actions_api;

import org.talend.dataprep.api.dataset.Quality;

import java.util.Set;

public class ImmutableQuality {

    private Quality delegate;

    public ImmutableQuality(Quality delegate) {
        this.delegate = delegate;
    }

    public int getEmpty() {
        return delegate.getEmpty();
    }

    public int getInvalid() {
        return delegate.getInvalid();
    }

    public int getValid() {
        return delegate.getValid();
    }

    public Set<String> getInvalidValues() {
        return delegate.getInvalidValues();
    }

}
