package hotaru.hebeu.databasemanager.database.sqlite;

import hotaru.hebeu.databasemanager.database.AbstractDatabase;

import java.io.File;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

/**
 * Sqlite实现的数据库类
 */
public class SqliteDatabase extends AbstractDatabase {
    private String parentPath;
    public SqliteDatabase(String n) {
        super(n);
    }
    public SqliteDatabase(String n,String adr) {
        super(n);
        parentPath=adr;
    }

    /**
     * {@inheritDoc}
     * @throws SQLException {@inheritDoc}
     */
    @Override
    public void createConnection() throws SQLException {
            if(parentPath!=null){
                connection = DriverManager.getConnection("jdbc:sqlite:"+parentPath+ File.separatorChar+getName()+".db");
            }else connection = DriverManager.getConnection("jdbc:sqlite:databases/"+getName()+".db");
            loadTables();
    }

    /**
     * {@inheritDoc}
     * @throws SQLException {@inheritDoc}
     */
    @Override
    protected void loadTables() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM sqlite_master WHERE type = \"table\"");
        tables = new TreeSet<>();
        while(resultSet.next()){
            tables.add(resultSet.getString("name"));
        }
        resultSet.close();
        statement.close();
    }

    /**
     * {@inheritDoc}
     * @param tableName 数据表名
     * @throws SQLException {@inheritDoc}
     */
    @Override
    public void createTable(String tableName) throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate("CREATE TABLE \""+tableName+"\" (rowid TEXT)");
        statement.executeUpdate("INSERT INTO \""+tableName+"\" (\"rowid\") VALUES (\"1\")");
        tables.add(tableName);
        statement.close();
    }

    /**
     * {@inheritDoc}
     * @param tableName 数据表名
     */
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

    /**
     * {@inheritDoc}
     * @param tableName 数据表名
     * @return {@inheritDoc}
     */
    @Override
    public boolean contains(String tableName) {
        return tables.contains(tableName);
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public Set<String> getTables() {
        return new HashSet<>(tables);
    }

    /**
     * {@inheritDoc}
     * 默认全为空
     * @param tableName 数据表名
     * @throws SQLException {@inheritDoc}。如果库中有自己设定的主键也会抛出。
     */
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

    /**
     * {@inheritDoc}
     * 由于Sqlite没有实现ResultSet的删除行方法，导致此方法删除效率极低
     * @param tableName 目标数据表名
     * @param rowIndex 要删除的行数
     * @throws SQLException {@inheritDoc}
     */
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
            StringBuffer sb = new StringBuffer("CREATE TABLE \"").append(tableName).append("_hotarubackup\" (").append("\"").append(name.get(0)).append("\"");
            for(int i=1;i<name.size();i++) sb.append(',').append("\"").append(name.get(i)).append("\"");
            sb.append(')');
            System.out.println(sb);
            statement.executeUpdate(sb.toString());
            for (Vector<Object> datum : data) {
                sb = new StringBuffer("INSERT INTO \"" + tableName + "_hotarubackup\" VALUES (").append(datum.get(0));
                for (int j = 1; j < datum.size(); j++)
                    sb.append(',').append(datum.get(j));
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

    /**
     * {@inheritDoc}
     * @param tableName 数据表名
     * @param columnName 要加入的列的列名
     * @throws SQLException {@inheritDoc}
     */
    @Override
    public void addNewColumn(String tableName, String columnName) throws SQLException {
        if(tables.contains(tableName)){
            Statement statement = connection.createStatement();
            statement.executeUpdate("ALTER TABLE \""+tableName+"\" ADD COLUMN \""+columnName+"\" TEXT");
            statement.close();
        }
    }

    /**
     * {@inheritDoc}
     * 由于Sqlite自己本身没有删除列方法，导致此方法删除效率极低
     * @param tableName 目标数据表名
     * @param columnName 要删除的目标列名
     * @throws SQLException {@inheritDoc}
     */
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
            StringBuffer sb = new StringBuffer("CREATE TABLE \"").append(tableName).append("_hotarubackup\" (").append("\"").append(name.get(0)).append("\"");
            for(int i=1;i<name.size();i++) sb.append(',').append("\"").append(name.get(i)).append("\"");
            sb.append(')');
            System.out.println(sb);
            statement.executeUpdate(sb.toString());
            for (Vector<Object> datum : data) {
                sb = new StringBuffer("INSERT INTO \"" + tableName + "_hotarubackup\" VALUES (").append(datum.get(0));
                for (int j = 1; j < datum.size(); j++)
                    sb.append(',').append(datum.get(j));
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

    /**
     * {@inheritDoc}
     * 由于Sqlite没有实现ResultSet的update方法，而本身就不能通过其他手段（例如主键）定位行，导致此方法删除效率极低
     * 这里也是最诟病的地方，比上面两个还烦，垃圾Sqlite，狗都不用
     * @param tableName 数据表名
     * @param columnName 目标列名
     * @param rowIndex 目标行序号
     * @param value 修改前的值
     * @throws SQLException {@inheritDoc}
     */
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
                    if(ri==rowIndex+1 && name.get(i).equals(columnName)){
                        if(value!=null && value instanceof String)
                            tepv.add("\""+value+"\"");
                        else tepv.add(value);
                    }
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
            StringBuffer sb = new StringBuffer("CREATE TABLE \"").append(tableName).append("_hotarubackup\" (").append("\"").append(name.get(0)).append("\"");
            for(int i=1;i<name.size();i++) sb.append(',').append("\"").append(name.get(i)).append("\"");
            sb.append(')');
            System.out.println(sb);
            statement.executeUpdate(sb.toString());
            for (Vector<Object> datum : data) {
                sb = new StringBuffer("INSERT INTO \"" + tableName + "_hotarubackup\" VALUES (").append(datum.get(0));
                for (int j = 1; j < datum.size(); j++)
                    sb.append(',').append(datum.get(j));
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

    /**
     * {@inheritDoc}
     * @param tableName 目标数据表名
     * @return {@inheritDoc}
     */
    @Override
    public PreparedStatement getTableData(String tableName) {
        if(tables.contains(tableName)){
            try {
                return connection.prepareStatement("SELECT * FROM \""+tableName+"\"");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
