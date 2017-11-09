package jimit.testfcm.utils.network;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by jimit on 18-06-2017.
 */

public class RequestTokenInterceptor implements Interceptor {

    private static final String DEBUG_AUTH = "ecaeffb8a2e13ab0f860470fa958306e";

    private static final String CONTENT_TYPE_KEY = "Content-Type";
    private static final String CONTENT_TYPE_VALUE = "application/json";

    public RequestTokenInterceptor() {

    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request.Builder requestBuilder = original.newBuilder()
                .header(CONTENT_TYPE_KEY, CONTENT_TYPE_VALUE);

        requestBuilder.method(original.method(), original.body());
        Request request = requestBuilder.build();
        return chain.proceed(request);
    }
}
