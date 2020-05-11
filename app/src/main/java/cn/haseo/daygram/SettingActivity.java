package cn.haseo.daygram;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import cn.haseo.daygram.util.BackupTask;
import cn.haseo.daygram.util.ExportTask;
import cn.haseo.daygram.util.Protectable;
import cn.haseo.daygram.widget.MyTextView;

/**
 * 进行设置的活动
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener, Protectable {
    // 定义设置
    private SharedPreferences settings;

    // 定义选择主题的按钮
    private Button selectTheme;

    // 定义设置字体大小的按钮数组
    private ImageButton[] fontSizes = new ImageButton[5];

    // 定义切换预览类型的按钮数组
    private ImageButton[] previewTypes = new ImageButton[2];

    // 定义使用系统字体的按钮
    private ImageButton useSystemFont;
    // 定义使用自定义字体的按钮
    private ImageButton useCustomFont;

    // 定义开启密码保护的按钮
    private ImageButton passwordOn;
    // 定义关闭密码保护的按钮
    private ImageButton passwordOff;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // 获取设置
        settings = getSharedPreferences("settings", MODE_PRIVATE);

        try {
            // 获取版本号
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            // 显示本版号
            MyTextView versionCode = findViewById(R.id.version_code);
            versionCode.setText(packageInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // 获取反馈按钮的实例
        Button sendFeedback = findViewById(R.id.send_feedback);
        // 设置按钮的字体
        sendFeedback.setTypeface(app.getTypeface("Arvil_Sans"));
        // 监听按钮的点击事件
        sendFeedback.setOnClickListener(this);

        // 获取评价按钮的实例
        Button rateApp = findViewById(R.id.rate_app);
        // 设置评价按钮的字体
        rateApp.setTypeface(app.getTypeface("Arvil_Sans"));
        // 监听按钮的点击事件
        rateApp.setOnClickListener(this);

        // 获取前往微博的按钮实例
        Button goWeibo = findViewById(R.id.go_weibo);
        // 设置按钮的字体
        goWeibo.setTypeface(app.getTypeface("Arvil_Sans"));
        // 监听按钮的点击事件
        goWeibo.setOnClickListener(this);

        // 获取选择主题的按钮实例
        selectTheme = findViewById(R.id.select_theme);
        // 设置按钮的实例
        selectTheme.setTypeface(app.getTypeface("Arvil_Sans"));
        // 监听按钮的点击事件
        selectTheme.setOnClickListener(this);

        // 初始化设置字体大小的按钮
        for (int i = 0; i < fontSizes.length; i++) {
            int buttonId = getResources().getIdentifier("font_size" + i, "id", getPackageName());
            fontSizes[i] = findViewById(buttonId);
            fontSizes[i].setTag(i);
            fontSizes[i].setOnClickListener(new FontSizeListener());
        }

        // 初始化设置预览类型的按钮
        for (int i = 0; i < previewTypes.length; i++) {
            int buttonId = getResources().getIdentifier("preview_type" + i, "id", getPackageName());
            previewTypes[i] = findViewById(buttonId);
            previewTypes[i].setTag(i);
            previewTypes[i].setOnClickListener(new PreviewTypeListener());
        }

        // 获取使用系统字体的按钮实例
        useSystemFont = findViewById(R.id.use_system_font);
        // 监听按钮的点击事件
        useSystemFont.setOnClickListener(this);
        // 获取使用自定义字体的按钮实例
        useCustomFont = findViewById(R.id.use_custom_font);
        // 监听按钮的点击事件
        useCustomFont.setOnClickListener(this);

        // 获取开启密码保护的按钮实例
        passwordOn = findViewById(R.id.password_on);
        // 设置按钮的点击事件
        passwordOn.setOnClickListener(this);

        // 获取关闭密码保护的按钮实例
        passwordOff = findViewById(R.id.password_off);
        // 设置按钮的点击事件
        passwordOff.setOnClickListener(this);

        // 获取更换更换密码的按钮实例
        ImageButton changePassword = findViewById(R.id.change_password);
        // 设置按钮的点击事件
        changePassword.setOnClickListener(this);

        // 获取备份按钮的实例
        ImageButton backup = findViewById(R.id.backup);
        // 设置按钮的点击事件
        backup.setOnClickListener(this);

        // 获取导出日记的按钮实例
        ImageButton export = findViewById(R.id.export);
        // 设置按钮的点击事件
        export.setOnClickListener(this);

        // 获取离开设置的按钮实例
        ImageButton outSetting = findViewById(R.id.out_Setting);
        // 设置按钮的点击事件
        outSetting.setOnClickListener(this);
    }

    /**
     * 该方法在活动准备好和用户进行交互的时候调用，即活动显示到前台来时
     */
    @Override
    protected void onResume() {
        super.onResume();

        // 加载设置
        loadSettings();
    }

    /**
     * 该方法用于加载设置
     */
    private void loadSettings() {
        int themeIndex = settings.getInt("theme.index", 0);
        selectTheme.setText(getResources().getStringArray(R.array.theme_name_list)[themeIndex]);

        int fontSize = settings.getInt("font.size", 2);
        fontSizes[fontSize].setSelected(true);

        int previewType = settings.getInt("preview.type", 1);
        previewTypes[previewType].setSelected(true);

        if (settings.getBoolean("system.font.enabled", false)) {
            useCustomFont.setSelected(false);
            useSystemFont.setSelected(true);
        } else {
            useCustomFont.setSelected(true);
            useSystemFont.setSelected(false);
        }

        if (settings.getBoolean("password.enabled", false)) {
            passwordOff.setSelected(false);
            passwordOn.setSelected(true);
        } else {
            passwordOff.setSelected(true);
            passwordOn.setSelected(false);
        }
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
            // 点击反馈按钮
            case R.id.send_feedback:
                // 获取资源
                Resources res1 = getResources();
                // 创建跳转到邮箱 App 的 Intent
                Intent intent1 = new Intent("android.intent.action.SENDTO", Uri.fromParts("mailto", "2277062105@qq.com", null));
                // 添加邮件主题
                intent1.putExtra("android.intent.extra.SUBJECT", res1.getString(R.string.feedback_subject));
                // 添加邮件内容
                intent1.putExtra("android.intent.extra.TEXT", res1.getString(R.string.feedback_body_greeting));
                // 跳转到外部邮箱 App
                startActivity(Intent.createChooser(intent1, res1.getString(R.string.feedback_header)));
                break;
            // 点击评价按钮
            case R.id.rate_app:
                String packageName = getPackageName();
                try {
                    // 跳转到外部可以解析网址的 App
                    startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + packageName)));
                } catch (ActivityNotFoundException e) {
                    // 跳转到外部可以解析网址的 App
                    startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://play.google.com/store/apps/details?id=" + packageName)));
                }
                break;
            // 点击前往微博的按钮
            case R.id.go_weibo:
                // 跳转到外部可以解析网址的 App
                startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://weibo.com/u/2396473925")));
                break;
            // 点击选择主题的按钮
            case R.id.select_theme:
                // 获取主题名称数组
                final String[] themeNames = getResources().getStringArray(R.array.theme_name_list);
                // 创建对话框
                new AlertDialog.Builder(this)
                        .setTitle(R.string.dialog_title_theme)
                        .setItems(themeNames, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 根据索引将主题名称设置到按钮上
                                selectTheme.setText(themeNames[which]);
                                // 保存置
                                settings.edit().putInt("theme.index", which).apply();
                            }
                        }).show();
                break;
            // 点击使用系统按钮的字体
            case R.id.use_system_font:
                // 将使用自定义字体的按钮设为未选中状态
                useCustomFont.setSelected(false);
                // 将使用系统字体的按钮设为选中状态
                useSystemFont.setSelected(true);
                // 保存置
                settings.edit().putBoolean("system.font.enabled", true).apply();
                break;
            // 点击使用自定义字体的按钮
            case R.id.use_custom_font:
                // 将使用自定义字体的按钮设为选中状态
                useCustomFont.setSelected(true);
                // 将使用系统字体的按钮设为未选中状态
                useSystemFont.setSelected(false);
                // 保存置
                settings.edit().putBoolean("system.font.enabled", false).apply();
                break;
            // 点击开启密码保护的按钮
            case R.id.password_on:
                // 将关闭密码保护的按钮设为未选中状态
                passwordOff.setSelected(false);
                // 将开启密码保护的按钮设为选中状态
                passwordOn.setSelected(true);
                // 当密码不存在时
                if (settings.getString("password.value", "null").equals("null")) {
                    // 创建 Intent
                    Intent intent2 = new Intent(this, PasswordActivity.class);
                    // 使用模式 2 启动 PasswordActivity
                    intent2.putExtra("mode", 2);
                    startActivity(intent2);
                    return;
                }
                // 保存置
                settings.edit().putBoolean("password.enabled", true).apply();
                break;
            // 点击关闭密码保护的按钮
            case R.id.password_off:
                // 将关闭密码保护的按钮设为选中状态
                passwordOff.setSelected(true);
                // 将开启密码保护的按钮设为未选中状态
                passwordOn.setSelected(false);
                // 当密码保护已经开启时
                if (settings.getBoolean("password.enabled", false)) {
                    // 创建 Intent
                    Intent intent3 = new Intent(this, PasswordActivity.class);
                    // 使用模式 4 启动 PasswordActivity
                    intent3.putExtra("mode", 4);
                    startActivity(intent3);
                }
                break;
            // 点击修改密码的按钮
            case R.id.change_password:
                // 创建 Intent
                Intent intent4 = new Intent(this, PasswordActivity.class);
                // 使用模式 3 启动 PasswordActivity
                intent4.putExtra("mode", 3);
                startActivity(intent4);
                break;
            // 点击导出日记的按钮
            case R.id.export:
                // 获取资源
                final Resources res2 = getResources();
                // 创建对话框
                new AlertDialog.Builder(this)
                        .setTitle(R.string.app_name)
                        .setItems(new String[]{res2.getString(R.string.setting_export_as_mail), res2.getString(R.string.setting_export_as_text)},
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // 创建导出任务
                                        ExportTask exportTask = new ExportTask();
                                        // 设置任务监听器
                                        exportTask.setTaskListener(new ExportTask.TaskListener() {
                                            @Override
                                            public void onSuccess(int result, String dayGrams) {
                                                switch (result) {
                                                    // 导出日记到邮箱
                                                    case 0:
                                                        // 创建跳转到邮箱 App 的 Intent
                                                        Intent intent1 = new Intent("android.intent.action.SENDTO", Uri.fromParts("mailto", "", null));
                                                        // 添加邮件主题
                                                        intent1.putExtra("android.intent.extra.SUBJECT", "Your DayGrams");
                                                        // 添加邮件内容
                                                        intent1.putExtra("android.intent.extra.TEXT", dayGrams);
                                                        // 跳转到外部邮箱 App
                                                        startActivity(Intent.createChooser(intent1, res2.getString(R.string.feedback_header)));
                                                        break;
                                                    // 导出日记到文本
                                                    case 1:
                                                        // 创建用于分享的 Intent
                                                        Intent intent2 = new Intent("android.intent.action.SEND");
                                                        // 设置分享类型为文本
                                                        intent2.setType("text/plain");
                                                        // 设置文本主题
                                                        intent2.putExtra("android.intent.extra.SUBJECT", "Your DayGrams");
                                                        // 设置文本内容
                                                        intent2.putExtra("android.intent.extra.TEXT", dayGrams);
                                                        // 跳转到外部可以打开文本的 App
                                                        startActivity(Intent.createChooser(intent2, res2.getString(R.string.app_name)));
                                                        break;
                                                }
                                            }
                                        });
                                        // 执行导出任务
                                        exportTask.execute(which);
                                    }
                                }).show();
                break;
            // 点击备份的按钮
            case R.id.backup:
                // 当系统版本大于等于 6.0 ，并且未获得存储空间权限时
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    // 申请存储空间权限
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    // 执行备份还原操作
                    backup();
                }
                break;
            // 点击离开设置的按钮
            case R.id.out_Setting:
                // 结束活动
                finish();
                break;
        }
    }

    /**
     * 该方法用于处理授权结果
     *
     * @param requestCode：代表请求码
     * @param permissions：代表权限组
     * @param grantResults：代表权限申请结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // 当权限获取成功时
        if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            // 执行备份还原操作
            backup();
        } else {
            // 弹出授权失败的提示
            Toast.makeText(this, getResources().getString(R.string.permission_message), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 该方法用于执行数据库的备份还原操作
     */
    private void backup() {
        // 获取资源
        final Resources res = getResources();

        // 创建备份任务
        final BackupTask backupTask = new BackupTask();
        // 设置任务监听器
        backupTask.setTaskListener(new BackupTask.TaskListener() {
            @Override
            public void onSuccess(String result) {
                // 提示任务结果
                Toast.makeText(SettingActivity.this, result, Toast.LENGTH_SHORT).show();
                // 当恢复成功时
                if (result.equals(res.getString(R.string.restore_success))) {
                    // 创建跳转到主活动的 Intent
                    Intent intent = new Intent(SettingActivity.this, MainActivity.class);
                    // 标记把堆栈中主活动之上的所有活动都清除掉，如果主活动是默认启动模式，则把主活动也清除掉并重新创建
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    // 跳转到主活动
                    startActivity(intent);
                }
            }
        });

        // 创建对话框
        new AlertDialog.Builder(this)
                .setTitle(R.string.app_name)
                .setItems(new String[]{res.getString(R.string.setting_backup_sd_backup), res.getString(R.string.setting_backup_sd_restore)},
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    // 备份数据库
                                    case 0:
                                        // 创建对话框
                                        new AlertDialog.Builder(SettingActivity.this)
                                                .setTitle(R.string.app_name)
                                                .setMessage(R.string.setting_backup_confirm)
                                                .setNegativeButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, final int which) {
                                                        // 启动备份任务
                                                        backupTask.execute(0);
                                                    }
                                                })
                                                .setPositiveButton(R.string.button_cancel, null)
                                                .show();
                                        break;
                                    // 还原数据库
                                    case 1:
                                        // 创建对话框
                                        new AlertDialog.Builder(SettingActivity.this)
                                                .setTitle(R.string.app_name)
                                                .setMessage(R.string.setting_restore_confirm)
                                                .setNegativeButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, final int which) {
                                                        // 启动还原任务
                                                        backupTask.execute(1);
                                                    }
                                                })
                                                .setPositiveButton(R.string.button_cancel, null)
                                                .show();
                                        break;
                                }
                            }
                        }).show();
    }


    /**
     * 字体大小按钮的监听器
     */
    private class FontSizeListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // 将全部设置字体大小的按钮还原为未选中状态
            for (ImageButton fontSize : fontSizes) {
                fontSize.setSelected(false);
            }
            // 获取被点击的按钮索引
            int index = (int) v.getTag();
            // 将被点击的按钮设为选中状态
            fontSizes[index].setSelected(true);
            // 保存设置
            settings.edit().putInt("font.size", index).apply();
        }
    }

    /**
     * 切换预览类型的按钮监听器
     */
    private class PreviewTypeListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // 将全部设置预览类型的按钮还原为未选中状态
            for (ImageButton previewType : previewTypes) {
                previewType.setSelected(false);
            }
            // 获取被点击的按钮索引
            int index = (int) v.getTag();
            // 将被点击的按钮设为选中状态
            previewTypes[index].setSelected(true);
            // 保存设置
            settings.edit().putInt("preview.type", index).apply();
        }
    }
}
