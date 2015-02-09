package org.talend.dataprep.api.service.command;

import com.netflix.hystrix.HystrixCommand;

public abstract class ChainedCommand<O, I> extends HystrixCommand<O> {

    private HystrixCommand<I> input;

    public ChainedCommand(HystrixCommand<I> input) {
        super(input.getCommandGroup());
        this.input = input;
    }

    public I getInput() {
        return input.observe().toBlocking().first();
    }

}
