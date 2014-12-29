/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gamefactory.assets.cache;

import com.gamefactory.assets.assetmanager.Asset;
import com.gamefactory.assets.assetmanager.AssetCache;
import com.gamefactory.assets.assetmanager.AssetKey;
import java.util.concurrent.ConcurrentHashMap;

public class BasicCacheImpl implements AssetCache {

    private final ConcurrentHashMap<AssetKey, Asset> cache;

    public BasicCacheImpl() {
        this.cache = new ConcurrentHashMap<>();
    }

    @Override
    public void addToCache(AssetKey assetKey, Asset asset) {
        cache.put(assetKey, asset);
    }

    @Override
    public Asset getFromCache(AssetKey assetKey) {
        return cache.get(assetKey);
    }

    @Override
    public boolean removeFromCache(AssetKey assetKey) {
        return (cache.remove(assetKey) != null);
    }

    @Override
    public void clearCache() {
        cache.clear();
    }

    @Override
    public Asset getCopyFromCache(AssetKey assetKey) {
        Asset copy = cache.get(assetKey);
        return (copy != null) ? copy.clone() : null;
    }

}