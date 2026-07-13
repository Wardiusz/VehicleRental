package com.wardiusz.carrental.model;

import java.io.*;
import java.util.*;

public abstract class ObjectExtent implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private static Map<Class<? extends ObjectExtent>, List<ObjectExtent>> allExtents = new HashMap<>();

    protected ObjectExtent() {
        allExtents.computeIfAbsent(getClass(), k -> new ArrayList<>()).add(this);
    }

    //
    @SuppressWarnings("unchecked")
    public static <T extends ObjectExtent> List<T> getExtent(Class<T> type) {
        return Collections.unmodifiableList((List<T>) allExtents.computeIfAbsent(type, k -> new ArrayList<>()));
    }

    public static <T extends ObjectExtent> List<T> getExtentWithSubclasses(Class<T> type) {
        List<T> result = new ArrayList<>();

        for (Map.Entry<Class<? extends ObjectExtent>, List<ObjectExtent>> e : allExtents.entrySet()) {
            if (type.isAssignableFrom(e.getKey())) {
                for (ObjectExtent o : e.getValue()) {
                    result.add(type.cast(o));
                }
            }
        }

        return result;
    }

    public static void removeFromExtent(ObjectExtent obj) {
        List<ObjectExtent> list = allExtents.get(obj.getClass());

        if (list != null) {
            list.remove(obj);
        }
    }

    public static void saveExtents(File file) throws IOException {
        File parent = file.getParentFile();

        if (parent != null) {
            parent.mkdirs();
        }

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(allExtents);
        }
    }

    @SuppressWarnings("unchecked")
    public static void loadExtents(File file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            allExtents = (Map<Class<? extends ObjectExtent>, List<ObjectExtent>>) in.readObject();
        }
    }

    public static void clearExtents() {
        allExtents = new HashMap<>();
    }
}
