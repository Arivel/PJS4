package com.gamefactory.displayable;

import com.gamefactory.utils.events.Event;
import com.gamefactory.utils.events.Observer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Le Component Manager encapsule le comportement des components au sein d'un
 * game Object.
 *
 * @author Pascal Luttgens
 *
 * @version 1.0
 *
 * @since 1.0
 */
public class ComponentManager implements Observer {

    private final GameObject owner;
    private final List<Component> components;
    private final List<Script> scripts;

    public ComponentManager(GameObject owner) {
        this.owner = owner;
        this.components = new ArrayList<>();
        this.scripts = new ArrayList<>();
    }

    /**
     * Initialise une liste de components.
     *
     * - Pascal Luttgens.
     *
     * @param components Les components.
     *
     * @since 1.0
     */
    public void init(Component... components) {
        for (Component component : components) {
            if (component instanceof Script) {
                this.scripts.add((Script) component);
            } else {
                this.components.add(component);
            }
        }
        this.components.sort(new Component.UpdatePriorityComparator());
        Iterator<Component> itComponent = this.components.iterator();
        while (itComponent.hasNext()) {
            itComponent.next().init(this);
        }

        Iterator<Script> itScript = this.scripts.iterator();
        while (itScript.hasNext()) {
            itScript.next().init(this);
        }
    }

    public void update() {
        for (Script script : this.scripts) {
            script.update();
        }
        for (Component component : this.components) {
            component.update();
        }
    }

// A modifier.
    /**
     * public Method invoke() { for (Component component : components) { try {
     * return (float) component.getClass().getMethod("getX").invoke(component);
     * } catch (NoSuchMethodException | SecurityException |
     * IllegalAccessException | IllegalArgumentException |
     * InvocationTargetException ex) { } } throw new
     * NoSuchComponentException("Le Game Object ne contient pas de component
     * position."); }*
     */
    public Component getComponent(String componentName) {
        Iterator<Component> it = components.iterator();
        while (it.hasNext()) {
            Component component = it.next();
            if (component.getClass().getSimpleName().equals(componentName)) {
                return component;
            }
        }
        throw new IllegalStateException("Component manquant : " + componentName);
    }

    public Component getComponent(Class<? extends Component> componentClass) {
        Iterator<Component> it = components.iterator();
        while (it.hasNext()) {
            Component component = it.next();
            if (component.getClass().equals(componentClass)) {
                return component;
            }
        }
        throw new IllegalStateException("Component manquant : " + componentClass.getSimpleName());
    }

    /**
     * Vérifie si un component est initialisé dans la liste à partir de son nom.
     *
     * - Pascal Luttgens.
     *
     * @param componentName Le nom du component.
     *
     * @return True si le component fait partie de la liste.
     *
     * @since 1.0
     */
    public boolean checkForComponent(String componentName) {
        Iterator<Component> it = components.iterator();
        while (it.hasNext()) {
            Component component = it.next();
            if (component.getClass().getSimpleName().equals(componentName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Vérifie si un component est initialisé à partir d'une représentation de
     * sa classe.
     *
     * - Pascal Luttgens.
     *
     * @param componentClass Un objet class représentant le component.
     *
     * @return True si le component fait partie de la liste.
     *
     * @since 1.0
     */
    public boolean checkForComponent(Class<? extends Component> componentClass) {
        Iterator<Component> it = components.iterator();
        while (it.hasNext()) {
            Component component = it.next();
            if (component.getClass().equals(componentClass)) {
                return true;
            }
        }
        return false;
    }

    /* Devrait passer par un notifier mais permet d'éviter un niveau inutile
     * d'indirection même si on ne devrait pas appeler la méthode onNotify directement...  
     */
    
    @Override
    public void onNotify(Event event) {
        Iterator<Component> itComponent = this.components.iterator();
        while (itComponent.hasNext()) {
            itComponent.next().onNotify(event);
        }

        Iterator<Script> itScript = this.scripts.iterator();
        while (itScript.hasNext()) {
            itScript.next().onNotify(event);
        }
    }
    
    
}
