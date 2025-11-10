package com.example.cn.helloworld.ui.main;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.cn.helloworld.R;

import java.util.List;

/**
 * 简单的轮播适配器，当前仅展示标题与描述。
 */
public class BannerAdapter extends PagerAdapter {

    private final List<HomeModels.BannerItem> banners;
    private final LayoutInflater inflater;
    private final Context context;

    public BannerAdapter(Context context, List<HomeModels.BannerItem> banners) {
        this.context = context;
        this.banners = banners;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return banners == null ? 0 : banners.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = inflater.inflate(R.layout.item_banner, container, false);
        HomeModels.BannerItem banner = banners.get(position);

        TextView titleView = itemView.findViewById(R.id.banner_title);
        TextView descriptionView = itemView.findViewById(R.id.banner_description);
        FrameLayout root = itemView.findViewById(R.id.banner_root);

        titleView.setText(banner.getTitle());
        descriptionView.setText(banner.getDescription());

        Drawable background = root.getBackground();
        if (background instanceof GradientDrawable) {
            ((GradientDrawable) background.mutate()).setColor(
                    ContextCompat.getColor(context, banner.getBackgroundColorResId()));
        }

        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
