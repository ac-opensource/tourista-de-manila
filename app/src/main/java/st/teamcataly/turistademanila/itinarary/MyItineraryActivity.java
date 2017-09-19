package st.teamcataly.turistademanila.itinarary;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.androidhuman.rxfirebase2.database.RxFirebaseDatabase;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.database.DatabaseReference;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
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
        if (chip.equals("on going")) {
            controller.setIsReport(false);
            controller.requestModelBuild();
        } else {
            controller.setIsReport(true);
            controller.requestModelBuild();
        }

    }

    @Override
    public void onItemLongClicked(POI poi) {
        DatabaseReference myItinerary = FirebaseApi.getInstance()
                .getDatabase().getReference("itinerary")
                .child(FirebaseApi.getUser().getUid())
                .child(poi.getPlaceId());

        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
        sweetAlertDialog.setTitleText("Are you sure?")
                .setContentText("Do you want to delete this itinerary?")
                .setConfirmText("Yes")
                .setCancelText("Cancel")
                .setConfirmClickListener(sweetAlertDialog1 -> {
                    sweetAlertDialog.changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
                    RxFirebaseDatabase.removeValue(myItinerary)
                            .subscribeOn(AndroidSchedulers.mainThread())
                            .subscribe(() -> {
                                loadItineraries();
                                sweetAlertDialog.dismissWithAnimation();
                            }, Crashlytics::logException);
                });

        sweetAlertDialog.show();

    }

    @Override
    public void onFilterClicked(String filter) {

    }
}
