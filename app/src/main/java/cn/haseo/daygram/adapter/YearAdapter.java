package cn.haseo.daygram.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import cn.haseo.daygram.App;
import cn.haseo.daygram.R;

/**
 * 年份的 RecyclerView 适配器
 */
public class YearAdapter extends RecyclerView.Adapter<YearAdapter.ViewHolder> {
    // 获取 Application 实例
    public App app = App.getInstance();

    // 创建年份集合
    private List<Integer> years = new ArrayList<>();
    // 定义 RecyclerView 上下文
    private Context context;
    // 定义当前的年份
    private int currentYear;
    // 定义用户所选择的年份
    private int selectedYear;

    // 定义 RecyclerView 子项点击事件的接口变量
    private OnItemClickListener onItemClickListener;


    /**
     * 该方法设置 RecyclerView 子项点击事件的监听器
     *
     * @param listener：子项点击事件的接口变量
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }


    /**
     * 构造方法
     *
     * @param context：RecyclerView 的上下文
     * @param currentYear：当前年份
     * @param selectedYear：用户选择的年份
     */
    public YearAdapter(Context context, int currentYear, int selectedYear) {
        this.context = context;
        this.currentYear = currentYear;
        this.selectedYear = selectedYear;

        for (int i = 2010; i <= currentYear; i++) {
            years.add(i);
        }
    }

    /**
     * 该方法创建 RecyclerView 的子项视图及其持有者
     *
     * @param parent：代表适配器所绑定的 RecyclerView
     * @param viewType：代表子项的类型
     * @return 创建好的子项持有者
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 创建子项
        View view = LayoutInflater.from(context).inflate(R.layout.year_item, parent, false);

        // 创建子项持有者
        ViewHolder viewHolder = new ViewHolder(view);

        // 设置年份按钮的字体
        viewHolder.selectYear.setTypeface(app.getTypeface("Arvil_Sans"));

        // 返回子项持有者
        return viewHolder;
    }

    /**
     * 该方法绑定子项持有者
     *
     * @param viewHolder：代表子项持有者
     * @param position：代表子项的位置
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        // 获取子项所对应的年份
        final int year = years.get(position);

        // 设置年份按钮的点击事件
        viewHolder.selectYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(year);
            }
        });


        // 获取年份按钮的布局参数，控件的布局参数必须与父布局一样，父布局为 LinearLayout，故转换成 LinearLayout.LayoutParams
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) viewHolder.selectYear.getLayoutParams();

        // 当年份位于 2010 年 和 当前年份之间时
        if (year > 2010 && year < currentYear) {
            // 设置年份按钮的外偏距都为 0
            layoutParams.setMargins(0, 0, 0, 0);
        } else {
            // 当年份等于 2010 年时
            if (year == 2010) {
                // 设置年份按钮的左外偏距为 15 dp
                layoutParams.setMargins(app.dpToPx(15.0f), 0, 0, 0);
            } else {
                // 当年份等于当前年份时，设置年份按钮的右外偏距为 15 dp
                layoutParams.setMargins(0, 0, app.dpToPx(15.0f), 0);
            }
        }
        // 将布局参数应用到按钮
        viewHolder.selectYear.setLayoutParams(layoutParams);


        // 设置年份按钮的文本
        viewHolder.selectYear.setText(String.valueOf(year));

        // 当按钮对应的年份不是用户当前选中的年份时
        if (year != selectedYear) {
            // 设置按钮的文本颜色
            viewHolder.selectYear.setTextColor(Color.parseColor("#81817F"));
        } else {
            // 当按钮对应的年份为用户当前选中的年份时，设置按钮的文本颜色
            viewHolder.selectYear.setTextColor(Color.parseColor("#252525"));
        }
    }

    /**
     * 该方法获取 RecyclerView 的子项数目
     *
     * @return 年份集合的长度
     */
    @Override
    public int getItemCount() {
        return years.size();
    }


    /**
     * 自定义 ViewHolder
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        // 定义显示年份的按钮
        Button selectYear;

        /**
         * 构造方法
         *
         * @param itemView：RecyclerView 子项
         */
        ViewHolder(View itemView) {
            super(itemView);
            // 获取显示年份的按钮实例
            selectYear = itemView.findViewById(R.id.select_year);
        }
    }

    /**
     * 定义 RecyclerView 子项点击事件的监听接口
     */
    public interface OnItemClickListener {
        // 执行点击时的操作
        void onItemClick(int selectedYear);
    }
}
