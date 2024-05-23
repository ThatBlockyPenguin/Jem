package com.blockypenguin.gemini.jem.plugins.internal.renderer.plaintext;

import com.blockypenguin.gemini.jem.browser.Document;
import com.blockypenguin.gemini.jem.browser.renderer.IRenderer;

import javax.swing.*;

public class PlainTextRenderer implements IRenderer {
    @Override
    public JComponent getComponent(Document doc) {
        return getTextArea(new String(doc.data()));
    }

    public static JTextArea getTextArea(String str) {
        return new JTextArea(str) {{
            setEditable(false);
        }};
    }
}