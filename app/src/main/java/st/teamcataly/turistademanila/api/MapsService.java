package st.teamcataly.turistademanila.api;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;
import st.teamcataly.turistademanila.data.MapQueryResult;

/**
 * @author A-Ar Andrew Concepcion
 * @createdOn 24/07/2017
 */
public interface MapsService {

    @GET("place/textsearch/json")
    Observable<MapQueryResult> queryPlacesOfInterest(@Query("key") String key,
                                                     @Query("query") String query,
                                                     @Query("pagetoken") String pageToken);

}
