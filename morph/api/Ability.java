package morph.api;

import net.minecraft.entity.*;
import net.minecraft.nbt.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.util.*;

public abstract class Ability
{
    private EntityLivingBase parent;
    public boolean inactive;
    
    public Ability() {
        this.parent = null;
    }
    
    public Ability parse(final String[] args) {
        return this;
    }
    
    public void setParent(final EntityLivingBase ent) {
        this.parent = ent;
    }
    
    public EntityLivingBase getParent() {
        return this.parent;
    }
    
    public abstract String getType();
    
    public abstract void tick();
    
    public abstract void kill();
    
    public abstract Ability clone();
    
    public boolean requiresInactiveClone() {
        return false;
    }
    
    public abstract void save(final NBTTagCompound p0);
    
    public abstract void load(final NBTTagCompound p0);
    
    @SideOnly(Side.CLIENT)
    public abstract void postRender();
    
    @SideOnly(Side.CLIENT)
    public abstract ResourceLocation getIcon();
    
    @SideOnly(Side.CLIENT)
    public boolean entityHasAbility(final EntityLivingBase living) {
        return true;
    }
    
    public static void registerAbility(final String name, final Class<? extends Ability> clz) {
        try {
            Class.forName("morph.common.ability.AbilityHandler").getDeclaredMethod("registerAbility", String.class, Class.class).invoke(null, name, clz);
        }
        catch (Exception ex) {}
    }
    
    public static void mapAbilities(final Class<? extends EntityLivingBase> entClass, final Ability... abilities) {
        try {
            Class.forName("morph.common.ability.AbilityHandler").getDeclaredMethod("mapAbilities", Class.class, Ability[].class).invoke(null, entClass, abilities);
        }
        catch (Exception ex) {}
    }
    
    public static void removeAbility(final Class<? extends EntityLivingBase> entClass, final String type) {
        try {
            Class.forName("morph.common.ability.AbilityHandler").getDeclaredMethod("removeAbility", Class.class, String.class).invoke(null, entClass, type);
        }
        catch (Exception ex) {}
    }
    
    public static boolean hasAbility(final Class<? extends EntityLivingBase> entClass, final String type) {
        try {
            return (boolean)Class.forName("morph.common.ability.AbilityHandler").getDeclaredMethod("hasAbility", Class.class, String.class).invoke(null, entClass, type);
        }
        catch (Exception e) {
            return false;
        }
    }
    
    public static Ability createNewAbilityByType(final String type, final String[] arguments) {
        try {
            return (Ability)Class.forName("morph.common.ability.AbilityHandler").getDeclaredMethod("createNewAbilityByType", String.class, String[].class).invoke(null, type, arguments);
        }
        catch (Exception e) {
            return null;
        }
    }
}
