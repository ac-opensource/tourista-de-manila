package st.teamcataly.turistademanila.databinding;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.databinding.BindingAdapter;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.crashlytics.android.Crashlytics;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class ImageViewBindingAdapters {

    /** Prevent instantiation **/
    private ImageViewBindingAdapters() {}

    @BindingAdapter({"bind:imageUrl", "bind:isFitCenter", "bind:withColorPalette"})
    public static void loadImage(ImageView view, String imageUrl, boolean isFitCenter, boolean withColorPalette) {
        try {
            Activity activity = getActivityFromView(view);

            if(!activity.isFinishing()) {
                DrawableTypeRequest<String> request = Glide.with(activity).load(imageUrl);
                if(isFitCenter) request.fitCenter();
                if(withColorPalette) {
                    request.asBitmap().into(new PrimaryPaletteBackgroundTarget(view, imageUrl));
                } else {
                    request.into(view);
                }
            }
        } catch(ClassCastException e) {
            Crashlytics.logException(e);
        }
    }

    @BindingAdapter({"bind:imageUrl", "bind:cornerRadius"})
    public static void loadImage(ImageView view, String imageUrl, float cornerRadius) {
        try {
            Activity activity = getActivityFromView(view);
            if(!activity.isFinishing()) {
                Glide.with(activity)
                        .load(imageUrl)
                        .bitmapTransform(new CenterCrop(activity), new RoundedCornersTransformation(activity, Math.round(cornerRadius), 0))
                        .into(view);
            }
        } catch(ClassCastException e) {
            Crashlytics.logException(e);
        }
    }

    @BindingAdapter({"bind:imageUrl"})
    public static void loadImage(ImageView view, String imageUrl) {
        try {
            Activity activity = getActivityFromView(view);
            if(!activity.isFinishing()) {
                Glide.with(activity).load(imageUrl).into(view);
            }
        } catch(ClassCastException e) {
            Crashlytics.logException(e);
        }
    }

    @BindingAdapter({"bind:imageUri"})
    public static void loadImage(ImageView view, Uri imageUri) {
        try {
            Activity activity = getActivityFromView(view);
            if(!activity.isFinishing()) {
                Glide.with(activity).load(imageUri).into(view);
            }
        } catch(ClassCastException e) {
            Crashlytics.logException(e);
        }
    }

    @BindingAdapter({"bind:imageResource"})
    public static void loadImage(ImageView view, int res) {
        view.setBackgroundResource(res);
    }

    private static Activity getActivityFromView(View view) throws ClassCastException {
        Context context = view.getContext();

        if(context instanceof Activity) return (Activity) context;

        while(context instanceof ContextWrapper) {
            if(context instanceof Activity) return (Activity) context;
            context = ((ContextWrapper) context).getBaseContext();
        }

        throw new ClassCastException("Can't find Activity from view");
    }
}