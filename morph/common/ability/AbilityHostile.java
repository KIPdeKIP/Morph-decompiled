package morph.common.ability;

import morph.api.*;
import net.minecraft.util.*;
import net.minecraft.nbt.*;
import cpw.mods.fml.relauncher.*;

public class AbilityHostile extends Ability
{
    public static final ResourceLocation iconResource;
    
    @Override
    public String getType() {
        return "hostile";
    }
    
    @Override
    public void tick() {
    }
    
    @Override
    public void kill() {
    }
    
    @Override
    public Ability clone() {
        return new AbilityHostile();
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
        return AbilityHostile.iconResource;
    }
    
    static {
        iconResource = new ResourceLocation("morph", "textures/icon/hostile.png");
    }
}
