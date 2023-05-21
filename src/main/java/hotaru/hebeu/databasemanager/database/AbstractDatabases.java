package hotaru.hebeu.databasemanager.database;

import hotaru.hebeu.databasemanager.database.AbstractDatabase;

import java.util.HashMap;
import java.util.Map;

abstract public class AbstractDatabases {
    private Map<String,AbstractDatabase> libList;
    public AbstractDatabases(){
        libList = new HashMap<String,AbstractDatabase>();
    }
    abstract public AbstractDatabase register(String name,String args[]);
    abstract public boolean contain(String name,String args[]);
    abstract public boolean remove(String name,String args[]);
    abstract public AbstractDatabase getDatabase(String name,String args[]);
    protected Map<String,AbstractDatabase> getLibList(){return libList;}
}
