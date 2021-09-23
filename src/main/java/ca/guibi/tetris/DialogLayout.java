package ca.guibi.tetris;

import javax.swing.JPanel;
import javax.swing.OverlayLayout;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.awt.Component;


public class DialogLayout extends JPanel {
    DialogLayout(Component mainContent, Component dialog)
    {
        layout = new OverlayLayout(this);
        setLayout(layout);

        // Dialog
        add(dialog);
        dialog.setVisible(false);
        this.dialog = dialog;
        
        // No mouse panel
        add(noMousePanel);
        noMousePanel.setVisible(false);
        noMousePanel.setOpaque(false);
        noMousePanel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) { e.consume(); }
            @Override
            public void mousePressed(MouseEvent e) { e.consume(); }
            @Override
            public void mouseReleased(MouseEvent e) { e.consume(); }
            @Override
            public void mouseEntered(MouseEvent e) { e.consume(); }
            @Override
            public void mouseExited(MouseEvent e) { e.consume(); }
        });

        // Blur panel
        add(blurPanel);
        blurPanel.setVisible(false);
        blurPanel.setBackground(new Color(1, 1, 1, 0.6f));

        // Main content
        add(mainContent);
        mainContent.setVisible(true);
        this.mainContent = mainContent;
    }

    public void setDialogVisible(boolean visible)
    {
        noMousePanel.setVisible(visible);
        blurPanel.setVisible(visible);
        dialog.setVisible(visible);
    }

    public void setDialog(Component dialog)
    {
        remove(this.dialog);
        add(dialog, 0);
        this.dialog = dialog;
    }

    public Component getDialog()
    {
        return dialog;
    }

    public void setMainContent(Component mainContent)
    {
        remove(this.mainContent);
        add(mainContent, 3);
        this.mainContent = mainContent;
    }

    public Component getMainContent()
    {
        return mainContent;
    }


    private OverlayLayout layout;

    private Component mainContent;
    private JPanel noMousePanel = new JPanel();
    private JPanel blurPanel = new JPanel();
    private Component dialog;
}
