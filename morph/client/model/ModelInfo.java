package morph.client.model;

import java.util.*;
import net.minecraft.client.model.*;
import net.minecraft.entity.*;
import net.minecraft.entity.boss.*;
import net.minecraft.client.renderer.entity.*;
import morph.common.*;

public class ModelInfo
{
    public final Class entClass;
    private final Render entRender;
    public final ModelBase modelParent;
    public final ArrayList<ModelRenderer> modelList;
    public final ModelRenderer assumedArm;
    
    public ModelInfo(final Class entityClass, final Render render, final ModelBase modelInstance) {
        this.entClass = entityClass;
        this.entRender = render;
        this.modelParent = modelInstance;
        this.modelList = ModelHelper.getModelCubes(modelInstance);
        this.assumedArm = ModelHelper.getPotentialArm(modelInstance);
    }
    
    public Render getRenderer() {
        return this.entRender;
    }
    
    public void forceRender(final Entity ent, final double d, final double d1, final double d2, final float f, final float f1) {
        final float bossHealthScale = BossStatus.healthScale;
        final int bossStatusBarTime = BossStatus.statusBarTime;
        final String bossName = BossStatus.bossName;
        final boolean hasColorModifier = BossStatus.hasColorModifier;
        if (RenderManager.instance.renderEngine != null && RenderManager.instance.livingPlayer != null) {
            try {
                this.entRender.doRender(ent, d, d1, d2, f, f1);
            }
            catch (Exception e) {
                Morph.console("A morph/model is causing an exception when Morph tries to render it! You might want to report this to the author of the Morphed mob (Not to Morph!)", true);
            }
        }
        BossStatus.healthScale = bossHealthScale;
        BossStatus.statusBarTime = bossStatusBarTime;
        BossStatus.bossName = bossName;
        BossStatus.hasColorModifier = hasColorModifier;
    }
}
