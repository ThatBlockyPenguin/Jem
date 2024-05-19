package com.blockypenguin.gemini.jem.utils;

import com.blockypenguin.gemini.jem.BrowserManager;
import com.blockypenguin.gemini.jem.browser.protocol.IProtocolHandler;
import com.formdev.flatlaf.extras.components.FlatButton;
import io.mikael.urlbuilder.UrlBuilder;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.swing.FontIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.function.Supplier;

public final class Utils {
    public static JButton iconButton(Ikon ikon, MouseListener listener) {
        var icon = FontIcon.of(ikon, 28);
        var btn = new JButton(icon) {{ addMouseListener(listener); }};
        
        var oc = icon.getIconColor();
        var disabledIcon = FontIcon.of(ikon, 28, new Color(
            oc.getRed(),
            oc.getGreen(),
            oc.getBlue(),
            percentage(oc.getAlpha(), 50)
        ));
        
        btn.setDisabledIcon(disabledIcon);
        return btn;
    }
    
    private static int percentage(int amt, int pct) {
        return (pct * amt) / 100;
    }
    
    public static String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);

        pw.close();

        return sw.toString();
    }
    
    public static int byteArrayIndexOf(byte[] data, byte[] search) {
        for(int i = 0; i <= data.length - search.length; i++) {
            boolean found = true;
            
            for(int j = 0; j < search.length; j++) {
                if(data[i + j] != search[j]) {
                    found = false;
                    break;
                }
            }
            
            if(found)
                return i;
        }
        
        return -1;
    }

    public static Optional<URL> createURL(URI uri) {
        var scheme = uri.getScheme();
        var handler = BrowserManager.getProtocolHandler(scheme).map(IProtocolHandler::getCustomURLStreamHandler);

        try{
            return Optional.of(URL.of(uri, handler.orElseThrow()));
        }catch(MalformedURLException | NoSuchElementException e) {
            return Optional.empty();
        }
    }
    
    @SuppressWarnings("deprecation")
    public static URL getURLUpper(URL url) {
        var path = getSliceOfArray(String.class, url.getPath().split("/"), 0, -1);
        try {
            return new URL(url, UrlBuilder.fromUrl(url).withPath(String.join("/", path)).toString());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
    
    @SuppressWarnings("unchecked")
    private static <T> T[] getSliceOfArray(Class<T> clazz, T[] src, int start, int end) {
        if(end < 0)
            end = src.length + end - 1;
        
        T[] arr = (T[]) Array.newInstance(clazz, end - start + 1);
        System.arraycopy(src, 0, arr, 0, end - start + 1);
        
        return arr;
    }
    
    public static URL getURLRoot(URL url) {
        if(url.getPath().isEmpty())
            return url;
        
        while(!url.getPath().isEmpty())
            url = getURLUpper(url);
        
        return url;
    }
}