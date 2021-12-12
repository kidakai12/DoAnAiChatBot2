package Server;

import Sercurity.AESUtil;
import Sercurity.RSAUtil;
import Tools.CurrencyExchange;
import Tools.IpLocateAPI;
import Tools.LanguageTranslator;
import Tools.PortScan;
import Tools.SimsimiAPI;
import Tools.VNCharacterUtils;
import Tools.WeatherAPI;
import Tools.WhoIs;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;
    private RSAUtil rsa;
    private SecretKey key;
    private AESUtil aes;

    public ClientHandler(Socket socket) throws NoSuchAlgorithmException {
        try {
            rsa = new RSAUtil();
            aes = new AESUtil();
            aes.init();
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername = bufferedReader.readLine();
            clientHandlers.add(this);
            sendkey(rsa.getPublickey());
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        } catch (Exception ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        String messageFromClient;

        while (socket.isConnected()) {
            try {
                //Đọc tin nhắn từ client
                messageFromClient = bufferedReader.readLine();
                String msg = messageFromClient;
                try {
                     //Giải mã rsa lấy secret key
                    if (rsa.Decrypt(msg).contains("#secretkey#")) {
                        messageFromClient = rsa.Decrypt(msg);
                    }
                } catch (InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
                }
                System.out.println("Tin nhắn từ " + clientUsername + ": " + messageFromClient);
                if (messageFromClient.contains("#secretkey#")) {
                    String params = messageFromClient.split("#secretkey#")[1];
                    System.out.println("Secret Key: " + params);
                    byte[] decodedKey = Base64.getDecoder().decode(params);
                    this.key = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
                    aes.setKey(key);
                } else {
                    String text = "Hello";
                    try {
                        //Giải mã tin nhắn
                        text = aes.decrypt(messageFromClient, aes.encode(aes.getKey().getEncoded()));
                    } catch (Exception e) {
                    }
                    //Trả kết qua simsimi
                    String result = getsimi(text);
                    //Mã hóa tin nhắn simsimi để gửi
                    System.out.println("Simsimi trả lời: " + aes.encrypt(result, aes.encode(aes.getKey().getEncoded())));
                    broadcastMessage(aes.encrypt(result, aes.encode(aes.getKey().getEncoded())));
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            } catch (Exception ex) {
                Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void sendkey(PublicKey publics) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (clientHandler.clientUsername.equals(clientUsername)) {
                    String publicK = Base64.getEncoder().encodeToString(publics.getEncoded());
                    System.out.println("Public key: " + publicK);
                    clientHandler.bufferedWriter.write("#publickey#" + publicK);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            } catch (Exception ex) {
                Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    //Gửi tin nhắn
    public void broadcastMessage(String messageFromClient) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (clientHandler.clientUsername.equals(clientUsername)) {
                    clientHandler.bufferedWriter.write(messageFromClient);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            } catch (Exception ex) {
                Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void removeClientHandler() {
        clientHandlers.remove(this);
        broadcastMessage("SERVER: " + clientUsername + " has left the chat");
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler();
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
        }
    }

    public String getsimi(String text) {
        String result = "";
        if (text.equals("hello")) {
            result = "Hello,tui là simsimi là 1 AI chính cống nhé"
                    + "\nNgoài khả năng tám chuyện ra tui còn có siêu năng lực làm được nhiều thứ\r\n"
                    + "Để liệt kê ra cho biết nà: \r\n"
                    + "\r\n"
                    + "Thứ nhất: Khả năng nhìn trời nhìn đất đoán mây đoán gió đoán mưa\r\n"
                    + "*Lệnh nhập: #weather# Tên 1 địa điểm\n"
                    + "\r\n"
                    + "Thứ hai: Xác định vị trí của bất kì thằng ất ơ và bằng cách tra địa chỉ IP nó\r\n"
                    + "*Lệnh nhập: #ip# IP hoặc tên miền\n"
                    + "\r\n"
                    + "Thứ ba: Khả năng nhìn ra kinh mạch của 1 trang web :V\r\n"
                    + "*Lệnh nhập: #portscan# tên miền hoặc IP bất kì# port bắt đầu # port kết thúc\r\n"
                    + "\r\n"
                    + "Thứ tư: Khả năng đổi đơn vị tiền tệ siu tốc\r\n"
                    + "*Lệnh nhập: #exchange#số tiền#Đơn vị của nó#Đơn vị muốn đổi"
                    + "\r\n"
                    + "Thứ năm: Khả năng nhìn ra chủ sở hữu của tên miền\r\n"
                    + "*Lệnh nhập: #whois# tên miền hoặc ip\r\n";
        } else if (text.matches("^#weather#[^/ ].*$")) {
            String params = text.split("(#weather#)")[1];
            String translate  = new LanguageTranslator().getdata(params);
            result = new WeatherAPI().getdata(translate);
        } else if (text.matches("^\\#portscan\\#([^/ ].*);([^/ ].*);([^/ ].*)$")) {
            String[] params = text.split("(#portscan#)");
            String[] params2 = params[1].split(";");
            result = new PortScan().scan(params2[0], params2[1], params2[2]);

        } else if (text.matches("^#ip#[^/ ].*$")) {
            String params = text.split("(#ip#)")[1];
            result = new IpLocateAPI().getdata(params);

        } else if (text.matches("^#whois#[^/ ].*$")) {
            String params = text.split("(#whois#)")[1];
            result = new WhoIs().getdata(params);

        } else if (text.matches("^#exchange#([^/ ].*);([^/ ].*);([^/ ].*)$")) {
            String[] params = text.split("(#exchange#)");
            String[] params2 = params[1].split(";");
            result = new CurrencyExchange().getdata(params2[0], params2[1], params2[2]);

        } else {
            result = new SimsimiAPI().getdata(text);
        }
        if (result == null) {
            result = "Hết 100 tin nhắn rồi";
        }
        return result;
    }
}
