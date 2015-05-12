package org.talend.dataprep.api.service.command.common;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import org.apache.http.client.HttpClient;

public abstract class ChainedCommand<O, I> extends DataPrepCommand<O> {

    private final HystrixCommand<I> input;

    public ChainedCommand(HystrixCommandGroupKey group, HttpClient client, HystrixCommand<I> input) {
        super(group, client);
        this.input = input;
    }

    public ChainedCommand(HttpClient client, HystrixCommand<I> input) {
        this(input.getCommandGroup(), client, input);
    }

    public I getInput() {
        return input.execute();
    }

}
