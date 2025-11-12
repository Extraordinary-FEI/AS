package com.example.cn.helloworld.data.repository;

import android.content.Context;
import android.text.TextUtils;

import com.example.cn.helloworld.R;
import com.example.cn.helloworld.data.model.Category;
import com.example.cn.helloworld.data.model.Product;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * 商品仓库：
 * 管理“演唱会门票”、“应援商品”、“签名照”等数据源。
 */
public class ProductRepository {

    private final Context appContext;
    private static final Map<String, Category> categories = new LinkedHashMap<>();
    private static final Map<String, List<Product>> productsByCategory = new LinkedHashMap<>();
    private static boolean initialized = false;

    public ProductRepository(Context context) {
        this.appContext = context.getApplicationContext();
        if (!initialized) {
            seedCategories();
            seedProducts();
            initialized = true;
        }
    }

    /** 初始化分类 */
    private void seedCategories() {
        categories.clear();

        Category ticket = new Category("qianxi_ticket", "演出纪念票", R.drawable.ic_category_ticket);
        Category merch = new Category("qianxi_merch", "官方周边", R.drawable.ic_category_merch);
        Category signed = new Category("qianxi_signed", "签名照 & 拍立得", R.drawable.ic_category_signed);

        categories.put(ticket.getId(), ticket);
        categories.put(merch.getId(), merch);
        categories.put(signed.getId(), signed);
    }

    /** 初始化商品数据 */
    private void seedProducts() {
        productsByCategory.clear();

        // ① 演出纪念票
        List<Product> ticketProducts = new ArrayList<>();
        Map<String, String> ticketAttributes1 = new LinkedHashMap<>();
        ticketAttributes1.put("场次", "上海 · 8 月 18 日 19:30");
        ticketAttributes1.put("应援兑换", "凭票背面序列号领取限定灯牌");
        ticketProducts.add(new Product(
                "ticket_001",
                "2024『风与少年』巡演纪念票",
                "金属浮雕票面搭配千玺手写问候卡，收藏与观演两相宜。",
                299.0,
                R.drawable.cover_baobei,
                "qianxi_ticket",
                188,
                Arrays.asList("实名绑定", "金属浮雕"),
                Arrays.asList("见面会优先购", "现场应援打卡"),
                true,
                "2024-08-08 12:00",
                "限量 2000 套",
                ticketAttributes1));

        Map<String, String> ticketAttributes2 = new LinkedHashMap<>();
        ticketAttributes2.put("场次", "广州 · 9 月 7 日 19:30");
        ticketAttributes2.put("场馆福利", "持票可换专属场馆明信片");
        ticketProducts.add(new Product(
                "ticket_002",
                "「夏日见面会」VIP 纪念票",
                "附赠独家电子导览与后台应援小卡，背面印制专属编号。",
                520.0,
                R.drawable.cover_nishuo,
                "qianxi_ticket",
                99,
                Arrays.asList("独立编号", "电子导览"),
                Arrays.asList("彩排侧拍放送", "应援横幅共创"),
                true,
                "2024-07-20 10:00",
                "限量 999 套",
                ticketAttributes2));

        // ② 应援周边
        List<Product> merchProducts = new ArrayList<>();
        Map<String, String> merchAttr1 = new LinkedHashMap<>();
        merchAttr1.put("材质", "植绒绣线 · 夜光涂层");
        merchAttr1.put("套装内容", "围巾、徽章、同款手幅");
        merchProducts.add(new Product(
                "merch_001",
                "「光影纪」应援套装",
                "以巡演主视觉延伸设计，夜光材质点亮应援现场。",
                268.0,
                R.drawable.cover_lisao,
                "qianxi_merch",
                520,
                Arrays.asList("夜光材质", "全套周边"),
                Arrays.asList("直播房入场券", "官博晒图应援"),
                true,
                "2024-07-01 20:00",
                "限量 1314 套",
                merchAttr1));

        Map<String, String> merchAttr2 = new LinkedHashMap<>();
        merchAttr2.put("尺寸", "自由调节 54-60cm");
        merchAttr2.put("设计亮点", "云雾渐变与亲笔签名织标");
        merchProducts.add(new Product(
                "merch_002",
                "「少年出发」巡演棒球帽",
                "帽檐内侧暗藏巡演城市坐标，细节满分。",
                199.0,
                R.drawable.logo,
                "qianxi_merch",
                800,
                Arrays.asList("巡演限定", "亲笔签名织标"),
                Arrays.asList("线下快闪优先购", "数字专辑折扣码"),
                true,
                "2024-06-25 18:00",
                "限量 3000 顶",
                merchAttr2));

        // ③ 签名照 & 拍立得
        List<Product> signedProducts = new ArrayList<>();
        Map<String, String> signedAttr1 = new LinkedHashMap<>();
        signedAttr1.put("抽选规则", "下单即获编号，直播抽取 100 名");
        signedAttr1.put("照片规格", "3R 手写祝福拍立得");
        signedProducts.add(new Product(
                "signed_001",
                "千玺 TO 签拍立得",
                "每张皆由千玺亲笔签名并附手写日期，收藏纪念意义满分。",
                666.0,
                R.drawable.music_bg,
                "qianxi_signed",
                50,
                Arrays.asList("亲笔签名", "直播抽选"),
                Arrays.asList("抽选现场直播", "粉丝后援群通行证"),
                true,
                "2024-07-05 21:00",
                "限量 100 套",
                signedAttr1));

        Map<String, String> signedAttr2 = new LinkedHashMap<>();
        signedAttr2.put("收藏编号", "每套附带金属铭牌");
        signedAttr2.put("权益", "附赠高清电子图 + 语音问候");
        signedProducts.add(new Product(
                "signed_002",
                "「四叶草」典藏签名照组",
                "三张主题照搭配限定语音问候卡，背板采用镜面质感。",
                488.0,
                R.drawable.bg_banner_foreground,
                "qianxi_signed",
                120,
                Arrays.asList("典藏套组", "语音问候"),
                Arrays.asList("生日会优先预约", "粉丝邮寄惊喜"),
                true,
                "2024-08-15 19:00",
                "限量 520 套",
                signedAttr2));

        productsByCategory.put("qianxi_ticket", ticketProducts);
        productsByCategory.put("qianxi_merch", merchProducts);
        productsByCategory.put("qianxi_signed", signedProducts);
    }

    /** 获取所有分类 */
    public List<Category> getCategories() {
        return new ArrayList<>(categories.values());
    }

    /** 按分类获取商品 */
    public List<Product> getProducts(String categoryId) {
        List<Product> list = productsByCategory.get(categoryId);
        return list != null ? list : Collections.<Product>emptyList();
    }

    /** 获取单个商品 */
    public Product getProductById(String productId) {
        for (List<Product> list : productsByCategory.values()) {
            for (Product p : list) {
                if (p.getId().equals(productId)) {
                    return p;
                }
            }
        }
        return null;
    }

    /** 生成唯一商品ID */
    public String generateProductId(String prefix) {
        String base = TextUtils.isEmpty(prefix) ? "product" : prefix.toLowerCase(Locale.US);
        return base + "_" + UUID.randomUUID().toString().substring(0, 8);
    }
}
