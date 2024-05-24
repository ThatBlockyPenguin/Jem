package com.blockypenguin.gemini.jem.ui;

import com.blockypenguin.gemini.jem.utils.CoreUtils;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;

public final class UIUtils {
    public static JButton iconButton(Ikon ikon, MouseListener listener) {
        var icon = FontIcon.of(ikon, 28);
        var btn = new JButton(icon) {{ addMouseListener(listener); }};
        
        var oc = icon.getIconColor();
        var disabledIcon = FontIcon.of(ikon, 28, new Color(
            oc.getRed(),
            oc.getGreen(),
            oc.getBlue(),
            CoreUtils.percentage(oc.getAlpha(), 50)
        ));
        
        btn.setDisabledIcon(disabledIcon);
        return btn;
    }
}