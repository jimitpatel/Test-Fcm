package jimit.testfcm;

import com.google.gson.JsonElement;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by jimit on 08-11-2017.
 */

public interface IUser {

    @POST("/rest-auth/login/")
    Call<JsonElement> login(@Body Map<String, String> requestBody);

    @POST("/rest-auth/registration/")
    Call<JsonElement> register(@Body Map<String, String> requestBody);
}
