package org.talend.dataprep.api.service.command.common;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;
import org.talend.daikon.exception.json.JsonErrorCode;
import org.talend.dataprep.api.service.command.ReleasableInputStream;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.CommonErrorCodes;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

@Component
@Scope("request")
public class GenericCommand<T> extends HystrixCommand<T> {

    public static final Logger LOGGER = LoggerFactory.getLogger(GenericCommand.class);
    private final Map<HttpStatus, BiFunction<HttpRequestBase, HttpResponse, T>> behavior = new EnumMap<>(HttpStatus.class);

    @Autowired
    protected Jackson2ObjectMapperBuilder builder;

    @Autowired
    protected ApplicationContext context;

    @Value("${dataset.service.url}")
    protected String datasetServiceUrl;

    protected HttpClient client;

    private Supplier<HttpRequestBase> httpCall;

    private Function<Exception, RuntimeException> onError = (e) -> new TDPException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);

    protected GenericCommand(HystrixCommandGroupKey group, HttpClient client) {
        super(group);
        this.client = client;
    }

    /**
     * Runs a data prep command with the following steps:
     * <ul>
     *     <li>Gets the HTTP command to execute (see {@link #execute(Supplier)}.</li>
     *     <li>Gets the behavior to adopt based on returned HTTP code (see {@link #on(HttpStatus)}.</li>
     *     <li>If no behavior was defined for returned code, returns an error as defined in {@link #onError(Function)}</li>
     *     <li>If a behavior was defined, invokes defined behavior.</li>
     * </ul>
     * @return A instance of <code>T</code>.
     * @throws Exception If command execution fails.
     */
    @Override
    protected T run() throws Exception {
        final HttpRequestBase request = httpCall.get();
        final HttpResponse response = client.execute(request);
        final HttpStatus status = HttpStatus.valueOf(response.getStatusLine().getStatusCode());
        BiFunction<HttpRequestBase, HttpResponse, T> defaultValue = (req, res) -> {
            final int statusCode = res.getStatusLine().getStatusCode();
            if (statusCode >= 400) {
                try {
                    JsonErrorCode code = builder.build().reader(JsonErrorCode.class).readValue(res.getEntity().getContent());
                    code.setHttpStatus(statusCode);
                    final TDPException cause = new TDPException(code);
                    throw onError.apply(cause);
                } catch (IOException e) {
                    throw new TDPException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
                } finally {
                    req.releaseConnection();
                }
            } else {
                LOGGER.error("Unable to process message for request {} (response code: {}).", request, statusCode);
                return Defaults.<T>asNull().apply(req, res);
            }
        };
        return behavior.getOrDefault(status, defaultValue).apply(request, response);
    }

    /**
     * Declares what exception should be thrown in case of error.
     * 
     * @param onError A {@link Function function} that returns a {@link RuntimeException}.
     * @see TDPException
     */
    protected void onError(Function<Exception, RuntimeException> onError) {
        this.onError = onError;
    }

    /**
     * Declares which {@link HttpRequestBase http request} to execute in command.
     * @param call The {@link Supplier} to provide the {@link HttpRequestBase} to execute.
     */
    protected void execute(Supplier<HttpRequestBase> call) {
        httpCall = call;
    }

    /**
     * Starts declaration of a behavior to adopt when HTTP response has status code <code>status</code>.
     * @param status An HTTP {@link HttpStatus status}.
     * @return A {@link BehaviorBuilder builder} to continue behavior declaration.
     * @see BehaviorBuilder#then(BiFunction) 
     */
    protected BehaviorBuilder on(HttpStatus status) {
        return new BehaviorBuilder(status);
    }

    /**
     * A helper class for common behavior definition.
     */
    public static class Defaults {

        public static <T> BiFunction<HttpRequestBase, HttpResponse, T> asNull() {
            return (request, response) -> null;
        }

        public static BiFunction<HttpRequestBase, HttpResponse, String> asString() {
            return (request, response) -> {
                try {
                    return IOUtils.toString(response.getEntity().getContent());
                } catch (IOException e) {
                    throw new TDPException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
                }
            };
        }

        public static BiFunction<HttpRequestBase, HttpResponse, String> emptyString() {
            return (request, response) -> StringUtils.EMPTY;
        }

        public static BiFunction<HttpRequestBase, HttpResponse, InputStream> emptyStream() {
            return (request, response) -> new ByteArrayInputStream(new byte[0]);
        }

        public static BiFunction<HttpRequestBase, HttpResponse, InputStream> pipeStream() {
            return (request, response) -> {
                try {
                    return new ReleasableInputStream(response.getEntity().getContent(), request::releaseConnection);
                } catch (IOException e) {
                    throw new TDPException(CommonErrorCodes.UNEXPECTED_EXCEPTION, e);
                }
            };
        }
    }

    // A intermediate builder for behavior definition.
    protected class BehaviorBuilder {

        private final HttpStatus status;

        public BehaviorBuilder(HttpStatus status) {
            this.status = status;
        }

        public void then(BiFunction<HttpRequestBase, HttpResponse, T> action) {
            GenericCommand.this.behavior.put(status, action);
        }
    }
}
