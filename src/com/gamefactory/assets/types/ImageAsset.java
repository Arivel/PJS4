package com.gamefactory.assets.types;

import com.gamefactory.assets.assetmanager.Asset;
import java.util.Arrays;
import java.util.Objects;

/**
 * La classe ImageAsset représente le stockage d'une grille de tiles.
 * 
 * @author Pascal Luttgens
 *
 * @version 1.0
 *
 * @since 1.0
 */
public class ImageAsset extends Asset {

    private final byte[] pixels;
    private final String extention;

    public ImageAsset(byte[] pixels, String extention) {
        this.pixels = pixels;
        this.extention = extention;
    }

    public byte[] getPixels() {
        return pixels;
    }

    public String getExtention() {
        return extention;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Arrays.hashCode(this.pixels);
        hash = 97 * hash + Objects.hashCode(this.extention);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ImageAsset other = (ImageAsset) obj;
        if (!Arrays.equals(this.pixels, other.pixels)) {
            return false;
        }
        return true;
    }

    
    @Override
    public Asset clone() {
        return new ImageAsset(Arrays.copyOf(pixels, pixels.length), extention);
    }

}
