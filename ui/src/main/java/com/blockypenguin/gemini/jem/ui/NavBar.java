package com.blockypenguin.gemini.jem.ui;

import com.blockypenguin.gemini.jem.BrowserManager;
import com.blockypenguin.gemini.jem.utils.MouseHandlerFactory;
import com.blockypenguin.gemini.jem.utils.CoreUtils;
import com.formdev.flatlaf.extras.components.FlatTextField;
import com.formdev.flatlaf.extras.components.FlatToolBar;
import org.kordamp.ikonli.materialdesign.MaterialDesign;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URI;
import java.net.URL;
import java.util.Optional;

public class NavBar extends FlatToolBar {
    private static final JButton ROOT = UIUtils.iconButton(
        MaterialDesign.MDI_CHEVRON_DOUBLE_UP,
        MouseHandlerFactory.create(
            MouseHandlerFactory.Type.CLICK,
            (e) -> BrowserManager.NAVIGATOR.root()
        )
    );
    
    private static final JButton BACK = UIUtils.iconButton(
        MaterialDesign.MDI_ARROW_LEFT,
        MouseHandlerFactory.create(
            MouseHandlerFactory.Type.CLICK,
            (e) -> BrowserManager.NAVIGATOR.back()
        )
    );
    
    private static final JButton UP = UIUtils.iconButton(
        MaterialDesign.MDI_ARROW_UP,
        MouseHandlerFactory.create(
            MouseHandlerFactory.Type.CLICK,
            (e) -> BrowserManager.NAVIGATOR.up()
        )
    );
    private static final JButton FORWARD = UIUtils.iconButton(
        MaterialDesign.MDI_ARROW_RIGHT,
        MouseHandlerFactory.create(
            MouseHandlerFactory.Type.CLICK,
            (e) -> BrowserManager.NAVIGATOR.forward()
        )
    );
    
    private static final JButton RELOAD = UIUtils.iconButton(
        MaterialDesign.MDI_RELOAD,
        MouseHandlerFactory.create(
            MouseHandlerFactory.Type.CLICK,
            (e) -> BrowserManager.NAVIGATOR.reload()
        )
    );
    
    private static final FlatTextField ADDRESS = new FlatTextField() {{
        setText(BrowserManager.DEFAULT_ADDRESS);
        setPlaceholderText("Enter URL...");
        setTrailingComponent(UIUtils.iconButton(
            MaterialDesign.MDI_MAGNIFY,
            MouseHandlerFactory.create(
                MouseHandlerFactory.Type.CLICK,
                (e) -> navigateGo()
            )
        ));
        
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER)
                    navigateGo();
            }
        });
    }};

    public NavBar() {
        this.add(ROOT);
        this.add(BACK);
        this.add(UP);
        this.add(FORWARD);
        this.add(RELOAD);
        this.add(ADDRESS);
        
        this.setBorder(new EmptyBorder(5, 7, 0, 7));
    }
    
    public static void navigateGo() {
        getURI().flatMap(CoreUtils::createURLWithHandler).ifPresent(BrowserManager.NAVIGATOR::go);
    }

    public static Optional<URI> getURI() {
        var address = ADDRESS.getText().trim();

        if(address.startsWith("//"))
            address = "gemini:" + address;

        if(!address.contains(":"))
            address = "gemini://" + address;

        if(address.startsWith(":"))
            address = "gemini" + address;

        while(address.contains("///"))
            address = address.replaceAll("///", "//");

        try {
            return Optional.of(URI.create(address));
        }catch(IllegalArgumentException e) {
            BrowserManager.showErrorDialogue(e, "Invalid URI: {}", address);
            return Optional.empty();
        }
    }
    
    public static void setURL(String url) {
        ADDRESS.setText(url);
    }
    
    public static void checkButtons(boolean hasPrev, boolean hasNext, URL url) {
        var path = url.getPath();
        var pathIsNotRoot = !(path.isEmpty() || path.contentEquals("/"));
        
        BACK.setEnabled(hasPrev);
        FORWARD.setEnabled(hasNext);
        ROOT.setEnabled(pathIsNotRoot);
        UP.setEnabled(path.contains("/") && pathIsNotRoot);
    }
}