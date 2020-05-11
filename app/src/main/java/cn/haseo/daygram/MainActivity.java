package cn.haseo.daygram;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.jetbrains.annotations.NotNull;
import org.litepal.LitePal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.haseo.daygram.model.Diary;
import cn.haseo.daygram.adapter.DiaryAdapter1;
import cn.haseo.daygram.util.Protectable;
import cn.haseo.daygram.util.ThemeUtil;
import cn.haseo.daygram.widget.MyListView;
import cn.haseo.daygram.adapter.MonthAdapter;
import cn.haseo.daygram.adapter.DiaryAdapter2;
import cn.haseo.daygram.adapter.YearAdapter;
import cn.haseo.daygram.util.ThemeTransformation;
import cn.haseo.daygram.widget.MyTextView;

/**
 * 主活动类
 */
public class MainActivity extends BaseActivity implements Runnable, View.OnClickListener, AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener, MyListView.OnSwipeListener, Protectable {
    // 获取当前日历
    private Calendar calendar = Calendar.getInstance();
    // 获取当前年份
    private int currentYear = calendar.get(Calendar.YEAR);
    // 获取当前月份
    private int currentMonth = calendar.get(Calendar.MONTH);
    // 获取当前日期
    private int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
    // 获取当前星期
    private int currentWeek = calendar.get(Calendar.DAY_OF_WEEK);

    // 定义用户所选择的年份，默认等于当前年份
    private int selectedYear = currentYear;
    // 定义用户所选择的月份，默认等于当前月份
    private int selectedMonth = currentMonth;

    // 定义日记集合
    private List<Diary> diaries = new ArrayList<>();
    // 定义被点击的子项索引
    private int position;

    // 定义日记的 ListView
    private MyListView myListView;
    // 定义类型 1 的日记适配器
    private DiaryAdapter1 diaryAdapter1;
    // 定义类型 2 的日记适配器
    private DiaryAdapter2 diaryAdapter2;

    // 定义活动的根布局
    private LinearLayout rootLayout;
    // 定义头布局显示当前星期的 TextView
    private MyTextView showWeek;
    // 定义头布局显示当前日期的 TextView
    private MyTextView showDay;
    // 定义头布局显示当前时间的 TextView
    private MyTextView showTime;
    // 定义头布局日期与时间之间的竖线
    private View verticalLine;

    // 定义切换月份的按钮
    private Button selectMonth;
    // 定义切换年份的按钮
    private Button selectYear;
    // 定义添加今天日记的按钮
    private ImageButton addToday;

    // 用于标记活动是否处于暂停状态
    private boolean isPause = false;
    // 用于标记是否使用类型 2 的子项布局
    private boolean isItemType2 = false;

    // 使用 Handler 在主线程里对 TextView 显示的时间进行更新
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NotNull Message msg) {
            // 更新 TextView 显示的时间
            showTime.setText(new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).format(System.currentTimeMillis()));
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 获取活动根布局的实例
        rootLayout = findViewById(R.id.root_layout);

        // 加载当前月份的日记
        loadDiary(currentYear, currentMonth);
        // 获取 ListView 的实例
        myListView = findViewById(R.id.my_list_view);
        // 创建类型 1 的日记适配器
        diaryAdapter1 = new DiaryAdapter1(this, diaries);
        // 创建类型 2 的日记适配器
        diaryAdapter2 = new DiaryAdapter2(this, diaries);
        // 设置 ListView 的适配器
        myListView.setAdapter(diaryAdapter1);
        // 监听 ListView 的点击事件
        myListView.setOnItemClickListener(this);
        // 监听 ListView 的长按事件
        myListView.setOnItemLongClickListener(this);
        // 监听 ListView 的滑动事件
        myListView.setOnSwipeListener(this);

        // 获取头布局中显示当前星期的 TextView 实例
        showWeek = findViewById(R.id.show_week);
        // 设置该控件的文本
        showWeek.setText(String.format(getResources().getString(R.string.today_week), app.getWeek(currentWeek)));
        // 获取头布局中显示当前日期的 TextView 实例
        showDay = findViewById(R.id.show_day);
        // 设置该控件的文本
        showDay.setText(String.format(getResources().getString(R.string.today_day), app.getMonth(currentMonth), currentDay));
        // 获取头布局中显示当前时间的 TextView 实例
        showTime = findViewById(R.id.show_time);
        // 获取时间分隔符的实例
        verticalLine = findViewById(R.id.vertical_line);

        // 获取切换月份的按钮实例
        selectMonth = findViewById(R.id.select_month);
        // 设置该按钮的字体
        selectMonth.setTypeface(app.getTypeface("Arvil_Sans"));
        // 设置该按钮的文本
        selectMonth.setText(app.getMonth(currentMonth));
        // 监听该按钮的点击事件
        selectMonth.setOnClickListener(this);

        // 获取切换年份的按钮实例
        selectYear = findViewById(R.id.select_year);
        // 设置该按钮的字体
        selectYear.setTypeface(app.getTypeface("Arvil_Sans"));
        // 设置该按钮的文本
        selectYear.setText(String.valueOf(currentYear));
        // 监听该按钮的点击事件
        selectYear.setOnClickListener(this);

        // 获取添加当天日记的按钮实例
        addToday = findViewById(R.id.add_today);
        // 监听该按钮的点击事件
        addToday.setOnClickListener(this);

        // 获取切换 ListView 子项布局的按钮实例
        ImageButton toggleView = findViewById(R.id.toggle_view);
        // 监听该按钮的点击事件
        toggleView.setOnClickListener(this);

        // 获取前往设置的按钮实例
        ImageButton goSetting = findViewById(R.id.go_setting);
        // 监听该按钮的点击事件
        goSetting.setOnClickListener(this);


        // 将 ListView 滚动到最后一项
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                myListView.smoothScrollToPosition(diaryAdapter1.getCount() - 1);
            }
        }, 720);


        // 检查数据库中有没有日记
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 当数据库中没有日记时
                if (LitePal.findFirst(Diary.class) == null) {
                    // 创建 Intent
                    Intent intent = new Intent(MainActivity.this, ContentActivity.class);
                    // 请求获取焦点
                    intent.putExtra("focus", true);
                    // 将日记添加到 Intent 并跳转到 ContentActivity
                    intent.putExtra("diary", new Diary(currentYear, currentMonth, currentDay, currentWeek, null));
                    startActivityForResult(intent, 2);
                }
            }
        }, 750);
    }

    /**
     * 该方法用于加载日记数据
     *
     * @param selectedYear：表示用户选中的年份
     * @param selectedMonth：表示用户选中的月份
     */
    private void loadDiary(int selectedYear, int selectedMonth) {
        // 向数据库查询用户所选中的年份中对应月份的所有日记，并按日期递增排列
        List<Diary> results = LitePal.where("year = ? and month = ?", String.valueOf(selectedYear), String.valueOf(selectedMonth))
                .order("day")
                .find(Diary.class);

        // 清空日记集合
        diaries.clear();

        // 当用户选择类型 2 的子项布局时
        if (isItemType2) {
            // 将数据库查询结果添加到日记集合
            diaries.addAll(results);
        } else {
            // 定义用户所选中的月份已经过去的天数（包括已经写了日记的最新一天）
            int lostDay;
            // 用户所选中的年份、月份与当前时间一致时
            if (selectedMonth == currentMonth && selectedYear == currentYear) {
                // 根据最新一天有没有日记来决定 lostDay 的大小
                lostDay = !results.isEmpty() && results.get(results.size() - 1).getDay() == currentDay ? currentDay : currentDay - 1;
            } else {
                // 用户所选中的年份、月份早已过去时，将日历翻到该年该月 1 号
                calendar.set(selectedYear, selectedMonth, 1);
                // 获取该月的最大天数
                lostDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            }

            // 根据用户所选中的月份已经过去的天数来创建日记（内容默认为空）
            for (int i = 1; i <= lostDay; i++) {
                // 将日历翻到该年该月该日
                calendar.set(selectedYear, selectedMonth, i);
                // 创建日记并添加到日记集合中
                diaries.add(new Diary(selectedYear, selectedMonth, i, calendar.get(Calendar.DAY_OF_WEEK), null));
            }

            // 将查询出来的日记覆盖日记集合中对应的日记
            for (Diary diary : results) {
                if (diary.getDay() <= lostDay) {
                    diaries.set(diary.getDay() - 1, diary);
                }
            }
        }
    }


    /**
     * 该方法在活动准备好和用户进行交互的时候调用，即活动显示到前台来时
     */
    @Override
    protected void onResume() {
        super.onResume();

        // 加载设置
        loadSetting();

        // 将活动标记为非暂停状态
        isPause = false;
        // 创建定时更新时间的线程
        new Thread(this).start();
    }

    /**
     * 该方法用于加载设置
     */
    private void loadSetting() {
        // 获取设置中的主题索引
        final int themeIndex = getSharedPreferences("settings", MODE_PRIVATE).getInt("theme.index", 0);

        // 当主题索引大于 0 时
        if (themeIndex > 0) {
            // 获取主题的 URL
            String themeUrl = getResources().getStringArray(R.array.theme_url_list)[themeIndex];
            // 创建存放主题的目标
            MyTarget target = new MyTarget();
            rootLayout.setTag(target);
            // 下载主题并存放到指定的目标中
            Picasso.get().load(themeUrl).transform(new ThemeTransformation(this, themeIndex)).into(target);
        } else {
            // 设置背景颜色
            rootLayout.setBackground(null);
            rootLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.global));
            // 清除主题缓存
            app.setThemeCache(null);
        }

        // 刷新界面
        refresh();

        // 延迟设置头布局各控件对应的字体颜色，提高响应速度
        myListView.postDelayed(new Runnable() {
            @Override
            public void run() {
                showWeek.setTextColor(ThemeUtil.getDateColor(themeIndex)[0]);
                showDay.setTextColor(ThemeUtil.getDateColor(themeIndex)[1]);
                verticalLine.setBackgroundColor(ThemeUtil.getDateColor(themeIndex)[3]);
                showTime.setTextColor(ThemeUtil.getDateColor(themeIndex)[2]);
            }
        }, 780);
    }

    /**
     * 该方法处理按键的按下的事件
     *
     * @param keyCode：按键键码值
     * @param event：按键事件变量
     * @return true 不让事件继续向下传递，false 表示让事件继续向下传递
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 当触发事件的按键是返回键时
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 让主活动退到后台，false 表示只对主活动有效
            moveTaskToBack(false);
            // 不让事件继续向下传递
            return true;
        }
        // 调用父类的方法继续处理返回键事件
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 该方法在活动准备去启动和恢复另一个活动的时候调用
     */
    @Override
    protected void onPause() {
        super.onPause();

        // 将活动标记为暂停状态，结束定时更新时间的线程
        isPause = true;
    }

    /**
     * 该方法在活动被销毁的时候调用
     */
    @Override
    protected void onDestroy() {
        // 清空消息队列，防止内存泄漏
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }


    /**
     * 该方法处理 ListView 的点击事件
     *
     * @param parent：代表被点击的              ListView
     * @param view：代表该                   ListView 被点击的子项
     * @param position：代表被点击的子项位置（包括头布局）
     * @param id：代表被点击的子项位置（不包括头布局）
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // 记录被点击的子项索引
        this.position = position;
        // 获取被点击的子项所对应的日记
        Diary diary = (Diary) parent.getItemAtPosition(position);
        // 创建 Intent
        Intent intent = new Intent(this, ContentActivity.class);
        // 如果是今天最新的日记则请求获取焦点
        intent.putExtra("focus", diary.getDay() == currentDay && diary.getMonth() == currentMonth && diary.getYear() == currentYear);
        // 将日记添加到 Intent 并跳转到 ContentActivity
        intent.putExtra("diary", diary);
        startActivityForResult(intent, 1);
    }

    /**
     * 该方法处理 ListView 的长按事件
     *
     * @param parent：代表被点击的              ListView
     * @param view：代表该                   ListView 被点击的子项
     * @param position：代表被点击的子项位置（包括头布局）
     * @param id：代表被点击的子项位置（不包括头布局）
     * @return true 表示不让事件继续向下传递，false 表示让事件继续向下传递
     */
    @Override
    public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position, long id) {
        // 获取被点击的子项所对应的日记
        final Diary diary = (Diary) parent.getItemAtPosition(position);

        // 当被点击的子项所用于的日记内容不为空时，执行删除操作
        if (diary.getContent() != null) {
            // 创建指定样式的对话框
            new AlertDialog.Builder(this)
                    .setTitle(R.string.app_name)
                    .setMessage(String.format(getResources().getString(R.string.context_delete_confirm), diary.getYear(), diary.getMonth() + 1, diary.getDay()))
                    .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 删除数据库中对应的日记
                            LitePal.deleteAll(Diary.class, "year = ? and month = ? and day = ?",
                                    String.valueOf(diary.getYear()), String.valueOf(diary.getMonth()), String.valueOf(diary.getDay()));

                            // 当前使用的是类型 2 的子项布局时
                            if (isItemType2) {
                                // 删除日记集合中对应的日记并刷新界面
                                diaries.remove(position - 1);
                                diaryAdapter2.notifyDataSetChanged();
                            } else {
                                // 当所要删除的日记是今天的日记时
                                if (diary.getDay() == currentDay && diary.getMonth() == currentMonth && diary.getYear() == currentYear) {
                                    // 删除日记集合中对应的日记并刷新界面
                                    diaries.remove(position - 1);
                                    diaryAdapter1.notifyDataSetChanged();
                                } else {
                                    // 当所要删除的日记不是今天的日记时，重置这条日记
                                    diaryAdapter1.resetItem(myListView, position);
                                }
                            }
                        }
                    })
                    .setNegativeButton(R.string.button_cancel, null)
                    .show();
        }
        // 事件不再继续向下传递
        return true;
    }


    /**
     * 该方法处理控件的点击事件
     *
     * @param v：触发事件的控件
     */
    @Override
    @SuppressWarnings("all")
    public void onClick(View v) {
        switch (v.getId()) {
            // 切换月份的按钮
            case R.id.select_month:
                // 创建指定样式的对话框
                final AlertDialog monthChooser = createChooser();

                // 获取对话框的窗体
                Window window1 = monthChooser.getWindow();

                // 设置对话框的布局
                window1.setContentView(R.layout.month_chooser);

                // 获取对话框上的 RecyclerView 控件实例
                RecyclerView monthRecyclerView = window1.findViewById(R.id.month_recycler_view);
                // 将 RecyclerView 设置为横向滑动
                LinearLayoutManager monthManager = new LinearLayoutManager(this);
                monthManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                monthRecyclerView.setLayoutManager(monthManager);
                // 设置 RecyclerView 的适配器
                MonthAdapter monthAdapter = new MonthAdapter(this, currentYear, selectedYear, currentMonth, selectedMonth);
                monthRecyclerView.setAdapter(monthAdapter);
                // 将 RecyclerView 滚动到所选定的月份
                monthRecyclerView.scrollToPosition(selectedMonth);

                // 设置 RecyclerView 子项的点击事件
                monthAdapter.setOnItemClickListener(new MonthAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int month) {
                        // 根据所点击的子项设置当前选中的月份
                        selectedMonth = month;

                        // 更改切换月份的按钮文本为所选月份
                        selectMonth.setText(app.getMonth(month));

                        // 重新加载数据
                        loadDiary(selectedYear, selectedMonth);

                        // 刷新界面
                        refresh();

                        // 关闭对话框
                        monthChooser.dismiss();
                    }
                });
                break;
            // 切换年份的按钮
            case R.id.select_year:
                // 创建指定样式的对话框
                final AlertDialog yearChooser = createChooser();

                // 获取对话框的窗体
                Window window2 = yearChooser.getWindow();

                // 设置对话框的布局
                window2.setContentView(R.layout.year_chooser);

                // 获取对话框上的 RecyclerView 控件实例
                RecyclerView yearRecyclerView = window2.findViewById(R.id.year_recycler_view);
                // 将 RecyclerView 设置为横向滑动
                LinearLayoutManager yearManager = new LinearLayoutManager(this);
                yearManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                yearRecyclerView.setLayoutManager(yearManager);
                // 设置 RecyclerView 的适配器
                YearAdapter yearAdapter = new YearAdapter(this, currentYear, selectedYear);
                yearRecyclerView.setAdapter(yearAdapter);
                // 将 RecyclerView 滚动到所选定的年份
                yearRecyclerView.scrollToPosition(selectedYear - 2011);

                // 设置 RecyclerView 子项的点击事件
                yearAdapter.setOnItemClickListener(new YearAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int year) {
                        // 根据所点击的子项设置当前选中的年份
                        selectedYear = year;
                        // 更改切换年份的按钮文本为所选年份
                        selectYear.setText(String.valueOf(year));

                        // 当选择的时间点超前时将其设置到当前时间
                        if (selectedYear == currentYear) {
                            selectedMonth = Math.min(selectedMonth, currentMonth);
                            // 更改切换月份的按钮文本为所选月份
                            selectMonth.setText(app.getMonth(selectedMonth));
                        }

                        // 重新加载数据
                        loadDiary(selectedYear, selectedMonth);

                        // 刷新界面
                        refresh();

                        // 关闭对话框
                        yearChooser.dismiss();
                    }
                });
                break;
            // 添加今天日记的按钮
            case R.id.add_today:
                // 查询数据库是否有今天的日记
                List<Diary> results = LitePal
                        .where("year = ? and month = ? and day = ?", String.valueOf(currentYear), String.valueOf(currentMonth), String.valueOf(currentDay))
                        .find(Diary.class);

                // 将日记添加到意图并启动内容活动
                Intent intent = new Intent(this, ContentActivity.class);
                // 请求获取焦点
                intent.putExtra("focus", true);
                // 将日记添加到 Intent 并跳转到 ContentActivity
                intent.putExtra("diary", results.isEmpty() ? new Diary(currentYear, currentMonth, currentDay, currentWeek, null) : results.get(0));
                startActivityForResult(intent, 2);
                break;
            // 改变 ListView 子项布局的按钮
            case R.id.toggle_view:
                // 当前使用的是类型 2 的子项布局时
                if (isItemType2) {
                    // 将使用类型 2 的子项布局标记设为 false
                    isItemType2 = false;

                    // 重新加载日记
                    loadDiary(selectedYear, selectedMonth);

                    // 设置对应的适配器
                    myListView.setAdapter(diaryAdapter1);
                    // 设置 ListView 的分割线高度
                    myListView.setDividerHeight(app.dpToPx(10.0f));
                } else {
                    // 当前使用的是类型 1 的子项布局时，将使用类型 2 的子项布局标记设为 true
                    isItemType2 = true;

                    // 重新加载日记
                    loadDiary(selectedYear, selectedMonth);

                    // 设置对应的适配器
                    myListView.setAdapter(diaryAdapter2);
                    // 设置 ListView 的分隔线高度
                    myListView.setDividerHeight(app.dpToPx(4.0f));
                }
                break;
            case R.id.go_setting:
                startActivity(new Intent(this, SettingActivity.class));
                break;
        }
    }

    /**
     * 该方法用于创建一个采用指定样式和动画的对话框
     *
     * @return selector：返回创建好的对话框
     */
    @SuppressWarnings("all")
    private AlertDialog createChooser() {
        // 根据样式创建对话框，并设置对话框可用返回键取消
        final AlertDialog chooser = new AlertDialog.Builder(this, R.style.ChooserDialog).show();

        // 获取对话框的窗体
        Window window = chooser.getWindow();
        // 设置对话框在屏幕底部弹出
        window.setGravity(Gravity.BOTTOM);
        // 设置对话框窗体的动画样式
        window.setWindowAnimations(R.style.ChooserAnimation);

        // 创建 DisplayMetrics 变量用于存放屏幕信息（DisplayMetrics 是 Android 提供的记述屏幕信息的类）
        DisplayMetrics dm = new DisplayMetrics();
        // 获取屏幕的相关信息并赋值给变量 dm
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        // 获取对话框的布局信息
        WindowManager.LayoutParams lp = window.getAttributes();
        // 修改对话框的布局宽度为屏幕宽度
        lp.width = dm.widthPixels;
        // 将修改后的布局信息应用到对话框窗体
        window.setAttributes(lp);

        // 返回创建好的对话框
        return chooser;
    }


    /**
     * 该方法接收活动返回来的数据
     *
     * @param requestCode：请求码，用于判断数据来源
     * @param resultCode：结果码：用于判断处理结果是否成功
     * @param data：数据
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 当数据处理成功时
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                // 表示通过主活动来修改日记
                case 1:
                    // 更新相应的日记集合
                    diaries.set(position - 1, (Diary) data.getParcelableExtra("diary"));
                    break;
                // 表示添加今天的日记
                case 2:
                    // 如果当前活动显示的是最新月份的日记时
                    if (selectedMonth == currentMonth && selectedYear == currentYear) {
                        // 从返回来的数据中取出日记

                        Diary diary = data.getParcelableExtra("diary");

                        if (diary != null) {
                            // 当日记已经存在时，更新相应的日记集合
                            if (diaries.size() == diary.getDay()) {
                                diaries.set(diary.getDay() - 1, diary);
                            } else {
                                // 当日记不存在时，添加日记
                                diaries.add(diary);
                            }
                        }
                    }
                    break;
                // 表示通过搜索活动来修改日记
                case 3:
                    // 从返回来的数据中取出日记
                    Diary diary = data.getParcelableExtra("diary");

                    // 如果当前活动显示的日记集合与所修改的日记同年同月时
                    if (Objects.requireNonNull(diary).getMonth() == selectedMonth && diary.getYear() == selectedYear) {
                        // 更新日记
                        diaries.set(diary.getDay() - 1, diary);
                    }
                    break;
            }
        }
    }

    /**
     * 该方法让 Handler 定时更新时间
     */
    @Override
    public void run() {
        while (!isPause) {
            try {
                // 添加到消息队列
                handler.sendMessage(new Message());
                // 线程休眠一秒
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 该方法执行滑动过程中的一些操作
     *
     * @param deltaY：ListView 的最大内偏距
     */
    @Override
    public void onSwipe(int deltaY, int maxOverScrollY) {
        // 根据滑动距离设置按钮背景的透明度
        if (deltaY < maxOverScrollY) {
            // 根据手指的滑动值设置按钮的透明度
            addToday.getBackground().setAlpha((int) ((maxOverScrollY - deltaY * 0.5) / maxOverScrollY * 255));
        } else {
            // 当 ListView 不可滑动时，切换按钮的图标
            addToday.setBackgroundResource(R.drawable.search_icon);
        }
    }

    /**
     * 该方法执行手指离开屏幕后的一些操作
     *
     * @param isBlock：代表 ListView 能否滑动
     */
    @Override
    public void onRelease(boolean isBlock) {
        // 重设按钮的图标
        addToday.setBackgroundResource(R.drawable.add_today_btn);
        // 将按钮的背景设置为不透明
        addToday.getBackground().setAlpha(255);

        // 当 ListView 底部滑动到最大内偏距并且手指不是抛动时
        if (isBlock) {
            // 跳转到搜索活动
            startActivityForResult(new Intent(this, SearchActivity.class), 3);
        }
    }


    /**
     * 该类用于存放下载好的主题
     */
    private class MyTarget implements Target {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            // 设置主题
            Drawable drawable = new BitmapDrawable(getResources(), bitmap);
            rootLayout.setBackground(drawable);
            // 缓冲主题
            app.setThemeCache(bitmap);
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    }

    /**
     * 该方法用于刷新界面
     */
    private void refresh() {
        // 根据子项类型采用相应的适配器刷新界面
        if (isItemType2) {
            diaryAdapter2.notifyDataSetChanged();
        } else {
            diaryAdapter1.notifyDataSetChanged();
        }
    }
}
