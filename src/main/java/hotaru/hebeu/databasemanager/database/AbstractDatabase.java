package hotaru.hebeu.databasemanager.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

abstract public class AbstractDatabase {
    private String name;
    protected Connection connection;
    protected Set<String> tables;

    protected AbstractDatabase(String n){
        name = n;
    }
    public String getName(){return name;}
    abstract public void createConnection();
    public Connection getConnection(){return connection;}
    abstract protected void loadTables() throws SQLException;
    abstract public void createTable(String tableName);
    abstract public void deleteTable(String tableName);
}
