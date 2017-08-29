package st.teamcataly.turistademanila.data;

/**
 * @author A-Ar Andrew Concepcion
 * @createdOn 31/07/2017
 */
public class ViewPort {
    private GeometricLocation northeast;
    private GeometricLocation southwest;

    public GeometricLocation getNortheast() {
        return northeast;
    }

    public void setNortheast(GeometricLocation northeast) {
        this.northeast = northeast;
    }

    public GeometricLocation getSouthwest() {
        return southwest;
    }

    public void setSouthwest(GeometricLocation southwest) {
        this.southwest = southwest;
    }
}
