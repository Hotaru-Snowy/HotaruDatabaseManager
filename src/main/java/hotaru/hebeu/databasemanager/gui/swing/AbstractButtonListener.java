package hotaru.hebeu.databasemanager.gui.swing;

import hotaru.hebeu.databasemanager.Main;
import hotaru.hebeu.databasemanager.database.AbstractDatabase;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.io.File;
import java.sql.SQLException;

/**
 * 监听方法实现大类，调用数据库的主要地方
 * 有点乱有点臃肿，但是能跑就行啦
 */
public final class AbstractButtonListener {
    private AbstractButtonListener(){}
    static String removeExtension(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int i = filename.lastIndexOf('.');
            if (i >-1 && i < filename.length())return filename.substring(0, i);
        }
        return filename;
    }
    //表格部分
    static void registerAddColumn(AbstractButton i,MainPage parentFrame){
        i.addActionListener(e->{
            try {
                String name = JOptionPane.showInputDialog(parentFrame,"新增列名：");
                if(name==null || name.trim().equals(""))return;
                parentFrame.rp.getDatabaseInShow().addNewColumn(parentFrame.rp.getTableNameInShow(),name);
                parentFrame.rp.flashTable();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(parentFrame, "列增添失败！", "错误！", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    static void registerAddRow(AbstractButton i,MainPage parentFrame){
        i.addActionListener(e->{
            try {
                parentFrame.rp.getDatabaseInShow().addNewRow(parentFrame.rp.getTableNameInShow());
                parentFrame.rp.flashTable();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(parentFrame, "行增添失败！", "错误！", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    static void registerDeleteColumn(AbstractButton i,MainPage parentFrame){
        i.addActionListener(e->{
            try {
                if(parentFrame.rp.getJTable().getColumnCount()<=1){
                    JOptionPane.showMessageDialog(parentFrame, "列数小于等于1，禁止删除！", "错误！", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                parentFrame.rp.getDatabaseInShow().deleteColumn(parentFrame.rp.getTableNameInShow(),parentFrame.rp.getJTable().getColumnName(parentFrame.rp.getJTable().getSelectedColumn()));
                parentFrame.rp.flashTable();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(parentFrame, "列删除失败！", "错误！", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    static void registerDeleteRow(AbstractButton i,MainPage parentFrame){
        i.addActionListener(e->{
            try {
                if(parentFrame.rp.getJTable().getRowCount()<=1){
                    JOptionPane.showMessageDialog(parentFrame, "行数小于等于1，禁止删除！", "错误！", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                parentFrame.rp.getDatabaseInShow().deleteRow(parentFrame.rp.getTableNameInShow(),parentFrame.rp.getJTable().getSelectedRow());
                parentFrame.rp.flashTable();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(parentFrame, "行删除失败！", "错误！", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    //Tree树部分
    static void registerOpenTable(AbstractButton i,MainPage parentFrame){
        i.addActionListener(e->{
            TreePath tpath = parentFrame.lp.getTree().getSelectionPath();
            if (tpath != null && tpath.getPath().length == 3) {
                String name = (String) ((DefaultMutableTreeNode) tpath.getLastPathComponent()).getUserObject();
                AbstractDatabase ad = (AbstractDatabase) ((DefaultMutableTreeNode) tpath.getParentPath().getLastPathComponent()).getUserObject();
                parentFrame.putDataIntoTable(ad.getTableData(name));
                parentFrame.rp.setTableName(ad, name);
            }
        });
    }

    static void registerDeleteTable(AbstractButton i,MainPage parentFrame){
        i.addActionListener(e->{
            TreePath tpath = parentFrame.lp.getTree().getSelectionPath();
            if(tpath!=null && tpath.getPath().length == 3){
                String name = (String) ((DefaultMutableTreeNode) tpath.getLastPathComponent()).getUserObject();
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)tpath.getParentPath().getLastPathComponent();
                AbstractDatabase ad = (AbstractDatabase) node.getUserObject();
                try {
                    ad.close();
                    ad.createConnection();
                    ad.deleteTable(name);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(parentFrame, "数据表删除失败！", "错误！", JOptionPane.ERROR_MESSAGE);
                }
                parentFrame.lp.getTreeModel().removeNodeFromParent((DefaultMutableTreeNode) tpath.getLastPathComponent());
                parentFrame.rp.cleatTable();
            }
        });
    }
    static void registerDeleteDatabase(AbstractButton i, MainPage parentFrame){
        i.addActionListener(e->{
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) parentFrame.lp.getTree().getLastSelectedPathComponent();
            if(node !=null && node.getUserObject() instanceof AbstractDatabase){
                AbstractDatabase ad = (AbstractDatabase) node.getUserObject();
                try {
                    ad.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                node.setUserObject(null);
                parentFrame.lp.getTreeModel().removeNodeFromParent(node);
            }
        });
    }
    static void registerCreateTable(AbstractButton i,MainPage parentFrame){
        i.addActionListener(e->{
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) parentFrame.lp.getTree().getLastSelectedPathComponent();
            if(node.getUserObject() !=null && node.getUserObject() instanceof AbstractDatabase){
                String name = JOptionPane.showInputDialog(parentFrame,"新建数据表名：");
                AbstractDatabase ad = (AbstractDatabase) node.getUserObject();
                if(!ad.getTables().contains(name)) {
                    try {
                        ad.createTable(name);
                        DefaultMutableTreeNode subNode = new DefaultMutableTreeNode(name);
                        parentFrame.lp.getTreeModel().insertNodeInto(subNode, node, 0);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(parentFrame, "数据表创建失败！", "错误！", JOptionPane.ERROR_MESSAGE);
                    }
                }
                else JOptionPane.showMessageDialog(parentFrame, "数据表已存在！", "错误！", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    static void registerCreateDatabase(AbstractButton i,MainPage parentFrame){
        i.addActionListener(e->{
            String name = JOptionPane.showInputDialog(parentFrame,"新建数据库库名：");
            AbstractDatabase ad = Main.databases.register(name,null);
            if(ad==null)JOptionPane.showMessageDialog(parentFrame, "数据库创建失败，请检查数据库名是否有效！", "错误！", JOptionPane.ERROR_MESSAGE);
            else {
                try {
                    ad.createConnection();
                    createDatabaseNode(ad,parentFrame);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(parentFrame, "数据库创建失败，请检查数据库名是否有效！", "错误！", JOptionPane.ERROR_MESSAGE);
                    Main.databases.remove(name,null);
                }
            }
        });
    }
    static void registerOpenDatabaseFile(AbstractButton i,MainPage parentFrame){
        i.addActionListener(e->{
            JFileChooser fc = new JFileChooser("./databases");
            fc.showOpenDialog(parentFrame);
            File f = fc.getSelectedFile();
            if(f==null)return;
            String fn = removeExtension(f.getName());
            System.out.println(f.getParent()+"\\"+fn);
            AbstractDatabase ad;
            if(f.getParent().equals(new File("./databases/").getPath())) ad = Main.databases.register(fn, new String[]{f.getParent()});
            else ad = Main.databases.register(fn, null);
            if(ad!=null) {
                try {
                    ad.createConnection();
                    createDatabaseNode(ad,parentFrame);
                    //parentFrame.putDataIntoTable(ad.getTableData("tables"));
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(parentFrame, "数据库打开失败，请检查是否是有效的数据库文件！", "错误！", JOptionPane.ERROR_MESSAGE);
                }
            }
            System.out.println(f.getParent()+File.separatorChar+f.getName());
        });
    }
    static void createDatabaseNode(AbstractDatabase ad, MainPage parentFrame){
        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(ad);
        parentFrame.lp.getTreeModel().insertNodeInto(newNode, parentFrame.lp.getRoot(), 0);
        DefaultMutableTreeNode subNode;
        for(String ts:ad.getTables()){
            subNode = new DefaultMutableTreeNode(ts);
            parentFrame.lp.getTreeModel().insertNodeInto(subNode, newNode, 0);
        }
    }
}
