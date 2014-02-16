package Server;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

class ServerMainFrame extends JFrame{
    private JButton cleanConsoleBtn;
    private JButton resetStatisticsBtn;
    private JTextAreaHandler jTextAreaHandler;
    private JTextArea textArea;

    public ServerMainFrame(final Statistics statistics,final Server server){
        super("Server");

        this.addWindowListener(new WindowAdapter() {//override default close routine
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                ServerLogger.getInstance().info("Closing the server");
                server.close();
            }
        });


        cleanConsoleBtn= new JButton("Clean Console");
        resetStatisticsBtn = new JButton("Reset statistics");

        cleanConsoleBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textArea.setText("");
            }
        });
        resetStatisticsBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                statistics.resetStatistics();
            }
        });

        textArea= new JTextArea(40, 60);
        textArea.setLineWrap(true);
        textArea.setEditable(false);

        jTextAreaHandler= new JTextAreaHandler(textArea);

        JPanel buttonsPanel= new JPanel(new FlowLayout());
        buttonsPanel.add(cleanConsoleBtn);
        buttonsPanel.add(resetStatisticsBtn);

        JScrollPane jScrollPane= new JScrollPane(textArea);
        jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        DefaultCaret caret = (DefaultCaret)textArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        add(buttonsPanel, BorderLayout.NORTH);
        add(jScrollPane, BorderLayout.SOUTH);

        this.pack();
        this.setBounds(700, 300, this.getWidth(), this.getHeight());
        this.setResizable(false);
    }

    public JTextAreaHandler getJTextAreaHandler(){
        return jTextAreaHandler;
    }
}
