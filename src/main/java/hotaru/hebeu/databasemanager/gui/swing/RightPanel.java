package hotaru.hebeu.databasemanager.gui.swing;

import hotaru.hebeu.databasemanager.database.AbstractDatabase;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.Vector;

/**
 * 右部分页面，用于显示表格
 */
public class RightPanel extends JPanel {
    private JTable ttable;
    private final MainPage parentFrame;
    private AbstractDatabase ad;
    private String tableName;
    public RightPanel(MainPage parentFrame){
        this.parentFrame = parentFrame;
        GridBagLayout gbl=new GridBagLayout();
        GridBagConstraints gbc=new GridBagConstraints();
        setLayout(gbl);

        gbc.fill=GridBagConstraints.BOTH;
        gbc.weightx=1;
        gbc.weighty=1;
        ttable = new JTable();
        ttable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        add(new JScrollPane(ttable),gbc);
        setBackground(Color.BLACK);

        //右键菜单
        JPopupMenu tableMenu = new JPopupMenu();
        JMenuItem addColumnMI=new JMenuItem("新增列至末尾");
        JMenuItem addRowMI=new JMenuItem("新增行至末尾");
        JMenuItem deleteColumnMI=new JMenuItem("删除当前列");
        JMenuItem deleteRowMI=new JMenuItem("删除当前行");
        AbstractButtonListener.registerAddColumn(addColumnMI,parentFrame);
        tableMenu.add(addColumnMI);
        AbstractButtonListener.registerAddRow(addRowMI,parentFrame);
        tableMenu.add(addRowMI);
        AbstractButtonListener.registerDeleteColumn(deleteColumnMI,parentFrame);
        tableMenu.add(deleteColumnMI);
        AbstractButtonListener.registerDeleteRow(deleteRowMI,parentFrame);
        tableMenu.add(deleteRowMI);
        JPopupMenu simpleTableMenu = new JPopupMenu();
        //simpleTableMenu.add(addColumnMI);
        //simpleTableMenu.add(addRowMI);

        //右键菜单监听
        ttable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                super.mouseClicked(evt);
                if (evt.getButton() == java.awt.event.MouseEvent.BUTTON3) {
                    //通过点击位置找到点击为表格中的行
                    int rowIndex = ttable.rowAtPoint(evt.getPoint());
                    int columnIndex = ttable.columnAtPoint(evt.getPoint());
                    if (rowIndex == -1 || columnIndex==-1) {
                        simpleTableMenu.show(ttable, evt.getX(), evt.getY());
                        System.out.println("不知道按到哪里了反正按到了");
                    }
                    else {
                        ttable.changeSelection(rowIndex,columnIndex,false,false);
                        tableMenu.show(ttable,evt.getX(),evt.getY());
                    }
                }
            }
        });
    }

    /**
     * 使用数据库给表格给传入数据
     * 需要传入一个SELECT语句接口，虽然这样子会导致耦合性过高
     * @param sm 一个准备好SELECT的语句接口
     */
    void putData(PreparedStatement sm){
        try {
            ResultSet rs = sm.executeQuery();
            ResultSetMetaData rmd = rs.getMetaData();
            Vector<Vector<Object>> data = new Vector<>();
            Vector<Object> name = new Vector<>();
            int n = rmd.getColumnCount();
            for(int i=0;i<n;i++)name.add(rmd.getColumnName(i+1));
            while(rs.next()){
                Vector<Object> tepv = new Vector<>(n);
                for(int i=0;i<n;i++)tepv.add(rs.getObject(i+1));
                data.add(tepv);
            }
            DefaultTableModel d = new DefaultTableModel(data,name);
            d.addTableModelListener(e->{
                if(e.getType() == TableModelEvent.UPDATE){
                    String cname = d.getColumnName(e.getColumn());
                    int ri = e.getLastRow();
                    Object value = d.getValueAt(e.getLastRow(),e.getColumn());
                    if(value instanceof String && ((String)value).trim().equals(""))value=null;
                    try {
                        ad.editValue(tableName,cname,ri,value);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(parentFrame, "内容修改失败！详情请看错误报告！", "错误！", JOptionPane.ERROR_MESSAGE);
                    }finally {
                        flashTable();
                    }
                }
            });
            ttable.setModel(d);
            rs.close();
            sm.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 清空表格数据
     */
    void cleatTable(){
        ttable.setModel(new DefaultTableModel());
        ad=null;
        tableName=null;
    }

    /**
     * 设置当前所展示的数据库和数据表
     * @param ad 展示的数据库
     * @param tableName 展示的数据表
     */
    void setTableName(AbstractDatabase ad,String tableName){
        this.ad=ad;
        this.tableName=tableName;
    }
    AbstractDatabase getDatabaseInShow(){return ad;}
    String getTableNameInShow(){return tableName;}

    /**
     * 刷新数据
     */
    void flashTable(){putData(ad.getTableData(tableName));}
    JTable getJTable(){return ttable;}
}
