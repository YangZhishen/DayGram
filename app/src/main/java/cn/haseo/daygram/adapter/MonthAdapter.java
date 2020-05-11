package cn.haseo.daygram.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import cn.haseo.daygram.App;
import cn.haseo.daygram.R;

/**
 * 月份的 RecyclerView 适配器
 */
public class MonthAdapter extends RecyclerView.Adapter<MonthAdapter.ViewHolder> {
    // 获取 Application 实例
    public App app = App.getInstance();

    // 定义月份集合
    private int[] months = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
    // 定义 RecyclerView 的上下文
    private Context context;

    // 定义当前年份
    private int currentYear;
    // 定义用户所选择的年份
    private int selectedYear;
    // 定义当前月份
    private int currentMonth;
    // 定义用户所选择的月份
    private int selectedMonth;

    // 定义 RecyclerView 子项点击事件的接口变量
    private OnItemClickListener listener;


    /**
     * 该方法设置子项点击事件的监听器
     *
     * @param listener：子项点击事件的接口变量
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    /**
     * 构造方法
     *
     * @param context：RecyclerView   的上下文
     * @param currentYear：当前年份
     * @param selectedYear：用户所选择的年份
     * @param currentMonth：当前月份
     * @param selectedMonth：用户所选择的月份
     */
    public MonthAdapter(Context context, int currentYear, int selectedYear, int currentMonth, int selectedMonth) {
        this.context = context;
        this.currentYear = currentYear;
        this.selectedYear = selectedYear;
        this.currentMonth = currentMonth;
        this.selectedMonth = selectedMonth;
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
        View view = LayoutInflater.from(context).inflate(R.layout.month_item, parent, false);

        // 返回子项持有者
        return new ViewHolder(view);
    }

    /**
     * 该方法绑定子项持有者
     *
     * @param viewHolder：代表子项持有者
     * @param position：代表子项的位置
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        // 获取子项所对应的月份
        final int month = months[position];

        // 设置月份 ImageView 的点击事件
        viewHolder.selectMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(month);
            }
        });

        // 获取 monthImage 的布局参数，控件的布局参数必须与父布局一样，父布局为 LinearLayout，故转换成 LinearLayout.LayoutParams
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) viewHolder.selectMonth.getLayoutParams();
        switch (month) {
            // 当控件为 1 月份时
            case 0:
                // 设置控件的左外偏距为 9 dp
                layoutParams.setMargins(app.dpToPx(9.0f), 0, 0, 0);
                break;
            // 当控件为 12 月份时
            case 11:
                // 设置控件的右外偏距为 9 dp
                layoutParams.setMargins(0, 0, app.dpToPx(9.0f), 0);
                break;
            // 当控件为其他年份时
            default:
                // 设置控件的外偏距都为 0
                layoutParams.setMargins(0, 0, 0, 0);
                break;
        }
        // 将布局参数应用到按钮
        viewHolder.selectMonth.setLayoutParams(layoutParams);


        // 获取对应月份的图片的 id
        int buttonId = context.getResources().getIdentifier("month_" + position + "_button", "drawable", context.getPackageName());
        // 将图片设置到 ImageButton
        viewHolder.selectMonth.setImageResource(buttonId);

        // 当用户所选择的年份是今年，并且月份大于当前的月份时
        if (selectedYear == currentYear && position > currentMonth) {
            // 将月份图片的透明度设置为100（越大越不透明，最大225）
            viewHolder.selectMonth.setImageAlpha(90);
            viewHolder.selectMonth.setEnabled(false);
        } else {
            // 将月份图片的透明度设置为225
            viewHolder.selectMonth.setImageAlpha(255);
            viewHolder.selectMonth.setEnabled(true);
        }

        // 当月份不是用户所选中的月份时
        if (position != selectedMonth) {
            viewHolder.selectMonth.setSelected(false);
        } else {
            viewHolder.selectMonth.setSelected(true);
        }
    }

    /**
     * 该方法获取 RecyclerView 的子项数目
     *
     * @return 月份数组的长度
     */
    @Override
    public int getItemCount() {
        return 12;
    }


    /**
     * 自定义 ViewHolder
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        // 定义月份的 ImageView
        ImageButton selectMonth;

        /**
         * 构造方法
         *
         * @param itemView：RecyclerView 子项
         */
        ViewHolder(View itemView) {
            super(itemView);
            selectMonth = itemView.findViewById(R.id.select_month);
        }
    }

    /**
     * 定义 RecyclerView 子项点击事件的监听接口
     */
    public interface OnItemClickListener {
        // 执行点击时的操作
        void onItemClick(int selectedMonth);
    }
}
