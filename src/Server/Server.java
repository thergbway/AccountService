package Server;

import AccountService.AccountService;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;
import java.util.logging.Level;

import static Server.Parameters.ACCOUNT_SERVICE_OBJECT_NAME;
import static Server.Parameters.PORT_FOR_RMI;

public class Server {
    private DataManager dataManager;
    AccountServiceImpl accountService;
    private Registry registry;
    private Statistics statistics;
    private ServerMainFrame serverMainFrame;

    public static void main(String[] args){
        try{
            new Server();
        } catch (SQLException e) {
            while (e != null){
                ServerLogger.getInstance().log(Level.SEVERE, "Server stopped because of an exception", e);
                e= e.getNextException();
            }
            System.exit(1);
        } catch (IOException e) {
            ServerLogger.getInstance().log(Level.SEVERE, "Server stopped because of an exception", e);
            System.exit(1);
        }
    }
    public Server() throws IOException, SQLException {
        statistics= new Statistics();
        dataManager= new DataManager(statistics);
        accountService= new AccountServiceImpl(dataManager);
        registry= LocateRegistry.createRegistry(PORT_FOR_RMI);
        registryAccountService(ACCOUNT_SERVICE_OBJECT_NAME, accountService);

        serverMainFrame= new ServerMainFrame(statistics, this);
        ServerLogger.getInstance().addHandler(serverMainFrame.getJTextAreaHandler());
        serverMainFrame.setVisible(true);

        ServerLogger.getInstance().info("Server is running");
        statistics.start();
    }
    private void registryAccountService(String name, AccountService accountService) throws RemoteException {
        registry.rebind(name,accountService);
    }
    private void unregistry(String name){
        try {
            registry.unbind(name);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }
    void close(){
        statistics.close();
        dataManager.close();
        ServerLogger.getInstance().close();

        unregistry(ACCOUNT_SERVICE_OBJECT_NAME);

        System.exit(0);
    }
}
