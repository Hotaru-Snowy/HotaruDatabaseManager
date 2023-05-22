package hotaru.hebeu.databasemanager.database.sqlite;

import hotaru.hebeu.databasemanager.database.AbstractDatabase;
import hotaru.hebeu.databasemanager.database.AbstractDatabases;

import java.io.File;
import java.sql.SQLException;

/**
 * Sqlite数据库注册类的实现
 */
public class SqliteDatabases extends AbstractDatabases {
    public SqliteDatabases(){
        super();
    }

    /**
     * {@inheritDoc}
     * @param name 数据库名
     * @param args 数据库地址
     * @return SqliteDatabase 注册好的数据库，如果注册失败返回null
     */
    @Override
    public SqliteDatabase register(String name, String[] args) {
        if(!contain(name,args)) {
            SqliteDatabase sd;
            if(args!=null){
                sd = new SqliteDatabase(name,args[0]);
                getLibList().put(args[0]+ File.separatorChar+name,sd);
            }else{
                sd = new SqliteDatabase(name);
                getLibList().put(name,sd);
            }
            return sd;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     * @param name 数据库名
     * @param args 数据库地址
     * @return {@inheritDoc}
     */
    @Override
    public boolean contain(String name, String[] args) {
        return getLibList().containsKey(getKey(name,args));
    }

    /**
     * {@inheritDoc}
     * @param name 数据库名
     * @param args 数据库地址
     * @return {@inheritDoc}
     */
    @Override
    public boolean remove(String name, String[] args) {
        if(contain(name,args)){
            try {
                getLibList().remove(getKey(name,args)).close();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * @param name 数据库名
     * @param args 数据库地址
     * @return AbstractDatabase 注册好的数据库，如果列表内没有则返回null
     */
    @Override
    public AbstractDatabase getDatabase(String name, String[] args) {
        return getLibList().get(getKey(name,args));
    }
    private String getKey(String name, String[] args){
        if(args!=null)return args[0]+ File.separatorChar+name;
        else return name;
    }
}
