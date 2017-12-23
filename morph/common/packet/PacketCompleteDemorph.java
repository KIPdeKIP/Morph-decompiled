package morph.common.packet;

import ichun.common.core.network.*;
import io.netty.buffer.*;
import cpw.mods.fml.common.network.*;
import net.minecraft.entity.player.*;
import net.minecraft.client.*;
import morph.client.morph.*;
import morph.common.*;
import morph.api.*;
import morph.common.morph.*;
import java.util.*;
import cpw.mods.fml.relauncher.*;

public class PacketCompleteDemorph extends AbstractPacket
{
    public String playerName;
    
    public PacketCompleteDemorph() {
    }
    
    public PacketCompleteDemorph(final String name) {
        this.playerName = name;
    }
    
    public void writeTo(final ByteBuf buffer, final Side side) {
        ByteBufUtils.writeUTF8String(buffer, this.playerName);
    }
    
    public void readFrom(final ByteBuf buffer, final Side side) {
        this.playerName = ByteBufUtils.readUTF8String(buffer);
    }
    
    public void execute(final Side side, final EntityPlayer player) {
        if (side.isClient()) {
            this.handleClient(side, player);
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void handleClient(final Side side, final EntityPlayer player) {
        final EntityPlayer player2 = Minecraft.getMinecraft().theWorld.getPlayerEntityByName(this.playerName);
        if (player2 != null) {
            player2.ignoreFrustumCheck = true;
            final MorphInfo info = Morph.proxy.tickHandlerClient.playerMorphInfo.get(this.playerName);
            if (info != null) {
                player2.setSize(info.nextState.entInstance.width, info.nextState.entInstance.height);
                player2.setPosition(player2.posX, player2.posY, player2.posZ);
                player2.eyeHeight = player2.getDefaultEyeHeight();
                player2.ignoreFrustumCheck = false;
            }
        }
        final MorphInfoClient info2 = Morph.proxy.tickHandlerClient.playerMorphInfo.get(this.playerName);
        if (info2 != null) {
            for (final Ability ability : info2.morphAbilities) {
                if (ability.inactive) {
                    continue;
                }
                if (ability.getParent() == null) {
                    continue;
                }
                ability.kill();
            }
        }
        Morph.proxy.tickHandlerClient.playerMorphInfo.remove(this.playerName);
    }
}
