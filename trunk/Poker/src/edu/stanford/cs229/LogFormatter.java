package edu.stanford.cs229;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * This is used exclusively for logging items to disk. This is used more for the
 * web application.
 * 
 * @author ago
 * 
 */
public class LogFormatter extends Formatter {
    public String format(LogRecord rec) {
        StringBuffer buf = new StringBuffer(1000);
        buf.append(formatMessage(rec));
        buf.append('\n');
        return buf.toString();
    }
}
