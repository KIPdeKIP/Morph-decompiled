package morph.common.core;

import net.minecraft.server.*;
import cpw.mods.fml.common.*;
import net.minecraftforge.common.*;
import ichun.common.core.*;
import net.minecraft.server.management.*;
import net.minecraft.world.*;
import morph.common.*;
import net.minecraft.entity.player.*;
import ichun.common.core.network.*;
import morph.common.morph.*;
import net.minecraft.entity.*;
import net.minecraft.command.*;
import net.minecraft.util.*;
import java.util.*;

public class CommandMorph extends CommandBase
{
    public String getCommandName() {
        return "morph";
    }
    
    public String getCommandUsage(final ICommandSender icommandsender) {
        return "/" + this.getCommandName() + " help";
    }
    
    public void processCommand(final ICommandSender icommandsender, final String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("help")) {
                icommandsender.addChatMessage(new ChatComponentTranslation("morph.command.demorph", new Object[0]).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GRAY)));
                icommandsender.addChatMessage(new ChatComponentTranslation("morph.command.clear", new Object[0]).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GRAY)));
                icommandsender.addChatMessage(new ChatComponentTranslation("morph.command.morphtarget", new Object[0]).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GRAY)));
                icommandsender.addChatMessage(new ChatComponentTranslation("morph.command.addtolist", new Object[0]).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GRAY)));
                icommandsender.addChatMessage(new ChatComponentTranslation("morph.command.removefromlist", new Object[0]).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GRAY)));
            }
            else if (args[0].equalsIgnoreCase("demorph")) {
                EntityPlayerMP player;
                if (args.length > 1) {
                    player = PlayerSelector.matchOnePlayer(icommandsender, args[1]);
                }
                else {
                    player = getCommandSenderAsPlayer(icommandsender);
                }
                if (player == null) {
                    player = MinecraftServer.getServer().getConfigurationManager().func_152612_a(args[1]);
                }
                if (player != null) {
                    if (EntityHelper.demorphPlayer(player)) {
                        func_152373_a(icommandsender, (ICommand)this, "morph.command.forcingDemorph", new Object[] { player.getCommandSenderName() });
                    }
                    else {
                        icommandsender.addChatMessage((IChatComponent)new ChatComponentTranslation("morph.command.notInMorph", new Object[] { player.getCommandSenderName() }));
                    }
                }
                else if (args.length > 2 && args[2].equalsIgnoreCase("true")) {
                    try {
                        final EntityPlayerMP player2 = new EntityPlayerMP(FMLCommonHandler.instance().getMinecraftServerInstance(), DimensionManager.getWorld(0), EntityHelperBase.getSimpleGameProfileFromName(args[1]), new ItemInWorldManager((World)DimensionManager.getWorld(0)));
                        FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().readPlayerDataFromFile(player2);
                        if (Morph.proxy.tickHandlerServer.getMorphDataFromPlayer((EntityPlayer)player2).hasKey("morphData")) {
                            final MorphState state = Morph.proxy.tickHandlerServer.getSelfState((World)DimensionManager.getWorld(0), (EntityPlayer)player2);
                            final MorphInfo info = new MorphInfo(args[1], state, state);
                            info.morphProgress = 80;
                            info.healthOffset = Morph.proxy.tickHandlerServer.getMorphDataFromPlayer((EntityPlayer)player2).getDouble("healthOffset");
                            Morph.proxy.tickHandlerServer.getMorphDataFromPlayer((EntityPlayer)player2).removeTag("morphData");
                            PacketHandler.sendToAll((EnumMap)Morph.channels, (AbstractPacket)info.getMorphInfoAsPacket());
                            Morph.proxy.tickHandlerServer.setPlayerMorphInfo((EntityPlayer)player2, null);
                            func_152373_a(icommandsender, (ICommand)this, "morph.command.forcingDemorph", new Object[] { args[1] });
                            FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList.add(player2);
                            FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().saveAllPlayerData();
                            FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList.remove(player2);
                        }
                        else {
                            icommandsender.addChatMessage((IChatComponent)new ChatComponentTranslation("morph.command.noMorphData", new Object[] { args[1] }));
                        }
                    }
                    catch (Exception e) {
                        icommandsender.addChatMessage((IChatComponent)new ChatComponentTranslation("morph.command.cannotReadMorphData", new Object[] { args[1] }));
                    }
                }
                else {
                    icommandsender.addChatMessage((IChatComponent)new ChatComponentTranslation("morph.command.notOnline", new Object[] { args[1] }));
                }
            }
            else if (args[0].equalsIgnoreCase("clear")) {
                EntityPlayer player3;
                if (args.length > 1) {
                    player3 = (EntityPlayer)PlayerSelector.matchOnePlayer(icommandsender, args[1]);
                }
                else {
                    player3 = (EntityPlayer)getCommandSenderAsPlayer(icommandsender);
                }
                if (player3 == null) {
                    player3 = (EntityPlayer)MinecraftServer.getServer().getConfigurationManager().func_152612_a(args[1]);
                }
                if (player3 != null) {
                    final MorphInfo info2 = Morph.proxy.tickHandlerServer.getPlayerMorphInfo(player3);
                    final MorphState state2 = Morph.proxy.tickHandlerServer.getSelfState(player3.worldObj, player3);
                    if (info2 != null) {
                        final MorphState state3 = info2.nextState;
                        final MorphInfo info3 = new MorphInfo(player3.getCommandSenderName(), state3, state2);
                        info3.setMorphing(true);
                        info3.healthOffset = info2.healthOffset;
                        info3.preMorphHealth = player3.getHealth();
                        Morph.proxy.tickHandlerServer.setPlayerMorphInfo(player3, info3);
                        PacketHandler.sendToAll((EnumMap)Morph.channels, (AbstractPacket)info3.getMorphInfoAsPacket());
                        player3.worldObj.playSoundAtEntity((Entity)player3, "morph:morph", 1.0f, 1.0f);
                    }
                    Morph.proxy.tickHandlerServer.removeAllPlayerMorphsExcludingCurrentMorph(player3);
                    MorphHandler.updatePlayerOfMorphStates((EntityPlayerMP)player3, null, true);
                    func_152373_a(icommandsender, (ICommand)this, "morph.command.clearingMorphs", new Object[] { args[1] });
                }
                else {
                    try {
                        final EntityPlayerMP player2 = new EntityPlayerMP(FMLCommonHandler.instance().getMinecraftServerInstance(), DimensionManager.getWorld(0), EntityHelperBase.getSimpleGameProfileFromName(args[1]), new ItemInWorldManager((World)DimensionManager.getWorld(0)));
                        FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().readPlayerDataFromFile(player2);
                        if (Morph.proxy.tickHandlerServer.getMorphDataFromPlayer((EntityPlayer)player2).hasKey("morphData")) {
                            final MorphState state = Morph.proxy.tickHandlerServer.getSelfState((World)DimensionManager.getWorld(0), (EntityPlayer)player2);
                            final MorphInfo info = new MorphInfo(args[1], state, state);
                            info.morphProgress = 80;
                            info.healthOffset = Morph.proxy.tickHandlerServer.getMorphDataFromPlayer((EntityPlayer)player2).getDouble("healthOffset");
                            Morph.proxy.tickHandlerServer.getMorphDataFromPlayer((EntityPlayer)player2).removeTag("morphData");
                            PacketHandler.sendToAll((EnumMap)Morph.channels, (AbstractPacket)info.getMorphInfoAsPacket());
                            Morph.proxy.tickHandlerServer.setPlayerMorphInfo((EntityPlayer)player2, null);
                        }
                        Morph.proxy.tickHandlerServer.removeAllPlayerMorphsExcludingCurrentMorph((EntityPlayer)player2);
                        if (Morph.proxy.tickHandlerServer.getMorphDataFromPlayer((EntityPlayer)player2).hasKey("morphStatesCount")) {
                            Morph.proxy.tickHandlerServer.getMorphDataFromPlayer((EntityPlayer)player2).removeTag("morphStatesCount");
                        }
                        func_152373_a(icommandsender, (ICommand)this, "morph.command.clearingMorphs", new Object[] { args[1] });
                        FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList.add(player2);
                        FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().saveAllPlayerData();
                        FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList.remove(player2);
                    }
                    catch (Exception e) {
                        icommandsender.addChatMessage((IChatComponent)new ChatComponentTranslation("morph.command.failToClear", new Object[] { args[1] }));
                    }
                }
            }
            else if (args[0].equalsIgnoreCase("morphtarget")) {
                EntityPlayerMP player;
                if (args.length > 1) {
                    player = PlayerSelector.matchOnePlayer(icommandsender, args[1]);
                }
                else {
                    player = getCommandSenderAsPlayer(icommandsender);
                }
                if (player == null) {
                    player = MinecraftServer.getServer().getConfigurationManager().func_152612_a(args[1]);
                }
                if (player != null) {
                    final MovingObjectPosition mop = EntityHelper.getEntityLook((EntityLivingBase)player, 4.0, false, 1.0f);
                    if (mop != null && mop.entityHit != null && mop.entityHit instanceof EntityLivingBase) {
                        EntityLivingBase living = (EntityLivingBase)mop.entityHit;
                        if (living instanceof EntityPlayerMP) {
                            final EntityPlayerMP player4 = (EntityPlayerMP)living;
                            final MorphInfo info4 = Morph.proxy.tickHandlerServer.getPlayerMorphInfo((EntityPlayer)player4);
                            if (info4 != null) {
                                if (info4.getMorphing()) {
                                    living = info4.prevState.entInstance;
                                }
                                else {
                                    living = info4.nextState.entInstance;
                                }
                            }
                        }
                        if (!EntityHelper.morphPlayer(player, living, false, true)) {
                            icommandsender.addChatMessage((IChatComponent)new ChatComponentTranslation("morph.command.notLookingAtMorphable", new Object[] { player.getCommandSenderName() }));
                        }
                        else {
                            func_152373_a(icommandsender, (ICommand)this, "morph.command.forcingMorphTarget", new Object[] { player.getCommandSenderName() });
                        }
                    }
                    else {
                        icommandsender.addChatMessage((IChatComponent)new ChatComponentTranslation("morph.command.notLookingAtMorphable", new Object[] { player.getCommandSenderName() }));
                    }
                }
                else if (args.length > 1) {
                    icommandsender.addChatMessage((IChatComponent)new ChatComponentTranslation("morph.command.cannotFindPlayer", new Object[] { args[1] }));
                }
            }
            else if (args[0].equalsIgnoreCase("addtolist")) {
                if (args.length == 1) {
                    icommandsender.addChatMessage(new ChatComponentTranslation("morph.command.addtolist", new Object[0]).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GRAY)));
                }
                else {
                    final StringBuilder sb = new StringBuilder();
                    for (int i = 1; i < args.length; ++i) {
                        sb.append(args[i]);
                        if (i < args.length - 1) {
                            sb.append(" ");
                        }
                    }
                    if (!Morph.playerList.contains(sb.toString().trim())) {
                        func_152373_a(icommandsender, (ICommand)this, "morph.command.addingToBlackWhite", new Object[] { sb.toString().trim() });
                        Morph.playerList.add(sb.toString().trim());
                        final StringBuilder sb2 = new StringBuilder();
                        for (int j = 0; j < Morph.playerList.size(); ++j) {
                            sb2.append(Morph.playerList.get(j));
                            if (j < Morph.playerList.size() - 1) {
                                sb2.append(", ");
                            }
                        }
                        Morph.config.get("blackwhitelistedPlayers").set(sb2.toString());
                        Morph.config.save();
                    }
                    else {
                        icommandsender.addChatMessage((IChatComponent)new ChatComponentTranslation("morph.command.alreadyInList", new Object[0]));
                    }
                }
            }
            else if (args[0].equalsIgnoreCase("removefromlist")) {
                if (args.length == 1) {
                    icommandsender.addChatMessage(new ChatComponentTranslation("morph.command.removefromlist", new Object[0]).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GRAY)));
                }
                else {
                    final StringBuilder sb = new StringBuilder();
                    for (int i = 1; i < args.length; ++i) {
                        sb.append(args[i]);
                        if (i < args.length - 1) {
                            sb.append(" ");
                        }
                    }
                    if (Morph.playerList.contains(sb.toString().trim())) {
                        func_152373_a(icommandsender, (ICommand)this, "morph.command.removingFromBlackWhite", new Object[] { sb.toString().trim() });
                        Morph.playerList.remove(sb.toString().trim());
                        final StringBuilder sb2 = new StringBuilder();
                        for (int j = 0; j < Morph.playerList.size(); ++j) {
                            sb2.append(Morph.playerList.get(j));
                            if (j < Morph.playerList.size() - 1) {
                                sb2.append(", ");
                            }
                        }
                        Morph.config.get("blackwhitelistedPlayers").set(sb2.toString());
                        Morph.config.save();
                    }
                    else {
                        icommandsender.addChatMessage((IChatComponent)new ChatComponentTranslation("morph.command.notInList", new Object[0]));
                    }
                }
            }
            return;
        }
        throw new WrongUsageException(this.getCommandUsage(icommandsender), new Object[0]);
    }
    
    public List addTabCompletionOptions(final ICommandSender ics, final String[] args) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, new String[] { "demorph", "clear", "morphtarget", "addtolist", "removefromlist" });
        }
        if (args.length == 2) {
            return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("demorph")) {
            final ArrayList list = new ArrayList();
            list.add("true");
            return list;
        }
        return null;
    }
}
