package hotaru.hebeu.databasemanager;

import hotaru.hebeu.databasemanager.database.sqlite.SqliteDatabases;
import hotaru.hebeu.databasemanager.gui.swing.MainPage;

public class Main {
    public static SqliteDatabases databases;

    //启动Main方法，jar启动的指向对象
    public static void main(String[] args) {
        init();
        //SqliteConnection.test();
    }

    private static void init(){
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        databases = new SqliteDatabases();
        MainPage mp = new MainPage();
        mp.setVisible(true);
    }
}