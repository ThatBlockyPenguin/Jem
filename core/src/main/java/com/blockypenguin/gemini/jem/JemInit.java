package com.blockypenguin.gemini.jem;

import com.blockypenguin.gemini.jem.plugins.PluginManager;

import javax.swing.SwingUtilities;

public final class JemInit {
    public static void init(IUserInterface ui) {
        BrowserManager.setUserInterface(ui);
        
        if(!PluginManager.loadPlugins())
            BrowserManager.showErrorDialogue("Could not load plugins!");
        
        BrowserManager.getUserInterface().goToNavBarURL();
        SwingUtilities.invokeLater(BrowserManager.getUserInterface()::show);
    }
}