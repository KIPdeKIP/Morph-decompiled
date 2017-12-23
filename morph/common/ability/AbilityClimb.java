package morph.common.ability;

import morph.api.*;
import net.minecraft.util.*;
import net.minecraft.nbt.*;
import cpw.mods.fml.relauncher.*;

public class AbilityClimb extends Ability
{
    public static final ResourceLocation iconResource;
    
    @Override
    public String getType() {
        return "climb";
    }
    
    @Override
    public void tick() {
        if (this.getParent().isCollidedHorizontally) {
            this.getParent().fallDistance = 0.0f;
            if (this.getParent().isSneaking()) {
                this.getParent().motionY = 0.0;
            }
            else {
                this.getParent().motionY = 0.1176;
            }
        }
        if (!this.getParent().worldObj.isRemote) {
            final double motionX = this.getParent().posX - this.getParent().lastTickPosX;
            final double motionZ = this.getParent().posZ - this.getParent().lastTickPosZ;
            final double motionY = this.getParent().posY - this.getParent().lastTickPosY - 0.765;
            if (motionY > 0.0 && (motionX == 0.0 || motionZ == 0.0)) {
                this.getParent().fallDistance = 0.0f;
            }
        }
    }
    
    @Override
    public void kill() {
    }
    
    @Override
    public Ability clone() {
        return new AbilityClimb();
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
        return AbilityClimb.iconResource;
    }
    
    static {
        iconResource = new ResourceLocation("morph", "textures/icon/climb.png");
    }
}
