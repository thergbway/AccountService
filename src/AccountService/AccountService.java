package AccountService;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;

public interface AccountService extends Remote {
    /**
     * Retrieves current balance or zero if addAmount() method was not called before for specified id
     *
     * @param id balance identifier
     */
    public Long getAmount(Integer id) throws RemoteException, SQLException;
    /**
     * Increases balance or set if addAmount() method was called first time
     *
     * @param id balance identifier
     * @param value positive or negative value, which must be added to current balance
     */
    public void addAmount(Integer id, Long value) throws RemoteException, SQLException;
}
