package com.mateuszaksjonow;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextEditor extends JFrame {

    JTextArea editorArea = new JTextArea(30, 50);
    JTextField searchField = new JTextField(20);
    JPanel centerPanel;
    JPanel northPanel;
    JMenuBar menuBar;
    JMenu fileMenu;
    JMenu searchMenu;

    boolean regex = false;

    private List<Integer> matchStartIndices;
    private List<Integer> matchEndIndices;
    private int matchPosition;

    /*
     * Creates the user interface for the editor.
     */
    public TextEditor() {
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 300, 500);
        setTitle("Text Editor");

        initNorthPanelAndSouthPanel();
        initMenuBar();

        setLocationRelativeTo(null);
        setVisible(true);
        pack();
    }

    /*
     * Initializes elements of editor
     */
    private void initNorthPanelAndSouthPanel() {
        northPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        northPanel.setBorder(new TitledBorder(new EtchedBorder(), ""));

        initSaveButton();
        initOpenButton();
        initSearchField();
        initSearchButton();
        initPreviousButton();
        initNextButton();
        initRegexCheckbox();

        centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerPanel.setBorder(new TitledBorder(new EtchedBorder(), ""));

        initTextAreaAndScrollPane();

        add(northPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void initMenuBar() {
        menuBar = new JMenuBar();
        menuBar.setName("MenuFile");
        setJMenuBar(menuBar);
        initSaveOpenMenuBar();
        initSearchMenuBar();
    }

    private void initSaveButton() {
        JButton saveButton = new JButton(urlImageReader("https://cdn2.iconfinder.com/data/icons/atrous/512/floppy_disk_save-24.png"));
        saveButton.setName("SaveButton");
        saveButton.addActionListener(actionEvent -> {
            initJFileChooserSavePath();
        });
        northPanel.add(saveButton, BorderLayout.NORTH);
    }

    private void initOpenButton() {
        JButton loadButton = new JButton(urlImageReader("https://cdn4.iconfinder.com/data/icons/common-toolbar/36/Open-24.png"));
        loadButton.setName("OpenButton");
        loadButton.addActionListener(actionEvent -> {
            initJFileChooserOpenPath();
        });
        northPanel.add(loadButton, BorderLayout.NORTH);
    }

    private void initSearchField() {
        searchField.setName("SearchField");
        northPanel.add(searchField, BorderLayout.NORTH);
    }

    private void initSearchButton() {
        JButton searchButton = new JButton(urlImageReader("https://cdn2.iconfinder.com/data/icons/atrous/512/search_magnifying_glass_find-24.png"));
        searchButton.setName("StartSearchButton");
        searchButton.addActionListener(actionEvent -> {
            search();
        });
        northPanel.add(searchButton, BorderLayout.NORTH);
    }

    private void initPreviousButton() {
        JButton nextButton = new JButton(urlImageReader("https://cdn3.iconfinder.com/data/icons/music-player-controls-3/100/arrow_back_backwards_repeat_previous_blue-24.png"));
        nextButton.setName("PreviousMatchButton");
        nextButton.addActionListener(actionEvent -> {
            decrementMatchAndMove();
        });
        northPanel.add(nextButton, BorderLayout.NORTH);
    }

    private void initNextButton() {
        JButton nextButton = new JButton(urlImageReader("https://cdn3.iconfinder.com/data/icons/music-player-controls-3/100/music_forward_front_next_arrow_blue-24.png"));
        nextButton.setName("NextMatchButton");
        nextButton.addActionListener(actionEvent -> {
            incrementMatchAndMove();
        });
        northPanel.add(nextButton, BorderLayout.NORTH);
    }

    private void initRegexCheckbox() {
        JCheckBox checkBox = new JCheckBox();
        checkBox.setName("UseRegExCheckbox");
        checkBox.setText("Use regex");
        checkBox.addActionListener(actionEvent -> {
            regex = !regex;
        });
        northPanel.add(checkBox, BorderLayout.NORTH);
    }

    private void initJFileChooserOpenPath() {
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jfc.setVisible(true);
        jfc.setDialogTitle("Choose a directory to open your file: ");
        jfc.setName("FileChooser");
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);

        if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            if (jfc.getSelectedFile().isFile()) {
                try {
                    File file = jfc.getSelectedFile();
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    editorArea.read(br, null);
                    editorArea.requestFocus();
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        add(jfc, BorderLayout.CENTER);
    }


    private void initJFileChooserSavePath() {
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jfc.setVisible(true);
        jfc.setDialogTitle("Choose a directory to save your file: ");
        jfc.setName("FileChooser");
        jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        if (jfc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            try (FileWriter fw = new FileWriter(jfc.getSelectedFile())) {
                fw.write(editorArea.getText());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        add(jfc, BorderLayout.CENTER);
    }


    private void initTextAreaAndScrollPane() {
        editorArea.setName("TextArea");
        centerPanel.add(editorArea, BorderLayout.CENTER);

        JScrollPane scroll = new JScrollPane(editorArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scroll.setName("ScrollPane");
        centerPanel.add(scroll);
    }

    private void initSaveOpenMenuBar() {
        fileMenu = new JMenu("File");
        fileMenu.setName("MenuFile");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(fileMenu);

        initSaveMenuItem();
        initLoadMenuItem();
        initExitMenuItem();
    }

    private void initSaveMenuItem() {
        JMenuItem saveMenuItem = new JMenuItem("Save");
        saveMenuItem.setName("MenuSave");
        saveMenuItem.addActionListener(actionEvent -> {
            initJFileChooserSavePath();
        });
        fileMenu.add(saveMenuItem);
    }

    private void initLoadMenuItem() {
        JMenuItem loadMenuItem = new JMenuItem("Load");
        loadMenuItem.setName("MenuOpen");
        loadMenuItem.addActionListener(actionEvent -> {
            initJFileChooserOpenPath();
        });
        fileMenu.add(loadMenuItem);
    }

    private void initExitMenuItem() {
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.setName("MenuExit");
        exitMenuItem.addActionListener(actionEvent -> {
            System.exit(0);
        });
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);
    }

    private void initSearchMenuBar() {
        searchMenu = new JMenu("Search");
        searchMenu.setName("MenuSearch");
        searchMenu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(searchMenu);

        initStartSearchItem();
        initNextMatchItem();
        initPreviousMatchItem();
        initUseRegularExpressionsItem();
    }

    private void initStartSearchItem() {
        JMenuItem saveMenuItem = new JMenuItem("Start Search");
        saveMenuItem.setName("MenuStartSearch");
        saveMenuItem.addActionListener(actionEvent -> {
            search();
        });
        searchMenu.add(saveMenuItem);
    }

    private void initPreviousMatchItem() {
        JMenuItem saveMenuItem = new JMenuItem("Previous Match");
        saveMenuItem.setName("MenuPreviousMatch");
        saveMenuItem.addActionListener(actionEvent -> {
            decrementMatchAndMove();
        });
        searchMenu.add(saveMenuItem);
    }

    private void initNextMatchItem() {
        JMenuItem saveMenuItem = new JMenuItem("Next Match");
        saveMenuItem.setName("MenuNextMatch");
        saveMenuItem.addActionListener(actionEvent -> {
            incrementMatchAndMove();
        });
        searchMenu.add(saveMenuItem);
    }

    private void initUseRegularExpressionsItem() {
        JMenuItem saveMenuItem = new JMenuItem("Use Regular Expressions");
        saveMenuItem.setName("MenuUseRegExp");
        saveMenuItem.addActionListener(actionEvent -> {
            regex = !regex;
        });
        searchMenu.add(saveMenuItem);
    }

    private void search() {
        new Thread(() -> {
            matchPosition = 0;
            matchStartIndices = new ArrayList<>();
            matchEndIndices = new ArrayList<>();

            Pattern pattern = Pattern.compile(String.format("(%s)", searchField.getText()));
            Matcher matcher = pattern.matcher(editorArea.getText());
            while (matcher.find()) {
                matchStartIndices.add(matcher.start());
                matchEndIndices.add(matcher.end());
            }

            moveMatch();
        }).start();
    }

    private void decrementMatchAndMove() {
        if (matchPosition - 1 < 0) {
            matchPosition = matchStartIndices.size() - 1;
        } else {
            matchPosition--;
        }
        moveMatch();
    }

    private void incrementMatchAndMove() {
        if (matchPosition + 1 > matchStartIndices.size() - 1) {
            matchPosition = 0;
        } else {
            matchPosition++;
        }
        moveMatch();
    }

    private void moveMatch() {
        editorArea.setCaretPosition(matchEndIndices.get(matchPosition));
        editorArea.select(matchStartIndices.get(matchPosition), matchEndIndices.get(matchPosition));
        editorArea.grabFocus();
    }

    //reads images from URL
    private ImageIcon urlImageReader(String urlImage) {
        try {
            URL url = new URL(urlImage);
            BufferedImage img = ImageIO.read(url);
            return new ImageIcon(img);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
