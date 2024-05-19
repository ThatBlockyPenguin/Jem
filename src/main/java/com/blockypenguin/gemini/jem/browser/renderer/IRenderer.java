package com.blockypenguin.gemini.jem.browser.renderer;

import com.blockypenguin.gemini.jem.browser.Document;

import javax.swing.*;
import java.awt.*;

public interface IRenderer {
    JComponent getComponent(Document doc);
}