package morph.common.ability;

import morph.api.*;
import net.minecraft.util.*;
import net.minecraft.entity.player.*;
import morph.common.*;
import morph.client.morph.*;
import morph.common.morph.*;
import net.minecraft.nbt.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.entity.*;

public class AbilityStep extends Ability
{
    public float stepHeight;
    public static final ResourceLocation iconResource;
    
    public AbilityStep() {
        this.stepHeight = 1.0f;
    }
    
    public AbilityStep(final float height) {
        this.stepHeight = height;
    }
    
    @Override
    public Ability parse(final String[] args) {
        this.stepHeight = Float.parseFloat(args[0]);
        return this;
    }
    
    @Override
    public String getType() {
        return "step";
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
        if (info != null && info.nextState.entInstance.isChild()) {
            return;
        }
        if (this.getParent().stepHeight != this.stepHeight) {
            this.getParent().stepHeight = this.stepHeight;
        }
    }
    
    @Override
    public void kill() {
        if (this.getParent().stepHeight == this.stepHeight) {
            this.getParent().stepHeight = 0.5f;
        }
    }
    
    @Override
    public Ability clone() {
        return new AbilityStep(this.stepHeight);
    }
    
    @Override
    public void save(final NBTTagCompound tag) {
        tag.setFloat("stepHeight", this.stepHeight);
    }
    
    @Override
    public void load(final NBTTagCompound tag) {
        this.stepHeight = tag.getFloat("stepHeight");
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void postRender() {
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public boolean entityHasAbility(final EntityLivingBase living) {
        return !living.isChild();
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public ResourceLocation getIcon() {
        return AbilityStep.iconResource;
    }
    
    static {
        iconResource = new ResourceLocation("morph", "textures/icon/step.png");
    }
}
