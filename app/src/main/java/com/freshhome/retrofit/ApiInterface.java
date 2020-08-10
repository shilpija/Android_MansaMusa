package com.freshhome.retrofit;

import com.freshhome.ccavenue.AvenueResponse;
import com.google.gson.JsonElement;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {
    @FormUrlEncoded
    @POST("login")
    Call<JsonElement> userlogin(@Field("username") String email,
                                @Field("password") String password,
                                @Field("role") String role,
                                @Field("unique_id") String unique_id,
                                @Field("device_token") String device_token);

    @FormUrlEncoded
    @POST("gmailLogin")
    Call<JsonElement> userGmaillogin(@Field("email") String email,
                                     @Field("name") String name,
                                     @Field("source_id") String source_id,
                                     @Field("device_token") String device_token);

    @FormUrlEncoded
    @POST("login")
    Call<JsonElement> ulogin(@Field("name") String name,
                             @Field("email") String email);

    @FormUrlEncoded
    @POST("checkNumber")
    Call<JsonElement> checkPhoneNumber(@Field("phone_number") String user_id);


    @FormUrlEncoded
    @POST("changePassword")
    Call<JsonElement> ChangePassword(@Field("id") String user_id,
                                     @Field("password") String password,
                                     @Field("new_password") String newpassword);


    @Headers({"Accept: application/json"})
    @FormUrlEncoded
    @POST("register")
    Call<JsonElement> regiser(@Field("phone_number") String phone_number,
                              @Field("email") String email,
                              @Field("password") String password,
                              @Field("latitude") String latitude,
                              @Field("longitude") String longitude,
                              @Field("name") String name,
                              @Field("location") String location,
                              @Field("DOB") String DOB,
                              @Field("username") String username,
                              @Field("building_no") String building_no,
                              @Field("flat_no") String flat_no,
                              @Field("floor_no") String floor_no,
                              @Field("landmark") String landmark,
                              @Field("city") String city,
                              @Field("role") String role,
                              @Field("supplier_type") String supplier_type);


    //image upload
    @Multipart
    @POST("store/photo")
    Call<JsonElement> UploadImage(@Part("user_id") RequestBody user_id,
                                  @Part("id") RequestBody id,
                                  @Part("side") RequestBody side,
                                  @Part MultipartBody.Part image,
                                  @Part("latitude") RequestBody lat,
                                  @Part("longitude") RequestBody lon);

    //get menu list
    @FormUrlEncoded
    @POST("menuList")
    Call<JsonElement> GetMenuList(@Field("supplier_id") String user_id, @Field("category") String category);

    //get dish detail
    @GET("viewDish/{dish_id}")
    Call<JsonElement> GetItemDeail(@Path("dish_id") String id);

    //getsupplier detail
    @GET("supplierinfo")
    Call<JsonElement> GetSupplierDeail(@Query("supplier_id") String id);

    //GetCategories
    @GET("getCategory")
    Call<JsonElement> GetCategories();

    //GetCuisine
    @GET("getCuisine")
    Call<JsonElement> GetCuisines();

    //add menu items
    @Multipart
    @POST("addMenu")
    Call<JsonElement> addMenuItem(@Part("dish_name") RequestBody dish_name,
                                  @Part("dish_name_arabic") RequestBody dish_name_arabic,
                                  @Part("dish_description") RequestBody dish_description,
                                  @Part("dish_description_arabic") RequestBody dish_description_arabic,
                                  @Part("dish_price") RequestBody dish_price,
                                  @Part("dish_cuisines") RequestBody dish_cuisines,
                                  @Part("dish_categories") RequestBody dish_categories,
                                  @Part("dish_since") RequestBody dish_since,
                                  @Part("city_id") RequestBody city_id,
                                  @Part("dish_meals") RequestBody dish_meals,
                                  @Part("dish_serve") RequestBody dish_serve,
                                  @Part MultipartBody.Part dish_image,
                                  @Part("collected_amount") RequestBody collected_amount,
                                  @Part("discount") RequestBody discount,
                                  @Part("brand_id") RequestBody brand_id,
                                  @Part("main_category") RequestBody main_cat);

    //edit menu items
    @Multipart
    @POST("editDish")
    Call<JsonElement> UpdateMenuItem(@Part("dish_id") RequestBody dish_id,
                                     @Part("dish_name") RequestBody dish_name,
                                     @Part("dish_description") RequestBody dish_description,
                                     @Part("dish_price") RequestBody dish_price,
                                     @Part("dish_cuisines") RequestBody dish_cuisines,
                                     @Part("dish_categories") RequestBody dish_categories,
                                     @Part("dish_since") RequestBody dish_since,
                                     @Part("city_id") RequestBody city_id,
                                     @Part("dish_meals") RequestBody dish_meals,
                                     @Part("dish_serve") RequestBody dish_serve,
                                     @Part MultipartBody.Part dish_image,
                                     @Part("collected_amount") RequestBody collected_amount,
                                     @Part("discount") RequestBody discount,
                                     @Part("brand_id") RequestBody brand_id,
                                     @Part("main_category") RequestBody main_cat);

    //edit menu items without image
//    @Multipart
    @FormUrlEncoded
    @POST("editDish")
    Call<JsonElement> UpdateMenuItemWithoutImage(@Field("dish_id") String dish_id,
                                                 @Field("dish_name") String dish_name,
                                                 @Field("dish_description") String dish_description,
                                                 @Field("dish_price") String dish_price,
                                                 @Field("dish_cuisines") String dish_cuisines,
                                                 @Field("dish_categories") String dish_categories,
                                                 @Field("dish_since") String dish_since,
                                                 @Field("city_id") String city_id,
                                                 @Field("dish_meals") String dish_meals,
                                                 @Field("status") String status,
                                                 @Field("dish_serve") String dish_serve,
                                                 @Field("collected_amount") String collected_amount,
                                                 @Field("discount") String discount,
                                                 @Field("brand_id") String brand_id);

    @FormUrlEncoded
    @POST("editDish")
    Call<JsonElement> UpdateMenuItemQTY(@Field("dish_id") String dish_id,
                                        @Field("dist_qty") String dish_qty,
                                        @Field("status") String status);


    //get user profile
    @FormUrlEncoded
    @POST("profile")
    Call<JsonElement> GetSupplierProfile(@Field("id") String id);


    //get to supplier menu without token
    @GET("menulisting")
    Call<JsonElement> GetSupplierMenu(@Query("supplier_id") String supplier_id);

    //remove menu item
    @DELETE("menu/deletedish")
    Call<JsonElement> DeleteMenuItem(@Query("dish_id") String dish_id);


    //get to supplier schedule without token
    @GET("schedulelisting")
    Call<JsonElement> GetOtherSupplierSchedule(@Query("supplier_id") String supplier_id);


    //update profile
    @Multipart
    @POST("updateProfile")
    Call<JsonElement> UpdateProfile(@Part("id") RequestBody id,
                                    @Part("name") RequestBody name,
                                    @Part("phone_number") RequestBody phone_number,
                                    @Part("description") RequestBody description,
                                    @Part("nationality") RequestBody nationality,
                                    @Part("DOB") RequestBody dob,
                                    @Part("latitude") RequestBody latitude,
                                    @Part("longitude") RequestBody longitude,
                                    @Part("location") RequestBody loc,
                                    @Part("availability") RequestBody availability,
                                    @Part("building_no") RequestBody building_no,
                                    @Part("flat_no") RequestBody flat_no,
                                    @Part("floor_no") RequestBody floor_no,
                                    @Part("landmark") RequestBody landmark,
                                    @Part("city") RequestBody city,
                                    @Part MultipartBody.Part imagrurl,
                                    @Part MultipartBody.Part imagrurlbanner);
//update profile
    @Multipart
    @POST("updateProfile")
    Call<JsonElement> UpdateProfileImg(@Part("id") RequestBody id,
                                    @Part("name") RequestBody name,
                                    @Part("phone_number") RequestBody phone_number,
                                    @Part("description") RequestBody description,
                                    @Part("nationality") RequestBody nationality,
                                    @Part("DOB") RequestBody dob,
                                    @Part("latitude") RequestBody latitude,
                                    @Part("longitude") RequestBody longitude,
                                    @Part("location") RequestBody loc,
                                    @Part("availability") RequestBody availability,
                                    @Part("building_no") RequestBody building_no,
                                    @Part("flat_no") RequestBody flat_no,
                                    @Part("floor_no") RequestBody floor_no,
                                    @Part("landmark") RequestBody landmark,
                                    @Part("city") RequestBody city,
                                    @Part MultipartBody.Part imagrurl);

    //update profile
    @Multipart
    @POST("updateProfile")
    Call<JsonElement> UpdateProfileBanner(@Part("id") RequestBody id,
                                    @Part("name") RequestBody name,
                                    @Part("phone_number") RequestBody phone_number,
                                    @Part("description") RequestBody description,
                                    @Part("nationality") RequestBody nationality,
                                    @Part("DOB") RequestBody dob,
                                    @Part("latitude") RequestBody latitude,
                                    @Part("longitude") RequestBody longitude,
                                    @Part("location") RequestBody loc,
                                    @Part("availability") RequestBody availability,
                                    @Part("building_no") RequestBody building_no,
                                    @Part("flat_no") RequestBody flat_no,
                                    @Part("floor_no") RequestBody floor_no,
                                    @Part("landmark") RequestBody landmark,
                                    @Part("city") RequestBody city,
                                    @Part MultipartBody.Part imagrurlbanner);





    //update supplier  profile
//    @Multipart
    @FormUrlEncoded
    @POST("updateProfile")
    Call<JsonElement> UpdateProfileWithoutImage(@Field("id") String id,
                                                @Field("name") String name,
                                                @Field("phone_number") String phone_number,
                                                @Field("description") String description,
                                                @Field("nationality") String nationality,
                                                @Field("DOB") String dob,
                                                @Field("latitude") String latitude,
                                                @Field("longitude") String longitude,
                                                @Field("location") String loc,
                                                @Field("availability") String availability,
                                                @Field("building_no") String building_no,
                                                @Field("flat_no") String flat_no,
                                                @Field("floor_no") String floor_no,
                                                @Field("landmark") String landmark,
                                                @Field("city") String city,
                                                @Field("supplier_header_image") String banner_id);

    //updateUser to supplier
    @FormUrlEncoded
    @POST("updateProfile")
    Call<JsonElement> UpdateUsertoSupplier(@Field("id") String id,
                                           @Field("name") String name,
                                           @Field("phone_number") String phone_number,
                                           @Field("DOB") String dob,
                                           @Field("latitude") String latitude,
                                           @Field("longitude") String longitude,
                                           @Field("location") String loc,
                                           @Field("building_no") String building_no,
                                           @Field("flat_no") String flat_no,
                                           @Field("floor_no") String floor_no,
                                           @Field("landmark") String landmark,
                                           @Field("city") String city,
                                           @Field("is_supplier") String issupplier,
                                           @Field("supplier_type") String supplier_type);

    //get nationalities
    @GET("getCountry")
    Call<JsonElement> GetNationalities();

    //get filterdata
    @FormUrlEncoded
    @POST("menu/getfilter")
    Call<JsonElement> GetFilterDataChange(@Field("screen_id") String screen_id,
                                          @Field("type") String type,
                                          @Field("country_name") String country_name,
                                          @Field("category_id") String category_id);

    //get filterdata
    @GET("menu/getfilter")
    Call<JsonElement> GetFilterData(@Query("country_name") String country_name);

    //findItem
    @FormUrlEncoded
    @POST("findItem")
    Call<JsonElement> GetDishItems(@Field("city_id") String city_id,
                                   @Field("cuisines") String cuisines,
                                   @Field("category") String category,
                                   @Field("meal") String meal,
                                   @Field("perPage") String perPage,
                                   @Field("sort") String sort,
                                   @Field("is_user") String role,
                                   @Field("screen_id") String screen_id,
                                   @Field("latitude") String latitude,
                                   @Field("longitude") String longitude);

    //search
    @FormUrlEncoded
    @POST("searchItem")
    Call<JsonElement> GetSearchedItems(@Field("city_id") String city_id,
                                       @Field("perPage") String perPage,
                                       @Field("name") String name,
                                       @Field("screen_id") String screen_id);

    //contact us
    @FormUrlEncoded
    @POST("contact")
    Call<JsonElement> ContactUs(@Field("subject") String supplier_id,
                                @Field("email") String schedule_date,
                                @Field("message") String start_time,
                                @Field("role") String role);


    //TODO:----------------------make user dish and supplier favo----------------------------
    //get favo menu items and supplier
    @GET("myfav")
    Call<JsonElement> GetFavoData();

    //--favo menu items--
    //add to favo menu item
    @FormUrlEncoded
    @POST("favMenu")
    Call<JsonElement> AddFavoDish(@Field("menu_id") String dish_id);

    //remove to favo dish
    @DELETE("favMenu")
    Call<JsonElement> RemoveFavoDish(@Query("dish_id") String dish_id);

    //--favo supplier--
    //add to favo supplier
    @FormUrlEncoded
    @POST("favSupplier")
    Call<JsonElement> AddFavoSupplier(@Field("supplier_id") String supplier_id);

    //remove to favo supplier
    @DELETE("favSupplier")
    Call<JsonElement> RemoveFavoSupplier(@Query("supplier_id") String supplier_id);


    //TODO:--------------------------delivery address-----------------------
    //Add Delivery Address
    @FormUrlEncoded
    @POST("userAddress")
    Call<JsonElement> AddDeliveryAddress(@Field("title") String title,
                                         @Field("location") String location,
                                         @Field("longitude") String longitude,
                                         @Field("latitude") String latitude,
                                         @Field("city") String city,
                                         @Field("building_name") String building_name,
                                         @Field("flat_no") String flat_no,
                                         @Field("floor_no") String floor_no,
                                         @Field("landmark") String landmark, @Field("is_default") String is_default);

    // get user's delivery address
    @GET("userAddress")
    Call<JsonElement> GetAddressList();

    //delete user's delivery address
    @DELETE("userAddress")
    Call<JsonElement> DeleteAddress(@Query("address_id") String address_id);

    //update user's delivery address
    @FormUrlEncoded
    @POST("userAddress")
    Call<JsonElement> UpdateDeliveryAddress(@Field("address_id") String address_id,
                                            @Field("title") String title,
                                            @Field("location") String location,
                                            @Field("longitude") String longitude,
                                            @Field("latitude") String latitude,
                                            @Field("city") String city,
                                            @Field("building_name") String building_name,
                                            @Field("flat_no") String flat_no,
                                            @Field("floor_no") String floor_no,
                                            @Field("landmark") String landmark, @Field("is_default") String is_default);


    //TODO:------------ Get User profile--------------
//    @FormUrlEncoded
    @POST("profile")
    Call<JsonElement> GetUserProfile(@Query("is_user") String id);

    //TODO:------------Update User profile--------------
    @Multipart
    @POST("updateUserProfile")
    Call<JsonElement> UpdateUserProfile(@Part("name") RequestBody name,
                                        //  @Part("phone_number") RequestBody phone_number,
                                        @Part("description") RequestBody description,
                                        @Part("nationality") RequestBody nationality,
                                        @Part("dob") RequestBody dob,
                                        @Part("gender") RequestBody gender,
                                        @Part("occupation_id") RequestBody occupation_id,
                                        @Part MultipartBody.Part imagrurl,
                                        @Part("header_image") RequestBody banner_id);

    @FormUrlEncoded
    @POST("updateUserProfile")
    Call<JsonElement> UpdateUserProfileWithoutImage(@Field("name") String name,
//                                                @Field("phone_number") String phone_number,
                                                    @Field("description") String description,
                                                    @Field("nationality") String nationality,
                                                    @Field("dob") String dob,
                                                    @Field("gender") String gender,
                                                    @Field("occupation_id") String occupation_id,
                                                    @Field("header_image") String banner_id);


//TODO:---------------------------cart---------------------------------------

    @FormUrlEncoded
    @POST("cart/add")
    Call<JsonElement> AddUpdateDeleteCartItem(@Field("menu_id") String menu_id,
                                              @Field("quantity") String quantity);

    @FormUrlEncoded
    @POST("cart/add")
    Call<JsonElement> AddConfirm(@Field("menu_id") String menu_id,
                                 @Field("quantity") String quantity, @Field("confirm") String confirm);


    //for product with varients
    @FormUrlEncoded
    @POST("cart/add")
    Call<JsonElement> AddUpdateDeleteCartItem(@FieldMap Map<String, String> options);

    @FormUrlEncoded
    @POST("cart/add")
    Call<JsonElement> AddConfirm(@FieldMap Map<String, String> options,
                                 @Field("confirm") String confirm);


    // get user's delivery address
    @GET("cart/view-cart")
    Call<JsonElement> GetUserCart();

    @FormUrlEncoded
    @POST("cart/checkout")
    Call<JsonElement> Checkout_Online(@Field("address_id") String address_id,
                                      @Field("phone_number") String phone_number,
                                      @Field("notes") String notes,
                                      @Field("order_type") String order_type,
                                      @Field("payment_code") String payment_code,
                                      @Field("card_id") String card_id);

    @FormUrlEncoded
    @POST("cart/checkout")
    Call<JsonElement> Checkout_PreOrder(@Field("address_id") String address_id,
                                        @Field("phone_number") String phone_number,
                                        @Field("collection_time") String collection_time,
                                        @Field("collection_date") String collection_date,
                                        @Field("notes") String notes,
                                        @Field("order_type") String order_type,
                                        @Field("payment_code") String payment_code,
                                        @Field("card_id") String card_id);

    //TODO:-----------------------order history----------------------


    // get user's order history
    @GET("user/orders")
    Call<JsonElement> GetUserOrderHistory();

    // get user's order history
    @GET("user/order/{order_id}")
    Call<JsonElement> GetUserOrderDetail(@Path("order_id") String order_id);

    // repeat order
    @FormUrlEncoded
    @POST("user/repeat-order")
    Call<JsonElement> RepeatOrder(@Field("order_id") String order_id);

    // return order
    @FormUrlEncoded
    @POST("user/return-order")
    Call<JsonElement> ReturnOrder(@Field("order_id") String order_id,
                                  @Field("reason") String reason,
                                  @Field("comment") String comment);



    // TODO --------------cancel order----------------------
    @FormUrlEncoded
    @POST("user/cancel-order")
    Call<JsonElement> CancelOrder_User(@Field("order_id") String order_id, @Field("reason") String reason);

    @FormUrlEncoded
    @POST("supplier/cancel-order")
    Call<JsonElement> CancelOrder_Supplier(@Field("order_id") String order_id, @Field("reason") String reason);


    //TODO:-------------post feedback----------------
    // menu item
    @FormUrlEncoded
    @POST("cart/menurating")
    Call<JsonElement> FeedbackMenuItem(@Field("order_id") String order_id,
                                       @Field("menu_id") String menu_id,
                                       @Field("rating") String rating,
                                       @Field("reviews") String reviews);

    // menu item
    @FormUrlEncoded
    @POST("cart/rating")
    Call<JsonElement> FeedbackOrder(@Field("order_id") String order_id,
                                    @Field("taste_rating") String taste_rating,
                                    @Field("presentation_rating") String presentation_rating,
                                    @Field("packing_rating") String packing_rating,
                                    @Field("overall_rating") String overall_rating,
                                    @Field("review") String review);

    //get to menu item comments
//    : http://freshhomee.com/api/menu/get-menu-rating?menu_id={menu_id}&page={1,2,3,4 etc etc}

    @GET("menu/get-menu-rating")
    Call<JsonElement> GetMenuItemCommentList(@Query("menu_id") String menu_id,
                                             @Query("page") String page);


    //get to supplier comments
//    http://freshhomee.com/api/user/supplier-rating?supplier_id={supplier_id}&page={1,2,3,4
    @GET("user/supplier-rating")
    Call<JsonElement> GetSupplierCommentList(@Query("supplier_id") String menu_id,
                                             @Query("page") String page);


    //TODO:--------------------schedule--------------

    // schedule
//   @FormUrlEncoded
    @POST("addmenuschedule")
    Call<JsonElement> AddMenuSchedule(@Body RequestBody jsondata);


    @POST("updatemenuschedule")
    Call<JsonElement> UpdateMenuSchedule(@Body RequestBody jsondata);

    @GET("menu/getmenuschedule")
    Call<JsonElement> GetSchedule();

    //TODO: remove schedule   item
    @DELETE("deleteschedule")
    Call<JsonElement> DeleteScheduledDay(@Query("id") String id);


    //TODO:------------my kitchen-----------------------
    @GET("supplier/orders")
    Call<JsonElement> GetMyKitchenOrders();


    //TODO:------------my orders products-----------------------
    @GET("supplier/orders?type=2")
    Call<JsonElement> GetMyOrders();


    //TODO:------------my kitchen order detail-----------------------
    @GET("supplier/order")
    Call<JsonElement> GetOrdersDetail(@Query("order_id") String order_id);

    // TODO:accept order
    @FormUrlEncoded
    @POST("order/accept")
    Call<JsonElement> AcceptOrder(@Field("order_id") String order_id);

    //TODO: reject order
    @FormUrlEncoded
    @POST("order/reject")
    Call<JsonElement> RejectOrder(@Field("order_id") String order_id,
                                  @Field("reject_type") String reject_type,
                                  @Field("reject_message") String reject_message);

    //TODO:-------notification-----------
    @GET("notification")
    Call<JsonElement> GetNotification(@Query("role") String role);


    //TODO:----sales report---------------
    @GET("report")
    Call<JsonElement> GetSalesReportData(@Query("start_date") String start_date,
                                         @Query("end_date") String end_date,
                                         @Query("type") String type);


    //TODO:-----------------------driver module-----------------------------------

    // GetDeliveryCompanies
    @GET("driver/companies")
    Call<JsonElement> GetDeliveryCompanies();


    //reject order
    @FormUrlEncoded
    @POST("driver/check-number")
    Call<JsonElement> CheckDrvierNumber(@Field("company_id") String company_id,
                                        @Field("phone_number") String phone_number,
                                        @Field("device_token") String device_token);


    // Get Driver profile Data
    @GET("driver/profile")
    Call<JsonElement> GetDriverProfile();


    @Multipart
    @POST("driver/update")
    Call<JsonElement> UpdateDriverProfile(@Part("name") RequestBody name,
                                          @Part("is_available") RequestBody is_available,
                                          @Part("nationality") RequestBody nationality,
                                          @Part("age") RequestBody age,
                                          @Part MultipartBody.Part imagrurl);

    @FormUrlEncoded
    @POST("driver/update")
    Call<JsonElement> UpdateDriverProfileWithoutImage(@Field("name") String name,
                                                      @Field("is_available") String is_available,
                                                      @Field("nationality") String nationality,
                                                      @Field("age") String age);


    @FormUrlEncoded
    @POST("driver/update")
    Call<JsonElement> UpdateDriverStatus(@Field("is_available") String is_available);


    //contact us
    @FormUrlEncoded
    @POST("driver/contact")
    Call<JsonElement> ContactUsDriver(@Field("subject") String supplier_id,
                                      @Field("email") String schedule_date,
                                      @Field("message") String start_time);

    // Get Driver orders Data
    @GET("driver/orders")
    Call<JsonElement> GetDriverOrders();

    //order detail
    @GET("driver/order")
    Call<JsonElement> GetDriverOrderDetail(@Query("order_id") String role);


    //notification
    @GET("notification")
    Call<JsonElement> GetDriverNotification(@Query("role") String role);


    //accept order
    @FormUrlEncoded
    @POST("driver/accept")
    Call<JsonElement> DriverAcceptOrder(@Field("order_id") String supplier_id);

    //accept order
    @FormUrlEncoded
    @POST("profiles/accept-order")
    Call<JsonElement> DAcceptOrder(@Field("order_ids") String supplier_id, @Field("credit_card") String card_id);


    //reject order
    @FormUrlEncoded
    @POST("driver/reject")
    Call<JsonElement> DriverRejectOrder(@Field("order_id") String supplier_id);

    //driver home
    @GET("driver/home")
    Call<JsonElement> GetDriverPendingOrder();

    //GetKitchenLocations
    @GET("profiles/suppliers")
    Call<JsonElement> GetKitchenLocations(@Query("latitude") String lat, @Query("longitude") String lng);


    //update driver location
    @FormUrlEncoded
    @POST("user/updatelocation")
    Call<JsonElement> DriverLocationUpdate(@Field("latitude") Double latitude,
                                           @Field("longitude") Double longitude);


    @Multipart
    @POST("user/register")
    Call<JsonElement> IndividualDriverSignUp(@Part("phone_number") RequestBody phone_number,
                                             @Part("email") RequestBody email,
                                             @Part("password") RequestBody password,
                                             @Part("latitude") RequestBody latitude,
                                             @Part("longitude") RequestBody longitude,
                                             @Part("name") RequestBody name,
                                             @Part("location") RequestBody location,
                                             @Part("dob") RequestBody dob,
                                             @Part("username") RequestBody username,
                                             @Part("city") RequestBody city,
                                             @Part("role") RequestBody role,
                                             @Part MultipartBody.Part imagepath);


    @GET("driver/individual-profile")
    Call<JsonElement> GetIndividaulDriverProfile();

    //update profile
    @Multipart
    @POST("user/driver-update")
    Call<JsonElement> UpdateIndividaulDriverProfileImage(@Part("name") RequestBody name,
                                                         @Part("city") RequestBody city,
                                                         @Part("dob") RequestBody dob,
                                                         @Part("location") RequestBody location,
                                                         @Part("latitude") RequestBody latitude,
                                                         @Part("longitude") RequestBody longitude,
                                                         @Part MultipartBody.Part imagrurl);

    @FormUrlEncoded
    @POST("user/driver-update")
    Call<JsonElement> UpdateIndividaulDriverProfile(@Field("name") String name,
                                                    @Field("city") String city,
                                                    @Field("dob") String dob,
                                                    @Field("location") String location,
                                                    @Field("latitude") String latitude,
                                                    @Field("longitude") String longitude);

    @FormUrlEncoded
    @POST("user/driver-update")
    Call<JsonElement> UpdateIndividaulDriverStatus(@Field("is_available") String is_available);

    //update profile
    @Multipart
    @POST("user/change-document")
    Call<JsonElement> UpdateIndividaulDriverLicsense(@Part MultipartBody.Part imagrurl);

    //update national_id
    @Multipart
    @POST("menu/national-id")
    Call<JsonElement> UpdateNationalID(@Part MultipartBody.Part imagrurl);


    //SUBMIT PICKUP THE ORDER
    @GET("driver/orderupdate")
    Call<JsonElement> PickuptheOrder(@Query("order_id") String order_id, @Query("status_id") String status_id);

    //delivery api
    @Multipart
    @POST("driver/complete")
    Call<JsonElement> completeOrder(@Part("order_id") RequestBody order_id,
                                    @Part MultipartBody.Part imagrurl);


    //---------------TODO sales module--------------------
    //register salesperson
    @FormUrlEncoded
    @POST("sales/register")
    Call<JsonElement> SalesRegister(@Field("username") String username,
                                    @Field("email") String email,
                                    @Field("phone_number") String phone_number,
                                    @Field("full_name") String full_name,
                                    @Field("gender") String gender,
                                    @Field("dob") String dob,
                                    @Field("city") String city,
                                    @Field("password") String password,
                                    @Field("latitude") Double latitude,
                                    @Field("longitude") Double longitude);


    //my request
    //notification
    @GET("sales/myrequests")
    Call<JsonElement> GetSaleRequest();

    //my customer
    @GET("sales/mycustomer")
    Call<JsonElement> GetSaleMyCustomer();

    //jobs done
    @GET("sales/jobsdone")
    Call<JsonElement> GetSaleJobsDone();

    //notification accept
    @GET("sales/accept-request")
    Call<JsonElement> AcceptSalesReq(@Query("notification_id") String notification_id);


    //notification accept
    @GET("sales/reject-request")
    Call<JsonElement> RejectSalesReq(@Query("notification_id") String notification_id);


    //notification scann accept
    @GET("sales/complete-request")
    Call<JsonElement> CompeleteSalesReq(@Query("request_id") String request_id);

    //reject scann notification
    @GET("sales/decline-request")
    Call<JsonElement> RejectScanRequest(@Query("request_id") String request_id);

    //Get Order Id for payment
    @GET("plans/order-key")
    Call<JsonElement> getOrderIDforPayment();

    //Get Order Id for payment
    @GET("cart/order-key")
    Call<JsonElement> getOrderIDforBuyerPayment();


    //PROFILE
    @GET("sales/profile")
    Call<JsonElement> GetSalesProfile();

    @FormUrlEncoded
    @POST("sales/update")
    Call<JsonElement> UpdateSalesProfileWithoutImage(@Field("full_name") String name,
                                                     @Field("gender") String gender,
                                                     @Field("city") String city,
                                                     @Field("dob") String dob);

    @Multipart
    @POST("sales/update")
    Call<JsonElement> UpdateSalesProfileWithImage(@Part("full_name") RequestBody name,
                                                  @Part("gender") RequestBody gender,
                                                  @Part("city") RequestBody city,
                                                  @Part("dob") RequestBody dob,
                                                  @Part MultipartBody.Part imagrurl);


    //TODO ----------------ask for help--------------------------------------
    @FormUrlEncoded
    @POST("askforhelp")
    Call<JsonElement> AskForHelp(@Field("name") String name,
                                 @Field("location") String location,
                                 @Field("phonenumber") String phonenumber,
                                 @Field("lat") Double lat,
                                 @Field("lng") Double lng,
                                 @Field("device_token") String device_token,
                                 @Field("request_type") String request_type,
                                 @Field("email") String email);


    @FormUrlEncoded
    @POST("sales/cancel-request")
    Call<JsonElement> CancelRequest(@Field("request_id") String request_id,
                                    @Field("reason_id") String reason_id,
                                    @Field("message") String message);

    //PROFILE
    @GET("cancel-reasons")
    Call<JsonElement> GetCancelReason();


    @GET("sales/videos")
    Call<JsonElement> GetSalesDemoVideos();

    @FormUrlEncoded
    @POST("sales/video-watch")
    Call<JsonElement> UdpdateVideoWatched(@Field("video_id") String video_id);


    //get current request and roles data
    @GET("sales/help-text")
    Call<JsonElement> GetCurrentReq(@Query("device_token") String device_token);

    @GET("sales/complete-request")
    Call<JsonElement> GetCompleteReq(@Query("request_id") String request_id);


    @FormUrlEncoded
    @POST("sales/cancel-request-by-supplier")
    Call<JsonElement> CancelRequestBySupplier(@Field("request_id") String request_id,
                                              @Field("reason_id") String reason_id,
                                              @Field("message") String message,
                                              @Field("device_token") String device_token);


    @FormUrlEncoded
    @POST("sales/scan-code")
    Call<JsonElement> SubmitScannedData(@Field("email") String email,
                                        @Field("device_token") String device_token);


    //get banner images
    @GET("banners")
    Call<JsonElement> GetBannerImages(@Query("type") String type);


    @FormUrlEncoded
    @POST("user/switch")
    Call<JsonElement> SwitchSupplierToSales(@Field("sale") String sale);


    @FormUrlEncoded
    @POST("sales/sales-person-rating")
    Call<JsonElement> RateSalesPerson(@Field("request_id") String request_id, @Field("rating") String rating);


    //TODO ADD ITEMS FOR SHOP PRODUCTS AND HOME PRODUCTS
    @FormUrlEncoded
    @POST("products/get-categories")
    Call<JsonElement> GetProductCategories(@Field("parent_id") String parent_id);

    //TODO ADD ITEMS FOR SHOP PRODUCTS AND HOME PRODUCTS
    @FormUrlEncoded
    @POST("products/add-product")
    Call<JsonElement> AddProductItem(@Field("product_name") String product_name,
                                     @Field("product_description") String product_description,
                                     @Field("product_price") String product_price,
                                     @Field("product_qty") String product_qty,
                                     @Field("collected_amount") String collected_amount,
                                     @Field("type_of_product") String type_of_product,
                                     @Field("discount") String discount);

    @Multipart
    @POST("products/add-product")
    Call<JsonElement> AddProductItemWithImage(@Part("product_name") RequestBody product_name,
                                              @Part("product_name_arabic") RequestBody product_name_arabic,
                                              @Part("product_description") RequestBody product_description,
                                              @Part("product_description_arabic") RequestBody product_description_arabic,
                                              @Part("product_price") RequestBody product_price,
                                              @Part("product_qty") RequestBody product_qty,
                                              @Part MultipartBody.Part imagepath,
                                              @Part("main_category") RequestBody main_Category,
                                              @Part("categories") RequestBody sub_categories,
                                              @Part("collected_amount") RequestBody collected_amount,
                                              @Part("type_of_product") RequestBody type_of_product,
                                              @Part("discount") RequestBody discount,
                                              @Part("brand_id") RequestBody brand_id);

    //product list
    @GET("products/my-products")
    Call<JsonElement> GetProductList(@Query("screen_id") String screen_id);

    //product detail
    @GET("products/product")
    Call<JsonElement> GetProductDetail(@Query("product_id") String product_id);


    //remove menu item
    @GET("products/delete-product")
    Call<JsonElement> DeleteProductItem(@Query("product_id") String product_id);


    @FormUrlEncoded
    @POST("products/edit-product")
    Call<JsonElement> EditProductItemWithoutImage(@Field("product_id") String product_id,
                                                  @Field("product_name") String product_name,
                                                  @Field("product_description") String product_description,
                                                  @Field("product_price") String product_price,
                                                  @Field("product_qty") String product_qty,
                                                  @Field("status") String status,
                                                  @Field("collected_amount") String collected_amount,
                                                  @Field("type_of_product") String type_of_product,
                                                  @Field("discount") String discount);


    @FormUrlEncoded
    @POST("products/edit-product")
    Call<JsonElement> UpdateProductStatus(@Field("product_id") String product_id,
                                          @Field("product_qty") String product_qty,
                                          @Field("status") String status,
                                          @Field("product_name") String product_name,
                                          @Field("product_description") String product_description,
                                          @Field("product_price") String product_price);


    @Multipart
    @POST("products/edit-product")
    Call<JsonElement> EditProductItemWithImage(@Part("product_name") RequestBody product_name,
                                               @Part("product_description") RequestBody product_description,
                                               @Part("product_price") RequestBody product_price,
                                               @Part("product_qty") RequestBody product_qty,
                                               @Part MultipartBody.Part imagepath,
                                               @Part("product_id") RequestBody product_id,
                                               @Part("collected_amount") RequestBody collected_amount,
                                               @Part("type_of_product") RequestBody type_of_product,
                                               @Part("discount") RequestBody discount);

    //TODO change email or phone number
    @FormUrlEncoded
    @POST("user/change-email")
    Call<JsonElement> ChangeEmail(@Field("old_email_address") String old_email_address,
                                  @Field("email") String email,
                                  @Field("type") String type);

    @FormUrlEncoded
    @POST("user/change-email")
    Call<JsonElement> ChangePhoneNumber(@Field("phone_number") String phone_number,
                                        @Field("type") String type);


    @GET("products/get-menu-rating")
    Call<JsonElement> GetProductCommentList(@Query("product_id") String product_id);


    //TODO-------------------PLANS SUBSCIPTION-----------------------------
    //plans
    @GET("plans/all-plans")
    Call<JsonElement> GetPlans(@Query("type") String product_id);

    //WALLET
    @GET("user/mywallet")
    Call<JsonElement> GetWalletDetails(@Query("role") String role);


    @FormUrlEncoded
    @POST("plans/subscribe")
    Call<JsonElement> SubscribePlan(@Field("plan_id") String plan_id,
                                    @Field("card_id") String card_id);


    @FormUrlEncoded
    @POST("plans/subscribewallet")
    Call<JsonElement> subscribewallet(@Field("plan_id") String plan_id,
                                      @Field("card_id") String card_id);


    @FormUrlEncoded
    @POST("user/add-money")
    Call<JsonElement> AddMoneytoWallet(@Field("card_id") String card_id,
                                       @Field("amount") String amount);

    @FormUrlEncoded
    @POST("plans/cancel")
    Call<JsonElement> CancelPlan(@Field("plan_id") String plan_id);
    //TODO---------------------add edit delete cards----------------------------


    @GET("account/mycards")
    Call<JsonElement> GetCards();

    @FormUrlEncoded
    @POST("account/addcard")
    Call<JsonElement> AddCardDetails(@Field("card_number") String card_number,
                                     @Field("exp_month") String exp_month,
                                     @Field("exp_year") String exp_year,
                                     @Field("cvv") String cvv,
                                     @Field("card_name") String card_name);

    @FormUrlEncoded
    @POST("account/updatecard")
    Call<JsonElement> UpdateCardDetails(@Field("card_number") String card_number,
                                        @Field("exp_month") String exp_month,
                                        @Field("exp_year") String exp_year,
                                        @Field("cvv") String cvv,
                                        @Field("card_name") String card_name,
                                        @Field("card_id") String card_id);

    @FormUrlEncoded
    @POST("account/deletecard")
    Call<JsonElement> DeleteCardDetails(@Field("card_id") String card_id);

//TODO-----------------ADD BANK DETAILS-----------------------

    @GET("user/view-bank")
    Call<JsonElement> GetBankDetails();

    @FormUrlEncoded
    @POST("user/add-bank")
    Call<JsonElement> AddBankDetails(@Field("first_name") String first_name,
                                     @Field("last_name") String last_name,
                                     @Field("email_address") String email_address,
                                     @Field("bank_name") String bank_name,
                                     @Field("account_number") String account_number,
                                     @Field("iban") String iban);


    @FormUrlEncoded
    @POST("user/update-bank")
    Call<JsonElement> UpdateBankDetails(@Field("first_name") String first_name,
                                        @Field("last_name") String last_name,
                                        @Field("email_address") String email_address,
                                        @Field("bank_name") String bank_name,
                                        @Field("account_number") String account_number,
                                        @Field("iban") String iban);


    //TODO--------------order ready------------------------
    @FormUrlEncoded
    @POST("order/process-order")
    Call<JsonElement> OrderReady(@Field("order_id") String order_id);


    //TODO--------------forgot password------------------------
    @FormUrlEncoded
    @POST("reset")
    Call<JsonElement> forgotpassword(@Field("email") String email_id);


    @FormUrlEncoded
    @POST("user/switch-to-driver")
    Call<JsonElement> SwitchtoDriver(@Field("update") String update);


    //TODO HOME PAGE
    @GET("menu/home-items")
    Call<JsonElement> getHomeData();

    @FormUrlEncoded
    @POST("menu/latest-product")
    Call<JsonElement> GetLatestProducts(@Field("limit") String limit);

    @FormUrlEncoded
    @POST("menu/best-selling-product")
    Call<JsonElement> GetBestSellingProducts(@Field("limit") String limit);

    @FormUrlEncoded
    @POST("menu/order-now")
    Call<JsonElement> GetOrderNowProducts(@Field("limit") String limit);

    @GET("menu/online-kitchens")
    Call<JsonElement> GetKitchens(@Query("limit") String limit);


    //TODO CATEGORY DETAIL APIS
    @GET("menu/category-details")
    Call<JsonElement> GetCategoryDetail(@Query("homecategoryid") String homecategoryid);

    @FormUrlEncoded
    @POST("menu/view-all-supplier")
    Call<JsonElement> GetAllSuppliers(@Field("homecategoryid") String homecategoryid,
                                      @Field("limit") String limit);

    @FormUrlEncoded
    @POST("menu/view-all-sub-category")
    Call<JsonElement> GetAllSubCategories(@Field("homecategoryid") String homecategoryid,
                                          @Field("limit") String limit);

    @FormUrlEncoded
    @POST("menu/all-brand-list")
    Call<JsonElement> GetAllBrands(@Field("homecategoryid") String homecategoryid,
                                   @Field("limit") String limit);

    @FormUrlEncoded
    @POST("menu/homecategory")
    Call<JsonElement> getHomeCategories(@Field("main_category_id") String screen_id);

    @FormUrlEncoded
    @POST("products/get-categories")
    Call<JsonElement> getHomeProductCategories(@Field("parent_id") String parent_id);

    @FormUrlEncoded
    @POST("products/get-categories")
    Call<JsonElement> getProductSubCategories(@Field("parent_id") String parent_id);

    @FormUrlEncoded
    @POST("user/mywithdraw")
    Call<JsonElement> myWithdraw(@Field("type") String type,
                                 @Field("amount") String amount,
                                 @Field("fullname") String fullname,
                                 @Field("mobile") String mobile,
                                 @Field("role") String role);

    // sub categories
    @GET("menu/homecategorydetail")
    Call<JsonElement> GetSUBCategories(@Query("homecategoryid") String homecategoryid);

    // sub categories
    @GET("menu/brand-list-by-sub-category")
    Call<JsonElement> GetBrandsASCategories(@Query("categoryid") String homecategoryid);


    @GET("plans/rsa-key")
    Call<AvenueResponse> getRsaKey(@Query("orderid") String orderid);

    @FormUrlEncoded
    @POST("searchItem")
    Call<JsonElement> searchItem(@Field("name") String name);

    @FormUrlEncoded
    @POST("search")
    Call<JsonElement> search(@Field("name") String name,
                             @Field("type") String type,
                             @Field("screen_id") String screen_id,
                             @Field("perPage") String perPage,
                             @Field("latitude") String latitude,
                             @Field("longitude") String longitude);


    //TODO CATEGORY DETAIL APIS
    @GET("menu/subcategory-list")
    Call<JsonElement> GetsubcategoryList(@Query("categoryid") String categoryid);

    //findItem
    @FormUrlEncoded
    @POST("findItem")
    Call<JsonElement> GetCatProducts(@Field("city_id") String city_id,
                                     @Field("categories") String category,
                                     @Field("perPage") String perPage,
                                     @Field("sort") String sort,
                                     @Field("is_user") String role,
                                     @Field("screen_id") String screen_id,
                                     @Field("latitude") String latitude,
                                     @Field("longitude") String longitude);

    //LatestFilterItem
    @FormUrlEncoded
    @POST("menu/latest-product")
    Call<JsonElement> GetLatestProductsChange(@Field("sort") String sort,
                                              @Field("categories") String categories,
                                              @Field("filterprice") String filterprice,
                                              @Field("filterdiscount") String filterdiscount,
                                              @Field("filterrating") String filterrating,
                                              @Field("newarrivals") String newarrivals);

    //SearchFilterItem
    @FormUrlEncoded
    @POST("search")
    Call<JsonElement> GetsearchFilter(@Field("filterdiscount") String filterdiscount,
                                      @Field("filterprice") String filterprice,
                                      @Field("name") String name,
                                      @Field("categories") String categories,
                                      @Field("newarrivals") String newarrivals,
                                      @Field("filterrating") String filterrating,
                                      @Field("sort") String sort);


    //BestSellingFilter
    @FormUrlEncoded
    @POST("menu/best-selling-product")
    Call<JsonElement> GetBestSellingChange(@Field("sort") String sort,
                                           @Field("categories") String categories,
                                           @Field("filterprice") String filterprice,
                                           @Field("filterdiscount") String filterdiscount,
                                           @Field("filterrating") String filterrating,
                                           @Field("newarrivals") String newarrivals);
//BestdealFilter
    @FormUrlEncoded
    @POST("menu/order-now")
    Call<JsonElement> GetBestdealChange(@Field("sort") String sort,
                                           @Field("categories") String categories,
                                           @Field("filterprice") String filterprice,
                                           @Field("filterdiscount") String filterdiscount,
                                           @Field("filterrating") String filterrating,
                                           @Field("newarrivals") String newarrivals);

    //findItem
    @FormUrlEncoded
    @POST("findItem")
    Call<JsonElement> GetDishItemsFilter(@Field("filterdiscount") String filterdiscount,
                                         @Field("filterprice") String filterprice,
                                         @Field("categories") String categories,
                                         @Field("newarrivals") String newarrivals,
                                         @Field("filterrating") String filterrating,
                                         @Field("perPage") String perPage,
                                         @Field("sort") String sort,
                                         @Field("is_user") String role,
                                         @Field("screen_id") String screen_id,
                                         @Field("latitude") String latitude,
                                         @Field("longitude") String longitude);

    @FormUrlEncoded
    @POST("guest-register")
    Call<JsonElement> guestregister(@Field("unique_id") String unique_id);

}




