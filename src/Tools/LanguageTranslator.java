package Tools;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public class LanguageTranslator {

    public LanguageTranslator() {

    }

    public String getdata(String tp) {
        String result = "";
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://google-translate1.p.rapidapi.com/language/translate/v2"))
                    .header("content-type", "application/x-www-form-urlencoded")
                    .header("accept-encoding", "application/gzip").header("x-rapidapi-key",
                    "fcb719c57amshd29d860f560916dp1b2969jsn0bd0e81a5530")
                    .header("x-rapidapi-host", "google-translate1.p.rapidapi.com")
                    .method("POST", HttpRequest.BodyPublishers.ofString("q=" + tp + "&target=en&source=vi")).build();
            HttpResponse<String> res = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            //Đưa dữ liệu lấy được vào trong obj
            JSONObject obj = new JSONObject(res.body());
            //Nếu trả về cod = 200 tức là thành công

            JSONObject obj1 = obj.getJSONObject("data");
            JSONArray obj3 = obj1.getJSONArray("translations");
            JSONObject obj2 = obj3.getJSONObject(0);
            result = obj2.getString("translatedText");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException ex) {
            Logger.getLogger(LanguageTranslator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public static void main(String[] args) throws IOException {
        LanguageTranslator w = new LanguageTranslator();
        System.out.println(w.getdata("Hồ chí minh"));
    }
}
