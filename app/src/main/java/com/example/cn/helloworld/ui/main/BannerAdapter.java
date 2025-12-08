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

    public BannerAdapter(Context context, List<HomeModels.BannerItem> banners) {
        this.banners = banners;
        this.inflater = LayoutInflater.from(context);
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
        applyImageBackgroundGradient(item, banner.getImageResId());
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

    private void applyImageBackgroundGradient(final View item, int imageResId) {
        setFallbackGradient(item);

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

                if (!bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            }
        });
    }

    private void setFallbackGradient(View item) {
        GradientDrawable gradient = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{FALLBACK_START_COLOR, FALLBACK_END_COLOR});
        gradient.setCornerRadius(0f);
        ViewCompat.setBackground(item, gradient);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
