package hotaru.hebeu.databasemanager.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
    abstract public void createConnection() throws SQLException;
    public Connection getConnection(){return connection;}
    abstract protected void loadTables() throws SQLException;
    abstract public void createTable(String tableName) throws SQLException;
    abstract public void deleteTable(String tableName);
    abstract public boolean contains(String tableName);
    abstract public Set<String> getTables();
    abstract public void addNewRow(String tableName) throws SQLException;
    abstract public void deleteRow(String tableName,int rowIndex) throws SQLException;
    abstract public void addNewColumn(String tableName, String columnName) throws SQLException;
    abstract public void deleteColumn(String tableName,String columnName) throws SQLException;
    abstract public void editValue(String tableName,String columnName,int rowIndex,Object value) throws SQLException;
    abstract public PreparedStatement getTableData(String tableName);
    public void close() throws SQLException {
        if(!connection.isClosed())connection.close();
    }
    public String toString(){
        return name;
    }
}
