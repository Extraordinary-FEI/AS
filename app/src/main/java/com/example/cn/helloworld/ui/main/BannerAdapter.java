package com.example.cn.helloworld.ui.main;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

        // ⭐ 强制填满 ViewPager（关键修复）
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        itemView.setLayoutParams(params);

        HomeModels.BannerItem banner = banners.get(position);

        TextView titleView = (TextView) itemView.findViewById(R.id.banner_title);
        TextView descriptionView = (TextView) itemView.findViewById(R.id.banner_description);
        ImageView bannerImage = (ImageView) itemView.findViewById(R.id.banner_image);

        titleView.setText(banner.getTitle());
        descriptionView.setText(banner.getDescription());

        bannerImage.setImageResource(banner.getImageResId());

        container.addView(itemView);
        return itemView;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
