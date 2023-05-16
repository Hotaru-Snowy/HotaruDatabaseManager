package hotaru.hebeu.databasemanager.database.sqlite;

import hotaru.hebeu.databasemanager.database.AbstractDatabase;

import java.sql.*;
import java.util.TreeSet;

public class SqliteDatabase extends AbstractDatabase {
    public SqliteDatabase(String n) {
        super(n);
    }

    @Override
    public void createConnection() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:databases/"+getName()+".db");
            loadTables();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void loadTables() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM sqlite_master WHERE type = ‘table’");
        tables = new TreeSet<String>();
        while(resultSet.next()){
            tables.add(resultSet.getString("name"));
        }
        resultSet.close();
        statement.close();
    }

    @Override
    public void createTable(String tableName) {
        try {
            PreparedStatement statement = connection.prepareStatement("CREATE TABLE ? (rowid INTEGER PRIMARY KEY) WITHOUT ROWID");
            statement.setString(1,tableName);
            statement.executeUpdate();
            tables.add(tableName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteTable(String tableName) {
        try {
            PreparedStatement statement = connection.prepareStatement("DROP TABLE ?");
            statement.setString(1,tableName);
            statement.executeUpdate();
            tables.remove(tableName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
