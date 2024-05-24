package com.blockypenguin.gemini.jem.ui;

import com.blockypenguin.gemini.jem.IUserInterface;

import javax.swing.*;
import java.net.URI;
import java.net.URL;
import java.util.Optional;

public final class JemUI implements IUserInterface {
    private static final JemWindow WINDOW = new JemWindow();
    
    @Override
    public void setRendererComponent(JComponent component) {
        WINDOW.setRendererComponent(component);
    }
    
    @Override
    public void checkNavButtons(boolean hasPrev, boolean hasNext, URL url) {
        NavBar.checkButtons(hasPrev, hasNext, url);
    }
    
    @Override
    public void goToNavBarURL() {
        NavBar.navigateGo();
    }
    
    @Override
    public void show() {
        WINDOW.setVisible(true);
    }
    
    @Override
    public void showDialogue(String dialogMessage, String title, int messageType) {
        JOptionPane.showMessageDialog(
            WINDOW,
            dialogMessage,
            title,
            messageType
        );
    }
    
    @Override
    public void setNavURLText(String text) {
        NavBar.setURL(text);
    }
    
    @Override
    public Optional<URI> getNavURI() {
        return NavBar.getURI();
    }
}