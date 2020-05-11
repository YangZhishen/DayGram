package cn.haseo.daygram;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import java.util.Objects;

import cn.haseo.daygram.widget.MyTextView;

/**
 * 进行密码设置的活动
 */
public class PasswordActivity extends BaseActivity {
    // 定义设置
    private SharedPreferences settings;
    // 定义活动的模式
    private int mode;
    // 定义活动的阶段
    private int state;

    // 定义圆点图片
    private ImageView[] dotImages = new ImageView[4];
    // 定义密码框
    private EditText passwordField;
    // 定义提示布局
    private FrameLayout hintLayout;
    // 定义提示语
    private MyTextView hintText;
    // 定义提示语左边的竖线
    private View verticalLine1;
    // 定义提示语右边的竖线
    private View verticalLine2;

    // 定义密码缓存
    private String tempPass;


    @Override
    @SuppressWarnings("all")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        // 获取传递过来的模式
        mode = getIntent().getIntExtra("mode", 1);

        // 获取设置
        settings = getSharedPreferences("settings", MODE_PRIVATE);
        // 当密码为空并且当前模式为修改密码的模式时
        if (settings.getString("password.value", "null").equals("null") && mode == 3) {
            // 将模式改为创建密码模式
            mode = 2;
        }

        // 获取取消按钮的实例
        ImageButton cancel = findViewById(R.id.cancel);
        // 监听按钮的点击事件
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 将 App 活动设为解锁状态
                app.setUnlock(true);
                // 结束活动
                finish();
            }
        });

        // 初始化圆点的 ImageView
        for (int i = 0; i < dotImages.length; i++) {
            int dotImgId = getResources().getIdentifier("password_dot_img" + i, "id", getPackageName());
            dotImages[i] = findViewById(dotImgId);
        }

        // 获取提示布局的实例
        hintLayout = findViewById(R.id.hint_layout);
        // 获取提示文本的实例
        hintText = findViewById(R.id.hint);
        // 获取两根竖线的实例
        verticalLine1 = findViewById(R.id.vertical_line1);
        verticalLine2 = findViewById(R.id.vertical_line2);

        // 根据模式进行相应的初始化
        switch (mode) {
            // 验证密码
            case 1:
                // 隐藏取消按钮
                cancel.setVisibility(View.GONE);
                // 隐藏提示布局
                hintLayout.setVisibility(View.GONE);
                break;
            // 创建密码
            case 2:
                // 设置为创建密码阶段
                state = 1;
                // 显示取消按钮
                cancel.setVisibility(View.VISIBLE);
                // 显示提示布局
                hintLayout.setVisibility(View.VISIBLE);
                hintText.setText(R.string.password_enter_new);
                break;
            // 修改密码
            case 3:
                // 设置为修改密码阶段
                state = 2;
                // 显示取消按钮
                cancel.setVisibility(View.VISIBLE);
                // 显示提示布局
                hintLayout.setVisibility(View.VISIBLE);
                hintText.setText(R.string.password_enter_old);
                break;
            // 关闭密码
            case 4:
                // 显示取消按钮
                cancel.setVisibility(View.VISIBLE);
                // 隐藏提示布局
                hintLayout.setVisibility(View.GONE);
                break;
        }

        // 获取密码框的实例
        passwordField = findViewById(R.id.password_field);
        // 监听文本框的文本变化事件
        passwordField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 即时切换图片
                toggleDotImage();
                // 当输入完成时
                onInputComplete();
            }
        });
    }

    /**
     * 该方法根据输入的字符数目即时切换圆点图片
     */
    private void toggleDotImage() {
        // 将所有圆点图片切换成默认状态
        for (ImageView dotImages : dotImages) {
            dotImages.setImageResource(R.drawable.password_dot_off);
        }

        // 获取输入的字符数目
        int length = passwordField.getText().toString().length();

        // 根据字符数目修改圆点图片
        for (int i = 0; i < length; i++) {
            dotImages[i].setImageResource(R.drawable.password_dot_on);
        }
    }

    /**
     * 该方法在输入完密码后根据模式进行相应的处理
     */
    @SuppressWarnings("all")
    private void onInputComplete() {
        // 当密码框的字符数目到达 4 时
        if (passwordField.getText().length() == 4) {
            // 根据模式采取相应的处理
            switch (mode) {
                // 验证密码
                case 1:
                    // 当密码验证成功时
                    if (settings.getString("password.value", "null").equals(passwordField.getText().toString())) {
                        // 解锁 App
                        app.setUnlock(true);
                        // 结束活动
                        finish();
                    } else {
                        // 当密码错误时
                        onPasswordError();
                    }
                    break;
                // 创建密码
                case 2:
                    // 创建密码
                    createPassword();
                    break;
                // 修改密码
                case 3:
                    // 当活动处于验证密码阶段时
                    if (state == 2) {
                        // 当密码验证成功时
                        if (settings.getString("password.value", "null").equals(passwordField.getText().toString())) {
                            // 将活动标记为创建密码阶段
                            state = 1;
                            // 提示输入新密码
                            hintText.setText(R.string.password_enter_new);
                            // 清除文本
                            clearText();
                        } else {
                            // 当密码验证失败时，清除文本
                            clearText();
                        }
                    } else {
                        // 当活动不是处于密码验证阶段时，直接创建密码
                        createPassword();
                    }
                    break;
                // 关闭密码
                case 4:
                    // 当密码验证成功时
                    if (settings.getString("password.value", "null").equals(passwordField.getText().toString())) {
                        // 关闭密码验证
                        settings.edit().putBoolean("password.enabled", false).putString("password.value", "null").apply();
                        // 解除 App 的锁定
                        app.setUnlock(true);
                        // 结束活动
                        finish();
                    } else {
                        // 当密码错误时
                        onPasswordError();
                    }
                    break;
            }
        }
    }

    /**
     * 该方法在密码错误时执行一些操作
     */
    private void onPasswordError() {
        // 将所有圆点图片设为红色
        for (ImageView dotImage : dotImages) {
            dotImage.setImageResource(R.drawable.password_dot_red);
        }

        // 定义密码错误时的颜色（红色）
        int red = ContextCompat.getColor(this, R.color.red);
        // 将提示语的字体颜色设为红色
        hintText.setTextColor(red);
        // 将两根竖线的颜色设为红色
        verticalLine1.setBackgroundColor(red);
        verticalLine2.setBackgroundColor(red);
        // 提示密码错误
        hintText.setText(R.string.password_failed);
        // 显示提示布局
        hintLayout.setVisibility(View.VISIBLE);

        // 还原默认状态
        hintText.postDelayed(new Runnable() {
            public void run() {
                // 定义默认颜色（灰色）
                int gray = Color.parseColor("#989486");
                // 将提示语的字体颜色设为灰色
                hintText.setTextColor(gray);
                // 将两根竖线的颜色设为灰色
                verticalLine1.setBackgroundColor(gray);
                verticalLine2.setBackgroundColor(gray);
                // 隐藏提示布局
                hintLayout.setVisibility(View.GONE);
                // 清除文本
                clearText();
            }
        }, 1200);
    }

    /**
     * 该方法用于创建密码
     */
    private void createPassword() {
        // 当活动处于创建密码的阶段时
        if (state == 1) {
            // 暂存密码
            tempPass = passwordField.getText().toString();
            // 提示再输入一次密码
            hintText.setText(R.string.password_reenter);
            // 将活动标记为再输入一次密码的阶段
            state = 3;
            // 清除文本
            clearText();
            return;
        }

        // 当活动不处于再输入一次密码的阶段时
        if (state != 3) {
            // 结束函数，不执行后面的操作
            return;
        }

        // 当两次输入的密码相同时
        if (passwordField.getText().toString().equals(tempPass)) {
            // 保存密码
            settings.edit().putBoolean("password.enabled", true).putString("password.value", passwordField.getText().toString()).apply();
            // 将 App 设为解锁状态
            app.setUnlock(true);
            // 结束活动
            finish();
        } else {
            // 当两次输入密码不同时，提示输入新密码（重新来过）
            hintText.setText(R.string.password_enter_new);
            // 将活动标记为创建密码阶段
            state = 1;
            // 清除文本
            clearText();
        }
    }

    /**
     * 该方法用于清除文本和重置圆点图片
     */
    private void clearText() {
        passwordField.setText("");
        toggleDotImage();
    }


    /**
     * 该方法在活动准备去启动和恢复另一个活动的时候调用
     */
    @Override
    protected void onResume() {
        super.onResume();

        // 自动弹出软键盘
        passwordField.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 密码框请求获取焦点
                passwordField.requestFocus();
                // 弹出软键盘
                ((InputMethodManager) Objects.requireNonNull(getSystemService(INPUT_METHOD_SERVICE))).showSoftInput(passwordField, InputMethodManager.RESULT_UNCHANGED_SHOWN);
            }
        }, 250);
    }

    /**
     * 按下返回键时不执行任何操作
     */
    @Override
    public void onBackPressed() {}
}
