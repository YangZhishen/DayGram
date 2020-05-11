package cn.haseo.daygram;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.squareup.picasso.Picasso;

import org.litepal.LitePal;
import org.litepal.tablemanager.Connector;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.core.content.ContextCompat;
import cn.haseo.daygram.model.Diary;
import cn.haseo.daygram.util.KeyBoardUtil;
import cn.haseo.daygram.util.Protectable;
import cn.haseo.daygram.util.ThemeTransformation;
import cn.haseo.daygram.widget.MyTextView;

/**
 * 编辑日记的活动
 */
public class ContentActivity extends BaseActivity implements View.OnClickListener,
        KeyBoardUtil.OnSoftKeyboardStateChangedListener, Protectable {
    // 定义日记
    private Diary diary;
    // 定义日记的年份
    private int year;
    // 定义日记的月份
    private int month;
    // 定义日记的日期
    private int day;

    // 定义内容布局
    private LinearLayout contentLayout;
    // 定义滚动控件
    private ScrollView scrollView;
    // 定义日记编辑器
    private EditText diaryEditor;
    // 定义工具条
    private FrameLayout toolbar;

    // 用于标记 EditText 是否获取过焦点
    private boolean hadFocus = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        // 将活动背景图片设置到 DecorView，这样软键盘弹起时不会被挤压，DecorView 为整个 Window 界面最顶层的 View
        getWindow().getDecorView().setBackgroundResource(R.drawable.content_bg);

        // 解决软键盘挤压布局的 BUG
        KeyBoardUtil.assistActivity(this);

        // 获取内容布局的实例
        contentLayout = findViewById(R.id.content_layout);
        // 获取滚动控件的实例
        scrollView = findViewById(R.id.scroll_view);

        // 获取上一个活动传递过来的数据
        Intent intent = getIntent();
        // 从传递过来的数据中取出日记
        diary = intent.getParcelableExtra("diary");

        if (diary != null) {
            // 取出日记中的年月日、星期等信息
            year = diary.getYear();
            month = diary.getMonth();
            day = diary.getDay();
            int week = diary.getWeek();

            // 获取显示日记具体日期的 TextView 实例
            MyTextView showDate = findViewById(R.id.show_date);
            // 设置日记日期格式
            String date = app.getWeek(week) + " / " + app.getMonth(month) + " " + day + " / " + year;
            // 当星期为星期天时
            if (week == 1) {
                // 创建 SpannableString 变量 dateStyle
                SpannableString dateStyle = new SpannableString(date);
                // 将 dateStyle 前面六个字符设置成红色（Sunday 为六个字符），INCLUSIVE_EXCLUSIVE 表示包括起始下标、不包括终止下标
                dateStyle.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.red)), 0, 6, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                // 将文本应用到 TextView
                showDate.setText(dateStyle);
            } else {
                // 将文本应用到 TextView
                showDate.setText(date);
            }


            // 获取日记编辑器的实例
            diaryEditor = findViewById(R.id.diary_editor);
            diaryEditor.setOnClickListener(this);


            // 获取日记的内容
            final String content = diary.getContent();

            // 当日记内容不为空时
            if (content != null) {
                // 从传递过来的数据中取出关键字（SearchActivity 传递过来的）
                final String keyword = intent.getStringExtra("keyword");

                // 当关键字不为空时
                if (keyword != null) {
                    diaryEditor.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // 创建 SpannableString 变量 contentStyle
                            SpannableString contentStyle = new SpannableString(content);
                            // 定义搜索的起始坐标
                            int searchIndex = 0;
                            // 将日记内容中的所有大写字母转换成小写
                            String content1 = content.toLowerCase();
                            // 将关键字中的所有大写字母转换成小写
                            String keyword1 = keyword.toLowerCase();

                            while (true) {
                                // 获取相匹配的内容的起始坐标
                                int matchIndex = content1.indexOf(keyword1, searchIndex);
                                // 当搜索完毕时
                                if (matchIndex == -1) {
                                    // 将日记内容设置到日记编辑器
                                    diaryEditor.setText(contentStyle);
                                    // 结束线程
                                    return;
                                }
                                // 设置日记内容中关键字的颜色
                                contentStyle.setSpan(new ForegroundColorSpan(Color.parseColor("#8C8C8C")), matchIndex, matchIndex + keyword.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                                // 检索下一个内容
                                searchIndex = keyword1.length() + matchIndex;
                            }
                        }
                    }, 300);
                } else {
                    diaryEditor.setText(content);

                    // 当日记请求获取焦点时（最新的日记）
                    if (intent.getBooleanExtra("focus", false)) {
                        // 获取日记的内容长度
                        int length = content.length();
                        // 当日记的内容长度大于 2 且内容的最后一个字符不是换行符时
                        if (length > 2 && content.charAt(length - 1) != '\n') {
                            // 连换两行
                            String text = content + "\n\n";
                            diaryEditor.setText(text);
                            length = text.length();
                        }
                        // 重新设置光标位置
                        diaryEditor.setSelection(Math.max(0, length));

                        // 弹出软键盘
                        requestIME();
                    }
                }
            } else {
                // 弹出软键盘
                requestIME();
            }
        }


        // 获取工具条实例
        toolbar = findViewById(R.id.toolbar);

        // 获取添加当前时间的按钮实例
        Button addTime = findViewById(R.id.add_time_btn);
        // 监听按钮点击事件
        addTime.setOnClickListener(this);

        // 获取完成日记的按钮实例
        Button done = findViewById(R.id.done_btn);
        // 监听按钮点击事件
        done.setOnClickListener(this);
    }


    /**
     * 该方法在活动准备好和用户进行交互的时候调用，即活动显示到前台来时
     */
    @Override
    protected void onResume() {
        super.onResume();

        // 加载设置
        loadSetting();
    }

    /**
     * 该方法用于加载设置
     */
    private void loadSetting() {
        // 获取设置
        SharedPreferences settings = getSharedPreferences("settings", MODE_PRIVATE);

        // 获取当前主题索引
        int themeIndex = settings.getInt("theme.index", 0);
        // 当主题索引大于 0 时
        if (themeIndex > 0) {
            // 若主题缓存为空时则下载
            if (app.getThemeCache() == null) {
                // 获取主题的 URL
                String themeUrl = getResources().getStringArray(R.array.theme_url_list)[themeIndex];
                // 创建存放主题的目标
                MyTarget target = new MyTarget();
                getWindow().getDecorView().setTag(target);
                // 下载主题并存放到指定的目标中
                Picasso.get().load(themeUrl).transform(new ThemeTransformation(this, themeIndex)).into(target);
            } else {
                // 利用主题缓存设置主题
                getWindow().getDecorView().setBackground(new BitmapDrawable(getResources(), app.getThemeCache()));
                // 给日记编辑器设置半透明白色背景
                contentLayout.setBackgroundColor(Color.parseColor("#B4F2F1ED"));
            }
        } else {
            // 当主题索引等于 0 时，还原默认主题
            getWindow().getDecorView().setBackgroundResource(R.drawable.content_bg);
            // 给日记编辑器设置透明背景
            contentLayout.setBackgroundColor(Color.TRANSPARENT);
        }
        // 设置字体大小
        int fontSize = settings.getInt("font.size", 2);
        diaryEditor.setTextSize(TypedValue.COMPLEX_UNIT_SP, app.getTextSize(fontSize));

        // 设置默认字体
        boolean isUseSysFont = settings.getBoolean("system.font.enabled", false);
        diaryEditor.setTypeface(isUseSysFont ? Typeface.DEFAULT : app.getTypeface("Georgia"));
    }


    /**
     * 该方法当活动被关闭时调用
     */
    @Override
    public void finish() {
        // 保存日记
        saveDiary();
        super.finish();
    }


    /**
     * 该方法用于保存日记
     */
    private void saveDiary() {
        // 创建数据库
        Connector.getDatabase();

        // 查询当前正在编辑的日记是否已经保存
        List<Diary> results = LitePal.where("year = ? and month = ? and day = ?", String.valueOf(year), String.valueOf(month), String.valueOf(day))
                .find(Diary.class);

        // 获取日记编辑器文本
        Editable editable = diaryEditor.getText();
        // 当日记编辑器文本不为空时
        if (editable != null) {
            // 当日记已经保存过时
            if (!results.isEmpty()) {
                // 将日记编辑器的内容设置到日记
                diary.setContent(editable.toString());
                // 更新数据库中对应的日记
                diary.updateAll("year = ? and month = ? and day = ?", String.valueOf(year), String.valueOf(month), String.valueOf(day));

                // 创建 Intent
                Intent intent = new Intent();
                // 将日记添加到 Intent
                intent.putExtra("diary", diary);
                // 返回给上一个活动
                setResult(RESULT_OK, intent);

                // 结束函数
                return;
            } else if (!editable.toString().equals("")) {
                // 当日记并未保存到数据库时，并且不是 "" 时，将日记编辑器的内容设置到日记
                diary.setContent(editable.toString());
                // 将日记保存到数据库
                diary.save();

                // 创建 Intent
                Intent intent = new Intent();
                // 将日记添加到 Intent
                intent.putExtra("diary", diary);
                // 返回给上一个活动
                setResult(RESULT_OK, intent);

                // 结束函数
                return;
            }
        }

        // 处理数据失败，返回上一个活动
        setResult(RESULT_CANCELED);
    }


    /**
     * 该方法处理控件的点击事件
     *
     * @param v：触发事件的控件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 添加当前时间的按钮
            case R.id.add_time_btn:
                String nowTime = new SimpleDateFormat("h:mma ", Locale.ENGLISH).format(new Date()).toLowerCase();
                int start = Math.max(diaryEditor.getSelectionStart(), 0);
                int end = Math.max(diaryEditor.getSelectionEnd(), 0);
                diaryEditor.getText().replace(Math.min(start, end), Math.max(start, end), nowTime, 0, nowTime.length());
                break;
            // 完成日记的按钮
            case R.id.done_btn:
                // 收起软键盘
                InputMethodManager inputMethodManager = ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE));
                if (inputMethodManager != null) {
                    inputMethodManager.hideSoftInputFromWindow(diaryEditor.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                break;
        }
    }

    /**
     * 当软键盘的可见性发生变化时调用此方法
     *
     * @param visibility：代表软键盘的可见性
     * @param keyBoardHeight：代表软键盘的高度
     */
    @Override
    public void OnSoftKeyboardStateChanged(int visibility, int keyBoardHeight) {
        // 获取 ScrollView 的布局参数（布局参数包含了子控件的位置、宽高等信息），子布局的布局参数类型必须与父布局一样采用 LinearLayout 类型
        LinearLayout.LayoutParams scrollViewParams = (LinearLayout.LayoutParams) scrollView.getLayoutParams();
        // 获取 toolbar 的布局参数
        LinearLayout.LayoutParams toolbarParams = (LinearLayout.LayoutParams) toolbar.getLayoutParams();

        // 当软键盘可见时
        if (visibility == View.VISIBLE) {
            // 标记 EditText 已经获取过焦点
            hadFocus = true;

            // 记录当前光标索引
            final int start = diaryEditor.getSelectionStart();
            // 隐藏光标
            diaryEditor.setCursorVisible(false);
            // 将光标索引定位到 0
            diaryEditor.setSelection(0);

            // 解决软键盘挤压布局的 BUG
            diaryEditor.post(new Runnable() {
                @Override
                public void run() {
                    // 还原光标位置
                    diaryEditor.setSelection(start);
                    // 将光标设为可见
                    diaryEditor.setCursorVisible(true);
                }
            });

            // 将 ScrollView 的底部外偏距设为 0
            scrollViewParams.setMargins(scrollView.getLeft(), scrollView.getTop(), scrollView.getRight(), 0);
            // 设置 toolbar 的高度为 60 dp
            toolbarParams.height = app.dpToPx(60.0f);
        } else {
            // 当软键盘没有显示时，还原 ScrollView 的底部外偏距
            scrollViewParams.setMargins(scrollView.getLeft(), scrollView.getTop(), scrollView.getRight(), app.dpToPx(20.0f));
            // 设置 toolbar 的高度为 0
            toolbarParams.height = 0;

            // 当 EditText 获取过焦点时
            if (hadFocus) {
                // 将 EditText 设置为无法获取焦点
                diaryEditor.setFocusable(false);
                // 将 EditText 设置为可以通过触摸获取焦点
                diaryEditor.setFocusableInTouchMode(true);
            }
        }
        // 应用布局参数到 ScrollView
        scrollView.setLayoutParams(scrollViewParams);
        // 应用布局参数到 toolbar
        toolbar.setLayoutParams(toolbarParams);

        // 将工具条设为可见
        toolbar.setVisibility(visibility);
    }

    /**
     * 该类用于存放下载好的主题
     */
    private class MyTarget implements com.squareup.picasso.Target {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            // 设置主题
            Drawable drawable = new BitmapDrawable(getResources(), bitmap);
            getWindow().getDecorView().setBackground(drawable);
            contentLayout.setBackgroundColor(Color.parseColor("#B4F2F1ED"));

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
     * 该方法用于弹出软键盘
     */
    private void requestIME() {
        // 将 EditText 设为可获取焦点
        diaryEditor.setFocusable(true);
        // 将 EditText 设为可通过触摸获取焦点
        diaryEditor.setFocusableInTouchMode(true);
        // 请求获取焦点
        diaryEditor.requestFocus();

        // 弹出软键盘
        diaryEditor.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager inputMethodManager = ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE));
                if (inputMethodManager != null) {
                    inputMethodManager.showSoftInput(diaryEditor, InputMethodManager.RESULT_UNCHANGED_SHOWN);
                }
            }
        }, 300);
    }
}


