package morph.common.ability;

import morph.api.*;
import net.minecraft.nbt.*;
import net.minecraft.entity.*;
import net.minecraft.entity.monster.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.util.*;

public class AbilityPotionEffect extends Ability
{
    public int potionId;
    public int duration;
    public int amplifier;
    public boolean ambient;
    
    public AbilityPotionEffect() {
        this.potionId = 0;
        this.duration = 0;
        this.amplifier = 0;
        this.ambient = true;
    }
    
    public AbilityPotionEffect(final int id, final int dur, final int amp, final boolean amb) {
        this.potionId = id;
        this.duration = dur;
        this.amplifier = amp;
        this.ambient = amb;
    }
    
    @Override
    public Ability parse(final String[] args) {
        this.potionId = Integer.parseInt(args[0]);
        this.duration = Integer.parseInt(args[1]);
        this.amplifier = Integer.parseInt(args[2]);
        this.ambient = Boolean.parseBoolean(args[3]);
        return this;
    }
    
    @Override
    public String getType() {
        return "potionEffect";
    }
    
    @Override
    public void tick() {
    }
    
    @Override
    public void kill() {
    }
    
    @Override
    public Ability clone() {
        return new AbilityPotionEffect(this.potionId, this.duration, this.amplifier, this.ambient);
    }
    
    @Override
    public void save(final NBTTagCompound tag) {
        tag.setInteger("potionId", this.potionId);
        tag.setInteger("duration", this.duration);
        tag.setInteger("amplifier", this.amplifier);
        tag.setBoolean("ambient", this.ambient);
    }
    
    @Override
    public void load(final NBTTagCompound tag) {
        this.potionId = tag.getInteger("potionId");
        this.duration = tag.getInteger("duration");
        this.amplifier = tag.getInteger("amplifier");
        this.ambient = tag.getBoolean("ambient");
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
    
    @Override
    public ResourceLocation getIcon() {
        return null;
    }
}
