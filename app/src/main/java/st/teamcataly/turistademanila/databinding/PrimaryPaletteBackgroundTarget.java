package st.teamcataly.turistademanila.databinding;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.widget.ImageView;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.HashMap;

public class PrimaryPaletteBackgroundTarget extends SimpleTarget<Bitmap> {
    private static final HashMap<String, Integer> backgroundColorCache = new HashMap<>();

    private final ImageView mBanner;
    private final String mBannerUrl;

    public PrimaryPaletteBackgroundTarget(ImageView banner, String bannerUrl) {
        mBanner = banner;
        mBannerUrl = bannerUrl;
    }

    @Override
    public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
        Integer backgroundColor = getBackgroundColor(bitmap);
        mBanner.setBackgroundColor(backgroundColor);
        mBanner.setImageBitmap(bitmap);
    }

    /**
     * @return the background color generated from the bitmap, transparent if the color is not available
     */
    @Nullable
    private int getBackgroundColor(Bitmap bitmap) {
        if (!backgroundColorCache.containsKey(mBannerUrl)) {
            Palette p = Palette.from(bitmap).generate();
            if (p.getDominantSwatch() == null) return Color.TRANSPARENT;
            backgroundColorCache.put(mBannerUrl, p.getDominantSwatch().getRgb());
        }
        return backgroundColorCache.get(mBannerUrl);
    }
}