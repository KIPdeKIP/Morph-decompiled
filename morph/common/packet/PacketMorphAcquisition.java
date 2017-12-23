package morph.common.packet;

import ichun.common.core.network.*;
import io.netty.buffer.*;
import net.minecraft.entity.player.*;
import net.minecraft.client.*;
import morph.client.morph.*;
import morph.common.*;
import morph.client.entity.*;
import net.minecraft.world.*;
import net.minecraft.entity.*;
import cpw.mods.fml.relauncher.*;

public class PacketMorphAcquisition extends AbstractPacket
{
    public int entityID1;
    public int entityID2;
    
    public PacketMorphAcquisition() {
    }
    
    public PacketMorphAcquisition(final int id1, final int id2) {
        this.entityID1 = id1;
        this.entityID2 = id2;
    }
    
    public void writeTo(final ByteBuf buffer, final Side side) {
        buffer.writeInt(this.entityID1);
        buffer.writeInt(this.entityID2);
    }
    
    public void readFrom(final ByteBuf buffer, final Side side) {
        this.entityID1 = buffer.readInt();
        this.entityID2 = buffer.readInt();
    }
    
    public void execute(final Side side, final EntityPlayer player) {
        if (side.isClient()) {
            this.handleClient(side, player);
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void handleClient(final Side side, final EntityPlayer player) {
        final Minecraft mc = Minecraft.getMinecraft();
        Entity ent = mc.theWorld.getEntityByID(this.entityID1);
        final Entity ent2 = mc.theWorld.getEntityByID(this.entityID2);
        if (ent instanceof EntityLivingBase && ent2 instanceof EntityLivingBase) {
            if (ent instanceof EntityPlayer) {
                final EntityPlayer player2 = (EntityPlayer)ent;
                final MorphInfoClient info = Morph.proxy.tickHandlerClient.playerMorphInfo.get(player2.getCommandSenderName());
                if (info != null) {
                    if (info.getMorphing()) {
                        ent = (Entity)info.prevState.entInstance;
                    }
                    else {
                        ent = (Entity)info.nextState.entInstance;
                    }
                    if (player2 != mc.thePlayer) {
                        player2.setDead();
                    }
                }
            }
            else {
                ent.setDead();
            }
            mc.theWorld.spawnEntityInWorld((Entity)new EntityMorphAcquisition((World)mc.theWorld, (EntityLivingBase)ent, (EntityLivingBase)ent2));
        }
    }
}
