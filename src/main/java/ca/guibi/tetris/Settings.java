package ca.guibi.tetris;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;

import java.util.ArrayList;
import java.util.Properties;

import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.awt.event.ActionListener;


public class Settings extends StyledPanel {
    Settings(Window window)
    {
        try {
            properties = new Properties();

            if (!configFile.exists())
                configFile.createNewFile();

            properties.load(new FileReader(configFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create elements
        elements.add(keyPause);
        elements.add(keyLeft);
        elements.add(keyRight);
        elements.add(keyRotate);
        elements.add(keyHold);
        elements.add(keyHardFall);
        elements.add(keySoftFall);

        // GUI
        titleLabel = new JLabel("Settings");
        FontManager.setComponentFont(titleLabel, 80f);

        cancelButton = new StyledButton("Cancel");
        FontManager.setComponentFont(cancelButton);
        cancelButton.setPreferredSize(new Dimension(200, 0));
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                resetGUIsettings();
                window.showMenu();
            }
        });

        saveButton = new StyledButton("Save");
        FontManager.setComponentFont(saveButton);
        saveButton.setPreferredSize(new Dimension(200, 0));
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                applyGUIsettings();
                window.showMenu();
            }
        });

        // Layout
        modifyShortcutPanel = new ModifyShortcutDialog();
        
        StyledPanel mainContentPanel = new StyledPanel();
        GroupLayout mainContentLayout = new GroupLayout(mainContentPanel);
        mainContentPanel.setLayout(mainContentLayout);

        mainContentLayout.setAutoCreateGaps(true);
        mainContentLayout.setAutoCreateContainerGaps(true);

        mainContentLayout.setHorizontalGroup(
            mainContentLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(titleLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(40)
                .addComponent(keyPause)
                .addComponent(keyLeft)
                .addComponent(keyRight)
                .addComponent(keyRotate)
                .addComponent(keyHold)
                .addComponent(keyHardFall)
                .addComponent(keySoftFall)
                .addGap(20)
                .addGroup(mainContentLayout.createSequentialGroup()
                    .addComponent(cancelButton, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(saveButton, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                )
        );
        
        mainContentLayout.setVerticalGroup(
            mainContentLayout.createSequentialGroup()
                .addComponent(titleLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(40)
                .addComponent(keyPause)
                .addComponent(keyLeft)
                .addComponent(keyRight)
                .addComponent(keyRotate)
                .addComponent(keyHold)
                .addComponent(keyHardFall)
                .addComponent(keySoftFall)
                .addGap(20)
                .addGroup(mainContentLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(cancelButton, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(saveButton, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                )
        );

        layout = new DialogLayout(mainContentPanel, modifyShortcutPanel);
        add(layout);

        loadSettings();
    }

    private void applyGUIsettings()
    {
        saveSettings();
    }

    private void resetGUIsettings()
    {
        loadSettings();
    }

    private void saveSettings()
    {
        for (SettingsLine se : elements)
            se.save();
        
        try {
            properties.store(new FileWriter(configFile), "Settings");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadSettings()
    {
        for (SettingsLine se : elements)
            se.load();
    }


    private File configFile = new File("config.properties");
    private Properties properties = new Properties();
    private ArrayList<SettingsLine> elements = new ArrayList<SettingsLine>();
    
    private JLabel titleLabel;
    private StyledButton saveButton;
    private StyledButton cancelButton;

    private DialogLayout layout;
    private ModifyShortcutDialog modifyShortcutPanel;

    public SettingsLine keyPause = new SettingsLine("Pause", KeyEvent.VK_P);
    public SettingsLine keyLeft = new SettingsLine("Left", KeyEvent.VK_LEFT);
    public SettingsLine keyRight = new SettingsLine("Right", KeyEvent.VK_RIGHT);
    public SettingsLine keyRotate = new SettingsLine("Rotate", KeyEvent.VK_SPACE);
    public SettingsLine keyHold = new SettingsLine("Hold", KeyEvent.VK_UP);
    public SettingsLine keyHardFall = new SettingsLine("Hard Fall", KeyEvent.VK_SHIFT);
    public SettingsLine keySoftFall = new SettingsLine("Soft Fall", KeyEvent.VK_DOWN);


    private class ModifyShortcutDialog extends StyledPanel implements KeyListener {
        ModifyShortcutDialog()
        {
            addKeyListener(this);

            setOpaque(false);
            setLayout(new GridBagLayout());

            StyledPanel mainPanel = new StyledPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            add(mainPanel);

            textLabel = new JLabel("Enter a key.");
            textLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
            FontManager.setComponentFont(textLabel);
            mainPanel.add(textLabel);

            cancelButton = new StyledButton("Cancel");
            FontManager.setComponentFont(cancelButton);
            cancelButton.setAlignmentX(StyledButton.CENTER_ALIGNMENT);
            cancelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    currentShortcut = null;
                    layout.setDialogVisible(false);
                }
            });
            mainPanel.add(cancelButton);
        }

        public void modifyShortcut(SettingsLine shortcut)
        {
            currentShortcut = shortcut;
            layout.setDialogVisible(true);
            requestFocusInWindow();
        }
        
        @Override
        public void keyPressed(KeyEvent e)
        {
            if (currentShortcut != null)
            {
                if (e.getKeyCode() != KeyEvent.VK_ESCAPE)
                    currentShortcut.setValue(e.getKeyCode());
                
                else
                    currentShortcut = null;

                layout.setDialogVisible(false);
                e.consume();
            }
        }

        @Override
        public void keyReleased(KeyEvent e) { return; }
        
        @Override
        public void keyTyped(KeyEvent e) { return; }
        

        private SettingsLine currentShortcut = null;

        private JLabel textLabel;
        private StyledButton cancelButton;
    }


    public class SettingsLine extends StyledPanel {
        SettingsLine(String name, int defaultValue)
        {
            this.defaultValue = defaultValue;
            this.name = name;

            // GUI
            nameLabel = new JLabel(name);
            FontManager.setComponentFont(nameLabel);

            modifyButton = new StyledButton("Shortcut here");
            FontManager.setComponentFont(modifyButton);
            modifyButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    modifyShortcutPanel.modifyShortcut(SettingsLine.this);
                }
            });

            resetButton = new StyledButton("Reset");
            FontManager.setComponentFont(resetButton);
            resetButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    resetValue();
                }
            });

            // Layout
            GroupLayout layout = new GroupLayout(this);
            layout.setAutoCreateGaps(true);
            setLayout(layout);

            layout.setHorizontalGroup(
                layout.createSequentialGroup()
                    .addComponent(nameLabel)
                    .addGap(40)
                    .addComponent(modifyButton)
                    .addComponent(resetButton)
            );

            layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(nameLabel)
                    .addComponent(modifyButton)
                    .addComponent(resetButton)
            );
        }

        public void resetValue()
        {
            setValue(defaultValue);
        }

        public void save()
        {
            properties.setProperty(name, Integer.toString(value));
        }

        public void load()
        {
            setValue(Integer.valueOf(properties.getProperty(name, Integer.toString(defaultValue))));
        }

        public void setValue(int value)
        {
            this.value = value;
            modifyButton.setText(KeyEvent.getKeyText(value));
        }

        public int getValue()
        {
            return value;
        }
        

        private final String name;
        private final int defaultValue;
        private int value;

        private JLabel nameLabel;
        private StyledButton modifyButton;
        private StyledButton resetButton;
    }
}
