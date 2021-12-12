package chatbotsimsimi;

import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GUIChat extends javax.swing.JFrame {

    private Client client;
    public String textchat;

    public GUIChat(Client client) {
        initComponents();
        this.client = client;
        KhungChatThat.setText(client.username + " đã tham gia chat");
        TextTinNhan.setText("");
        listenForMessage();
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
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
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        KhungChat = new javax.swing.JScrollPane();
        KhungChatThat = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        TextTinNhan = new javax.swing.JTextField();
        NutGui = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        KhungChatThat.setEditable(false);
        KhungChatThat.setColumns(20);
        KhungChatThat.setFont(new java.awt.Font("Times New Roman", 0, 24)); // NOI18N
        KhungChatThat.setRows(5);
        KhungChat.setViewportView(KhungChatThat);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        TextTinNhan.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        TextTinNhan.setToolTipText("");
        TextTinNhan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TextTinNhanActionPerformed(evt);
            }
        });

        NutGui.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        NutGui.setText("Gửi");
        NutGui.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NutGuiActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(TextTinNhan, javax.swing.GroupLayout.DEFAULT_SIZE, 726, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(NutGui, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(NutGui, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(TextTinNhan, javax.swing.GroupLayout.DEFAULT_SIZE, 39, Short.MAX_VALUE))
                .addContainerGap())
        );

        jLabel1.setFont(new java.awt.Font("Times New Roman", 0, 48)); // NOI18N
        jLabel1.setText("Simsimi");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(KhungChat)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(320, 320, 320)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(KhungChat, javax.swing.GroupLayout.PREFERRED_SIZE, 405, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void TextTinNhanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TextTinNhanActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TextTinNhanActionPerformed
    
    //Nút gửi tin nhắn
    private void NutGuiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NutGuiActionPerformed
        try {
            if (client.socket.isConnected()) {
                String messageSend = TextTinNhan.getText().trim();
                //Mã hóa tin nhắn
                String encode = client.aes.encrypt(messageSend.replace(" ", ""), client.aes.encode(client.aes.getKey().getEncoded()));
                System.out.println("Tin nhắn gửi đi: " + encode);
                client.bufferedWriter.write(encode);
                client.bufferedWriter.newLine();
                client.bufferedWriter.flush();
                textchat = KhungChatThat.getText() + "\n" + client.username + ": " + messageSend;
                KhungChatThat.setText(KhungChatThat.getText() + "\n" + client.username + ": " + messageSend
                        + "\n" + "Đang trả lời...");
                TextTinNhan.setEditable(false);
                NutGui.setEnabled(false);
                TextTinNhan.setText("");
            }
        } catch (IOException e) {
            closeEverything(client.socket, client.bufferedReader, client.bufferedWriter);
        } catch (Exception ex) {
            Logger.getLogger(GUIChat.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_NutGuiActionPerformed
    public void listenForMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromGroupChat = "";

                while (client.socket.isConnected()) {
                    try {
                        msgFromGroupChat = client.bufferedReader.readLine();
                        String chat = textchat;
                        KhungChatThat.setText(KhungChatThat.getText() + "\n" + "Đang trả lời......");
                        System.out.println("Tin nhắn nhân được từ simsimi: " + msgFromGroupChat);
                        String text = "Hello";
                        try {
                            //giải mã tin nhận
                            text = client.aes.decrypt(
                                    msgFromGroupChat,
                                    client.aes.encode(client.aes.getKey().getEncoded())
                            );
                        } catch (Exception e) {

                        }
                        KhungChatThat.setText(textchat + "\n *Simsimi: " + text);
                        TextTinNhan.setEditable(true);
                        NutGui.setEnabled(true);
                    } catch (IOException e) {
                        closeEverything(client.socket, client.bufferedReader, client.bufferedWriter);
                    } catch (Exception ex) {
                        Logger.getLogger(GUIChat.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }).start();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane KhungChat;
    private javax.swing.JTextArea KhungChatThat;
    private javax.swing.JButton NutGui;
    private javax.swing.JTextField TextTinNhan;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
