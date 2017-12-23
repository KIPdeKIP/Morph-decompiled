package morph.common.ability;

import morph.api.*;
import net.minecraft.util.*;
import net.minecraft.nbt.*;
import cpw.mods.fml.relauncher.*;

public class AbilityWaterAllergy extends Ability
{
    public static final ResourceLocation iconResource;
    
    @Override
    public String getType() {
        return "waterAllergy";
    }
    
    @Override
    public void tick() {
        if (this.getParent().isWet()) {
            this.getParent().attackEntityFrom(DamageSource.drown, 1.0f);
        }
    }
    
    @Override
    public void kill() {
    }
    
    @Override
    public Ability clone() {
        return new AbilityWaterAllergy();
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
        return AbilityWaterAllergy.iconResource;
    }
    
    static {
        iconResource = new ResourceLocation("morph", "textures/icon/waterAllergy.png");
    }
}
