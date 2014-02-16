package Server;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

class Parameters {
    public static final String SERVER_PROPERTIES_FILE_NAME= "server.properties";
    public static final String DATABASE_ACCOUNTS_TABLE_NAME= "accounts";
    public static final int PORT_FOR_RMI= 1099;
    public static final String ACCOUNT_SERVICE_OBJECT_NAME= "myAccountServiceObject";
    public final String JDBC_DRIVERS;
    public final String JDBC_URL;
    public final String JDBC_USERNAME;
    public final String JDBC_PASSWORD;
    public final int CACHE_CAPACITY;
    public final boolean START_WITH_EXISTING_DATABASE;


    private static Parameters instance;

    private Parameters() throws IOException {
        Properties props= new Properties();
        FileInputStream in= new FileInputStream(SERVER_PROPERTIES_FILE_NAME);
        props.load(in);
        in.close();

        JDBC_DRIVERS= props.getProperty("jdbc.drivers");
        JDBC_URL= props.getProperty("jdbc.url");
        JDBC_USERNAME= props.getProperty("jdbc.username");
        JDBC_PASSWORD= props.getProperty("jdbc.password");
        String cacheCapacityStr= props.getProperty("cache.capacity");
        String startWithExistingDatabaseStr= props.getProperty("database.start_with_existing");



        if(JDBC_DRIVERS == null || JDBC_PASSWORD == null ||
                JDBC_URL == null || JDBC_USERNAME == null ||
                cacheCapacityStr == null || startWithExistingDatabaseStr == null)
            throw new IOException("Invalid properties file content");

        CACHE_CAPACITY= Integer.parseInt(cacheCapacityStr);
        START_WITH_EXISTING_DATABASE= Boolean.parseBoolean(startWithExistingDatabaseStr);
    }

    public static Parameters getInstance() throws IOException {
        if(instance == null)
            instance= new Parameters();
        return instance;
    }
}
