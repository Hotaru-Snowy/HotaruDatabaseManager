package hotaru.hebeu.databasemanager.database.sqlite;

import hotaru.hebeu.databasemanager.database.AbstractDatabase;
import hotaru.hebeu.databasemanager.database.AbstractDatabases;

import java.io.File;
import java.sql.SQLException;

public class SqliteDatabases extends AbstractDatabases {
    public SqliteDatabases(){
        super();
    }
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
    @Override
    public boolean contain(String name, String[] args) {
        return getLibList().containsKey(getKey(name,args));
    }

    @Override
    public boolean remove(String name, String[] args) {
        if(contain(name,args)){
            try {
                getLibList().remove(getKey(name,args)).close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public AbstractDatabase getDatabase(String name, String[] args) {
        return getLibList().get(getKey(name,args));
    }
    private String getKey(String name, String[] args){
        if(args!=null)return args[0]+ File.separatorChar+name;
        else return name;
    }
}
