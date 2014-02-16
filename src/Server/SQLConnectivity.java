package Server;

import java.io.IOException;
import java.sql.*;
import java.util.logging.Level;

import static Server.Parameters.DATABASE_ACCOUNTS_TABLE_NAME;

//TODO It will be better to use prepared SQL queries, but unfortunately deadline is almost here
class SQLConnectivity {
    private Connection connection;
    private DatabaseMetaData dbMetaData;

    public SQLConnectivity() throws IOException, SQLException {
        connection= getConnection();
        dbMetaData= connection.getMetaData();

        if(!Parameters.getInstance().START_WITH_EXISTING_DATABASE){//if we should start with new database
            //if table already exists remove it from database
            ResultSet mrs= dbMetaData.getTables(null, null, null, new String[]{ "TABLE" });
            while (mrs.next())
                if(mrs.getString(3).equals(DATABASE_ACCOUNTS_TABLE_NAME)){
                    removeTable(DATABASE_ACCOUNTS_TABLE_NAME);
                    break;
                }
            mrs.close();

        //creating table
        createTable(DATABASE_ACCOUNTS_TABLE_NAME);
        }
        else{
            //check the existing of table
            ResultSet mrs= dbMetaData.getTables(null, null, null, new String[]{ "TABLE" });
            boolean tableExist= false;
            while (mrs.next())
                if(mrs.getString(3).equals(DATABASE_ACCOUNTS_TABLE_NAME)){
                    tableExist= true;
                    break;
                }
            mrs.close();
            if(!tableExist)//if table does not exist throw exception
                throw new SQLException("Table " + DATABASE_ACCOUNTS_TABLE_NAME + " does not EXIST");
        }
    }
    public Long getAmount(Integer id) throws SQLException {
        Statement st= connection.createStatement();

        String stringQuery= "SELECT * FROM " + DATABASE_ACCOUNTS_TABLE_NAME +
                " WHERE id = " + id;

        ResultSet resultSet= st.executeQuery(stringQuery);
        Long balance;
        if(!resultSet.next()){
            //no such id in DB
            balance= 0L;
        }
        else{//there is such id
            balance= resultSet.getLong("amount");
        }

        st.close();
        return balance;
    }
    public void addAmount(Integer id, Long value) throws SQLException {
        Statement st= connection.createStatement();
        String strQuery= "SELECT id FROM " + DATABASE_ACCOUNTS_TABLE_NAME +
                " WHERE id = " + id;
        ResultSet resultSet= st.executeQuery(strQuery);

        if(!resultSet.next()){
            //no such id in DB therefore create it
            String cmd= "INSERT INTO " + DATABASE_ACCOUNTS_TABLE_NAME + "(id,amount) VALUES " +
                    "(" + id + "," + value + ");";
            st.execute(cmd);
        }
        else{//otherwise add to the existing id
            String cmd= "UPDATE " + DATABASE_ACCOUNTS_TABLE_NAME +
                    " SET amount= amount + " + value +
                    " WHERE id = " + id;
            st.execute(cmd);
        }
        st.close();
    }
    public void close(){
        try {
            connection.close();
        } catch (SQLException e) {
            ServerLogger.getInstance().log(Level.SEVERE, "Exception while closing SQLConnection", e);
        }
    }
    private Connection getConnection() throws IOException, SQLException {
        Parameters params= Parameters.getInstance();
        System.setProperty("jdbc.drivers", params.JDBC_DRIVERS);

        String url= params.JDBC_URL;
        String username= params.JDBC_USERNAME;
        String password= params.JDBC_PASSWORD;

        return DriverManager.getConnection(url, username, password);
    }
    private void removeTable(String name) throws SQLException {
        Statement st= connection.createStatement();
        st.execute("DROP TABLE " + name);
        st.close();
    }
    private void createTable(String name) throws SQLException, IOException {
        String cmd= "CREATE TABLE " + name + "(" +
                "id INT PRIMARY KEY NOT NULL UNIQUE," +
                "amount BIGINT  NOT NULL)";

        Statement st= connection.createStatement();
        st.execute(cmd);
        st.close();
    }
}
