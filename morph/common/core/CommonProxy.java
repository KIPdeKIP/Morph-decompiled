package morph.common.core;

import morph.client.core.*;
import morph.common.*;
import morph.common.packet.*;
import ichun.common.core.network.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import morph.common.thread.*;
import cpw.mods.fml.common.*;
import net.minecraft.server.*;
import net.minecraft.command.*;
import java.util.*;
import cpw.mods.fml.relauncher.*;
import cpw.mods.fml.common.network.*;

public class CommonProxy
{
    public TickHandlerClient tickHandlerClient;
    public TickHandlerServer tickHandlerServer;
    public ArrayList<Class> compatibleEntities;
    
    public CommonProxy() {
        this.compatibleEntities = new ArrayList<Class>();
    }
    
    public void initMod() {
        Morph.parseBlacklist(Morph.config.getString("blacklistedMobs"));
        Morph.parsePlayerList(Morph.config.getString("blackwhitelistedPlayers"));
        Morph.channels = (EnumMap<Side, FMLEmbeddedChannel>)ChannelHandler.getChannelHandlers("Morph", new Class[] { PacketGuiInput.class, PacketMorphInfo.class, PacketSession.class, PacketMorphAcquisition.class, PacketCompleteDemorph.class, PacketMorphStates.class });
    }
    
    public void initPostMod() {
        for (final Map.Entry e : EntityList.classToStringMapping.entrySet()) {
            final Class clz = e.getKey();
            if (EntityLivingBase.class.isAssignableFrom(clz) && !this.compatibleEntities.contains(clz)) {
                this.compatibleEntities.add(clz);
            }
        }
        this.compatibleEntities.add(EntityPlayer.class);
        new ThreadGetOnlineResources(Morph.config.getString("customPatchLink")).start();
    }
    
    public void initTickHandlers() {
        this.tickHandlerServer = new TickHandlerServer();
        FMLCommonHandler.instance().bus().register((Object)this.tickHandlerServer);
    }
    
    public void initCommands(final MinecraftServer server) {
        final ICommandManager manager = server.getCommandManager();
        if (manager instanceof CommandHandler) {
            final CommandHandler handler = (CommandHandler)manager;
            handler.registerCommand((ICommand)new CommandMorph());
        }
    }
}
