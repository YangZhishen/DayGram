package cn.haseo.daygram.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Scroller;

import java.lang.reflect.Field;

import cn.haseo.daygram.App;
import cn.haseo.daygram.R;

/**
 * 自定义回弹 ListView
 */
public class MyListView extends ListView implements AbsListView.OnScrollListener {
    // 创建 Handler 变量
    private Handler handler = new Handler();

    // 定义滚动器（用于滚动的辅助计算）
    private Scroller scroller;

    // 定义手指按下时的屏幕 Y 坐标
    private float downY;
    // 定义手指滑动时的初始 Y 坐标
    private float moveY;
    // 创建最大过载滑动距离
    private int maxOverScrollY = App.getInstance().dpToPx(100.0f);

    // 标记 ListView 是否滑动到顶部
    private boolean isTop = false;
    // 标记 ListView 是否滑动到底部
    private boolean isBottom = false;
    // 标记 ListView 是否从顶部下拉
    private boolean isPullDown = false;
    // 标记 ListView 是否从底部上拉
    private boolean isPullUp = false;
    // 标记是否已记录手指滑动时的初始 Y 坐标
    private boolean isRecord = false;
    // 标记 ListView 能否滑动
    private boolean isBlock = false;

    // 定义 ListView 的头布局
    private View header;
    // 创建头布局的高度
    private int headerHeight = App.getInstance().dpToPx(80.0f);

    // 定义滑动监听器
    private OnSwipeListener listener;


    /**
     * 该方法设置滑动监听器
     * @param listener：滑动监听接口
     */
    public void setOnSwipeListener(OnSwipeListener listener) {
        this.listener = listener;
    }


    /**
     * 三个构造方法都要重写
     */
    public MyListView(Context context) {
        super(context);
        initView(context);
    }

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public MyListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }


    /**
     * 该方法用于初始化 ListView
     * @param context：来自构造方法的上下文
     */
    private void initView(Context context) {
        // 设置滚动监听
        setOnScrollListener(this);

        // 创建带加速插值器的滚动器（滚动动画为加速滚动效果）
        scroller = new Scroller(context, new AccelerateInterpolator());

        // 实例化头布局
        header = LayoutInflater.from(context).inflate(R.layout.today_header, this, false);
        // 将头布局添加到 ListView
        addHeaderView(header, null, false);
        // 将头布局的 paddingTop 设置为头布局高度的负值，让其处于隐藏状态
        setHeaderPaddingTop(-headerHeight);

        // 利用 Java 反射将 ListView 滑动到边界时的弧光颜色设为透明
        try {
            // 获取 AbsListView 类的 class 对象实例
            Class<?> absListView = Class.forName(AbsListView.class.getName());
            // 获取 AbsListView 类的 EdgeGlowTop 字段（EdgeEffect 类型）
            Field egtField = absListView.getDeclaredField("mEdgeGlowTop");
            // 获取 AbsListView 类的 mEdgeGlowBottom 字段（EdgeEffect 类型）
            Field egbField = absListView.getDeclaredField("mEdgeGlowBottom");
            // 设置 egtField 字段为允许访问（不会改变其原本的权限修饰词）
            egtField.setAccessible(true);
            // 设置 egbBottom 字段为允许访问（不会改变其原本的权限修饰词）
            egbField.setAccessible(true);
            // 获取 ListView 中的 mEdgeGlowTop 变量实例（ListView 的父类是 AbsListView）
            Object mEdgeGlowTop = egtField.get(this);
            // 获取 ListView 中的 mEdgeGlowBottom 变量实例（ListView 的父类是 AbsListView）
            Object mEdgeGlowBottom = egbField.get(this);

            // 获取 EdgeEffect 类的 class 对象实例（EdgeEffect 类拥有 mGlow、mEdge 两个字段）
            Class<?> edgeEffect = Class.forName(mEdgeGlowTop.getClass().getName());

            // 获取 EdgeEffect 类的 mGlow 字段（Drawable 类型）
            Field mGlow = edgeEffect.getDeclaredField("mGlow");
            // 设置 mGlow 字段为允许访问（不会改变其原本的权限修饰词）
            mGlow.setAccessible(true);
            // 调用 mGlow 字段的 set 方法将 mEdgeGlowTop 实例的 mGrow 变量设为透明 Drawable
            mGlow.set(mEdgeGlowTop, new ColorDrawable(Color.TRANSPARENT));
            // 调用 mGlow 字段的 set 方法将 mEdgeGlowBottom 实例的 mGrow 变量设为透明 Drawable
            mGlow.set(mEdgeGlowBottom, new ColorDrawable(Color.TRANSPARENT));

            // 获取 EdgeEffect 类的 mEdge 字段（Drawable 类型）
            Field mEdge = edgeEffect.getDeclaredField("mEdge");
            // 设置 mEdge 字段为允许访问（不会改变其原本的权限修饰词）
            mEdge.setAccessible(true);
            // 调用 mEdge 字段的 set 方法将 mEdgeGlowTop 实例的 mEdge 变量设为透明 Drawable
            mEdge.set(mEdgeGlowTop, new ColorDrawable(Color.TRANSPARENT));
            // 调用 mEdge 字段的 set 方法将 mEdgeGlowBottom 实例的 mEdge 变量设为透明 Drawable
            mEdge.set(mEdgeGlowBottom, new ColorDrawable(Color.TRANSPARENT));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 该方法设置头布局的 paddingTop 并重绘
     * @param paddingTop：头布局的顶部内偏距
     */
    private void setHeaderPaddingTop(int paddingTop) {
        // 设置头布局各个方向的 padding 值
        header.setPadding(header.getPaddingLeft(), paddingTop, header.getPaddingRight(), header.getPaddingBottom());
        // 重新绘制头布局
        header.invalidate();
    }


    /**
     * 该方法用于分发屏幕触摸事件
     * @param ev：与屏幕触摸相关的事件变量
     * @return true 表示分发给 onTouchEvent 处理， false 表示不处理该事件
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            // 当手指按到屏幕上时
            case MotionEvent.ACTION_DOWN:
                // 获取手指按下时的 Y 坐标
                downY = ev.getY();
                break;
            // 当手指在屏幕上滑动时
            case MotionEvent.ACTION_MOVE:
                // 标记 ListView 是否顶部下拉
                isPullDown = isTop && ev.getY() - downY > 0;
                // 标记 ListView 是底部否上拉
                isPullUp = isBottom && ev.getY() - downY < 0;
                // 当 ListView 不可滑动时
                if (isBlock) {
                    // 不处理滑动事件
                    return false;
                }
                break;
        }

        // 调用父类的方法继续分发屏幕触摸事件
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 该方法用于处理屏幕触摸事件
     * @param ev：与屏幕触摸相关的事件变量
     */
    @Override
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            // 当手指按到屏幕上时
            case MotionEvent.ACTION_DOWN:
                // 强制停止滚动
                scroller.forceFinished(true);
                break;
            // 当手指在屏幕上滑动时
            case MotionEvent.ACTION_MOVE:
                // 当 ListView 处于顶部下拉或者底部上拉状态
                if (isPullDown || isPullUp) {
                    // 当没有记录滑动的初始 Y 坐标时
                    if (!isRecord) {
                        // 记录滑动的初始 Y 坐标
                        moveY = ev.getY();
                        // 只记录一次
                        isRecord = true;
                    }
                    // 执行拖拽函数
                    onPull(ev);
                }
                break;
            // 当手指离开屏幕时
            case MotionEvent.ACTION_UP:
                // 执行离开屏幕时的一些操作
                listener.onRelease(isBlock);
                // 执行回弹函数
                springBack();
                // 取消 ListView 顶部下拉标记
                isPullDown = false;
                // 取消 ListView 底部上拉标记
                isPullUp = false;
                // 取消已记录滑动初始 Y 坐标的标记
                isRecord = false;
                // 恢复 ListView 为可滑动状态
                isBlock = false;
                break;
        }

        // 调用父类的方法继续处理屏幕触摸事件
        return super.onTouchEvent(ev);
    }

    /**
     * 该方法实现 ListView 顶部下拉和底部上拉的拖拽效果
     * @param ev：与用户触摸相关的事件变量
     */
    private void onPull(MotionEvent ev) {
        // 清空当前所有消息队列，防止正在回弹时，用户又滑动产生冲突
        handler.removeCallbacksAndMessages(null);

        // 计算手指的滑动距离（除以一定的系数是为了产生粘滞效果）
        int deltaY = (int) ((ev.getY() - moveY) / 2);

        // 当 ListView 顶部下拉时
        if (isPullDown) {
            // 当 ListView 顶部出现并且下拉距离小于头布局的高度时
            if (deltaY < headerHeight) {
                // 计算头布局还有多少高度未显示
                int pt = deltaY - headerHeight;
                // 即时设置头布局的隐藏高度
                setHeaderPaddingTop(pt);
            }
        } else if (isPullUp){
            // 当滑动距离超过最大过载滑动距离时
            if (-deltaY > maxOverScrollY) {
                // 将滑动距离设置为最大过载滑动距离
                deltaY = -maxOverScrollY;

                // 将 ListView 标记为不可滑动状态
                isBlock = true;
            }

            // 滑动至指定位置
            smoothScrollTo(-deltaY);

            // 执行滑动过程中的一些操作
            listener.onSwipe(-deltaY, maxOverScrollY);
        }
    }

    /**
     * 该方法实现 ListView 的越界回弹效果
     */
    private void springBack() {
        // 获取头布局当前的 paddingTop 值
        int headerPaddingTop = header.getPaddingTop();

        // 当 ListView 处于顶部下拉状态时
        if (headerPaddingTop > -headerHeight) {
            // 选中头布局之下的第一个子项，防止回弹时该子项被拉出屏幕之外
            setSelection(0);

            // 定义 Handler 发送消息的延迟时间，并随着循环增加， 好让前面的消息有足够的处理时间
            int duration = 0;

            // 当头布局未复位时，执行回弹操作
            while (headerPaddingTop > -headerHeight) {
                headerPaddingTop -= 10;
                duration += 10;
                final int pt = headerPaddingTop;

                header.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (pt < -headerHeight) {
                            // 如果回弹的距离超过头布局的高度时，则恢复初始状态，防止越界
                            setHeaderPaddingTop(-headerHeight);
                        } else {
                            // 即时设置头布局的 paddingTop 值
                            setHeaderPaddingTop(pt);
                        }
                    }
                }, duration);
            }
        }

        // 恢复 ListView 底部默认位置
        smoothScrollTo(0);
    }

    /**
     * 该方法使用动画滚动至指定位置
     * @param endY：要前往的屏幕坐标
     */
    private void smoothScrollTo(int endY) {
        // 使用动画滚动到指定位置
        scroller.startScroll(0, scroller.getFinalY(), 0, endY - scroller.getFinalY(), 300);
        // 重新绘制 ListView
        invalidate();
    }


    /**
     * 该方法用于控制滚动
     */
    @Override
    public void computeScroll() {
        // 判断滚动器的滚动操作是否完成，true 表示未完成
        if (scroller.computeScrollOffset()) {
            // 调用 ListView 的 scrollTo 方法完成滚动
            scrollTo(0, scroller.getCurrY());
            // 重新绘制 ListView
           invalidate();
        }
        super.computeScroll();
    }

    /**
     * 该方法是 OnScrollListener 接口的抽象函数，监听当前滚动的 Item（包括头布局和脚部局）
     * @param view：AbsListView 是 ListView 的父类，是用于实现条目的虚拟列表的基类，这里的列表没有空间的定义
     * @param firstVisibleItem：代表屏幕当前可见的第一个子项的索引
     * @param visibleItemCount：代表屏幕当前可见的子项总数，包括没有完整显示的子项
     * @param totalItemCount：代表 ListView 的子项总数
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // 当 ListView 第一个可见子项的索引为 0 时
        if (firstVisibleItem == 0) {
            // 获取 ListView 的第一个可见子项
            View firstVisibleItemView = getChildAt(0);
            // 当 ListView 第一个可见子项不为空且子项顶部的坐标为 0 时，标记 ListView 到达顶部
            isTop = firstVisibleItemView != null && firstVisibleItemView.getTop() == 0;
        }

        // 当 ListView 可见子项的总数等于 ListView 子项的总数时（数据不满屏）
        if (visibleItemCount == totalItemCount) {
            // 标记 ListView 到达底部
            isBottom = true;
        } else if ((firstVisibleItem + visibleItemCount) == totalItemCount) {
            // 当 ListView 第一个可见子项与最后一个可见子项的索引之和等于 ListView 子项总数时，获取 ListView 最后一个可见子项（数据满屏）
            View lastVisibleItemView = getChildAt(getChildCount() - 1);
            // 当 ListView 最后一个可见子项不为空并且子项的底部坐标为 ListView 的高度时，标记 ListView 到达底部
            isBottom = lastVisibleItemView != null && lastVisibleItemView.getBottom() == getHeight();
        }
    }

    /**
     * 该方法用于控制 ListView 的越界效果
     * @param deltaX：X 轴方向上将要滚动的偏移量
     * @param deltaY：Y 轴方向上将要滚动的便宜里
     * @param scrollX：当前所位于的 X 坐标
     * @param scrollY：当前所位于的 Y 坐标
     * @param scrollRangeX：X 轴方向上可以滚动的范围
     * @param scrollRangeY：Y 轴方向上可以滚动的范围
     * @param maxOverScrollX：X 轴方向上允许超过滚动范围的最大值
     * @param maxOverScrollY：Y 轴方向上允许超过滚动范围的最大值
     * @param isTouchEvent：是否由 TouchEvent 产生的越界
     * @return true 事件不在继续向下传递，false 事件继续向下传递
     */
    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        // 将 Y 轴方向上允许超过滚动范围的最大值设为 0（由 Scroller 来控制超过 Y 轴滚动范围时的滚动效果）
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, 0, isTouchEvent);
    }

    /**
     * 该方法是 OnScrollListener 接口的抽象函数，监听 ListView 当前的滚动状态
     * @param view：AbsListView 是 ListView 的父类，是用于实现条目的虚拟列表的基类，这里的列表没有空间的定义
     * @param scrollState：代表 ListView 当前的滚动状态
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {}


    /**
     * 自定义滑动监听接口
     */
    public interface OnSwipeListener {
        // 执行手指滑动时的操作
        void onSwipe(int deltaY, int maxOverScrollY);
        // 执行手指抬起时的操作
        void onRelease(boolean isBlock);
    }
}