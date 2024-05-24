package com.blockypenguin.gemini.jem.plugins.internal.renderer.gemtext;

import com.blockypenguin.gemini.jem.BrowserManager;
import com.blockypenguin.gemini.jem.browser.Document;
import com.blockypenguin.gemini.jem.browser.renderer.IRenderer;
import com.blockypenguin.gemini.jem.utils.CoreUtils;
import com.formdev.flatlaf.extras.components.FlatLabel;
import com.formdev.flatlaf.ui.FlatRoundBorder;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class GemtextRenderer implements IRenderer {
    @Override
    public JComponent getComponent(Document doc) {
        return new JPanel() {
            {
                setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
                setBorder(new EmptyBorder(4, 0, 4, 4));

                final String[] cache = {""};
                new String(doc.data()).lines().forEach(line -> {
                    if(!cache[0].isEmpty()) cache[0] += line + "\n";
                    else {
                        if(line.startsWith("=>")) link(line);
                        else if(line.startsWith("### ")) heading(3, line);
                        else if(line.startsWith("## ")) heading(2, line);
                        else if(line.startsWith("# ")) heading(1, line);
                        else if(line.startsWith("* ")) listItem(line);
                        else if(line.startsWith(">")) blockQuote(line);
                        else if(line.startsWith("```")) {}
                        else text(line);
                    }

                    if(line.startsWith("```")) {
                        if(cache[0].isEmpty()) cache[0] += line + "\n";
                        else {
                            preformatted(cache[0]);
                            cache[0] = "";
                        }
                    }
                });
            }

            private void heading(int level, String line) {
                add(new FlatLabel() {{
                    setText(line.substring(level).trim());
                    
                    setLabelType(switch(level) {
                        case 1 -> LabelType.h00;
                        case 2 -> LabelType.h0;
                        case 3 -> LabelType.h1;
                        default -> throw new IllegalArgumentException("Heading level must be between 1 and 3 inclusive.");
                    });
                }});
            }

            private void link(String line) {
                var linkSegments = line.substring(2).split("\\s+");
                var linkTarget = linkSegments[1];
                var linkText = Arrays.stream(linkSegments)
                        .filter(s -> !s.isBlank())
                        .skip(1)
                        .collect(Collectors.joining(" "));
                
                if(linkText.isBlank())
                    linkText = linkTarget;
                
                String finalLinkText = linkText;
                add(new FlatLabel() {{
                    setText(finalLinkText);
                    setToolTipText(linkTarget);
                    setForeground(Color.decode("#32adff"));
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            BrowserManager.getUserInterface().getNavURI().flatMap(uri -> CoreUtils.createURLWithHandler(uri.resolve(linkTarget)))
                                .ifPresent(BrowserManager.NAVIGATOR::go);
                        }
                        
                        @Override
                        public void mouseEntered(MouseEvent e) {
                            Font font = getFont();
                            Map<TextAttribute, Object> attributes = new HashMap<>(font.getAttributes());
                            attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
                            setFont(font.deriveFont(attributes));
                        }
                        
                        @Override
                        public void mouseExited(MouseEvent e) {
                            Font font = getFont();
                            Map<TextAttribute, Object> attributes = new HashMap<>(font.getAttributes());
                            attributes.put(TextAttribute.UNDERLINE, -1);
                            setFont(font.deriveFont(attributes));
                        }
                    });
                }});
            }

            private void listItem(String line) {
                add(new FlatLabel() {{
                    setText("•" + line.substring(2));
                }});
            }

            private void blockQuote(String line) {
                add(new FlatLabel() {{
                    setLabelType(LabelType.regular);
                    setText(line.substring(1));
                    setBackground(Color.gray);
                    setBorder(BorderFactory.createCompoundBorder(
                        new FlatRoundBorder(),
                        new EmptyBorder(5, 5, 5, 5)
                    ));
                }});
            }

            private void preformatted(String data) {
                add(new FlatLabel() {{
                    setLabelType(LabelType.monospaced);
                    setText(data);
                }});
            }

            private void text(String line) {
                add(new FlatLabel() {{
                    setLabelType(LabelType.regular);
                    setText(line.isBlank() ? "\n" : line);
                }});
            }
        };
    }
}