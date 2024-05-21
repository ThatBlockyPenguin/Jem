package com.blockypenguin.gemini.jem.windowing;

import com.formdev.flatlaf.extras.components.FlatScrollPane;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public final class ContentPane extends JPanel {
    private final FlatScrollPane scroll = new FlatScrollPane() {{
        getVerticalScrollBar().setUnitIncrement(16);
        getHorizontalScrollBar().setUnitIncrement(16);
        setSmoothScrolling(true);
    }};

    public ContentPane() {
        this.setLayout(new BorderLayout());
        this.add(scroll, BorderLayout.CENTER);
        this.setBorder(new EmptyBorder(0, 5, 5, 5));

        setContents(new JPanel());
    }

    public void setContents(JComponent rendererComponent) {
        rendererComponent.setOpaque(false);

        scroll.setViewportView(
            new JPanel(new CardLayout()) {{
                setBackground(UIManager.getColor("TextArea.background"));
                add(rendererComponent);
            }}
        );
    }
}