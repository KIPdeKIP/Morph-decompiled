package morph.common.ability;

import morph.api.*;
import net.minecraft.util.*;
import net.minecraft.potion.*;
import net.minecraft.nbt.*;

public class AbilityPoisonResistance extends Ability
{
    public static final ResourceLocation iconResource;
    
    @Override
    public String getType() {
        return "poisonResistance";
    }
    
    @Override
    public void tick() {
        if (this.getParent().isPotionActive(Potion.poison)) {
            this.getParent().removePotionEffect(Potion.poison.id);
        }
    }
    
    @Override
    public void kill() {
    }
    
    @Override
    public Ability clone() {
        return new AbilityPoisonResistance();
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
    
    @Override
    public ResourceLocation getIcon() {
        return AbilityPoisonResistance.iconResource;
    }
    
    static {
        iconResource = new ResourceLocation("morph", "textures/icon/poisonResistance.png");
    }
}
