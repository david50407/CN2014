package tw.davy.cn2014.extensions;

/**
 * Created by Davy on 2015/1/9.
 */
public class Logger {
    public Logger() {}
    public void info(String string) { System.out.println("[INFO] " + string); }
    public void warning(String string) { System.out.println("[WARNING] " + string); }
    public void error(String string) { System.out.println("[ERROR] " + string); }
}
