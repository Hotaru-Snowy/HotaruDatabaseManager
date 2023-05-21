package hotaru.hebeu.databasemanager.database.sqlite;

import hotaru.hebeu.databasemanager.database.AbstractDatabase;

import java.io.File;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

public class SqliteDatabase extends AbstractDatabase {
    private String parentPath;
    public SqliteDatabase(String n) {
        super(n);
    }
    public SqliteDatabase(String n,String adr) {
        super(n);
        parentPath=adr;
    }

    @Override
    public void createConnection() throws SQLException {
            if(parentPath!=null){
                connection = DriverManager.getConnection("jdbc:sqlite:"+parentPath+ File.separatorChar+getName()+".db");
            }else connection = DriverManager.getConnection("jdbc:sqlite:databases/"+getName()+".db");
            loadTables();
    }

    @Override
    protected void loadTables() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM sqlite_master WHERE type = \"table\"");
        tables = new TreeSet<String>();
        while(resultSet.next()){
            tables.add(resultSet.getString("name"));
        }
        resultSet.close();
        statement.close();
    }

    @Override
    public void createTable(String tableName) throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate("CREATE TABLE \""+tableName+"\" (rowid TEXT)");
        statement.executeUpdate("INSERT INTO \""+tableName+"\" (\"rowid\") VALUES (\"1\")");
        tables.add(tableName);
        statement.close();
    }

    @Override
    public void deleteTable(String tableName) {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("DROP TABLE IF EXISTS \""+tableName+"\"");
            tables.remove(tableName);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean contains(String tableName) {
        return tables.contains(tableName);
    }

    @Override
    public Set<String> getTables() {
        return new HashSet<>(tables);
    }

    @Override
    public void addNewRow(String tableName) throws SQLException {
        if(tables.contains(tableName)){
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM \""+tableName+"\"");
            ResultSetMetaData rsmd = rs.getMetaData();
            int ni;
            for(ni=1;ni<=rsmd.getColumnCount();ni++)if(rsmd.isNullable(ni)==ResultSetMetaData.columnNullable)break;
            String n1 = rsmd.getColumnName(ni);
            if(rsmd.isNullable(ni)==ResultSetMetaData.columnNullable)
                statement.executeUpdate("INSERT INTO \""+tableName+"\" (\""+n1+"\") VALUES (NULL)");
            else {
                statement.executeUpdate("INSERT INTO \""+tableName+"\" (\""+n1+"\") VALUES (11)");
            }
            rs.close();
            statement.close();
        }
    }

    @Override
    public void deleteRow(String tableName, int rowIndex) throws SQLException {
        if(tables.contains(tableName)){
            close();
            createConnection();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM \""+tableName+"\"");
            ResultSetMetaData rmd = rs.getMetaData();
            Vector<Vector<Object>> data = new Vector<>();
            Vector<Object> name = new Vector<>();
            int columnCount = rmd.getColumnCount();
            int ri = 0;
            for(int i=0;i<columnCount;i++)name.add(rmd.getColumnName(i+1));
            while(rs.next()){
                if(++ri==rowIndex+1)continue;
                Vector<Object> tepv = new Vector<>(columnCount);
                for(int i=0;i<columnCount;i++){
                    if(rs.getObject(i+1)!=null && rs.getObject(i+1) instanceof String)
                        tepv.add("\""+rs.getObject(i+1)+"\"");
                    else tepv.add(rs.getObject(i+1));
                }
                data.add(tepv);
            }
            rs.close();
            statement.executeUpdate("DROP TABLE IF EXISTS \""+tableName+"_hotarubackup\"");
            StringBuffer sb = new StringBuffer("CREATE TABLE \"").append(tableName).append("_hotarubackup\" (").append("\""+name.get(0)+"\"");
            for(int i=1;i<name.size();i++)sb.append(',').append("\""+name.get(i)+"\"");
            sb.append(')');
            System.out.println(sb);
            statement.executeUpdate(sb.toString());
            for(int i=0;i<data.size();i++){
                sb=new StringBuffer("INSERT INTO \""+tableName+"_hotarubackup\" VALUES (").append(data.get(i).get(0));
                for(int j=1;j<data.get(i).size();j++)
                    sb.append(',').append(data.get(i).get(j));
                sb.append(')');
                System.out.println(sb);
                statement.executeUpdate(sb.toString());
            }
            statement.close();
            statement = connection.createStatement();
            statement.executeUpdate("DROP TABLE IF EXISTS \""+tableName+"\"");
            statement.executeUpdate("ALTER TABLE \""+tableName+"_hotarubackup\" rename to \""+tableName+"\"");
            statement.executeUpdate("DROP TABLE IF EXISTS \""+tableName+"_hotarubackup\"");
        }
    }

    @Override
    public void addNewColumn(String tableName, String columnName) throws SQLException {
        if(tables.contains(tableName)){
            Statement statement = connection.createStatement();
            statement.executeUpdate("ALTER TABLE \""+tableName+"\" ADD COLUMN \""+columnName+"\" TEXT");
            statement.close();
        }
    }

    @Override
    public void deleteColumn(String tableName, String columnName) throws SQLException {
        if(tables.contains(tableName)){
            close();
            createConnection();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM \""+tableName+"\"");
            ResultSetMetaData rmd = rs.getMetaData();
            Vector<Vector<Object>> data = new Vector<>();
            Vector<Object> name = new Vector<>();
            int columnCount = rmd.getColumnCount();
            int ri = 0;
            for(int i=0;i<columnCount;i++){
                if(!rmd.getColumnName(i+1).equalsIgnoreCase(columnName))name.add(rmd.getColumnName(i+1));
                else ri=i;
            }

            while(rs.next()){
                Vector<Object> tepv = new Vector<>(columnCount-1);
                for(int i=0;i<columnCount;i++){
                    if(i==ri)continue;
                    if(rs.getObject(i+1)!=null && rs.getObject(i+1) instanceof String)
                        tepv.add("\""+rs.getObject(i+1)+"\"");
                    else tepv.add(rs.getObject(i+1));
                }
                data.add(tepv);
            }
            rs.close();
            statement.executeUpdate("DROP TABLE IF EXISTS \""+tableName+"_hotarubackup\"");
            StringBuffer sb = new StringBuffer("CREATE TABLE \"").append(tableName).append("_hotarubackup\" (").append("\""+name.get(0)+"\"");
            for(int i=1;i<name.size();i++)sb.append(',').append("\""+name.get(i)+"\"");
            sb.append(')');
            System.out.println(sb);
            statement.executeUpdate(sb.toString());
            for(int i=0;i<data.size();i++){
                sb=new StringBuffer("INSERT INTO \""+tableName+"_hotarubackup\" VALUES (").append(data.get(i).get(0));
                for(int j=1;j<data.get(i).size();j++)
                    sb.append(',').append(data.get(i).get(j));
                sb.append(')');
                System.out.println(sb);
                statement.executeUpdate(sb.toString());
            }
            statement.close();
            statement = connection.createStatement();
            statement.executeUpdate("DROP TABLE IF EXISTS \""+tableName+"\"");
            statement.executeUpdate("ALTER TABLE \""+tableName+"_hotarubackup\" rename to \""+tableName+"\"");
            statement.executeUpdate("DROP TABLE IF EXISTS \""+tableName+"_hotarubackup\"");
        }
    }

    @Override
    public void editValue(String tableName, String columnName, int rowIndex, Object value) throws SQLException {
        if(tables.contains(tableName)){
            close();
            createConnection();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM \""+tableName+"\"");
            ResultSetMetaData rmd = rs.getMetaData();
            Vector<Vector<Object>> data = new Vector<>();
            Vector<Object> name = new Vector<>();
            int columnCount = rmd.getColumnCount();
            int ri = 0;
            for(int i=0;i<columnCount;i++)name.add(rmd.getColumnName(i+1));
            while(rs.next()){
                ri++;
                Vector<Object> tepv = new Vector<>(columnCount);
                for(int i=0;i<columnCount;i++){
                    if(ri==rowIndex+1 && name.get(i).equals(columnName)) tepv.add(value);
                    else {
                        if(rs.getObject(i+1)!=null && rs.getObject(i+1) instanceof String)
                            tepv.add("\""+rs.getObject(i+1)+"\"");
                        else tepv.add(rs.getObject(i+1));
                    }
                }
                data.add(tepv);
            }
            rs.close();
            statement.executeUpdate("DROP TABLE IF EXISTS \""+tableName+"_hotarubackup\"");
            StringBuffer sb = new StringBuffer("CREATE TABLE \"").append(tableName).append("_hotarubackup\" (").append("\""+name.get(0)+"\"");
            for(int i=1;i<name.size();i++)sb.append(',').append("\""+name.get(i)+"\"");
            sb.append(')');
            System.out.println(sb);
            statement.executeUpdate(sb.toString());
            for(int i=0;i<data.size();i++){
                sb=new StringBuffer("INSERT INTO \""+tableName+"_hotarubackup\" VALUES (").append(data.get(i).get(0));
                for(int j=1;j<data.get(i).size();j++)
                    sb.append(',').append(data.get(i).get(j));
                sb.append(')');
                System.out.println(sb);
                statement.executeUpdate(sb.toString());
            }
            statement.close();
            statement = connection.createStatement();
            statement.executeUpdate("DROP TABLE IF EXISTS \""+tableName+"\"");
            statement.executeUpdate("ALTER TABLE \""+tableName+"_hotarubackup\" rename to \""+tableName+"\"");
            statement.executeUpdate("DROP TABLE IF EXISTS \""+tableName+"_hotarubackup\"");
        }
    }

    @Override
    public PreparedStatement getTableData(String tableName) {
        if(tables.contains(tableName)){
            try {
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM \""+tableName+"\"");
                return statement;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
