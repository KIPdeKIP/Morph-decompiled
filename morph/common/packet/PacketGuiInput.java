package morph.common.packet;

import io.netty.buffer.*;
import cpw.mods.fml.relauncher.*;
import cpw.mods.fml.common.network.*;
import morph.common.*;
import net.minecraft.entity.player.*;
import morph.common.ability.*;
import net.minecraftforge.common.*;
import morph.api.*;
import cpw.mods.fml.common.eventhandler.*;
import ichun.common.core.network.*;
import net.minecraft.entity.*;
import morph.common.morph.*;
import java.util.*;

public class PacketGuiInput extends AbstractPacket
{
    public int input;
    public String identifier;
    public boolean favourite;
    
    public PacketGuiInput() {
    }
    
    public PacketGuiInput(final int input, final String identifier, final boolean favourite) {
        this.input = input;
        this.identifier = identifier;
        this.favourite = favourite;
    }
    
    public void writeTo(final ByteBuf buffer, final Side side) {
        buffer.writeInt(this.input);
        ByteBufUtils.writeUTF8String(buffer, this.identifier);
        buffer.writeBoolean(this.favourite);
    }
    
    public void readFrom(final ByteBuf buffer, final Side side) {
        this.input = buffer.readInt();
        this.identifier = ByteBufUtils.readUTF8String(buffer);
        this.favourite = buffer.readBoolean();
    }
    
    public void execute(final Side side, final EntityPlayer player) {
        final MorphInfo info = Morph.proxy.tickHandlerServer.getPlayerMorphInfo(player);
        if (info == null || !info.getMorphing() || this.input == 2) {
            final MorphState state = MorphHandler.getMorphState((EntityPlayerMP)player, this.identifier);
            if (state != null) {
                switch (this.input) {
                    case 0: {
                        final MorphState old = (info != null) ? info.nextState : Morph.proxy.tickHandlerServer.getSelfState(player.worldObj, player);
                        final MorphInfo info2 = new MorphInfo(player.getCommandSenderName(), old, state);
                        info2.setMorphing(true);
                        info2.healthOffset = player.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue() - 20.0;
                        info2.preMorphHealth = player.getHealth();
                        final MorphInfo info3 = Morph.proxy.tickHandlerServer.getPlayerMorphInfo(player);
                        if (info3 != null) {
                            info2.morphAbilities = info3.morphAbilities;
                            info2.healthOffset = info3.healthOffset;
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
                        if (!MinecraftForge.EVENT_BUS.post((Event)new MorphEvent(player, info2.prevState.entInstance, info2.nextState.entInstance))) {
                            Morph.proxy.tickHandlerServer.setPlayerMorphInfo(player, info2);
                            PacketHandler.sendToAll((EnumMap)Morph.channels, (AbstractPacket)info2.getMorphInfoAsPacket());
                            player.worldObj.playSoundAtEntity((Entity)player, "morph:morph", 1.0f, 1.0f);
                            break;
                        }
                        break;
                    }
                    case 1: {
                        if (info != null && info.nextState.identifier.equalsIgnoreCase(state.identifier)) {
                            break;
                        }
                        if (state.playerMorph.equalsIgnoreCase(player.getCommandSenderName())) {
                            break;
                        }
                        final ArrayList<MorphState> states = Morph.proxy.tickHandlerServer.getPlayerMorphs(player.worldObj, player);
                        states.remove(state);
                        MorphHandler.updatePlayerOfMorphStates((EntityPlayerMP)player, null, true);
                        break;
                    }
                    case 2: {
                        state.isFavourite = this.favourite;
                        break;
                    }
                }
            }
        }
    }
}
