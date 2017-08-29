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
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import st.teamcataly.turistademanila.R;
import st.teamcataly.turistademanila.api.MapsApi;
import st.teamcataly.turistademanila.api.MapsService;
import st.teamcataly.turistademanila.customview.SpaceItemDecoration;
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
}
