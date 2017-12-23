package morph.common.ability;

import morph.api.*;
import net.minecraft.util.*;
import net.minecraft.nbt.*;
import cpw.mods.fml.relauncher.*;

public class AbilityFallNegate extends Ability
{
    public static final ResourceLocation iconResource;
    
    @Override
    public String getType() {
        return "fallNegate";
    }
    
    @Override
    public void tick() {
        this.getParent().fallDistance = -0.5f;
    }
    
    @Override
    public void kill() {
    }
    
    @Override
    public Ability clone() {
        return new AbilityFallNegate();
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
        return AbilityFallNegate.iconResource;
    }
    
    static {
        iconResource = new ResourceLocation("morph", "textures/icon/fallNegate.png");
    }
}
