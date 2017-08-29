package st.teamcataly.turistademanila.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.adroitandroid.chipcloud.ChipCloud;
import com.adroitandroid.chipcloud.ChipListener;
import com.airbnb.epoxy.ModelProp;
import com.airbnb.epoxy.ModelView;

import st.teamcataly.turistademanila.R;
import st.teamcataly.turistademanila.touristspots.TouristSpotsController;

@ModelView(defaultLayout = R.layout.header_view)
public class HeaderView extends LinearLayout {

    private ChipCloud chipCloud;
    private String[] categories = new String[]{
            "all",
            "airport",
            "amusement_park",
            "aquarium",
            "art_gallery",
            "atm",
            "bank",
            "bar",
            "casino",
            "church",
            "embassy",
            "establishment",
            "food",
            "hospital",
            "lodging",
            "mosque",
            "museum",
            "night_club",
            "painter",
            "park",
            "restaurant",
            "school",
            "shopping_mall",
            "stadium",
            "store",
            "university",
            "zoo"
    };

    public HeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        View view = inflate(getContext(), R.layout.tag_cloud, this);
        chipCloud = (ChipCloud) view.findViewById(R.id.chipCloud);
        chipCloud.addChips(categories);
    }

    @ModelProp(options = ModelProp.Option.DoNotHash)
    public void setListener(TouristSpotsController.AdapterCallbacks adapterCallbacks) {
        chipCloud.setChipListener(new ChipListener() {
            @Override
            public void chipSelected(int i) {
                if (adapterCallbacks != null) {
                    adapterCallbacks.onChipClicked(categories[i]);
                }
            }

            @Override
            public void chipDeselected(int i) {

            }
        });
    }

}