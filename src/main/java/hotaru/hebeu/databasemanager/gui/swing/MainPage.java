package hotaru.hebeu.databasemanager.gui.swing;

import hotaru.hebeu.databasemanager.Main;
import hotaru.hebeu.databasemanager.database.AbstractDatabase;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 主界面框架
 */
public class MainPage extends JFrame {
    LeftPanel lp;
    RightPanel rp;
    JMenuBar jmb;
    public MainPage(){
        super("数据库管理工具");
        int width = Toolkit.getDefaultToolkit().getScreenSize().width;
        int height = Toolkit.getDefaultToolkit().getScreenSize().height;
        int windowsWidth = 1000;
        int windowsHeight = 500;
        setBounds((width - windowsWidth) / 2,(height - windowsHeight) / 2, windowsWidth, windowsHeight);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        lp = new LeftPanel(this);
        rp = new RightPanel(this);

        GridBagLayout gbl=new GridBagLayout();
        GridBagConstraints gbc=new GridBagConstraints();
        setLayout(gbl);
        gbc.fill=GridBagConstraints.BOTH;
        gbc.gridx=0;
        gbc.gridy=0;
        gbc.weightx=1;
        gbc.weighty=1;
        add(lp,gbc);
        gbc.gridx=1;
        gbc.weightx=2;
        add(rp,gbc);

        jmb = new SwingMenuBar(this);
        setJMenuBar(jmb);

        initTree();
    }
    void putDataIntoTable(PreparedStatement sm){
        rp.putData(sm);
        try {
            if(!sm.isClosed()) sm.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //测试窗体用的临时主函数
    public static void main(String[] args){
        MainPage m = new MainPage();
        m.setVisible(true);
    }
    //其实这个方法或许应该放到LeftPanel里，懒得挪了。
    private void initTree(){
        File f = new File("./databases/");
        System.out.println(f.getAbsolutePath());
        if(!f.exists())f.mkdirs();
        else{
            File[] files = f.listFiles();
            if(files!=null){
                for(File fd : files){
                    if(fd.isFile() && fd.getName().endsWith(".db")){
                        String name = AbstractButtonListener.removeExtension(fd.getName());
                        AbstractDatabase ad = Main.databases.register(name,null);
                        try {
                            ad.createConnection();
                            AbstractButtonListener.createDatabaseNode(ad, this);
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            System.out.println("数据库文件加载失败："+name);
                            Main.databases.remove(name,null);
                        }
                    }
                }
            }
        }
    }
}
