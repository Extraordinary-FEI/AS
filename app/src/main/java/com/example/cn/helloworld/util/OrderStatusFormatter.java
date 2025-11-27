package com.example.cn.helloworld.util;

import android.content.Context;
import android.text.TextUtils;

import com.example.cn.helloworld.R;

import java.util.Locale;

/**
 * Utility to convert stored order statuses into localized labels for display.
 */
public final class OrderStatusFormatter {

    private OrderStatusFormatter() {
        // no-op
    }

    public static String format(Context context, String rawStatus) {
        if (context == null) {
            return rawStatus;
        }
        if (TextUtils.isEmpty(rawStatus)) {
            return context.getString(R.string.order_status_unknown);
        }
        String normalized = rawStatus.trim().toUpperCase(Locale.getDefault());
        if ("CREATED".equals(normalized) || "已创建".equals(rawStatus)) {
            return context.getString(R.string.order_status_created);
        }
        if ("PAID".equals(normalized) || "已付款".equals(rawStatus)) {
            return context.getString(R.string.order_status_paid);
        }
        if ("SHIPPED".equals(normalized) || "已发货".equals(rawStatus)) {
            return context.getString(R.string.order_status_shipped);
        }
        if ("FULFILLED".equals(normalized) || "已完成".equals(rawStatus)) {
            return context.getString(R.string.order_status_fulfilled);
        }
        if ("CANCELLED".equals(normalized) || "已取消".equals(rawStatus)) {
            return context.getString(R.string.order_status_cancelled);
        }
        return rawStatus;
    }
}
