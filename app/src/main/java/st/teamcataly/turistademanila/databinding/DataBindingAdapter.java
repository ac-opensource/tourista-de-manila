package st.teamcataly.turistademanila.databinding;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.databinding.library.baseAdapters.BR;

import java.util.ArrayList;
import java.util.List;

public class DataBindingAdapter<T extends RecyclerViewModel> extends RecyclerView.Adapter<DataBindingAdapter.ViewHolder> {

    protected List<T> mItems;

    public DataBindingAdapter(List<T> list) {
        mItems = list;
    }

    @Override
    public DataBindingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        ViewDataBinding bind = DataBindingUtil.bind(view);
        return new ViewHolder<>(bind);
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        if(holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
            final T item = mItems.get(holder.getAdapterPosition());
            item.onRecycled(holder.getBinding()); // Perform any clean up
        }
    }

    @Override
    public void onBindViewHolder(DataBindingAdapter.ViewHolder holder, int position) {
        final T item = mItems.get(position);
        item.onBind(holder.getBinding()); // Perform any initializations
        holder.getBinding().setVariable(BR.vm, item);
        holder.getBinding().executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mItems.get(position).getLayoutId();
    }

    public void setItems(List<T> viewModels) {
        mItems = new ArrayList<>(viewModels);
        notifyDataSetChanged();
    }

    public List<T> getItems() {
        return mItems;
    }

    public static class ViewHolder<V extends ViewDataBinding> extends RecyclerView.ViewHolder {

        private V v;

        public ViewHolder(V v) {
            super(v.getRoot());
            this.v = v;
        }

        public V getBinding() {
            return v;
        }
    }
}
