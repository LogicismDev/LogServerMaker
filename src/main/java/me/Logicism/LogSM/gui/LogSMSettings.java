package me.Logicism.LogSM.gui;

import me.Logicism.LogSM.LogSM;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class LogSMSettings extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox comboBox1;
    private JComboBox comboBox2;
    private JCheckBox checkForUpdatesOnCheckBox;
    private JButton checkForUpdatesButton;

    private LookAndFeel lookAndFeel;

    public LogSMSettings() {
        try {
            setIconImage(ImageIO.read(LogSM.class.getClassLoader().getResourceAsStream("icon.png")));
        } catch (IOException ignored) {

        }

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setTitle("LogSM");

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        comboBox1.setRenderer(new ComboBoxRenderer());

        DefaultComboBoxModel<UIManager.LookAndFeelInfo> dcbm = new DefaultComboBoxModel<>(UIManager.getInstalledLookAndFeels());
        comboBox1.setModel(dcbm);

        for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            if (info.getClassName().equals(LogSM.getSettings().get("Theme"))) {
                comboBox1.setSelectedItem(info);
            }
        }

        if (LogSM.getSettings().containsKey("Port Forwarding Library")) {
            if (LogSM.getSettings().get("Port Forwarding Library").equals("Cling")) {
                comboBox2.setSelectedItem("jUPnP");
            } else {
                comboBox2.setSelectedItem(LogSM.getSettings().get("Port Forwarding Library"));
            }
        }

        if (Boolean.parseBoolean(LogSM.getSettings().get("Check for Updates"))) {
            checkForUpdatesOnCheckBox.setSelected(true);
        }

        lookAndFeel = UIManager.getLookAndFeel();

        checkForUpdatesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String updatedVersion = IOUtils.toString(new URL("https://raw.githubusercontent.com/LogicismDev/LogServerMaker/main/version"), StandardCharsets.UTF_8);

                    if (Double.parseDouble(updatedVersion) > Double.parseDouble(LogSM.VERSION)) {
                        UpdateDialog updateDialog = new UpdateDialog();
                        updateDialog.pack();
                        updateDialog.setLocationRelativeTo(null);
                        updateDialog.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(null, "You are currently on the latest version!", "LogSM", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    private void onOK() {
        try {
            UIManager.setLookAndFeel(((UIManager.LookAndFeelInfo) comboBox1.getSelectedItem()).getClassName());
            for (Window w : Window.getWindows()) {
                SwingUtilities.updateComponentTreeUI(w);
            }
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException e) {

        }
        LogSM.getSettings().put("Theme", ((UIManager.LookAndFeelInfo) comboBox1.getSelectedItem()).getClassName());
        LogSM.getSettings().put("Port Forwarding Library", String.valueOf(comboBox2.getSelectedItem()));
        LogSM.getSettings().put("Check for Updates", String.valueOf(checkForUpdatesOnCheckBox.isSelected()));
        try {
            LogSM.saveSettings();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Can't Save Settings! " + e.getMessage(), "LogSM", JOptionPane.ERROR_MESSAGE);
        }
        dispose();
    }

    private void onCancel() {
        try {
            UIManager.setLookAndFeel(lookAndFeel);
            for (Window w : Window.getWindows()) {
                SwingUtilities.updateComponentTreeUI(w);
            }
        } catch (UnsupportedLookAndFeelException e1) {

        }
        dispose();
    }
}

class ComboBoxRenderer extends JLabel implements ListCellRenderer<UIManager.LookAndFeelInfo> {

    public ComboBoxRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends UIManager.LookAndFeelInfo> list, UIManager.LookAndFeelInfo value, int index, boolean isSelected, boolean cellHasFocus) {
        setText(value.getName());

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        return this;
    }
}
