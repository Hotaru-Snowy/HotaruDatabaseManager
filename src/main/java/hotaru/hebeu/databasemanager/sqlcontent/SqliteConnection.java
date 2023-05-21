package hotaru.hebeu.databasemanager.sqlcontent;
import java.sql.*;
import java.util.Vector;

public class SqliteConnection {
    static Connection conn = null;
    static ResultSet resultSet = null;
    static Statement statement = null;
    public static void test(){
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:databases/test.db");
            statement = conn.createStatement();
            statement.executeUpdate("drop table if exists tables");
            statement.executeUpdate("create table tables(name varchar(20),pwd varchar(20))");
            statement.executeUpdate("insert into tables values('admin','admin')");
            statement.executeUpdate("insert into tables values('123','456')");
            resultSet = statement.executeQuery("SELECT * FROM tables");

            while (resultSet.next()){
                System.out.println(resultSet.getString("name")+" "+resultSet.getString("pwd"));
            }
            resultSet.close();
            PreparedStatement psm = conn.prepareStatement("SELECT * FROM sqlite_master");
            resultSet = psm.executeQuery();
            ResultSetMetaData rmd = resultSet.getMetaData();
            Vector<Vector<Object>> data = new Vector<>();
            Vector<Object> name = new Vector<>();
            int n = rmd.getColumnCount();
            for(int i=0;i<n;i++)System.out.print(rmd.getColumnName(i+1)+"\t");
            System.out.println();
            while(resultSet.next()){
                for(int i=0;i<n;i++)System.out.print(resultSet.getString(i+1)+"\t");
                System.out.println();
            }


        }catch (SQLException sqlex){
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

