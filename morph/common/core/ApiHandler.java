package morph.common.core;

import morph.common.*;
import morph.client.morph.*;
import morph.common.morph.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import java.util.*;
import net.minecraft.util.*;
import morph.client.render.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.client.model.*;
import morph.client.model.*;

public class ApiHandler
{
    public static boolean hasMorph(final String playerName, final boolean isClient) {
        if (isClient) {
            return Morph.proxy.tickHandlerClient.playerMorphInfo.containsKey(playerName);
        }
        return Morph.proxy.tickHandlerServer.getPlayerMorphInfo(playerName) != null;
    }
    
    public static float morphProgress(final String playerName, final boolean isClient) {
        MorphInfo info;
        if (isClient) {
            info = Morph.proxy.tickHandlerClient.playerMorphInfo.get(playerName);
        }
        else {
            info = Morph.proxy.tickHandlerServer.getPlayerMorphInfo(playerName);
        }
        if (info != null) {
            float prog = info.morphProgress / 80.0f;
            if (prog > 1.0f) {
                prog = 1.0f;
            }
            return prog;
        }
        return 1.0f;
    }
    
    public static EntityLivingBase getPrevMorphEntity(final String playerName, final boolean isClient) {
        MorphInfo info;
        if (isClient) {
            info = Morph.proxy.tickHandlerClient.playerMorphInfo.get(playerName);
        }
        else {
            info = Morph.proxy.tickHandlerServer.getPlayerMorphInfo(playerName);
        }
        if (info != null && info.prevState != null) {
            return info.prevState.entInstance;
        }
        return null;
    }
    
    public static EntityLivingBase getMorphEntity(final String playerName, final boolean isClient) {
        MorphInfo info;
        if (isClient) {
            info = Morph.proxy.tickHandlerClient.playerMorphInfo.get(playerName);
        }
        else {
            info = Morph.proxy.tickHandlerServer.getPlayerMorphInfo(playerName);
        }
        if (info != null) {
            return info.nextState.entInstance;
        }
        return null;
    }
    
    public static void blacklistEntity(final Class<? extends EntityLivingBase> clz) {
        if (!Morph.blacklistedClasses.contains(clz)) {
            Morph.blacklistedClasses.add(clz);
        }
    }
    
    public static boolean forceMorph(final EntityPlayerMP player, final EntityLivingBase living) {
        return EntityHelper.morphPlayer(player, living, false, true);
    }
    
    public static void forceDemorph(final EntityPlayerMP player) {
        EntityHelper.demorphPlayer(player);
    }
    
    public static String isEntityAMorph(final EntityLivingBase living, final boolean isClient) {
        HashMap infoMap;
        if (isClient) {
            infoMap = Morph.proxy.tickHandlerClient.playerMorphInfo;
        }
        else {
            infoMap = Morph.proxy.tickHandlerServer.playerMorphInfo;
        }
        for (final Map.Entry e : infoMap.entrySet()) {
            if (e.getValue().nextState.entInstance == living || (e.getValue().prevState != null && e.getValue().prevState.entInstance == living)) {
                return e.getKey();
            }
        }
        return null;
    }
    
    public static void allowNextPlayerRender() {
        Morph.proxy.tickHandlerClient.allowRender = true;
    }
    
    @SideOnly(Side.CLIENT)
    public static ResourceLocation getMorphSkinTexture() {
        return RenderMorph.morphSkin;
    }
    
    @SideOnly(Side.CLIENT)
    public static void registerArmForModel(final ModelBase model, final ModelRenderer arm) {
        ModelHelper.armMappings.put(model.getClass(), arm);
    }
}
