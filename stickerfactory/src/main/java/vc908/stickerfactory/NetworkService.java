package vc908.stickerfactory;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;
import vc908.stickerfactory.model.response.ContentResponse;
import vc908.stickerfactory.model.response.NetworkResponseModel;
import vc908.stickerfactory.model.response.PackInfoResponse;
import vc908.stickerfactory.model.response.SearchResponse;
import vc908.stickerfactory.model.response.StickersResponse;

/**
 * @author Dmitry Nezhydenko (dehimb@gmail.com)
 */
public interface NetworkService {

    String PARAM_IS_SUBSCRIBER = "is_subscriber";

    @GET("shop/my")
    Observable<StickersResponse> getUserStickersList(@Query(PARAM_IS_SUBSCRIBER) int isSubscriber);

    @DELETE("packs/{packName}")
    Observable<NetworkResponseModel> hidePack(@Path("packName") String packName);

    @FormUrlEncoded
    @POST("packs/{packName}")
    Observable<PackInfoResponse> purchasePack(@Path("packName") String packName, @Field("purchase_type") String purchaseType);

    @GET("content/{contentId}")
    Observable<ContentResponse> getContentById(@Path("contentId") String contentId);

    @POST("statistics")
    Observable<NetworkResponseModel> sendAnalytics(@Body RequestBody body);

    @PUT("user")
    Observable<NetworkResponseModel> sendUserData(@Body RequestBody body);

    @FormUrlEncoded
    @POST("token")
    Observable<Object> sendToken(@Field("token") String token);

    @GET("search")
    Observable<SearchResponse> getSearchResults(@Query("q") String query, @Query("limit") int limit, @Query("top_if_empty") int topIfEmpty, @Query("whole_word") int wholeWord);

}
