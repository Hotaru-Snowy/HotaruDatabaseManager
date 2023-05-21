package hotaru.hebeu.databasemanager.gui.swing;

import hotaru.hebeu.databasemanager.database.AbstractDatabase;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class TreeIcon extends DefaultTreeCellRenderer {
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        if(value==null)return null;
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
        JLabel l  = new JLabel();
        if (selected) {
            l.setOpaque(false);//设置该组件边缘像素不可编辑
            l.setForeground(getTextSelectionColor());
            l.setBackground(getBackgroundSelectionColor());
        } else {
            l.setOpaque(true);
            l.setForeground(getTextNonSelectionColor());
            l.setBackground(getBackgroundNonSelectionColor());
        }

        if(node.getParent()==null){
            setClosedIcon(ResourceLib.getRootImageIcon());
            setOpenIcon(ResourceLib.getRootImageIcon());
            l.setIcon(ResourceLib.getRootImageIcon());
        }else if(node.getUserObject()!=null && node.getUserObject() instanceof AbstractDatabase){
            setClosedIcon(ResourceLib.getDatabaseImageIcon());
            setOpenIcon(ResourceLib.getDatabaseImageIcon());
            l.setIcon(ResourceLib.getDatabaseImageIcon());
        }else{
            setClosedIcon(ResourceLib.getTableImageIcon());
            setOpenIcon(ResourceLib.getTableImageIcon());
            l.setIcon(ResourceLib.getTableImageIcon());
        }
        l.setText(node.toString());
        return l;
    }
}