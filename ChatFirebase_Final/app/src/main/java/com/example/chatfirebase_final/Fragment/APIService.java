package com.example.chatfirebase_final.Fragment;

import com.example.chatfirebase_final.Notifications.MyResponse;
import com.example.chatfirebase_final.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAy77Ffko:APA91bFn_hXpIv5i4sc-2m8s2umjZnz601zE9OonTvNUtpCXi6dYdn3wjz4xPJ0o2uLlqc2TNCj9Q_wvOfhnt2jEBxTovR_uws_bJt4K_FRQuIdr3esUyqNO_3WxsLZGQrRHN9FOBdOv"
            }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
