package Server;

class Statistics {
    private long startTimeMillis;
    private long currGetCount;
    private long currTotalGetCount;
    private long currAddCount;
    private long currTotalAddCount;
    private long totalCount;

    private double cacheHitRatio;
    private Thread infoThread;


    public Statistics(){
        startTimeMillis= System.currentTimeMillis();
        infoThread= new Thread(new InfoRunnable(this));
    }

    synchronized long getStartTimeMillis() {
        return startTimeMillis;
    }
    synchronized long getCurrGetCount() {
        return currGetCount;
    }
    synchronized long getCurrTotalGetCount() {
        return currTotalGetCount;
    }
    synchronized long getCurrAddCount() {
        return currAddCount;
    }
    synchronized long getCurrTotalAddCount() {
        return currTotalAddCount;
    }
    synchronized long getTotalCount() {
        return totalCount;
    }
    synchronized double getCacheHitRatio() {
        return cacheHitRatio;
    }

    public synchronized void resetStatistics(){
        startNewPeriod();
        currGetCount= 0L;
        currTotalGetCount= 0L;
        currAddCount= 0L;
        currTotalAddCount= 0L;
        totalCount= 0L;

        ServerLogger.getInstance().info("Statistics reset has been done");
    }
    public synchronized void registerNewGetCall(){
        ++currGetCount;
        ++totalCount;
        ++currTotalGetCount;
    }
    public synchronized void registerNewAddCall(){
        ++currAddCount;
        ++totalCount;
        ++currTotalAddCount;
    }
    public synchronized void updateCacheHitRatio(double cacheHitRatio){
        this.cacheHitRatio= cacheHitRatio;
    }
    synchronized void startNewPeriod(){
        startTimeMillis= System.currentTimeMillis();
        currAddCount= 0L;
        currGetCount= 0L;
    }
    public void start(){
        infoThread.start();
    }
    public void close(){
        infoThread.interrupt();
        try {
            infoThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

class InfoRunnable implements Runnable{
    private Statistics st;

    public InfoRunnable(Statistics st){
        this.st= st;
    }

    @Override
    public void run() {
        while(true){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            long elapsedTime= System.currentTimeMillis() - st.getStartTimeMillis();
            final long MILLIS_IN_SECOND= 1000L;

            StringBuilder record= new StringBuilder();
            record.append("add: " + st.getCurrTotalAddCount() + "(" + st.getCurrAddCount()*MILLIS_IN_SECOND/elapsedTime + " in sec)");
            record.append(" get: " + st.getCurrTotalGetCount() + "(" + st.getCurrGetCount()*MILLIS_IN_SECOND/elapsedTime + " in sec)");
            record.append(" total: " + st.getTotalCount() + "(" + (st.getCurrAddCount()+st.getCurrGetCount())*MILLIS_IN_SECOND/elapsedTime + " in sec)");
            record.append(" cache_hit_ratio: " + (int)(st.getCacheHitRatio()*100)+ "%");

            ServerLogger.getInstance().info(record.toString());

            st.startNewPeriod();


            if(Thread.currentThread().isInterrupted())//on closing
                return;
        }
    }
}



