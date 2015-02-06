package org.talend.dataprep.api.service;

import com.netflix.hystrix.HystrixCommand;

abstract class ChainedCommand<O, I> extends HystrixCommand<O> {

    private HystrixCommand<I> input;

    public ChainedCommand(HystrixCommand<I> input) {
        super(input.getCommandGroup());
        this.input = input;
    }

    public I getInput() {
        return input.observe().toBlocking().first();
    }

}
