package org.talend.dataprep.api.service.command;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

public abstract class ChainedCommand<O, I> extends HystrixCommand<O> {

    private final HystrixCommand<I> input;

    public ChainedCommand(HystrixCommandGroupKey group, HystrixCommand<I> input) {
        super(group);
        this.input = input;
    }

    public ChainedCommand(HystrixCommand<I> input) {
        this(input.getCommandGroup(), input);
    }

    public I getInput() {
        return input.execute();
    }

}
