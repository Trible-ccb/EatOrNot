package com.android.eatingornot.weibo;
import android.app.Activity;
import android.view.Gravity;
import android.widget.Toast;

/**
 * 本类提供notifcation的实现方法供�?��类调�?
 */
public class Notification {
    private Activity mActivity = null;
    public Notification(Activity activity){
        mActivity = activity;
    }
    /**
     * 显示�?��toast窗口,以长时间显示，用户不用指定显示时�?
     *
     * @param message
     *            要显示的信息
     */
    public void toast(final String message) {
        toast(message, Toast.LENGTH_LONG);
    }

    /**
     * 显示�?��toast窗口,用户�?��指定显示时间
     *
     * @param message
     *            要显示的信息
     * @param duration
     *            显示toast的时�?�?<br>
     */
    public void toast(final String message, final int duration) {
        Runnable runnable = new Runnable() {
            public void run() {
                Toast toast = Toast.makeText(mActivity, message,
                        duration);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        };
        mActivity.runOnUiThread(runnable);
    }
}
