package st.teamcataly.turistademanila.data;

import java.io.Serializable;

/**
 * @author A-Ar Andrew Concepcion
 * @createdOn 24/07/2017
 */
public class GeometricLocation implements Serializable {
    private double lat;
    private double lng;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
