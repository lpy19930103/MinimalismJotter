package com.lipy.jotter.utils

import android.util.Log
import com.lipy.jotter.constants.JotterConfig
import java.io.PrintWriter
import java.io.StringWriter

/**
 * Log输出
 * Created by lipy on 2014/7/29.
 */
object Logger {

    var openLog = JotterConfig.TRIVIA_LOG_PRINT

    var fileLog = JotterConfig.TRIVIA_FILE_LOG_PRINT

    var TAG = "JOTTER"

    fun d(msg: String) {
        d(null, msg)
    }


    fun d(msg: String, tr: Throwable) {
        d(null, msg, tr)

    }

    @JvmOverloads fun d(tag: String?, msg: String, tr: Throwable? = null) {
        if (openLog) {
            dealMsg(LogType.d, dealTag(tag), msg + getErrorInfo(tr))
        }
    }

    fun e(msg: String) {
        e(null, msg)
    }

    fun e(msg: String, tr: Throwable) {
        e(null, msg, tr)

    }

    @JvmOverloads fun e(tag: String?, msg: String, tr: Throwable? = null) {
        if (openLog) {
            dealMsg(LogType.e, dealTag(tag), msg + getErrorInfo(tr))
        }
    }

    fun i(msg: String) {
        i(null, msg)
    }

    fun i(msg: String, tr: Throwable) {
        i(null, msg, tr)

    }

    @JvmOverloads fun i(tag: String?, msg: String, tr: Throwable? = null) {
        if (openLog) {
            dealMsg(LogType.i, dealTag(tag), msg + getErrorInfo(tr))
        }
    }

    fun v(msg: String) {
        v(null, msg)
    }

    fun v(msg: String, tr: Throwable) {
        v(null, msg, tr)

    }

    @JvmOverloads fun v(tag: String?, msg: String, tr: Throwable? = null) {
        if (openLog) {
            dealMsg(LogType.v, dealTag(tag), msg + getErrorInfo(tr))
        }
    }

    fun w(msg: String) {
        w(null, msg)
    }

    fun w(msg: String, tr: Throwable) {
        w(null, msg, tr)

    }

    @JvmOverloads fun w(tag: String?, msg: String, tr: Throwable? = null) {
        if (openLog) {
            dealMsg(LogType.w, dealTag(tag), msg + getErrorInfo(tr))
        }
    }

    fun wtf(msg: String) {
        wtf(null, msg)
    }

    fun wtf(msg: String, tr: Throwable) {
        wtf(null, msg, tr)

    }

    @JvmOverloads fun wtf(tag: String?, msg: String, tr: Throwable? = null) {
        if (openLog) {
            dealMsg(LogType.wtf, dealTag(tag), msg + getErrorInfo(tr))
        }
    }

    /**
     * 解决报文过长，打印不全的问题！
     */
    private fun dealMsg(logType: LogType, tag: String, msg: String) {
        val length = msg.length
        val offset = 3000
        if (length > offset) {
            var n = 0
            var i = 0
            while (i < length) {
                n += offset
                if (n > length) {
                    n = length
                }
                printMsg(logType, tag, msg.substring(i, n))
                i += offset
            }
        } else {
            printMsg(logType, tag, msg)
        }
    }

    private fun dealTag(tag: String?): String {
        return if (StringUtils.isEmpty(tag)) TAG else tag!!

    }

    private fun printMsg(logType: LogType, tag: String, msg: String) {
        log2File(tag + ":" + msg)

        when (logType) {
            Logger.LogType.d -> Log.d(tag, msg)
            Logger.LogType.i -> Log.i(tag, msg)
            Logger.LogType.e -> Log.e(tag, msg)
            Logger.LogType.v -> Log.v(tag, msg)
            Logger.LogType.w -> Log.w(tag, msg)
            Logger.LogType.wtf -> Log.wtf(tag, msg)
        }

    }


    private enum class LogType {
        d, i, e, v, w, wtf
    }

    /**
     * 记录日志到文件中。
     */
    fun log2File(msg: String) {
        if (fileLog) {
            //该功能暂无
            //LogUtil.log2File(jotter_log.txt", CommonUtil.getTime() + ":" + msg);
        }
    }

    /**
     * 记录日志到文件中 可以指定文件名字。
     */
    /* private static void log2File(String fileName, String msg) {
        try {
            File appDir = Environment.getExternalStorageDirectory();
            File exLogFile = new File(appDir, fileName);
            if (!exLogFile.exists()) {
                exLogFile.createNewFile();
            }
            FileWriter fw = new FileWriter(exLogFile, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(msg);
            bw.write("\n");
            bw.flush();

            bw.close();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
*/
    private fun getErrorInfo(tr: Throwable?): String {
        if (tr == null) {
            return ""
        }
        val writer = StringWriter()
        val pw = PrintWriter(writer)
        tr.printStackTrace(pw)
        pw.close()
        val error = writer.toString()
        return "\n" + error
    }
}
