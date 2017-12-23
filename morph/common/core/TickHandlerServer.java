package morph.common.core;

import morph.common.morph.*;
import net.minecraft.entity.player.*;
import cpw.mods.fml.common.gameevent.*;
import cpw.mods.fml.common.eventhandler.*;
import cpw.mods.fml.common.*;
import net.minecraft.util.*;
import morph.api.*;
import net.minecraft.entity.*;
import morph.common.*;
import ichun.common.core.network.*;
import morph.common.ability.*;
import java.util.*;
import net.minecraft.nbt.*;
import net.minecraft.world.*;
import morph.common.packet.*;
import ichun.common.core.*;

public class TickHandlerServer
{
    public long clock;
    public boolean purgeSession;
    public MorphSaveData saveData;
    public HashMap<String, MorphInfo> playerMorphInfo;
    public HashMap<String, ArrayList<MorphState>> playerMorphs;
    public WeakHashMap<EntityPlayer, ArrayList<MorphState>> saveList;
    
    public TickHandlerServer() {
        this.saveData = null;
        this.playerMorphInfo = new HashMap<String, MorphInfo>();
        this.playerMorphs = new HashMap<String, ArrayList<MorphState>>();
        this.saveList = new WeakHashMap<EntityPlayer, ArrayList<MorphState>>();
    }
    
    @SubscribeEvent
    public void worldTick(final TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.side.isServer()) {
            final WorldServer world = (WorldServer)event.world;
            if (this.clock != world.getWorldTime() || !world.getGameRules().getGameRuleBooleanValue("doDaylightCycle")) {
                this.clock = world.getWorldTime();
            }
        }
    }
    
    @SubscribeEvent
    public void playerTick(final TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.side.isServer()) {
            final EntityPlayer player = event.player;
            final MorphInfo info = this.getPlayerMorphInfo(player);
            if (info != null) {
                float prog = (info.morphProgress > 10) ? (info.morphProgress / 60.0f) : 0.0f;
                if (prog > 1.0f) {
                    prog = 1.0f;
                }
                prog = (float)Math.pow(prog, 2.0);
                final float prev = (info.prevState != null && !(info.prevState.entInstance instanceof EntityPlayer)) ? info.prevState.entInstance.getEyeHeight() : player.yOffset;
                final float next = (info.nextState != null && !(info.nextState.entInstance instanceof EntityPlayer)) ? info.nextState.entInstance.getEyeHeight() : player.yOffset;
                final double ySize = player.yOffset - (prev + (next - prev) * prog);
                final EntityPlayer entityPlayer = player;
                entityPlayer.lastTickPosY += ySize;
                final EntityPlayer entityPlayer2 = player;
                entityPlayer2.prevPosY += ySize;
                final EntityPlayer entityPlayer3 = player;
                entityPlayer3.posY += ySize;
            }
        }
    }
    
    @SubscribeEvent
    public void serverTick(final TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            final Iterator<Map.Entry<String, MorphInfo>> ite = this.playerMorphInfo.entrySet().iterator();
            while (ite.hasNext()) {
                final Map.Entry<String, MorphInfo> e = ite.next();
                final MorphInfo info = e.getValue();
                final EntityPlayer player = (EntityPlayer)FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().func_152612_a(info.playerName);
                if (info.getMorphing()) {
                    final MorphInfo morphInfo = info;
                    ++morphInfo.morphProgress;
                    if (info.morphProgress > 80) {
                        info.morphProgress = 80;
                        info.setMorphing(false);
                        if (player != null) {
                            player.setSize(info.nextState.entInstance.width, info.nextState.entInstance.height);
                            player.setPosition(player.posX, player.posY, player.posZ);
                            player.eyeHeight = ((info.nextState.entInstance instanceof EntityPlayer) ? ((EntityPlayer)info.nextState.entInstance).getDefaultEyeHeight() : (info.nextState.entInstance.getEyeHeight() - player.yOffset));
                            double nextMaxHealth = MathHelper.clamp_double(info.nextState.entInstance.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue(), 1.0, 20.0) + info.healthOffset;
                            if (nextMaxHealth < 1.0) {
                                nextMaxHealth = 1.0;
                            }
                            if (nextMaxHealth != player.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue()) {
                                player.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(nextMaxHealth);
                            }
                            final ArrayList<Ability> newAbilities = AbilityHandler.getEntityAbilities(info.nextState.entInstance.getClass());
                            final ArrayList<Ability> oldAbilities = info.morphAbilities;
                            info.morphAbilities = new ArrayList<Ability>();
                            for (final Ability ability : newAbilities) {
                                try {
                                    final Ability clone = ability.clone();
                                    clone.setParent((EntityLivingBase)player);
                                    info.morphAbilities.add(clone);
                                }
                                catch (Exception ex) {}
                            }
                            for (final Ability ability : oldAbilities) {
                                if (ability.inactive) {
                                    continue;
                                }
                                boolean isRemoved = true;
                                for (final Ability newAbility : info.morphAbilities) {
                                    if (newAbility.getType().equalsIgnoreCase(ability.getType())) {
                                        isRemoved = false;
                                        break;
                                    }
                                }
                                if (!isRemoved || ability.getParent() == null) {
                                    continue;
                                }
                                ability.kill();
                            }
                        }
                        if (info.nextState.playerMorph.equalsIgnoreCase(e.getKey())) {
                            PacketHandler.sendToAll((EnumMap)Morph.channels, (AbstractPacket)new PacketCompleteDemorph(e.getKey()));
                            for (final Ability ability2 : info.morphAbilities) {
                                if (ability2.inactive) {
                                    continue;
                                }
                                if (ability2.getParent() == null) {
                                    continue;
                                }
                                ability2.kill();
                            }
                            if (player != null) {
                                this.getMorphDataFromPlayer(player).removeTag("morphData");
                            }
                            ite.remove();
                        }
                    }
                    else if (info.prevState != null && player != null) {
                        player.setSize(info.prevState.entInstance.width + (info.nextState.entInstance.width - info.prevState.entInstance.width) * (info.morphProgress / 80.0f), info.prevState.entInstance.height + (info.nextState.entInstance.height - info.prevState.entInstance.height) * (info.morphProgress / 80.0f));
                        player.setPosition(player.posX, player.posY, player.posZ);
                        final float prevEyeHeight = (info.prevState.entInstance instanceof EntityPlayer) ? ((EntityPlayer)info.prevState.entInstance).getDefaultEyeHeight() : (info.prevState.entInstance.getEyeHeight() - player.yOffset);
                        final float nextEyeHeight = (info.nextState.entInstance instanceof EntityPlayer) ? ((EntityPlayer)info.nextState.entInstance).getDefaultEyeHeight() : (info.nextState.entInstance.getEyeHeight() - player.yOffset);
                        player.eyeHeight = prevEyeHeight + (nextEyeHeight - prevEyeHeight) * (info.morphProgress / 80.0f);
                        final double prevMaxHealth = MathHelper.clamp_double(info.prevState.entInstance.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue(), 1.0, 20.0);
                        final double nextMaxHealth2 = MathHelper.clamp_double(info.nextState.entInstance.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue(), 1.0, 20.0);
                        if (prevMaxHealth != nextMaxHealth2) {
                            final double healthScale = info.preMorphHealth / prevMaxHealth;
                            final double prevHealth = info.preMorphHealth;
                            final double nextHealth = nextMaxHealth2 * healthScale;
                            if (healthScale <= 1.0) {
                                float targetHealth = (float)(prevHealth + (nextHealth - prevHealth) * (info.morphProgress / 80.0f));
                                if (targetHealth < 1.0f) {
                                    targetHealth = 1.0f;
                                }
                                if (((nextMaxHealth2 > prevMaxHealth && player.getHealth() + 0.5f < (float)Math.floor(targetHealth)) || prevMaxHealth > nextMaxHealth2) && player.isEntityAlive()) {
                                    player.setHealth(targetHealth);
                                }
                            }
                            final double curMaxHealth = player.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue();
                            double morphMaxHealth = Math.round(prevMaxHealth + (nextMaxHealth2 - prevMaxHealth) * (info.morphProgress / 80.0f)) + info.healthOffset;
                            if (morphMaxHealth < 1.0) {
                                morphMaxHealth = 1.0;
                            }
                            if (morphMaxHealth != curMaxHealth) {
                                player.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(morphMaxHealth);
                            }
                        }
                    }
                }
                else if (player != null && Morph.config.getSessionInt("forceSizeWhenMorphed") == 1) {
                    player.setSize(info.nextState.entInstance.width, info.nextState.entInstance.height);
                    player.setPosition(player.posX, player.posY, player.posZ);
                    player.eyeHeight = ((info.nextState.entInstance instanceof EntityPlayer) ? ((EntityPlayer)info.nextState.entInstance).getDefaultEyeHeight() : (info.nextState.entInstance.getEyeHeight() - player.yOffset));
                }
                if (player != null) {
                    if (player.isPlayerSleeping()) {
                        info.sleeping = true;
                    }
                    else if (info.sleeping) {
                        info.sleeping = false;
                        player.setSize(info.nextState.entInstance.width, info.nextState.entInstance.height);
                        player.setPosition(player.posX, player.posY, player.posZ);
                        player.eyeHeight = ((info.nextState.entInstance instanceof EntityPlayer) ? ((EntityPlayer)info.nextState.entInstance).getDefaultEyeHeight() : (info.nextState.entInstance.getEyeHeight() - player.yOffset));
                    }
                    if (info.prevState != null) {
                        info.prevState.entInstance.setEntityId(player.getEntityId());
                    }
                    if (info.nextState != null) {
                        info.nextState.entInstance.setEntityId(player.getEntityId());
                    }
                }
                for (final Ability ability2 : info.morphAbilities) {
                    if ((player != null && ability2.getParent() == player) || (player == null && ability2.getParent() != null)) {
                        if (ability2.inactive) {
                            continue;
                        }
                        ability2.tick();
                        if (info.firstUpdate || !(ability2 instanceof AbilityFly) || player == null) {
                            continue;
                        }
                        info.flying = player.capabilities.isFlying;
                    }
                    else {
                        ability2.setParent((EntityLivingBase)player);
                    }
                }
                info.firstUpdate = false;
            }
            final Iterator<Map.Entry<EntityPlayer, ArrayList<MorphState>>> ite2 = this.saveList.entrySet().iterator();
            while (ite2.hasNext()) {
                final Map.Entry<EntityPlayer, ArrayList<MorphState>> e2 = ite2.next();
                final NBTTagCompound tag = this.getMorphDataFromPlayer(e2.getKey());
                tag.setInteger("morphStatesCount", e2.getValue().size());
                for (int i = 0; i < e2.getValue().size(); ++i) {
                    final MorphState state = e2.getValue().get(i);
                    tag.setTag("morphState" + i, (NBTBase)state.getTag());
                }
                ite2.remove();
            }
            if (this.purgeSession) {
                this.purgeSession = false;
                this.updateSession(null);
            }
        }
    }
    
    public MorphState getSelfState(final World world, final EntityPlayer player) {
        final ArrayList<MorphState> list = this.getPlayerMorphs(world, player);
        for (final MorphState state : list) {
            if (state.playerName.equalsIgnoreCase(state.playerMorph)) {
                return state;
            }
        }
        return new MorphState(world, player.getCommandSenderName(), player.getCommandSenderName(), null, world.isRemote);
    }
    
    public ArrayList<MorphState> getPlayerMorphs(final World world, final EntityPlayer player) {
        final String name = player.getCommandSenderName();
        ArrayList<MorphState> list = this.playerMorphs.get(name);
        if (list == null) {
            list = new ArrayList<MorphState>();
            this.playerMorphs.put(name, list);
            list.add(0, new MorphState(world, name, name, null, world.isRemote));
        }
        boolean found = false;
        for (final MorphState state : list) {
            if (state.playerMorph.equals(name)) {
                found = true;
                break;
            }
        }
        if (!found) {
            list.add(0, new MorphState(world, name, name, null, world.isRemote));
        }
        this.saveList.put(player, list);
        return list;
    }
    
    public void removeAllPlayerMorphsExcludingCurrentMorph(final EntityPlayer player) {
        this.getMorphDataFromPlayer(player).removeTag("morphStatesCount");
    }
    
    public boolean hasMorphState(final EntityPlayer player, final MorphState state) {
        final ArrayList<MorphState> states = this.getPlayerMorphs(player.worldObj, player);
        if (!state.playerMorph.equalsIgnoreCase("")) {
            for (final MorphState mState : states) {
                if (mState.playerMorph.equalsIgnoreCase(state.playerMorph)) {
                    return true;
                }
            }
        }
        else {
            for (final MorphState mState : states) {
                if (mState.identifier.equalsIgnoreCase(state.identifier)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public void updateSession(final EntityPlayer player) {
        if (player != null) {
            PacketHandler.sendToPlayer((EnumMap)Morph.channels, (AbstractPacket)new PacketSession(player), player);
        }
        else {
            PacketHandler.sendToAll((EnumMap)Morph.channels, (AbstractPacket)new PacketSession(player));
        }
    }
    
    public NBTTagCompound getMorphDataFromPlayer(final EntityPlayer player) {
        final NBTTagCompound tag = EntityHelperBase.getPlayerPersistentData(player).getCompoundTag("MorphSave");
        EntityHelperBase.getPlayerPersistentData(player).setTag("MorphSave", (NBTBase)tag);
        return tag;
    }
    
    public void setPlayerMorphInfo(final EntityPlayer player, final MorphInfo info) {
        if (info != null) {
            final NBTTagCompound tag1 = new NBTTagCompound();
            info.writeNBT(tag1);
            this.getMorphDataFromPlayer(player).setTag("morphData", (NBTBase)tag1);
            this.playerMorphInfo.put(player.getCommandSenderName(), info);
        }
        else {
            this.getMorphDataFromPlayer(player).removeTag("morphData");
            this.playerMorphInfo.remove(player.getCommandSenderName());
        }
    }
    
    public MorphInfo getPlayerMorphInfo(final EntityPlayer player) {
        return this.playerMorphInfo.get(player.getCommandSenderName());
    }
    
    public MorphInfo getPlayerMorphInfo(final String playerName) {
        return this.playerMorphInfo.get(playerName);
    }
}
