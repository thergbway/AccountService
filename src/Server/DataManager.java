package Server;

import java.io.IOException;
import java.sql.SQLException;

class DataManager {
    private CachedSQLConnectivity cachedSQLConnectivity;

    public DataManager(Statistics st) throws IOException, SQLException {
        cachedSQLConnectivity= new CachedSQLConnectivity(st);
    }
    public Long getAmount(Integer id) throws SQLException{
        return cachedSQLConnectivity.getAmount(id);
    }
    public void addAmount(Integer id, Long value) throws SQLException {
        cachedSQLConnectivity.addAmount(id, value);
    }
    public void close(){
        cachedSQLConnectivity.close();
    }
}
