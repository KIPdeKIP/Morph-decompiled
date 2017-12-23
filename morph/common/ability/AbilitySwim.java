package morph.common.ability;

import morph.api.*;
import net.minecraftforge.client.*;
import net.minecraft.entity.player.*;
import net.minecraft.block.material.*;
import net.minecraft.enchantment.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import net.minecraft.entity.*;
import net.minecraft.util.*;
import net.minecraft.entity.ai.attributes.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.client.renderer.*;
import net.minecraft.nbt.*;

public class AbilitySwim extends Ability
{
    public boolean canSurviveOutOfWater;
    public int air;
    public float swimSpeed;
    public float landSpeed;
    public boolean canMaintainDepth;
    public static final ResourceLocation iconResource;
    
    public AbilitySwim() {
        this.canSurviveOutOfWater = false;
        this.air = 8008135;
        this.swimSpeed = 1.0f;
        this.landSpeed = 1.0f;
        this.canMaintainDepth = false;
    }
    
    public AbilitySwim(final boolean airBreather) {
        this();
        this.canSurviveOutOfWater = airBreather;
    }
    
    public AbilitySwim(final boolean airBreather, final float swimModifier, final float landModifier, final boolean maintainDepth) {
        this(airBreather);
        this.swimSpeed = swimModifier;
        this.landSpeed = landModifier;
        this.canMaintainDepth = maintainDepth;
        if (this.swimSpeed > 1.22f) {
            this.swimSpeed = 1.22f;
        }
    }
    
    @Override
    public Ability parse(final String[] args) {
        this.canSurviveOutOfWater = Boolean.parseBoolean(args[0]);
        this.swimSpeed = Float.parseFloat(args[1]);
        this.landSpeed = Float.parseFloat(args[2]);
        this.canMaintainDepth = Boolean.parseBoolean(args[3]);
        if (this.swimSpeed > 1.22f) {
            this.swimSpeed = 1.22f;
        }
        return this;
    }
    
    @Override
    public String getType() {
        return "swim";
    }
    
    @Override
    public void tick() {
        if (this.air == 8008135) {
            this.air = this.getParent().getAir();
        }
        final boolean flying = false;
        if (this.getParent().isInWater()) {
            if (this.getParent().worldObj.isRemote && GuiIngameForge.renderAir) {
                GuiIngameForge.renderAir = false;
            }
            this.getParent().setAir(300);
            this.air = 300;
            if (this.swimSpeed != 1.0f && (!(this.getParent() instanceof EntityPlayer) || !((EntityPlayer)this.getParent()).capabilities.isFlying)) {
                if (this.getParent().motionX > -this.swimSpeed && this.getParent().motionX < this.swimSpeed) {
                    final EntityLivingBase parent = this.getParent();
                    parent.motionX *= this.swimSpeed * 0.995f;
                }
                if (this.getParent().motionZ > -this.swimSpeed && this.getParent().motionZ < this.swimSpeed) {
                    final EntityLivingBase parent2 = this.getParent();
                    parent2.motionZ *= this.swimSpeed * 0.995f;
                }
            }
            if (this.canMaintainDepth) {
                final boolean isJumping = this.getParent().isJumping;
                if (!this.getParent().isSneaking() && !isJumping && this.getParent().isInsideOfMaterial(Material.water)) {
                    this.getParent().motionY = 0.0;
                }
                else if (isJumping) {
                    final EntityLivingBase parent3 = this.getParent();
                    parent3.motionY *= this.swimSpeed;
                }
            }
        }
        else if (!this.canSurviveOutOfWater) {
            final int j = EnchantmentHelper.getRespiration(this.getParent());
            this.air = ((j > 0 && this.getParent().getRNG().nextInt(j + 1) > 0) ? this.air : (this.air - 1));
            if (this.air == -20) {
                this.air = 0;
                this.getParent().attackEntityFrom(DamageSource.drown, 2.0f);
            }
            if ((!(this.getParent() instanceof EntityPlayer) || !((EntityPlayer)this.getParent()).capabilities.isFlying) && this.landSpeed != 1.0f && this.air < 285) {
                if (this.getParent().motionX > -this.landSpeed && this.getParent().motionX < this.landSpeed) {
                    final EntityLivingBase parent4 = this.getParent();
                    parent4.motionX *= this.landSpeed;
                }
                if (this.getParent().motionZ > -this.landSpeed && this.getParent().motionZ < this.landSpeed) {
                    final EntityLivingBase parent5 = this.getParent();
                    parent5.motionZ *= this.landSpeed;
                }
            }
        }
    }
    
    @Override
    public void kill() {
        if (this.getParent().worldObj.isRemote && !GuiIngameForge.renderAir) {
            GuiIngameForge.renderAir = true;
        }
    }
    
    @Override
    public Ability clone() {
        return new AbilitySwim(this.canSurviveOutOfWater, this.swimSpeed, this.landSpeed, this.canMaintainDepth);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void postRender() {
        if (!this.canSurviveOutOfWater) {
            final Minecraft mc = Minecraft.getMinecraft();
            if (mc.currentScreen == null && mc.thePlayer == this.getParent() && !mc.thePlayer.isInsideOfMaterial(Material.water) && !mc.thePlayer.capabilities.disableDamage) {
                mc.getTextureManager().bindTexture(Gui.icons);
                final ScaledResolution scaledresolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
                final int width = scaledresolution.getScaledWidth();
                final int height = scaledresolution.getScaledHeight();
                final int l1 = width / 2 + 91;
                final int i2 = height - 39;
                final IAttributeInstance attributeinstance = mc.thePlayer.getEntityAttribute(SharedMonsterAttributes.maxHealth);
                final float f = (float)attributeinstance.getAttributeValue();
                final float f2 = mc.thePlayer.getAbsorptionAmount();
                final int j2 = MathHelper.ceiling_float_int((f + f2) / 2.0f / 10.0f);
                final int k2 = Math.max(10 - (j2 - 2), 3);
                final int l2 = i2 - (j2 - 1) * k2 - 10;
                final int k3 = this.air;
                for (int l3 = MathHelper.ceiling_double_int((this.air - 2) * 10.0 / 300.0), i3 = MathHelper.ceiling_double_int(this.air * 10.0 / 300.0) - l3, k4 = 0; k4 < l3 + i3; ++k4) {
                    if (k4 < l3) {
                        this.drawTexturedModalRect(l1 - k4 * 8 - 9, l2, 16, 18, 9, 9);
                    }
                    else {
                        this.drawTexturedModalRect(l1 - k4 * 8 - 9, l2, 25, 18, 9, 9);
                    }
                }
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    public void drawTexturedModalRect(final int par1, final int par2, final int par3, final int par4, final int par5, final int par6) {
        final float f = 0.00390625f;
        final float f2 = 0.00390625f;
        final Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double)(par1 + 0), (double)(par2 + par6), 0.0, (double)((par3 + 0) * f), (double)((par4 + par6) * f2));
        tessellator.addVertexWithUV((double)(par1 + par5), (double)(par2 + par6), 0.0, (double)((par3 + par5) * f), (double)((par4 + par6) * f2));
        tessellator.addVertexWithUV((double)(par1 + par5), (double)(par2 + 0), 0.0, (double)((par3 + par5) * f), (double)((par4 + 0) * f2));
        tessellator.addVertexWithUV((double)(par1 + 0), (double)(par2 + 0), 0.0, (double)((par3 + 0) * f), (double)((par4 + 0) * f2));
        tessellator.draw();
    }
    
    @Override
    public boolean requiresInactiveClone() {
        return true;
    }
    
    @Override
    public void save(final NBTTagCompound tag) {
        tag.setBoolean("canSurviveOutOfWater", this.canSurviveOutOfWater);
        tag.setFloat("swimSpeedModifier", this.swimSpeed);
        tag.setFloat("landSpeedModifier", this.landSpeed);
        tag.setBoolean("canMaintainDepth", this.canMaintainDepth);
    }
    
    @Override
    public void load(final NBTTagCompound tag) {
        this.canSurviveOutOfWater = tag.getBoolean("canSurviveOutOfWater");
        this.swimSpeed = tag.getFloat("swimSpeedModifier");
        this.landSpeed = tag.getFloat("landSpeedModifier");
        this.canMaintainDepth = tag.getBoolean("canMaintainDepth");
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public ResourceLocation getIcon() {
        return AbilitySwim.iconResource;
    }
    
    static {
        iconResource = new ResourceLocation("morph", "textures/icon/swim.png");
    }
}
