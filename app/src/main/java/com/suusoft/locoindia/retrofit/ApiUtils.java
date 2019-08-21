package com.suusoft.locoindia.retrofit;

import com.suusoft.locoindia.AppController;
import com.suusoft.locoindia.R;

public class ApiUtils {

    public static final String BASE_URL = AppController.getInstance().getString(R.string.URL_API)+ "backend/web/index.php/api/transport/";

    public static APIService getAPIService() {

        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }
}
