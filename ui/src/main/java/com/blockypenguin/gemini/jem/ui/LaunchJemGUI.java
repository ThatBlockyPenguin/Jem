package com.blockypenguin.gemini.jem.ui;

import com.blockypenguin.gemini.jem.JemInit;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

public final class LaunchJemGUI {
    public static void main(String[] args) {
        FlatLightLaf.setup();
        UIManager.put("TextComponent.arc", 12);
        UIManager.put("ScrollPane.arc", 12);
        UIManager.put("Button.arc", 12);
        JemInit.init(new JemUI());
    }
}