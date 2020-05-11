package cn.haseo.daygram;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;

import org.litepal.LitePalApplication;

import java.util.Map;

/**
 * 自定义 Application
 */
@SuppressLint("StaticFieldLeak")
public class App extends LitePalApplication {
    // 定义月份名称数组
    private String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    // 定义星期名称数组
    private String[] weeks = {"--", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

    // 定义 Application 实例
    private static App app;
    // 定义 App 是否处于解锁状态
    private Boolean unlock = true;
    // 定义主题缓存
    private Bitmap themeCache = null;

    // 定义需要用到的各种字体
    private Typeface arvilSans;
    private Typeface avenirRoman;
    private Typeface georgia;
    private Typeface georgiaBold;
    private Typeface robotoBold;
    private Typeface robotoThin;


    // 获取 Application 实例
    public static App getInstance() {
        return app;
    }

    // 获取主题缓存
    public Bitmap getThemeCache() {
        return themeCache;
    }

    // 设置主题缓存
    public void setThemeCache(Bitmap themeCache) {
        this.themeCache = themeCache;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        // 将变量 app 指向 Application 实例
        app = this;

        // 初始化各字体
        arvilSans = Typeface.createFromAsset(getAssets(), "fonts/Arvil_Sans.ttf");
        avenirRoman = Typeface.createFromAsset(getAssets(), "fonts/Avenir-Roman.otf");
        georgia = Typeface.createFromAsset(getAssets(), "fonts/Georgia.otf");
        georgiaBold = Typeface.createFromAsset(getAssets(), "fonts/Georgia-Bold.otf");
        robotoBold = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Bold.ttf");
        robotoThin = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf");

        // 加载设置
        loadSettings();
    }

    /**
     * 该方法获取设置
     */
    private void loadSettings() {
        SharedPreferences settings = getSharedPreferences("settings", 0);
        SharedPreferences.Editor edit = settings.edit();
        Map all = settings.getAll();
        if (!all.containsKey("theme.index")) {
            edit.putInt("theme.index", 0);
        }
        if (!all.containsKey("font.size")) {
            edit.putInt("font.size", 2);
        }
        if (!all.containsKey("preview.type")) {
            edit.putInt("preview.type", 1);
        }
        if (!all.containsKey("password.value")) {
            edit.putString("password.value", "null");
        }
        if (!all.containsKey("password.enabled")) {
            edit.putBoolean("password.enabled", false);
        }
        if (!all.containsKey("system.font.enabled")) {
            edit.putBoolean("system.font.enabled", false);
        }
        edit.apply();
    }


    /**
     * 该方法根据星期索引获取对应的星期字符串
     *
     * @param index：星期索引
     * @return 星期字符串
     */
    public String getWeek(int index) {
        return weeks[index];
    }

    /**
     * 该方法根据月份索引获取对应的月份字符串
     *
     * @param index：月份索引
     * @return 月份字符串
     */
    public String getMonth(int index) {
        return months[index];
    }

    /**
     * 该方法根据字体名称获取对应的字体
     *
     * @param fontName：字体名称
     * @return 对应的字体
     */
    public Typeface getTypeface(String fontName) {
        switch (fontName) {
            case "Arvil_Sans":
                return app.arvilSans;
            case "Avenir-Roman":
                return app.avenirRoman;
            case "Georgia":
                return app.georgia;
            case "Georgia-Bold":
                return app.georgiaBold;
            case "Roboto-Bold":
                return app.robotoBold;
            case "Roboto-Thin":
                return app.robotoThin;
            default:
                return Typeface.DEFAULT;
        }
    }

    /**
     * 该方法根据字体大小编号返回定义大小的字体
     *
     * @param fontSize：字体大小编号
     * @return 字体大小
     */
    public float getTextSize(int fontSize) {
        switch (fontSize) {
            case 0:
                return 13.3f;
            case 1:
                return 14.3f;
            case 2:
                return 15.3f;
            case 3:
                return 16.3f;
            case 4:
                return 17.3f;
        }
        return 15.3f;
    }


    /**
     * 该方法设置 App 的解锁状态
     *
     * @param unlock：代表 App 的解锁状态
     */
    public void setUnlock(boolean unlock) {
        this.unlock = unlock;
        if (unlock) {
            // 保存解锁时的具体时刻
            BaseActivity.lastUnlockTime = System.currentTimeMillis();
        }
    }

    /**
     * 获取 App 的锁定状态
     */
    public boolean isLock() {
        if (unlock) {
            return false;
        }
        return isLockable();
    }

    /**
     * 该方法获取 App 的锁定状态
     *
     * @return true 代表 App 可以锁定， false 代表 App 不可以锁定
     */
    @SuppressWarnings("all")
    public boolean isLockable() {
        SharedPreferences settings = getSharedPreferences("settings", MODE_PRIVATE);
        boolean lockable = settings.getBoolean("password.enabled", false);
        return lockable && !settings.getString("password.value", "null").equals("null");
    }


    /**
     * 该方法将 dp 转换成 px
     *
     * @param dpValue：dp 值
     * @return px 值
     */
    public int dpToPx(float dpValue) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
