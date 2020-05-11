package cn.haseo.daygram.util;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import cn.haseo.daygram.App;
import cn.haseo.daygram.R;

/**
 * 备份日记的任务
 */
public class BackupTask extends AsyncTask<Integer, Void, String> {
    // 获取 Application 实例
    private App app = App.getInstance();

    // 定义任务监听器
    private TaskListener taskListener;


    // 设置任务监听器
    public void setTaskListener(TaskListener taskListener) {
        this.taskListener = taskListener;
    }


    @Override
    @SuppressWarnings("all")
    protected String doInBackground(Integer... integers) {
        // 定义数据库文件
        File databaseFile = new File(app.getDatabasePath("Diary") + ".db");

        // 定义备份的目录
        File backupDir = new File(Environment.getExternalStorageDirectory(), "DayGram");
        // 当备份目录不存在时
        if (!backupDir.exists()) {
            // 创建备份目录
            backupDir.mkdirs();
        }
        // 定义数据库副本
        File backupFile = new File(backupDir, databaseFile.getName());

        Resources resources = app.getResources();
        // 根据参数执行相应的操作
        switch (integers[0]) {
            // 备份数据库
            case 0:
                // 当数据库不存在时
                if (!databaseFile.exists()) {
                    return resources.getString(R.string.database_not_found);
                }
                try {
                    // 创建副本文件
                    backupFile.createNewFile();
                    // 复制数据库到副本文件
                    copyFile(databaseFile, backupFile);
                } catch (Exception e) {
                    e.printStackTrace();
                    return resources.getString(R.string.backup_failed);
                }
                return resources.getString(R.string.backup_success);
            // 还原数据库
            case 1:
                // 当数据库副本不存在时
                if (!backupFile.exists()) {
                    return resources.getString(R.string.backup_not_found);
                }
                try {
                    // 复制数据库副本到数据库
                    copyFile(backupFile, databaseFile);
                } catch (Exception e) {
                    e.printStackTrace();
                    return resources.getString(R.string.restore_failed);
                }
                return resources.getString(R.string.restore_success);
            default:
                return resources.getString(R.string.unknown_error);
        }
    }

    @Override
    protected void onPostExecute(String s) {
        if (taskListener != null) {
            taskListener.onSuccess(s);
        }
    }


    /**
     * 该方法用于复制文件
     *
     * @param file1：文件 1
     * @param file2：文件 2
     * @throws IOException：IO 异常
     */
    private void copyFile(File file1, File file2) throws IOException {
        try (FileChannel inChannel = new FileInputStream(file1).getChannel();
             FileChannel outChannel = new FileOutputStream(file2).getChannel()) {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 任务监听接口
     */
    public interface TaskListener {
        void onSuccess(String result);
    }
}