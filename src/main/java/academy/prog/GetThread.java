package academy.prog;

import academy.prog.jsons.JsonResponse;
import academy.prog.models.Message;
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
                URL url = new URL(Utils.getURL() + "/get?from=" + n + "&login=" + currUserLogin);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();

                System.out.println(url);
                InputStream is = http.getInputStream();
                try {
                    byte[] buf = responseBodyToArray(is);
                    String strBuf = new String(buf, StandardCharsets.UTF_8);

                    System.out.println("Server response: " + strBuf);
                    JsonResponse response = gson.fromJson(strBuf, JsonResponse.class);

                    if (response != null) {
                        if(response.getMessages() != null){
                            for (Message m : response.getMessages()) {
                                if (m.getTo() == null || m.getTo().equals(currUserLogin)) {
                                    System.out.println(m);
                                    n++;
                                }
                            }
                        }
                        if(response.getUsers() != null){
                            System.out.println("Online users: " + response.getUsers());
                        }
                    }
                } finally {
                    is.close();
                }
                // C -> S -> x
                // WebSockets
                Thread.sleep(6000);
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
