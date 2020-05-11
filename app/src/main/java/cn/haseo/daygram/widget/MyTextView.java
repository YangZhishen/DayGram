package cn.haseo.daygram.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;
import cn.haseo.daygram.App;
import cn.haseo.daygram.R;

/**
 * 自定义 TextView
 */
public class MyTextView extends AppCompatTextView {

    public MyTextView(Context context) {
        super(context);
    }

    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public MyTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    // 应用 xml 中所设定的字体
    private void initView(Context context, AttributeSet attrs) {
        // 获取 xml 中自定义属性的集合
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyTextView);
        // 从自定义属性中取出自定义字体的名称
        String fontName = typedArray.getString(0);
        // 设置对应的字体
        if (fontName != null) {
            setTypeface(App.getInstance().getTypeface(fontName));
        }
        // 回收自定义属性的集合
        typedArray.recycle();
    }
}
