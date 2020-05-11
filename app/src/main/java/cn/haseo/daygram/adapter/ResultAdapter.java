package cn.haseo.daygram.adapter;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;
import java.util.regex.Pattern;

import cn.haseo.daygram.App;
import cn.haseo.daygram.R;
import cn.haseo.daygram.model.Diary;
import cn.haseo.daygram.widget.MyTextView;

/**
 * 显示搜索结果的 ListView 适配器
 */
public class ResultAdapter extends BaseAdapter {
    // 获取 Application 实例
    public App app = App.getInstance();

    // 定义日记的搜索关键字
    private String keyword = "";
    // 定义日记的搜索结果集合
    private List<Diary> results;
    // 定义过滤空白字符的正则表达式，Pattern.CASE_INSENSITIVE 表示忽略大小写
    private Pattern pattern = Pattern.compile("\\s+|\\n+", Pattern.CASE_INSENSITIVE);


    // 设置日记的搜索关键字
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }


    // 构造方法
    public ResultAdapter(List<Diary> results) {
        this.results = results;
    }


    /**
     * 该方法获取 ListView 的子项数目
     *
     * @return 全日记集合的长度
     */
    @Override
    public int getCount() {
        return results.size();
    }


    /**
     * 该方法创建 ListView 的子项视图
     *
     * @param position：代表子项的位置（不包含头布局）
     * @param convertView：代表子项的缓存
     * @param parent：代表适配器所绑定的         ListView
     * @return 创建好的子项
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 获取子项所对应的日记
        Diary diary = results.get(position);

        // 定义子项
        View view;
        // 定义子项持有者
        ViewHolder viewHolder;

        // 当子项缓存为空时
        if (convertView == null) {
            // 创建子项
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.result_item, parent, false);
            // 创建子项持有者
            viewHolder = new ViewHolder(view);
            // 绑定子项持有者
            view.setTag(viewHolder);
        } else {
            // 当子项缓存不为空时，复用子项缓存
            view = convertView;
            // 取出绑定的子项持有者
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // 将日记内容中的所有空白类字符替换成 " "
        String content = pattern.matcher(diary.getContent()).replaceAll(" ");
        // 统计关键字出现的次数
        int[] searchResult = countKeyword(content, keyword);
        // 当搜索结果为空时
        if (searchResult == null) {
            throw new IllegalStateException("countKeyword return null!");
        }

        // 获取最后一个相匹配的内容的起始坐标
        int lastMatchIndex = searchResult[0];
        // 定义搜索结果的起始坐标
        int searchResultStart = 0;
        // 从 lastMatchIndex 的前 16 个字符开始向前搜索 " "
        int tempStart = lastMatchIndex - 16;
        if (tempStart >= 0) {
            searchResultStart = tempStart;
            while (searchResultStart > 0) {
                // 当搜索的内容中含有 " " 时
                if (content.charAt(searchResultStart) == ' ') {
                    // 获取 " " 后面一个字符的坐标
                    searchResultStart++;
                    // 跳出循环
                    break;
                }
                // 继续向前搜索
                searchResultStart--;
            }
        }

        // 定义搜索结果的终止坐标
        int searchResultEnd = searchResultStart + 180;
        if (searchResultEnd > content.length()) {
            searchResultEnd = content.length();
        }
        // 从日记内容中截取搜索结果
        String substring = content.substring(searchResultStart, searchResultEnd);

        // 创建 SpannableString 变量 style
        SpannableString style = new SpannableString(substring);
        // 获取关键字在搜索结果中的起始坐标
        int keyWordStart = substring.toLowerCase().indexOf(keyword.toLowerCase());
        // 设置关键字的颜色
        style.setSpan(new ForegroundColorSpan(Color.parseColor("#8C8C8C")), keyWordStart, keyword.length() + keyWordStart, Spannable.SPAN_POINT_MARK);
        // 显示日记的搜索结果
        viewHolder.searchResult.setText(style, MyTextView.BufferType.SPANNABLE);

        // 显示日记的日期
        viewHolder.diaryDate.setText(String.format(parent.getContext().getResources().getString(R.string.diary_date),
                app.getMonth(diary.getMonth()), diary.getDay(), diary.getYear(), app.getWeek(diary.getWeek())));

        // 显示相匹配的内容数目
        viewHolder.resultCount.setText(String.format(parent.getContext().getResources().getString(R.string.result_count), searchResult[1]));

        // 返回创建好的子项
        return view;
    }

    /**
     * 该方法负责统计相匹配的内容数量
     *
     * @param content：日记内容
     * @param keyword：关键字
     * @return int[] 返回搜索结果
     */
    private int[] countKeyword(String content, String keyword) {
        // 当日记的内容为空或者关键字为空时
        if (content == null || keyword == null) {
            // 返回空
            return null;
        }

        // 将日记内容中的所有大写字母转换成小写
        String content1 = content.toLowerCase();
        // 将关键字中的所有大写字母转换成小写
        String keyword1 = keyword.toLowerCase();

        // 定义搜索的起始坐标
        int searchIndex = 0;
        // 定义相匹配的内容数量
        int countKeyword = 0;
        // 定义最后一个相匹配的内容的起始坐标
        int lastMatchIndex = 0;

        while (true) {
            // 获取相匹配的内容的起始坐标
            int matchIndex = content1.indexOf(keyword1, searchIndex);

            // 当没有内容相匹配时，返回搜索结果
            if (matchIndex == -1) {
                return new int[]{lastMatchIndex, countKeyword};
            }

            // 检索下一个内容
            searchIndex = keyword1.length() + matchIndex;

            // 统计相匹配的内容的数量
            countKeyword++;

            // 储存最后一个相匹配的内容的起始坐标
            lastMatchIndex = matchIndex;
        }
    }


    /**
     * 该方法获取 ListView 子项所对应的数据
     *
     * @param position：代表子项的位置（不包含头布局）
     * @return 子项所对应的数据
     */
    @Override
    public Object getItem(int position) {
        return results.get(position);
    }

    /**
     * 该方法获取 ListView 子项的 id
     *
     * @param position：代表子项的位置（不包含头布局）
     * @return 子项的 id
     */
    @Override
    public long getItemId(int position) {
        return position;
    }


    /**
     * 自定义的 ViewHolder
     */
    private static class ViewHolder {
        // 定义显示搜索结果的 TextView
        MyTextView searchResult;
        // 定义显示日记日期的 TextView
        MyTextView diaryDate;
        // 定义显示匹配数目的 TextView
        MyTextView resultCount;

        /**
         * 构造方法
         *
         * @param itemView：ListView 子项
         */
        ViewHolder(View itemView) {
            // 实例化各控件
            searchResult = itemView.findViewById(R.id.search_result);
            diaryDate = itemView.findViewById(R.id.diary_date);
            resultCount = itemView.findViewById(R.id.result_count);
        }
    }
}
