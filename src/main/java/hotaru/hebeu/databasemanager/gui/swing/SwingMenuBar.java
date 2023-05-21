package hotaru.hebeu.databasemanager.gui.swing;

import javax.swing.*;

public class SwingMenuBar extends JMenuBar {
    private JMenu[] jms;
    private MainPage parentFrame;
    public SwingMenuBar(MainPage parentFrame){
        this.parentFrame=parentFrame;
        init();
    }
    private void init(){
        jms = new JMenu[1];
        jms[0] = new JMenu("文件");
        JMenuItem creaMI = new JMenuItem("新建...");
        AbstractButtonListener.registerCreateDatabase(creaMI,parentFrame);
        jms[0].add(creaMI);
        JMenuItem openMI = new JMenuItem("打开...");
        AbstractButtonListener.registerOpenDatabaseFile(openMI,parentFrame);
        jms[0].add(openMI);
        for(int i=0;i<jms.length;i++)add(jms[0]);
    }
}
