package Server;

import java.util.LinkedHashMap;
import java.util.Map;

interface Cache {
    void put(Integer id, Long value);
    Long get(Integer id);
    Long invalidate(Integer id);
    void clear();
    double hitRatio();
    void resetHitRatio();
}

class HeapCache implements Cache{
    private long hit;
    private long miss;
    private Map<Integer, Long> map;

    public HeapCache(int capacity){
        map= new LimitedHashMap(capacity);
    }

    @Override
    public void put(Integer key, Long value) {
        map.put(key, value);
    }

    @Override
    public Long get(Integer key) {
        Long result= map.get(key);
        if(result == null)
            ++miss;
        else
            ++hit;
        return result;
    }

    @Override
    public Long invalidate(Integer key) {
        return map.remove(key);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public double hitRatio() {
        return hit/(double)(hit+miss);
    }

    @Override
    public void resetHitRatio() {
        hit= 0L;
        miss= 0L;
    }

    private class LimitedHashMap extends LinkedHashMap<Integer, Long>{
        private static final long serialVersionUID = -831411504252696399L;
        private int capacity;

        public LimitedHashMap(int capacity){
            super(capacity, 0.75f, true);
            this.capacity= capacity;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<Integer, Long> eldest) {
            return size() > capacity;
        }
    }
}
