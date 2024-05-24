package com.blockypenguin.gemini.jem;

import javax.swing.*;
import java.net.URI;
import java.net.URL;
import java.util.Optional;

public interface IUserInterface {
    void setRendererComponent(JComponent component);
    void checkNavButtons(boolean hasPrev, boolean hasNext, URL url);
    void goToNavBarURL();
    void show();
    void showDialogue(String dialogMessage, String title, int messageType);
    void setNavURLText(String text);
    Optional<URI> getNavURI();
}