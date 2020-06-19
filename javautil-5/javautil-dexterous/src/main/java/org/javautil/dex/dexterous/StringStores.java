package org.javautil.dex.dexterous;


import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

public class StringStores {
    private final  Map<String,TreeMap<String,String>> stores = new TreeMap<String,TreeMap<String,String>>();

    public void add(final String poolName, final String value) {
        TreeMap<String,String> store = stores.get(poolName);
        if (store == null) {
            store = new TreeMap<String,String>();
            stores.put(poolName,store);
          // throw new IllegalArgumentException("no such store: '" + poolName + "'");
        }
        store.put(value,value);
    }

    public void addAll(final StringStores store) {
        for (final Map.Entry<String,TreeMap<String,String>> entry : store.stores.entrySet()) {
            final String storeKey = entry.getKey();
            final Map<String,String> values = entry.getValue();
            for (final String value : values.values()) {
                this.add(storeKey,value);
            }
        }
    }

    public void addStore(final String storeName) {
       TreeMap<String,String> store = stores.get(storeName);
       if (store == null) {
           store = new TreeMap<String,String>();
           stores.put(storeName,store);
       }

    }

    public Collection<String> getStore(final String storeName) {
        Collection<String> returnValue = null;
        final Map<String,String> store = stores.get(storeName);
        if (store != null) {
            returnValue = store.values();
        }
        return returnValue;
    }
}
