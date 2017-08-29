package st.teamcataly.turistademanila.data;

import com.squareup.moshi.Json;

import java.io.Serializable;

/**
 * @author A-Ar Andrew Concepcion
 * @createdOn 24/07/2017
 */
public class Photo implements Serializable {
    private int height;
    private int width;
    @Json(name = "photo_reference") private String photoReference;

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public String getPhotoReference() {
        return photoReference;
    }

    public void setPhotoReference(String photoReference) {
        this.photoReference = photoReference;
    }
}
