package net.seanomik.tamablefoxes.util;

import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Objects;

public class Utils {
    public static String getPrefix() {
        return ChatColor.RED + "[Tamable Foxes] ";
    }

    private static Plugin tamableFoxesPlugin;

    public static Plugin getTamableFoxesPlugin() {
        if (tamableFoxesPlugin == null) {
            throw new IllegalStateException("TamableFoxes plugin instance is not initialized");
        }
        return tamableFoxesPlugin;
    }

    public static void setTamableFoxesPlugin(Plugin plugin) {
        tamableFoxesPlugin = plugin;
    }

    public static Class<?> getPrivateInnerClass(Class outer, String innerName) {
        for (Class<?> declaredClass : outer.getDeclaredClasses()) {
            if (declaredClass.getSimpleName().equals(innerName)) return declaredClass;
        }

        return null;
    }

    public static Object instantiatePrivateInnerClass(Class outer, String innerName, Object outerObject, List<Object> args, List<Class<?>> argTypes) {
        try {
            // FOX: better error message
            Class<?> innerClass = Objects.requireNonNull(getPrivateInnerClass(outer, innerName), "Inner class of " + outer.getName() + " not found: " + innerName);

            Object[] argObjects = new Object[args.size() + 1];
            Class<?>[] argClasses = new Class<?>[argTypes.size() + 1];

            // Needed due to how List#toArray() converts the classes to objects
            for (int i = 0; i < argClasses.length; i++) {
                if (i == argClasses.length - 1) continue;
                argObjects[i + 1] = args.get(i);
                argClasses[i + 1] = argTypes.get(i);
            }
            argObjects[0] = outerObject;
            argClasses[0] = outer;

            Constructor<?> innerConstructor = innerClass.getDeclaredConstructor(argClasses);
            innerConstructor.setAccessible(true);

            Object instantiatedClass = innerConstructor.newInstance(argObjects);

            innerConstructor.setAccessible(false);

            return instantiatedClass;
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate private inner class " + innerName + " of " + outer.getName(), e);
        }
    }

}
