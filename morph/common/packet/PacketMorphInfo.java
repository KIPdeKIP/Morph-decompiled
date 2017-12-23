package morph.common.packet;

import ichun.common.core.network.*;
import io.netty.buffer.*;
import cpw.mods.fml.common.network.*;
import net.minecraft.entity.player.*;
import net.minecraft.client.*;
import net.minecraft.potion.*;
import net.minecraft.nbt.*;
import morph.common.morph.*;
import net.minecraft.world.*;
import morph.client.morph.*;
import morph.common.*;
import morph.common.ability.*;
import morph.api.*;
import java.util.*;
import cpw.mods.fml.relauncher.*;

public class PacketMorphInfo extends AbstractPacket
{
    public String playerName;
    public boolean morphing;
    public int morphProgress;
    public boolean hasPrevState;
    public boolean hasNextState;
    public NBTTagCompound prevTag;
    public NBTTagCompound nextTag;
    public boolean flying;
    
    public PacketMorphInfo() {
    }
    
    public PacketMorphInfo(final String playerName, final boolean morphing, final int morphProgress, final boolean hasPrevState, final boolean hasNextState, final NBTTagCompound prevTag, final NBTTagCompound nextTag, final boolean flying) {
        this.playerName = playerName;
        this.morphing = morphing;
        this.morphProgress = morphProgress;
        this.hasPrevState = hasPrevState;
        this.hasNextState = hasNextState;
        this.prevTag = prevTag;
        this.nextTag = nextTag;
        this.flying = flying;
    }
    
    public void writeTo(final ByteBuf buffer, final Side side) {
        ByteBufUtils.writeUTF8String(buffer, this.playerName);
        buffer.writeBoolean(this.hasPrevState);
        if (this.hasPrevState) {
            ByteBufUtils.writeTag(buffer, this.prevTag);
        }
        buffer.writeBoolean(this.hasNextState);
        if (this.hasNextState) {
            ByteBufUtils.writeTag(buffer, this.nextTag);
        }
        buffer.writeBoolean(this.morphing);
        buffer.writeInt(this.morphProgress);
        buffer.writeBoolean(this.flying);
    }
    
    public void readFrom(final ByteBuf buffer, final Side side) {
        this.prevTag = new NBTTagCompound();
        this.nextTag = new NBTTagCompound();
        this.playerName = ByteBufUtils.readUTF8String(buffer);
        this.hasPrevState = buffer.readBoolean();
        if (this.hasPrevState) {
            this.prevTag = ByteBufUtils.readTag(buffer);
        }
        this.hasNextState = buffer.readBoolean();
        if (this.hasNextState) {
            this.nextTag = ByteBufUtils.readTag(buffer);
        }
        this.morphing = buffer.readBoolean();
        this.morphProgress = buffer.readInt();
        this.flying = buffer.readBoolean();
    }
    
    public void execute(final Side side, final EntityPlayer player) {
        if (side.isClient()) {
            this.handleClient(side, player);
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void handleClient(final Side side, final EntityPlayer player) {
        final Minecraft mc = Minecraft.getMinecraft();
        if (this.hasNextState) {
            final EntityPlayer player2 = mc.theWorld.getPlayerEntityByName(this.playerName);
            if (player2 != null && !player2.getActivePotionEffects().isEmpty()) {
                final NBTTagList nbttaglist = new NBTTagList();
                for (final PotionEffect potioneffect : player2.getActivePotionEffects()) {
                    nbttaglist.appendTag((NBTBase)potioneffect.writeCustomPotionEffectToNBT(new NBTTagCompound()));
                }
                this.nextTag.setTag("ActiveEffects", (NBTBase)nbttaglist);
            }
        }
        final MorphState prevState = new MorphState((World)mc.theWorld, this.playerName, "", null, true);
        final MorphState nextState = new MorphState((World)mc.theWorld, this.playerName, "", null, true);
        prevState.readTag((World)mc.theWorld, this.prevTag);
        nextState.readTag((World)mc.theWorld, this.nextTag);
        if (prevState.entInstance != null && prevState.entInstance != mc.thePlayer) {
            prevState.entInstance.noClip = true;
        }
        if (nextState.entInstance != null && nextState.entInstance != mc.thePlayer) {
            nextState.entInstance.noClip = true;
        }
        final MorphInfoClient info = new MorphInfoClient(this.playerName, prevState, nextState);
        info.setMorphing(this.morphing);
        info.morphProgress = this.morphProgress;
        final MorphInfoClient info2 = Morph.proxy.tickHandlerClient.playerMorphInfo.get(this.playerName);
        if (info2 != null) {
            info.morphAbilities = info2.morphAbilities;
            final ArrayList<Ability> newAbilities = AbilityHandler.getEntityAbilities(info.nextState.entInstance.getClass());
            for (final Ability ability : newAbilities) {
                if (ability.requiresInactiveClone()) {
                    try {
                        final Ability clone = ability.clone();
                        clone.inactive = true;
                        info.morphAbilities.add(clone);
                    }
                    catch (Exception ex) {}
                }
            }
        }
        else {
            final ArrayList<Ability> newAbilities = AbilityHandler.getEntityAbilities(info.nextState.entInstance.getClass());
            info.morphAbilities = new ArrayList<Ability>();
            for (final Ability ability : newAbilities) {
                try {
                    final Ability clone = ability.clone();
                    info.morphAbilities.add(clone);
                }
                catch (Exception ex2) {}
            }
        }
        Morph.proxy.tickHandlerClient.playerMorphInfo.put(this.playerName, info);
        info.flying = this.flying;
        if (Morph.config.getInt("sortMorphs") == 3 && info.playerName.equalsIgnoreCase(mc.thePlayer.getCommandSenderName())) {
            final String name1 = info.nextState.entInstance.getCommandSenderName();
            if (name1 != null) {
                final ArrayList<String> order = new ArrayList<String>();
                final Iterator<String> ite = Morph.proxy.tickHandlerClient.playerMorphCatMap.keySet().iterator();
                while (ite.hasNext()) {
                    order.add(ite.next());
                }
                order.remove(name1);
                order.remove(mc.thePlayer.getCommandSenderName());
                order.add(0, name1);
                order.add(0, mc.thePlayer.getCommandSenderName());
                final LinkedHashMap<String, ArrayList<MorphState>> bufferList = new LinkedHashMap<String, ArrayList<MorphState>>(Morph.proxy.tickHandlerClient.playerMorphCatMap);
                Morph.proxy.tickHandlerClient.playerMorphCatMap.clear();
                for (int i = 0; i < order.size(); ++i) {
                    Morph.proxy.tickHandlerClient.playerMorphCatMap.put(order.get(i), bufferList.get(order.get(i)));
                }
            }
        }
    }
}
