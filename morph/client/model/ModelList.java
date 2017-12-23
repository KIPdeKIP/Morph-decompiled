package morph.client.model;

import java.util.*;
import net.minecraft.entity.*;
import morph.common.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.model.*;

public class ModelList
{
    private static final HashMap<Class, ModelInfo> modelInfoMap;
    
    public static void addModelInfo(final Class clz, final ModelInfo info) {
        if (EntityLivingBase.class.isAssignableFrom(clz)) {
            ModelList.modelInfoMap.put(clz, info);
        }
    }
    
    public static ModelInfo getModelInfo(final Class clz) {
        ModelInfo info = ModelList.modelInfoMap.get(clz);
        if (info == null) {
            try {
                for (Class clzz = clz.getSuperclass(); clzz != Entity.class && Entity.class.isAssignableFrom(clzz) && info == null; info = ModelList.modelInfoMap.get(clzz), clzz = clzz.getSuperclass()) {}
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            if (info == null) {
                Morph.console("Cannot find ModelInfo for " + clz.getName() + ". Attempting to generate one.", true);
                final Render rend = RenderManager.instance.getEntityClassRenderObject(clz);
                final ModelBase base = ModelHelper.getPossibleModel(rend);
                info = new ModelInfo(clz, rend, base);
                addModelInfo(clz, info);
            }
        }
        return info;
    }
    
    static {
        modelInfoMap = new HashMap<Class, ModelInfo>();
    }
}
