package academy.prog;

import academy.prog.jsons.JsonMessages;
import academy.prog.jsons.JsonUsers;
import academy.prog.models.Message;
import academy.prog.models.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class GetThread implements Runnable {
    private final Gson gson;
    private int n; // /get?from=n

    private String currUserLogin;

    public GetThread(String currUserLogin){
        this.currUserLogin = currUserLogin;
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    }

    @Override
    public void run() { // WebSockets
        try {
            while ( ! Thread.interrupted()) {
                URL urlMessages = new URL(Utils.getURL() + "/getmsg?from=" + n);
                HttpURLConnection httpMessages = (HttpURLConnection) urlMessages.openConnection();

                System.out.println("Server Messages url request: " + urlMessages);
                InputStream ism = httpMessages.getInputStream();
                try {
                    byte[] buf = responseBodyToArray(ism);
                    String strBuf = new String(buf, StandardCharsets.UTF_8);

                    System.out.println("Server Messages response: " + strBuf);
                    JsonMessages listM = gson.fromJson(strBuf, JsonMessages.class);

                    if (listM != null) {
                        for (Message m : listM.getList()) {
                            if (m.getTo() == null || m.getTo().equals(currUserLogin)) {
                                System.out.println("New message: " + m);
                                n++;
                            }
                        }
                    }
                } finally {
                    ism.close();
                }
                Thread.sleep(30000);


                URL urlUsers = new URL(Utils.getURL() + "/getusr?login=" + currUserLogin);
                HttpURLConnection httpUsers = (HttpURLConnection) urlUsers.openConnection();

                System.out.println("Server Users url request: " + urlUsers);
                InputStream isu = httpUsers.getInputStream();
                try {
                    byte[] buf = responseBodyToArray(isu);
                    String strBuf = new String(buf, StandardCharsets.UTF_8);

                    System.out.println("Server Users response: " + strBuf);
                    JsonUsers listU = gson.fromJson(strBuf, JsonUsers.class);

                    if (listU != null) {
                        System.out.println("Online users: ");
                        for (User u : listU.getList()) {
                            System.out.print(u.getLogin());
                        }
//                        System.out.println("Online users: " + listU.getList());
                    }
                } finally {
                    isu.close();
                }
                // C -> S -> x
                // WebSockets
                Thread.sleep(500);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private byte[] responseBodyToArray(InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[10240];
        int r;

        do {
            r = is.read(buf);
            if (r > 0) bos.write(buf, 0, r);
        } while (r != -1);

        return bos.toByteArray();
    }
}
