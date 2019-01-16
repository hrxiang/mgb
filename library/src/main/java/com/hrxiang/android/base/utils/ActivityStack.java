package com.hrxiang.android.base.utils;

import android.app.Activity;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by xianghairui on 2018/12/13.
 * index == 0 栈顶
 * index == size -1 栈底
 */
public final class ActivityStack {
    private static List<WeakReference<Activity>> mActivityStacks =
            Collections.synchronizedList(new LinkedList<WeakReference<Activity>>());

    private ActivityStack() {
    }

    public static boolean isStackTopActivity(Activity ac) {
        return !mActivityStacks.isEmpty()
                && ac == mActivityStacks.get(0).get();
    }

    public Activity getStackTopActivity() {
        if (!mActivityStacks.isEmpty()) return mActivityStacks.get(0).get();
        return null;
    }

    /**
     * *ndex == 0 栈顶
     * index == size -1 栈底
     * 移动activity到栈顶,并将在这之上的activity全部finish掉
     */
    public static void moveActivityToStackTop(Activity ac) {
        if (!mActivityStacks.isEmpty()) {
            Iterator<WeakReference<Activity>> it = mActivityStacks.iterator();
            while (it.hasNext()) {
                Activity activity = it.next().get();
                if (ac != activity) {
                    it.remove();
                    finish(activity);
                    continue;
                }
                break;
            }
        }
    }

    public static boolean isExist(String classname) {
        for (WeakReference<Activity> mActivityStack : mActivityStacks) {
            Activity ac = mActivityStack.get();
            if (null == ac || ac.getClass().getName().equals(classname)) {
                return true;
            }
        }
        return false;
    }

    /**
     * finish掉除ac以外的所有activity，并且只保留一个ac
     */
    public static void finishExcept(Activity ac) {
        for (int i = mActivityStacks.size() - 1; i >= 0; i--) {
            Activity activity = mActivityStacks.get(i).get();
            if (null != activity && activity == ac) {
                continue;
            }
            finish(mActivityStacks.remove(i).get());
        }
    }

    public static void push(Activity ac) {
        push(ac, 0);// 栈顶 index == 0
    }

    /**
     * 当前ac最多存在count个
     *
     * @param count 栈里当前activity的数量最多count个
     */
    public static void push(Activity ac, int count) {
        mActivityStacks.add(0, element(ac));
        if (count > 0) {
            int exist = 0;
            for (int i = 0; i < mActivityStacks.size(); i++) {
                Activity activity = mActivityStacks.get(i).get();
                if (null != activity && classNameEquals(activity, ac)) {
                    exist++;
                }
            }

            int delete = exist - count;
            if (delete > 0) {
                for (int i = mActivityStacks.size() - 1; i >= 0; i--) {
                    if (delete == 0) break;
                    Activity activity = mActivityStacks.get(i).get();
                    if (null != activity && classNameEquals(activity, ac)) {
                        delete--;
                        finish(mActivityStacks.remove(i).get());
                    }
                }
            }
        }
    }

    /**
     * 出stack，不会finish出栈的activity
     */
    public static void pop(Activity ac) {
        if (!mActivityStacks.isEmpty()) {
            Iterator<WeakReference<Activity>> it = mActivityStacks.iterator();
            while (it.hasNext()) {
                Activity activity = it.next().get();
                if (ac == activity) {
                    it.remove();
                    break;
                }
            }
        }
    }

    public static void exitApp() {
        if (!mActivityStacks.isEmpty()) {
            for (int i = mActivityStacks.size() - 1; i >= 0; i--) {
                finish(mActivityStacks.remove(i).get());
            }
        }
    }

    public static void clear() {
        mActivityStacks.clear();
    }

    private static boolean classNameEquals(Activity ac1, Activity ac2) {
        return null != ac1 && ac1.getClass().getName().equals(ac2.getClass().getName());
    }

    private static WeakReference<Activity> element(Activity ac) {
        return new WeakReference<>(ac);
    }

    private static void finish(Activity ac) {
        if (null != ac) {
            ac.finish();
        }
    }
}
