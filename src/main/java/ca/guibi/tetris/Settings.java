package ca.guibi.tetris;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;

import java.util.Properties;

import javax.swing.GroupLayout;

import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class Settings extends StyledPanel
{
    Settings(Window window)
    {
        this.window = window;

        // Open config file
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


        // GUI
        pauseSetting = new SettingButtons("Pause", KeyEvent.VK_P);
        leftSetting = new SettingButtons("Left", KeyEvent.VK_LEFT);
        rightSetting = new SettingButtons("Right", KeyEvent.VK_RIGHT);
        rotateSetting = new SettingButtons("Rotate", KeyEvent.VK_SPACE);
        holdSetting = new SettingButtons("Hold", KeyEvent.VK_UP);
        fallSetting = new SettingButtons("Fall", KeyEvent.VK_DOWN);
        
        StyledLabel titleLabel = new StyledLabel("Settings", 80f);
        
        StyledButton cancelButton = new StyledButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                loadSettings();
                window.showMenu();
            }
        });

        StyledButton saveButton = new StyledButton("Save");
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                saveSettings();
                window.showMenu();
            }
        });

        // Layout
        StyledPanel centeredPanel = new StyledPanel();
        GroupLayout layout = new GroupLayout(centeredPanel);
        centeredPanel.setLayout(layout);
        setLayout(new GridBagLayout());
        add(centeredPanel);
        
        layout.setAutoCreateGaps(true);

        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(titleLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(40)
                .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addComponent(pauseSetting)
                        .addComponent(leftSetting)
                        .addComponent(rightSetting)
                        .addComponent(rotateSetting)
                        .addComponent(holdSetting)
                        .addComponent(fallSetting)
                    )
                )
                .addGap(20)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(cancelButton, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(saveButton, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                )
        );
        
        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addComponent(titleLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(40)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pauseSetting)
                        .addComponent(leftSetting)
                        .addComponent(rightSetting)
                        .addComponent(rotateSetting)
                        .addComponent(holdSetting)
                        .addComponent(fallSetting)
                    )
                )
                .addGap(20)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(cancelButton, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(saveButton, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                )
        );

        loadSettings();
    }

    private void saveSettings()
    {
        pauseSetting.save();
        leftSetting.save();
        rightSetting.save();
        rotateSetting.save();
        holdSetting.save();
        fallSetting.save();
        
        try {
            properties.store(new FileWriter(configFile), "Settings");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadSettings()
    {
        pauseSetting.load();
        leftSetting.load();
        rightSetting.load();
        rotateSetting.load();
        holdSetting.load();
        fallSetting.load();
    }


    private Window window;

    private File configFile = new File("config.properties");
    private Properties properties = new Properties();

    public SettingButtons currentlyEditingSetting;

    public SettingButtons pauseSetting;
    public SettingButtons leftSetting;
    public SettingButtons rightSetting;
    public SettingButtons rotateSetting;
    public SettingButtons holdSetting;
    public SettingButtons fallSetting;


    class SettingButtons extends StyledPanel
    {
        SettingButtons(String name, int defaultValue)
        {
            this.name = name;
            this.defaultValue = defaultValue;

            // GUI
            StyledLabel nameLabel = new StyledLabel(name);

            valueButton = new StyledButton("ERROR");
            valueButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    currentlyEditingSetting = SettingButtons.this;
                    window.showKeySelector();
                }
            });
            
            StyledButton resetButton = new StyledButton("Reset");
            resetButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    setValue(defaultValue);
                }
            });
            
            
            GroupLayout layout = new GroupLayout(this);
            layout.setAutoCreateGaps(true);
            setLayout(layout);

            layout.setHorizontalGroup(
                layout.createSequentialGroup()
                    .addComponent(nameLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addGap(40)
                    .addComponent(valueButton, GroupLayout.DEFAULT_SIZE, 175, 175)
                    .addComponent(resetButton, GroupLayout.DEFAULT_SIZE, 175, 175)
            );
                
            layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(nameLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(valueButton, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(resetButton, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
            );
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
            valueButton.setText(KeyEvent.getKeyText(value));
        }

        public int getValue()
        {
            return value;
        }
        

        private final String name;
        private final int defaultValue;
        private int value;

        private final StyledButton valueButton;
    }
}
