package com.example.cn.helloworld.ui.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cn.helloworld.R;
import java.util.List;

public class BannerAdapter extends PagerAdapter {

    private static final int FALLBACK_START_COLOR = Color.parseColor("#2D234F");
    private static final int FALLBACK_END_COLOR = Color.parseColor("#181332");

    private final List<HomeModels.BannerItem> banners;
    private final LayoutInflater inflater;
    private final int[] startColors;
    private final int[] endColors;
    private OnGradientUpdateListener gradientUpdateListener;

    public BannerAdapter(Context context, List<HomeModels.BannerItem> banners) {
        this.banners = banners;
        this.inflater = LayoutInflater.from(context);
        int size = banners == null ? 0 : banners.size();
        this.startColors = new int[size];
        this.endColors = new int[size];
    }

    public void setGradientUpdateListener(OnGradientUpdateListener listener) {
        this.gradientUpdateListener = listener;
    }

    @Override
    public int getCount() {
        return banners == null ? 0 : banners.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View item = inflater.inflate(R.layout.item_banner, container, false);

        ImageView img = (ImageView) item.findViewById(R.id.banner_image);
        TextView title = (TextView) item.findViewById(R.id.banner_title);
        TextView desc = (TextView) item.findViewById(R.id.banner_description);
        TextView tag = (TextView) item.findViewById(R.id.banner_tag);
        TextView cta = (TextView) item.findViewById(R.id.banner_cta);

        HomeModels.BannerItem banner = banners.get(position);

        img.setImageResource(banner.getImageResId());
        applyImageBackgroundGradient(item, banner.getImageResId(), position);
        title.setText(banner.getTitle());
        desc.setText(banner.getDescription());
        tag.setText(banner.getTag());
        cta.setText(banner.getCta());

        // ⭐ 高级居中轻缩放
        item.setScaleX(0.92f);
        item.setScaleY(0.92f);

        container.addView(item);
        return item;
    }

    public void dispatchGradientForPosition(int position) {
        if (gradientUpdateListener == null) {
            return;
        }
        int start = FALLBACK_START_COLOR;
        int end = FALLBACK_END_COLOR;
        if (position >= 0 && position < startColors.length && endColors.length > position) {
            if (startColors[position] != 0 && endColors[position] != 0) {
                start = startColors[position];
                end = endColors[position];
            }
        }
        gradientUpdateListener.onGradientReady(start, end, position);
    }

    private void applyImageBackgroundGradient(final View item, int imageResId, final int position) {
        setFallbackGradient(item, position);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inSampleSize = 4;

        final Bitmap bitmap = BitmapFactory.decodeResource(item.getResources(), imageResId, options);
        if (bitmap == null) {
            return;
        }

        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                int startColor = palette.getVibrantColor(palette.getLightVibrantColor(FALLBACK_START_COLOR));
                int endColor = palette.getDarkVibrantColor(palette.getMutedColor(FALLBACK_END_COLOR));

                GradientDrawable gradient = new GradientDrawable(
                        GradientDrawable.Orientation.LEFT_RIGHT,
                        new int[]{startColor, endColor});
                gradient.setCornerRadius(0f);
                ViewCompat.setBackground(item, gradient);

                cacheColors(position, startColor, endColor);
                if (gradientUpdateListener != null) {
                    gradientUpdateListener.onGradientReady(startColor, endColor, position);
                }

                if (!bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            }
        });
    }

    private void setFallbackGradient(View item, int position) {
        GradientDrawable gradient = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{FALLBACK_START_COLOR, FALLBACK_END_COLOR});
        gradient.setCornerRadius(0f);
        ViewCompat.setBackground(item, gradient);

        cacheColors(position, FALLBACK_START_COLOR, FALLBACK_END_COLOR);
        if (gradientUpdateListener != null) {
            gradientUpdateListener.onGradientReady(FALLBACK_START_COLOR, FALLBACK_END_COLOR, position);
        }
    }

    private void cacheColors(int position, int startColor, int endColor) {
        if (position < 0 || position >= startColors.length) {
            return;
        }
        startColors[position] = startColor;
        endColors[position] = endColor;
    }

    public interface OnGradientUpdateListener {
        void onGradientReady(int startColor, int endColor, int position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
