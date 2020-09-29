package com.mateuszaksjonow;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextEditor extends JFrame {

    JTextArea editorArea = new JTextArea(30, 50);
    JTextField searchField = new JTextField(20);

    String editorText;
    String searchText;

    int caretPosition = 0;
    int index = 0;
    boolean regex = false;

    /*
     * Creates the user interface for the editor.
     */
    public TextEditor() {
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 300, 500);
        setTitle("Text Editor");

        initNorthPanelAndSouthPanel();

        JMenuBar menuBar = new JMenuBar();
        menuBar.setName("MenuFile");
        setJMenuBar(menuBar);
        initSaveOpenMenuBar(menuBar);
        initSearchMenuBar(menuBar);

        setLocationRelativeTo(null);
        setVisible(true);
        pack();
    }

    /*
     * Initializes elements of editor
     */
    private void initNorthPanelAndSouthPanel() {
        JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        northPanel.setBorder(new TitledBorder(new EtchedBorder(), ""));

        initSaveButton(northPanel);
        initOpenButton(northPanel);
        initSearchField(northPanel);
        initSearchButton(northPanel);
        initPreviousButton(northPanel);
        initNextButton(northPanel);
        initRegexCheckbox(northPanel);

        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerPanel.setBorder(new TitledBorder(new EtchedBorder(), ""));

        initTextAreaAndScrollPane(centerPanel);

        add(northPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    private void initSaveButton(JPanel panel) {
        JButton saveButton = new JButton(urlImageReader("https://cdn2.iconfinder.com/data/icons/atrous/512/floppy_disk_save-24.png"));
        saveButton.setName("SaveButton");
        saveButton.addActionListener(actionEvent -> {
            initJFileChooserSavePath();
        });
        panel.add(saveButton, BorderLayout.NORTH);
    }

    private void initOpenButton(JPanel panel) {
        JButton loadButton = new JButton(urlImageReader("https://cdn4.iconfinder.com/data/icons/common-toolbar/36/Open-24.png"));
        loadButton.setName("OpenButton");
        loadButton.addActionListener(actionEvent -> {
            initJFileChooserOpenPath();
        });
        panel.add(loadButton, BorderLayout.NORTH);
    }

    private void initSearchField(JPanel panel) {
        searchField.setName("SearchField");
        panel.add(searchField, BorderLayout.NORTH);
    }

    private void initSearchButton(JPanel panel) {
        JButton searchButton = new JButton(urlImageReader("https://cdn2.iconfinder.com/data/icons/atrous/512/search_magnifying_glass_find-24.png"));
        searchButton.setName("StartSearchButton");
        searchButton.addActionListener(actionEvent -> {
            searchNext();
        });
        panel.add(searchButton, BorderLayout.NORTH);
    }

    private void initPreviousButton(JPanel panel) {
        JButton nextButton = new JButton(urlImageReader("https://cdn3.iconfinder.com/data/icons/music-player-controls-3/100/arrow_back_backwards_repeat_previous_blue-24.png"));
        nextButton.setName("PreviousMatchButton");
        nextButton.addActionListener(actionEvent -> {
            searchPrevious();
        });
        panel.add(nextButton, BorderLayout.NORTH);
    }

    private void initNextButton(JPanel panel) {
        JButton nextButton = new JButton(urlImageReader("https://cdn3.iconfinder.com/data/icons/music-player-controls-3/100/music_forward_front_next_arrow_blue-24.png"));
        nextButton.setName("NextMatchButton");
        nextButton.addActionListener(actionEvent -> {
            searchNext();
        });
        panel.add(nextButton, BorderLayout.NORTH);
    }

    private void initRegexCheckbox(JPanel panel) {
        JCheckBox checkBox = new JCheckBox();
        checkBox.setName("UseRegExCheckbox");
        checkBox.setText("Use regex");
        checkBox.addActionListener(actionEvent -> {
            regex = !regex;
        });
        panel.add(checkBox, BorderLayout.NORTH);
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


    private void initTextAreaAndScrollPane(JPanel panel) {
        editorArea.setName("TextArea");
        panel.add(editorArea, BorderLayout.CENTER);

        JScrollPane scroll = new JScrollPane(editorArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scroll.setName("ScrollPane");
        panel.add(scroll);
    }

    private void initSaveOpenMenuBar(JMenuBar menuBar) {
        JMenu fileMenu = new JMenu("File");
        fileMenu.setName("MenuFile");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(fileMenu);

        initSaveMenuItem(fileMenu);
        initLoadMenuItem(fileMenu);
        initExitMenuItem(fileMenu);
    }

    private void initSaveMenuItem(JMenu menu) {
        JMenuItem saveMenuItem = new JMenuItem("Save");
        saveMenuItem.setName("MenuSave");
        saveMenuItem.addActionListener(actionEvent -> {
            initJFileChooserSavePath();
        });
        menu.add(saveMenuItem);
    }

    private void initLoadMenuItem(JMenu menu) {
        JMenuItem loadMenuItem = new JMenuItem("Load");
        loadMenuItem.setName("MenuOpen");
        loadMenuItem.addActionListener(actionEvent -> {
            initJFileChooserOpenPath();
        });
        menu.add(loadMenuItem);
    }

    private void initExitMenuItem(JMenu menu) {
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.setName("MenuExit");
        exitMenuItem.addActionListener(actionEvent -> {
            System.exit(0);
        });
        menu.addSeparator();
        menu.add(exitMenuItem);
    }

    private void initSearchMenuBar(JMenuBar menuBar) {
        JMenu fileMenu = new JMenu("Search");
        fileMenu.setName("MenuSearch");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(fileMenu);

        initStartSearchItem(fileMenu);
        initNextMatchItem(fileMenu);
        initPreviousMatchItem(fileMenu);
        initUseRegularExpressionsItem(fileMenu);
    }

    private void initStartSearchItem(JMenu menu) {
        JMenuItem saveMenuItem = new JMenuItem("Start Search");
        saveMenuItem.setName("MenuStartSearch");
        saveMenuItem.addActionListener(actionEvent -> {
            searchNext();
        });
        menu.add(saveMenuItem);
    }

    private void initPreviousMatchItem(JMenu menu) {
        JMenuItem saveMenuItem = new JMenuItem("Previous Match");
        saveMenuItem.setName("MenuPreviousMatch");
        saveMenuItem.addActionListener(actionEvent -> {
            searchPrevious();
        });
        menu.add(saveMenuItem);
    }

    private void initNextMatchItem(JMenu menu) {
        JMenuItem saveMenuItem = new JMenuItem("Next Match");
        saveMenuItem.setName("MenuNextMatch");
        saveMenuItem.addActionListener(actionEvent -> {
            searchNext();
        });
        menu.add(saveMenuItem);
    }

    private void initUseRegularExpressionsItem(JMenu menu) {
        JMenuItem saveMenuItem = new JMenuItem("Use Regular Expressions");
        saveMenuItem.setName("MenuUseRegExp");
        saveMenuItem.addActionListener(actionEvent -> {
            regex = !regex;
        });
        menu.add(saveMenuItem);
    }

    //searches for next and resets caret position if reaches end of the file
    private void searchNext() {
        editorText = editorArea.getText();
        searchText = searchField.getText();

        if (regex) {
            searchWithRegex(true);
        }else {
            index = editorText.indexOf(searchText, caretPosition);

            if (index == -1) {
                caretPosition = 0;
                index = editorText.indexOf(searchText, caretPosition);
            }
            setCaretPositionAndSelectText(index, searchText);
        }
    }

    //searches for next and resets caret position if reaches end of the file
    private void searchPrevious() {
        editorText = editorArea.getText();
        searchText = searchField.getText();

        if (regex) {
            searchWithRegex(false);
        }else {
            index = editorText.lastIndexOf(searchText, caretPosition - searchText.length() - 1);

            if (index == -1) {
                caretPosition = editorText.length() + 1;
                index = editorText.lastIndexOf(searchText, caretPosition - searchText.length() - 1);
            }
            setCaretPositionAndSelectText(index, searchText);
        }
    }

    //searches for parts compatible with regex
    private void searchWithRegex(boolean isNext) {
        editorText = editorArea.getText();
        searchText = searchField.getText();

        Pattern pattern = Pattern.compile(searchText);
        Matcher matcher = pattern.matcher(editorText);
        index = indexOfRegex(Pattern.compile(searchText), editorText, caretPosition, isNext);

        if(!matcher.find()){
            caretPosition = 0;
            index = indexOfRegex(Pattern.compile(searchText), editorText, caretPosition, isNext);
        }

        caretPosition = index + matcher.end();
        editorArea.setCaretPosition(caretPosition);
        editorArea.select(index, caretPosition);
        editorArea.grabFocus();
    }

    //return index of regex
    private int indexOfRegex(Pattern pattern, String editorText, int caretPosition, boolean isNext) {
        editorText = editorArea.getText();
        searchText = searchField.getText();

        if (isNext) {
            Matcher matcher = pattern.matcher(editorText);
            return matcher.find(caretPosition) ? matcher.start() : -1;
        }else {
            Matcher matcher = pattern.matcher(editorText);
            String match = matcher.group(0);
            index = editorText.lastIndexOf(match, caretPosition - match.length() - 1);

            if (index == -1) {
                caretPosition = editorText.length() + 1;
                index = editorText.lastIndexOf(match, caretPosition - match.length() - 1);
            }
            return index;
        }
    }

    private void setCaretPositionAndSelectText(int index, String searchText) {
        caretPosition = index + searchText.length();
        editorArea.setCaretPosition(caretPosition);
        editorArea.select(index, caretPosition);
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
