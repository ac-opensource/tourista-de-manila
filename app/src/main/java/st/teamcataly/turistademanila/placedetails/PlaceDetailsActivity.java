package st.teamcataly.turistademanila.placedetails;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import com.androidhuman.rxfirebase2.database.RxFirebaseDatabase;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import st.teamcataly.turistademanila.R;
import st.teamcataly.turistademanila.api.FirebaseApi;
import st.teamcataly.turistademanila.data.Feedback;
import st.teamcataly.turistademanila.data.POI;
import st.teamcataly.turistademanila.databinding.PlaceDetailsActivityBinding;

public class PlaceDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private boolean isFromItinerary;
    private PlaceDetailsActivityBinding binding;
    private RatingAdapter ratingAdapter;
    public POI poi;

    public static void start(Context context, POI poi, boolean isFromItinerary) {
        Intent starter = new Intent(context, PlaceDetailsActivity.class);
        starter.putExtra("place", poi);
        starter.putExtra("isFromItinerary", isFromItinerary);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.place_details_activity);
        poi = (POI) getIntent().getSerializableExtra("place");
        isFromItinerary = getIntent().getBooleanExtra("isFromItinerary", false);
        binding.setVm(this);
        if (isFromItinerary) {
            binding.addToItinerary.setImageResource(R.drawable.ic_star_border_24dp);
        }

        List<Feedback> feedbackList = new ArrayList<>();
        ratingAdapter = new RatingAdapter(this, feedbackList);
        binding.ratingPager.setAdapter(ratingAdapter);
        binding.ratingPager.setClipToPadding(false);
        binding.ratingPager.setPageMargin(20);
        binding.ratingPager.setPadding(80, 0, 80, 0);
        DatabaseReference feedbacks = FirebaseApi.getInstance().getDatabase().getReference("feedbacks").child(poi.getPlaceId());
        RxFirebaseDatabase.dataChanges(feedbacks).subscribe(dataSnapshot -> {
            feedbackList.clear();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                Feedback feedback = snapshot.getValue(Feedback.class);
                feedbackList.add(feedback);
            }
            ratingAdapter.notifyDataSetChanged();
        });
        binding.imageGrid.setAdapter(new ImageAdapter(this, poi.getPhotos()));
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng loc = new LatLng(poi.getGeometry().getLocation().getLat(), poi.getGeometry().getLocation().getLng());
        mMap.addMarker(new MarkerOptions().position(loc).title(poi.getName()));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 16));
    }

    public void onAddToItineraryClicked(POI poi) {
        DatabaseReference feedbacks = FirebaseApi.getInstance().getDatabase().getReference("feedbacks").child(poi.getPlaceId());
        if (isFromItinerary) {
            Dialog rateDialog = new Dialog(this, R.style.FullHeightDialog);
            rateDialog.setContentView(R.layout.rating_dialog);
            rateDialog.setCancelable(true);
            RatingBar ratingBar = (RatingBar) rateDialog.findViewById(R.id.dialog_ratingbar);
            ratingBar.setRating(poi.getRating());

            Button updateButton = (Button) rateDialog.findViewById(R.id.rate_dialog_button);
            EditText feedbackForm = (EditText) rateDialog.findViewById(R.id.feedback);
            updateButton.setOnClickListener(v -> {
                Feedback feedback = new Feedback();
                feedback.setComment(feedbackForm.getText().toString());
                feedback.setRating(ratingBar.getRating());
                feedbacks.child(FirebaseApi.getUser().getUid()).setValue(feedback).addOnCompleteListener(task -> {
                    rateDialog.dismiss();
                });
            });
            //now that the dialog is set up, it's time to show it
            rateDialog.show();
        } else {
            DatabaseReference myItinerary = FirebaseApi.getInstance().getDatabase().getReference("itinerary").child(FirebaseApi.getUser().getUid());
            myItinerary.child(poi.getPlaceId()).setValue(poi).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    finish();
                }
            });
        }

    }
}
