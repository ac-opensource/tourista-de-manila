package st.teamcataly.turistademanila.touristspots;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;

import com.adroitandroid.chipcloud.ChipCloud;
import com.androidhuman.rxfirebase2.database.RxFirebaseDatabase;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import st.teamcataly.turistademanila.R;
import st.teamcataly.turistademanila.api.FirebaseApi;
import st.teamcataly.turistademanila.api.MapsApi;
import st.teamcataly.turistademanila.api.MapsService;
import st.teamcataly.turistademanila.customview.SpaceItemDecoration;
import st.teamcataly.turistademanila.data.Feedback;
import st.teamcataly.turistademanila.data.MapQueryResult;
import st.teamcataly.turistademanila.data.POI;

public class TouristSpotsActivity extends AppCompatActivity implements MaterialSearchBar.OnSearchActionListener, TouristSpotsController.AdapterCallbacks {
    private final static Handler MAIN_THREAD_HANDLER = new Handler(Looper.getMainLooper());
    private RecyclerView recyclerView;
    private MaterialSearchBar searchBar;
    private ChipCloud chipCloud;
    private TouristSpotsController controller;
    private MapsService mapsService;
    private List<POI> results = new ArrayList<>();
    private String category = "";
    private String searchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourist_spots);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mapsService = MapsApi.getInstance().getMapsService();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        searchBar = (MaterialSearchBar) findViewById(R.id.searchBar);

        searchBar.setHint("Search...");
        searchBar.setSpeechMode(false);
        searchBar.setOnSearchActionListener(this);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.material_baseline_grid_1x)));
        recyclerView.setLayoutManager(layoutManager);

        controller = new TouristSpotsController(this, this, false);
        recyclerView.setAdapter(controller.getAdapter());

        loadSpots(null, "", "");

    }

    private void loadSpots(String nextPageToken, String searchQuery, String category) {
//        viewModels.clear();
        loadTouristSpots(nextPageToken, searchQuery, category)
                .filter(mapQueryResult -> mapQueryResult != null)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mapQueryResult -> {
                    results.addAll(mapQueryResult.getResults());
                    controller.setPoiList(results);

                    if (!TextUtils.isEmpty(mapQueryResult.getNextPageToken())) {
                        loadSpots(mapQueryResult.getNextPageToken(), searchQuery, category);
                    } else {
                        controller.requestModelBuild();
                    }
                }, throwable -> {
                    Log.e("WTF", "WTF", throwable);
                });
    }

    private Observable<MapQueryResult> loadTouristSpots(String nextPageToken, String searchQuery, String category) {
        String query = (searchQuery + " " + category + " metro manila point of interest").trim();
        return mapsService.queryPlacesOfInterest(getString(R.string.google_api_key),
                query,
                nextPageToken)
                .subscribeOn(Schedulers.io());
    }

    @Override
    public void onSearchStateChanged(boolean b) {

    }

    @Override
    public void onSearchConfirmed(CharSequence charSequence) {
        searchQuery = charSequence.toString();
        newSearch();
    }

    private void newSearch() {
        results.clear();
        controller.setPoiList(results);
        controller.requestModelBuild();
        loadSpots(null, searchQuery, category);
    }

    @Override
    public void onButtonClicked(int i) {

    }

    @Override
    public void onPlaceClicked(POI poi) {

    }

    @Override
    public void onChipClicked(String chip) {
        searchQuery = "";
        if (chip.equalsIgnoreCase("all")) {
            category = "";
        } else {
            category = chip;
        }

        onSearchConfirmed(searchQuery);
    }

    @Override
    public void onItemLongClicked(POI poi) {

    }

    @Override
    public void onFilterClicked(String filter) {
        if (filter.equals("all")) {
            controller.setPoiList(results);
            controller.requestModelBuild();
            return;
        }
        controller.setPoiList(new ArrayList<>());
        controller.requestModelBuild();
        if (filter.equals("top rated")) {
            showTopRated();
        } else {
            showMostPopular();
        }

    }

    private void showTopRated() {
        Map<String, POI> poiMap = new HashMap<>();
        Map<String, List<Float>> ratingsPerPlace = new HashMap<>();
        DatabaseReference feedbacks = FirebaseApi.getInstance().getDatabase().getReference("feedbacks");
        RxFirebaseDatabase.data(feedbacks).toObservable()
                .map(DataSnapshot::getChildren) // list of all feedbacks for all users || feedback/
                .flatMap(Observable::fromIterable)
                .map(DataSnapshot::getChildren) // feedbackPerUser || feedback/{userId}
                .flatMap(Observable::fromIterable)
                .map(snapshot -> snapshot.getValue(Feedback.class))
                .filter(feedback -> feedback.getPoi() != null)
                .subscribe(feedback -> {
                    poiMap.put(feedback.getPoi().getPlaceId(), feedback.getPoi());

                    List<Float> listOfRating = ratingsPerPlace.get(feedback.getPoi().getPlaceId());
                    if (listOfRating == null) {
                        listOfRating = new ArrayList<>();
                    }
                    listOfRating.add(feedback.getRating());
                    ratingsPerPlace.put(feedback.getPoi().getPlaceId(), listOfRating);

                }, Crashlytics::logException, () -> {
                    for (String key : poiMap.keySet()) {
                        POI poi = poiMap.get(key);
                        float rating = 0;
                        if (ratingsPerPlace.get(key) != null && ratingsPerPlace.size() >= 1) {
                            for (Float rate : ratingsPerPlace.get(key)) {
                                rating += rate;
                            }
                            rating = rating / ratingsPerPlace.size();
                        }
                        poi.setRating(rating);
                        poiMap.put(key, poi);
                    }
                    List<POI> sortedPoi = new ArrayList<>(poiMap.values());
                    Collections.sort(sortedPoi, (lhs, rhs) ->
                            Float.compare(rhs.getRating(), lhs.getRating()));
                    controller.setPoiList(sortedPoi);
                    controller.requestModelBuild();
                });
    }

    private void showMostPopular() {
        Map<String, POI> poiMap = new HashMap<>();
        List<String> ids = new ArrayList<>();
        DatabaseReference itineraries = FirebaseApi.getInstance().getDatabase().getReference("itinerary");
        RxFirebaseDatabase.data(itineraries).toObservable()
                .map(DataSnapshot::getChildren) // list of all itinerary for all users || itinerary/
                .flatMap(Observable::fromIterable)
                .map(DataSnapshot::getChildren) // itineraryPerUser || itinerary/{userId}
                .flatMap(Observable::fromIterable)
                .map(snapshot -> snapshot.getValue(POI.class))
                .subscribe(poi -> {
                    poiMap.put(poi.getPlaceId(), poi);
                    ids.add(poi.getPlaceId());
                }, Crashlytics::logException, () -> {
                    for (String key : poiMap.keySet()) {
                        POI poi = poiMap.get(key);
                        poi.setWeight(Collections.frequency(ids, poi.getPlaceId()));
                        poiMap.put(key, poi);
                    }
                    List<POI> sortedPoi = new ArrayList<>(poiMap.values());
                    Collections.sort(sortedPoi, (lhs, rhs) -> Integer.compare(rhs.getWeight(), lhs.getWeight()));
                    controller.setPoiList(sortedPoi);
                    controller.requestModelBuild();
                });
    }
}
