package controller;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.net.InetAddress;
import java.util.ArrayList;
import rmiinterface.*;

public class RMIServer extends UnicastRemoteObject implements RMIInterface {

    int thisPort = 3232; // this port(registry’s port)    
    String thisAddress;
    Registry registry; // dang ki RMI
    private ServerDAO dao;

    public RMIServer() throws RemoteException {        // dang ki RMI server    
        try {
            registry = LocateRegistry.createRegistry(thisPort);
            registry.rebind("rmiServer", this);
            dao = new ServerDAO();
        } catch (RemoteException e) {
            throw e;
        }
    }

    public static void main(String[] args) throws RemoteException {
        RMIServer rmiServer = new RMIServer();
    }

    @Override
    public int Register(User user) throws RemoteException {
        return dao.Register(user);
    }

    @Override
    public User Login(User user) throws RemoteException {
        return dao.Login(user);
    }

    @Override
    public int CreateGroup(User user, Group group) throws RemoteException {
        return dao.CreateGroup(user, group);
    }

    @Override
    public int JoinGroup(User user, Group group) throws RemoteException {
        return dao.JoinGroup(user, group);
    }

    @Override
    public ArrayList<Group> GetListGroup(User user) throws RemoteException {
        return dao.GetListGroup(user);
    }

    @Override
    public ArrayList<Message> GetListMessage(Group group) throws RemoteException {
        return dao.GetListMessage(group);
    }

    @Override
    public void AddMessage(Message message) throws RemoteException {
        dao.AddMessage(message);
    }

}
