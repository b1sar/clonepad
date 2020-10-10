package clonepad;


import clonepad.ui.Clonepad;

import javax.swing.*;

public class ApplicationRunner {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Clonepad::new);
    }
}
