package com.dimits.dimitschat.remote;

import com.dimits.dimitschat.model.FCMResponse;
import com.dimits.dimitschat.model.FCMSendData;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAiCt0EuA:APA91bE5IBVoCVLwomGDFu3fdZvdJQHTUgK4apIZHOJ1cgtSA2_dzAvfgyTNYbWTYGSft0UMZC9bV09o6S36IUm7MbcodAGmKu1AQUqasItlOHLjxP0g0JQMYnkWwmIp8UhplBuJ_yaA"
    })
    @POST("fcm/send")
    Observable<FCMResponse> sendNotification(@Body FCMSendData body);
}