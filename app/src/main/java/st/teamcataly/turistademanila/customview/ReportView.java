package st.teamcataly.turistademanila.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adroitandroid.chipcloud.ChipCloud;
import com.adroitandroid.chipcloud.ChipListener;
import com.airbnb.epoxy.ModelProp;
import com.airbnb.epoxy.ModelView;

import st.teamcataly.turistademanila.R;
import st.teamcataly.turistademanila.touristspots.TouristSpotsController;

@ModelView(defaultLayout = R.layout.report_view)
public class ReportView extends LinearLayout {

    private ChipCloud chipCloud;
    private String[] filter = new String[]{
            "on going",
            "travel report"
    };

    public ReportView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        View view = inflate(getContext(), R.layout.tag_cloud, this);
        chipCloud = (ChipCloud) view.findViewById(R.id.chipCloud);
        ((TextView) view.findViewById(R.id.title)).setText("View Type");
        chipCloud.addChips(filter);
    }

    @ModelProp(options = ModelProp.Option.DoNotHash)
    public void setListener(TouristSpotsController.AdapterCallbacks adapterCallbacks) {
        chipCloud.setChipListener(new ChipListener() {
            @Override
            public void chipSelected(int i) {
                if (adapterCallbacks != null) {
                    adapterCallbacks.onChipClicked(filter[i]);
                }
            }

            @Override
            public void chipDeselected(int i) {

            }
        });
    }

}