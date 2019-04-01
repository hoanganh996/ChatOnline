package rmiinterface;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface RMIInterface extends Remote {

    public int Register(User user) throws RemoteException;  //0:success    1:username exist     2:fail
    public User Login(User user) throws RemoteException;
    public int CreateGroup(User user, Group group) throws RemoteException;   //0:success    1:group exist     2:fail
    public int JoinGroup(User user, Group group) throws RemoteException;   //0:success    1:group not exist     2:fail
    public ArrayList<Group> GetListGroup(User user) throws RemoteException;
    public ArrayList<Message> GetListMessage(Group group) throws RemoteException;
    public void AddMessage(Message message) throws RemoteException;
}