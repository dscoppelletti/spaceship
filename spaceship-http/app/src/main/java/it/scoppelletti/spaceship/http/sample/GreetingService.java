package it.scoppelletti.spaceship.http.sample;

import io.reactivex.Single;
import retrofit2.http.GET;

public interface GreetingService {

    @GET("v1/public/hello")
    Single<String> getPublicGreeting();
}
