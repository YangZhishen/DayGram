package cn.haseo.daygram.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.crud.LitePalSupport;

/**
 * 日记类
 */
public class Diary extends LitePalSupport implements Parcelable {
    // 定义日记的年份
    private int year;
    // 定义日记的月份
    private int month;
    // 定义日记的日期
    private int day;
    // 定义日记的星期
    private int week;
    // 定义日记的内容
    private String content;


    /**
     * 默认构造方法（LitePal 要求）
     */
    public Diary() {}

    /**
     * 自定义构造方法
     *
     * @param year：日记的年份
     * @param month：日记的月份
     * @param day：日记的日期
     * @param week：日记的星期
     * @param content：日记的内容
     */
    public Diary(int year, int month, int day, int week, String content) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.week = week;
        this.content = content;
    }


    /**
     * 内容接口描述
     *
     * @return 默认返回 0 就可以
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * 该方法执行序列化过程，将类的数据写入外部提供的 Parcel 中
     *
     * @param dest：外部提供的 Parcel 变量
     * @param flags：
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // 必须按照成员变量声明的顺序进行封装
        dest.writeInt(year);
        dest.writeInt(month);
        dest.writeInt(day);
        dest.writeInt(week);
        dest.writeString(content);
    }

    /**
     * 实例化静态内部对象 CREATOR 实现接口 Parcelable.Creator
     * 其中 public static final 一个都不能少，内部对象 CREATOR 的名称也不能改变，必须全部大写。
     */
    public static final Creator<Diary> CREATOR = new Creator<Diary>() {
        /**
         * 该方法执行反序列化过程，Parcel 数据来源于 writeToParcel 方法，读取 Parcel 的数据时必须按照成员变量声明的顺序
         * @param source：表示要读取的数据源
         * @return diary 读取出来的日记对象
         */
        @Override
        public Diary createFromParcel(Parcel source) {
            // 创建日记对象
            return new Diary(source.readInt(), source.readInt(), source.readInt(), source.readInt(), source.readString());
        }

        /**
         * 该方法供外部类反序列化本类数组使用
         * @param size：数组大小
         * @return 返回对应大小的本类数组
         */
        @Override
        public Diary[] newArray(int size) {
            return new Diary[size];
        }
    };

    /**
     * 获取日记的年份
     *
     * @return 日记的年份
     */
    public int getYear() {
        return year;
    }

    /**
     * 获取日记的月份
     *
     * @return 日记的月份
     */
    public int getMonth() {
        return month;
    }

    /**
     * 获取日记的日期
     *
     * @return 日记的日期
     */
    public int getDay() {
        return day;
    }

    /**
     * 获取日记的星期
     *
     * @return 日记的星期
     */
    public int getWeek() {
        return week;
    }

    /**
     * 获取日记的内容
     *
     * @return 日记的内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置日记的内容
     *
     * @param content：日记的内容
     */
    public void setContent(String content) {
        this.content = content;
    }
}
