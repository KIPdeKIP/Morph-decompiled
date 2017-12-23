package morph.client.core;

import morph.client.entity.*;
import morph.common.*;
import cpw.mods.fml.client.registry.*;
import morph.common.core.*;
import net.minecraft.client.renderer.entity.*;
import morph.client.model.*;
import ichun.common.core.util.*;
import java.util.*;
import java.lang.reflect.*;
import cpw.mods.fml.common.*;

public class ClientProxy extends CommonProxy
{
    @Override
    public void initMod() {
        super.initMod();
        RenderingRegistry.registerEntityRenderingHandler((Class)EntityMorphAcquisition.class, (Render)Morph.proxy.tickHandlerClient.renderMorphInstance);
    }
    
    @Override
    public void initPostMod() {
        super.initPostMod();
        final HashMap<Class, RendererLivingEntity> renders = new HashMap<Class, RendererLivingEntity>();
        try {
            final List entityRenderers = (List)ObfuscationReflectionHelper.getPrivateValue((Class)RenderingRegistry.class, (Object)RenderingRegistry.instance(), new String[] { "entityRenderers" });
            for (final Object obj : entityRenderers) {
                final Field[] fields = obj.getClass().getDeclaredFields();
                Render render = null;
                Class clzz = null;
                for (final Field f : fields) {
                    f.setAccessible(true);
                    if (f.getType() == Render.class) {
                        render = (Render)f.get(obj);
                    }
                    else if (f.getType() == Class.class) {
                        clzz = (Class)f.get(obj);
                    }
                }
                if (render instanceof RendererLivingEntity && clzz != null) {
                    renders.put(clzz, (RendererLivingEntity)render);
                }
            }
        }
        catch (Exception ex) {}
        for (int i = this.compatibleEntities.size() - 1; i >= 0; --i) {
            if (!renders.containsKey(this.compatibleEntities.get(i))) {
                Render rend = EntityHelper.getEntityClassRenderObject((Class)this.compatibleEntities.get(i));
                if (rend != null && rend.getClass() == RenderEntity.class) {
                    rend = (Render)renders.get(this.compatibleEntities.get(i));
                }
                if (!(rend instanceof RendererLivingEntity)) {
                    this.compatibleEntities.remove(i);
                }
                else {
                    renders.put(this.compatibleEntities.get(i), (RendererLivingEntity)rend);
                }
            }
        }
        for (final Class clz : this.compatibleEntities) {
            try {
                final RendererLivingEntity rend2 = renders.get(clz);
                ModelList.addModelInfo(clz, new ModelInfo(clz, (Render)rend2, rend2.mainModel));
            }
            catch (Exception e) {
                ObfHelper.obfWarning();
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public void initTickHandlers() {
        super.initTickHandlers();
        this.tickHandlerClient = new TickHandlerClient();
        FMLCommonHandler.instance().bus().register((Object)this.tickHandlerClient);
    }
}
