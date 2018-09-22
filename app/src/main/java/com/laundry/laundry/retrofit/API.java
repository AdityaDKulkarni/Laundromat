package com.laundry.laundry.retrofit;

import com.laundry.laundry.models.BagModel;
import com.laundry.laundry.models.ClothModel;
import com.laundry.laundry.models.CustomerModel;
import com.laundry.laundry.models.RFIDModel;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * @author Aditya Kulkarni
 */

public interface API {

    @GET("api/v1/bags/")
    Call<List<BagModel>> getBags();

    @GET("api/v1/bags/{pk}/")
    Call<BagModel> getBag(
            @Path("pk") int pk
    );

    @POST("api/v1/bags/")
    @FormUrlEncoded
    Call<ResponseBody> createBags(
            @Field("customer") int customer,
            @Field("count") int count,
            @Field("uid") String uid,
            @Field("current_status") String current_status,
            @Field("service_type") String service_type
    );

    @POST("api/v1/clothes/")
    @FormUrlEncoded
    Call<ResponseBody> createCloth(
            @Field("uid") String uid,
            @Field("cloth_type") String cloth_type,
            @Field("color") String color,
            @Field("bag") int pk
    );

    @GET("api/v1/clothes/")
    Call<List<ClothModel>> getClothes();

    @GET("api/v1/customers/{pk}/")
    Call<CustomerModel> getCustomer(
            @Path("pk") int id);

    @GET("api/v1/tags/")
    Call<List<RFIDModel>> getRFID();

    @PATCH("api/v1/bags/{pk}/")
    Call<BagModel> updateStatus(
            @Path("pk") int pk,
            @Body BagModel model
    );
}
