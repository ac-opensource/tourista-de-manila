package st.teamcataly.turistademanila.itinarary;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.androidhuman.rxfirebase2.database.RxFirebaseDatabase;
import com.google.firebase.database.DatabaseReference;

import io.reactivex.Observable;
import st.teamcataly.turistademanila.R;
import st.teamcataly.turistademanila.api.FirebaseApi;
import st.teamcataly.turistademanila.customview.SpaceItemDecoration;
import st.teamcataly.turistademanila.data.POI;
import st.teamcataly.turistademanila.touristspots.TouristSpotsController;

public class MyItineraryActivity extends AppCompatActivity implements TouristSpotsController.AdapterCallbacks {
    private RecyclerView recyclerView;
    private TouristSpotsController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_itinerary);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        recyclerView.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.material_baseline_grid_1x)));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        controller = new TouristSpotsController(this, this, true);
        recyclerView.setAdapter(controller.getAdapter());

        loadItineraries();

    }

    private void loadItineraries() {
        DatabaseReference myItinerary = FirebaseApi.getInstance().getDatabase().getReference("itinerary").child(FirebaseApi.getUser().getUid());
        RxFirebaseDatabase.data(myItinerary).toObservable()
                .flatMap(dataSnapshot -> Observable.fromIterable(dataSnapshot.getChildren()))
                .map(dataSnapshot -> dataSnapshot.getValue(POI.class))
                .toList()
                .subscribe(poiList -> {
                    controller.setPoiList(poiList);
                    controller.requestModelBuild();
                });
    }

    @Override
    public void onPlaceClicked(POI poi) {

    }

    @Override
    public void onChipClicked(String chip) {

    }
}
