package cn.haseo.daygram;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.haseo.daygram.model.Diary;
import cn.haseo.daygram.util.KeyBoardUtil;
import cn.haseo.daygram.adapter.ResultAdapter;
import cn.haseo.daygram.util.Protectable;

/**
 * 搜索日记的活动
 */
public class SearchActivity extends BaseActivity implements AdapterView.OnItemClickListener,
        KeyBoardUtil.OnSoftKeyboardStateChangedListener, Protectable {
    // 定义搜索结果的集合
    private List<Diary> results = new ArrayList<>();
    // 定义搜索关键字
    private String keyword;

    // 定义显示搜索结果的 ListView
    private ListView resultListView;
    // 定义 ListView 的适配器
    private ResultAdapter resultAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // 解决软键盘挤压布局的 BUG
        KeyBoardUtil.assistActivity(this);
        // 设置活动不自动弹出软键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // 获取 ListView 的实例
        resultListView = findViewById(R.id.result_list_view);
        // 创建 ListView 的适配器
        resultAdapter = new ResultAdapter(results);
        // 设置 ListView 的适配器
        resultListView.setAdapter(resultAdapter);
        // 监听 ListView 的点击事件
        resultListView.setOnItemClickListener(this);

        // 获取搜索栏的实例
        final EditText searchBar = findViewById(R.id.search_bar);
        // 自带弹出软键盘
        searchBar.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 搜索栏清酒获取焦点
                searchBar.requestFocus();
                // 弹出软键盘
                ((InputMethodManager) Objects.requireNonNull(getSystemService(INPUT_METHOD_SERVICE))).showSoftInput(searchBar, InputMethodManager.RESULT_UNCHANGED_SHOWN);
            }
        }, 320);
        // 监听搜索栏文本变化的事件
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 获取搜索栏的关键字
                keyword = s.toString();
                // 当关键字内容不为空时
                if (!keyword.trim().isEmpty()) {
                    // 查询匹配的日记内容
                    List<Diary> results = LitePal.where("content like ?", "%" + keyword + "%")
                            .order("year, month, day")
                            .find(Diary.class);
                    // 清除上一次的搜索结果
                    SearchActivity.this.results.clear();
                    // 添加新的搜索结果
                    SearchActivity.this.results.addAll(results);
                    // 设置关键字到适配器
                    resultAdapter.setKeyword(keyword);
                    // 刷新界面
                    resultAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    /**
     * 该方法处理 ListView 的点击事件
     *
     * @param parent：代表被点击的              ListView
     * @param view：代表该                   ListView 被点击的子项
     * @param position：代表被点击的子项位置（包括头布局）
     * @param id：代表被点击的子项位置（不包括头布局），一般等于 position
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // 创建 Intent
        Intent intent = new Intent(this, ContentActivity.class);
        // 将搜索关键字添加到 Intent
        intent.putExtra("keyword", keyword);
        // 将日记添加到 Intent 并跳转到 ContentActivity
        intent.putExtra("diary", (Diary) parent.getItemAtPosition(position));
        startActivityForResult(intent, 4);
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
            // 创建 Intent
            Intent intent = new Intent();
            // 将 ContentActivity 返回来的日记添加到 Intent
            intent.putExtra("diary", data.getParcelableExtra("diary"));
            // 返回给上一个活动
            setResult(RESULT_OK, intent);
        } else {
            // 处理数据失败，返回上一个活动
            setResult(RESULT_CANCELED);
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
        // 获取 ListView 的布局参数（布局参数包含了子控件的位置、宽高等信息），子布局的布局参数类型必须与父布局一样采用 LinearLayout 类型
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) resultListView.getLayoutParams();

        // 当软键盘可见时
        if (visibility == View.VISIBLE) {
            // 设置 ListView 的 MarginTop 为软键盘的高度
            layoutParams.setMargins(0, keyBoardHeight, 0, 0);
        } else {
            // 当软键盘不可见时，设置 ListView 的 MarginTop 为 0
            layoutParams.setMargins(0, 0, 0, 0);
        }

        // 应用布局参数到 resultListView
        resultListView.setLayoutParams(layoutParams);
    }
}
