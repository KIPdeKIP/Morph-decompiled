package morph.common.core;

import morph.common.*;
import net.minecraft.client.*;
import org.lwjgl.opengl.*;
import morph.client.morph.*;
import ichun.common.core.util.*;
import cpw.mods.fml.relauncher.*;
import morph.client.render.*;
import morph.client.model.*;
import net.minecraft.client.model.*;
import cpw.mods.fml.common.eventhandler.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.gui.inventory.*;
import morph.client.core.*;
import net.minecraft.client.renderer.entity.*;
import ichun.client.keybind.*;
import morph.common.packet.*;
import java.util.*;
import ichun.common.core.network.*;
import net.minecraft.client.gui.*;
import morph.api.*;
import net.minecraftforge.client.event.*;
import cpw.mods.fml.common.*;
import net.minecraftforge.event.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.util.*;
import morph.common.ability.*;
import net.minecraft.potion.*;
import net.minecraftforge.event.entity.living.*;
import morph.common.morph.*;
import net.minecraft.entity.boss.*;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.world.*;
import net.minecraft.world.*;
import cpw.mods.fml.common.network.*;
import cpw.mods.fml.common.gameevent.*;
import net.minecraft.entity.*;
import net.minecraft.nbt.*;

public class EventHandler
{
    public boolean forcedSpecialRenderCall;
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onRenderGameOverlayPre(final RenderGameOverlayEvent.Pre event) {
        if (event.type == RenderGameOverlayEvent.ElementType.CROSSHAIRS && Morph.proxy.tickHandlerClient.radialShow) {
            if (Morph.config.getInt("renderCrosshairInRadialMenu") == 1) {
                final double mag = Math.sqrt(Morph.proxy.tickHandlerClient.radialDeltaX * Morph.proxy.tickHandlerClient.radialDeltaX + Morph.proxy.tickHandlerClient.radialDeltaY * Morph.proxy.tickHandlerClient.radialDeltaY);
                final double magAcceptance = 0.8;
                double radialAngle = -720.0;
                final double aSin = Math.toDegrees(Math.asin(Morph.proxy.tickHandlerClient.radialDeltaX));
                if (Morph.proxy.tickHandlerClient.radialDeltaY >= 0.0 && Morph.proxy.tickHandlerClient.radialDeltaX >= 0.0) {
                    radialAngle = aSin;
                }
                else if (Morph.proxy.tickHandlerClient.radialDeltaY < 0.0 && Morph.proxy.tickHandlerClient.radialDeltaX >= 0.0) {
                    radialAngle = 90.0 + (90.0 - aSin);
                }
                else if (Morph.proxy.tickHandlerClient.radialDeltaY < 0.0 && Morph.proxy.tickHandlerClient.radialDeltaX < 0.0) {
                    radialAngle = 180.0 - aSin;
                }
                else if (Morph.proxy.tickHandlerClient.radialDeltaY >= 0.0 && Morph.proxy.tickHandlerClient.radialDeltaX < 0.0) {
                    radialAngle = 270.0 + (90.0 + aSin);
                }
                final ScaledResolution reso = new ScaledResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
                GL11.glTranslated(reso.getScaledWidth_double() / 2.0, reso.getScaledHeight_double() / 2.0, 0.0);
                GL11.glRotatef((float)radialAngle, 0.0f, 0.0f, 1.0f);
                GL11.glTranslated(-reso.getScaledWidth_double() / 2.0, -reso.getScaledHeight_double() / 2.0, 0.0);
                GL11.glTranslatef(0.0f, -((float)reso.getScaledHeight_double() / 2.85f * 0.675f * MathHelper.clamp_float((float)(mag / magAcceptance), 0.0f, 1.0f) + MathHelper.clamp_float((float)((mag - magAcceptance) / (1.0 - magAcceptance)), 0.0f, 1.0f) * (float)reso.getScaledHeight_double() / 2.85f * 0.325f), 0.0f);
            }
            else {
                event.setCanceled(true);
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onRenderGameOverlayPost(final RenderGameOverlayEvent.Post event) {
        if (event.type == RenderGameOverlayEvent.ElementType.CROSSHAIRS && Morph.proxy.tickHandlerClient.radialShow && Morph.config.getInt("renderCrosshairInRadialMenu") == 1) {
            final double mag = Math.sqrt(Morph.proxy.tickHandlerClient.radialDeltaX * Morph.proxy.tickHandlerClient.radialDeltaX + Morph.proxy.tickHandlerClient.radialDeltaY * Morph.proxy.tickHandlerClient.radialDeltaY);
            final double magAcceptance = 0.8;
            double radialAngle = -720.0;
            final double aSin = Math.toDegrees(Math.asin(Morph.proxy.tickHandlerClient.radialDeltaX));
            if (Morph.proxy.tickHandlerClient.radialDeltaY >= 0.0 && Morph.proxy.tickHandlerClient.radialDeltaX >= 0.0) {
                radialAngle = aSin;
            }
            else if (Morph.proxy.tickHandlerClient.radialDeltaY < 0.0 && Morph.proxy.tickHandlerClient.radialDeltaX >= 0.0) {
                radialAngle = 90.0 + (90.0 - aSin);
            }
            else if (Morph.proxy.tickHandlerClient.radialDeltaY < 0.0 && Morph.proxy.tickHandlerClient.radialDeltaX < 0.0) {
                radialAngle = 180.0 - aSin;
            }
            else if (Morph.proxy.tickHandlerClient.radialDeltaY >= 0.0 && Morph.proxy.tickHandlerClient.radialDeltaX < 0.0) {
                radialAngle = 270.0 + (90.0 + aSin);
            }
            final ScaledResolution reso = new ScaledResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
            GL11.glTranslatef(0.0f, (float)reso.getScaledHeight_double() / 2.85f * 0.675f * MathHelper.clamp_float((float)(mag / magAcceptance), 0.0f, 1.0f) + MathHelper.clamp_float((float)((mag - magAcceptance) / (1.0 - magAcceptance)), 0.0f, 1.0f) * (float)reso.getScaledHeight_double() / 2.85f * 0.325f, 0.0f);
            GL11.glTranslated(reso.getScaledWidth_double() / 2.0, reso.getScaledHeight_double() / 2.0, 0.0);
            GL11.glRotatef(-(float)radialAngle, 0.0f, 0.0f, 1.0f);
            GL11.glTranslated(-reso.getScaledWidth_double() / 2.0, -reso.getScaledHeight_double() / 2.0, 0.0);
        }
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRenderHand(final RenderHandEvent event) {
        if (Morph.config.getInt("handRenderOverride") == 1) {
            GL11.glPushMatrix();
            final Minecraft mc = Minecraft.getMinecraft();
            if (Morph.proxy.tickHandlerClient.playerMorphInfo.containsKey(mc.thePlayer.getCommandSenderName())) {
                event.setCanceled(true);
                final MorphInfoClient info = Morph.proxy.tickHandlerClient.playerMorphInfo.get(mc.thePlayer.getCommandSenderName());
                GL11.glClear(256);
                if (info.morphProgress <= 40) {
                    if (info.prevModelInfo != null && info.morphProgress < 10) {
                        final RenderPlayer rend = (RenderPlayer)RenderManager.instance.getEntityRenderObject((Entity)mc.thePlayer);
                        final ResourceLocation resourceLoc = ObfHelper.invokeGetEntityTexture(info.prevModelInfo.getRenderer(), (Class)info.prevModelInfo.getRenderer().getClass(), info.prevState.entInstance);
                        Morph.proxy.tickHandlerClient.renderHandInstance.progress = 1.0f;
                        Morph.proxy.tickHandlerClient.renderHandInstance.setParent(rend);
                        Morph.proxy.tickHandlerClient.renderHandInstance.resourceLoc = resourceLoc;
                        Morph.proxy.tickHandlerClient.renderHandInstance.replacement = info.prevModelInfo.assumedArm;
                        RenderManager.instance.entityRenderMap.put(mc.thePlayer.getClass(), Morph.proxy.tickHandlerClient.renderHandInstance);
                        mc.entityRenderer.renderHand(Morph.proxy.tickHandlerClient.renderTick, 0);
                        if (info.getMorphing()) {
                            final float progress = (info.morphProgress + Morph.proxy.tickHandlerClient.renderTick) / 10.0f;
                            Morph.proxy.tickHandlerClient.renderHandInstance.progress = progress;
                            final String resourceDomain = resourceLoc.resourceDomain;
                            final String resourcePath = resourceLoc.resourcePath;
                            ReflectionHelper.setPrivateValue((Class)ResourceLocation.class, (Object)resourceLoc, (Object)"morph", ObfHelper.resourceDomain);
                            ReflectionHelper.setPrivateValue((Class)ResourceLocation.class, (Object)resourceLoc, (Object)"textures/skin/morphskin.png", ObfHelper.resourcePath);
                            mc.entityRenderer.renderHand(Morph.proxy.tickHandlerClient.renderTick, 0);
                            ReflectionHelper.setPrivateValue((Class)ResourceLocation.class, (Object)resourceLoc, (Object)resourceDomain, ObfHelper.resourceDomain);
                            ReflectionHelper.setPrivateValue((Class)ResourceLocation.class, (Object)resourceLoc, (Object)resourcePath, ObfHelper.resourcePath);
                        }
                        RenderManager.instance.entityRenderMap.put(mc.thePlayer.getClass(), rend);
                    }
                }
                else if (info.nextModelInfo != null && info.morphProgress >= 70) {
                    final RenderPlayer rend = (RenderPlayer)RenderManager.instance.getEntityRenderObject((Entity)mc.thePlayer);
                    final ResourceLocation resourceLoc = ObfHelper.invokeGetEntityTexture(info.nextModelInfo.getRenderer(), (Class)info.nextModelInfo.getRenderer().getClass(), info.nextState.entInstance);
                    Morph.proxy.tickHandlerClient.renderHandInstance.progress = 1.0f;
                    Morph.proxy.tickHandlerClient.renderHandInstance.setParent(rend);
                    Morph.proxy.tickHandlerClient.renderHandInstance.resourceLoc = resourceLoc;
                    Morph.proxy.tickHandlerClient.renderHandInstance.replacement = info.nextModelInfo.assumedArm;
                    RenderManager.instance.entityRenderMap.put(mc.thePlayer.getClass(), Morph.proxy.tickHandlerClient.renderHandInstance);
                    mc.entityRenderer.renderHand(Morph.proxy.tickHandlerClient.renderTick, 0);
                    if (info.getMorphing()) {
                        float progress = (info.morphProgress - 70.0f + Morph.proxy.tickHandlerClient.renderTick) / 10.0f;
                        if (progress > 1.0f) {
                            progress = 1.0f;
                        }
                        Morph.proxy.tickHandlerClient.renderHandInstance.progress = 1.0f - progress;
                        final String resourceDomain = resourceLoc.resourceDomain;
                        final String resourcePath = resourceLoc.resourcePath;
                        ReflectionHelper.setPrivateValue((Class)ResourceLocation.class, (Object)resourceLoc, (Object)"morph", ObfHelper.resourceDomain);
                        ReflectionHelper.setPrivateValue((Class)ResourceLocation.class, (Object)resourceLoc, (Object)"textures/skin/morphskin.png", ObfHelper.resourcePath);
                        mc.entityRenderer.renderHand(Morph.proxy.tickHandlerClient.renderTick, 0);
                        ReflectionHelper.setPrivateValue((Class)ResourceLocation.class, (Object)resourceLoc, (Object)resourceDomain, ObfHelper.resourceDomain);
                        ReflectionHelper.setPrivateValue((Class)ResourceLocation.class, (Object)resourceLoc, (Object)resourcePath, ObfHelper.resourcePath);
                    }
                    RenderManager.instance.entityRenderMap.put(mc.thePlayer.getClass(), rend);
                }
                if (info.prevModelInfo != null && info.nextModelInfo != null && info.morphProgress >= 10 && info.morphProgress < 70) {
                    final RenderPlayer rend = (RenderPlayer)RenderManager.instance.getEntityRenderObject((Entity)mc.thePlayer);
                    final ResourceLocation resourceLoc = RenderMorph.morphSkin;
                    Morph.proxy.tickHandlerClient.renderHandInstance.progress = 1.0f;
                    Morph.proxy.tickHandlerClient.renderHandInstance.setParent(rend);
                    Morph.proxy.tickHandlerClient.renderHandInstance.resourceLoc = resourceLoc;
                    Morph.proxy.tickHandlerClient.renderHandInstance.replacement = ModelHelper.createMorphArm(info.interimModel, info.prevModelInfo.assumedArm, info.nextModelInfo.assumedArm, info.morphProgress, Morph.proxy.tickHandlerClient.renderTick);
                    RenderManager.instance.entityRenderMap.put(mc.thePlayer.getClass(), Morph.proxy.tickHandlerClient.renderHandInstance);
                    mc.entityRenderer.renderHand(Morph.proxy.tickHandlerClient.renderTick, 0);
                    RenderManager.instance.entityRenderMap.put(mc.thePlayer.getClass(), rend);
                }
            }
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glPopMatrix();
        }
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRenderPlayer(final RenderPlayerEvent.Pre event) {
        float shadowSize = 0.5f;
        try {
            if (Morph.proxy.tickHandlerClient.playerRenderShadowSize < 0.0f) {
                Morph.proxy.tickHandlerClient.playerRenderShadowSize = event.renderer.shadowSize;
            }
        }
        catch (Exception e) {
            ObfHelper.obfWarning();
            e.printStackTrace();
        }
        if (Morph.proxy.tickHandlerClient.allowRender) {
            Morph.proxy.tickHandlerClient.allowRender = false;
            event.renderer.shadowSize = Morph.proxy.tickHandlerClient.playerRenderShadowSize;
            return;
        }
        if (Morph.proxy.tickHandlerClient.forceRender) {
            event.renderer.shadowSize = Morph.proxy.tickHandlerClient.playerRenderShadowSize;
            return;
        }
        if (Morph.proxy.tickHandlerClient.renderingMorph && Morph.proxy.tickHandlerClient.renderingPlayer > 1) {
            event.setCanceled(true);
            return;
        }
        final TickHandlerClient tickHandlerClient = Morph.proxy.tickHandlerClient;
        ++tickHandlerClient.renderingPlayer;
        if (Morph.proxy.tickHandlerClient.playerMorphInfo.containsKey(event.entityPlayer.getCommandSenderName())) {
            final MorphInfoClient info = Morph.proxy.tickHandlerClient.playerMorphInfo.get(event.entityPlayer.getCommandSenderName());
            if (!event.entityPlayer.isPlayerSleeping() && event.entityPlayer.worldObj.playerEntities.contains(event.entityPlayer)) {
                info.player = event.entityPlayer;
            }
            final double par2 = Morph.proxy.tickHandlerClient.renderTick;
            final int br1 = (info.prevState != null) ? info.prevState.entInstance.getBrightnessForRender((float)par2) : event.entityPlayer.getBrightnessForRender((float)par2);
            final int br2 = info.nextState.entInstance.getBrightnessForRender((float)par2);
            float prog = (float)(info.morphProgress + par2) / 80.0f;
            if (prog > 1.0f) {
                prog = 1.0f;
            }
            try {
                float prevShadowSize = Morph.proxy.tickHandlerClient.playerRenderShadowSize;
                if (info.prevState != null && info.prevState.entInstance != null) {
                    final Render render = RenderManager.instance.getEntityRenderObject((Entity)info.prevState.entInstance);
                    if (render != null) {
                        prevShadowSize = render.shadowSize;
                    }
                }
                final Render render = RenderManager.instance.getEntityRenderObject((Entity)info.nextState.entInstance);
                if (render == event.renderer) {
                    shadowSize = Morph.proxy.tickHandlerClient.playerRenderShadowSize;
                }
                else if (render != null) {
                    shadowSize = render.shadowSize;
                }
                float shadowProg = prog;
                shadowProg /= 0.8f;
                if (shadowProg < 1.0f) {
                    shadowSize = prevShadowSize + (shadowSize - prevShadowSize) * prog;
                }
                render.shadowSize = shadowSize;
            }
            catch (Exception e2) {
                ObfHelper.obfWarning();
                e2.printStackTrace();
            }
            if (Morph.proxy.tickHandlerClient.renderingPlayer == 2) {
                final TickHandlerClient tickHandlerClient2 = Morph.proxy.tickHandlerClient;
                --tickHandlerClient2.renderingPlayer;
                return;
            }
            event.setCanceled(true);
            final double d0 = event.entityPlayer.lastTickPosX + (event.entityPlayer.posX - event.entityPlayer.lastTickPosX) * par2;
            final double d2 = event.entityPlayer.lastTickPosY + (event.entityPlayer.posY - event.entityPlayer.lastTickPosY) * par2;
            final double d3 = event.entityPlayer.lastTickPosZ + (event.entityPlayer.posZ - event.entityPlayer.lastTickPosZ) * par2;
            final float f1 = event.entityPlayer.prevRotationYaw + (event.entityPlayer.rotationYaw - event.entityPlayer.prevRotationYaw) * (float)par2;
            int i = br1 + (int)((br2 - br1) * prog);
            if (event.entityPlayer.isBurning()) {
                i = 15728880;
            }
            final int j = i % 65536;
            final int k = i / 65536;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j / 1.0f, k / 1.0f);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            if (info != null) {
                Morph.proxy.tickHandlerClient.renderingMorph = true;
                GL11.glPushMatrix();
                GL11.glTranslated(1.0 * (d0 - RenderManager.renderPosX), 1.0 * (d2 - RenderManager.renderPosY) + ((event.entityPlayer == Minecraft.getMinecraft().thePlayer && ((!(Minecraft.getMinecraft().currentScreen instanceof GuiInventory) && !(Minecraft.getMinecraft().currentScreen instanceof GuiContainerCreative)) || RenderManager.instance.playerViewY != 180.0f)) ? Morph.proxy.tickHandlerClient.ySize : 0.0), 1.0 * (d3 - RenderManager.renderPosZ));
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                float renderTick = Morph.proxy.tickHandlerClient.renderTick;
                final float prevEntSize = (info.prevState.entInstance != null) ? ((info.prevState.entInstance.width > info.prevState.entInstance.height) ? info.prevState.entInstance.width : info.prevState.entInstance.height) : 1.0f;
                final float nextEntSize = (info.nextState.entInstance != null) ? ((info.nextState.entInstance.width > info.nextState.entInstance.height) ? info.nextState.entInstance.width : info.nextState.entInstance.height) : 1.0f;
                final float prevScaleMag = (prevEntSize > 2.5f) ? (2.5f / prevEntSize) : 1.0f;
                final float nextScaleMag = (nextEntSize > 2.5f) ? (2.5f / nextEntSize) : 1.0f;
                if (info.morphProgress <= 40) {
                    if (info.prevModelInfo != null && info.morphProgress < 10) {
                        final float ff2 = info.prevState.entInstance.renderYawOffset;
                        final float ff3 = info.prevState.entInstance.rotationYaw;
                        final float ff4 = info.prevState.entInstance.rotationPitch;
                        final float ff5 = info.prevState.entInstance.prevRotationYawHead;
                        final float ff6 = info.prevState.entInstance.rotationYawHead;
                        if ((Minecraft.getMinecraft().currentScreen instanceof GuiInventory || Minecraft.getMinecraft().currentScreen instanceof GuiContainerCreative) && RenderManager.instance.playerViewY == 180.0f) {
                            GL11.glScalef(prevScaleMag, prevScaleMag, prevScaleMag);
                            final EntityLivingBase renderView = Minecraft.getMinecraft().renderViewEntity;
                            info.prevState.entInstance.renderYawOffset = renderView.renderYawOffset;
                            info.prevState.entInstance.rotationYaw = renderView.rotationYaw;
                            info.prevState.entInstance.rotationPitch = renderView.rotationPitch;
                            info.prevState.entInstance.prevRotationYawHead = renderView.prevRotationYawHead;
                            info.prevState.entInstance.rotationYawHead = renderView.rotationYawHead;
                            renderTick = 1.0f;
                        }
                        info.prevModelInfo.forceRender((Entity)info.prevState.entInstance, 0.0, 0.0 - event.entityPlayer.yOffset, 0.0, f1, renderTick);
                        if (info.getMorphing()) {
                            final float progress = (info.morphProgress + Morph.proxy.tickHandlerClient.renderTick) / 10.0f;
                            GL11.glEnable(3042);
                            GL11.glBlendFunc(770, 771);
                            GL11.glColor4f(1.0f, 1.0f, 1.0f, progress);
                            final ResourceLocation resourceLoc = ObfHelper.invokeGetEntityTexture(info.prevModelInfo.getRenderer(), (Class)info.prevModelInfo.getRenderer().getClass(), info.prevState.entInstance);
                            final String resourceDomain = (String)ReflectionHelper.getPrivateValue((Class)ResourceLocation.class, (Object)resourceLoc, ObfHelper.resourceDomain);
                            final String resourcePath = (String)ReflectionHelper.getPrivateValue((Class)ResourceLocation.class, (Object)resourceLoc, ObfHelper.resourcePath);
                            ReflectionHelper.setPrivateValue((Class)ResourceLocation.class, (Object)resourceLoc, (Object)"morph", ObfHelper.resourceDomain);
                            ReflectionHelper.setPrivateValue((Class)ResourceLocation.class, (Object)resourceLoc, (Object)"textures/skin/morphskin.png", ObfHelper.resourcePath);
                            info.prevModelInfo.forceRender((Entity)info.prevState.entInstance, 0.0, 0.0 - event.entityPlayer.yOffset, 0.0, f1, renderTick);
                            ReflectionHelper.setPrivateValue((Class)ResourceLocation.class, (Object)resourceLoc, (Object)resourceDomain, ObfHelper.resourceDomain);
                            ReflectionHelper.setPrivateValue((Class)ResourceLocation.class, (Object)resourceLoc, (Object)resourcePath, ObfHelper.resourcePath);
                            GL11.glDisable(3042);
                            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                        }
                        info.prevState.entInstance.renderYawOffset = ff2;
                        info.prevState.entInstance.rotationYaw = ff3;
                        info.prevState.entInstance.rotationPitch = ff4;
                        info.prevState.entInstance.prevRotationYawHead = ff5;
                        info.prevState.entInstance.rotationYawHead = ff6;
                    }
                }
                else if (info.nextModelInfo != null && info.morphProgress >= 70) {
                    final float ff2 = info.nextState.entInstance.renderYawOffset;
                    final float ff3 = info.nextState.entInstance.rotationYaw;
                    final float ff4 = info.nextState.entInstance.rotationPitch;
                    final float ff5 = info.nextState.entInstance.prevRotationYawHead;
                    final float ff6 = info.nextState.entInstance.rotationYawHead;
                    if ((Minecraft.getMinecraft().currentScreen instanceof GuiInventory || Minecraft.getMinecraft().currentScreen instanceof GuiContainerCreative) && RenderManager.instance.playerViewY == 180.0f) {
                        GL11.glScalef(nextScaleMag, nextScaleMag, nextScaleMag);
                        final EntityLivingBase renderView = Minecraft.getMinecraft().renderViewEntity;
                        final EntityLivingBase entInstance = info.nextState.entInstance;
                        final EntityLivingBase entInstance2 = info.nextState.entInstance;
                        final float renderYawOffset = renderView.renderYawOffset;
                        entInstance2.renderYawOffset = renderYawOffset;
                        entInstance.prevRenderYawOffset = renderYawOffset;
                        info.nextState.entInstance.rotationYaw = renderView.rotationYaw;
                        info.nextState.entInstance.rotationPitch = renderView.rotationPitch;
                        info.nextState.entInstance.prevRotationYawHead = renderView.prevRotationYawHead;
                        info.nextState.entInstance.rotationYawHead = renderView.rotationYawHead;
                        renderTick = 1.0f;
                    }
                    info.nextModelInfo.forceRender((Entity)info.nextState.entInstance, 0.0, 0.0 - event.entityPlayer.yOffset, 0.0, f1, renderTick);
                    if (info.getMorphing()) {
                        float progress = (info.morphProgress - 70.0f + Morph.proxy.tickHandlerClient.renderTick) / 10.0f;
                        GL11.glEnable(3042);
                        GL11.glBlendFunc(770, 771);
                        if (progress > 1.0f) {
                            progress = 1.0f;
                        }
                        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f - progress);
                        final ResourceLocation resourceLoc = ObfHelper.invokeGetEntityTexture(info.nextModelInfo.getRenderer(), (Class)info.nextModelInfo.getRenderer().getClass(), info.nextState.entInstance);
                        final String resourceDomain = (String)ReflectionHelper.getPrivateValue((Class)ResourceLocation.class, (Object)resourceLoc, ObfHelper.resourceDomain);
                        final String resourcePath = (String)ReflectionHelper.getPrivateValue((Class)ResourceLocation.class, (Object)resourceLoc, ObfHelper.resourcePath);
                        ReflectionHelper.setPrivateValue((Class)ResourceLocation.class, (Object)resourceLoc, (Object)"morph", ObfHelper.resourceDomain);
                        ReflectionHelper.setPrivateValue((Class)ResourceLocation.class, (Object)resourceLoc, (Object)"textures/skin/morphskin.png", ObfHelper.resourcePath);
                        info.nextModelInfo.forceRender((Entity)info.nextState.entInstance, 0.0, 0.0 - event.entityPlayer.yOffset, 0.0, f1, renderTick);
                        ReflectionHelper.setPrivateValue((Class)ResourceLocation.class, (Object)resourceLoc, (Object)resourceDomain, ObfHelper.resourceDomain);
                        ReflectionHelper.setPrivateValue((Class)ResourceLocation.class, (Object)resourceLoc, (Object)resourcePath, ObfHelper.resourcePath);
                        GL11.glDisable(3042);
                        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                    }
                    info.nextState.entInstance.renderYawOffset = ff2;
                    info.nextState.entInstance.rotationYaw = ff3;
                    info.nextState.entInstance.rotationPitch = ff4;
                    info.nextState.entInstance.prevRotationYawHead = ff5;
                    info.nextState.entInstance.rotationYawHead = ff6;
                }
                if (info.prevModelInfo != null && info.nextModelInfo != null && info.morphProgress >= 10 && info.morphProgress < 70) {
                    final float progress2 = (info.morphProgress - 10.0f + Morph.proxy.tickHandlerClient.renderTick) / 60.0f;
                    final float ff7 = info.prevState.entInstance.renderYawOffset;
                    final float ff8 = info.prevState.entInstance.rotationYaw;
                    final float ff9 = info.prevState.entInstance.rotationPitch;
                    final float ff10 = info.prevState.entInstance.prevRotationYawHead;
                    final float ff11 = info.prevState.entInstance.rotationYawHead;
                    final float fff2 = info.nextState.entInstance.renderYawOffset;
                    final float fff3 = info.nextState.entInstance.rotationYaw;
                    final float fff4 = info.nextState.entInstance.rotationPitch;
                    final float fff5 = info.nextState.entInstance.prevRotationYawHead;
                    final float fff6 = info.nextState.entInstance.rotationYawHead;
                    if ((Minecraft.getMinecraft().currentScreen instanceof GuiInventory || Minecraft.getMinecraft().currentScreen instanceof GuiContainerCreative) && RenderManager.instance.playerViewY == 180.0f) {
                        GL11.glScalef(prevScaleMag + (nextScaleMag - prevScaleMag) * progress2, prevScaleMag + (nextScaleMag - prevScaleMag) * progress2, prevScaleMag + (nextScaleMag - prevScaleMag) * progress2);
                        final EntityLivingBase renderView2 = Minecraft.getMinecraft().renderViewEntity;
                        final EntityLivingBase entInstance3 = info.nextState.entInstance;
                        final EntityLivingBase entInstance4 = info.prevState.entInstance;
                        final float renderYawOffset2 = renderView2.renderYawOffset;
                        entInstance4.renderYawOffset = renderYawOffset2;
                        entInstance3.renderYawOffset = renderYawOffset2;
                        final EntityLivingBase entInstance5 = info.nextState.entInstance;
                        final EntityLivingBase entInstance6 = info.prevState.entInstance;
                        final float rotationYaw = renderView2.rotationYaw;
                        entInstance6.rotationYaw = rotationYaw;
                        entInstance5.rotationYaw = rotationYaw;
                        final EntityLivingBase entInstance7 = info.nextState.entInstance;
                        final EntityLivingBase entInstance8 = info.prevState.entInstance;
                        final float rotationPitch = renderView2.rotationPitch;
                        entInstance8.rotationPitch = rotationPitch;
                        entInstance7.rotationPitch = rotationPitch;
                        final EntityLivingBase entInstance9 = info.nextState.entInstance;
                        final EntityLivingBase entInstance10 = info.prevState.entInstance;
                        final float prevRotationYawHead = renderView2.prevRotationYawHead;
                        entInstance10.prevRotationYawHead = prevRotationYawHead;
                        entInstance9.prevRotationYawHead = prevRotationYawHead;
                        final EntityLivingBase entInstance11 = info.nextState.entInstance;
                        final EntityLivingBase entInstance12 = info.prevState.entInstance;
                        final float rotationYawHead = renderView2.rotationYawHead;
                        entInstance12.rotationYawHead = rotationYawHead;
                        entInstance11.rotationYawHead = rotationYawHead;
                        renderTick = 1.0f;
                    }
                    info.prevModelInfo.forceRender((Entity)info.prevState.entInstance, 0.0, -500.0 - event.entityPlayer.yOffset, 0.0, f1, renderTick);
                    info.nextModelInfo.forceRender((Entity)info.nextState.entInstance, 0.0, -500.0 - event.entityPlayer.yOffset, 0.0, f1, renderTick);
                    info.prevState.entInstance.renderYawOffset = ff7;
                    info.prevState.entInstance.rotationYaw = ff8;
                    info.prevState.entInstance.rotationPitch = ff9;
                    info.prevState.entInstance.prevRotationYawHead = ff10;
                    info.prevState.entInstance.rotationYawHead = ff11;
                    info.nextState.entInstance.renderYawOffset = fff2;
                    info.nextState.entInstance.rotationYaw = fff3;
                    info.nextState.entInstance.rotationPitch = fff4;
                    info.nextState.entInstance.prevRotationYawHead = fff5;
                    info.nextState.entInstance.rotationYawHead = fff6;
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                    Morph.proxy.tickHandlerClient.renderMorphInstance.setMainModel(info.interimModel);
                    Morph.proxy.tickHandlerClient.renderMorphInstance.doRender((EntityLivingBase)event.entityPlayer, 0.0, 0.0 - event.entityPlayer.yOffset, 0.0, f1, renderTick);
                }
                GL11.glPopMatrix();
                Morph.proxy.tickHandlerClient.renderingMorph = false;
            }
        }
        else {
            event.renderer.shadowSize = Morph.proxy.tickHandlerClient.playerRenderShadowSize;
        }
        final TickHandlerClient tickHandlerClient3 = Morph.proxy.tickHandlerClient;
        --tickHandlerClient3.renderingPlayer;
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRenderSpecials(final RenderLivingEvent.Specials.Pre event) {
        for (final Map.Entry<String, MorphInfoClient> e : Morph.proxy.tickHandlerClient.playerMorphInfo.entrySet()) {
            if (e.getValue().nextState.entInstance == event.entity || (e.getValue().prevState != null && e.getValue().prevState.entInstance == event.entity)) {
                if (e.getValue().prevState != null && e.getValue().prevState.entInstance instanceof EntityPlayer && !((EntityPlayer)e.getValue().prevState.entInstance).getCommandSenderName().equals(e.getKey())) {
                    event.setCanceled(true);
                }
                final EntityPlayer player = event.entity.worldObj.getPlayerEntityByName((String)e.getKey());
                if (player != null && (!(e.getValue().nextState.entInstance instanceof EntityPlayer) || !((EntityPlayer)e.getValue().nextState.entInstance).getCommandSenderName().equals(e.getKey())) && Morph.config.getSessionInt("showPlayerLabel") == 1) {
                    if (e.getValue().nextState.entInstance instanceof EntityPlayer && !((EntityPlayer)e.getValue().nextState.entInstance).getCommandSenderName().equals(e.getKey())) {
                        event.setCanceled(true);
                    }
                    final RenderPlayer rend = (RenderPlayer)RenderManager.instance.getEntityRenderObject((Entity)player);
                    GL11.glAlphaFunc(516, 0.1f);
                    if (Minecraft.isGuiEnabled() && player != Minecraft.getMinecraft().thePlayer && !player.isInvisibleToPlayer((EntityPlayer)Minecraft.getMinecraft().thePlayer) && player.riddenByEntity == null) {
                        final float f = 1.6f;
                        final float f2 = 0.016666668f * f;
                        final double d3 = player.getDistanceSqToEntity((Entity)Minecraft.getMinecraft().thePlayer);
                        final float f3 = player.isSneaking() ? RendererLivingEntity.NAME_TAG_RANGE_SNEAK : RendererLivingEntity.NAME_TAG_RANGE;
                        if (d3 < f3 * f3) {
                            final String s = player.func_145748_c_().getFormattedText();
                            rend.func_96449_a((EntityLivingBase)player, event.x, event.y, event.z, s, f2, d3);
                        }
                    }
                    break;
                }
                break;
            }
        }
    }
    
    public void onDrawBlockHighlight(final DrawBlockHighlightEvent event) {
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onKeyBindEvent(final KeyEvent event) {
        final Minecraft mc = Minecraft.getMinecraft();
        if (event.keyBind.isPressed()) {
            if (event.keyBind.equals((Object)Morph.config.getKeyBind("keySelectorUp")) || event.keyBind.equals((Object)Morph.config.getKeyBind("keySelectorDown"))) {
                Morph.proxy.tickHandlerClient.abilityScroll = 0;
                if (!Morph.proxy.tickHandlerClient.selectorShow && mc.currentScreen == null) {
                    Morph.proxy.tickHandlerClient.selectorShow = true;
                    final TickHandlerClient tickHandlerClient = Morph.proxy.tickHandlerClient;
                    Morph.proxy.tickHandlerClient.getClass();
                    tickHandlerClient.selectorTimer = 10 - Morph.proxy.tickHandlerClient.selectorTimer;
                    final TickHandlerClient tickHandlerClient2 = Morph.proxy.tickHandlerClient;
                    Morph.proxy.tickHandlerClient.getClass();
                    tickHandlerClient2.scrollTimerHori = 3;
                    Morph.proxy.tickHandlerClient.selectorSelected = 0;
                    Morph.proxy.tickHandlerClient.selectorSelectedHori = 0;
                    final MorphInfoClient info = Morph.proxy.tickHandlerClient.playerMorphInfo.get(mc.thePlayer.getCommandSenderName());
                    if (info != null) {
                        final MorphState state = info.nextState;
                        final String entName = state.entInstance.getCommandSenderName();
                        int i = 0;
                        for (final Map.Entry<String, ArrayList<MorphState>> e : Morph.proxy.tickHandlerClient.playerMorphCatMap.entrySet()) {
                            if (e.getKey().equalsIgnoreCase(entName)) {
                                Morph.proxy.tickHandlerClient.selectorSelected = i;
                                final ArrayList<MorphState> states = e.getValue();
                                for (int j = 0; j < states.size(); ++j) {
                                    if (states.get(j).identifier.equalsIgnoreCase(state.identifier)) {
                                        Morph.proxy.tickHandlerClient.selectorSelectedHori = j;
                                        break;
                                    }
                                }
                                break;
                            }
                            ++i;
                        }
                    }
                }
                else {
                    Morph.proxy.tickHandlerClient.selectorSelectedHori = 0;
                    Morph.proxy.tickHandlerClient.selectorSelectedPrev = Morph.proxy.tickHandlerClient.selectorSelected;
                    final TickHandlerClient tickHandlerClient3 = Morph.proxy.tickHandlerClient;
                    final TickHandlerClient tickHandlerClient4 = Morph.proxy.tickHandlerClient;
                    Morph.proxy.tickHandlerClient.getClass();
                    final int n = 3;
                    tickHandlerClient4.scrollTimer = n;
                    tickHandlerClient3.scrollTimerHori = n;
                    if (event.keyBind.equals((Object)Morph.config.getKeyBind("keySelectorUp"))) {
                        final TickHandlerClient tickHandlerClient5 = Morph.proxy.tickHandlerClient;
                        --tickHandlerClient5.selectorSelected;
                        if (Morph.proxy.tickHandlerClient.selectorSelected < 0) {
                            Morph.proxy.tickHandlerClient.selectorSelected = Morph.proxy.tickHandlerClient.playerMorphCatMap.size() - 1;
                        }
                    }
                    else {
                        final TickHandlerClient tickHandlerClient6 = Morph.proxy.tickHandlerClient;
                        ++tickHandlerClient6.selectorSelected;
                        if (Morph.proxy.tickHandlerClient.selectorSelected > Morph.proxy.tickHandlerClient.playerMorphCatMap.size() - 1) {
                            Morph.proxy.tickHandlerClient.selectorSelected = 0;
                        }
                    }
                }
            }
            else if (event.keyBind.equals((Object)Morph.config.getKeyBind("keySelectorLeft")) || event.keyBind.equals((Object)Morph.config.getKeyBind("keySelectorRight"))) {
                Morph.proxy.tickHandlerClient.abilityScroll = 0;
                if (!Morph.proxy.tickHandlerClient.selectorShow && mc.currentScreen == null) {
                    Morph.proxy.tickHandlerClient.selectorShow = true;
                    final TickHandlerClient tickHandlerClient7 = Morph.proxy.tickHandlerClient;
                    Morph.proxy.tickHandlerClient.getClass();
                    tickHandlerClient7.selectorTimer = 10 - Morph.proxy.tickHandlerClient.selectorTimer;
                    final TickHandlerClient tickHandlerClient8 = Morph.proxy.tickHandlerClient;
                    Morph.proxy.tickHandlerClient.getClass();
                    tickHandlerClient8.scrollTimerHori = 3;
                    Morph.proxy.tickHandlerClient.selectorSelected = 0;
                    Morph.proxy.tickHandlerClient.selectorSelectedHori = 0;
                    final MorphInfoClient info = Morph.proxy.tickHandlerClient.playerMorphInfo.get(mc.thePlayer.getCommandSenderName());
                    if (info != null) {
                        final MorphState state = info.nextState;
                        final String entName = state.entInstance.getCommandSenderName();
                        int i = 0;
                        for (final Map.Entry<String, ArrayList<MorphState>> e : Morph.proxy.tickHandlerClient.playerMorphCatMap.entrySet()) {
                            if (e.getKey().equalsIgnoreCase(entName)) {
                                Morph.proxy.tickHandlerClient.selectorSelected = i;
                                final ArrayList<MorphState> states = e.getValue();
                                for (int j = 0; j < states.size(); ++j) {
                                    if (states.get(j).identifier.equalsIgnoreCase(state.identifier)) {
                                        Morph.proxy.tickHandlerClient.selectorSelectedHori = j;
                                        break;
                                    }
                                }
                                break;
                            }
                            ++i;
                        }
                    }
                }
                else {
                    Morph.proxy.tickHandlerClient.selectorSelectedHoriPrev = Morph.proxy.tickHandlerClient.selectorSelectedHori;
                    final TickHandlerClient tickHandlerClient9 = Morph.proxy.tickHandlerClient;
                    Morph.proxy.tickHandlerClient.getClass();
                    tickHandlerClient9.scrollTimerHori = 3;
                    if (event.keyBind.equals((Object)Morph.config.getKeyBind("keySelectorLeft"))) {
                        final TickHandlerClient tickHandlerClient10 = Morph.proxy.tickHandlerClient;
                        --tickHandlerClient10.selectorSelectedHori;
                    }
                    else {
                        final TickHandlerClient tickHandlerClient11 = Morph.proxy.tickHandlerClient;
                        ++tickHandlerClient11.selectorSelectedHori;
                    }
                }
            }
            else if (event.keyBind.equals((Object)Morph.config.getKeyBind("keySelectorSelect")) || (event.keyBind.keyIndex == mc.gameSettings.keyBindAttack.getKeyCode() && event.keyBind.isMinecraftBind())) {
                if (Morph.proxy.tickHandlerClient.selectorShow) {
                    Morph.proxy.tickHandlerClient.selectorShow = false;
                    final TickHandlerClient tickHandlerClient12 = Morph.proxy.tickHandlerClient;
                    Morph.proxy.tickHandlerClient.getClass();
                    tickHandlerClient12.selectorTimer = 10 - Morph.proxy.tickHandlerClient.selectorTimer;
                    final TickHandlerClient tickHandlerClient13 = Morph.proxy.tickHandlerClient;
                    Morph.proxy.tickHandlerClient.getClass();
                    tickHandlerClient13.scrollTimerHori = 3;
                    final MorphInfoClient info = Morph.proxy.tickHandlerClient.playerMorphInfo.get(Minecraft.getMinecraft().thePlayer.getCommandSenderName());
                    MorphState selectedState = null;
                    int k = 0;
                    for (final Map.Entry<String, ArrayList<MorphState>> e2 : Morph.proxy.tickHandlerClient.playerMorphCatMap.entrySet()) {
                        if (k == Morph.proxy.tickHandlerClient.selectorSelected) {
                            final ArrayList<MorphState> states2 = e2.getValue();
                            for (int l = 0; l < states2.size(); ++l) {
                                if (l == Morph.proxy.tickHandlerClient.selectorSelectedHori) {
                                    selectedState = states2.get(l);
                                    break;
                                }
                            }
                            break;
                        }
                        ++k;
                    }
                    if (selectedState != null && ((info != null && !info.nextState.identifier.equalsIgnoreCase(selectedState.identifier)) || (info == null && !selectedState.playerMorph.equalsIgnoreCase(mc.thePlayer.getCommandSenderName())))) {
                        PacketHandler.sendToServer((EnumMap)Morph.channels, (AbstractPacket)new PacketGuiInput(0, selectedState.identifier, false));
                    }
                }
                else if (Morph.proxy.tickHandlerClient.radialShow) {
                    Morph.proxy.tickHandlerClient.selectRadialMenu();
                    Morph.proxy.tickHandlerClient.radialShow = false;
                }
            }
            else if (event.keyBind.equals((Object)Morph.config.getKeyBind("keySelectorCancel")) || (event.keyBind.keyIndex == mc.gameSettings.keyBindUseItem.getKeyCode() && event.keyBind.isMinecraftBind())) {
                if (Morph.proxy.tickHandlerClient.selectorShow) {
                    if (mc.currentScreen instanceof GuiIngameMenu) {
                        mc.displayGuiScreen((GuiScreen)null);
                    }
                    Morph.proxy.tickHandlerClient.selectorShow = false;
                    final TickHandlerClient tickHandlerClient14 = Morph.proxy.tickHandlerClient;
                    Morph.proxy.tickHandlerClient.getClass();
                    tickHandlerClient14.selectorTimer = 10 - Morph.proxy.tickHandlerClient.selectorTimer;
                    final TickHandlerClient tickHandlerClient15 = Morph.proxy.tickHandlerClient;
                    Morph.proxy.tickHandlerClient.getClass();
                    tickHandlerClient15.scrollTimerHori = 3;
                }
                if (Morph.proxy.tickHandlerClient.radialShow) {
                    Morph.proxy.tickHandlerClient.radialShow = false;
                }
            }
            else if (event.keyBind.equals((Object)Morph.config.getKeyBind("keySelectorRemoveMorph")) || event.keyBind.keyIndex == 211) {
                if (Morph.proxy.tickHandlerClient.selectorShow) {
                    final MorphInfoClient info = Morph.proxy.tickHandlerClient.playerMorphInfo.get(Minecraft.getMinecraft().thePlayer.getCommandSenderName());
                    MorphState selectedState = null;
                    int k = 0;
                    final Iterator<Map.Entry<String, ArrayList<MorphState>>> ite2 = Morph.proxy.tickHandlerClient.playerMorphCatMap.entrySet().iterator();
                    boolean multiple = false;
                    boolean decrease = false;
                    while (ite2.hasNext()) {
                        final Map.Entry<String, ArrayList<MorphState>> e3 = ite2.next();
                        if (k == Morph.proxy.tickHandlerClient.selectorSelected) {
                            final ArrayList<MorphState> states3 = e3.getValue();
                            int m = 0;
                            while (m < states3.size()) {
                                if (m == Morph.proxy.tickHandlerClient.selectorSelectedHori) {
                                    selectedState = states3.get(m);
                                    if (m == states3.size() - 1) {
                                        decrease = true;
                                        break;
                                    }
                                    break;
                                }
                                else {
                                    ++m;
                                }
                            }
                            if (states3.size() > 1) {
                                multiple = true;
                                break;
                            }
                            break;
                        }
                        else {
                            ++k;
                        }
                    }
                    if (selectedState != null && !selectedState.isFavourite && (info == null || (info != null && !info.nextState.identifier.equalsIgnoreCase(selectedState.identifier))) && !selectedState.playerMorph.equalsIgnoreCase(mc.thePlayer.getCommandSenderName())) {
                        PacketHandler.sendToServer((EnumMap)Morph.channels, (AbstractPacket)new PacketGuiInput(1, selectedState.identifier, false));
                        if (!multiple) {
                            final TickHandlerClient tickHandlerClient16 = Morph.proxy.tickHandlerClient;
                            --tickHandlerClient16.selectorSelected;
                            if (Morph.proxy.tickHandlerClient.selectorSelected < 0) {
                                Morph.proxy.tickHandlerClient.selectorSelected = Morph.proxy.tickHandlerClient.playerMorphCatMap.size() - 1;
                            }
                        }
                        else if (decrease) {
                            final TickHandlerClient tickHandlerClient17 = Morph.proxy.tickHandlerClient;
                            --tickHandlerClient17.selectorSelectedHori;
                            if (Morph.proxy.tickHandlerClient.selectorSelected < 0) {
                                Morph.proxy.tickHandlerClient.selectorSelected = 0;
                            }
                        }
                    }
                }
            }
            else if (event.keyBind.equals((Object)Morph.config.getKeyBind("keyFavourite"))) {
                if (Morph.proxy.tickHandlerClient.selectorShow) {
                    MorphState selectedState2 = null;
                    int i2 = 0;
                    for (final Map.Entry<String, ArrayList<MorphState>> e4 : Morph.proxy.tickHandlerClient.playerMorphCatMap.entrySet()) {
                        if (i2 == Morph.proxy.tickHandlerClient.selectorSelected) {
                            final ArrayList<MorphState> states4 = e4.getValue();
                            for (int j2 = 0; j2 < states4.size(); ++j2) {
                                if (j2 == Morph.proxy.tickHandlerClient.selectorSelectedHori) {
                                    selectedState2 = states4.get(j2);
                                    break;
                                }
                            }
                            break;
                        }
                        ++i2;
                    }
                    if (selectedState2 != null && !selectedState2.playerMorph.equalsIgnoreCase(selectedState2.playerName)) {
                        selectedState2.isFavourite = !selectedState2.isFavourite;
                        PacketHandler.sendToServer((EnumMap)Morph.channels, (AbstractPacket)new PacketGuiInput(2, selectedState2.identifier, selectedState2.isFavourite));
                    }
                }
                else if (mc.currentScreen == null) {
                    Morph.proxy.tickHandlerClient.favouriteStates.clear();
                    for (final Map.Entry<String, ArrayList<MorphState>> e5 : Morph.proxy.tickHandlerClient.playerMorphCatMap.entrySet()) {
                        final ArrayList<MorphState> states5 = e5.getValue();
                        for (int j3 = 0; j3 < states5.size(); ++j3) {
                            if (states5.get(j3).isFavourite) {
                                Morph.proxy.tickHandlerClient.favouriteStates.add(states5.get(j3));
                            }
                        }
                    }
                    Morph.proxy.tickHandlerClient.radialPlayerYaw = mc.renderViewEntity.rotationYaw;
                    Morph.proxy.tickHandlerClient.radialPlayerPitch = mc.renderViewEntity.rotationPitch;
                    final TickHandlerClient tickHandlerClient18 = Morph.proxy.tickHandlerClient;
                    final TickHandlerClient tickHandlerClient19 = Morph.proxy.tickHandlerClient;
                    final double n2 = 0.0;
                    tickHandlerClient19.radialDeltaY = n2;
                    tickHandlerClient18.radialDeltaX = n2;
                    Morph.proxy.tickHandlerClient.radialShow = true;
                    Morph.proxy.tickHandlerClient.radialTime = 3;
                }
            }
        }
        else if (event.keyBind.equals((Object)Morph.config.getKeyBind("keyFavourite")) && Morph.proxy.tickHandlerClient.radialShow) {
            Morph.proxy.tickHandlerClient.selectRadialMenu();
            Morph.proxy.tickHandlerClient.radialShow = false;
        }
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onMouseEvent(final MouseEvent event) {
        if (Morph.proxy.tickHandlerClient.selectorShow) {
            final int k = event.dwheel;
            if (k != 0) {
                final TickHandlerClient tickHandlerClient = Morph.proxy.tickHandlerClient;
                final TickHandlerClient tickHandlerClient2 = Morph.proxy.tickHandlerClient;
                Morph.proxy.tickHandlerClient.getClass();
                final int n = 3;
                tickHandlerClient2.scrollTimer = n;
                tickHandlerClient.scrollTimerHori = n;
                if (GuiScreen.isShiftKeyDown()) {
                    Morph.proxy.tickHandlerClient.selectorSelectedHoriPrev = Morph.proxy.tickHandlerClient.selectorSelectedHori;
                    if (k > 0) {
                        final TickHandlerClient tickHandlerClient3 = Morph.proxy.tickHandlerClient;
                        --tickHandlerClient3.selectorSelectedHori;
                    }
                    else {
                        final TickHandlerClient tickHandlerClient4 = Morph.proxy.tickHandlerClient;
                        ++tickHandlerClient4.selectorSelectedHori;
                    }
                }
                else {
                    Morph.proxy.tickHandlerClient.selectorSelectedPrev = Morph.proxy.tickHandlerClient.selectorSelected;
                    if (k > 0) {
                        final TickHandlerClient tickHandlerClient5 = Morph.proxy.tickHandlerClient;
                        --tickHandlerClient5.selectorSelected;
                        if (Morph.proxy.tickHandlerClient.selectorSelected < 0) {
                            Morph.proxy.tickHandlerClient.selectorSelected = Morph.proxy.tickHandlerClient.playerMorphCatMap.size() - 1;
                        }
                    }
                    else {
                        final TickHandlerClient tickHandlerClient6 = Morph.proxy.tickHandlerClient;
                        ++tickHandlerClient6.selectorSelected;
                        if (Morph.proxy.tickHandlerClient.selectorSelected > Morph.proxy.tickHandlerClient.playerMorphCatMap.size() - 1) {
                            Morph.proxy.tickHandlerClient.selectorSelected = 0;
                        }
                    }
                }
                event.setCanceled(true);
            }
        }
        else if (Morph.proxy.tickHandlerClient.radialShow) {
            final TickHandlerClient tickHandlerClient7 = Morph.proxy.tickHandlerClient;
            tickHandlerClient7.radialDeltaX += event.dx / 100.0;
            final TickHandlerClient tickHandlerClient8 = Morph.proxy.tickHandlerClient;
            tickHandlerClient8.radialDeltaY += event.dy / 100.0;
            final double mag = Math.sqrt(Morph.proxy.tickHandlerClient.radialDeltaX * Morph.proxy.tickHandlerClient.radialDeltaX + Morph.proxy.tickHandlerClient.radialDeltaY * Morph.proxy.tickHandlerClient.radialDeltaY);
            if (mag > 1.0) {
                final TickHandlerClient tickHandlerClient9 = Morph.proxy.tickHandlerClient;
                tickHandlerClient9.radialDeltaX /= mag;
                final TickHandlerClient tickHandlerClient10 = Morph.proxy.tickHandlerClient;
                tickHandlerClient10.radialDeltaY /= mag;
            }
        }
    }
    
    @SubscribeEvent
    public void onLivingSetAttackTarget(final LivingSetAttackTargetEvent event) {
        if (!event.entityLiving.worldObj.isRemote) {
            final ArrayList<Ability> mobAbilities = AbilityHandler.getEntityAbilities(event.entityLiving.getClass());
            boolean hostile = false;
            for (final Ability ab : mobAbilities) {
                if (ab.getType().equalsIgnoreCase("hostile")) {
                    hostile = true;
                    break;
                }
            }
            if (event.target instanceof EntityPlayer) {
                final EntityPlayer player = (EntityPlayer)event.target;
                final MorphInfo info = Morph.proxy.tickHandlerServer.getPlayerMorphInfo(player);
                if (info != null) {
                    for (final Ability ab2 : info.morphAbilities) {
                        if (ab2 instanceof AbilityFear) {
                            final AbilityFear abFear = (AbilityFear)ab2;
                            for (final Class clz : abFear.classList) {
                                if (clz.isInstance(event.entityLiving)) {
                                    event.entityLiving.setRevengeTarget((EntityLivingBase)null);
                                    if (!(event.entityLiving instanceof EntityLiving)) {
                                        continue;
                                    }
                                    ((EntityLiving)event.entityLiving).setAttackTarget((EntityLivingBase)null);
                                }
                            }
                            break;
                        }
                    }
                    if (Morph.config.getInt("hostileAbilityMode") > 0 && hostile && !info.getMorphing() && info.morphProgress >= 80) {
                        boolean playerHostile = false;
                        for (final Ability ab3 : info.morphAbilities) {
                            if (ab3.getType().equalsIgnoreCase("hostile")) {
                                playerHostile = true;
                                break;
                            }
                        }
                        if (hostile && playerHostile) {
                            if ((info.nextState.entInstance.getClass() == event.entityLiving.getClass() && Morph.config.getInt("hostileAbilityMode") == 2) || (info.nextState.entInstance.getClass() != event.entityLiving.getClass() && Morph.config.getInt("hostileAbilityMode") == 3)) {
                                return;
                            }
                            if (Morph.config.getInt("hostileAbilityMode") == 4) {
                                final double dist = event.entityLiving.getDistanceToEntity((Entity)player);
                                if (dist < Morph.config.getInt("hostileAbilityDistanceCheck")) {
                                    return;
                                }
                            }
                            else if (Morph.config.getInt("hostileAbilityMode") == 5 && event.target.getLastAttacker() == event.entityLiving) {
                                return;
                            }
                            event.entityLiving.setRevengeTarget((EntityLivingBase)null);
                            if (event.entityLiving instanceof EntityLiving) {
                                ((EntityLiving)event.entityLiving).setAttackTarget((EntityLivingBase)null);
                            }
                        }
                    }
                }
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onSetupFog(final EntityViewRenderEvent.FogColors event) {
        final EntityPlayer player = (EntityPlayer)Minecraft.getMinecraft().thePlayer;
        if (player != null && Morph.proxy.tickHandlerClient.playerMorphInfo.get(player.getCommandSenderName()) != null) {
            final MorphInfo info = Morph.proxy.tickHandlerClient.playerMorphInfo.get(player.getCommandSenderName());
            if ((!info.getMorphing() && info.morphProgress >= 80) || (info.getMorphing() && info.morphProgress <= 80)) {
                for (final Ability ab : info.morphAbilities) {
                    if (ab.getType().equalsIgnoreCase("swim")) {
                        final AbilitySwim abilitySwim = (AbilitySwim)ab;
                        if (abilitySwim.canSurviveOutOfWater) {
                            continue;
                        }
                        if (player.isInWater()) {
                            float multi = 7.5f;
                            boolean hasSwim = false;
                            ArrayList<Ability> mobAbilities = AbilityHandler.getEntityAbilities(info.nextState.entInstance.getClass());
                            for (final Ability ab2 : mobAbilities) {
                                if (ab2.getType().equalsIgnoreCase("swim")) {
                                    hasSwim = true;
                                    break;
                                }
                            }
                            boolean alsoHasSwim = false;
                            mobAbilities = AbilityHandler.getEntityAbilities(info.prevState.entInstance.getClass());
                            for (final Ability ab3 : mobAbilities) {
                                if (ab3.getType().equalsIgnoreCase("swim")) {
                                    alsoHasSwim = true;
                                    break;
                                }
                            }
                            if (info.getMorphing()) {
                                if (!hasSwim) {
                                    multi -= 6.5f * MathHelper.clamp_float(info.morphProgress / 80.0f, 0.0f, 1.0f);
                                }
                                else if (!alsoHasSwim) {
                                    multi -= 6.5f * MathHelper.clamp_float((80.0f - info.morphProgress) / 80.0f, 0.0f, 1.0f);
                                }
                            }
                            event.red *= multi;
                            event.blue *= multi;
                            event.green *= multi;
                            break;
                        }
                        break;
                    }
                }
            }
        }
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onSetupFog(final EntityViewRenderEvent.FogDensity event) {
        final EntityPlayer player = (EntityPlayer)Minecraft.getMinecraft().thePlayer;
        if (player != null && Morph.proxy.tickHandlerClient.playerMorphInfo.get(player.getCommandSenderName()) != null) {
            final MorphInfo info = Morph.proxy.tickHandlerClient.playerMorphInfo.get(player.getCommandSenderName());
            if ((!info.getMorphing() && info.morphProgress >= 80) || (info.getMorphing() && info.morphProgress <= 80)) {
                for (final Ability ab : info.morphAbilities) {
                    if (ab.getType().equalsIgnoreCase("swim")) {
                        final AbilitySwim abilitySwim = (AbilitySwim)ab;
                        if (!abilitySwim.canSurviveOutOfWater) {
                            GL11.glFogi(2917, 2048);
                            if (player.isInWater()) {
                                event.density = 0.025f;
                                boolean hasSwim = false;
                                ArrayList<Ability> mobAbilities = AbilityHandler.getEntityAbilities(info.nextState.entInstance.getClass());
                                for (final Ability ab2 : mobAbilities) {
                                    if (ab2.getType().equalsIgnoreCase("swim")) {
                                        hasSwim = true;
                                        break;
                                    }
                                }
                                boolean alsoHasSwim = false;
                                mobAbilities = AbilityHandler.getEntityAbilities(info.prevState.entInstance.getClass());
                                for (final Ability ab3 : mobAbilities) {
                                    if (ab3.getType().equalsIgnoreCase("swim")) {
                                        alsoHasSwim = true;
                                        break;
                                    }
                                }
                                if (info.getMorphing()) {
                                    if (!hasSwim) {
                                        event.density += 0.05f * MathHelper.clamp_float(info.morphProgress / 80.0f, 0.0f, 1.0f);
                                    }
                                    else if (!alsoHasSwim) {
                                        event.density += 0.05f * MathHelper.clamp_float((80.0f - info.morphProgress) / 80.0f, 0.0f, 1.0f);
                                    }
                                }
                            }
                            else {
                                event.density = 0.075f;
                                boolean hasSwim = false;
                                ArrayList<Ability> mobAbilities = AbilityHandler.getEntityAbilities(info.nextState.entInstance.getClass());
                                for (final Ability ab2 : mobAbilities) {
                                    if (ab2.getType().equalsIgnoreCase("swim")) {
                                        hasSwim = true;
                                        break;
                                    }
                                }
                                boolean alsoHasSwim = false;
                                mobAbilities = AbilityHandler.getEntityAbilities(info.prevState.entInstance.getClass());
                                for (final Ability ab3 : mobAbilities) {
                                    if (ab3.getType().equalsIgnoreCase("swim")) {
                                        alsoHasSwim = true;
                                        break;
                                    }
                                }
                                if (info.getMorphing()) {
                                    if (!hasSwim) {
                                        event.density -= 0.073f * MathHelper.clamp_float(info.morphProgress / 80.0f, 0.0f, 1.0f);
                                    }
                                    else if (!alsoHasSwim) {
                                        event.density -= 0.073f * MathHelper.clamp_float((80.0f - info.morphProgress) / 80.0f, 0.0f, 1.0f);
                                    }
                                }
                            }
                            event.setCanceled(true);
                            break;
                        }
                        continue;
                    }
                }
            }
        }
    }
    
    @SubscribeEvent
    public void onPlayerSleep(final PlayerSleepInBedEvent event) {
        final EntityPlayer player = event.entityPlayer;
        final EntityPlayer.EnumStatus stats = EntityPlayer.EnumStatus.OTHER_PROBLEM;
        if (Morph.config.getSessionInt("canSleepMorphed") == 0) {
            if (FMLCommonHandler.instance().getEffectiveSide().isServer() && Morph.proxy.tickHandlerServer.getPlayerMorphInfo(player) != null) {
                event.result = stats;
                player.addChatMessage((IChatComponent)new ChatComponentTranslation("morph.denySleep", new Object[0]));
            }
            else if (FMLCommonHandler.instance().getEffectiveSide().isClient() && Morph.proxy.tickHandlerClient.playerMorphInfo.containsKey(player.getCommandSenderName())) {
                event.result = stats;
            }
        }
    }
    
    @SubscribeEvent
    public void onPlaySoundAtEntity(final PlaySoundAtEntityEvent event) {
        if (event.entity instanceof EntityPlayer && event.name.equalsIgnoreCase("damage.hit")) {
            final EntityPlayer player = (EntityPlayer)event.entity;
            if (FMLCommonHandler.instance().getEffectiveSide().isServer() && Morph.proxy.tickHandlerServer.getPlayerMorphInfo(player) != null) {
                final MorphInfo info = Morph.proxy.tickHandlerServer.getPlayerMorphInfo(player);
                event.name = EntityHelper.getHurtSound((Class)info.nextState.entInstance.getClass(), info.nextState.entInstance);
            }
            else if (FMLCommonHandler.instance().getEffectiveSide().isClient() && Morph.proxy.tickHandlerClient.playerMorphInfo.containsKey(player.getCommandSenderName())) {
                final MorphInfo info = Morph.proxy.tickHandlerClient.playerMorphInfo.get(player.getCommandSenderName());
                event.name = EntityHelper.getHurtSound((Class)info.nextState.entInstance.getClass(), info.nextState.entInstance);
            }
        }
    }
    
    @SubscribeEvent
    public void onLivingHurt(final LivingHurtEvent event) {
        if (event.source.getEntity() instanceof EntityPlayerMP && !(event.source instanceof EntityDamageSourceIndirect)) {
            final EntityPlayer player = (EntityPlayer)event.source.getEntity();
            final MorphInfo info = Morph.proxy.tickHandlerServer.getPlayerMorphInfo(player);
            if (info != null && player.getCurrentEquippedItem() == null) {
                for (final Ability ab : info.morphAbilities) {
                    if (ab instanceof AbilityPotionEffect) {
                        final AbilityPotionEffect abPot = (AbilityPotionEffect)ab;
                        event.entityLiving.addPotionEffect(new PotionEffect(abPot.potionId, abPot.duration, abPot.amplifier, abPot.ambient));
                    }
                }
            }
        }
    }
    
    @SubscribeEvent
    public void onLivingDeath(final LivingDeathEvent event) {
        if (!event.entityLiving.worldObj.isRemote) {
            if (Morph.config.getInt("loseMorphsOnDeath") >= 1 && event.entityLiving instanceof EntityPlayerMP) {
                final EntityPlayerMP player = (EntityPlayerMP)event.entityLiving;
                final MorphInfo info = Morph.proxy.tickHandlerServer.getPlayerMorphInfo((EntityPlayer)player);
                final MorphState state = Morph.proxy.tickHandlerServer.getSelfState(player.worldObj, (EntityPlayer)player);
                if (Morph.config.getInt("loseMorphsOnDeath") == 1) {
                    Morph.proxy.tickHandlerServer.removeAllPlayerMorphsExcludingCurrentMorph((EntityPlayer)player);
                }
                else if (info != null && info.nextState != state) {
                    final ArrayList<MorphState> states = Morph.proxy.tickHandlerServer.getPlayerMorphs(player.worldObj, (EntityPlayer)player);
                    states.remove(info.nextState);
                }
                MorphHandler.updatePlayerOfMorphStates(player, null, true);
                if (info != null && state != null) {
                    final MorphInfo info2 = new MorphInfo(player.getCommandSenderName(), info.nextState, state);
                    info2.setMorphing(true);
                    info2.healthOffset = info.healthOffset;
                    Morph.proxy.tickHandlerServer.setPlayerMorphInfo((EntityPlayer)player, info2);
                    PacketHandler.sendToAll((EnumMap)Morph.channels, (AbstractPacket)info2.getMorphInfoAsPacket());
                    player.worldObj.playSoundAtEntity((Entity)player, "morph:morph", 1.0f, 1.0f);
                }
            }
            if (event.source.getEntity() instanceof EntityPlayerMP && event.entityLiving != event.source.getEntity()) {
                final EntityPlayerMP player = (EntityPlayerMP)event.source.getEntity();
                EntityLivingBase living = event.entityLiving;
                if (event.entityLiving instanceof EntityPlayerMP) {
                    final EntityPlayerMP player2 = (EntityPlayerMP)event.entityLiving;
                    final MorphInfo info3 = Morph.proxy.tickHandlerServer.getPlayerMorphInfo((EntityPlayer)player2);
                    if (info3 != null) {
                        if (info3.getMorphing()) {
                            living = info3.prevState.entInstance;
                        }
                        else {
                            living = info3.nextState.entInstance;
                        }
                    }
                }
                if (EntityHelper.morphPlayer(player, living, true) && !(event.entityLiving instanceof EntityPlayerMP) && !(event.entityLiving instanceof IBossDisplayData)) {
                    living.setDead();
                }
            }
            if ((Morph.classToKillForFlight != null && Morph.classToKillForFlight.isInstance(event.entityLiving)) || (Morph.classToKillForFlight == null && event.entityLiving instanceof EntityWither)) {
                if (event.source.getEntity() instanceof EntityPlayerMP) {
                    final EntityPlayerMP player = (EntityPlayerMP)event.source.getEntity();
                    final boolean firstKill = !Morph.proxy.tickHandlerServer.getMorphDataFromPlayer((EntityPlayer)player).getBoolean("hasKilledWither");
                    Morph.proxy.tickHandlerServer.getMorphDataFromPlayer((EntityPlayer)player).setBoolean("hasKilledWither", true);
                    if (Morph.config.getInt("disableEarlyGameFlight") == 2 && firstKill) {
                        Morph.proxy.tickHandlerServer.updateSession((EntityPlayer)player);
                    }
                }
                if (!Morph.proxy.tickHandlerServer.saveData.hasKilledWither) {
                    Morph.proxy.tickHandlerServer.saveData.hasKilledWither = true;
                    Morph.proxy.tickHandlerServer.saveData.markDirty();
                    if (Morph.config.getInt("disableEarlyGameFlight") == 2) {
                        Morph.config.updateSession("allowFlight", (Object)1);
                        Morph.proxy.tickHandlerServer.updateSession(null);
                    }
                }
            }
        }
    }
    
    @SubscribeEvent
    public void onInteract(final EntityInteractEvent event) {
    }
    
    @SubscribeEvent
    public void onWorldLoad(final WorldEvent.Load event) {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer() && event.world.provider.dimensionId == 0) {
            final WorldServer world = (WorldServer)event.world;
            MorphSaveData saveData = (MorphSaveData)world.perWorldStorage.loadData((Class)MorphSaveData.class, "MorphSaveData");
            if (saveData == null) {
                saveData = new MorphSaveData("MorphSaveData");
                world.perWorldStorage.setData("MorphSaveData", (WorldSavedData)saveData);
            }
            Morph.proxy.tickHandlerServer.saveData = saveData;
            if ((Morph.config.getInt("disableEarlyGameFlight") == 1 && !Morph.proxy.tickHandlerServer.saveData.hasTravelledToNether) || (Morph.config.getInt("disableEarlyGameFlight") == 2 && !Morph.proxy.tickHandlerServer.saveData.hasKilledWither)) {
                Morph.config.updateSession("allowFlight", (Object)0);
            }
        }
    }
    
    @SubscribeEvent
    public void onClientConnect(final FMLNetworkEvent.ClientConnectedToServerEvent event) {
        this.onClientConnection();
    }
    
    @SubscribeEvent
    public void onClientDisconnect(final FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        this.onClientConnection();
    }
    
    public void onClientConnection() {
        Morph.config.resetSession();
        Morph.proxy.tickHandlerClient.playerMorphInfo.clear();
        Morph.proxy.tickHandlerClient.playerMorphCatMap.clear();
    }
    
    @SubscribeEvent
    public void onPlayerLogin(final PlayerEvent.PlayerLoggedInEvent event) {
        Morph.proxy.tickHandlerServer.updateSession(event.player);
        final ArrayList list = Morph.proxy.tickHandlerServer.getPlayerMorphs(event.player.worldObj, event.player);
        final NBTTagCompound tag = Morph.proxy.tickHandlerServer.getMorphDataFromPlayer(event.player);
        MorphHandler.addOrGetMorphState(list, new MorphState(event.player.worldObj, event.player.getCommandSenderName(), event.player.getCommandSenderName(), null, event.player.worldObj.isRemote));
        for (int count = tag.getInteger("morphStatesCount"), i = 0; i < count; ++i) {
            final MorphState state = new MorphState(event.player.worldObj, event.player.getCommandSenderName(), event.player.getCommandSenderName(), null, false);
            state.readTag(event.player.worldObj, tag.getCompoundTag("morphState" + i));
            if (!state.identifier.equalsIgnoreCase("")) {
                MorphHandler.addOrGetMorphState(list, state);
            }
        }
        final NBTTagCompound tag2 = tag.getCompoundTag("morphData");
        if (tag2.hasKey("playerName")) {
            final MorphInfo info = new MorphInfo();
            info.readNBT(tag2);
            if (!info.nextState.playerName.equals(info.nextState.playerMorph)) {
                Morph.proxy.tickHandlerServer.setPlayerMorphInfo(event.player, info);
                MorphHandler.addOrGetMorphState(list, info.nextState);
                PacketHandler.sendToAll((EnumMap)Morph.channels, (AbstractPacket)info.getMorphInfoAsPacket());
            }
        }
        MorphHandler.updatePlayerOfMorphStates((EntityPlayerMP)event.player, null, true);
        for (final Map.Entry<String, MorphInfo> e : Morph.proxy.tickHandlerServer.playerMorphInfo.entrySet()) {
            if (e.getKey().equalsIgnoreCase(event.player.getCommandSenderName())) {
                continue;
            }
            PacketHandler.sendToPlayer((EnumMap)Morph.channels, (AbstractPacket)e.getValue().getMorphInfoAsPacket(), event.player);
        }
        final MorphInfo info = Morph.proxy.tickHandlerServer.getPlayerMorphInfo(event.player);
        if (info != null) {
            event.player.setSize(info.nextState.entInstance.width, info.nextState.entInstance.height);
            event.player.setPosition(event.player.posX, event.player.posY, event.player.posZ);
            event.player.eyeHeight = ((info.nextState.entInstance instanceof EntityPlayer) ? (((EntityPlayer)info.nextState.entInstance).getCommandSenderName().equalsIgnoreCase(event.player.getCommandSenderName()) ? event.player.getDefaultEyeHeight() : ((EntityPlayer)info.nextState.entInstance).getDefaultEyeHeight()) : (info.nextState.entInstance.getEyeHeight() - event.player.yOffset));
            double nextMaxHealth = MathHelper.clamp_double(info.nextState.entInstance.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue(), 0.0, 20.0) + info.healthOffset;
            if (nextMaxHealth < 1.0) {
                nextMaxHealth = 1.0;
            }
            if (nextMaxHealth != event.player.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue()) {
                event.player.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(nextMaxHealth);
                event.player.setHealth((float)nextMaxHealth);
            }
        }
    }
    
    @SubscribeEvent
    public void onPlayerLogout(final PlayerEvent.PlayerLoggedOutEvent event) {
        final MorphInfo info = Morph.proxy.tickHandlerServer.playerMorphInfo.get(event.player.getCommandSenderName());
        if (info != null) {
            if (info.morphProgress < 80) {
                info.morphProgress = 80;
                double nextMaxHealth = MathHelper.clamp_double(info.nextState.entInstance.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue(), 1.0, 20.0) + info.healthOffset;
                if (nextMaxHealth < 1.0) {
                    nextMaxHealth = 1.0;
                }
                if (nextMaxHealth != event.player.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue()) {
                    event.player.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(nextMaxHealth);
                }
            }
            final NBTTagCompound tag1 = new NBTTagCompound();
            info.writeNBT(tag1);
            Morph.proxy.tickHandlerServer.getMorphDataFromPlayer(event.player).setTag("morphData", (NBTBase)tag1);
        }
        final ArrayList<MorphState> states = Morph.proxy.tickHandlerServer.playerMorphs.get(event.player.getCommandSenderName());
        if (states != null) {
            Morph.proxy.tickHandlerServer.getMorphDataFromPlayer(event.player).setInteger("morphStatesCount", states.size());
            for (int i = 0; i < states.size(); ++i) {
                Morph.proxy.tickHandlerServer.getMorphDataFromPlayer(event.player).setTag("morphState" + i, (NBTBase)states.get(i).getTag());
            }
        }
        Morph.proxy.tickHandlerServer.playerMorphs.remove(event.player.getCommandSenderName());
        Morph.proxy.tickHandlerServer.playerMorphInfo.remove(event.player.getCommandSenderName());
    }
    
    @SubscribeEvent
    public void onPlayerChangedDimension(final PlayerEvent.PlayerChangedDimensionEvent event) {
        final MorphInfo info = Morph.proxy.tickHandlerServer.getPlayerMorphInfo(event.player);
        if (info != null) {
            event.player.setSize(info.nextState.entInstance.width, info.nextState.entInstance.height);
            event.player.setPosition(event.player.posX, event.player.posY, event.player.posZ);
            event.player.eyeHeight = ((info.nextState.entInstance instanceof EntityPlayer) ? (((EntityPlayer)info.nextState.entInstance).getCommandSenderName().equalsIgnoreCase(event.player.getCommandSenderName()) ? event.player.getDefaultEyeHeight() : ((EntityPlayer)info.nextState.entInstance).getDefaultEyeHeight()) : (info.nextState.entInstance.getEyeHeight() - event.player.yOffset));
            double nextMaxHealth = MathHelper.clamp_double(info.nextState.entInstance.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue(), 0.0, 20.0) + info.healthOffset;
            if (nextMaxHealth < 1.0) {
                nextMaxHealth = 1.0;
            }
            if (nextMaxHealth != event.player.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue()) {
                event.player.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(nextMaxHealth);
                event.player.setHealth((float)nextMaxHealth);
            }
        }
        if (event.player.dimension == -1 && Morph.proxy.tickHandlerServer.saveData != null) {
            final boolean firstVisit = !Morph.proxy.tickHandlerServer.getMorphDataFromPlayer(event.player).getBoolean("hasTravelledToNether");
            Morph.proxy.tickHandlerServer.getMorphDataFromPlayer(event.player).setBoolean("hasTravelledToNether", true);
            if (Morph.config.getInt("disableEarlyGameFlight") == 1 && firstVisit) {
                Morph.proxy.tickHandlerServer.updateSession(event.player);
            }
            if (!Morph.proxy.tickHandlerServer.saveData.hasTravelledToNether) {
                Morph.proxy.tickHandlerServer.saveData.hasTravelledToNether = true;
                Morph.proxy.tickHandlerServer.saveData.markDirty();
                if (Morph.config.getInt("disableEarlyGameFlight") == 1) {
                    Morph.config.updateSession("allowFlight", (Object)1);
                    Morph.proxy.tickHandlerServer.updateSession(null);
                }
            }
        }
    }
    
    @SubscribeEvent
    public void onPlayerRespawn(final PlayerEvent.PlayerRespawnEvent event) {
        final MorphInfo info = Morph.proxy.tickHandlerServer.getPlayerMorphInfo(event.player);
        if (info != null) {
            event.player.setSize(info.nextState.entInstance.width, info.nextState.entInstance.height);
            event.player.setPosition(event.player.posX, event.player.posY, event.player.posZ);
            event.player.eyeHeight = ((info.nextState.entInstance instanceof EntityPlayer) ? (((EntityPlayer)info.nextState.entInstance).getCommandSenderName().equalsIgnoreCase(event.player.getCommandSenderName()) ? event.player.getDefaultEyeHeight() : ((EntityPlayer)info.nextState.entInstance).getDefaultEyeHeight()) : (info.nextState.entInstance.getEyeHeight() - event.player.yOffset));
            double nextMaxHealth = MathHelper.clamp_double(info.nextState.entInstance.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue(), 0.0, 20.0) + info.healthOffset;
            if (nextMaxHealth < 1.0) {
                nextMaxHealth = 1.0;
            }
            if (nextMaxHealth != event.player.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue()) {
                event.player.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(nextMaxHealth);
                event.player.setHealth((float)nextMaxHealth);
            }
        }
    }
}
