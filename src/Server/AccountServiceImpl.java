package Server;

import AccountService.AccountService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;

class AccountServiceImpl extends UnicastRemoteObject implements AccountService{
    private DataManager dataManager;

    public AccountServiceImpl(DataManager dm) throws RemoteException{
        dataManager= dm;
    }

    @Override
    public synchronized Long getAmount(Integer id) throws RemoteException, SQLException {
        return dataManager.getAmount(id);
    }

    @Override
    public synchronized void addAmount(Integer id, Long value) throws RemoteException, SQLException {
        dataManager.addAmount(id, value);
    }
}
