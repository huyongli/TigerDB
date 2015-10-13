package cn.ittiger.log;

import android.util.Log;

/**
 * 日志管理器
 * @author: huylee
 * @time:	2015-8-12下午10:42:07
 */
public final class TigerLog {
	/**
	 * 日志标签
	 */
	private static final String TAG = "ItTiger";
	/**
	 * 是否调试模式
	 */
	private static boolean IS_DEBUG = true;
	
	public static boolean isDebug() {
		return IS_DEBUG;
	}
	
	/**
	 * 是否开启调试模式
	 * Author: hyl
	 * Time: 2015-8-13上午9:55:41
	 * @param enable
	 */
	public static final void debugEnable(boolean enable) {
		IS_DEBUG = enable;
	}
	
	public static final void debug(Object msg) {
		debug(msg.toString());
	}
	
	public static final void debug(String msg) {
		if(IS_DEBUG) {
			Log.d(TAG, msg);
		}
	}
	
	public static final void debug(String msg, Throwable e) {
		if(IS_DEBUG) {
			Log.d(TAG, msg, e);
		}
	}
	
	public static final void warn(Object msg) {
		warn(msg.toString());
	}
	
	public static final void warn(String msg) {
		if(IS_DEBUG) {
			Log.w(TAG, msg);
		}
	}
	
	public static final void warn(String msg, Throwable e) {
		if(IS_DEBUG) {
			Log.w(TAG, msg, e);
		}
	}
	
	public static final void info(Object msg) {
		info(msg.toString());
	}
	
	public static final void info(String msg) {
		if(IS_DEBUG) {
			Log.i(TAG, msg);
		}
	}
	
	public static final void info(String msg, Throwable e) {
		if(IS_DEBUG) {
			Log.i(TAG, msg, e);
		}
	}
	
	public static final void error(Object msg) {
		error(msg.toString());
	}
	
	public static final void error(String msg) {
		if(IS_DEBUG) {
			Log.i(TAG, msg);
		}
	}
	
	public static final void error(String msg, Throwable e) {
		if(IS_DEBUG) {
			Log.e(TAG, msg, e);
		}
	}
	
	public static final void info(String sql, Object[] params) {
		if(IS_DEBUG) {
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < params.length; i++) {
				if(sb.length() <= 0) {
					sb.append(",");
				}
				sb.append(params.toString());
			}
			Log.i(TAG, "{SQL:" + sql + ",PARAMS：" + sb.toString() + "}");
		}
	}
	
	public static final void warn(String sql, Object[] params, Throwable e) {
		if(IS_DEBUG) {
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < params.length; i++) {
				if(sb.length() <= 0) {
					sb.append(",");
				}
				sb.append(params.toString());
			}
			Log.w(TAG, "{SQL:" + sql + ",PARAMS：" + sb.toString() + "}", e);
		}
	}
	
	 /**
     * @return 当前的类名（全名）
     */
    private static String getClassName() {
        String result;
        StackTraceElement thisMethodStack = (new Exception()).getStackTrace()[1];
        result = thisMethodStack.getClassName();
        return result;
    }

    /**
     * log这个方法就可以显示超链
     */
    private static String callMethodAndLine() {
        String result = "at ";
        StackTraceElement thisMethodStack = (new Exception()).getStackTrace()[1];
        result += thisMethodStack.getClassName()+ ".";
        result += thisMethodStack.getMethodName();
        result += "(" + thisMethodStack.getFileName();
        result += ":" + thisMethodStack.getLineNumber() + ")  ";
        return result;
    }
}
