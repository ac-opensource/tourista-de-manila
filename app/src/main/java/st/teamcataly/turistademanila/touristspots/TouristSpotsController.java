package st.teamcataly.turistademanila.touristspots;

import android.content.Context;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.airbnb.epoxy.EpoxyController;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import st.teamcataly.turistademanila.PlaceItemBindingModel_;
import st.teamcataly.turistademanila.R;
import st.teamcataly.turistademanila.ReportItemBindingModel_;
import st.teamcataly.turistademanila.customview.FilterViewModel_;
import st.teamcataly.turistademanila.customview.HeaderViewModel_;
import st.teamcataly.turistademanila.customview.ReportViewModel_;
import st.teamcataly.turistademanila.data.POI;
import st.teamcataly.turistademanila.placedetails.PlaceDetailsActivity;

public class TouristSpotsController extends EpoxyController {
    private final boolean isFromItinerary;
    private final AdapterCallbacks adapterCallbacks;
    private boolean isReport;

    public interface AdapterCallbacks {
        void onPlaceClicked(POI poi);
        void onChipClicked(String chip);
        void onItemLongClicked(POI poi);
        void onFilterClicked(String filter);
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

    public void setIsReport(boolean isReport) {
        this.isReport = isReport;
    }

    @Override
    protected void buildModels() {

        if (!isFromItinerary) {
            new FilterViewModel_()
                    .id("filter")
                    .listener(adapterCallbacks)
                    .spanSizeOverride((totalSpanCount, position, itemCount) -> 3)
                    .onBind((model, view, position) -> {
                        if (view.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams) {
                            StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
                            layoutParams.setFullSpan(true);
                        }
                    })
                    .addTo(this);
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
        } else {
            new ReportViewModel_()
                    .id("report")
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

            if (isFromItinerary) {
                if (isReport) {
                    if (poi.getPlannedDate() < System.currentTimeMillis()) {
                        createReportItem(poi);
                    }

                } else {
                    if (poi.getPlannedDate() >= System.currentTimeMillis()) {
                        createPlaceItem(poi);
                    }
                }
            } else {
                createPlaceItem(poi);
            }
        }

//        new LoadingModel_()
//                .id("loading model")
//                .addIf(loadingMore, this);
    }

    private void createReportItem(POI poi) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(poi.getPlannedDate());
        new ReportItemBindingModel_()
                .id(poi.getPlaceId())
                .poi(poi)
                .date(String.format("%s-%s-%s", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)))
                .clickListener(view -> {
                    if (adapterCallbacks != null) {
                        adapterCallbacks.onPlaceClicked(poi);
                    }
                    PlaceDetailsActivity.start(context, poi, isFromItinerary);
                })
                .longClickListener(view -> {
                    if (!isFromItinerary) return false;
                    if (adapterCallbacks != null) {
                        adapterCallbacks.onItemLongClicked(poi);
                    }
                    return true;
                })
                .addTo(this);
    }

    private void createPlaceItem(POI poi) {
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
                .longClickListener(view -> {
                    if (!isFromItinerary) return false;
                    if (adapterCallbacks != null) {
                        adapterCallbacks.onItemLongClicked(poi);
                    }
                    return true;
                })
                .addTo(this);
    }

    @Override
    protected void onExceptionSwallowed(RuntimeException exception) {
        super.onExceptionSwallowed(exception);
    }
}