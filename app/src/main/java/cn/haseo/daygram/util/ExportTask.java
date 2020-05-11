package cn.haseo.daygram.util;

import android.os.AsyncTask;

import org.litepal.LitePal;

import java.util.List;

import cn.haseo.daygram.App;
import cn.haseo.daygram.model.Diary;


/**
 * 导出日记的任务
 */
public class ExportTask extends AsyncTask<Integer, Void, Integer> {
    // 获取 Application 实例
    private App app = App.getInstance();

    // 定义导出的内容
    private String dayGrams;
    // 定义任务监听器
    private TaskListener taskListener;


    // 设置任务监听器
    public void setTaskListener(TaskListener taskListener) {
        this.taskListener = taskListener;
    }


    /**
     * 该方法所有代码都会在子线程中执行，用于处理耗时任务
     *
     * @param integers：执行任务时需要传进来的参数
     * @return 任务结果
     */
    @Override
    protected Integer doInBackground(Integer... integers) {
        // 根据参数执行相应的操作
        switch (integers[0]) {
            // 导出日记到邮件
            case 0:
                export("%s %d %s %d");
                return 0;
            // 导出日记到邮件
            case 1:
                export("** %s %d %s %d **");
                return 1;
            // 不执行任何操作
            default:
                return 2;
        }
    }

    /**
     * 当任务执行完毕时这个方法就会被调用，可以利用返回来的结果执行一些 UI 操作
     *
     * @param integer：任务结果
     */
    @Override
    protected void onPostExecute(Integer integer) {
        if (taskListener != null) {
            taskListener.onSuccess(integer, dayGrams);
        }
    }


    /**
     * 该方法导出日记
     *
     * @param format：字符串格式
     */
    private void export(String format) {
        List<Diary> results = LitePal.order("year, month, day").find(Diary.class);

        StringBuilder builder = new StringBuilder();

        for (Diary diary : results) {
            String date = String.format(format, app.getMonth(diary.getMonth()), diary.getDay(), app.getWeek(diary.getWeek()), diary.getYear()) + "\n\n";
            builder.append(date);
            String content = diary.getContent() + "\n\n";
            builder.append(content);
        }

        dayGrams = builder.toString();
    }


    /**
     * 任务监听接口
     */
    public interface TaskListener {
        void onSuccess(int result, String dayGrams);
    }
}
