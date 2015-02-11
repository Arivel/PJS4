package com.gamefactory.services;

import com.gamefactory.assets.assetmanager.AssetManager;
import com.gamefactory.utils.events.Notifier;
import com.gamefactory.utils.events.Subject;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import org.json.JSONObject;

/**
 * Le locateur de service sert d'interface entre le code et les services afin de
 * les découpler et de contrôler l'instance des services.
 *
 * Il possède aussi des méthodes statiques utilites notamment pour l'utilisation
 * des objet JSON.
 *
 * @author Pascal Luttgens
 *
 * @version 1.0
 *
 * @since 1.0
 */
public class ServiceLocator {

    private final static Notifier notifier = new Notifier(null);
    private final static JSONObject config = getJSONObjectFromFile(new File("config/config.cfg"));
    private final static HashMap<String, Service> services = new HashMap<>();
    private static AssetManager assetManager;
    private static JFrame frame;

    public static void provideService(String serviceName, Service service) {
        ServiceLocator.services.put(serviceName, service);
        ServiceLocator.notifier.notifyObservers(serviceName.toUpperCase() + "_SERVICE_PROVIDED");
    }

    public static void provideAssetManager(AssetManager assetManager) {
        ServiceLocator.assetManager = assetManager;
        ServiceLocator.notifier.notifyObservers("ASSET_MANAGER_PROVIDED");
    }

    public static Service getService(String type) {
        return ServiceLocator.services.get(type);
    }

    public static AssetManager getAssetManager() {
        return assetManager;
    }

    public static JSONObject getConfig() {
        return ServiceLocator.config;
    }
    
    public static void provideWindow(JFrame frame) {
        ServiceLocator.frame = frame;
        ServiceLocator.notifier.notifyObservers("GAME_WINDOW_PROVIDED");
    }

    public static JFrame getWindow() {
        return ServiceLocator.frame;
    }
    
    public static JSONObject getJSONObjectFromFile(File file) {
        try (FileReader fr = new FileReader(file)) {
            String json = new String();
            while (fr.ready()) {
                json += (char) fr.read();
            }
            int index = json.indexOf("{");
            json = json.substring(index);
            return new JSONObject(json);
        } catch (IOException ex) {
            Logger.getLogger(ServiceLocator.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }

    public static Notifier getNotifier() {
        return ServiceLocator.notifier;
    }
}
