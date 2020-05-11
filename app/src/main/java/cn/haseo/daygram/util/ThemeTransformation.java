package cn.haseo.daygram.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.DisplayMetrics;

import com.squareup.picasso.Transformation;

/**
 * 该类根据屏幕大小来调整主题的大小
 */
public class ThemeTransformation implements Transformation {
    // 定义上下文
    private Context context;
    // 定义主题编号
    private final String themeNum;


    /**
     * 构造方法
     *
     * @param context：上下文
     * @param themeIndex：主题索引
     */
    public ThemeTransformation(Context context, int themeIndex) {
        // 设置上下文
        this.context = context;
        // 设置主题编号
        themeNum = "Theme" + themeIndex;
    }

    /**
     * 该方法对主题进行处理
     *
     * @param source：主题
     * @return 处理后的主题
     */
    @Override
    public Bitmap transform(Bitmap source) {
        // 创建 DisplayMetrics 变量用于存放屏幕信息（DisplayMetrics 是 Android 提供的记述屏幕信息的类）
        DisplayMetrics dm = new DisplayMetrics();
        // 获取屏幕的相关信息并赋值给变量 dm
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);

        // 获取屏幕宽度
        int screenWidth = dm.widthPixels;
        // 获取屏幕高度
        int screenHeight = dm.heightPixels;
        // 获取屏幕的宽高比
        float scale1 = (float) screenWidth / screenHeight;

        // 获取主题的宽度
        int sourceWidth = source.getWidth();
        // 获取主题的高度
        int sourceHeight = source.getHeight();
        // 获取主题的宽高比
        float scale2 = (float) sourceWidth / sourceHeight;

        // 以屏幕的宽高创建画布
        Bitmap createBitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);

        // 定义偏移量
        int offset;
        // 当主题的宽高比例大于屏幕的宽高比例时
        if (scale2 > scale1) {
            // 计算宽度方向上的偏移量
            offset = (int) ((sourceWidth - scale1 * sourceHeight) / 2.0f);
            // 把主题居中画在画布上
            canvas.drawBitmap(source, new Rect(offset, 0, sourceWidth - offset, sourceHeight), new Rect(0, 0, screenWidth, screenHeight), null);
        } else if (scale2 < scale1) {
            // 当主题的宽高比例小于屏幕的宽高比例时，计算高度方向上的偏移量
            offset = (int) ((sourceHeight - sourceWidth / scale1) / 2.0f);
            // 把主题居中画在画布上
            canvas.drawBitmap(source, new Rect(0, offset, sourceWidth, sourceHeight - offset), new Rect(0, 0, screenWidth, screenHeight), null);
        } else {
            canvas.drawBitmap(source, new Rect(0, 0, sourceWidth, sourceHeight), new Rect(0, 0, screenWidth, screenHeight), null);
        }
        // 回收原始主题
        source.recycle();

        // 返回处理好的主题
        return createBitmap;
    }

    /**
     * 该方法返回主题编号
     *
     * @return 主题编号
     */
    @Override
    public String key() {
        return themeNum;
    }
}
