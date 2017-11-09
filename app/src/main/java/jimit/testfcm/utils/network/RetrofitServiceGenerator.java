package jimit.testfcm.utils.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import jimit.testfcm.BuildConfig;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by jimit on 17-06-2017.
 */

public class RetrofitServiceGenerator {

    private static OkHttpClient.Builder _httpClient = new OkHttpClient.Builder();
    private static Gson _gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
//            .registerTypeAdapter(ApiResponse.class, new DynamicConverter())
            .create();
    private static Retrofit.Builder _builder = new Retrofit.Builder()
            .baseUrl(BuildConfig.API)
            .addConverterFactory(GsonConverterFactory.create(_gson));

    public static <S> S createService(Class<S> serviceClass) {
        if (!_httpClient.interceptors().isEmpty()) _httpClient.interceptors().clear();
        _httpClient.addInterceptor(new RequestTokenInterceptor());
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            _httpClient.addInterceptor(interceptor);
        }

        OkHttpClient client = _httpClient.build();
        Retrofit retrofit = _builder.client(client).build();
        return retrofit.create(serviceClass);
    }

    public static Gson getGson() {
        return _gson;
    }
}
