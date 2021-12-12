package Tools;

import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

public class WeatherAPI {

    public WeatherAPI() {

    }

    public String getdata(String tp) {
        String result = "Chịu, chả biết luôn !!";
        try {
            //Lấy dữ liệu từ trang bằng jsoup trả về document
            Connection.Response res = Jsoup.connect("https://community-open-weather-map.p.rapidapi.com/forecast/daily?"
                    //Đưa giá trị địa điểm vào
                    + "q=" + tp
                    + //Ngày dự báo là 7 ngày tới là 1 tuần
                    "&cnt=7&"
                    //Đơn vị tính là metric tại vì có độ C
                    + "units=metric&"
                    //Ngôn ngữ tiếng việt
                    + "lang=vi")
                    .method(Connection.Method.GET)
                    .header("x-rapidapi-host", "community-open-weather-map.p.rapidapi.com")
                    .header("x-rapidapi-key", "fcb719c57amshd29d860f560916dp1b2969jsn0bd0e81a5530")
                    .ignoreContentType(true)
                    .execute();
            //Đưa dữ liệu lấy được vào trong obj
            JSONObject obj = new JSONObject(res.body());
            //Nếu trả về cod = 200 tức là thành công
            if (obj.getInt("cod") == 200) {
                //Lấy danh sách dự báo các ngày trong tuần
                JSONArray arr = obj.getJSONArray("list");
                //Lấy Tên thành phố
                JSONObject obj2 = obj.getJSONObject("city");
                //Lấy Số ngày
                int cnt = obj.getInt("cnt");
                //Ghi tên thành phố vào
                result = "-Thời tiết của " + obj2.getString("name") + " là: \r\n";
                for (int i = 0; i < cnt; i++) {
                    //Lấy dự báo ngày thứ i
                    JSONObject id = arr.getJSONObject(i);

                    //Lấy thời gian cụ thể là UNIX date
                    long timeStamp = id.getLong("dt");
                    //Đổi sang ngày tháng năm
                    java.util.Date time = new java.util.Date((long) timeStamp * 1000);

                    //Lấy nhiệt độ
                    JSONObject obj3 = id.getJSONObject("temp");

                    //Lấy độ ẩm
                    int humid = id.getInt("humidity");

                    //Lấy danh sách thời tiết chỉ có 1
                    JSONArray arr2 = id.getJSONArray("weather");
                    //Lấy thông tin dự báo ra
                    JSONObject id2 = arr2.getJSONObject(0);
                    //Lấy mô tả thời tiết ra
                    String mota = id2.getString("description");

                    //Ghi lại kết quả
                    result = result + "+Thời gian: " + time.toString() + "\r\n"
                            + "   *Nhiệt độ max/min: " + obj3.getInt("day") + "/" + obj3.getInt("night") + "'C \r\n"
                            + "   *Độ ẩm: " + humid + "% \r\n"
                            + "   *Mô tả: " + mota + " \r\n";
                }
            } else {
                result = "Chịu, chả biết luôn !!";
            }
        } catch (IOException e) {
            result = "Chịu, chả biết luôn !!";
        }
        return result;
    }

    public static void main(String[] args) throws IOException {
        WeatherAPI w = new WeatherAPI();
        VNCharacterUtils util = new VNCharacterUtils();
        System.out.println(w.getdata(util.removeAccent("HồChíMinh")));
    }
}
