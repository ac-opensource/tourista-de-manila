package st.teamcataly.turistademanila.data;

import com.squareup.moshi.Json;

import java.util.List;

/**
 * @author A-Ar Andrew Concepcion
 * @createdOn 24/07/2017
 */
public class MapQueryResult {
    @Json(name = "next_page_token") private String next_page_token;
    private List<POI> results;

    public String getNextPageToken() {
        return next_page_token;
    }

    public void setNextPageToken(String nextPageToken) {
        this.next_page_token = nextPageToken;
    }

    public List<POI> getResults() {
        return results;
    }

    public void setResults(List<POI> results) {
        this.results = results;
    }
}
