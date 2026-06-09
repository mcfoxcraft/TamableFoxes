package net.seanomik.tamablefoxes.versions.version_26_1_R1;

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
    // FOX: Paper 26.x has setTarget(LivingEntity, TargetReason); genuine Spigot 26.x
    // only has the 3-arg variant with a trailing fireEvent boolean. Bind whichever
    // exists so the class initializer cannot crash entity saves/ticks on Spigot.
    private static final boolean SET_MOB_TARGET_HAS_FIRE_EVENT_ARG;

    static {
        Method setTarget;
        boolean hasFireEventArg = false;
        try {
            setTarget = Mob.class.getMethod("setTarget", LivingEntity.class, EntityTargetEvent.TargetReason.class);
        } catch (NoSuchMethodException e) {
            try {
                setTarget = Mob.class.getMethod("setTarget", LivingEntity.class, EntityTargetEvent.TargetReason.class, boolean.class);
                hasFireEventArg = true;
            } catch (NoSuchMethodException e2) {
                throw new RuntimeException(e2);
            }
        }
        setTarget.setAccessible(true);
        SET_MOB_TARGET_METHOD = setTarget;
        SET_MOB_TARGET_HAS_FIRE_EVENT_ARG = hasFireEventArg;
    }

    public static void setTarget(Mob mob, LivingEntity livingEntity, EntityTargetEvent.TargetReason reason) {
        try {
            if (SET_MOB_TARGET_HAS_FIRE_EVENT_ARG) {
                SET_MOB_TARGET_METHOD.invoke(mob, livingEntity, reason, true);
            } else {
                SET_MOB_TARGET_METHOD.invoke(mob, livingEntity, reason);
            }
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
