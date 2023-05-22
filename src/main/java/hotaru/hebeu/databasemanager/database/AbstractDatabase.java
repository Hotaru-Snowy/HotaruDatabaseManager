package hotaru.hebeu.databasemanager.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Set;

/**
 * 抽象数据库类
 * 预设了许多可能会使用的接口，所有数据库应当实现该接口
 */
abstract public class AbstractDatabase {
    private final String name;
    protected Connection connection;
    protected Set<String> tables;

    protected AbstractDatabase(String n){
        name = n;
    }

    /**
     * 获取当前数据库名
     * @return String 数据库名
     */
    public String getName(){return name;}

    /**
     * 创建数据库连接
     * @throws SQLException 创建失败时抛出
     */
    abstract public void createConnection() throws SQLException;

    /**
     * 获取当前数据库的连接
     * @return Connection 返回数据库连接，如果没有创建连接则可能是null
     */
    public Connection getConnection(){return connection;}

    /**
     * 加载数据表，通常在创建数据库连接时调用
     * @throws SQLException 数据表获取失败时抛出
     */
    abstract protected void loadTables() throws SQLException;

    /**
     * 根据数据表名创建数据表
     * @param tableName 数据表名
     * @throws SQLException 数据表创建失败时抛出
     */
    abstract public void createTable(String tableName) throws SQLException;

    /**
     * 根据数据表名删除数据表，如果数据表不存在则不会进行操作
     * @param tableName 数据表名
     */
    abstract public void deleteTable(String tableName);

    /**
     * 检测数据表是否存在
     * @param tableName 数据表名
     * @return true 如果数据表存在，反之返回false
     */
    abstract public boolean contains(String tableName);

    /**
     * 获取数据表名的列表
     * @return Set 一个数据表名构成的集合，如果数据库连接从未创建则有可能为null
     */
    abstract public Set<String> getTables();

    /**
     * 目标数据表增添新空白行
     * @param tableName 数据表名
     * @throws SQLException 插入行失败时抛出
     */
    abstract public void addNewRow(String tableName) throws SQLException;

    /**
     * 目标数据表删除第目标行记录
     * @param tableName 数据表名
     * @param rowIndex 要删除的目标行号
     * @throws SQLException 删除行失败时抛出
     */
    abstract public void deleteRow(String tableName,int rowIndex) throws SQLException;

    /**
     * 目标数据表新增空白列
     * @param tableName 数据表名
     * @param columnName 要加入的列的列名
     * @throws SQLException 新增列失败时抛出
     */
    abstract public void addNewColumn(String tableName, String columnName) throws SQLException;

    /**
     * 目标数据表删除指定列
     * @param tableName 数据表名
     * @param columnName 要删除的列的列名
     * @throws SQLException 删除列失败时抛出
     */
    abstract public void deleteColumn(String tableName,String columnName) throws SQLException;

    /**
     * 目标数据表修改指定位置的数据
     * @param tableName 数据表名
     * @param columnName 目标列名
     * @param rowIndex 目标行序号
     * @param value 修改前的值
     * @throws SQLException 修改数据失败时抛出
     */
    abstract public void editValue(String tableName,String columnName,int rowIndex,Object value) throws SQLException;

    /**
     * 获取数据表数据
     * @param tableName 目标数据表名
     * @return PreparedStatement 一个准备好的SELECT状态数据
     */
    abstract public PreparedStatement getTableData(String tableName);

    /**
     * 关闭数据库连接
     * @throws SQLException 数据库连接关闭失败时抛出
     */
    public void close() throws SQLException {
        if(!connection.isClosed())connection.close();
    }

    /**
     * 获取数据库名
     * @return String 数据库名
     */
    public String toString(){
        return name;
    }
}
