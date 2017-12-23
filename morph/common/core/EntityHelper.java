package morph.common.core;

import ichun.common.core.*;
import morph.common.*;
import net.minecraft.entity.player.*;
import net.minecraft.entity.boss.*;
import net.minecraftforge.common.util.*;
import net.minecraft.nbt.*;
import net.minecraftforge.common.*;
import cpw.mods.fml.common.eventhandler.*;
import morph.common.morph.*;
import morph.api.*;
import ichun.common.core.network.*;
import net.minecraft.entity.*;
import morph.common.packet.*;
import java.util.*;

public class EntityHelper extends EntityHelperBase
{
    public static boolean morphPlayer(final EntityPlayerMP player, final EntityLivingBase living, final boolean kill) {
        return morphPlayer(player, living, kill, false);
    }
    
    public static boolean morphPlayer(final EntityPlayerMP player, final EntityLivingBase living, final boolean kill, final boolean forced) {
        if ((Morph.config.getInt("childMorphs") == 0 && living.isChild()) || (Morph.config.getInt("playerMorphs") == 0 && living instanceof EntityPlayer) || (Morph.config.getInt("bossMorphs") == 0 && living instanceof IBossDisplayData) || player.getClass() == FakePlayer.class || player.playerNetServerHandler == null) {
            return false;
        }
        for (final Class<? extends EntityLivingBase> clz : Morph.blacklistedClasses) {
            if (clz.isInstance(living)) {
                return false;
            }
        }
        Label_0167: {
            if (!Morph.playerList.isEmpty()) {
                if (Morph.config.getInt("listIsBlacklist") == 0) {
                    if (!Morph.playerList.contains(player.getCommandSenderName())) {
                        break Label_0167;
                    }
                }
                else if (Morph.playerList.contains(player.getCommandSenderName())) {
                    break Label_0167;
                }
                return false;
            }
        }
        MorphInfo info = Morph.proxy.tickHandlerServer.getPlayerMorphInfo((EntityPlayer)player);
        if ((!living.writeToNBTOptional(new NBTTagCompound()) || living instanceof EntityPlayer) && !(living instanceof EntityPlayer)) {
            return false;
        }
        if (info == null) {
            info = new MorphInfo(player.getCommandSenderName(), null, Morph.proxy.tickHandlerServer.getSelfState(player.worldObj, (EntityPlayer)player));
        }
        else if (info.getMorphing() || info.nextState.entInstance == living) {
            return false;
        }
        final byte isPlayer = (byte)((info.nextState.entInstance instanceof EntityPlayer && living instanceof EntityPlayer) ? 3 : ((info.nextState.entInstance instanceof EntityPlayer) ? 1 : ((living instanceof EntityPlayer) ? 2 : 0)));
        if (!(info.nextState.entInstance instanceof EntityPlayer) && !info.nextState.entInstance.writeToNBTOptional(new NBTTagCompound())) {
            return false;
        }
        final String username1 = (isPlayer == 1 || isPlayer == 3) ? ((EntityPlayer)info.nextState.entInstance).getCommandSenderName() : "";
        final String username2 = (isPlayer == 2 || isPlayer == 3) ? ((EntityPlayer)living).getCommandSenderName() : "";
        final NBTTagCompound prevTag = new NBTTagCompound();
        final NBTTagCompound nextTag = new NBTTagCompound();
        info.nextState.entInstance.writeToNBTOptional(prevTag);
        living.writeToNBTOptional(nextTag);
        MorphState prevState = new MorphState(player.worldObj, player.getCommandSenderName(), username1, prevTag, false);
        MorphState nextState = new MorphState(player.worldObj, player.getCommandSenderName(), username2, nextTag, false);
        if (Morph.proxy.tickHandlerServer.hasMorphState((EntityPlayer)player, nextState) || (!forced && MinecraftForge.EVENT_BUS.post((Event)new MorphAcquiredEvent((EntityPlayer)player, nextState.entInstance)))) {
            return false;
        }
        prevState = MorphHandler.addOrGetMorphState(Morph.proxy.tickHandlerServer.getPlayerMorphs(player.worldObj, (EntityPlayer)player), prevState);
        nextState = MorphHandler.addOrGetMorphState(Morph.proxy.tickHandlerServer.getPlayerMorphs(player.worldObj, (EntityPlayer)player), nextState);
        if (nextState.identifier.equalsIgnoreCase(info.nextState.identifier)) {
            return false;
        }
        if (Morph.config.getInt("instaMorph") == 1 || forced) {
            final MorphInfo info2 = new MorphInfo(player.getCommandSenderName(), prevState, nextState);
            info2.setMorphing(true);
            info2.healthOffset = player.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue() - 20.0;
            info2.preMorphHealth = player.getHealth();
            final MorphInfo info3 = Morph.proxy.tickHandlerServer.getPlayerMorphInfo((EntityPlayer)player);
            if (info3 != null) {
                info2.morphAbilities = info3.morphAbilities;
                info2.healthOffset = info3.healthOffset;
            }
            if (!MinecraftForge.EVENT_BUS.post((Event)new MorphEvent((EntityPlayer)player, info2.prevState.entInstance, info2.nextState.entInstance))) {
                Morph.proxy.tickHandlerServer.setPlayerMorphInfo((EntityPlayer)player, info2);
                PacketHandler.sendToAll((EnumMap)Morph.channels, (AbstractPacket)info2.getMorphInfoAsPacket());
                player.worldObj.playSoundAtEntity((Entity)player, "morph:morph", 1.0f, 1.0f);
            }
        }
        if (kill) {
            PacketHandler.sendToDimension((EnumMap)Morph.channels, (AbstractPacket)new PacketMorphAcquisition(living.getEntityId(), player.getEntityId()), player.dimension);
        }
        MorphHandler.updatePlayerOfMorphStates(player, nextState, false);
        return true;
    }
    
    public static boolean demorphPlayer(final EntityPlayerMP player) {
        final MorphInfo info = Morph.proxy.tickHandlerServer.getPlayerMorphInfo((EntityPlayer)player);
        final MorphState state2 = Morph.proxy.tickHandlerServer.getSelfState(player.worldObj, (EntityPlayer)player);
        if (info != null) {
            final MorphState state3 = info.nextState;
            final MorphInfo info2 = new MorphInfo(player.getCommandSenderName(), state3, state2);
            info2.setMorphing(true);
            info2.preMorphHealth = player.getHealth();
            final MorphInfo info3 = Morph.proxy.tickHandlerServer.getPlayerMorphInfo((EntityPlayer)player);
            if (info3 != null) {
                info2.morphAbilities = info3.morphAbilities;
                info2.healthOffset = info3.healthOffset;
            }
            Morph.proxy.tickHandlerServer.setPlayerMorphInfo((EntityPlayer)player, info2);
            PacketHandler.sendToAll((EnumMap)Morph.channels, (AbstractPacket)info2.getMorphInfoAsPacket());
            player.worldObj.playSoundAtEntity((Entity)player, "morph:morph", 1.0f, 1.0f);
            return true;
        }
        return false;
    }
}
