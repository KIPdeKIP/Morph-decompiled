package morph.common.ability;

import morph.api.*;
import net.minecraft.util.*;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.*;
import cpw.mods.fml.relauncher.*;

public class AbilityFloat extends Ability
{
    public double terminalVelocity;
    public boolean negateFallDistance;
    public static final ResourceLocation iconResource;
    
    public AbilityFloat() {
        this.terminalVelocity = -1000.0;
        this.negateFallDistance = false;
    }
    
    public AbilityFloat(final double termVelo, final boolean negateFall) {
        this.terminalVelocity = termVelo;
        this.negateFallDistance = negateFall;
    }
    
    @Override
    public Ability parse(final String[] args) {
        this.terminalVelocity = Double.parseDouble(args[0]);
        this.negateFallDistance = Boolean.parseBoolean(args[1]);
        return this;
    }
    
    @Override
    public String getType() {
        return "float";
    }
    
    @Override
    public void tick() {
        boolean flying = false;
        if (this.getParent() instanceof EntityPlayer) {
            flying = ((EntityPlayer)this.getParent()).capabilities.isFlying;
        }
        if (!flying && this.getParent().motionY < this.terminalVelocity) {
            this.getParent().motionY = this.terminalVelocity;
            if (this.negateFallDistance) {
                this.getParent().fallDistance = 0.0f;
            }
        }
    }
    
    @Override
    public void kill() {
    }
    
    @Override
    public Ability clone() {
        return new AbilityFloat(this.terminalVelocity, this.negateFallDistance);
    }
    
    @Override
    public void postRender() {
    }
    
    @Override
    public void save(final NBTTagCompound tag) {
        tag.setDouble("terminalVelocity", this.terminalVelocity);
        tag.setBoolean("negateFallDistance", this.negateFallDistance);
    }
    
    @Override
    public void load(final NBTTagCompound tag) {
        this.terminalVelocity = tag.getDouble("terminalVelocity");
        this.negateFallDistance = tag.getBoolean("negateFallDistance");
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public ResourceLocation getIcon() {
        return AbilityFloat.iconResource;
    }
    
    static {
        iconResource = new ResourceLocation("morph", "textures/icon/float.png");
    }
}
