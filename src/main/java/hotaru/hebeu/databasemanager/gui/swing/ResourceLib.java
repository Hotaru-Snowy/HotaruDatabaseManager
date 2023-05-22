package hotaru.hebeu.databasemanager.gui.swing;

import javax.swing.*;

/**
 * 资源库文件
 * 虽然目前只有给JTree用到的图标并且最后好像没用到
 */
public final class ResourceLib {
    private final static ImageIcon rootImageIcon = new ImageIcon("img/databases.png");
    private final static ImageIcon databaseImageIcon = new ImageIcon("img/database.png");
    private final static ImageIcon tableImageIcon = new ImageIcon("img/database.png");
    static ImageIcon getRootImageIcon(){
        return rootImageIcon;
    }
    static ImageIcon getDatabaseImageIcon(){
        return databaseImageIcon;
    }
    static ImageIcon getTableImageIcon(){
        return tableImageIcon;
    }
}
