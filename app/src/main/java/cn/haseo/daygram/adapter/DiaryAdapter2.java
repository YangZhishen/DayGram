package cn.haseo.daygram.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import androidx.core.content.ContextCompat;
import cn.haseo.daygram.App;
import cn.haseo.daygram.R;
import cn.haseo.daygram.model.Diary;
import cn.haseo.daygram.util.ThemeUtil;
import cn.haseo.daygram.widget.MySpan;

/**
 * 类型 2 的 ListView 适配器
 */
public class DiaryAdapter2 extends BaseAdapter {
    // 获取 Application 实例
    private App app = App.getInstance();

    // 定义子日记集合
    private List<Diary> subDiaries;
    // 定义 ListView 上下文
    private Context context;

    /**
     * 构造方法
     *
     * @param context：ListView 的上下文
     * @param subDiaries：子日记集合
     */
    public DiaryAdapter2(Context context, List<Diary> subDiaries) {
        this.context = context;
        this.subDiaries = subDiaries;
    }

    /**
     * 该方法获取 ListView 的子项数目
     *
     * @return 子日记集合的长度
     */
    @Override
    public int getCount() {
        return subDiaries.size();
    }

    /**
     * 该方法创建 ListView 的子项视图
     *
     * @param position：代表子项的位置（不包含头布局）
     * @param convertView：代表子项的缓存
     * @param parent：代表适配器所绑定的         ListView
     * @return 创建好的子项
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 获取子项所对应的日记
        Diary diary = subDiaries.get(position);

        // 定义子项
        View itemView;
        // 定义子项持有者
        ViewHolder viewHolder;

        // 当子项缓存为空时
        if (convertView == null) {
            // 创建子项
            itemView = LayoutInflater.from(context).inflate(R.layout.diary_item2, parent, false);
            // 创建子项持有者
            viewHolder = new ViewHolder(itemView);
            // 绑定子项持有者
            itemView.setTag(viewHolder);
        } else {
            // 当子项缓存不为空时，复用子项缓存
            itemView = convertView;
            // 取出绑定的子项持有者
            viewHolder = (ViewHolder) itemView.getTag();
        }

        // 将日记日期字符串化
        String day = String.valueOf(diary.getDay()) + " ";
        // 获取日记的星期
        String week = app.getWeek(diary.getWeek());
        // 整合需要预览的内容
        String preview = day + week + " / " + diary.getContent();

        // 获取设置
        SharedPreferences settings = context.getSharedPreferences("settings", Context.MODE_PRIVATE);

        // 设置字体大小
        int fontSize = settings.getInt("font.size", 2);
        viewHolder.item2Text.setTextSize(TypedValue.COMPLEX_UNIT_SP, app.getTextSize(fontSize));

        // 获取主题索引
        int themeIndex = settings.getInt("theme.index", 0);
        // 根据主题索引设置字体颜色
        int textColor = ThemeUtil.getTextColor(themeIndex);
        viewHolder.item2Text.setTextColor(textColor);

        // 创建 SpannableString 变量 style
        SpannableString style = new SpannableString(preview);


        // 设置日期、星期的字体大小
        style.setSpan(new RelativeSizeSpan(1.0f), 0, day.length() + week.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        // 设置日期、星期的字体样式
        style.setSpan(new MySpan(app.getTypeface("Georgia-Bold")),
                0, day.length() + week.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        // 设置日记内容的字体样式
        style.setSpan(new MySpan(app.getTypeface("Georgia")),
                day.length() + week.length(), preview.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        // 当日记为星期天的日记时
        if (diary.getWeek() == 1) {
            // 将星期的字体颜色设置为红色
            style.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.red)), day.length(),
                    day.length() + week.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        } else {
            // 当日记不是星期天的日记时，将星期的字体颜色设置为黑色
            style.setSpan(new ForegroundColorSpan(textColor), day.length(),
                    day.length() + week.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }

        // 应用字体样式
        viewHolder.item2Text.setText(style);

        // 返回创建好的子项
        return itemView;
    }

    /**
     * 该方法获取 ListView 子项所对应的数据
     *
     * @param position：代表子项的位置（不包含头布局）
     * @return 子项所对应的数据
     */
    @Override
    public Object getItem(int position) {
        return subDiaries.get(position);
    }

    /**
     * 该方法获取 ListView 子项的 id
     *
     * @param position：代表子项的位置（不包含头布局）
     * @return 子项的 id
     */
    @Override
    public long getItemId(int position) {
        return position;
    }


    /**
     * 自定义的 ViewHolder
     */
    private static class ViewHolder {
        // 定义类型 2 的 TextView
        TextView item2Text;

        /**
         * 构造方法
         *
         * @param itemView：ListView 子项
         */
        ViewHolder(View itemView) {
            // 实例化预览日记的 TextView
            item2Text = itemView.findViewById(R.id.item2_text);
        }
    }
}
