package hotaru.hebeu.databasemanager;

import hotaru.hebeu.databasemanager.sqlcontent.SqliteConnection;

public class Main {
    public static void main(String[] args) {
        init();
        SqliteConnection.test();
    }
    private static void init(){
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}