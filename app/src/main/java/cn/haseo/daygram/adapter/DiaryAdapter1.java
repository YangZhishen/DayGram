package cn.haseo.daygram.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.List;

import androidx.core.content.ContextCompat;
import cn.haseo.daygram.App;
import cn.haseo.daygram.model.Diary;
import cn.haseo.daygram.R;
import cn.haseo.daygram.util.ThemeUtil;
import cn.haseo.daygram.widget.MyListView;
import cn.haseo.daygram.widget.MyTextView;

/**
 * 类型 1 的 ListView 适配器
 */
public class DiaryAdapter1 extends BaseAdapter {
    // 获取 Application 实例
    private App app = App.getInstance();

    // 定义全日记集合
    private List<Diary> diaries;
    // 定义 ListView 的上下文
    private Context context;


    /**
     * 构造方法
     *
     * @param context：ListView 的上下文
     * @param diaries：全日记集合
     */
    public DiaryAdapter1(Context context, List<Diary> diaries) {
        this.context = context;
        this.diaries = diaries;
    }


    /**
     * 该方法获取 ListView 的子项数目
     *
     * @return 全日记集合的长度
     */
    @Override
    public int getCount() {
        return diaries.size();
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
        Diary diary = diaries.get(position);

        // 定义子项
        View view;
        // 定义子项持有者
        ViewHolder viewHolder;

        // 当子项缓存为空时
        if (convertView == null) {
            // 创建子项
            view = LayoutInflater.from(context).inflate(R.layout.diary_item1, parent, false);
            // 创建子项持有者
            viewHolder = new ViewHolder(view);
            // 绑定子项持有者
            view.setTag(viewHolder);
        } else {
            // 当子项缓存不为空时，复用子项缓存
            view = convertView;
            // 取出绑定的子项持有者
            viewHolder = (ViewHolder) view.getTag();
        }

        // 获取设置
        SharedPreferences settings = context.getSharedPreferences("settings", Context.MODE_PRIVATE);

        // 设置字体大小
        int fontSize = settings.getInt("font.size", 2);
        viewHolder.showContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, app.getTextSize(fontSize));

        // 设置默认字体
        boolean isUseSysFont = settings.getBoolean("system.font.enabled", false);
        viewHolder.showContent.setTypeface(isUseSysFont ? Typeface.DEFAULT : app.getTypeface("Georgia"));

        // 设置预览类型
        int previewType = settings.getInt("preview.type", 1);
        viewHolder.showContent.setMaxLines(previewType + 1);


        // 获取主题索引，根据主题设置圆点图片
        int themeIndex = settings.getInt("theme.index", 0);
        // 当日记为星期天的日记时
        if (diary.getWeek() == 1) {
            // 将圆点的图片设置为红色圆点
            viewHolder.dot.setImageResource(ThemeUtil.getDotId(themeIndex)[1]);
            // 将日期的字体颜色设置为红色
            viewHolder.showDay.setTextColor(ContextCompat.getColor(context, R.color.red));
        } else {
            // 当日记不是星期天的日记时，将圆点的图片设置为黑色圆点
            viewHolder.dot.setImageResource(ThemeUtil.getDotId(themeIndex)[0]);
            // 将日期的字体颜色设置为黑色
            viewHolder.showDay.setTextColor(ContextCompat.getColor(context, R.color.black));
        }

        // 当日记内容不为空时
        if (diary.getContent() != null) {
            // 隐藏圆点图片
            viewHolder.dot.setVisibility(View.GONE);
            // 显示日记预览
            viewHolder.item1_layout.setVisibility(View.VISIBLE);

            // 设置显示星期的 TextView 文本
            viewHolder.showWeek.setText(app.getWeek(diary.getWeek()).substring(0, 3));
            // 设置显示日期的 TextView 文本
            viewHolder.showDay.setText(String.valueOf(diary.getDay()));
            // 设置显示内容的 TextView 文本
            viewHolder.showContent.setText(diary.getContent());
        } else {
            // 当日记内容为空时，显示圆点图片
            viewHolder.dot.setVisibility(View.VISIBLE);
            // 隐藏日记预览
            viewHolder.item1_layout.setVisibility(View.GONE);
        }

        // 根据主题索引设置子项各控件的背景
        if (themeIndex > 0) {
            viewHolder.horizontalLine.setBackgroundColor(Color.TRANSPARENT);
            viewHolder.verticalLine.setBackgroundColor(Color.TRANSPARENT);
            viewHolder.showWeek.setBackgroundColor(Color.parseColor("#B4F2F1ED"));
            viewHolder.showDay.setBackgroundColor(Color.parseColor("#B4F2F1ED"));
            viewHolder.showContent.setBackgroundColor(Color.parseColor("#B4F2F1ED"));
            viewHolder.item1_layout.setBackgroundResource(0);
        } else {
            viewHolder.horizontalLine.setBackgroundColor(Color.parseColor("#494949"));
            viewHolder.verticalLine.setBackgroundColor(Color.parseColor("#494949"));
            viewHolder.showWeek.setBackgroundResource(R.drawable.week_bg);
            viewHolder.showDay.setBackgroundColor(Color.TRANSPARENT);
            viewHolder.showContent.setBackgroundColor(Color.TRANSPARENT);
            viewHolder.item1_layout.setBackgroundResource(R.drawable.diary_item1_bg);
        }

        // 返回创建好的子项
        return view;
    }

    /**
     * 该方法获取 ListView 子项所对应的数据
     *
     * @param position：代表子项的位置（不包含头布局）
     * @return 子项所对应的数据
     */
    @Override
    public Object getItem(int position) {
        return diaries.get(position);
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
     * 该方法重置 ListView 单条数据
     *
     * @param myListView：代表与适配器所绑定的       ListView
     * @param position：代表需要更新的子项位置（包括头布局）
     */
    public void resetItem(MyListView myListView, int position) {
        // 获取需要重置的日记
        Diary diary = diaries.get(position - 1);
        // 将其内容设置空
        diary.setContent(null);
        // 更新 ListView 单条数据
        updateItem(myListView, position, diary);
    }

    /**
     * 该方法更新 ListView 单条数据
     *
     * @param myListView：代表与适配器所绑定的       ListView
     * @param position：代表需要更新的子项位置（包括头布局）
     * @param diary：代表需要更新的日记
     */
    private void updateItem(MyListView myListView, int position, Diary diary) {
        // 将最新的日记覆盖到 ListView 的日记集合
        diaries.set(position - 1, diary);
        // 获取第一个可见的子项位置
        int firstVisiblePosition = myListView.getFirstVisiblePosition();
        // 创建需要更新的子项
        View view = myListView.getChildAt(position - firstVisiblePosition);
        // 调用 getView 更新子项
        getView(position - 1, view, myListView);
    }


    /**
     * 自定义的 ViewHolder
     */
    private static class ViewHolder {
        // 定义存放圆点图片的 ImageView
        ImageView dot;

        // 定义类型 1 的子项布局
        LinearLayout item1_layout;
        // 定义显示日记星期的 TextView
        MyTextView showWeek;
        // 定义显示日记日期的 TextView
        MyTextView showDay;
        // 定义显示日记的内容 TextView
        MyTextView showContent;
        // 水平线
        View horizontalLine;
        // 竖线
        View verticalLine;

        /**
         * 构造方法
         *
         * @param itemView：ListView 子项
         */
        ViewHolder(View itemView) {
            // 实例化各控件
            dot = itemView.findViewById(R.id.dot);
            item1_layout = itemView.findViewById(R.id.item1_layout);
            showWeek = itemView.findViewById(R.id.diary_week);
            showDay = itemView.findViewById(R.id.diary_day);
            showContent = itemView.findViewById(R.id.diary_content);
            horizontalLine = itemView.findViewById(R.id.horizontal_line);
            verticalLine = itemView.findViewById(R.id.vertical_line);
        }
    }
}