package morph.common.ability;

import morph.api.*;
import net.minecraft.util.*;
import net.minecraft.entity.player.*;
import morph.common.*;
import morph.client.morph.*;
import net.minecraft.entity.monster.*;
import morph.common.morph.*;
import net.minecraft.nbt.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.entity.*;

public class AbilityFireImmunity extends Ability
{
    public static final ResourceLocation iconResource;
    
    @Override
    public String getType() {
        return "fireImmunity";
    }
    
    @Override
    public void tick() {
        MorphInfo info = null;
        if (this.getParent() instanceof EntityPlayer) {
            final EntityPlayer player = (EntityPlayer)this.getParent();
            if (!player.worldObj.isRemote) {
                info = Morph.proxy.tickHandlerServer.getPlayerMorphInfo(player);
            }
            else {
                info = Morph.proxy.tickHandlerClient.playerMorphInfo.get(player.getCommandSenderName());
            }
        }
        boolean fireproof = true;
        if (info != null && info.nextState.entInstance instanceof EntitySkeleton) {
            final EntitySkeleton skele = (EntitySkeleton)info.nextState.entInstance;
            if (skele.getSkeletonType() != 1) {
                fireproof = false;
            }
        }
        if (fireproof) {
            if (!this.getParent().isImmuneToFire()) {
                this.getParent().isImmuneToFire = true;
            }
            this.getParent().extinguish();
        }
    }
    
    @Override
    public void kill() {
        this.getParent().isImmuneToFire = false;
    }
    
    @Override
    public Ability clone() {
        return new AbilityFireImmunity();
    }
    
    @Override
    public void postRender() {
    }
    
    @Override
    public void save(final NBTTagCompound tag) {
    }
    
    @Override
    public void load(final NBTTagCompound tag) {
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public ResourceLocation getIcon() {
        return AbilityFireImmunity.iconResource;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public boolean entityHasAbility(final EntityLivingBase living) {
        if (living instanceof EntitySkeleton) {
            final EntitySkeleton skele = (EntitySkeleton)living;
            if (skele.getSkeletonType() != 1) {
                return false;
            }
        }
        return true;
    }
    
    static {
        iconResource = new ResourceLocation("morph", "textures/icon/fireImmunity.png");
    }
}
