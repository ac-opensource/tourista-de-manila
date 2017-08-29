package st.teamcataly.turistademanila.placedetails;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import st.teamcataly.turistademanila.R;
import st.teamcataly.turistademanila.data.Photo;

public class ImageAdapter extends BaseAdapter {
    private final List<Photo> photoReferences;
    private Context context;

    public ImageAdapter(Context c, List<Photo> photoReferences) {
        context = c;
        this.photoReferences = photoReferences;
    }

    public int getCount() {
        return photoReferences.size();
    }

    public Object getItem(int position) {
        return photoReferences.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 170, context.getResources().getDisplayMetrics());
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(px, px));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }
        String imageUrl = context.getString(R.string.photo_request_format,
                400,
                photoReferences.get(position).getPhotoReference(),
                context.getString(R.string.google_api_key));
        Glide.with(context).load(imageUrl).into(imageView);
        return imageView;
    }
}