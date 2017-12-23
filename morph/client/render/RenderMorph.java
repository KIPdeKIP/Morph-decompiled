package morph.client.render;

import net.minecraft.client.renderer.entity.*;
import net.minecraft.util.*;
import net.minecraft.client.model.*;
import morph.client.entity.*;
import net.minecraft.entity.*;

public class RenderMorph extends RendererLivingEntity
{
    public static final ResourceLocation morphSkin;
    
    public RenderMorph(final ModelBase par1ModelBase, final float par2) {
        super(par1ModelBase, par2);
    }
    
    protected ResourceLocation getEntityTexture(final Entity entity) {
        if (entity instanceof EntityMorphAcquisition) {
            this.setMainModel(((EntityMorphAcquisition)entity).model);
        }
        return RenderMorph.morphSkin;
    }
    
    protected void passSpecialRender(final EntityLivingBase par1EntityLivingBase, final double par2, final double par4, final double par6) {
    }
    
    public void setMainModel(final ModelBase base) {
        this.mainModel = base;
    }
    
    static {
        morphSkin = new ResourceLocation("morph", "textures/skin/morphskin.png");
    }
}
