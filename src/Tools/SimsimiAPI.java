package Tools;

import java.io.IOException;
import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

public class SimsimiAPI {

    public SimsimiAPI() {

    }

    public String getdata(String ask) {
        String result = "";
        try {
            Connection.Response res = Jsoup.connect("https://tuanxuong.com/api/simsimi/index.php?text=" + ask)
                    .method(Connection.Method.GET)
                    .ignoreContentType(true)
                    .execute();
            JSONObject obj = new JSONObject(res.body());
            if (obj.getInt("result") == 100) {
                result = StringEscapeUtils.unescapeJava(obj.getString("response"));
            } else {
                result = "Không biết trả lời sao luôn";
            }
        } catch (IOException e) {

        }
        return result;
    }

    public static void main(String[] args) throws IOException {
        SimsimiAPI w = new SimsimiAPI();
        VNCharacterUtils util = new VNCharacterUtils();
        String ask = "Hello con đĩ";
        System.out.println(w.getdata(ask));
    }
}
