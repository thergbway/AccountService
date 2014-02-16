package Server;

import java.io.IOException;
import java.sql.SQLException;

class CachedSQLConnectivity {
    private SQLConnectivity sqlConnectivity;
    private Cache cache;
    private Statistics statistics;

    private int getCountCalls;

    public Long getAmount(Integer id) throws SQLException{
        statistics.registerNewGetCall();
        ++getCountCalls;

        //working with the cache statistics
        statistics.updateCacheHitRatio(cache.hitRatio());
        if(getCountCalls % 100 == 0){
            cache.resetHitRatio();
        }

        Long result= cache.get(id);
        if(result == null){//if no in cache therefore add value and get it again
            result= sqlConnectivity.getAmount(id);
            cache.put(id, result);
        }
        return result;
    }
    public void addAmount(Integer id, Long value) throws SQLException {
        statistics.registerNewAddCall();
        cache.invalidate(id);
        sqlConnectivity.addAmount(id, value);
    }
    public CachedSQLConnectivity(Statistics st) throws IOException, SQLException {
        Parameters params= Parameters.getInstance();

        sqlConnectivity= new SQLConnectivity();
        cache= new HeapCache(params.CACHE_CAPACITY);
        statistics= st;
    }
    public void close(){
        sqlConnectivity.close();
    }
}
