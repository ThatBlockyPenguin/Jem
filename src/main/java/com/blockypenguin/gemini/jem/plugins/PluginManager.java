package com.blockypenguin.gemini.jem.plugins;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Properties;

public final class PluginManager {
    private static final Logger LOGGER = LogManager.getLogger("Plugin Manager");
    private static final Path PLUGINS_PATH = Path.of("plugins");
    
    private static PluginProperties loadPluginProperties(File file) throws IOException {
        var jarURL = createJarURL(file.toURI().toURL(), "jem.plugin.properties");
        
        var conn = (JarURLConnection) jarURL.openConnection();
        var input = conn.getInputStream();
        
        var properties = new Properties();
        properties.load(input);
        
        var main = properties.getProperty("main");
        var name = properties.getProperty("name");
        var description = properties.getProperty("description");
        var version = properties.getProperty("version");
        
        return new PluginProperties(main, name, description, version);
    }
    
    private static URL createJarURL(URL url, String path) throws MalformedURLException {
        return URI.create("jar:" + url +"!/" + path).toURL();
    }
    
    private static Optional<JemPlugin> loadPlugin(File file) {
        LOGGER.error("Loading plugin {}...", file.getName());
        
        if(!file.exists()) {
            return Optional.empty();
        }
        
        PluginProperties property;
        
        try {
            property = loadPluginProperties(file);
        }catch(IOException e) {
            LOGGER.error("Could not load properties of plugin {}!", file.getName(), e);
            return Optional.empty();
        }
        
        URL[] urls;
        
        try {
            urls = new URL[]{ createJarURL(file.toURI().toURL(), "") };
        }catch(MalformedURLException e) {
            LOGGER.error("Could not create URL to plugin {}!", file.getName(), e);
            return Optional.empty();
        }
        
        @SuppressWarnings("resource")
        var ucl = new URLClassLoader(urls, ClassLoader.getSystemClassLoader());
        
        try {
            var instance = ucl.loadClass(property.main()).getConstructor().newInstance();
            
            if(instance instanceof JemPlugin plugin)
                return Optional.of(plugin);
            
            return Optional.empty();
        }catch(InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            LOGGER.error("Could not create instance of plugin {}!", file.getName(), e);
            return Optional.empty();
        }
    }
    
    public static boolean loadPlugins() {
        try {
            Files.createDirectories(PLUGINS_PATH);
        }catch(IOException e) {
            LOGGER.error("Could not create plugins directory!", e);
            return false;
        }
        
        File[] pluginFiles = PLUGINS_PATH.toFile().listFiles();
        
        if(pluginFiles == null) {
            LOGGER.error("Could not read plugins directory");
            return false;
        }
        
        for(File f : pluginFiles) {
            if(f.isDirectory() || !f.getName().endsWith(".jar"))
                continue;
            
            loadPlugin(f).ifPresent(JemPlugin::load);
        }
        
        return true;
    }
}