package net.seanomik.tamablefoxes.versions.version_1_21_11_R1;

import net.minecraft.core.UUIDUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.bukkit.event.entity.EntityTargetEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class NMSUtil {

    private static final Method SET_MOB_TARGET_METHOD;

    static {
        try {
            // For some reason, classes loaded by maven return an old method ending with a boolean parameter
            // So we use reflection to fix this
            SET_MOB_TARGET_METHOD = Mob.class.getMethod("setTarget", LivingEntity.class, EntityTargetEvent.TargetReason.class);
            SET_MOB_TARGET_METHOD.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setTarget(Mob mob, LivingEntity livingEntity, EntityTargetEvent.TargetReason reason) {
        try {
            SET_MOB_TARGET_METHOD.invoke(mob, livingEntity, reason);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static void putUUID(ValueOutput valueOutput, String key, UUID uuid) {
        valueOutput.putIntArray(key, UUIDUtil.uuidToIntArray(uuid));
    }

    public static UUID getUUID(ValueInput valueInput, String key) {
        int[] result = valueInput.getIntArray(key).orElse(null);
        if (result == null) return null;
        return getUUITFromTag(result);
    }

    public static UUID getUUITFromTag(int[] intArray) {
        return UUIDUtil.uuidFromIntArray(intArray);
    }
}
