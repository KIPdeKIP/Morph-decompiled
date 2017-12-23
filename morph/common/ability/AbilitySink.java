package morph.common.ability;

import morph.api.*;
import net.minecraft.util.*;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.*;
import cpw.mods.fml.relauncher.*;

public class AbilitySink extends Ability
{
    public boolean isInWater;
    public static final ResourceLocation iconResource;
    
    @Override
    public String getType() {
        return "sink";
    }
    
    @Override
    public void tick() {
        boolean flying = false;
        if (this.getParent() instanceof EntityPlayer) {
            flying = ((EntityPlayer)this.getParent()).capabilities.isFlying;
        }
        if (this.getParent().isInWater() || this.getParent().handleLavaMovement()) {
            if (this.getParent().isCollidedHorizontally) {
                this.getParent().motionY = 0.07;
            }
            else if (this.getParent().motionY > -0.07 && !flying) {
                this.getParent().motionY = -0.07;
            }
        }
        if (!this.getParent().isInWater() && this.isInWater && !flying) {
            this.getParent().motionY = 0.32;
        }
        this.isInWater = this.getParent().isInWater();
    }
    
    @Override
    public void kill() {
    }
    
    @Override
    public Ability clone() {
        return new AbilitySink();
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
        return AbilitySink.iconResource;
    }
    
    static {
        iconResource = new ResourceLocation("morph", "textures/icon/sink.png");
    }
}
