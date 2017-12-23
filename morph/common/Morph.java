package morph.common;

import cpw.mods.fml.relauncher.*;
import cpw.mods.fml.common.network.*;
import java.io.*;
import java.util.*;
import net.minecraft.entity.*;
import net.minecraftforge.common.config.*;
import cpw.mods.fml.common.*;
import net.minecraft.entity.player.*;
import ichun.common.core.config.*;
import ichun.common.*;
import net.minecraft.client.*;
import ichun.client.keybind.*;
import morph.common.core.*;
import net.minecraftforge.common.*;
import ichun.common.core.updateChecker.*;
import cpw.mods.fml.common.event.*;
import org.apache.logging.log4j.*;

@Mod(modid = "Morph", name = "Morph", version = "0.9.2", dependencies = "required-after:iChunUtil@[4.0.0,)", acceptableRemoteVersions = "[0.9.0,0.10.0)")
public class Morph implements IConfigUser
{
    public static final String version = "0.9.2";
    @Mod.Instance("Morph")
    public static Morph instance;
    @SidedProxy(clientSide = "morph.client.core.ClientProxy", serverSide = "morph.common.core.CommonProxy")
    public static CommonProxy proxy;
    private static Logger logger;
    public static EnumMap<Side, FMLEmbeddedChannel> channels;
    public static Config config;
    public static File configFolder;
    public static ArrayList<Class<? extends EntityLivingBase>> blacklistedClasses;
    public static ArrayList<String> playerList;
    public static Class<? extends EntityLivingBase> classToKillForFlight;
    
    public boolean onConfigChange(final Config cfg, final Property prop) {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            if (prop.getName().equalsIgnoreCase("blacklistedMobs")) {
                parseBlacklist(prop.getString());
            }
            if (prop.getName().equalsIgnoreCase("blackwhitelistedPlayers")) {
                parsePlayerList(prop.getString());
            }
            if (prop.getName().equalsIgnoreCase("abilities") || prop.getName().equalsIgnoreCase("canSleepMorphed") || prop.getName().equalsIgnoreCase("allowMorphSelection") || prop.getName().equalsIgnoreCase("showPlayerLabel") || prop.getName().equalsIgnoreCase("disabledAbilities")) {
                Morph.proxy.tickHandlerServer.updateSession(null);
            }
        }
        else {
            Morph.proxy.tickHandlerServer.purgeSession = true;
        }
        return true;
    }
    
    @Mod.EventHandler
    public void preLoad(final FMLPreInitializationEvent event) {
        Morph.configFolder = event.getModConfigurationDirectory();
        (Morph.config = ConfigHandler.createConfig(event.getSuggestedConfigurationFile(), "morph", "Morph", Morph.logger, (IConfigUser)Morph.instance)).setCurrentCategory("gameplay", "morph.config.cat.gameplay.name", "morph.config.cat.gameplay.comment");
        Morph.config.createIntBoolProperty("childMorphs", "morph.config.prop.childMorphs.name", "morph.config.prop.childMorphs.comment", true, false, false);
        Morph.config.createIntBoolProperty("playerMorphs", "morph.config.prop.playerMorphs.name", "morph.config.prop.playerMorphs.comment", true, false, true);
        Morph.config.createIntBoolProperty("bossMorphs", "morph.config.prop.bossMorphs.name", "morph.config.prop.bossMorphs.comment", true, false, false);
        Morph.config.createStringProperty("blacklistedMobs", "morph.config.prop.blacklistedMobs.name", "morph.config.prop.blacklistedMobs.comment", true, false, "");
        Morph.config.createStringProperty("blackwhitelistedPlayers", "morph.config.prop.blackwhitelistedPlayers.name", "morph.config.prop.blackwhitelistedPlayers.comment", true, false, "");
        Morph.config.createIntBoolProperty("listIsBlacklist", "morph.config.prop.listIsBlacklist.name", "morph.config.prop.listIsBlacklist.comment", true, false, false);
        Morph.config.createIntProperty("loseMorphsOnDeath", "morph.config.prop.loseMorphsOnDeath.name", "morph.config.prop.loseMorphsOnDeath.comment", true, false, 0, 0, 2);
        Morph.config.createIntBoolProperty("instaMorph", "morph.config.prop.instaMorph.name", "morph.config.prop.instaMorph.comment", true, false, false);
        Morph.config.createIntBoolProperty("NBTStripper", "morph.config.prop.NBTStripper.name", "morph.config.prop.NBTStripper.comment", false, false, true);
        Morph.config.createIntBoolProperty("useLocalResources", "morph.config.prop.useLocalResources.name", "morph.config.prop.useLocalResources.comment", false, false, false);
        Morph.config.createIntBoolProperty("canSleepMorphed", "morph.config.prop.canSleepMorphed.name", "morph.config.prop.canSleepMorphed.comment", true, true, false);
        Morph.config.createIntBoolProperty("allowMorphSelection", "morph.config.prop.allowMorphSelection.name", "morph.config.prop.allowMorphSelection.comment", true, true, true);
        Morph.config.createIntBoolProperty("showPlayerLabel", "morph.config.prop.showPlayerLabel.name", "morph.config.prop.showPlayerLabel.comment", true, true, false);
        Morph.config.createIntBoolProperty("forceSizeWhenMorphed", "morph.config.prop.forceSizeWhenMorphed.name", "morph.config.prop.forceSizeWhenMorphed.comment", true, true, true);
        Morph.config.setCurrentCategory("abilities", "morph.config.cat.abilities.name", "morph.config.cat.abilities.comment");
        Morph.config.createIntBoolProperty("abilities", "morph.config.prop.abilities.name", "morph.config.prop.abilities.comment", false, true, true);
        Morph.config.createStringProperty("customPatchLink", "morph.config.prop.customPatchLink.name", "morph.config.prop.customPatchLink.comment", false, false, "");
        Morph.config.createIntProperty("hostileAbilityMode", "morph.config.prop.hostileAbilityMode.name", "morph.config.prop.hostileAbilityMode.comment", true, false, 0, 0, 4);
        Morph.config.createIntProperty("hostileAbilityDistanceCheck", "morph.config.prop.hostileAbilityDistanceCheck.name", "morph.config.prop.hostileAbilityDistanceCheck.comment", true, false, 6, 0, 128);
        Morph.config.createIntProperty("disableEarlyGameFlight", "morph.config.prop.disableEarlyGameFlight.name", "morph.config.prop.disableEarlyGameFlight.comment", true, false, 0, 0, 2);
        Morph.config.createIntBoolProperty("disableEarlyGameFlightMode", "morph.config.prop.disableEarlyGameFlightMode.name", "morph.config.prop.disableEarlyGameFlightMode.comment", true, false, false);
        Morph.config.createStringProperty("customMobToKillForFlight", "morph.config.prop.customMobToKillForFlight.name", "morph.config.prop.customMobToKillForFlight.comment", false, false, "");
        Morph.config.createStringProperty("disabledAbilities", "morph.config.prop.disabledAbilities.name", "morph.config.prop.disabledAbilities.comment", false, true, "");
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            Morph.config.setCurrentCategory("clientOnly", "morph.config.cat.clientOnly.name", "morph.config.cat.clientOnly.comment");
            Morph.config.createKeybindProperty("keySelectorUp", "morph.config.prop.keySelectorUp.name", "morph.config.prop.keySelectorUp.comment", 26, false, false, false, false, 0, false);
            Morph.config.createKeybindProperty("keySelectorDown", "morph.config.prop.keySelectorDown.name", "morph.config.prop.keySelectorDown.comment", 27, false, false, false, false, 0, false);
            Morph.config.createKeybindProperty("keySelectorLeft", "morph.config.prop.keySelectorLeft.name", "morph.config.prop.keySelectorLeft.comment", 26, true, false, false, false, 0, false);
            Morph.config.createKeybindProperty("keySelectorRight", "morph.config.prop.keySelectorRight.name", "morph.config.prop.keySelectorRight.comment", 27, true, false, false, false, 0, false);
            iChunUtil.proxy.registerMinecraftKeyBind(Minecraft.getMinecraft().gameSettings.keyBindAttack);
            iChunUtil.proxy.registerMinecraftKeyBind(Minecraft.getMinecraft().gameSettings.keyBindUseItem);
            iChunUtil.proxy.registerKeyBind(new KeyBind(211, false, false, false, false), (KeyBind)null);
            Morph.config.createKeybindProperty("keySelectorSelect", "morph.config.prop.keySelectorSelect.name", "morph.config.prop.keySelectorSelect.comment", 28, false, false, false, false, 0, false);
            Morph.config.createKeybindProperty("keySelectorCancel", "morph.config.prop.keySelectorCancel.name", "morph.config.prop.keySelectorCancel.comment", 1, false, false, false, false, 0, false);
            Morph.config.createKeybindProperty("keySelectorRemoveMorph", "morph.config.prop.keySelectorRemoveMorph.name", "morph.config.prop.keySelectorRemoveMorph.comment", 14, false, false, false, false, 0, false);
            Morph.config.createKeybindProperty("keyFavourite", "morph.config.prop.keyFavourite.name", "morph.config.prop.keyFavourite.comment", 41, false, false, false, false, 0, false);
            Morph.config.createIntBoolProperty("handRenderOverride", "morph.config.prop.handRenderOverride.name", "morph.config.prop.handRenderOverride.comment", true, false, true);
            Morph.config.createIntBoolProperty("showAbilitiesInGui", "morph.config.prop.showAbilitiesInGui.name", "morph.config.prop.showAbilitiesInGui.comment", true, true, true);
            Morph.config.createIntProperty("sortMorphs", "morph.config.prop.sortMorphs.name", "morph.config.prop.sortMorphs.comment", true, false, 0, 0, 3);
            Morph.config.createIntBoolProperty("renderCrosshairInRadialMenu", "morph.config.prop.renderCrosshairInRadialMenu.name", "morph.config.prop.renderCrosshairInRadialMenu.comment", true, false, false);
        }
        if (!Morph.config.getString("customMobToKillForFlight").isEmpty()) {
            try {
                final Class clz = Class.forName(Morph.config.getString("customMobToKillForFlight"));
                if (EntityLivingBase.class.isAssignableFrom(clz)) {
                    Morph.classToKillForFlight = (Class<? extends EntityLivingBase>)clz;
                    console(clz.getName() + " was mapped for a custom mob to be killed for flight.", false);
                }
                else {
                    console(clz.getName() + " was mapped for a custom mob to be killed for flight but the class is not of an EntityLivingBase type!", true);
                }
            }
            catch (ClassNotFoundException e) {
                console("A class was mapped for a custom mob to be killed for flight but the class was not found!", true);
            }
        }
        final EventHandler eventHandler = new EventHandler();
        MinecraftForge.EVENT_BUS.register((Object)eventHandler);
        FMLCommonHandler.instance().bus().register((Object)eventHandler);
        ModVersionChecker.register_iChunMod(new ModVersionInfo("Morph", "1.7.10", "0.9.2", false));
    }
    
    @Mod.EventHandler
    public void load(final FMLInitializationEvent event) {
        Morph.proxy.initTickHandlers();
        Morph.proxy.initMod();
    }
    
    @Mod.EventHandler
    public void postLoad(final FMLPostInitializationEvent event) {
        Morph.proxy.initPostMod();
    }
    
    @Mod.EventHandler
    public void serverStarting(final FMLServerAboutToStartEvent event) {
        Morph.config.resetSession();
        Morph.config.updateSession("allowFlight", (Object)1);
        Morph.proxy.tickHandlerServer.purgeSession = false;
        Morph.proxy.initCommands(event.getServer());
    }
    
    @Mod.EventHandler
    public void serverStarted(final FMLServerStartedEvent event) {
    }
    
    @Mod.EventHandler
    public void serverStopped(final FMLServerStoppedEvent event) {
        Morph.proxy.tickHandlerServer.saveData = null;
        Morph.proxy.tickHandlerServer.playerMorphInfo.clear();
        Morph.proxy.tickHandlerServer.playerMorphs.clear();
    }
    
    public static void parseBlacklist(final String s) {
        Morph.blacklistedClasses.clear();
        final String[] split;
        final String[] classes = split = s.split(", *");
        for (final String className : split) {
            if (!className.trim().isEmpty()) {
                try {
                    final Class clz = Class.forName(className.trim());
                    if (EntityLivingBase.class.isAssignableFrom(clz) && !Morph.blacklistedClasses.contains(clz)) {
                        Morph.blacklistedClasses.add(clz);
                        console("Blacklisting class: " + clz.getName(), false);
                    }
                }
                catch (Exception e) {
                    console("Could not find class to blacklist: " + className.trim(), true);
                }
            }
        }
    }
    
    public static void parsePlayerList(final String s) {
        Morph.playerList.clear();
        final String[] names = s.split(", *");
        boolean added = false;
        for (final String playerName : names) {
            if (!playerName.trim().isEmpty()) {
                added = true;
                if (!Morph.playerList.contains(playerName.trim())) {
                    Morph.playerList.add(playerName.trim());
                }
            }
        }
        if (!Morph.playerList.isEmpty()) {
            final StringBuilder sb = new StringBuilder((Morph.config.getInt("listIsBlacklist") == 0) ? "Blacklisted players: " : "Whitelisted players: ");
            for (int i = 0; i < Morph.playerList.size(); ++i) {
                sb.append(Morph.playerList.get(i));
                if (i < Morph.playerList.size() - 1) {
                    sb.append(", ");
                }
            }
            console(sb.toString(), false);
        }
    }
    
    public static void console(final String s, final boolean warning) {
        final StringBuilder sb = new StringBuilder();
        Morph.logger.log(warning ? Level.WARN : Level.INFO, sb.append("[").append("0.9.2").append("] ").append(s).toString());
    }
    
    static {
        Morph.logger = LogManager.getLogger("Morph");
        Morph.blacklistedClasses = new ArrayList<Class<? extends EntityLivingBase>>();
        Morph.playerList = new ArrayList<String>();
        Morph.classToKillForFlight = null;
    }
}
