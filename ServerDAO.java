package controller;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import rmiinterface.*;

public class ServerDAO {

    private Connection conn = null;
    private String dbClass = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private String url = "jdbc:sqlserver://localhost:1433;databaseName=Chat";
    private String user = "sa";
    private String password = "12345678";

    public ServerDAO() {
        try {
            Class.forName(dbClass);
            conn = DriverManager.getConnection(url, user, password);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public int Register(User user) {
        if(checkUserByUsername(user.getUsername())) {
            return 1;
        }
        try {
            String sql = "INSERT INTO tblUser(username, password, display_name) VALUES(?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getDisplayName());
            ps.executeUpdate();
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 2;
        }
    }
    
    public User Login(User user) {
        User _user = null;
        try {
            String sql = "SELECT * FROM tblUser WHERE username = ? AND password = ? ";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                _user = new User(rs.getInt("id"), rs.getString("username"), rs.getString("password"), rs.getString("display_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return _user;
    }
    
    private boolean checkUserByUsername(String username) {
        try {
            String sql = "SELECT * FROM tblUser WHERE username = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private int getIdGroupByName(String groupname) {
        try {
            String sql = "SELECT * FROM tblGroup WHERE name = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, groupname);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    public int CreateGroup(User user, Group group) {
        if(getIdGroupByName(group.getName()) != 0) {
            return 1;
        }
        try {
            String sql = "INSERT INTO tblGroup(name, password) VALUES(?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, group.getName());
            ps.setString(2, group.getPassword());
            ps.executeUpdate();
            
            return JoinGroup(user, group);
        } catch (Exception e) {
            e.printStackTrace();
            return 2;
        }
    }
    
    public int JoinGroup(User user, Group group) {
        if(getIdGroupByName(group.getName()) == 0) {
            return 1;
        }
        try {
            String sql = "INSERT INTO tblUserGroup(idUser, idGroup) VALUES(?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, user.getId());
            ps.setInt(2, getIdGroupByName(group.getName()));
            ps.executeUpdate();
            
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 2;
        }
    }
    
    public ArrayList<Group> GetListGroup(User user){
        ArrayList<Group> listGroup = new ArrayList<Group>();
        try {
            String sql = "SELECT * FROM tblUserGroup, tblGroup WHERE tblUserGroup.idUser = ? AND tblUserGroup.idGroup = tblGroup.id";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, user.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Group g = new Group(rs.getInt("idGroup"), rs.getString("name"), rs.getString("password"));
                listGroup.add(g);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listGroup;
    }
    
    public ArrayList<Message> GetListMessage(Group group){
        ArrayList<Message> listMessage = new ArrayList<Message>();
        try {
            String sql = "SELECT TOP 1000 * FROM tblMessage, tblUserGroup, tblUser WHERE tblUserGroup.idGroup = ? "
                    + "AND tblUserGroup.id = tblMessage.idUserGroup AND tblUserGroup.idUser = tblUser.id ORDER BY tblMessage.id DESC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, group.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User u = new User(rs.getInt("idUser"), rs.getString("username"), "", rs.getString("display_name"));
                Message m = new Message(rs.getInt(1), u, group, rs.getString("message"));
                listMessage.add(m);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Collections.reverse(listMessage);
        return listMessage;
    }
    
    private int findIdUserGroup(User user, Group group) {
        try {
            String sql = "SELECT * FROM tblUserGroup WHERE idUser = ? AND idGroup = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, user.getId());
            ps.setInt(2, group.getId());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    public void AddMessage(Message message) {
        try {
            String sql = "INSERT INTO tblMessage(idUserGroup, message) VALUES(?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, findIdUserGroup(message.getUser(), message.getGroup()));
            ps.setString(2, message.getMessage());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
