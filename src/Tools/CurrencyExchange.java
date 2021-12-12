package Tools;

import java.io.IOException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

public class CurrencyExchange {

    public CurrencyExchange() {

    }
    //["SGD","MYR","EUR","USD","AUD","JPY","CNH","HKD","CAD","INR","DKK","GBP","RUB","NZD","MXN","IDR","TWD","THB","VND"]

    public String getdata(String amount, String from, String to) {
        String result = "Có đơn vị đó tồn tại nữa hả bạn ???";
         try {
             if (Integer.parseInt(amount) <= 0 ||Long.parseLong(amount)>1000000000) {
                 result = "Tui chỉ đổi được từ 0 tới 1 tỷ thôi, nhìn tui vậy chứ ngu lắm";
                 return result;
             }
         }catch (Exception e){
             result = "Nhập số dùm cái đi, nhập chữ đổi bằng niềm à :V";
             return result;
         }
        try {
            Connection.Response res = Jsoup.connect("https://exchangerate-api.p.rapidapi.com/rapid/latest/" + from)
                    .method(Connection.Method.GET)
                    .header("x-rapidapi-host", "exchangerate-api.p.rapidapi.com")
                    .header("x-rapidapi-key", "fcb719c57amshd29d860f560916dp1b2969jsn0bd0e81a5530")
                    .ignoreContentType(true)
                    .execute();
            JSONObject obj = new JSONObject(res.body());
            JSONObject obj1 = obj.getJSONObject("rates");
            float obj2;
            obj2 = obj1.getFloat(to);

            result = amount +" "+from+" = "+String.valueOf((long)(Double.parseDouble(amount)*obj2))+" "+to;

        } catch (IOException e) {
            result = "Có đơn vị đó tồn tại nữa hả bạn ???";
        }
        return result;
    }

    public String getlist() {
        String result = "";
        try {
            Connection.Response res = Jsoup.connect("https://currency-exchange.p.rapidapi.com/listquotes")
                    .method(Connection.Method.GET)
                    .header("x-rapidapi-host", "currency-exchange.p.rapidapi.com")
                    .header("x-rapidapi-key", "fcb719c57amshd29d860f560916dp1b2969jsn0bd0e81a5530")
                    .ignoreContentType(true)
                    .execute();
            result = res.body();
        } catch (IOException e) {

        }
        return result;
    }

    public static void main(String[] args) throws IOException {
        CurrencyExchange w = new CurrencyExchange();
        VNCharacterUtils util = new VNCharacterUtils();
        System.out.println(w.getdata("100000000", "VND", "USD"));
    }
}
