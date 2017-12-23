package morph.client.model;

import net.minecraft.entity.*;
import net.minecraft.client.renderer.*;
import cpw.mods.fml.relauncher.*;
import java.lang.reflect.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.model.*;
import morph.common.*;
import java.util.*;

public class ModelHelper
{
    public static Random rand;
    public static HashMap<Class<? extends ModelBase>, ModelRenderer> armMappings;
    
    public static ArrayList<ModelRenderer> getModelCubesCopy(final ModelInfo info, final ModelBase base, final EntityLivingBase ent) {
        if (ent != null) {
            info.forceRender((Entity)ent, 0.0, -500.0, 0.0, 0.0f, 1.0f);
            final ArrayList<ModelRenderer> modelList = getModelCubes(getPossibleModel(info.getRenderer()));
            for (int i = 0; i < modelList.size(); ++i) {
                final ModelRenderer cube = modelList.get(i);
                if (cube.compiled) {
                    GLAllocation.deleteDisplayLists(cube.displayList);
                    cube.compiled = false;
                }
            }
            info.forceRender((Entity)ent, 0.0, -500.0, 0.0, 0.0f, 1.0f);
            final ArrayList<ModelRenderer> list = new ArrayList<ModelRenderer>();
            for (int j = 0; j < modelList.size(); ++j) {
                final ModelRenderer cube2 = modelList.get(j);
                if (cube2.compiled) {
                    list.add(buildCopy(cube2, base, 0, true));
                }
            }
            return list;
        }
        final ArrayList<ModelRenderer> list2 = new ArrayList<ModelRenderer>();
        for (int i = 0; i < info.modelList.size(); ++i) {
            final ModelRenderer cube = info.modelList.get(i);
            list2.add(buildCopy(cube, base, 0, true));
        }
        return list2;
    }
    
    public static ModelRenderer getPotentialArm(final ModelBase parent) {
        if (parent != null) {
            Class clz = parent.getClass();
            if (ModelHelper.armMappings.containsKey(clz)) {
                return ModelHelper.armMappings.get(clz);
            }
            if (clz == ModelHorse.class) {
                final ModelHorse dummy = new ModelHorse();
                final ModelRenderer leg = new ModelRenderer((ModelBase)dummy, 60, 29);
                leg.addBox(-1.1f, -1.0f, -2.1f, 3, 8, 4);
                leg.setRotationPoint(-4.0f, 9.0f, -8.0f);
                final ModelRenderer shin = new ModelRenderer((ModelBase)dummy, 60, 41);
                shin.addBox(-1.1f, 0.0f, -1.6f, 3, 5, 3);
                shin.setRotationPoint(0.0f, 7.0f, 0.0f);
                leg.addChild(shin);
                final ModelRenderer hoof = new ModelRenderer((ModelBase)dummy, 60, 51);
                hoof.addBox(-1.6f, 5.1f, -2.1f, 4, 3, 4);
                hoof.setRotationPoint(0.0f, 0.0f, 0.0f);
                shin.addChild(hoof);
                return leg;
            }
            while (clz != ModelBase.class && ModelBase.class.isAssignableFrom(clz)) {
                try {
                    final Field[] declaredFields;
                    final Field[] fields = declaredFields = clz.getDeclaredFields();
                    for (final Field f : declaredFields) {
                        f.setAccessible(true);
                        if (f.getType() == ModelRenderer.class) {
                            if ((clz == ModelBiped.class && (f.getName().equalsIgnoreCase("bipedRightArm") || f.getName().equalsIgnoreCase("f") || f.getName().equalsIgnoreCase("field_78112_f"))) || (clz == ModelQuadruped.class && (f.getName().equalsIgnoreCase("leg3") || f.getName().equalsIgnoreCase("e") || f.getName().equalsIgnoreCase("field_78147_e"))) || (clz == ModelCreeper.class && (f.getName().equalsIgnoreCase("leg3") || f.getName().equalsIgnoreCase("f") || f.getName().equalsIgnoreCase("field_78129_f"))) || (clz == ModelIronGolem.class && (f.getName().equalsIgnoreCase("ironGolemRightArm") || f.getName().equalsIgnoreCase("c") || f.getName().equalsIgnoreCase("field_78177_c"))) || (clz == ModelSpider.class && (f.getName().equalsIgnoreCase("spiderLeg7") || f.getName().equalsIgnoreCase("j") || f.getName().equalsIgnoreCase("field_78210_j"))) || (clz == ModelWolf.class && (f.getName().equalsIgnoreCase("wolfLeg3") || f.getName().equalsIgnoreCase("e") || f.getName().equalsIgnoreCase("field_78182_e"))) || (clz == ModelOcelot.class && (f.getName().equalsIgnoreCase("ocelotFrontRightLeg") || f.getName().equalsIgnoreCase("d") || f.getName().equalsIgnoreCase("field_78157_d"))) || (clz != ModelBiped.class && clz != ModelQuadruped.class && clz != ModelCreeper.class && clz != ModelIronGolem.class && clz != ModelSpider.class && clz != ModelWolf.class && clz != ModelOcelot.class && (f.getName().contains("Right") || f.getName().contains("right")) && (f.getName().contains("arm") || f.getName().contains("hand") || f.getName().contains("Arm") || f.getName().contains("Hand")))) {
                                final ModelRenderer arm = (ModelRenderer)f.get(parent);
                                if (arm != null) {
                                    return arm;
                                }
                            }
                        }
                        else if (f.getType() == ModelRenderer[].class && clz == ModelSquid.class && (f.getName().equalsIgnoreCase("squidTentacles") || f.getName().equalsIgnoreCase("b") || f.getName().equalsIgnoreCase("field_78201_b"))) {
                            return ((ModelRenderer[])f.get(parent))[0];
                        }
                    }
                    clz = clz.getSuperclass();
                    continue;
                }
                catch (Exception e) {
                    throw new ReflectionHelper.UnableToAccessFieldException(new String[0], e);
                }
                break;
            }
        }
        return null;
    }
    
    public static ArrayList<ModelRenderer> getModelCubes(final ModelBase parent) {
        final ArrayList<ModelRenderer> list = new ArrayList<ModelRenderer>();
        final ArrayList<ModelRenderer[]> list2 = new ArrayList<ModelRenderer[]>();
        if (parent != null) {
            Class clz = parent.getClass();
            while (clz != ModelBase.class && ModelBase.class.isAssignableFrom(clz)) {
                try {
                    final Field[] declaredFields;
                    final Field[] fields = declaredFields = clz.getDeclaredFields();
                    for (final Field f : declaredFields) {
                        f.setAccessible(true);
                        if (f.getType() == ModelRenderer.class) {
                            if ((clz == ModelBiped.class && !f.getName().equalsIgnoreCase("bipedCloak") && !f.getName().equalsIgnoreCase("k") && !f.getName().equalsIgnoreCase("field_78122_k")) || clz != ModelBiped.class) {
                                final ModelRenderer rend = (ModelRenderer)f.get(parent);
                                if (rend != null) {
                                    list.add(rend);
                                }
                            }
                        }
                        else if (f.getType() == ModelRenderer[].class) {
                            final ModelRenderer[] rend2 = (ModelRenderer[])f.get(parent);
                            if (rend2 != null) {
                                list2.add(rend2);
                            }
                        }
                    }
                    clz = clz.getSuperclass();
                    continue;
                }
                catch (Exception e) {
                    throw new ReflectionHelper.UnableToAccessFieldException(new String[0], e);
                }
                break;
            }
        }
        for (final ModelRenderer[] array : list2) {
            final ModelRenderer[] cubes = array;
            for (final ModelRenderer cube : array) {
                if (cube != null && !list.contains(cube)) {
                    list.add(cube);
                }
            }
        }
        final ArrayList<ModelRenderer> children = new ArrayList<ModelRenderer>();
        for (final ModelRenderer cube2 : list) {
            for (final ModelRenderer child : getChildren(cube2, true, 0)) {
                if (!children.contains(child)) {
                    children.add(child);
                }
            }
        }
        for (final ModelRenderer child2 : children) {
            list.remove(child2);
        }
        return list;
    }
    
    public static ModelBase getPossibleModel(final Render rend) {
        final ArrayList<ArrayList<ModelBase>> models = new ArrayList<ArrayList<ModelBase>>();
        if (rend != null) {
            try {
                for (Class clz = rend.getClass(); clz != Render.class; clz = clz.getSuperclass()) {
                    final ArrayList<ModelBase> priorityLevel = new ArrayList<ModelBase>();
                    final Field[] declaredFields;
                    final Field[] fields = declaredFields = clz.getDeclaredFields();
                    for (final Field f : declaredFields) {
                        f.setAccessible(true);
                        if (ModelBase.class.isAssignableFrom(f.getType())) {
                            final ModelBase base = (ModelBase)f.get(rend);
                            if (base != null) {
                                priorityLevel.add(base);
                            }
                        }
                        else if (ModelBase[].class.isAssignableFrom(f.getType())) {
                            final ModelBase[] modelBases = (ModelBase[])f.get(rend);
                            if (modelBases != null) {
                                for (final ModelBase base2 : modelBases) {
                                    priorityLevel.add(base2);
                                }
                            }
                        }
                    }
                    models.add(priorityLevel);
                    if (clz == RendererLivingEntity.class) {
                        final ArrayList<ModelBase> topPriority = new ArrayList<ModelBase>();
                        for (final Field f2 : fields) {
                            f2.setAccessible(true);
                            if (ModelBase.class.isAssignableFrom(f2.getType()) && (f2.getName().equalsIgnoreCase("mainModel") || f2.getName().equalsIgnoreCase("field_77045_g"))) {
                                final ModelBase base3 = (ModelBase)f2.get(rend);
                                if (base3 != null) {
                                    topPriority.add(base3);
                                }
                            }
                        }
                        models.add(topPriority);
                    }
                }
            }
            catch (Exception e) {
                throw new ReflectionHelper.UnableToAccessFieldException(new String[0], e);
            }
        }
        ModelBase base4 = null;
        int priorityLevel2 = -1;
        int size = -1;
        int currentPriority = 0;
        for (int i = 0; i < models.size(); ++i) {
            final ArrayList<ModelBase> modelList = models.get(i);
            for (final ModelBase base : modelList) {
                final ArrayList<ModelRenderer> mrs = getModelCubes(base);
                if (mrs.size() > size || (mrs.size() == size && currentPriority > priorityLevel2)) {
                    size = mrs.size();
                    base4 = base;
                    priorityLevel2 = currentPriority;
                }
            }
            ++currentPriority;
        }
        return base4;
    }
    
    public static ArrayList<ModelRenderer> getChildren(final ModelRenderer parent, final boolean recursive, final int depth) {
        final ArrayList<ModelRenderer> list = new ArrayList<ModelRenderer>();
        if (parent.childModels != null && depth < 20) {
            for (int i = 0; i < parent.childModels.size(); ++i) {
                final ModelRenderer child = parent.childModels.get(i);
                if (recursive) {
                    final ArrayList<ModelRenderer> children = getChildren(child, recursive, depth + 1);
                    for (final ModelRenderer child2 : children) {
                        if (!list.contains(child2)) {
                            list.add(child2);
                        }
                    }
                }
                if (!list.contains(child)) {
                    list.add(child);
                }
            }
        }
        return list;
    }
    
    public static void createEmptyContents(final ModelBase base, final ModelRenderer ori, final ModelRenderer copy, final int depth) {
        if (depth > 20) {
            return;
        }
        if (copy.cubeList.size() < ori.cubeList.size()) {
            for (int j = copy.cubeList.size(); j < ori.cubeList.size(); ++j) {
                final ModelBox box = ori.cubeList.get(j);
                final float param7 = 0.0f;
                final ModelBox randBox = ori.cubeList.get(ModelHelper.rand.nextInt(ori.cubeList.size()));
                final float x = randBox.posX1 + ((randBox.posX2 - randBox.posX1 > 0.0f) ? ModelHelper.rand.nextInt(((int)(randBox.posX2 - randBox.posX1) > 0) ? ((int)(randBox.posX2 - randBox.posX1)) : 1) : 0.0f);
                final float y = randBox.posY1 + ((randBox.posY2 - randBox.posY1 > 0.0f) ? ModelHelper.rand.nextInt(((int)(randBox.posY2 - randBox.posY1) > 0) ? ((int)(randBox.posY2 - randBox.posY1)) : 1) : 0.0f);
                final float z = randBox.posZ1 + ((randBox.posZ2 - randBox.posZ1 > 0.0f) ? ModelHelper.rand.nextInt(((int)(randBox.posZ2 - randBox.posZ1) > 0) ? ((int)(randBox.posZ2 - randBox.posZ1)) : 1) : 0.0f);
                copy.addBox(x, y, z, 0, 0, 0, param7);
            }
        }
        if (ori.childModels != null && (copy.childModels == null || copy.childModels.size() < ori.childModels.size())) {
            for (int i = 0; i < ori.childModels.size(); ++i) {
                final ModelRenderer childCopy = buildCopy(ori.childModels.get(i), base, 0, false);
                createEmptyContents(base, ori.childModels.get(i), childCopy, depth + 1);
                copy.addChild(childCopy);
            }
        }
    }
    
    public static ModelRenderer buildCopy(final ModelRenderer original, final ModelBase copyBase, final int depth, final boolean hasFullModelBox) {
        final int txOffsetX = original.textureOffsetX;
        final int txOffsetY = original.textureOffsetY;
        final ModelRenderer cubeCopy = new ModelRenderer(copyBase, txOffsetX, txOffsetY);
        cubeCopy.mirror = original.mirror;
        cubeCopy.textureHeight = original.textureHeight;
        cubeCopy.textureWidth = original.textureWidth;
        for (int j = 0; j < original.cubeList.size(); ++j) {
            final ModelBox box = original.cubeList.get(j);
            final float param7 = 0.0f;
            if (hasFullModelBox) {
                cubeCopy.addBox(box.posX1, box.posY1, box.posZ1, (int)Math.abs(box.posX2 - box.posX1), (int)Math.abs(box.posY2 - box.posY1), (int)Math.abs(box.posZ2 - box.posZ1));
            }
            else {
                final ModelBox randBox = original.cubeList.get(ModelHelper.rand.nextInt(original.cubeList.size()));
                final float x = randBox.posX1 + ((randBox.posX2 - randBox.posX1 > 0.0f) ? ModelHelper.rand.nextInt(((int)(randBox.posX2 - randBox.posX1) > 0) ? ((int)(randBox.posX2 - randBox.posX1)) : 1) : 0.0f);
                final float y = randBox.posY1 + ((randBox.posY2 - randBox.posY1 > 0.0f) ? ModelHelper.rand.nextInt(((int)(randBox.posY2 - randBox.posY1) > 0) ? ((int)(randBox.posY2 - randBox.posY1)) : 1) : 0.0f);
                final float z = randBox.posZ1 + ((randBox.posZ2 - randBox.posZ1 > 0.0f) ? ModelHelper.rand.nextInt(((int)(randBox.posZ2 - randBox.posZ1) > 0) ? ((int)(randBox.posZ2 - randBox.posZ1)) : 1) : 0.0f);
                cubeCopy.addBox(x, y, z, (int)Math.abs(box.posX2 - box.posX1), (int)Math.abs(box.posY2 - box.posY1), (int)Math.abs(box.posZ2 - box.posZ1));
            }
        }
        if (original.childModels != null && depth < 20) {
            for (int i = 0; i < original.childModels.size(); ++i) {
                final ModelRenderer child = original.childModels.get(i);
                cubeCopy.addChild(buildCopy(child, copyBase, depth + 1, hasFullModelBox));
            }
        }
        cubeCopy.setRotationPoint(original.rotationPointX, original.rotationPointY, original.rotationPointZ);
        cubeCopy.rotateAngleX = original.rotateAngleX;
        cubeCopy.rotateAngleY = original.rotateAngleY;
        cubeCopy.rotateAngleZ = original.rotateAngleZ;
        return cubeCopy;
    }
    
    public static int getModelHeight(final ModelRenderer model) {
        int height = 0;
        for (int i = 0; i < model.cubeList.size(); ++i) {
            final ModelBox box = model.cubeList.get(i);
            if ((int)Math.abs(box.posY2 - box.posY1) > height) {
                height = (int)Math.abs(box.posY2 - box.posY1);
            }
        }
        return height;
    }
    
    public static ModelRenderer createMorphArm(final ModelBase morph, ModelRenderer prevArm, ModelRenderer nextArm, final int morphProgress, final float renderTick) {
        if (prevArm == null) {
            prevArm = new ModelRenderer(morph, 0, 0);
            prevArm.addBox(-3.0f, -2.0f, -2.0f, 0, 12, 0, 0.0f);
            prevArm.setRotationPoint(-5.0f, 2.0f, 0.0f);
        }
        if (nextArm == null) {
            nextArm = new ModelRenderer(morph, 0, 0);
            nextArm.addBox(-3.0f, -2.0f, -2.0f, 0, 12, 0, 0.0f);
            nextArm.setRotationPoint(-5.0f, 2.0f, 0.0f);
        }
        final ModelRenderer cube = new ModelRenderer(morph, 0, 0);
        float mag = (float)Math.pow((morphProgress - 10.0f + renderTick) / 50.0f, 2.0);
        cube.mirror = nextArm.mirror;
        if (mag > 1.0f) {
            mag = 1.0f;
        }
        for (int i = 0; i < nextArm.cubeList.size(); ++i) {
            final ModelBox newBox = nextArm.cubeList.get(i);
            ModelBox prevBox = null;
            if (i < prevArm.cubeList.size()) {
                prevBox = prevArm.cubeList.get(i);
            }
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
            cube.addBox(prevXpos + (nextXpos - prevXpos) * mag, prevYpos + (nextYpos - prevYpos) * mag, prevZpos + (nextZpos - prevZpos) * mag, Math.round(prevXSize + (newXSize - prevXSize) * mag), Math.round(prevYSize + (newYSize - prevYSize) * mag), Math.round(prevZSize + (newZSize - prevZSize) * mag), 0.0f);
        }
        if (morphProgress <= 60) {
            final int heightDiff = 24 - getModelHeight(prevArm) - getModelHeight(nextArm);
            cube.rotationPointX = prevArm.rotationPointX + (nextArm.rotationPointX - prevArm.rotationPointX) * mag;
            cube.rotationPointY = prevArm.rotationPointY + (nextArm.rotationPointY - prevArm.rotationPointY) * mag + heightDiff;
            cube.rotationPointZ = prevArm.rotationPointZ + (nextArm.rotationPointZ - prevArm.rotationPointZ) * mag;
        }
        else {
            final int heightDiff = 12 - getModelHeight(nextArm);
            cube.rotationPointX = nextArm.rotationPointX;
            cube.rotationPointY = nextArm.rotationPointY + heightDiff;
            cube.rotationPointZ = nextArm.rotationPointZ;
        }
        return cube;
    }
    
    public static ArrayList<ModelRenderer> compileRenderableModels(final ModelInfo modelInfo, final EntityLivingBase ent) {
        if (ent != null) {
            final boolean forceRender = Morph.proxy.tickHandlerClient.forceRender;
            Morph.proxy.tickHandlerClient.forceRender = true;
            modelInfo.forceRender((Entity)ent, 0.0, -500.0, 0.0, 0.0f, 1.0f);
            final ArrayList<ModelRenderer> modelList = getModelCubes(getPossibleModel(modelInfo.getRenderer()));
            for (int i = 0; i < modelList.size(); ++i) {
                final ModelRenderer cube = modelList.get(i);
                if (cube.compiled) {
                    GLAllocation.deleteDisplayLists(cube.displayList);
                    cube.compiled = false;
                }
            }
            modelInfo.forceRender((Entity)ent, 0.0, -500.0, 0.0, 0.0f, 1.0f);
            Morph.proxy.tickHandlerClient.forceRender = forceRender;
            final ArrayList<ModelRenderer> list = new ArrayList<ModelRenderer>(modelList);
            for (int j = list.size() - 1; j >= 0; --j) {
                final ModelRenderer cube2 = list.get(j);
                if (!cube2.compiled) {
                    list.remove(j);
                }
            }
            return list;
        }
        return modelInfo.modelList;
    }
    
    static {
        ModelHelper.rand = new Random();
        ModelHelper.armMappings = new HashMap<Class<? extends ModelBase>, ModelRenderer>();
    }
}
