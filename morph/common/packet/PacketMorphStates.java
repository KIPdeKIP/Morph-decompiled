package morph.common.packet;

import ichun.common.core.network.*;
import net.minecraft.nbt.*;
import java.util.*;
import io.netty.buffer.*;
import cpw.mods.fml.common.network.*;
import net.minecraft.entity.player.*;
import net.minecraft.client.*;
import morph.common.*;
import net.minecraft.world.*;
import morph.common.morph.*;
import cpw.mods.fml.relauncher.*;

public class PacketMorphStates extends AbstractPacket
{
    public boolean clear;
    public ArrayList<NBTTagCompound> stateTags;
    
    public PacketMorphStates() {
        this.stateTags = new ArrayList<NBTTagCompound>();
    }
    
    public PacketMorphStates(final boolean clear, final ArrayList<MorphState> states) {
        this.stateTags = new ArrayList<NBTTagCompound>();
        this.clear = clear;
        for (final MorphState state : states) {
            this.stateTags.add(state.getTag());
        }
    }
    
    public void writeTo(final ByteBuf buffer, final Side side) {
        buffer.writeBoolean(this.clear);
        for (final NBTTagCompound tag : this.stateTags) {
            ByteBufUtils.writeUTF8String(buffer, "state");
            ByteBufUtils.writeTag(buffer, tag);
        }
        ByteBufUtils.writeUTF8String(buffer, "##end");
    }
    
    public void readFrom(final ByteBuf buffer, final Side side) {
        this.clear = buffer.readBoolean();
        while (ByteBufUtils.readUTF8String(buffer).equalsIgnoreCase("state")) {
            this.stateTags.add(ByteBufUtils.readTag(buffer));
        }
    }
    
    public void execute(final Side side, final EntityPlayer player) {
        if (side.isClient()) {
            this.handleClient(side, player);
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void handleClient(final Side side, final EntityPlayer player) {
        final Minecraft mc = Minecraft.getMinecraft();
        if (this.clear) {
            Morph.proxy.tickHandlerClient.playerMorphCatMap.clear();
        }
        boolean requireReorder = false;
        for (final NBTTagCompound tag : this.stateTags) {
            final MorphState state = new MorphState((World)mc.theWorld, mc.thePlayer.getCommandSenderName(), "", null, true);
            if (tag != null) {
                state.readTag((World)mc.theWorld, tag);
                final String name = state.entInstance.getCommandSenderName();
                if (name == null) {
                    continue;
                }
                ArrayList<MorphState> states = Morph.proxy.tickHandlerClient.playerMorphCatMap.get(name);
                if (states == null) {
                    requireReorder = true;
                    states = new ArrayList<MorphState>();
                    Morph.proxy.tickHandlerClient.playerMorphCatMap.put(name, states);
                }
                MorphHandler.addOrGetMorphState(states, state);
            }
        }
        if (requireReorder) {
            MorphHandler.reorderMorphs(Minecraft.getMinecraft().thePlayer.getCommandSenderName(), Morph.proxy.tickHandlerClient.playerMorphCatMap);
        }
    }
}
