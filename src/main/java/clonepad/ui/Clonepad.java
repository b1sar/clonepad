package clonepad.ui;


import clonepad.background.SearchWorker;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class Clonepad extends JFrame {
    private JTextArea contentArea;
    private JTextField searhField;
    private JButton saveButton;
    private JButton loadButton;
    private JCheckBox useRegexCheckBox;

    private JFileChooser fileChooser;

    private List<SearchWorker.MatchIndexes> results = new ArrayList<>();
    private ListIterator<SearchWorker.MatchIndexes> resultsIterator = null;
    private Integer currentElementIndex;
    private ClassLoader classLoader = getClass().getClassLoader();

    public Clonepad() {
        super("Clonepad");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setSize(700, 600);
        fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        add(fileChooser);
        initComponents();
        setVisible(true);

    }

    private void initComponents() {

        /***************/
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        fileMenu.setName("MenuFile");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        JMenuItem loadMenuItem = new JMenuItem("Load");
        loadMenuItem.setIcon(new ImageIcon(classLoader.getResource("icons/load-minik.png")));
        loadMenuItem.setName("MenuOpen");
        loadMenuItem.addActionListener(e -> openFileChooser(false));
        JMenuItem saveMenuItem = new JMenuItem("Save");
        saveMenuItem.setIcon(new ImageIcon(classLoader.getResource("icons/save-minik.png")));
        saveMenuItem.setName("MenuSave");
        saveMenuItem.addActionListener(e -> openFileChooser(true));
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.setIcon(new ImageIcon(classLoader.getResource("icons/exit-minik.png")));
        exitMenuItem.setName("MenuExit");
        exitMenuItem.addActionListener(e -> System.exit(0));

        fileMenu.add(loadMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);

        /** Search menus**/
        JMenu searchMenu = new JMenu("Search");
        searchMenu.setName("MenuSearch");

        JMenuItem menuStartSearchItem = new JMenuItem("Start search");
        menuStartSearchItem.setName("MenuStartSearch");
        menuStartSearchItem.addActionListener(onSearchAction());

        JMenuItem MenuPreviousMatch = new JMenuItem("Previous Match");
        MenuPreviousMatch.setName("MenuPreviousMatch");
        MenuPreviousMatch.addActionListener(e -> onPreviousButtonClicked());

        JMenuItem nextMatchItem = new JMenuItem("Next Match");
        nextMatchItem.setName("MenuNextMatch");
        nextMatchItem.addActionListener(e -> onNextButtonClicked());

        JMenuItem regexItem = new JMenuItem("Use Regex");
        regexItem.setName("MenuUseRegExp");
        regexItem.addActionListener(e -> {
            if (useRegexCheckBox.isSelected()) {
                useRegexCheckBox.setSelected(false);
            } else {
                useRegexCheckBox.setSelected(true);
            }
        });

        searchMenu.add(menuStartSearchItem);
        searchMenu.add(MenuPreviousMatch);
        searchMenu.add(nextMatchItem);
        searchMenu.add(regexItem);


        menuBar.add(fileMenu);
        menuBar.add(searchMenu);
        setJMenuBar(menuBar);
        /***************/

        //Text content area
        JPanel contentPanel = new JPanel();
        contentPanel.setBorder(new EmptyBorder(5, 10, 10, 10));
        contentPanel.setLayout(new BorderLayout());

        contentArea = new JTextArea(20,20);
        contentArea.setName("TextArea");
        JScrollPane scrollPane = new JScrollPane(contentArea);
        scrollPane.setName("ScrollPane");
        scrollPane.setVisible(true);

        contentPanel.add(scrollPane, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);

        //Top Panel
        JPanel topPanel = new JPanel();
        FlowLayout flowLayout = new FlowLayout(FlowLayout.LEFT, 10, 5);
        topPanel.setLayout(flowLayout);

        saveButton = new JButton();
        saveButton.setName("SaveButton");
        saveButton.setIcon(new ImageIcon(classLoader.getResource("icons/save1-24px.png")));
        saveButton.setContentAreaFilled(false);
        saveButton.setFocusPainted(false);
        saveButton.setBorderPainted(false);
        saveButton.addActionListener(e -> openFileChooser(true));
        topPanel.add(saveButton);

        loadButton = new JButton();
        loadButton.setName("OpenButton");
        loadButton.setIcon(new ImageIcon(classLoader.getResource("icons/upload-24px.png")));
        loadButton.setContentAreaFilled(false);
        loadButton.setFocusPainted(false);
        loadButton.setBorderPainted(false);
        loadButton.addActionListener(e -> openFileChooser(false));
        topPanel.add(loadButton);

        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(flowLayout);

        searhField = new JTextField();
        searhField.setName("SearchField");
        searhField.setPreferredSize(new Dimension(200, 30));
        searchPanel.add(searhField);

        JButton startSearch = new JButton();
        startSearch.setName("StartSearchButton");
        startSearch.setIcon(new ImageIcon(classLoader.getResource("icons/search-24px.png")));
        startSearch.setContentAreaFilled(false);
        startSearch.setFocusPainted(false);
        startSearch.setBorderPainted(false);
        startSearch.addActionListener(onSearchAction());

        JButton prevMatch = new JButton();
        prevMatch.setName("PreviousMatchButton");
        prevMatch.setIcon(new ImageIcon(classLoader.getResource("icons/previous-24px.png")));
        prevMatch.setContentAreaFilled(false);
        prevMatch.setFocusPainted(false);
        prevMatch.setBorderPainted(false);
        prevMatch.addActionListener(e -> onPreviousButtonClicked());

        JButton nextMatch = new JButton();
        nextMatch.setName("NextMatchButton");
        nextMatch.setIcon(new ImageIcon(classLoader.getResource("icons/next-24px.png")));
        nextMatch.setContentAreaFilled(false);
        nextMatch.setFocusPainted(false);
        nextMatch.setBorderPainted(false);
        nextMatch.addActionListener(e -> onNextButtonClicked());

        useRegexCheckBox = new JCheckBox("Regex");
        useRegexCheckBox.setName("UseRegExCheckbox");
        searchPanel.add(startSearch);
        searchPanel.add(prevMatch);
        searchPanel.add(nextMatch);
        searchPanel.add(useRegexCheckBox);


        topPanel.setVisible(true);
        topPanel.add(searchPanel);
        add(topPanel, BorderLayout.NORTH);

        fileChooser.setName("FileChooser");
        fileChooser.setVisible(false);
        fileChooser.setDialogTitle("Select the file you want to open:");
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("TXT Files", "txt");
        fileChooser.addChoosableFileFilter(filter);
    }

    private void onNextButtonClicked() {
        if(results.size()>0 && resultsIterator !=null) {
            if(resultsIterator.hasNext()) {

                /** this block is to optimize the alternating behaviour **/
                {
                    if(resultsIterator.nextIndex()==this.currentElementIndex) {
                        resultsIterator.next();
                    }
                    this.currentElementIndex = this.resultsIterator.nextIndex();
                }
                /********************************************************/
                SearchWorker.MatchIndexes cur = resultsIterator.next();
                System.out.println("Next: ["+cur.startIndex+"] ["+cur.endIndex+"]");
                contentArea.setCaretPosition(cur.endIndex);
                contentArea.select(cur.startIndex, cur.endIndex);
                contentArea.grabFocus();
            }
            else {
                resultsIterator = this.results.listIterator(0);
                SearchWorker.MatchIndexes cur = resultsIterator.next();
                contentArea.setCaretPosition(cur.endIndex);
                contentArea.select(cur.startIndex, cur.endIndex);
                contentArea.grabFocus();
            }
        }

        else if(results.size()>0) {
            resultsIterator = results.listIterator();
            if(resultsIterator.hasNext()) {
                SearchWorker.MatchIndexes cur = resultsIterator.next();
                contentArea.setCaretPosition(cur.endIndex);
                contentArea.select(cur.startIndex, cur.endIndex);
                contentArea.grabFocus();
            }
        }
        else if (results==null || results.size()==0) {
        }
    }

    private void onPreviousButtonClicked() {
        if(results.size()>0 && resultsIterator !=null) {
            if(resultsIterator.hasPrevious()) {

                /** this block is to optimize the alternating behaviour of the listiterator **/
                {
                    if(resultsIterator.previousIndex()==this.currentElementIndex) {
                        resultsIterator.previous();
                    }
                    this.currentElementIndex = resultsIterator.previousIndex();
                }
                /**********************************************************/

                SearchWorker.MatchIndexes cur = resultsIterator.previous();
                System.out.println("Prev: ["+cur.startIndex+"] ["+cur.endIndex+"]");
                contentArea.setCaretPosition(cur.endIndex);
                contentArea.select(cur.startIndex, cur.endIndex);
                contentArea.grabFocus();
            }
            else {
                resultsIterator = this.results.listIterator(this.results.size());
                SearchWorker.MatchIndexes cur = resultsIterator.previous();
                contentArea.setCaretPosition(cur.endIndex);
                contentArea.select(cur.startIndex, cur.endIndex);
                contentArea.grabFocus();
            }
        }
        /***/
        else if(results.size()>0 && resultsIterator==null) {
            resultsIterator = results.listIterator();
            if (resultsIterator.hasPrevious()) {
                SearchWorker.MatchIndexes cur = resultsIterator.previous();
                contentArea.setCaretPosition(cur.endIndex);
                contentArea.select(cur.startIndex, cur.endIndex);
                contentArea.grabFocus();
            }
        }
        else if (results==null || results.size()==0) {
        }
    }

    private ActionListener onSearchAction() {
        return e -> {
            SwingUtilities.invokeLater(
                    new SearchWorker(this.contentArea, this.searhField, useRegexCheckBox.isSelected(), this::updateTheResults)
            );
        };
    }

    private void updateTheResults(List<SearchWorker.MatchIndexes> indexes) {
        this.results = indexes;
        this.resultsIterator = this.results.listIterator();

        if(resultsIterator.hasNext()) {
            this.currentElementIndex = resultsIterator.nextIndex();
            SearchWorker.MatchIndexes cur = resultsIterator.next();
            contentArea.setCaretPosition(cur.endIndex);
            contentArea.select(cur.startIndex, cur.endIndex);
            contentArea.grabFocus();
        }

    }

    private void openFileChooser(boolean save) {
        fileChooser.setVisible(true);

        if(!save) {
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                try {
                    File theFile = fileChooser.getSelectedFile();
                    contentArea.setText(new String(Files.readAllBytes(Paths.get(theFile.getAbsolutePath()))));
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
        else {
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                try {
                    File theFile = fileChooser.getSelectedFile();
                    Files.writeString(Paths.get(theFile.getAbsolutePath()), contentArea.getText());
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
    }
}
