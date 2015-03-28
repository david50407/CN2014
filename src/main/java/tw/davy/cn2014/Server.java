package tw.davy.cn2014;

import org.json.JSONArray;
import org.json.JSONObject;
import tw.davy.cn2014.extensions.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Davy on 2015/1/9.
 */
public class Server extends ServerSocket {
    private Logger logger;
    private HashMap<Long, ClientThread> clients;
    public Server(Logger logger, int port) throws IOException {
        super(port);
        this.logger = logger;
        logger.info("Listening on port: " + port);

        clients = new HashMap<Long, ClientThread>();
        try
        {
            while (true)
            {
                Socket socket = accept();
                long id = new Date().getTime();
                clients.put(id, new ClientThread(socket, id));
            }
        }
        catch (IOException e)
        {
            logger.warning("IOException: " + e.getLocalizedMessage());
        }
        finally
        {
            close();
        }
    }

    private void broadcastAdd(ClientThread newClient) {
        for (ClientThread client : getClients().values()) {
            if (client == newClient)
                continue;
            client.sendAdd(newClient.id,
                    newClient.sperm.getX(), newClient.sperm.getY());
        }
    }

    private void broadcastRemove(ClientThread removeClient) {
        for (ClientThread client : getClients().values()) {
            if (client == removeClient)
                continue;
            client.sendRemove(removeClient.id);
        }
        getClients().remove(removeClient.id);
    }

    private void broadcastAction(JSONObject object) {
        for (ClientThread client : getClients().values()) {
            client.sendAction(object);
        }
    }

    private HashMap<Long, ClientThread> getClients() {
        return clients;
    }

    class ClientThread extends Thread {
        private long id;
        private Sperm sperm;
        private Socket socket;
        private BufferedReader input;
        private PrintWriter output;

        public ClientThread(Socket socket, long id) throws IOException {
            this.socket = socket;
            this.id = id;
            input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            output = new PrintWriter(this.socket.getOutputStream(), true);
            sperm = new Sperm(350, 200);
            broadcastAdd(this);
            logger.info("#add: " + id);
            start();
        }

        public void run() {
            try {
                sendInit();
                String line;
                while ((line = input.readLine()) != null) {
                    JSONObject object = new JSONObject(line);
                    String status = object.getString("status");
                    if (status.equals("#move")) {
                        String dir = object.getString("action");
                        if (dir.equals("left")) {
                            sperm.moveLeft();
                        }
                        else if (dir.equals("right")) {
                            sperm.moveRight();
                        }
                        else if (dir.equals("up")) {
                            sperm.moveUp();
                        }
                        else if (dir.equals("down")) {
                            sperm.moveDown();
                        }
                        else if (dir.equals("left_up")) {
                            sperm.moveLeftUp();
                        }
                        else if (dir.equals("right_up")) {
                            sperm.moveRightUp();
                        }
                        else if (dir.equals("left_down")) {
                            sperm.moveLeftDown();
                        }
                        else if (dir.equals("right_down")) {
                            sperm.moveRightDown();
                        }
                        object.put("id", id);
                        logger.info("#move: " + object.toString());
                        broadcastAction(object);
                    }
                    else if (status.equals("#message")) {
                        object.put("id", id);
                        logger.info("#message: " + object.toString());
                        broadcastAction(object);
                    }
                }
                socket.close();
            }
            catch (IOException e) {
                logger.warning("IOException: " + e.getLocalizedMessage());
            }
            finally {
                logger.info("#remove: " + id);
                broadcastRemove(this);
            }
        }

        private void sendInit() {
            JSONArray datas = new JSONArray();
            for (ClientThread client : getClients().values()) {
                datas.put(new JSONObject()
                                .put("id", client.id)
                                .put("X", client.sperm.getX())
                                .put("Y", client.sperm.getY())
                );
            }
            JSONObject object = new JSONObject();
            object.put("status", "#add")
                    .put("data", datas);
            output.println(object.toString());
        }

        private void sendAdd(long id, int x, int y) {
            JSONObject object = new JSONObject();
            object.put("status", "#add")
                    .put("data", new JSONArray()
                                    .put(new JSONObject().put("id", id)
                                                    .put("X", x)
                                                    .put("Y", y)
                                    )
                );
            output.println(object.toString());
        }

        private void sendRemove(long id) {
            JSONObject object = new JSONObject();
            object.put("status", "#remove")
                  .put("id", id);
            output.println(object.toString());
        }

        private void sendAction(JSONObject object) {
            output.println(object.toString());
        }
    }
}
