package hotaru.hebeu.databasemanager.sqlcontent;
import java.sql.*;

public class SqliteConnection {
    static Connection conn = null;
    static ResultSet resultSet = null;
    static Statement statement = null;
    public static void test(){
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:databases/test.db");
            statement = conn.createStatement();
            statement.executeUpdate("drop table if exists tables");
            statement.executeUpdate("create table tables(name varchar(20),pwd varchar(20))");
            statement.executeUpdate("insert into tables values('admin','admin')");
            resultSet = statement.executeQuery("SELECT * FROM tables");


            while (resultSet.next()){
                System.out.println(resultSet.getString("name")+" "+resultSet.getString("pwd"));
            }


        }catch (ClassNotFoundException ex){
            System.out.println(ex.getMessage());
            ex.printStackTrace();

        }
        catch (SQLException sqlex){
            System.out.println(sqlex.getMessage());
            sqlex.printStackTrace();
        }
        finally {
            try {
                resultSet.close();
                statement.close();
                conn.close();
            }
            catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
    }
}

