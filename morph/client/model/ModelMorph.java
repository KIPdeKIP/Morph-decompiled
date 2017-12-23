package morph.client.model;

import morph.client.morph.*;
import net.minecraft.client.model.*;
import ichun.common.core.util.*;
import org.lwjgl.opengl.*;
import morph.common.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.*;
import java.nio.*;
import java.util.*;
import net.minecraft.entity.*;

public class ModelMorph extends ModelBase
{
    public MorphInfoClient morphInfo;
    public ArrayList<ModelRenderer> modelList;
    public ArrayList<ModelRenderer> prevModelList;
    public ArrayList<ModelRenderer> nextModelList;
    public Random rand;
    
    public ModelMorph() {
    }
    
    public ModelMorph(final MorphInfoClient info) {
        this.morphInfo = info;
        this.rand = new Random();
        if (info != null && info.morphProgress < 80 && info.prevModelInfo != null && info.nextModelInfo != null) {
            this.prevModelList = ModelHelper.compileRenderableModels(info.prevModelInfo, info.prevState.entInstance);
            this.nextModelList = ModelHelper.compileRenderableModels(info.nextModelInfo, info.nextState.entInstance);
            this.modelList = ModelHelper.getModelCubesCopy(info.prevModelInfo, this, info.prevState.entInstance);
            for (int i = 0; i < this.nextModelList.size() && i < this.modelList.size(); ++i) {
                final ModelRenderer cubeCopy = this.modelList.get(i);
                final ModelRenderer cubeNewParent = this.nextModelList.get(i);
                ModelHelper.createEmptyContents(this, cubeNewParent, cubeCopy, 0);
            }
            if (this.modelList.size() < this.nextModelList.size()) {
                for (int i = this.modelList.size(); i < this.nextModelList.size(); ++i) {
                    final ModelRenderer parentCube = this.nextModelList.get(i);
                    try {
                        final int txOffsetX = parentCube.textureOffsetX;
                        final int txOffsetY = parentCube.textureOffsetY;
                        final ModelRenderer cubeCopy2 = new ModelRenderer((ModelBase)this, txOffsetX, txOffsetY);
                        cubeCopy2.mirror = parentCube.mirror;
                        cubeCopy2.textureHeight = parentCube.textureHeight;
                        cubeCopy2.textureWidth = parentCube.textureWidth;
                        for (int j = 0; j < parentCube.cubeList.size(); ++j) {
                            final ModelBox box = parentCube.cubeList.get(j);
                            final float param7 = 0.0f;
                            ModelBox randBox;
                            if (this.modelList.size() > 0) {
                                final ModelRenderer randCube = this.modelList.get(this.rand.nextInt(this.modelList.size()));
                                randBox = randCube.cubeList.get(this.rand.nextInt(randCube.cubeList.size()));
                            }
                            else {
                                final ModelRenderer randParentCube = this.nextModelList.get(this.rand.nextInt(this.nextModelList.size()));
                                randBox = randParentCube.cubeList.get(this.rand.nextInt(randParentCube.cubeList.size()));
                            }
                            final float x = randBox.posX1 + ((randBox.posX2 - randBox.posX1 > 0.0f) ? this.rand.nextInt((int)(randBox.posX2 - randBox.posX1)) : 0.0f);
                            final float y = randBox.posY1 + ((randBox.posY2 - randBox.posY1 > 0.0f) ? this.rand.nextInt((int)(randBox.posY2 - randBox.posY1)) : 0.0f);
                            final float z = randBox.posZ1 + ((randBox.posZ2 - randBox.posZ1 > 0.0f) ? this.rand.nextInt((int)(randBox.posZ2 - randBox.posZ1)) : 0.0f);
                            cubeCopy2.addBox(x, y, z, 0, 0, 0, param7);
                        }
                        cubeCopy2.setRotationPoint(parentCube.rotationPointX, parentCube.rotationPointY, parentCube.rotationPointZ);
                        cubeCopy2.rotateAngleX = parentCube.rotateAngleX;
                        cubeCopy2.rotateAngleY = parentCube.rotateAngleY;
                        cubeCopy2.rotateAngleZ = parentCube.rotateAngleZ;
                        this.modelList.add(cubeCopy2);
                        ModelHelper.createEmptyContents(this, parentCube, cubeCopy2, 0);
                    }
                    catch (Exception e) {
                        ObfHelper.obfWarning();
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    
    public void render(final Entity entity, final float f, final float f1, final float f2, final float f3, final float f4, final float f5) {
        GL11.glPushMatrix();
        final ArrayList<ModelRenderer> prevCubes = this.prevModelList;
        final ArrayList<ModelRenderer> nextCubes = this.nextModelList;
        for (int i = 0; i < nextCubes.size(); ++i) {
            final ModelRenderer cube = this.modelList.get(i);
            if (i < prevCubes.size() && this.morphInfo.morphProgress <= 40) {
                final ModelRenderer currentMorphCube = prevCubes.get(i);
                final ModelRenderer nextMorphCube = nextCubes.get(i);
                final float mag = (float)Math.pow((this.morphInfo.morphProgress - 10.0f + Morph.proxy.tickHandlerClient.renderTick) / 30.0f, 2.0);
                cube.rotateAngleX = currentMorphCube.rotateAngleX + (nextMorphCube.rotateAngleX - currentMorphCube.rotateAngleX) * mag;
                cube.rotateAngleY = currentMorphCube.rotateAngleY + (nextMorphCube.rotateAngleY - currentMorphCube.rotateAngleY) * mag;
                cube.rotateAngleZ = currentMorphCube.rotateAngleZ + (nextMorphCube.rotateAngleZ - currentMorphCube.rotateAngleZ) * mag;
            }
            else {
                final ModelRenderer nextMorphCube2 = nextCubes.get(i);
                cube.rotateAngleX = nextMorphCube2.rotateAngleX;
                cube.rotateAngleY = nextMorphCube2.rotateAngleY;
                cube.rotateAngleZ = nextMorphCube2.rotateAngleZ;
            }
            if (i < prevCubes.size() && this.morphInfo.morphProgress <= 60) {
                final ModelRenderer parentCube = prevCubes.get(i);
                final ModelRenderer newParentCube = nextCubes.get(i);
                final float mag = (float)Math.pow((this.morphInfo.morphProgress - 10.0f + Morph.proxy.tickHandlerClient.renderTick) / 50.0f, 2.0);
                cube.rotationPointX = parentCube.rotationPointX + (newParentCube.rotationPointX - parentCube.rotationPointX) * mag;
                cube.rotationPointY = parentCube.rotationPointY + (newParentCube.rotationPointY - parentCube.rotationPointY) * mag;
                cube.rotationPointZ = parentCube.rotationPointZ + (newParentCube.rotationPointZ - parentCube.rotationPointZ) * mag;
            }
            else {
                final ModelRenderer nextMorphCube2 = nextCubes.get(i);
                cube.rotationPointX = nextMorphCube2.rotationPointX;
                cube.rotationPointY = nextMorphCube2.rotationPointY;
                cube.rotationPointZ = nextMorphCube2.rotationPointZ;
            }
        }
        if (this.morphInfo.morphProgress <= 60) {
            final float param7 = 0.0f;
            final float mag2 = (float)Math.pow((this.morphInfo.morphProgress - 10.0f + Morph.proxy.tickHandlerClient.renderTick) / 50.0f, 2.0);
            updateCubeMorph(this.modelList, prevCubes, nextCubes, param7, mag2, 0);
        }
        final FloatBuffer buffer = GLAllocation.createDirectFloatBuffer(16);
        final FloatBuffer buffer2 = GLAllocation.createDirectFloatBuffer(16);
        GL11.glPushMatrix();
        GL11.glGetFloat(2982, buffer);
        ObfHelper.invokePreRenderCallback(this.morphInfo.prevModelInfo.getRenderer(), (Class)this.morphInfo.prevModelInfo.getRenderer().getClass(), (Entity)this.morphInfo.prevState.entInstance, Morph.proxy.tickHandlerClient.renderTick);
        GL11.glGetFloat(2982, buffer2);
        GL11.glPopMatrix();
        final float prevScaleX = buffer2.get(0) / buffer.get(0);
        final float prevScaleY = buffer2.get(5) / buffer.get(5);
        final float prevScaleZ = buffer2.get(8) / buffer.get(8);
        GL11.glPushMatrix();
        GL11.glGetFloat(2982, buffer);
        ObfHelper.invokePreRenderCallback(this.morphInfo.nextModelInfo.getRenderer(), (Class)this.morphInfo.nextModelInfo.getRenderer().getClass(), (Entity)this.morphInfo.nextState.entInstance, Morph.proxy.tickHandlerClient.renderTick);
        GL11.glGetFloat(2982, buffer2);
        GL11.glPopMatrix();
        final float nextScaleX = buffer2.get(0) / buffer.get(0);
        final float nextScaleY = buffer2.get(5) / buffer.get(5);
        final float nextScaleZ = buffer2.get(8) / buffer.get(8);
        final float progress = (this.morphInfo.morphProgress - 10.0f + Morph.proxy.tickHandlerClient.renderTick) / 60.0f;
        final float scaleX = prevScaleX + (nextScaleX - prevScaleX) * progress;
        final float scaleY = prevScaleY + (nextScaleY - prevScaleY) * progress;
        final float scaleZ = prevScaleZ + (nextScaleZ - prevScaleZ) * progress;
        final double offset = (1.0f - scaleY) * Minecraft.getMinecraft().thePlayer.yOffset;
        GL11.glTranslated(0.0, offset, 0.0);
        GL11.glScalef(scaleX, scaleY, scaleZ);
        for (final ModelRenderer cube2 : this.modelList) {
            cube2.render(f5);
        }
        GL11.glPopMatrix();
    }
    
    public void setLivingAnimations(final EntityLivingBase par1EntityLivingBase, final float par2, final float par3, final float par4) {
    }
    
    public static void updateCubeMorph(final List morphCubesList, List currentMorphCubes, List nextMorphCubes, final float param7, final float mag, final int depth) {
        if (morphCubesList == null || depth > 20) {
            return;
        }
        if (currentMorphCubes == null) {
            currentMorphCubes = (ArrayList<ModelRenderer>)new ArrayList<Object>();
        }
        if (nextMorphCubes == null) {
            nextMorphCubes = (ArrayList<ModelRenderer>)new ArrayList<Object>();
        }
        try {
            for (int i = 0; i < morphCubesList.size(); ++i) {
                final ModelRenderer cube = morphCubesList.get(i);
                for (int j = cube.cubeList.size() - 1; j >= 0; --j) {
                    final ModelBox box = cube.cubeList.get(j);
                    float nextXpos = 0.0f;
                    float nextYpos = 0.0f;
                    float nextZpos = 0.0f;
                    float prevXpos = 0.0f;
                    float prevYpos = 0.0f;
                    float prevZpos = 0.0f;
                    int newXSize = 0;
                    int newYSize = 0;
                    int newZSize = 0;
                    int prevXSize = 0;
                    int prevYSize = 0;
                    int prevZSize = 0;
                    ModelBox newBox = null;
                    ModelBox prevBox = null;
                    ModelRenderer nextMorphCube = null;
                    ModelRenderer currentMorphCube = null;
                    if (i < nextMorphCubes.size()) {
                        nextMorphCube = (ModelRenderer)nextMorphCubes.get(i);
                        cube.mirror = nextMorphCube.mirror;
                        if (i < currentMorphCubes.size()) {
                            currentMorphCube = (ModelRenderer)currentMorphCubes.get(i);
                            if (j < nextMorphCube.cubeList.size()) {
                                newBox = nextMorphCube.cubeList.get(j);
                                if (j < currentMorphCube.cubeList.size()) {
                                    prevBox = currentMorphCube.cubeList.get(j);
                                }
                            }
                            else if (j < currentMorphCube.cubeList.size()) {
                                prevBox = currentMorphCube.cubeList.get(j);
                            }
                        }
                        else if (j < nextMorphCube.cubeList.size()) {
                            newBox = nextMorphCube.cubeList.get(j);
                        }
                    }
                    else if (i < currentMorphCubes.size()) {
                        currentMorphCube = (ModelRenderer)currentMorphCubes.get(i);
                        if (j < currentMorphCube.cubeList.size()) {
                            prevBox = currentMorphCube.cubeList.get(j);
                        }
                    }
                    if (newBox != null) {
                        newXSize = (int)Math.abs(newBox.posX2 - newBox.posX1);
                        newYSize = (int)Math.abs(newBox.posY2 - newBox.posY1);
                        newZSize = (int)Math.abs(newBox.posZ2 - newBox.posZ1);
                        nextXpos = newBox.posX1;
                        nextYpos = newBox.posY1;
                        nextZpos = newBox.posZ1;
                    }
                    if (prevBox != null) {
                        prevXSize = (int)Math.abs(prevBox.posX2 - prevBox.posX1);
                        prevYSize = (int)Math.abs(prevBox.posY2 - prevBox.posY1);
                        prevZSize = (int)Math.abs(prevBox.posZ2 - prevBox.posZ1);
                        prevXpos = prevBox.posX1;
                        prevYpos = prevBox.posY1;
                        prevZpos = prevBox.posZ1;
                    }
                    cube.cubeList.remove(j);
                    cube.addBox(prevXpos + (nextXpos - prevXpos) * mag, prevYpos + (nextYpos - prevYpos) * mag, prevZpos + (nextZpos - prevZpos) * mag, Math.round(prevXSize + (newXSize - prevXSize) * mag), Math.round(prevYSize + (newYSize - prevYSize) * mag), Math.round(prevZSize + (newZSize - prevZSize) * mag), param7);
                    updateCubeMorph(cube.childModels, (currentMorphCube == null) ? null : currentMorphCube.childModels, (nextMorphCube == null) ? null : nextMorphCube.childModels, param7, mag, depth + 1);
                }
                if (cube.compiled) {
                    GLAllocation.deleteDisplayLists(cube.displayList);
                    cube.compiled = false;
                }
            }
        }
        catch (Exception e) {
            ObfHelper.obfWarning();
            e.printStackTrace();
        }
    }
}
