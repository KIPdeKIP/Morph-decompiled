package morph.client.model;

import morph.client.entity.*;
import morph.client.morph.*;
import net.minecraft.client.model.*;
import net.minecraft.entity.*;
import org.lwjgl.opengl.*;
import morph.common.*;
import java.util.*;
import net.minecraft.client.renderer.*;
import ichun.common.core.util.*;
import net.minecraft.client.*;
import java.nio.*;

public class ModelMorphAcquisition extends ModelMorph
{
    public EntityMorphAcquisition morphEnt;
    public ModelInfo acquired;
    public ModelInfo acquirer;
    public ArrayList<ModelRenderer> modelListCopy;
    public float renderYaw;
    
    public ModelMorphAcquisition(final EntityMorphAcquisition ent) {
        super(null);
        this.morphEnt = ent;
        this.acquired = ModelList.getModelInfo(ent.acquired.getClass());
        this.acquirer = ModelList.getModelInfo(ent.acquirer.getClass());
        if (this.acquired != null) {
            this.modelList = ModelHelper.getModelCubesCopy(this.acquired, this, ent.acquired);
            this.modelListCopy = ModelHelper.getModelCubesCopy(this.acquired, this, ent.acquired);
            for (final ModelRenderer modelRenderer : this.modelList) {
                final ModelRenderer cube = modelRenderer;
                modelRenderer.rotationPointY -= 8.0;
            }
            for (final ModelRenderer modelRenderer2 : this.modelListCopy) {
                final ModelRenderer cube = modelRenderer2;
                modelRenderer2.rotationPointY -= 8.0;
            }
        }
        this.renderYaw = ent.acquired.renderYawOffset;
    }
    
    @Override
    public void render(final Entity entity, final float f, final float f1, final float f2, final float f3, final float f4, final float f5) {
        GL11.glPushMatrix();
        GL11.glRotatef(this.renderYaw, 0.0f, 1.0f, 0.0f);
        if (this.morphEnt.progress <= 40.0f) {
            final float param7 = 0.0f;
            final float mag = (float)Math.pow((this.morphEnt.progress + Morph.proxy.tickHandlerClient.renderTick) / 40.0f, 2.0);
            ModelMorph.updateCubeMorph(this.modelList, this.modelListCopy, null, param7, mag, 0);
        }
        final FloatBuffer buffer = GLAllocation.createDirectFloatBuffer(16);
        final FloatBuffer buffer2 = GLAllocation.createDirectFloatBuffer(16);
        GL11.glPushMatrix();
        GL11.glGetFloat(2982, buffer);
        ObfHelper.invokePreRenderCallback(this.acquired.getRenderer(), (Class)this.acquired.getRenderer().getClass(), (Entity)this.morphEnt.acquired, Morph.proxy.tickHandlerClient.renderTick);
        GL11.glGetFloat(2982, buffer2);
        GL11.glPopMatrix();
        final float prevScaleX = buffer2.get(0) / buffer.get(0);
        final float prevScaleY = buffer2.get(5) / buffer.get(5);
        final float prevScaleZ = buffer2.get(8) / buffer.get(8);
        GL11.glPushMatrix();
        GL11.glGetFloat(2982, buffer);
        ObfHelper.invokePreRenderCallback(this.acquirer.getRenderer(), (Class)this.acquirer.getRenderer().getClass(), (Entity)this.morphEnt.acquirer, Morph.proxy.tickHandlerClient.renderTick);
        GL11.glGetFloat(2982, buffer2);
        GL11.glPopMatrix();
        final float nextScaleX = buffer2.get(0) / buffer.get(0);
        final float nextScaleY = buffer2.get(5) / buffer.get(5);
        final float nextScaleZ = buffer2.get(8) / buffer.get(8);
        float progress = (this.morphEnt.progress + Morph.proxy.tickHandlerClient.renderTick) / 50.0f;
        if (progress < 0.0f) {
            progress = 0.0f;
        }
        progress = (float)Math.pow(progress, 2.0);
        final float scaleX = prevScaleX + (nextScaleX - prevScaleX) * progress;
        final float scaleY = prevScaleY + (nextScaleY - prevScaleY) * progress;
        final float scaleZ = prevScaleZ + (nextScaleZ - prevScaleZ) * progress;
        final double offset = (1.0f - scaleY) * Minecraft.getMinecraft().thePlayer.yOffset;
        GL11.glTranslated(0.0, offset, 0.0);
        GL11.glScalef(scaleX, scaleY, scaleZ);
        for (int i = 0; i < this.modelList.size(); ++i) {
            final ModelRenderer cube = this.modelList.get(i);
            this.rand.setSeed(i * 1000);
            GL11.glRotatef(560.0f * this.rand.nextFloat() * progress, this.rand.nextFloat() * ((this.rand.nextFloat() > 0.5f) ? -1 : 1) * progress, this.rand.nextFloat() * ((this.rand.nextFloat() > 0.5f) ? -1 : 1) * progress, this.rand.nextFloat() * ((this.rand.nextFloat() > 0.5f) ? -1 : 1) * progress);
            GL11.glTranslated(this.rand.nextDouble() * progress * 0.3, this.rand.nextDouble() * progress * 0.3, this.rand.nextDouble() * progress * 0.3);
            cube.render(f5);
        }
        GL11.glPopMatrix();
    }
}
