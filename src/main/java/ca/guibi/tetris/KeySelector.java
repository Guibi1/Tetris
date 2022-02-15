package ca.guibi.tetris;

import javax.swing.GroupLayout;

import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


class KeySelector extends StyledPanel implements KeyListener
{
    KeySelector(Window window)
    {
        this.window = window;
        setFocusable(true);
        addKeyListener(this);

        // GUI
        StyledLabel label = new StyledLabel("Press a key to continue");

        StyledButton cancelButton = new StyledButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                window.showSettings();
            }
        });

        // Layout
        StyledPanel centeredPanel = new StyledPanel();
        GroupLayout layout = new GroupLayout(centeredPanel);
        centeredPanel.setLayout(layout);
        setLayout(new GridBagLayout());
        add(centeredPanel);

        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(label, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .addComponent(cancelButton, GroupLayout.DEFAULT_SIZE, 175, 175)
        );
            
        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addComponent(label, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(20)
                .addComponent(cancelButton, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
        );
    }

    private void handleValidKey(int keyCode)
    {
        window.getSettings().currentlyEditingSetting.setValue(keyCode);
        window.showSettings();
    }
    
    @Override
    public void keyReleased(KeyEvent e)
    {
        // Handle letters and digits
        if (Character.isLetterOrDigit(e.getKeyCode()))
            handleValidKey(e.getKeyCode());
            
        // Handle arrow keys and space
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_SPACE:
                handleValidKey(e.getKeyCode());
        }

        e.consume();
    }

    @Override
    public void keyPressed(KeyEvent e) { return; }
    
    @Override
    public void keyTyped(KeyEvent e) { return; }


    private Window window;
}
