package st.teamcataly.turistademanila.data;

import java.io.Serializable;

/**
 * @author A-Ar Andrew Concepcion
 * @createdOn 24/07/2017
 */
public class Geometry implements Serializable {
    private GeometricLocation location;
    private ViewPort viewPort;

    public GeometricLocation getLocation() {
        return location;
    }

    public void setLocation(GeometricLocation location) {
        this.location = location;
    }

    public ViewPort getViewPort() {
        return viewPort;
    }

    public void setViewPort(ViewPort viewPort) {
        this.viewPort = viewPort;
    }
}
