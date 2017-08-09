package it.scoppelletti.spaceship.http.sample;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GreetingService {

    @GET("v1/public/hello")
    Call<String> getPublicGreeting();
}
