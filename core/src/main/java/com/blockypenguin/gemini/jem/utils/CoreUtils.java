package com.blockypenguin.gemini.jem.utils;

import com.blockypenguin.gemini.jem.BrowserManager;
import com.blockypenguin.gemini.jem.browser.protocol.IProtocolHandler;
import io.mikael.urlbuilder.UrlBuilder;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.NoSuchElementException;
import java.util.Optional;

public final class CoreUtils {
    public static int percentage(int amt, int pct) {
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

    public static Optional<URL> createURLWithHandler(URI uri) {
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