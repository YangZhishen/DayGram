package cn.haseo.daygram.util;

import android.graphics.Color;

import cn.haseo.daygram.R;

/**
 * 该类用于根据主题索引获取对应的资源
 */
public class ThemeUtil {
    /**
     * 该方法根据主题索引获取对应的一组头布局各控件的字体颜色
     *
     * @param themeIndex：主题索引
     * @return 与主题相对应的一组头布局各控件的字体颜色
     */
    public static int[] getDateColor(int themeIndex) {
        int[] colors = new int[10];
        switch (themeIndex) {
            case 1:
                colors[0] = Color.parseColor("#BAB8AE");
                colors[1] = Color.parseColor("#BAB8AE");
                colors[3] = Color.parseColor("#726D71");
                colors[2] = Color.parseColor("#ADABA2");
                break;
            case 2:
                colors[0] = Color.parseColor("#D7D6CF");
                colors[1] = Color.parseColor("#BAB8AE");
                colors[3] = Color.parseColor("#706C6B");
                colors[2] = Color.parseColor("#B1B0A9");
                break;
            case 3:
                colors[0] = Color.parseColor("#49220C");
                colors[1] = Color.parseColor("#68372E");
                colors[3] = Color.parseColor("#80554E");
                colors[2] = Color.parseColor("#5D362F");
                break;
            case 4:
                colors[0] = Color.parseColor("#503F11");
                colors[1] = Color.parseColor("#675C15");
                colors[3] = Color.parseColor("#8E8A6E");
                colors[2] = Color.parseColor("#797141");
                break;
            case 5:
                colors[0] = Color.parseColor("#FFFFFF");
                colors[1] = Color.parseColor("#FFFFFF");
                colors[3] = Color.parseColor("#FFFFFF");
                colors[2] = Color.parseColor("#FFFFFF");
                break;
            case 6:
                colors[0] = Color.parseColor("#E6FFFFFF");
                colors[1] = Color.parseColor("#CCFFFFFF");
                colors[3] = Color.parseColor("#B3FFFFFF");
                colors[2] = Color.parseColor("#80FFFFFF");
                break;
            case 7:
                colors[0] = Color.parseColor("#3D505B");
                colors[1] = Color.parseColor("#3D505B");
                colors[3] = Color.parseColor("#7E939E");
                colors[2] = Color.parseColor("#526976");
                break;
            case 8:
                colors[0] = Color.parseColor("#23262C");
                colors[1] = Color.parseColor("#3F3D45");
                colors[3] = Color.parseColor("#706C71");
                colors[2] = Color.parseColor("#58555F");
                break;
            case 9:
                colors[0] = Color.parseColor("#141414");
                colors[1] = Color.parseColor("#2E2E2E");
                colors[3] = Color.parseColor("#4F4F4F");
                colors[2] = Color.parseColor("#3D3D3D");
                break;
            case 10:
                colors[0] = Color.parseColor("#141414");
                colors[1] = Color.parseColor("#575757");
                colors[3] = Color.parseColor("#939393");
                colors[2] = Color.parseColor("#6E6E6E");
            case 11:
                colors[0] = Color.parseColor("#141414");
                colors[1] = Color.parseColor("#575757");
                colors[3] = Color.parseColor("#939393");
                colors[2] = Color.parseColor("#6E6E6E");
                break;
            case 12:
                colors[0] = Color.parseColor("#2B2B2B");
                colors[1] = Color.parseColor("#636363");
                colors[3] = Color.parseColor("#939393");
                colors[2] = Color.parseColor("#707070");
            case 13:
                colors[0] = Color.parseColor("#2B2B2B");
                colors[1] = Color.parseColor("#636363");
                colors[3] = Color.parseColor("#939393");
                colors[2] = Color.parseColor("#707070");
                break;
            case 14:
                colors[0] = Color.parseColor("#72716B");
                colors[1] = Color.parseColor("#72716b");
                colors[3] = Color.parseColor("#504f4b");
                colors[2] = Color.parseColor("#696862");
                break;
            case 15:
                colors[0] = Color.parseColor("#815864");
                colors[1] = Color.parseColor("#815864");
                colors[3] = Color.parseColor("#a98e96");
                colors[2] = Color.parseColor("#956d79");
                break;
            default:
                colors[0] = Color.parseColor("#4B4B48");
                colors[1] = Color.parseColor("#52524E");
                colors[3] = Color.parseColor("#C9C8C3");
                colors[2] = Color.parseColor("#797872");
                break;
        }
        return colors;
    }


    /**
     * 该方法根据主题索引获取对应的一组圆点图片资源 id
     *
     * @param themeIndex：主题索引
     * @return 与主题相对应的一组圆点图片资源 id
     */
    public static int[] getDotId(int themeIndex) {
        int[] dots = new int[2];
        switch (themeIndex) {
            case 1:
                dots[0] = R.drawable.button_add_dot_01;
                dots[1] = R.drawable.button_add_dot_red_01;
                break;
            case 2:
                dots[0] = R.drawable.button_add_dot_02;
                dots[1] = R.drawable.button_add_dot_red_02;
                break;
            case 3:
                dots[0] = R.drawable.button_add_dot_03;
                dots[1] = R.drawable.button_add_dot_red_03;
                break;
            case 4:
                dots[0] = R.drawable.button_add_dot_04;
                dots[1] = R.drawable.button_add_dot_red_04;
                break;
            case 5:
                dots[0] = R.drawable.button_add_dot_05;
                dots[1] = R.drawable.button_add_dot_red_05;
                break;
            case 6:
                dots[0] = R.drawable.button_add_dot_06;
                dots[1] = R.drawable.button_add_dot_red_06;
                break;
            case 7:
                dots[0] = R.drawable.button_add_dot_07;
                dots[1] = R.drawable.button_add_dot_red_07;
                break;
            case 8:
                dots[0] = R.drawable.button_add_dot_08;
                dots[1] = R.drawable.button_add_dot_red_08;
                break;
            case 9:
                dots[0] = R.drawable.button_add_dot_09;
                dots[1] = R.drawable.button_add_dot_red_09;
                break;
            case 10:
                dots[0] = R.drawable.button_add_dot_10;
                dots[1] = R.drawable.button_add_dot_red_10;
                break;
            case 11:
                dots[0] = R.drawable.button_add_dot_11;
                dots[1] = R.drawable.button_add_dot_red_11;
                break;
            case 12:
                dots[0] = R.drawable.button_add_dot_12;
                dots[1] = R.drawable.button_add_dot_red_12;
                break;
            case 13:
                dots[0] = R.drawable.button_add_dot_13;
                dots[1] = R.drawable.button_add_dot_red_13;
                break;
            case 14:
                dots[0] = R.drawable.button_add_dot_14;
                dots[1] = R.drawable.button_add_dot_red_14;
                break;
            case 15:
                dots[0] = R.drawable.button_add_dot_15;
                dots[1] = R.drawable.button_add_dot_red_15;
                break;
            default:
                dots[0] = R.drawable.button_add_dot_00;
                dots[1] = R.drawable.button_add_dot_red_00;
                break;
        }
        return dots;
    }


    /**
     * 该方法根据主题索引获取类型 2 子项布局对应的字体颜色
     *
     * @param themeIndex：主题索引
     * @return 与主题相对应的类型 2 子项布局的字体颜色
     */
    public static int getTextColor(int themeIndex) {
        switch (themeIndex) {
            case 1:
                return parseColor("#FFFFFF", 0.7f);
            case 2:
                return parseColor("#FFFFFF", 0.95f);
            case 3:
                return parseColor("#FFFFFF", 0.82f);
            case 4:
                return parseColor("#000000", 0.8f);
            case 5:
                return parseColor("#FFFFFF", 0.95f);
            case 6:
                return parseColor("#FFFFFF", 0.8f);
            case 7:
                return parseColor("#000000", 0.9f);
            case 8:
                return parseColor("#FFFFFF", 0.85f);
            case 9:
                return parseColor("#000000", 0.9f);
            case 10:
                return parseColor("#000000", 0.9f);
            case 11:
                return parseColor("#000000", 0.9f);
            case 12:
                return parseColor("#000000", 0.9f);
            case 13:
                return parseColor("#FFFFFF", 0.9f);
            case 14:
                return parseColor("#FFFFFF", 0.9f);
            case 15:
                return parseColor("#000000", 0.9f);
            default:
                return parseColor("#1F1F1F", 0.93f);
        }
    }

    /**
     * 该方法用于给颜色值添加透明度
     *
     * @param colorString：原颜色值
     * @param alpha：透明度
     * @return 添加透明度后的颜色值
     */
    private static int parseColor(String colorString, float alpha) {
        int parseColor = Color.parseColor(colorString);
        return Color.argb((int) (255.0f * alpha), Color.red(parseColor), Color.green(parseColor), Color.blue(parseColor));
    }
}
