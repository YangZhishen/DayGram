package cn.haseo.daygram.widget;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

import androidx.annotation.NonNull;

/**
 * 该类用于解决 SpannableString、SpannableStringBuilder 无法使用自定义字体的问题
 */
public class MySpan extends MetricAffectingSpan {
    // 定义字体
    private final Typeface typeface;


    /**
     * 构造方法
     *
     * @param typeface：字体
     */
    public MySpan(final Typeface typeface) {
        this.typeface = typeface;
    }


    @Override
    public void updateDrawState(final TextPaint drawState) {
        apply(drawState);
    }

    @Override
    public void updateMeasureState(@NonNull final TextPaint paint) {
        apply(paint);
    }

    private void apply(final Paint paint) {
        final Typeface oldTypeface = paint.getTypeface();
        final int oldStyle = oldTypeface != null ? oldTypeface.getStyle() : 0;
        final int fakeStyle = oldStyle & ~typeface.getStyle();
        if ((fakeStyle & Typeface.BOLD) != 0) {
            paint.setFakeBoldText(true);
        }
        if ((fakeStyle & Typeface.ITALIC) != 0) {
            paint.setTextSkewX(-0.25f);
        }
        paint.setTypeface(typeface);
    }
}
