package hotaru.hebeu.databasemanager.gui.swing;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 左部分页面，用于显示层次树
 */
public class LeftPanel extends JPanel {
    private final JTree sqltree;
    private final DefaultMutableTreeNode root;
    private final MainPage parentFrame;
    public LeftPanel(MainPage parentFrame){
        this.parentFrame = parentFrame;
        GridBagLayout gbl=new GridBagLayout();
        GridBagConstraints gbc=new GridBagConstraints();
        setLayout(gbl);

        root = new DefaultMutableTreeNode("数据库");
        sqltree = new JTree(root);

        gbc.fill=GridBagConstraints.BOTH;
        gbc.weightx=1;
        gbc.weighty=1;
        add(new JScrollPane(sqltree),gbc);

        //对数据库的右键菜单
        JPopupMenu dbMenu = new JPopupMenu();
        JMenuItem creaTableMI=new JMenuItem("新建表");
        AbstractButtonListener.registerCreateTable(creaTableMI,this.parentFrame);
        JMenuItem deleteDBMI=new JMenuItem("删除库");
        AbstractButtonListener.registerDeleteDatabase(deleteDBMI,this.parentFrame);
        dbMenu.add(creaTableMI);
        dbMenu.add(deleteDBMI);
        //对数据表的右键菜单
        JPopupMenu tableMenu = new JPopupMenu();
        JMenuItem openTableMI=new JMenuItem("打开表");
        AbstractButtonListener.registerOpenTable(openTableMI,this.parentFrame);
        JMenuItem deleteTableMI=new JMenuItem("删除表");
        AbstractButtonListener.registerDeleteTable(deleteTableMI,this.parentFrame);
        tableMenu.add(openTableMI);
        tableMenu.add(deleteTableMI);

        sqltree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int x = e.getX();
                int y = e.getY();
                if(e.getButton()==MouseEvent.BUTTON3){
                    TreePath pathForLocation = sqltree.getPathForLocation(x, y);
                    if(pathForLocation==null)return;
                    sqltree.setSelectionPath(pathForLocation);
                    if(pathForLocation.getPath().length==3)
                        tableMenu.show(sqltree, x, y);
                    else if(pathForLocation.getPath().length==2)dbMenu.show(sqltree, x, y);
                }
            }
        });
        //sqltree.setCellRenderer(new TreeIcon());
    }

    DefaultMutableTreeNode getRoot(){return root;}
    DefaultTreeModel getTreeModel(){return (DefaultTreeModel)sqltree.getModel();}
    JTree getTree(){return sqltree;}
}
