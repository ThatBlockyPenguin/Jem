package com.blockypenguin.gemini.jem.ui;

import com.blockypenguin.gemini.jem.BrowserManager;
import com.blockypenguin.gemini.jem.utils.CoreUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URI;

public class JemWindow extends JFrame {
    private static final int ALT = InputEvent.ALT_DOWN_MASK & InputEvent.ALT_GRAPH_DOWN_MASK;
    private final ContentPane contentPane = new ContentPane();
    
    public JemWindow() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout(5, 5));

        initComponents();
        initKeybinds();

        this.setTitle("Jem Browser");
        this.setMinimumSize(new Dimension(700, 400));
        this.setLocationRelativeTo(null);
    }
    
    private void initComponents() {
        this.add(new NavBar(), BorderLayout.NORTH);
        this.add(contentPane, BorderLayout.CENTER);
    }
    
    private void initKeybinds() {
        addKeybind(
            KeyStroke.getKeyStroke("F1"),
            e -> CoreUtils.createURLWithHandler(URI.create(BrowserManager.HELP_ADDRESS)).ifPresent(BrowserManager.NAVIGATOR::go)
        );
        addKeybind(KeyStroke.getKeyStroke("F5"), e -> BrowserManager.NAVIGATOR.reload());
        addKeybind(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, ALT), e -> BrowserManager.NAVIGATOR.back());
        addKeybind(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.ALT_DOWN_MASK), e -> BrowserManager.NAVIGATOR.forward());
        addKeybind(KeyStroke.getKeyStroke(KeyEvent.VK_UP, ALT), e -> BrowserManager.NAVIGATOR.up());
        addKeybind(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, ALT), e -> BrowserManager.NAVIGATOR.root());
    }
    
    private void addKeybind(KeyStroke keystroke, ActionListener listener) {
        this.getRootPane().registerKeyboardAction(listener, keystroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
    }
    
    public void setRendererComponent(JComponent rendererComponent) {
        if(rendererComponent == null)
            setRendererComponent(new JPanel());
        else
            contentPane.setContents(rendererComponent);
    }
}