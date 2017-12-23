package morph.common.packet;

import ichun.common.core.network.*;
import net.minecraft.entity.player.*;
import io.netty.buffer.*;
import cpw.mods.fml.relauncher.*;
import morph.common.*;
import cpw.mods.fml.common.network.*;

public class PacketSession extends AbstractPacket
{
    public EntityPlayer player;
    
    public PacketSession() {
    }
    
    public PacketSession(final EntityPlayer player) {
        this.player = player;
    }
    
    public void writeTo(final ByteBuf buffer, final Side side) {
        buffer.writeBoolean(this.player == null && Morph.config.getSessionInt("disableEarlyGameFlight") > 0);
        buffer.writeInt(Morph.config.getSessionInt("abilities"));
        buffer.writeInt(Morph.config.getSessionInt("canSleepMorphed"));
        buffer.writeInt(Morph.config.getSessionInt("allowMorphSelection"));
        buffer.writeInt(Morph.config.getSessionInt("showPlayerLabel"));
        buffer.writeInt(Morph.config.getSessionInt("forceSizeWhenMorphed"));
        ByteBufUtils.writeUTF8String(buffer, Morph.config.getSessionString("disabledAbilities"));
        buffer.writeInt((this.player != null && Morph.config.getSessionInt("disableEarlyGameFlightMode") == 1 && Morph.config.getSessionInt("disableEarlyGameFlight") > 0) ? ((Morph.config.getSessionInt("disableEarlyGameFlight") == 1) ? (Morph.proxy.tickHandlerServer.getMorphDataFromPlayer(this.player).getBoolean("hasTravelledToNether") ? 1 : 0) : (Morph.proxy.tickHandlerServer.getMorphDataFromPlayer(this.player).getBoolean("hasKilledWither") ? 1 : 0)) : Morph.config.getSessionInt("allowFlight"));
    }
    
    public void readFrom(final ByteBuf buffer, final Side side) {
        final boolean isServerwideSession = buffer.readBoolean();
        Morph.config.updateSession("abilities", (Object)buffer.readInt());
        Morph.config.updateSession("canSleepMorphed", (Object)buffer.readInt());
        Morph.config.updateSession("allowMorphSelection", (Object)buffer.readInt());
        Morph.config.updateSession("showPlayerLabel", (Object)buffer.readInt());
        Morph.config.updateSession("forceSizeWhenMorphed", (Object)buffer.readInt());
        Morph.config.updateSession("disabledAbilities", (Object)ByteBufUtils.readUTF8String(buffer));
        if (!isServerwideSession) {
            Morph.config.updateSession("allowFlight", (Object)buffer.readInt());
        }
    }
    
    public void execute(final Side side, final EntityPlayer player) {
    }
}
