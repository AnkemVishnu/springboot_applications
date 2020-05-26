package com.bizmobia.vgwallet.vgwapp.websocket;

import com.bizmobia.vgwallet.vgwapp.utilities.DateFormate;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

/**
 *
 * @author Vishnu
 */
@ServerEndpoint(value = "/crossweblogin/{clientID}", decoders = MessageDecoder.class, encoders = MessageEncoder.class)
public class ChatEndpoint {

    private Session session;
    private static Set<ChatEndpoint> chatEndpoints = new CopyOnWriteArraySet<>();
    private static HashMap<String, Session> users = new HashMap<>();

    public static Integer dbConnect(String uniqueId) {
        int result;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            Connection con = DriverManager.getConnection("jdbc:mysql://159.65.146.244:3306/vgwweb?useSSL=false", "root", "Sql@bizzmobia@123");

            PreparedStatement statment = con.prepareStatement("INSERT INTO CrossLogin (verified, uniqueId, created_on) values (?, ?, ?)");
            statment.setBoolean(1, false);
            statment.setString(2, uniqueId);
            statment.setDate(3, new Date(DateFormate.createdDate()));
            result = statment.executeUpdate();
            con.close();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | SQLException e) {
            result = 0;
        }
        return result;
    }

    @OnOpen
    public void onOpen(@PathParam("clientID") String clientID, Session session) throws IOException, EncodeException {
        this.session = session;
        chatEndpoints.add(this);
        users.put(clientID, session);

        String uniqueId = UUID.randomUUID().toString();

        if (dbConnect(uniqueId) > 0) {
            System.out.println("Cross Login Recorded");

            Messenger message = new Messenger();
            message.setStatusCode(0);
            message.setMessage("Connected");
            message.setSession(clientID);
            message.setUniqueId(uniqueId);

            broadcast(message);
        } else {
            onClose(session);
        }
    }

    @OnMessage
    public void onMessage(Session session, Messenger message) throws IOException, EncodeException {
        broadcast(message);
    }

    @OnClose
    public void onClose(Session session) throws IOException, EncodeException {
        chatEndpoints.remove(this);
        Messenger message = new Messenger();
        message.setStatusCode(1);
        message.setMessage("Disconnected");
        broadcast(message);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println(session.getId() + " - Error");
    }

    private static void broadcast(Messenger message) throws IOException, EncodeException {
        chatEndpoints.forEach(endpoint -> {
            synchronized (endpoint) {
                try {
                    endpoint.session.getBasicRemote().
                            sendObject(message);
                } catch (IOException | EncodeException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void login(Messenger message) throws IOException, EncodeException {
        chatEndpoints.forEach(endpoint -> {
            synchronized (endpoint) {
                try {
                    endpoint.session.getBasicRemote().
                            sendObject(message);
                } catch (IOException | EncodeException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
