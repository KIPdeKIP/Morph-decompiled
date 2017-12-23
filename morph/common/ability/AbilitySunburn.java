package morph.common.ability;

import morph.api.*;
import net.minecraft.entity.player.*;
import morph.common.*;
import net.minecraft.util.*;
import net.minecraft.item.*;
import morph.common.morph.*;
import net.minecraft.nbt.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.entity.*;

public class AbilitySunburn extends Ability
{
    public AbilityFireImmunity fireImmunityInstance;
    public static final ResourceLocation iconResource;
    
    public AbilitySunburn() {
        this.fireImmunityInstance = new AbilityFireImmunity();
    }
    
    @Override
    public String getType() {
        return "sunburn";
    }
    
    @Override
    public void tick() {
        boolean isChild = false;
        if (!this.getParent().worldObj.isRemote && this.getParent() instanceof EntityPlayer) {
            final EntityPlayer player = (EntityPlayer)this.getParent();
            final MorphInfo info = Morph.proxy.tickHandlerServer.getPlayerMorphInfo(player);
            if (player.capabilities.isCreativeMode) {
                isChild = true;
            }
            if (info != null && info.nextState.entInstance.isChild()) {
                isChild = true;
            }
        }
        if (this.getParent().worldObj.isDaytime() && !this.getParent().worldObj.isRemote && !isChild) {
            final float f = this.getParent().getBrightness(1.0f);
            if (f > 0.5f && this.getParent().getRNG().nextFloat() * 30.0f < (f - 0.4f) * 2.0f && this.getParent().worldObj.canBlockSeeTheSky(MathHelper.floor_double(this.getParent().posX), MathHelper.floor_double(this.getParent().posY), MathHelper.floor_double(this.getParent().posZ))) {
                boolean flag = true;
                final ItemStack itemstack = this.getParent().getEquipmentInSlot(4);
                if (itemstack != null) {
                    if (itemstack.isItemStackDamageable()) {
                        itemstack.setItemDamage(itemstack.getItemDamageForDisplay() + this.getParent().getRNG().nextInt(2));
                        if (itemstack.getItemDamageForDisplay() >= itemstack.getMaxDamage()) {
                            this.getParent().renderBrokenItemStack(itemstack);
                            this.getParent().setCurrentItemOrArmor(4, (ItemStack)null);
                        }
                    }
                    flag = false;
                }
                if (flag) {
                    this.getParent().setFire(8);
                }
            }
        }
    }
    
    @Override
    public void kill() {
    }
    
    @Override
    public Ability clone() {
        return new AbilitySunburn();
    }
    
    @Override
    public void save(final NBTTagCompound tag) {
    }
    
    @Override
    public void load(final NBTTagCompound tag) {
    }
    
    @Override
    public void postRender() {
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public ResourceLocation getIcon() {
        return AbilitySunburn.iconResource;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public boolean entityHasAbility(final EntityLivingBase living) {
        return (!AbilityHandler.hasAbility(living.getClass(), "fireImmunity") || !this.fireImmunityInstance.entityHasAbility(living)) && !living.isChild();
    }
    
    static {
        iconResource = new ResourceLocation("morph", "textures/icon/sunburn.png");
    }
}
