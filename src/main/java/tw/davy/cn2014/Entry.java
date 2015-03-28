package tw.davy.cn2014;

import org.json.JSONException;
import org.json.JSONObject;
import tw.davy.cn2014.extensions.Logger;
import tw.davy.cn2014.extensions.TextAreaOutputStream;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;

/**
 * Created by Davy on 2015/1/9.
 */
public class Entry {
    public static void main(String args[]) {
        JFrame frame = new JFrame("Bootstraping...");
        Dimension dimension = new Dimension();
        dimension.setSize(800, 500);
        frame.setPreferredSize(dimension);
        frame.setSize(dimension);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        TextAreaOutputStream textAreaOutputStream = new TextAreaOutputStream(textArea, 60);
        PrintStream printStream = new PrintStream(textAreaOutputStream);
        System.setOut(printStream);
        System.setErr(printStream);

        frame.add(new JScrollPane(textArea));

        frame.pack();
        frame.setVisible(true);

        Logger logger = new Logger();

        if (!new File("server.config").exists()) {
            logger.warning("Config not found: server.config.");
            logger.warning("Creating default config...");

            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream("server.config");
            OutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream("server.config");
                int read = 0;
                byte[] bytes = new byte[1024];
                while ((read = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }
            } catch (IOException e) {
                logger.error("Creating config failed.");
                System.exit(-1);
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        JSONObject configJSON = null;
        logger.info("Loading config...");
        try {
            FileReader fr = new FileReader("server.config");
            BufferedReader br = new BufferedReader(fr);
            StringBuffer stringBuffer = new StringBuffer();
            while (br.ready()) {
                stringBuffer.append(br.readLine());
            }
            fr.close();
            configJSON = new JSONObject(stringBuffer.toString());
        }
        catch (JSONException e) {
            logger.error("Invalid config: " + e.getLocalizedMessage());
            System.exit(-1);
        }
        catch (IOException e) {
            logger.error("Load config failed: " + e.getLocalizedMessage());
            System.exit(-1);
        }

        if (configJSON.has("title")) {
            frame.setTitle(configJSON.getString("title"));
        }

        int port = 5566;
        if (configJSON.has("port")) {
            port = configJSON.getInt("port");
        }

        try {
            new Server(logger, port);
        }
        catch (IOException e) {
            logger.error("Server start failed: " + e.getLocalizedMessage());
            System.exit(-1);
        }
    }
}
