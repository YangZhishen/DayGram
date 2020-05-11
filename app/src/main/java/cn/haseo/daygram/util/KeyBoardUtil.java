package cn.haseo.daygram.util;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import cn.haseo.daygram.ContentActivity;

/**
 * 该类用于用于解决软键盘 BUG
 */
public class KeyBoardUtil {
    // 定义状态栏高度
    private int statusBarHeight;

    // 定义活动布局（setContentView 引入的布局）
    private View mChildOfContent;
    // 定义活动布局前一次变化时的可用高度
    private int lastUsableHeight;
    // 定义活动布局的布局参数
    private FrameLayout.LayoutParams frameLayoutParams;

    // 定义输入状态变化的处理接口
    public interface OnSoftKeyboardStateChangedListener {
        void OnSoftKeyboardStateChanged(int visibility, int KeyBoardHeight);
    }

    // 定义输入状态变化的接口变量
    private OnSoftKeyboardStateChangedListener onSoftKeyboardStateChanged;

    /**
     * 该方法设置输入状态监听器
     *
     * @param listener：输入状态变化的接口变量
     */
    private void addSoftKeyboardChangedListener(OnSoftKeyboardStateChangedListener listener) {
        this.onSoftKeyboardStateChanged = listener;
    }


    /**
     * 构造方法
     *
     * @param activity：需要解决软键盘挡住工具栏问题的活动
     */
    private KeyBoardUtil(final Activity activity) {
        // 获取状态栏控件的 id
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        // 获取状态栏的高度（getDimensionPixelSize 方法获得的高度是四舍五入后的）
        statusBarHeight = activity.getResources().getDimensionPixelSize(resourceId);

        // 获取活动布局的根布局（该布局为 FrameLayout 类型）
        FrameLayout content = activity.findViewById(android.R.id.content);
        // 获取活动布局
        mChildOfContent = content.getChildAt(0);
        // 为活动布局的视图树观察者设置全局布局监听，当视图树中全局布局发生改变或者视图树中的某个视图的可视状态发生改变都会调用此函数
        mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                // 重新调整活动布局的大小并重绘
                possiblyResizeChildOfContent(activity);
            }
        });
        // 获取活动布局的布局参数（布局参数包含了子控件的位置、宽高等信息），子布局的布局参数类型必须与父布局一样采用 FrameLayout 类型
        frameLayoutParams = (FrameLayout.LayoutParams) mChildOfContent.getLayoutParams();
    }

    /**
     * 该方法计算活动布局当前的可用高度
     *
     * @return 活动布局当前的可用高度
     */
    private int computeUsableHeight() {
        // 创建矩形变量 rect
        Rect rect = new Rect();
        // 获取活动布局当前可视区域的大小，并存放到变量 rect 中
        mChildOfContent.getWindowVisibleDisplayFrame(rect);
        // 活动布局的可视高度等于 rect.bottom - rect.top，全屏模式下 rect.top = 0，直接返回 rect.bottom 即可
        return rect.bottom;
    }

    /**
     * 该方法重新调整活动布局的大小并重绘
     */
    private void possiblyResizeChildOfContent(Activity activity) {
        // 获取没有弹出软键盘时活动布局的根布局高度
        int usableHeightSansKeyboard = mChildOfContent.getRootView().getHeight();


        // 计算活动布局当前的可用高度
        int usableHeight = computeUsableHeight();
        // 当可用高度与之前记录的不一致时
        if (usableHeight != lastUsableHeight) {
            // 计算布局变化前后的高度差
            int heightDifference = usableHeightSansKeyboard - usableHeight;

            // 当高度差大于没有键盘时的根布局高度的 1/4 时，表示键盘已经弹出
            if (heightDifference > (usableHeightSansKeyboard / 4)) {
                // 当活动是 ContentActivity 的实例时
                if (activity instanceof ContentActivity) {
                    // 重新设置活动布局的可用高度
                    frameLayoutParams.height = usableHeight + statusBarHeight;
                }

                // 当软键盘收起时
                onSoftKeyboardStateChanged.OnSoftKeyboardStateChanged(View.VISIBLE, heightDifference);
            } else {
                // 否则表示键盘已收起，重新设置活动布局的可用高度
                frameLayoutParams.height = usableHeightSansKeyboard;

                // 当软键盘收起时
                onSoftKeyboardStateChanged.OnSoftKeyboardStateChanged(View.GONE, heightDifference);
            }
            // 重新绘制活动布局
            mChildOfContent.requestLayout();
            // 记录这次变化的可用高度
            lastUsableHeight = usableHeight;
        }
    }


    /**
     * 连接需要解决软键盘挡住工具栏问题的活动
     *
     * @param activity：需要解决软键盘挡住工具栏问题的活动
     */
    public static void assistActivity(Activity activity) {
        KeyBoardUtil keyBoardUtil = new KeyBoardUtil(activity);
        keyBoardUtil.addSoftKeyboardChangedListener((OnSoftKeyboardStateChangedListener) activity);
    }
}