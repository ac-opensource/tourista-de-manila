package st.teamcataly.turistademanila.api;


import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;

/**
 * @author A-Ar Andrew Concepcion
 * @createdOn 25/07/2017
 */
public class MapsApi {
    private static final MapsApi ourInstance = new MapsApi();
    private final MapsService mapsService;

    public static MapsApi getInstance() {
        return ourInstance;
    }

    public MapsService getMapsService() {
        return mapsService;
    }

    private MapsApi() {
        mapsService = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/")
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(MapsService.class);
    }
}
