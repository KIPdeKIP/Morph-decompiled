package morph.common.ability;

import morph.api.*;
import net.minecraft.util.*;
import net.minecraft.potion.*;
import net.minecraft.nbt.*;
import net.minecraft.entity.*;
import net.minecraft.entity.monster.*;
import cpw.mods.fml.relauncher.*;

public class AbilityWitherResistance extends Ability
{
    public static final ResourceLocation iconResource;
    
    @Override
    public String getType() {
        return "witherResistance";
    }
    
    @Override
    public void tick() {
        if (this.getParent().isPotionActive(Potion.wither)) {
            this.getParent().removePotionEffect(Potion.wither.id);
        }
    }
    
    @Override
    public void kill() {
    }
    
    @Override
    public Ability clone() {
        return new AbilityWitherResistance();
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
    public boolean entityHasAbility(final EntityLivingBase living) {
        if (living instanceof EntitySkeleton) {
            final EntitySkeleton skele = (EntitySkeleton)living;
            if (skele.getSkeletonType() != 1) {
                return false;
            }
        }
        return true;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public ResourceLocation getIcon() {
        return AbilityWitherResistance.iconResource;
    }
    
    static {
        iconResource = new ResourceLocation("morph", "textures/icon/witherResistance.png");
    }
}
