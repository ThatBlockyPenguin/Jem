package com.blockypenguin.gemini.jem;

import com.blockypenguin.gemini.jem.plugins.JemPlugin;
import com.blockypenguin.gemini.jem.plugins.PluginManager;
import com.blockypenguin.gemini.jem.plugins.internal.InternalPlugin;
import com.blockypenguin.gemini.jem.windowing.NavBar;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

public final class JemInit {

    public static void main(String[] args) {
        FlatLightLaf.setup();
        UIManager.put("TextComponent.arc", 12);
        UIManager.put("ScrollPane.arc", 12);
        UIManager.put("Button.arc", 12);
        
        JemPlugin.load(new InternalPlugin());
        if(!PluginManager.loadPlugins())
            BrowserManager.showErrorDialogue("Could not load plugins!");
        
        NavBar.navigateGo();
        SwingUtilities.invokeLater(() -> BrowserManager.WINDOW.setVisible(true));
    }
}