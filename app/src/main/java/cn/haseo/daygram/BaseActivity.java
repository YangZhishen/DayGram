package cn.haseo.daygram;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import cn.haseo.daygram.util.Protectable;

/**
 * 该类是所有活动的基类
 */
// 该注解用于忽略活动必须注册的警告
@SuppressLint("Registered")
public class BaseActivity extends Activity {
    // 获取 Application 实例
    protected App app = App.getInstance();

    // 定义上一次的解锁时间
    protected static long lastUnlockTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 设置活动的过渡动画
        overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 如果当前活动是需要密码保护的活动并且 App 是可锁定时
        if ((this instanceof Protectable) && app.isLockable()) {
            // 当 App 的解锁时间是否已经超过 1 秒时
            if (lastUnlockTime + 1000 < System.currentTimeMillis()) {
                // 将 App 设为锁定状态
                app.setUnlock(false);
            }
        }
        // 如果当前活动是需要密码保护的活动并且 App 已经处于锁定状态时
        if ((this instanceof Protectable) && app.isLock()) {
            // 跳转到解锁界面
            Intent intent = new Intent(this, PasswordActivity.class);
            intent.putExtra("mode", 1);
            startActivity(intent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 如果当前活动是需要密码保护的活动并且 App 是可锁定时
        if ((this instanceof Protectable) && app.isLockable()) {
            // 储存最新的解锁时间
            lastUnlockTime = System.currentTimeMillis();
        }
    }

    @Override
    public void finish() {
        // 设置活动的过渡动画
        overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
        super.finish();
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        // 设置活动的过渡动画
        overridePendingTransition(R.anim.activity_fade_in, R.anim.activity_fade_out);
    }
}
