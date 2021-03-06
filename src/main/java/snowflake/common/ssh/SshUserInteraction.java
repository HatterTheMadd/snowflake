package snowflake.common.ssh;

import snowflake.components.common.ModalGlassPanel;
import snowflake.components.newsession.SessionInfo;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.atomic.*;

public class SshUserInteraction extends AbstractUserInteraction {
    private JRootPane rootPane;

    public SshUserInteraction(SessionInfo info, JRootPane rootPane) {
        super(info);
        this.rootPane = rootPane;
    }

    protected boolean showModal(List<JComponent> components, boolean yesNo) {
        JButton btnOk = new JButton("Yes");
        JButton btnCancel = null;
        final AtomicBoolean isOk = new AtomicBoolean(false);

        if (yesNo) {
            btnCancel = new JButton("No");
        }

        ModalGlassPanel modalGlassPanel = new ModalGlassPanel(btnOk, btnCancel, components);

        btnOk.addActionListener(e -> {
            synchronized (this) {
                modalGlassPanel.setVisible(false);
                isOk.set(true);
                this.notify();
            }
        });


        if (yesNo) {
            btnCancel.addActionListener(e -> {
                synchronized (this) {
                    modalGlassPanel.setVisible(false);
                    isOk.set(false);
                    this.notify();
                }
            });
        }

        SwingUtilities.invokeLater(() -> {
            System.out.println("Root pane: " + rootPane);
            rootPane.getGlassPane().setVisible(false);
            rootPane.setGlassPane(modalGlassPanel);
            modalGlassPanel.setVisible(true);
            System.out.println("Prompt made visible");
        });

        synchronized (this) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return isOk.get();
    }
}
