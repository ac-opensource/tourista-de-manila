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

@ModelView(defaultLayout = R.layout.filter_view)
public class FilterView extends LinearLayout {

    private ChipCloud chipCloud;
    private String[] filter = new String[]{
            "all",
            "most popular",
            "top rated"
    };

    public FilterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        View view = inflate(getContext(), R.layout.tag_cloud, this);
        chipCloud = (ChipCloud) view.findViewById(R.id.chipCloud);
        chipCloud.addChips(filter);
    }

    @ModelProp(options = ModelProp.Option.DoNotHash)
    public void setListener(TouristSpotsController.AdapterCallbacks adapterCallbacks) {
        chipCloud.setChipListener(new ChipListener() {
            @Override
            public void chipSelected(int i) {
                if (adapterCallbacks != null) {
                    adapterCallbacks.onFilterClicked(filter[i]);
                }
            }

            @Override
            public void chipDeselected(int i) {

            }
        });
    }

}