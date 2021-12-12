package Tools;

import java.io.IOException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

public class IpLocateAPI {

    private String ipadress;

    public String getIpadress() {
        return ipadress;
    }

    public void setIpadress(String ipadress) {
        this.ipadress = ipadress;
    }

    public IpLocateAPI(String ipadress) {
        this.ipadress = ipadress;
    }

    public IpLocateAPI() {
    }

    public String getdata(String ip) {
        if (isIP(ip)) {
            if (isPrivateIP(ip)) {
                return "Này private IP mà đưa tui làm gì :V";
            }
            if (isLoopBackIP(ip)) {
                return "Đưa tui Loopback ip thì chịu :V";
            }
        }
        String result = "Tui tìm mãi mà không thấy kết quả, bạn thử kiểm tra kỹ lại coi.";
        try {
            //Lấy dữ liệu từ trang bằng jsoup trả về document
            Connection.Response res = Jsoup.connect("http://ip-api.com/json/"
                    //Đưa ip hoặc tên miền vào
                    + ip)
                    .method(Connection.Method.GET)
                    .ignoreContentType(true)
                    .execute();
            //Đưa dữ liệu lấy được vào trong obj
            JSONObject obj = new JSONObject(res.body());
            //Nếu trả về cod = 200 tức là thành công
            if (obj.getString("status").equals("success")) {
                //Lấy kinh độ vĩ độ của ip
                float kinhdo = obj.getFloat("lon");
                float vido = obj.getFloat("lat");

                //Lấy tên quốc gia và thành phố
                String country = obj.getString("country");
                String City = obj.getString("city");

                //Ghi tên thành phố vào
                result = "Tọa độ: " + kinhdo + " / " + vido + " | Địa điểm: " + City + " / " + country;

            } else {
                return result;
            }
        } catch (IOException e) {
            return result;
        }
        return result;
    }

    public boolean isPrivateIP(String ip) {
        return ip.matches("(10)(\\.([2]([0-5][0-5]|[01234][6-9])|[1][0-9][0-9]|[1-9][0-9]|[0-9])){3}")
                || ip.matches(
                        "(172)\\.(1[6-9]|2[0-9]|3[0-1])(\\.(2[0-4][0-9]|25[0-5]|[1][0-9][0-9]|[1-9][0-9]|[0-9])){2}")
                || ip.matches("(192)\\.(168)(\\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9][0-9]|[0-9])){2}");
    }

    public boolean isIP(String ip) {
        return ip.matches("^((25[0-5]|(2[0-4]|1[0-9]|[1-9]|)[0-9])(\\.(?!$)|$)){4}$");
    }

    public boolean isLoopBackIP(String ip) {
        return ip.startsWith("127.");
    }

    public static void main(String[] args) throws IOException {
        IpLocateAPI w = new IpLocateAPI();
        System.out.println(w.getdata("youtube.com"));
    }
}
