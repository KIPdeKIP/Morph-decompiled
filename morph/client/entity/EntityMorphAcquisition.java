package morph.client.entity;

import net.minecraft.entity.*;
import morph.client.model.*;
import net.minecraft.world.*;
import net.minecraft.nbt.*;
import net.minecraft.item.*;

public class EntityMorphAcquisition extends EntityLivingBase
{
    public EntityLivingBase acquired;
    public EntityLivingBase acquirer;
    public int progress;
    public ModelMorphAcquisition model;
    
    public EntityMorphAcquisition(final World par1World) {
        super(par1World);
        this.model = new ModelMorphAcquisition(this);
        this.yOffset = -0.5f;
        this.setSize(0.1f, 0.1f);
        this.noClip = true;
        this.renderDistanceWeight = 10.0;
        this.ignoreFrustumCheck = true;
    }
    
    public EntityMorphAcquisition(final World par1World, final EntityLivingBase ac, final EntityLivingBase ar) {
        super(par1World);
        this.acquired = ac;
        this.acquirer = ar;
        this.model = new ModelMorphAcquisition(this);
        this.progress = 0;
        this.yOffset = -0.5f;
        this.setSize(0.1f, 0.1f);
        this.noClip = true;
        this.renderDistanceWeight = 10.0;
        this.ignoreFrustumCheck = true;
        this.setLocationAndAngles(this.acquired.posX, this.acquired.posY, this.acquired.posZ, this.acquired.rotationYaw, this.acquired.rotationPitch);
    }
    
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        ++this.progress;
        if (this.progress > 40) {
            this.setDead();
            return;
        }
        float prog = this.progress / 20.0f;
        if (prog > 1.0f) {
            prog = 1.0f;
        }
        prog = (float)Math.pow(prog, 2.0);
        this.posX = this.acquired.posX + (this.acquirer.posX - this.acquired.posX) * prog;
        this.posY = this.acquired.boundingBox.minY + (this.acquirer.boundingBox.minY - this.acquired.boundingBox.minY) * prog + this.yOffset;
        this.posZ = this.acquired.posZ + (this.acquirer.posZ - this.acquired.posZ) * prog;
    }
    
    public boolean isEntityAlive() {
        return !this.isDead;
    }
    
    public void setHealth(final float par1) {
    }
    
    public boolean writeToNBTOptional(final NBTTagCompound par1NBTTagCompound) {
        return false;
    }
    
    protected void entityInit() {
        super.entityInit();
    }
    
    public void readEntityFromNBT(final NBTTagCompound nbttagcompound) {
    }
    
    public void writeEntityToNBT(final NBTTagCompound nbttagcompound) {
    }
    
    public ItemStack getHeldItem() {
        return null;
    }
    
    public ItemStack getEquipmentInSlot(final int i) {
        return null;
    }
    
    public void setCurrentItemOrArmor(final int i, final ItemStack itemstack) {
    }
    
    public ItemStack[] getLastActiveItems() {
        return new ItemStack[0];
    }
}
