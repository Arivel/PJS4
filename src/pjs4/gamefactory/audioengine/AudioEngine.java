/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pjs4.gamefactory.audioengine;

import java.io.IOException;
import java.util.Collections;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javafx.util.Pair;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import pjs4.gamefactory.services.AudioService;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.DataLine;
import pjs4.gamefactory.services.ServiceLocator;
import pjs4.gamefactory.utils.collections.RingBuffer;

/**
 * AudioEngine est le moteur audio par défaut de la librairie, il permet de
 * jouer un son répertioré dans l'énum AudioRessource.
 *
 * Il recoit un AudioEvent contenant les informations sur le fichier audio et
 * l'action a effectuer avec (le jouer ou le stopper) ainsi que le volume auquel
 * celui-ci doit être joué.
 *
 * Il y'a un thread qui s'occupe de récupérer les events a partir de la liste et
 * un autre qui s'occupe de les jouer.
 *
 * @author Pascal Luttgens
 *
 * @version 1.0
 *
 * @since 1.0
 */
public class AudioEngine implements AudioService {

    private final Object lock = new Object();

    private final static String assetType = "audio";

    private final RingBuffer<AudioEvent> soundEvents;
    private final HashMap<String, Clip> playingSounds;

    private final Thread handler;

    /**
     * Construit un AudioEngine et initialise les collections ainsi que les
     * threads.
     *
     * - Pascal Luttgens.
     */
    public AudioEngine() {

        this.soundEvents = new RingBuffer<>();

        this.playingSounds = new HashMap<>();

        this.handler = new Thread(
                /**
                 * Récupère l'event le plus ancien de la collection d'event pour
                 * le passer à la collection des sons en cours de lecture.
                 *
                 * - Pascal Luttgens.
                 */
                () -> {
                    while (true) {
                         AudioEvent event;
                        synchronized (lock) {
                            event = soundEvents.get();
                        }
                        AudioEvent.Type eventType = event.getType();
                        if (eventType.equals(AudioEvent.Type.PLAY)) {
                            System.out.println("before loading");
                            Clip clip = loadClipFromEvent(event);
                            System.out.println("loaded");
                            if (clip != null) {
                                System.out.println("clip not null");
                                clip.addLineListener((LineEvent le) -> {
                                    if (le.getType().equals(LineEvent.Type.STOP)) {
                                        long eventPos = le.getFramePosition();
                                        Clip eventClip = (Clip) le.getLine();
                                        if (eventPos - eventClip.getFrameLength() > -0.1
                                        && eventPos - eventClip.getFrameLength() < 0.1) {
                                            le.getLine().close();
                                        }
                                    }
                                });
                                /**
                                 * Ajoute un listener sur le clip pour le fermer
                                 * lorsque sa lecture est finie.
                                 */
                                clip.addLineListener((LineEvent le) -> {
                                    if (le.getType().equals(LineEvent.Type.CLOSE)) {
                                        String id = event.getId();
                                        le.getLine().close();
                                        playingSounds.remove(id);
                                    }
                                });

                                synchronized (playingSounds) {
                                    playingSounds.put(event.getId(), clip);
                                }

                                clip.start();

                            } else if (eventType.equals(AudioEvent.Type.STOP)) {
                                synchronized (playingSounds) {
                                    playingSounds.get(event.getId()).stop();
                                }
                            }
                        }

                    }
                });

    }

    /**
     * Retourne un clip du système à partir d'un event.
     *
     * - Pascal Luttgens.
     *
     * @param ae L'event contenant la ressource
     *
     * @return Le clip du système initialisé avec la ressource
     */
    private Clip loadClipFromEvent(AudioEvent ae) {
        synchronized (lock) {
            try {
                AudioInputStream inputStream = (AudioInputStream) ServiceLocator.getAssetManager().getAsset(assetType, ae.getAsset());
                AudioFormat af = inputStream.getFormat();
                int size = (int) (af.getFrameSize() * inputStream.getFrameLength());
                byte[] audio = new byte[size];
                DataLine.Info info = new DataLine.Info(Clip.class, af, size);
                inputStream.read(audio, 0, size);

                Clip clip = (Clip) AudioSystem.getLine(info);
                clip.open(af, audio, 0, size);
                return clip;
            } catch (LineUnavailableException | IOException ex) {
                Logger.getLogger(AudioEngine.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }
    }

    /**
     * Réçoit un event et effectue l'action adéquate.
     *
     * - Pascal Luttgens.
     *
     * @param event L'event audio
     */
    @Override
    public void onNotify(EventObject event) {
        try {
            soundEvents.add((AudioEvent) event);
        } catch (ClassCastException ex) {
            Logger.getLogger(AudioEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Lance les processus de l'AudioEngine.
     */
    public void start() {
        handler.start();
    }

}
