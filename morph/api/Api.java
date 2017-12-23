package morph.api;

import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.util.*;
import net.minecraft.client.entity.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.client.model.*;

public final class Api
{
    public static boolean hasMorph(final String playerName, final boolean isClient) {
        try {
            return (boolean)Class.forName("morph.common.core.ApiHandler").getDeclaredMethod("hasMorph", String.class, Boolean.TYPE).invoke(null, playerName, isClient);
        }
        catch (Exception e) {
            return false;
        }
    }
    
    public static float morphProgress(final String playerName, final boolean isClient) {
        try {
            return (float)Class.forName("morph.common.core.ApiHandler").getDeclaredMethod("morphProgress", String.class, Boolean.TYPE).invoke(null, playerName, isClient);
        }
        catch (Exception e) {
            return 1.0f;
        }
    }
    
    public static EntityLivingBase getPrevMorphEntity(final String playerName, final boolean isClient) {
        try {
            return (EntityLivingBase)Class.forName("morph.common.core.ApiHandler").getDeclaredMethod("getPrevMorphEntity", String.class, Boolean.TYPE).invoke(null, playerName, isClient);
        }
        catch (Exception e) {
            return null;
        }
    }
    
    public static EntityLivingBase getMorphEntity(final String playerName, final boolean isClient) {
        try {
            return (EntityLivingBase)Class.forName("morph.common.core.ApiHandler").getDeclaredMethod("getMorphEntity", String.class, Boolean.TYPE).invoke(null, playerName, isClient);
        }
        catch (Exception e) {
            return null;
        }
    }
    
    public static void blacklistEntity(final Class<? extends EntityLivingBase> clz) {
        try {
            Class.forName("morph.common.core.ApiHandler").getDeclaredMethod("blacklistEntity", Class.class).invoke(null, clz);
        }
        catch (Exception ex) {}
    }
    
    public static boolean forceMorph(final EntityPlayerMP player, final EntityLivingBase living) {
        try {
            return (boolean)Class.forName("morph.common.core.ApiHandler").getDeclaredMethod("forceMorph", EntityPlayerMP.class, EntityLivingBase.class).invoke(null, player, living);
        }
        catch (Exception e) {
            return false;
        }
    }
    
    public static void forceDemorph(final EntityPlayerMP player) {
        try {
            Class.forName("morph.common.core.ApiHandler").getDeclaredMethod("forceDemorph", EntityPlayerMP.class).invoke(null, player);
        }
        catch (Exception ex) {}
    }
    
    public static String isEntityAMorph(final EntityLivingBase living, final boolean isClient) {
        try {
            return (String)Class.forName("morph.common.core.ApiHandler").getDeclaredMethod("isEntityAMorph", EntityLivingBase.class, Boolean.TYPE).invoke(null, living, isClient);
        }
        catch (Exception e) {
            return null;
        }
    }
    
    public static void allowNextPlayerRender() {
        try {
            Class.forName("morph.common.core.ApiHandler").getDeclaredMethod("allowNextPlayerRender", (Class<?>[])new Class[0]).invoke(null, new Object[0]);
        }
        catch (Exception ex) {}
    }
    
    @SideOnly(Side.CLIENT)
    public static ResourceLocation getMorphSkinTexture() {
        try {
            return (ResourceLocation)Class.forName("morph.common.core.ApiHandler").getDeclaredMethod("getMorphSkinTexture", (Class<?>[])new Class[0]).invoke(null, new Object[0]);
        }
        catch (Exception e) {
            return AbstractClientPlayer.locationStevePng;
        }
    }
    
    @SideOnly(Side.CLIENT)
    public static void registerArmForModel(final ModelBase model, final ModelRenderer arm) {
        try {
            Class.forName("morph.common.core.ApiHandler").getDeclaredMethod("registerArmForModel", ModelBase.class, ModelRenderer.class).invoke(null, model, arm);
        }
        catch (Exception ex) {}
    }
}
