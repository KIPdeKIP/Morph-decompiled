package morph.common.ability;

import morph.api.*;
import net.minecraft.entity.player.*;
import morph.common.*;
import net.minecraft.util.*;
import morph.client.morph.*;
import morph.common.morph.*;
import java.util.*;
import net.minecraft.nbt.*;
import cpw.mods.fml.relauncher.*;

public class AbilityFly extends Ability
{
    public boolean slowdownInWater;
    public static final ResourceLocation iconResource;
    
    public AbilityFly() {
        this.slowdownInWater = true;
    }
    
    public AbilityFly(final boolean slowdown) {
        this.slowdownInWater = slowdown;
    }
    
    @Override
    public Ability parse(final String[] args) {
        this.slowdownInWater = Boolean.parseBoolean(args[0]);
        return this;
    }
    
    @Override
    public String getType() {
        return "fly";
    }
    
    @Override
    public void tick() {
        if (this.getParent() instanceof EntityPlayer) {
            if (Morph.config.getSessionInt("allowFlight") == 0) {
                return;
            }
            final EntityPlayer player = (EntityPlayer)this.getParent();
            if (!player.capabilities.allowFlying) {
                player.capabilities.allowFlying = true;
                player.sendPlayerAbilities();
            }
            if (player.capabilities.isFlying && !player.capabilities.isCreativeMode) {
                final double motionX = player.worldObj.isRemote ? player.motionX : (player.posX - player.lastTickPosX);
                final double motionZ = player.worldObj.isRemote ? player.motionZ : (player.posZ - player.lastTickPosZ);
                final int i = Math.round(MathHelper.sqrt_double(motionX * motionX + motionZ * motionZ) * 100.0f);
                if (i > 0 && i < 10) {
                    if (player.isInWater() && this.slowdownInWater) {
                        player.addExhaustion(0.125f * i * 0.01f);
                    }
                    else {
                        player.addExhaustion(0.035f * i * 0.01f);
                    }
                }
                else {
                    player.addExhaustion(0.002f);
                }
                if (player.worldObj.isRemote && player.isInWater() && this.slowdownInWater) {
                    final MorphInfo info = Morph.proxy.tickHandlerClient.playerMorphInfo.get(player.getCommandSenderName());
                    if (info != null) {
                        boolean swim = false;
                        for (final Ability ability : info.morphAbilities) {
                            if (ability.getType().equalsIgnoreCase("swim")) {
                                swim = true;
                                break;
                            }
                        }
                        if (!swim) {
                            final EntityPlayer entityPlayer = player;
                            entityPlayer.motionX *= 0.65;
                            final EntityPlayer entityPlayer2 = player;
                            entityPlayer2.motionZ *= 0.65;
                            final EntityPlayer entityPlayer3 = player;
                            entityPlayer3.motionY *= 0.2;
                        }
                    }
                }
            }
        }
        this.getParent().fallDistance = 0.0f;
    }
    
    @Override
    public void kill() {
        if (this.getParent() instanceof EntityPlayer) {
            final EntityPlayer player = (EntityPlayer)this.getParent();
            if (!player.capabilities.isCreativeMode) {
                player.capabilities.allowFlying = false;
                if (player.capabilities.isFlying) {
                    player.capabilities.isFlying = false;
                }
                player.sendPlayerAbilities();
            }
        }
    }
    
    @Override
    public Ability clone() {
        return new AbilityFly(this.slowdownInWater);
    }
    
    @Override
    public void postRender() {
    }
    
    @Override
    public void save(final NBTTagCompound tag) {
        tag.setBoolean("slowdownInWater", this.slowdownInWater);
    }
    
    @Override
    public void load(final NBTTagCompound tag) {
        this.slowdownInWater = tag.getBoolean("slowdownInWater");
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public ResourceLocation getIcon() {
        return (Morph.config.getSessionInt("allowFlight") == 1) ? AbilityFly.iconResource : null;
    }
    
    static {
        iconResource = new ResourceLocation("morph", "textures/icon/fly.png");
    }
}
