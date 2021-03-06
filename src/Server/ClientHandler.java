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
                //?????c tin nh???n t??? client
                messageFromClient = bufferedReader.readLine();
                String msg = messageFromClient;
                try {
                     //Gi???i m?? rsa l???y secret key
                    if (rsa.Decrypt(msg).contains("#secretkey#")) {
                        messageFromClient = rsa.Decrypt(msg);
                    }
                } catch (InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
                }
                System.out.println("Tin nh???n t??? " + clientUsername + ": " + messageFromClient);
                if (messageFromClient.contains("#secretkey#")) {
                    String params = messageFromClient.split("#secretkey#")[1];
                    System.out.println("Secret Key: " + params);
                    byte[] decodedKey = Base64.getDecoder().decode(params);
                    this.key = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
                    aes.setKey(key);
                } else {
                    String text = "Hello";
                    try {
                        //Gi???i m?? tin nh???n
                        text = aes.decrypt(messageFromClient, aes.encode(aes.getKey().getEncoded()));
                    } catch (Exception e) {
                    }
                    //Tr??? k???t qua simsimi
                    String result = getsimi(text);
                    //M?? h??a tin nh???n simsimi ????? g???i
                    System.out.println("Simsimi tr??? l???i: " + aes.encrypt(result, aes.encode(aes.getKey().getEncoded())));
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
    
    //G???i tin nh???n
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
            result = "Hello,tui l?? simsimi l?? 1 AI ch??nh c???ng nh??"
                    + "\nNgo??i kh??? n??ng t??m chuy???n ra tui c??n c?? si??u n??ng l???c l??m ???????c nhi???u th???\r\n"
                    + "????? li???t k?? ra cho bi???t n??: \r\n"
                    + "\r\n"
                    + "Th??? nh???t: Kh??? n??ng nh??n tr???i nh??n ?????t ??o??n m??y ??o??n gi?? ??o??n m??a\r\n"
                    + "*L???nh nh???p: #weather# T??n 1 ?????a ??i???m\n"
                    + "\r\n"
                    + "Th??? hai: X??c ?????nh v??? tr?? c???a b???t k?? th???ng ???t ?? v?? b???ng c??ch tra ?????a ch??? IP n??\r\n"
                    + "*L???nh nh???p: #ip# IP ho???c t??n mi???n\n"
                    + "\r\n"
                    + "Th??? ba: Kh??? n??ng nh??n ra kinh m???ch c???a 1 trang web :V\r\n"
                    + "*L???nh nh???p: #portscan# t??n mi???n ho???c IP b???t k??# port b???t ?????u # port k???t th??c\r\n"
                    + "\r\n"
                    + "Th??? t??: Kh??? n??ng ?????i ????n v??? ti???n t??? siu t???c\r\n"
                    + "*L???nh nh???p: #exchange#s??? ti???n#????n v??? c???a n??#????n v??? mu???n ?????i"
                    + "\r\n"
                    + "Th??? n??m: Kh??? n??ng nh??n ra ch??? s??? h???u c???a t??n mi???n\r\n"
                    + "*L???nh nh???p: #whois# t??n mi???n ho???c ip\r\n";
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
            result = "H???t 100 tin nh???n r???i";
        }
        return result;
    }
}
