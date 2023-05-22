package hotaru.hebeu.databasemanager.database;

import com.sun.istack.internal.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据库注册类抽象类
 * 所有必须实现的类型之一，用于初始调用和统计数据库，以数据库文件地址和文件名作为主键，便于构建数据库列表
 */
abstract public class AbstractDatabases {
    private final Map<String,AbstractDatabase> libList;
    public AbstractDatabases(){
        libList = new HashMap<>();
    }

    /**
     * 注册数据库
     * @param name 数据库名
     * @param args 数据库地址
     * @return AbstractDatabase 注册好的数据库，如果注册失败返回null
     */
    @Nullable
    abstract public AbstractDatabase register(String name, String[] args);

    /**
     * 检测目标数据库是否已经被注册
     * @param name 数据库名
     * @param args 数据库地址
     * @return true - 如果已经注册过了，反之返回false
     */
    abstract public boolean contain(String name, String[] args);

    /**
     * 删除目标数据库
     * @param name 数据库名
     * @param args 数据库地址
     * @return true，如果删除成功。如果删除失败或者该数据库不在列表内，返回false
     */
    abstract public boolean remove(String name,String[] args);

    /**
     * 根据数据库名和地址返回数据库
     * @param name 数据库名
     * @param args 数据库地址
     * @return AbstractDatabase 注册好的数据库，如果列表内没有则返回null
     */
    @Nullable
    abstract public AbstractDatabase getDatabase(String name,String[] args);

    protected Map<String,AbstractDatabase> getLibList(){return libList;}
}
