package st.teamcataly.turistademanila.databinding;

import android.databinding.ViewDataBinding;

public interface RecyclerViewModel extends ViewModel {
    void onRecycled(ViewDataBinding binding);

    void onBind(ViewDataBinding binding);
}
