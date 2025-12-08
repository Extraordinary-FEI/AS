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

public class BannerAdapter extends PagerAdapter {

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

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
