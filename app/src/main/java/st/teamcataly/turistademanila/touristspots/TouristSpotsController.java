package st.teamcataly.turistademanila.touristspots;

import android.content.Context;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.EpoxyController;

import java.util.Collections;
import java.util.List;

import st.teamcataly.turistademanila.PlaceItemBindingModel_;
import st.teamcataly.turistademanila.R;
import st.teamcataly.turistademanila.customview.HeaderViewModel_;
import st.teamcataly.turistademanila.data.POI;
import st.teamcataly.turistademanila.placedetails.PlaceDetailsActivity;

public class TouristSpotsController extends EpoxyController {
    private final boolean isFromItinerary;
    private final AdapterCallbacks adapterCallbacks;

    public interface AdapterCallbacks {
        void onPlaceClicked(POI poi);
        void onChipClicked(String chip);
    }

    private final Context context;
    private List<POI> poiList = Collections.emptyList();
    private boolean loadingMore = true;

    public TouristSpotsController(Context context, AdapterCallbacks adapterCallbacks, boolean isFromItinerary) {
        this.context = context;
        this.adapterCallbacks = adapterCallbacks;
        this.isFromItinerary = isFromItinerary;
        setFilterDuplicates(true);
    }

    public void setLoadingMore(boolean loadingMore) {
        this.loadingMore = loadingMore;
        requestModelBuild();
    }

    public void setPoiList(List<POI> poiList) {
        this.poiList = poiList;
        requestModelBuild();
    }

    @Override
    protected void buildModels() {

        if (!isFromItinerary) {
            new HeaderViewModel_()
                    .id("header")
                    .listener(adapterCallbacks)
                    .spanSizeOverride((totalSpanCount, position, itemCount) -> 3)
                    .onBind((model, view, position) -> {
                        if (view.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams) {
                            StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
                            layoutParams.setFullSpan(true);
                        }
                    })
                    .addTo(this);
        }

        for (POI poi : poiList) {
            if (poi.getPhotos() == null || poi.getPhotos().isEmpty()) continue;
            String primaryPoiImage = context.getString(R.string.photo_request_format,
                    400,
                    poi.getPhotos().get(0).getPhotoReference(),
                    context.getString(R.string.google_api_key));

            new PlaceItemBindingModel_()
                    .id(poi.getPlaceId())
                    .poi(poi)
                    .placeImageUrl(primaryPoiImage)
                    .clickListener(view -> {
                        if (adapterCallbacks != null) {
                            adapterCallbacks.onPlaceClicked(poi);
                        }
                        PlaceDetailsActivity.start(context, poi, isFromItinerary);
                    })
                    .addTo(this);
        }

//        new LoadingModel_()
//                .id("loading model")
//                .addIf(loadingMore, this);
    }

    @Override
    protected void onExceptionSwallowed(RuntimeException exception) {
        super.onExceptionSwallowed(exception);
    }
}