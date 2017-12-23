package morph.client.render;

import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.model.*;
import net.minecraft.util.*;
import net.minecraft.entity.player.*;
import net.minecraft.client.*;
import org.lwjgl.opengl.*;
import morph.client.model.*;
import net.minecraft.entity.*;

public class RenderPlayerHand extends RenderPlayer
{
    public float progress;
    public RenderPlayer parent;
    public ModelBiped biped;
    public ModelRenderer replacement;
    public ResourceLocation resourceLoc;
    
    public void renderFirstPersonArm(final EntityPlayer par1EntityPlayer) {
        if (this.replacement != null) {
            final float f = 1.0f;
            Minecraft.getMinecraft().getTextureManager().bindTexture(this.resourceLoc);
            GL11.glColor4f(f, f, f, this.progress);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            final ModelRenderer arm = this.biped.bipedRightArm;
            this.biped.bipedRightArm = this.replacement;
            final int heightDiff = 12 - ModelHelper.getModelHeight(this.replacement);
            final float rotX = this.replacement.rotationPointX;
            final float rotY = this.replacement.rotationPointY;
            final float rotZ = this.replacement.rotationPointZ;
            final float angX = this.replacement.rotateAngleX;
            final float angY = this.replacement.rotateAngleY;
            final float angZ = this.replacement.rotateAngleZ;
            this.replacement.rotationPointX = arm.rotationPointX;
            this.replacement.rotationPointY = arm.rotationPointY + heightDiff;
            this.replacement.rotationPointZ = arm.rotationPointZ;
            this.biped.onGround = 0.0f;
            this.biped.setRotationAngles(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f, (Entity)par1EntityPlayer);
            this.biped.bipedRightArm.render(0.0625f);
            this.biped.bipedRightArm = arm;
            this.replacement.rotationPointX = rotX;
            this.replacement.rotationPointY = rotY;
            this.replacement.rotationPointZ = rotZ;
            this.replacement.rotateAngleX = angX;
            this.replacement.rotateAngleY = angY;
            this.replacement.rotateAngleZ = angZ;
            GL11.glDisable(3042);
            GL11.glColor4f(f, f, f, 1.0f);
        }
    }
    
    public void setParent(final RenderPlayer render) {
        if (this.parent != render) {
            this.biped = render.modelBipedMain;
        }
        this.parent = render;
    }
}
