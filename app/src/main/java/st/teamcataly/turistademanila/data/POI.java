package st.teamcataly.turistademanila.data;

import com.squareup.moshi.Json;

import java.io.Serializable;
import java.util.List;

/**
 * @author A-Ar Andrew Concepcion
 * @createdOn 24/07/2017
 */
public class POI implements Serializable {

    private Geometry geometry;
    private String name;
    private Float rating;
    private List<Photo> photos;
    @Json(name = "formatted_address") private String formattedAddress;
    @Json(name = "place_id") private String placeId;
    private List<String> types;
    private int weight;

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }
}
