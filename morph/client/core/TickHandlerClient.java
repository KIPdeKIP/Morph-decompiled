package morph.client.core;

import morph.client.render.*;
import morph.client.morph.*;
import morph.common.morph.*;
import morph.client.model.*;
import net.minecraft.client.model.*;
import cpw.mods.fml.common.gameevent.*;
import net.minecraft.client.*;
import net.minecraft.entity.player.*;
import org.lwjgl.input.*;
import org.lwjgl.opengl.*;
import morph.common.*;
import net.minecraftforge.client.*;
import morph.api.*;
import net.minecraft.client.entity.*;
import cpw.mods.fml.common.eventhandler.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.entity.*;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.boss.*;
import net.minecraft.client.multiplayer.*;
import net.minecraft.world.*;
import net.minecraft.client.renderer.*;
import net.minecraft.util.*;
import morph.common.ability.*;
import net.minecraft.potion.*;
import ichun.client.render.*;
import morph.common.packet.*;
import java.util.*;
import ichun.common.core.network.*;

public class TickHandlerClient
{
    public long clock;
    public RenderMorph renderMorphInstance;
    public RenderPlayerHand renderHandInstance;
    public float playerRenderShadowSize;
    public HashMap<String, MorphInfoClient> playerMorphInfo;
    public LinkedHashMap<String, ArrayList<MorphState>> playerMorphCatMap;
    public float renderTick;
    public float playerHeight;
    public float ySize;
    public float eyeHeight;
    public boolean shiftedPosY;
    public boolean allowRender;
    public boolean forceRender;
    public boolean renderingMorph;
    public byte renderingPlayer;
    public boolean selectorShow;
    public int selectorTimer;
    public int selectorSelectedPrev;
    public int selectorSelected;
    public int selectorSelectedHoriPrev;
    public int selectorSelectedHori;
    public long systemTime;
    public int currentItem;
    public int scrollTimer;
    public int scrollTimerHori;
    public int abilityScroll;
    public boolean radialShow;
    public float radialPlayerYaw;
    public float radialPlayerPitch;
    public double radialDeltaX;
    public double radialDeltaY;
    public int radialTime;
    public ArrayList<MorphState> favouriteStates;
    public final int selectorShowTime = 10;
    public final int scrollTime = 3;
    public static final ResourceLocation rlFavourite;
    public static final ResourceLocation rlSelected;
    public static final ResourceLocation rlUnselected;
    public static final ResourceLocation rlUnselectedSide;
    public static final ResourceLocation rlGuiInventory;
    
    public TickHandlerClient() {
        this.playerRenderShadowSize = -1.0f;
        this.playerMorphInfo = new HashMap<String, MorphInfoClient>();
        this.playerMorphCatMap = new LinkedHashMap<String, ArrayList<MorphState>>();
        this.playerHeight = 1.8f;
        this.favouriteStates = new ArrayList<MorphState>();
        (this.renderMorphInstance = new RenderMorph(new ModelMorph(), 0.0f)).setRenderManager(RenderManager.instance);
        (this.renderHandInstance = new RenderPlayerHand()).setRenderManager(RenderManager.instance);
    }
    
    @SubscribeEvent
    public void renderTick(final TickEvent.RenderTickEvent event) {
        final Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld != null) {
            if (event.phase == TickEvent.Phase.START) {
                this.renderTick = event.renderTickTime;
                final MorphInfoClient info1 = this.playerMorphInfo.get(mc.thePlayer.getCommandSenderName());
                if (info1 != null) {
                    float prog = (info1.morphProgress > 10) ? ((info1.morphProgress + this.renderTick) / 60.0f) : 0.0f;
                    if (prog > 1.0f) {
                        prog = 1.0f;
                    }
                    prog = (float)Math.pow(prog, 2.0);
                    final float prev = (info1.prevState != null && !(info1.prevState.entInstance instanceof EntityPlayer)) ? info1.prevState.entInstance.getEyeHeight() : (mc.thePlayer.yOffset + mc.thePlayer.getDefaultEyeHeight());
                    final float next = (info1.nextState != null && !(info1.nextState.entInstance instanceof EntityPlayer)) ? info1.nextState.entInstance.getEyeHeight() : (mc.thePlayer.yOffset + mc.thePlayer.getDefaultEyeHeight());
                    this.ySize = mc.thePlayer.yOffset - (prev + (next - prev) * prog) + mc.thePlayer.getDefaultEyeHeight();
                    this.eyeHeight = mc.thePlayer.eyeHeight;
                    final EntityClientPlayerMP thePlayer = mc.thePlayer;
                    thePlayer.lastTickPosY -= this.ySize;
                    final EntityClientPlayerMP thePlayer2 = mc.thePlayer;
                    thePlayer2.prevPosY -= this.ySize;
                    final EntityClientPlayerMP thePlayer3 = mc.thePlayer;
                    thePlayer3.posY -= this.ySize;
                    mc.thePlayer.eyeHeight = mc.thePlayer.getDefaultEyeHeight();
                    this.shiftedPosY = true;
                }
                if (this.radialShow) {
                    Mouse.getDX();
                    Mouse.getDY();
                    final MouseHelper mouseHelper = mc.mouseHelper;
                    final MouseHelper mouseHelper2 = mc.mouseHelper;
                    final boolean b = false;
                    mouseHelper2.deltaY = (b ? 1 : 0);
                    mouseHelper.deltaX = (b ? 1 : 0);
                    final EntityLivingBase renderViewEntity = mc.renderViewEntity;
                    final EntityLivingBase renderViewEntity2 = mc.renderViewEntity;
                    final float radialPlayerYaw = this.radialPlayerYaw;
                    renderViewEntity2.rotationYawHead = radialPlayerYaw;
                    renderViewEntity.prevRotationYawHead = radialPlayerYaw;
                    final EntityLivingBase renderViewEntity3 = mc.renderViewEntity;
                    final EntityLivingBase renderViewEntity4 = mc.renderViewEntity;
                    final float radialPlayerYaw2 = this.radialPlayerYaw;
                    renderViewEntity4.rotationYaw = radialPlayerYaw2;
                    renderViewEntity3.prevRotationYaw = radialPlayerYaw2;
                    final EntityLivingBase renderViewEntity5 = mc.renderViewEntity;
                    final EntityLivingBase renderViewEntity6 = mc.renderViewEntity;
                    final float radialPlayerPitch = this.radialPlayerPitch;
                    renderViewEntity6.rotationPitch = radialPlayerPitch;
                    renderViewEntity5.prevRotationPitch = radialPlayerPitch;
                }
            }
            else {
                final MorphInfoClient info2 = this.playerMorphInfo.get(mc.thePlayer.getCommandSenderName());
                if (info2 != null) {
                    this.shiftedPosY = false;
                    final EntityClientPlayerMP thePlayer4 = mc.thePlayer;
                    thePlayer4.lastTickPosY += this.ySize;
                    final EntityClientPlayerMP thePlayer5 = mc.thePlayer;
                    thePlayer5.prevPosY += this.ySize;
                    final EntityClientPlayerMP thePlayer6 = mc.thePlayer;
                    thePlayer6.posY += this.ySize;
                    mc.thePlayer.eyeHeight = this.eyeHeight;
                }
                final float bossHealthScale = BossStatus.healthScale;
                final int bossStatusBarTime = BossStatus.statusBarTime;
                final String bossName = BossStatus.bossName;
                final boolean hasColorModifier = BossStatus.hasColorModifier;
                if ((this.selectorTimer > 0 || this.selectorShow) && !mc.gameSettings.hideGUI) {
                    GL11.glPushMatrix();
                    float progress = (11.0f - (this.selectorTimer + (1.0f - this.renderTick))) / 11.0f;
                    if (this.selectorShow) {
                        progress = 1.0f - progress;
                    }
                    if (this.selectorShow && this.selectorTimer == 0) {
                        progress = 0.0f;
                    }
                    progress = (float)Math.pow(progress, 2.0);
                    GL11.glTranslatef(-52.0f * progress, 0.0f, 0.0f);
                    final ScaledResolution reso = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
                    int gap = (reso.getScaledHeight() - 210) / 2;
                    final double size = 42.0;
                    final double width1 = 0.0;
                    GL11.glPushMatrix();
                    int maxShowable = (int)Math.ceil(reso.getScaledHeight() / size) + 2;
                    if ((this.selectorSelected == 0 && this.selectorSelectedPrev > 0) || (this.selectorSelectedPrev == 0 && this.selectorSelected > 0)) {
                        maxShowable = 150;
                    }
                    float progressV = (3.0f - (this.scrollTimer - this.renderTick)) / 3.0f;
                    progressV = (float)Math.pow(progressV, 2.0);
                    if (progressV > 1.0f) {
                        progressV = 1.0f;
                        this.selectorSelectedPrev = this.selectorSelected;
                    }
                    float progressH = (3.0f - (this.scrollTimerHori - this.renderTick)) / 3.0f;
                    progressH = (float)Math.pow(progressH, 2.0);
                    if (progressH > 1.0f) {
                        progressH = 1.0f;
                        this.selectorSelectedHoriPrev = this.selectorSelectedHori;
                    }
                    GL11.glTranslatef(0.0f, (this.selectorSelected - this.selectorSelectedPrev) * 42.0f * (1.0f - progressV), 0.0f);
                    GL11.glDisable(2929);
                    GL11.glDepthMask(false);
                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                    GL11.glDisable(3008);
                    GL11.glEnable(3042);
                    GL11.glBlendFunc(770, 771);
                    int i = 0;
                    Iterator<Map.Entry<String, ArrayList<MorphState>>> ite = this.playerMorphCatMap.entrySet().iterator();
                    while (ite.hasNext()) {
                        final Map.Entry<String, ArrayList<MorphState>> e = ite.next();
                        if (i > this.selectorSelected + maxShowable || i < this.selectorSelected - maxShowable) {
                            ++i;
                        }
                        else {
                            final double height1 = gap + size * (i - this.selectorSelected);
                            final ArrayList<MorphState> states = e.getValue();
                            if (states == null || states.isEmpty()) {
                                ite.remove();
                                ++i;
                                break;
                            }
                            final Tessellator tessellator = Tessellator.instance;
                            if (i == this.selectorSelected) {
                                if (this.selectorSelectedHori < 0) {
                                    this.selectorSelectedHori = states.size() - 1;
                                }
                                if (this.selectorSelectedHori >= states.size()) {
                                    this.selectorSelectedHori = 0;
                                }
                                boolean newSlide = false;
                                if (progressV < 1.0f && this.selectorSelectedPrev != this.selectorSelected) {
                                    this.selectorSelectedHoriPrev = states.size() - 1;
                                    this.selectorSelectedHori = 0;
                                    newSlide = true;
                                }
                                if (!this.selectorShow) {
                                    this.selectorSelectedHori = states.size() - 1;
                                    newSlide = true;
                                }
                                else if (progress > 0.0f) {
                                    this.selectorSelectedHoriPrev = states.size() - 1;
                                    newSlide = true;
                                }
                                for (int j = 0; j < states.size(); ++j) {
                                    GL11.glPushMatrix();
                                    GL11.glTranslated((newSlide && j == 0) ? 0.0 : ((double)((this.selectorSelectedHori - this.selectorSelectedHoriPrev) * 42.0f * (1.0f - progressH))), 0.0, 0.0);
                                    mc.getTextureManager().bindTexture((states.size() == 1 || j == states.size() - 1) ? TickHandlerClient.rlUnselected : TickHandlerClient.rlUnselectedSide);
                                    final double dist = size * (j - this.selectorSelectedHori);
                                    tessellator.startDrawingQuads();
                                    tessellator.setColorOpaque_F(1.0f, 1.0f, 1.0f);
                                    tessellator.addVertexWithUV(width1 + dist, height1 + size, -90.0 + j, 0.0, 1.0);
                                    tessellator.addVertexWithUV(width1 + dist + size, height1 + size, -90.0 + j, 1.0, 1.0);
                                    tessellator.addVertexWithUV(width1 + dist + size, height1, -90.0 + j, 1.0, 0.0);
                                    tessellator.addVertexWithUV(width1 + dist, height1, -90.0 + j, 0.0, 0.0);
                                    tessellator.draw();
                                    GL11.glPopMatrix();
                                }
                            }
                            else {
                                mc.getTextureManager().bindTexture(TickHandlerClient.rlUnselected);
                                tessellator.startDrawingQuads();
                                tessellator.setColorOpaque_F(1.0f, 1.0f, 1.0f);
                                tessellator.addVertexWithUV(width1, height1 + size, -90.0, 0.0, 1.0);
                                tessellator.addVertexWithUV(width1 + size, height1 + size, -90.0, 1.0, 1.0);
                                tessellator.addVertexWithUV(width1 + size, height1, -90.0, 1.0, 0.0);
                                tessellator.addVertexWithUV(width1, height1, -90.0, 0.0, 0.0);
                                tessellator.draw();
                            }
                            ++i;
                        }
                    }
                    GL11.glDisable(3042);
                    int height2 = gap;
                    GL11.glDepthMask(true);
                    GL11.glEnable(2929);
                    GL11.glEnable(3008);
                    gap += 36;
                    i = 0;
                    ite = this.playerMorphCatMap.entrySet().iterator();
                    while (ite.hasNext()) {
                        final Map.Entry<String, ArrayList<MorphState>> e2 = ite.next();
                        if (i > this.selectorSelected + maxShowable || i < this.selectorSelected - maxShowable) {
                            ++i;
                        }
                        else {
                            height2 = gap + (int)size * (i - this.selectorSelected);
                            final ArrayList<MorphState> states2 = e2.getValue();
                            if (i == this.selectorSelected) {
                                boolean newSlide2 = false;
                                if (progressV < 1.0f && this.selectorSelectedPrev != this.selectorSelected) {
                                    this.selectorSelectedHoriPrev = states2.size() - 1;
                                    this.selectorSelectedHori = 0;
                                    newSlide2 = true;
                                }
                                if (!this.selectorShow) {
                                    this.selectorSelectedHori = states2.size() - 1;
                                    newSlide2 = true;
                                }
                                for (int k = 0; k < states2.size(); ++k) {
                                    final MorphState state = states2.get(k);
                                    GL11.glPushMatrix();
                                    GL11.glTranslated((newSlide2 && k == 0) ? 0.0 : ((double)((this.selectorSelectedHori - this.selectorSelectedHoriPrev) * 42.0f * (1.0f - progressH))), 0.0, 0.0);
                                    final double dist2 = size * (k - this.selectorSelectedHori);
                                    GL11.glTranslated(dist2, 0.0, 0.0);
                                    final float entSize = (state.entInstance.width > state.entInstance.height) ? state.entInstance.width : state.entInstance.height;
                                    float prog2 = (k - this.selectorSelectedHori == 0) ? ((this.selectorShow ? (3.0f - this.scrollTimerHori + this.renderTick) : (this.scrollTimerHori - this.renderTick)) / 3.0f) : 0.0f;
                                    prog2 = MathHelper.clamp_float(prog2, 0.0f, 1.0f);
                                    final float scaleMag = (2.5f + (entSize - 2.5f) * prog2) / entSize;
                                    this.drawEntityOnScreen(state, state.entInstance, 20, height2, (entSize > 2.5f) ? (16.0f * scaleMag) : 16.0f, 2.0f, 2.0f, this.renderTick, true, k == states2.size() - 1);
                                    GL11.glPopMatrix();
                                }
                            }
                            else {
                                final MorphState state2 = states2.get(0);
                                final float entSize2 = (state2.entInstance.width > state2.entInstance.height) ? state2.entInstance.width : state2.entInstance.height;
                                float prog3 = (this.selectorSelected == i) ? ((this.selectorShow ? (3.0f - this.scrollTimer + this.renderTick) : (this.scrollTimer - this.renderTick)) / 3.0f) : 0.0f;
                                prog3 = MathHelper.clamp_float(prog3, 0.0f, 1.0f);
                                final float scaleMag2 = 2.5f / entSize2;
                                this.drawEntityOnScreen(state2, state2.entInstance, 20, height2, (entSize2 > 2.5f) ? (16.0f * scaleMag2) : 16.0f, 2.0f, 2.0f, this.renderTick, this.selectorSelected == i, true);
                            }
                            GL11.glTranslatef(0.0f, 0.0f, 20.0f);
                            ++i;
                        }
                    }
                    GL11.glPopMatrix();
                    if (this.selectorShow) {
                        GL11.glEnable(3042);
                        GL11.glBlendFunc(770, 771);
                        gap -= 36;
                        height2 = gap;
                        mc.getTextureManager().bindTexture(TickHandlerClient.rlSelected);
                        final Tessellator tessellator2 = Tessellator.instance;
                        tessellator2.startDrawingQuads();
                        tessellator2.setColorOpaque_F(1.0f, 1.0f, 1.0f);
                        tessellator2.addVertexWithUV(width1, height2 + size, -90.0, 0.0, 1.0);
                        tessellator2.addVertexWithUV(width1 + size, height2 + size, -90.0, 1.0, 1.0);
                        tessellator2.addVertexWithUV(width1 + size, (double)height2, -90.0, 1.0, 0.0);
                        tessellator2.addVertexWithUV(width1, (double)height2, -90.0, 0.0, 0.0);
                        tessellator2.draw();
                        GL11.glDisable(3042);
                    }
                    GL11.glPopMatrix();
                }
                if (this.radialShow && !mc.gameSettings.hideGUI) {
                    double mag = Math.sqrt(Morph.proxy.tickHandlerClient.radialDeltaX * Morph.proxy.tickHandlerClient.radialDeltaX + Morph.proxy.tickHandlerClient.radialDeltaY * Morph.proxy.tickHandlerClient.radialDeltaY);
                    final double magAcceptance = 0.8;
                    final ScaledResolution reso2 = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
                    float prog4 = (3.0f - this.radialTime + this.renderTick) / 3.0f;
                    if (prog4 > 1.0f) {
                        prog4 = 1.0f;
                    }
                    float rad = ((mag > magAcceptance) ? 0.85f : 0.82f) * prog4;
                    int radius = 80;
                    radius *= (int)Math.pow(prog4, 0.5);
                    if (!mc.gameSettings.hideGUI) {
                        GL11.glEnable(3042);
                        GL11.glBlendFunc(770, 771);
                        GL11.glMatrixMode(5888);
                        GL11.glPushMatrix();
                        GL11.glLoadIdentity();
                        GL11.glMatrixMode(5889);
                        GL11.glPushMatrix();
                        GL11.glLoadIdentity();
                        final int NUM_PIZZA_SLICES = 100;
                        final double zLev = 0.05;
                        GL11.glDisable(3553);
                        final int stencilBit = MinecraftForgeClient.reserveStencilBit();
                        if (stencilBit >= 0) {
                            GL11.glEnable(2960);
                            GL11.glDepthMask(false);
                            GL11.glColorMask(false, false, false, false);
                            final int stencilMask = 1 << stencilBit;
                            GL11.glStencilMask(stencilMask);
                            GL11.glStencilFunc(519, stencilMask, stencilMask);
                            GL11.glStencilOp(7680, 7680, 7681);
                            GL11.glClear(1024);
                            rad = ((mag > magAcceptance) ? 0.85f : 0.82f) * prog4 * (257.0f / reso2.getScaledHeight());
                            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                            GL11.glBegin(6);
                            GL11.glVertex3d(0.0, 0.0, zLev);
                            for (int l = 0; l <= NUM_PIZZA_SLICES; ++l) {
                                final double angle = 6.283185307179586 * l / NUM_PIZZA_SLICES;
                                GL11.glVertex3d(Math.cos(angle) * reso2.getScaledHeight_double() / reso2.getScaledWidth_double() * rad, Math.sin(angle) * rad, zLev);
                            }
                            GL11.glEnd();
                            GL11.glStencilFunc(519, 0, stencilMask);
                            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                            rad = 0.44f * prog4 * (257.0f / reso2.getScaledHeight());
                            GL11.glBegin(6);
                            GL11.glVertex3d(0.0, 0.0, zLev);
                            for (int l = 0; l <= NUM_PIZZA_SLICES; ++l) {
                                final double angle = 6.283185307179586 * l / NUM_PIZZA_SLICES;
                                GL11.glVertex3d(Math.cos(angle) * reso2.getScaledHeight_double() / reso2.getScaledWidth_double() * rad, Math.sin(angle) * rad, zLev);
                            }
                            GL11.glEnd();
                            GL11.glStencilMask(0);
                            GL11.glStencilFunc(514, stencilMask, stencilMask);
                            GL11.glDepthMask(true);
                            GL11.glColorMask(true, true, true, true);
                        }
                        rad = ((mag > magAcceptance) ? 0.85f : 0.82f) * prog4 * (257.0f / reso2.getScaledHeight());
                        GL11.glColor4f(0.0f, 0.0f, 0.0f, (mag > magAcceptance) ? 0.6f : 0.4f);
                        GL11.glBegin(6);
                        GL11.glVertex3d(0.0, 0.0, zLev);
                        for (int m = 0; m <= NUM_PIZZA_SLICES; ++m) {
                            final double angle2 = 6.283185307179586 * m / NUM_PIZZA_SLICES;
                            GL11.glVertex3d(Math.cos(angle2) * reso2.getScaledHeight_double() / reso2.getScaledWidth_double() * rad, Math.sin(angle2) * rad, zLev);
                        }
                        GL11.glEnd();
                        if (stencilBit >= 0) {
                            GL11.glDisable(2960);
                        }
                        MinecraftForgeClient.releaseStencilBit(stencilBit);
                        GL11.glEnable(3553);
                        GL11.glPopMatrix();
                        GL11.glMatrixMode(5888);
                        GL11.glPopMatrix();
                    }
                    GL11.glPushMatrix();
                    final int showAb = Morph.config.getSessionInt("showAbilitiesInGui");
                    Morph.config.updateSession("showAbilitiesInGui", (Object)0);
                    double radialAngle = -720.0;
                    if (mag > magAcceptance) {
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
                    }
                    if (mag > 0.9999999) {
                        mag = Math.round(mag);
                    }
                    GL11.glDepthMask(true);
                    GL11.glEnable(2929);
                    GL11.glEnable(3008);
                    for (int i2 = 0; i2 < this.favouriteStates.size(); ++i2) {
                        double angle3 = 6.283185307179586 * i2 / this.favouriteStates.size();
                        angle3 -= Math.toRadians(90.0);
                        final float leeway = 360.0f / this.favouriteStates.size();
                        boolean selected = false;
                        if (mag > magAcceptance * 0.75 && ((i2 == 0 && ((radialAngle < leeway / 2.0f && radialAngle >= 0.0) || radialAngle > 360.0f - leeway / 2.0f)) || (i2 != 0 && radialAngle < leeway * i2 + leeway / 2.0f && radialAngle > leeway * i2 - leeway / 2.0f))) {
                            selected = true;
                        }
                        this.favouriteStates.get(i2).isFavourite = false;
                        final float entSize2 = (this.favouriteStates.get(i2).entInstance.width > this.favouriteStates.get(i2).entInstance.height) ? this.favouriteStates.get(i2).entInstance.width : this.favouriteStates.get(i2).entInstance.height;
                        final float scaleMag3 = (entSize2 > 2.5f) ? ((float)((2.5 + (entSize2 - 2.5f) * ((mag > magAcceptance && selected) ? ((mag - magAcceptance) / (1.0 - magAcceptance)) : 0.0)) / entSize2)) : 1.0f;
                        this.drawEntityOnScreen(this.favouriteStates.get(i2), this.favouriteStates.get(i2).entInstance, reso2.getScaledWidth() / 2 + (int)(radius * Math.cos(angle3)), (reso2.getScaledHeight() + 32) / 2 + (int)(radius * Math.sin(angle3)), 16.0f * prog4 * scaleMag3 + (float)(selected ? (6.0 * mag) : 0.0), 2.0f, 2.0f, this.renderTick, selected, true);
                        this.favouriteStates.get(i2).isFavourite = true;
                    }
                    Morph.config.updateSession("showAbilitiesInGui", (Object)showAb);
                    GL11.glPopMatrix();
                }
                BossStatus.healthScale = bossHealthScale;
                BossStatus.statusBarTime = bossStatusBarTime;
                BossStatus.bossName = bossName;
                BossStatus.hasColorModifier = hasColorModifier;
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                for (final Map.Entry<String, MorphInfoClient> e3 : this.playerMorphInfo.entrySet()) {
                    final MorphInfoClient morphInfo = e3.getValue();
                    for (final Ability ability : morphInfo.morphAbilities) {
                        if (ability.inactive) {
                            continue;
                        }
                        ability.postRender();
                    }
                }
            }
        }
    }
    
    @SubscribeEvent
    public void worldTick(final TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && Minecraft.getMinecraft().theWorld != null) {
            final Minecraft mc = Minecraft.getMinecraft();
            final WorldClient world = mc.theWorld;
            ++this.abilityScroll;
            if (mc.currentScreen != null) {
                if (this.selectorShow) {
                    if (mc.currentScreen instanceof GuiIngameMenu) {
                        mc.displayGuiScreen((GuiScreen)null);
                    }
                    this.selectorShow = false;
                    this.selectorTimer = 10 - this.selectorTimer;
                    this.scrollTimerHori = 3;
                }
                if (this.radialShow) {
                    this.radialShow = false;
                }
            }
            if (this.selectorTimer > 0) {
                --this.selectorTimer;
                if (this.selectorTimer == 0 && !this.selectorShow) {
                    this.selectorSelected = 0;
                    final MorphInfoClient info = this.playerMorphInfo.get(mc.thePlayer.getCommandSenderName());
                    if (info != null) {
                        final MorphState state = info.nextState;
                        final String entName = state.entInstance.getCommandSenderName();
                        int i = 0;
                        for (final Map.Entry<String, ArrayList<MorphState>> e : this.playerMorphCatMap.entrySet()) {
                            if (e.getKey().equalsIgnoreCase(entName)) {
                                this.selectorSelected = i;
                                final ArrayList<MorphState> states = e.getValue();
                                for (int j = 0; j < states.size(); ++j) {
                                    if (states.get(j).identifier.equalsIgnoreCase(state.identifier)) {
                                        this.selectorSelectedHori = j;
                                        break;
                                    }
                                }
                                break;
                            }
                            ++i;
                        }
                    }
                }
            }
            if (this.scrollTimer > 0) {
                --this.scrollTimer;
            }
            if (this.scrollTimerHori > 0) {
                --this.scrollTimerHori;
            }
            if (this.radialTime > 0) {
                --this.radialTime;
            }
            if (this.clock != world.getWorldTime() || !world.getGameRules().getGameRuleBooleanValue("doDaylightCycle")) {
                this.clock = world.getWorldTime();
                for (final Map.Entry<String, MorphInfoClient> e2 : this.playerMorphInfo.entrySet()) {
                    final MorphInfoClient info2 = e2.getValue();
                    if (info2.getMorphing()) {
                        final MorphInfoClient morphInfoClient = info2;
                        ++morphInfoClient.morphProgress;
                        if (info2.morphProgress > 80) {
                            info2.morphProgress = 80;
                            info2.setMorphing(false);
                            if (info2.player != null) {
                                info2.player.setSize(info2.nextState.entInstance.width, info2.nextState.entInstance.height);
                                info2.player.setPosition(info2.player.posX, info2.player.posY, info2.player.posZ);
                                info2.player.eyeHeight = ((info2.nextState.entInstance instanceof EntityPlayer) ? ((((EntityPlayer)info2.nextState.entInstance).getCommandSenderName().equalsIgnoreCase(mc.thePlayer.getCommandSenderName()) || info2.player == mc.thePlayer) ? mc.thePlayer.getDefaultEyeHeight() : ((EntityPlayer)info2.nextState.entInstance).getDefaultEyeHeight()) : (info2.nextState.entInstance.getEyeHeight() - info2.player.yOffset));
                                final ArrayList<Ability> newAbilities = AbilityHandler.getEntityAbilities(info2.nextState.entInstance.getClass());
                                final ArrayList<Ability> oldAbilities = info2.morphAbilities;
                                info2.morphAbilities = new ArrayList<Ability>();
                                for (final Ability ability : newAbilities) {
                                    try {
                                        final Ability clone = ability.clone();
                                        clone.setParent((EntityLivingBase)info2.player);
                                        info2.morphAbilities.add(clone);
                                    }
                                    catch (Exception ex) {}
                                }
                                for (final Ability ability : oldAbilities) {
                                    if (ability.inactive) {
                                        continue;
                                    }
                                    boolean isRemoved = true;
                                    for (final Ability newAbility : info2.morphAbilities) {
                                        if (newAbility.getType().equalsIgnoreCase(ability.getType())) {
                                            isRemoved = false;
                                            break;
                                        }
                                    }
                                    if (!isRemoved || ability.getParent() == null) {
                                        continue;
                                    }
                                    ability.kill();
                                }
                            }
                        }
                        else if (info2.prevState != null && info2.player != null) {
                            info2.player.setSize(info2.prevState.entInstance.width + (info2.nextState.entInstance.width - info2.prevState.entInstance.width) * (info2.morphProgress / 80.0f), info2.prevState.entInstance.height + (info2.nextState.entInstance.height - info2.prevState.entInstance.height) * (info2.morphProgress / 80.0f));
                            info2.player.setPosition(info2.player.posX, info2.player.posY, info2.player.posZ);
                            final float prevEyeHeight = (info2.prevState.entInstance instanceof EntityPlayer) ? ((((EntityPlayer)info2.prevState.entInstance).getCommandSenderName().equalsIgnoreCase(mc.thePlayer.getCommandSenderName()) || info2.player == mc.thePlayer) ? mc.thePlayer.getDefaultEyeHeight() : ((EntityPlayer)info2.prevState.entInstance).getDefaultEyeHeight()) : (info2.prevState.entInstance.getEyeHeight() - info2.player.yOffset);
                            final float nextEyeHeight = (info2.nextState.entInstance instanceof EntityPlayer) ? ((((EntityPlayer)info2.nextState.entInstance).getCommandSenderName().equalsIgnoreCase(mc.thePlayer.getCommandSenderName()) || info2.player == mc.thePlayer) ? mc.thePlayer.getDefaultEyeHeight() : ((EntityPlayer)info2.nextState.entInstance).getDefaultEyeHeight()) : (info2.nextState.entInstance.getEyeHeight() - info2.player.yOffset);
                            info2.player.eyeHeight = prevEyeHeight + (nextEyeHeight - prevEyeHeight) * (info2.morphProgress / 80.0f);
                        }
                    }
                    else if (info2.player != null && Morph.config.getSessionInt("forceSizeWhenMorphed") == 1) {
                        info2.player.setSize(info2.nextState.entInstance.width, info2.nextState.entInstance.height);
                        info2.player.setPosition(info2.player.posX, info2.player.posY, info2.player.posZ);
                        info2.player.eyeHeight = ((info2.nextState.entInstance instanceof EntityPlayer) ? ((((EntityPlayer)info2.nextState.entInstance).getCommandSenderName().equalsIgnoreCase(mc.thePlayer.getCommandSenderName()) || info2.player == mc.thePlayer) ? mc.thePlayer.getDefaultEyeHeight() : ((EntityPlayer)info2.nextState.entInstance).getDefaultEyeHeight()) : (info2.nextState.entInstance.getEyeHeight() - info2.player.yOffset));
                    }
                    if (info2.player != null && (info2.player.dimension != mc.thePlayer.dimension || !info2.player.isEntityAlive() || !world.playerEntities.contains(info2.player) || info2.player.isPlayerSleeping() || info2.player.getSleepTimer() > 0)) {
                        info2.player = null;
                    }
                    if (info2.player == null) {
                        info2.player = world.getPlayerEntityByName((String)e2.getKey());
                        if (info2.player != null) {
                            if (!info2.getMorphing()) {
                                info2.player.setSize(info2.nextState.entInstance.width, info2.nextState.entInstance.height);
                                info2.player.setPosition(info2.player.posX, info2.player.posY, info2.player.posZ);
                                info2.player.eyeHeight = ((info2.nextState.entInstance instanceof EntityPlayer) ? ((((EntityPlayer)info2.nextState.entInstance).getCommandSenderName().equalsIgnoreCase(mc.thePlayer.getCommandSenderName()) || info2.player == mc.thePlayer) ? mc.thePlayer.getDefaultEyeHeight() : ((EntityPlayer)info2.nextState.entInstance).getDefaultEyeHeight()) : (info2.nextState.entInstance.getEyeHeight() - info2.player.yOffset));
                                double nextMaxHealth = MathHelper.clamp_double(info2.nextState.entInstance.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue(), 0.0, 20.0) + info2.healthOffset;
                                if (nextMaxHealth < 1.0) {
                                    nextMaxHealth = 1.0;
                                }
                                if (nextMaxHealth != info2.player.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue()) {
                                    info2.player.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(nextMaxHealth);
                                    if (info2.player.getHealth() < nextMaxHealth) {
                                        info2.player.setHealth((float)nextMaxHealth);
                                    }
                                }
                            }
                            for (final Ability ability2 : info2.morphAbilities) {
                                ability2.setParent((EntityLivingBase)info2.player);
                            }
                        }
                    }
                    if (info2.prevState.entInstance == null) {
                        info2.prevState.entInstance = (EntityLivingBase)info2.player;
                    }
                    if (info2.player != null) {
                        for (final Ability ability2 : info2.morphAbilities) {
                            if (ability2.inactive) {
                                continue;
                            }
                            if (ability2.getParent() == null) {
                                continue;
                            }
                            ability2.tick();
                        }
                        if (info2.prevState.entInstance != null && info2.nextState.entInstance != null) {
                            info2.player.ignoreFrustumCheck = true;
                            if (info2.morphProgress < 10) {
                                if (info2.prevState.entInstance != mc.thePlayer) {
                                    final EntityLivingBase entInstance = info2.prevState.entInstance;
                                    entInstance.lastTickPosY -= info2.player.yOffset;
                                    final EntityLivingBase entInstance2 = info2.prevState.entInstance;
                                    entInstance2.prevPosY -= info2.player.yOffset;
                                    final EntityLivingBase entInstance3 = info2.prevState.entInstance;
                                    entInstance3.posY -= info2.player.yOffset;
                                    info2.prevState.entInstance.setPosition(info2.prevState.entInstance.posX, info2.prevState.entInstance.posY, info2.prevState.entInstance.posZ);
                                    info2.prevState.entInstance.onUpdate();
                                    final EntityLivingBase entInstance4 = info2.prevState.entInstance;
                                    entInstance4.lastTickPosY += info2.player.yOffset;
                                    final EntityLivingBase entInstance5 = info2.prevState.entInstance;
                                    entInstance5.prevPosY += info2.player.yOffset;
                                    final EntityLivingBase entInstance6 = info2.prevState.entInstance;
                                    entInstance6.posY += info2.player.yOffset;
                                    info2.prevState.entInstance.setPosition(info2.prevState.entInstance.posX, info2.prevState.entInstance.posY, info2.prevState.entInstance.posZ);
                                }
                            }
                            else if (info2.morphProgress > 70 && info2.nextState.entInstance != mc.thePlayer) {
                                if (!(info2.nextState.entInstance instanceof EntityPlayer) && !(RenderManager.instance.getEntityRenderObject((Entity)info2.nextState.entInstance) instanceof RenderBiped)) {
                                    info2.nextState.entInstance.yOffset = 0.0f;
                                }
                                final EntityLivingBase entInstance7 = info2.nextState.entInstance;
                                entInstance7.lastTickPosY -= info2.player.yOffset;
                                final EntityLivingBase entInstance8 = info2.nextState.entInstance;
                                entInstance8.prevPosY -= info2.player.yOffset;
                                final EntityLivingBase entInstance9 = info2.nextState.entInstance;
                                entInstance9.posY -= info2.player.yOffset;
                                info2.nextState.entInstance.setPosition(info2.nextState.entInstance.posX, info2.nextState.entInstance.posY, info2.nextState.entInstance.posZ);
                                info2.nextState.entInstance.onUpdate();
                                final EntityLivingBase entInstance10 = info2.nextState.entInstance;
                                entInstance10.lastTickPosY += info2.player.yOffset;
                                final EntityLivingBase entInstance11 = info2.nextState.entInstance;
                                entInstance11.prevPosY += info2.player.yOffset;
                                final EntityLivingBase entInstance12 = info2.nextState.entInstance;
                                entInstance12.posY += info2.player.yOffset;
                                info2.nextState.entInstance.setPosition(info2.nextState.entInstance.posX, info2.nextState.entInstance.posY, info2.nextState.entInstance.posZ);
                            }
                            final EntityLivingBase entInstance13 = info2.prevState.entInstance;
                            final EntityLivingBase entInstance14 = info2.nextState.entInstance;
                            final float prevRotationYawHead = info2.player.prevRotationYawHead;
                            entInstance14.prevRotationYawHead = prevRotationYawHead;
                            entInstance13.prevRotationYawHead = prevRotationYawHead;
                            final EntityLivingBase entInstance15 = info2.prevState.entInstance;
                            final EntityLivingBase entInstance16 = info2.nextState.entInstance;
                            final float prevRotationYaw = info2.player.prevRotationYaw;
                            entInstance16.prevRotationYaw = prevRotationYaw;
                            entInstance15.prevRotationYaw = prevRotationYaw;
                            final EntityLivingBase entInstance17 = info2.prevState.entInstance;
                            final EntityLivingBase entInstance18 = info2.nextState.entInstance;
                            final float prevRotationPitch = info2.player.prevRotationPitch;
                            entInstance18.prevRotationPitch = prevRotationPitch;
                            entInstance17.prevRotationPitch = prevRotationPitch;
                            final EntityLivingBase entInstance19 = info2.prevState.entInstance;
                            final EntityLivingBase entInstance20 = info2.nextState.entInstance;
                            final float prevRenderYawOffset = info2.player.prevRenderYawOffset;
                            entInstance20.prevRenderYawOffset = prevRenderYawOffset;
                            entInstance19.prevRenderYawOffset = prevRenderYawOffset;
                            final EntityLivingBase entInstance21 = info2.prevState.entInstance;
                            final EntityLivingBase entInstance22 = info2.nextState.entInstance;
                            final float prevLimbSwingAmount = info2.player.prevLimbSwingAmount;
                            entInstance22.prevLimbSwingAmount = prevLimbSwingAmount;
                            entInstance21.prevLimbSwingAmount = prevLimbSwingAmount;
                            final EntityLivingBase entInstance23 = info2.prevState.entInstance;
                            final EntityLivingBase entInstance24 = info2.nextState.entInstance;
                            final float prevSwingProgress = info2.player.prevSwingProgress;
                            entInstance24.prevSwingProgress = prevSwingProgress;
                            entInstance23.prevSwingProgress = prevSwingProgress;
                            final EntityLivingBase entInstance25 = info2.prevState.entInstance;
                            final EntityLivingBase entInstance26 = info2.nextState.entInstance;
                            final double prevPosX = info2.player.prevPosX;
                            entInstance26.prevPosX = prevPosX;
                            entInstance25.prevPosX = prevPosX;
                            final EntityLivingBase entInstance27 = info2.prevState.entInstance;
                            final EntityLivingBase entInstance28 = info2.nextState.entInstance;
                            final double prevPosY = info2.player.prevPosY;
                            entInstance28.prevPosY = prevPosY;
                            entInstance27.prevPosY = prevPosY;
                            final EntityLivingBase entInstance29 = info2.prevState.entInstance;
                            final EntityLivingBase entInstance30 = info2.nextState.entInstance;
                            final double prevPosZ = info2.player.prevPosZ;
                            entInstance30.prevPosZ = prevPosZ;
                            entInstance29.prevPosZ = prevPosZ;
                            final EntityLivingBase entInstance31 = info2.prevState.entInstance;
                            final EntityLivingBase entInstance32 = info2.nextState.entInstance;
                            final float rotationYawHead = info2.player.rotationYawHead;
                            entInstance32.rotationYawHead = rotationYawHead;
                            entInstance31.rotationYawHead = rotationYawHead;
                            final EntityLivingBase entInstance33 = info2.prevState.entInstance;
                            final EntityLivingBase entInstance34 = info2.nextState.entInstance;
                            final float rotationYaw = info2.player.rotationYaw;
                            entInstance34.rotationYaw = rotationYaw;
                            entInstance33.rotationYaw = rotationYaw;
                            final EntityLivingBase entInstance35 = info2.prevState.entInstance;
                            final EntityLivingBase entInstance36 = info2.nextState.entInstance;
                            final float rotationPitch = info2.player.rotationPitch;
                            entInstance36.rotationPitch = rotationPitch;
                            entInstance35.rotationPitch = rotationPitch;
                            final EntityLivingBase entInstance37 = info2.prevState.entInstance;
                            final EntityLivingBase entInstance38 = info2.nextState.entInstance;
                            final float renderYawOffset = info2.player.renderYawOffset;
                            entInstance38.renderYawOffset = renderYawOffset;
                            entInstance37.renderYawOffset = renderYawOffset;
                            final EntityLivingBase entInstance39 = info2.prevState.entInstance;
                            final EntityLivingBase entInstance40 = info2.nextState.entInstance;
                            final float limbSwingAmount = info2.player.limbSwingAmount;
                            entInstance40.limbSwingAmount = limbSwingAmount;
                            entInstance39.limbSwingAmount = limbSwingAmount;
                            final EntityLivingBase entInstance41 = info2.prevState.entInstance;
                            final EntityLivingBase entInstance42 = info2.nextState.entInstance;
                            final float swingProgress = info2.player.swingProgress;
                            entInstance42.swingProgress = swingProgress;
                            entInstance41.swingProgress = swingProgress;
                            final EntityLivingBase entInstance43 = info2.prevState.entInstance;
                            final EntityLivingBase entInstance44 = info2.nextState.entInstance;
                            final float limbSwing = info2.player.limbSwing;
                            entInstance44.limbSwing = limbSwing;
                            entInstance43.limbSwing = limbSwing;
                            final EntityLivingBase entInstance45 = info2.prevState.entInstance;
                            final EntityLivingBase entInstance46 = info2.nextState.entInstance;
                            final double posX = info2.player.posX;
                            entInstance46.posX = posX;
                            entInstance45.posX = posX;
                            final EntityLivingBase entInstance47 = info2.prevState.entInstance;
                            final EntityLivingBase entInstance48 = info2.nextState.entInstance;
                            final double posY = info2.player.posY;
                            entInstance48.posY = posY;
                            entInstance47.posY = posY;
                            final EntityLivingBase entInstance49 = info2.prevState.entInstance;
                            final EntityLivingBase entInstance50 = info2.nextState.entInstance;
                            final double posZ = info2.player.posZ;
                            entInstance50.posZ = posZ;
                            entInstance49.posZ = posZ;
                            final EntityLivingBase entInstance51 = info2.prevState.entInstance;
                            final EntityLivingBase entInstance52 = info2.nextState.entInstance;
                            final double motionX = info2.player.motionX;
                            entInstance52.motionX = motionX;
                            entInstance51.motionX = motionX;
                            final EntityLivingBase entInstance53 = info2.prevState.entInstance;
                            final EntityLivingBase entInstance54 = info2.nextState.entInstance;
                            final double motionY = info2.player.motionY;
                            entInstance54.motionY = motionY;
                            entInstance53.motionY = motionY;
                            final EntityLivingBase entInstance55 = info2.prevState.entInstance;
                            final EntityLivingBase entInstance56 = info2.nextState.entInstance;
                            final double motionZ = info2.player.motionZ;
                            entInstance56.motionZ = motionZ;
                            entInstance55.motionZ = motionZ;
                            final EntityLivingBase entInstance57 = info2.prevState.entInstance;
                            final EntityLivingBase entInstance58 = info2.nextState.entInstance;
                            final int ticksExisted = info2.player.ticksExisted;
                            entInstance58.ticksExisted = ticksExisted;
                            entInstance57.ticksExisted = ticksExisted;
                            final EntityLivingBase entInstance59 = info2.prevState.entInstance;
                            final EntityLivingBase entInstance60 = info2.nextState.entInstance;
                            final boolean isAirBorne = info2.player.isAirBorne;
                            entInstance60.isAirBorne = isAirBorne;
                            entInstance59.isAirBorne = isAirBorne;
                            final EntityLivingBase entInstance61 = info2.prevState.entInstance;
                            final EntityLivingBase entInstance62 = info2.nextState.entInstance;
                            final float moveStrafing = info2.player.moveStrafing;
                            entInstance62.moveStrafing = moveStrafing;
                            entInstance61.moveStrafing = moveStrafing;
                            final EntityLivingBase entInstance63 = info2.prevState.entInstance;
                            final EntityLivingBase entInstance64 = info2.nextState.entInstance;
                            final float moveForward = info2.player.moveForward;
                            entInstance64.moveForward = moveForward;
                            entInstance63.moveForward = moveForward;
                            final EntityLivingBase entInstance65 = info2.prevState.entInstance;
                            final EntityLivingBase entInstance66 = info2.nextState.entInstance;
                            final int dimension = info2.player.dimension;
                            entInstance66.dimension = dimension;
                            entInstance65.dimension = dimension;
                            final EntityLivingBase entInstance67 = info2.prevState.entInstance;
                            final EntityLivingBase entInstance68 = info2.nextState.entInstance;
                            final World worldObj = info2.player.worldObj;
                            entInstance68.worldObj = worldObj;
                            entInstance67.worldObj = worldObj;
                            final EntityLivingBase entInstance69 = info2.prevState.entInstance;
                            final EntityLivingBase entInstance70 = info2.nextState.entInstance;
                            final Entity ridingEntity = info2.player.ridingEntity;
                            entInstance70.ridingEntity = ridingEntity;
                            entInstance69.ridingEntity = ridingEntity;
                            final EntityLivingBase entInstance71 = info2.prevState.entInstance;
                            final EntityLivingBase entInstance72 = info2.nextState.entInstance;
                            final int hurtTime = info2.player.hurtTime;
                            entInstance72.hurtTime = hurtTime;
                            entInstance71.hurtTime = hurtTime;
                            final EntityLivingBase entInstance73 = info2.prevState.entInstance;
                            final EntityLivingBase entInstance74 = info2.nextState.entInstance;
                            final int deathTime = info2.player.deathTime;
                            entInstance74.deathTime = deathTime;
                            entInstance73.deathTime = deathTime;
                            final EntityLivingBase entInstance75 = info2.prevState.entInstance;
                            final EntityLivingBase entInstance76 = info2.nextState.entInstance;
                            final boolean isSwingInProgress = info2.player.isSwingInProgress;
                            entInstance76.isSwingInProgress = isSwingInProgress;
                            entInstance75.isSwingInProgress = isSwingInProgress;
                            final boolean prevOnGround = info2.nextState.entInstance.onGround;
                            final EntityLivingBase entInstance77 = info2.prevState.entInstance;
                            final EntityLivingBase entInstance78 = info2.nextState.entInstance;
                            final boolean onGround = info2.player.onGround;
                            entInstance78.onGround = onGround;
                            entInstance77.onGround = onGround;
                            if (info2.player != mc.thePlayer) {
                                info2.nextState.entInstance.noClip = false;
                                info2.nextState.entInstance.boundingBox.setBB(info2.player.boundingBox);
                                info2.nextState.entInstance.moveEntity(0.0, -0.01, 0.0);
                                final EntityLivingBase entInstance79 = info2.prevState.entInstance;
                                final EntityLivingBase entInstance80 = info2.nextState.entInstance;
                                final double prevPosY2 = info2.player.prevPosY;
                                entInstance80.prevPosY = prevPosY2;
                                entInstance79.prevPosY = prevPosY2;
                            }
                            final EntityLivingBase entInstance81 = info2.prevState.entInstance;
                            final EntityLivingBase entInstance82 = info2.nextState.entInstance;
                            final boolean noClip = info2.player.noClip;
                            entInstance82.noClip = noClip;
                            entInstance81.noClip = noClip;
                            info2.prevState.entInstance.setSneaking(info2.player.isSneaking());
                            info2.nextState.entInstance.setSneaking(info2.player.isSneaking());
                            info2.prevState.entInstance.setSprinting(info2.player.isSprinting());
                            info2.nextState.entInstance.setSprinting(info2.player.isSprinting());
                            info2.prevState.entInstance.setInvisible(info2.player.isInvisible());
                            info2.nextState.entInstance.setInvisible(info2.player.isInvisible());
                            info2.prevState.entInstance.setHealth(info2.prevState.entInstance.getMaxHealth() * (info2.player.getHealth() / info2.player.getMaxHealth()));
                            info2.nextState.entInstance.setHealth(info2.nextState.entInstance.getMaxHealth() * (info2.player.getHealth() / info2.player.getMaxHealth()));
                            if (!(info2.nextState.entInstance instanceof EntityPlayer) && !(RenderManager.instance.getEntityRenderObject((Entity)info2.nextState.entInstance) instanceof RenderBiped)) {
                                info2.nextState.entInstance.yOffset = info2.player.yOffset;
                            }
                            if (prevOnGround && !info2.nextState.entInstance.onGround && info2.nextState.entInstance instanceof EntitySlime) {
                                ((EntitySlime)info2.nextState.entInstance).squishAmount = 0.6f;
                            }
                            if (info2.nextState.entInstance instanceof EntityDragon) {
                                final EntityLivingBase entInstance83 = info2.nextState.entInstance;
                                entInstance83.prevRotationYaw += 180.0f;
                                final EntityLivingBase entInstance84 = info2.nextState.entInstance;
                                entInstance84.rotationYaw += 180.0f;
                                ((EntityDragon)info2.nextState.entInstance).deathTicks = info2.player.deathTime;
                            }
                            for (int k = 0; k < 5; ++k) {
                                if ((info2.nextState.entInstance.getEquipmentInSlot(k) == null && info2.player.getEquipmentInSlot(k) != null) || (info2.nextState.entInstance.getEquipmentInSlot(k) != null && info2.player.getEquipmentInSlot(k) == null) || (info2.nextState.entInstance.getEquipmentInSlot(k) != null && info2.player.getEquipmentInSlot(k) != null && !info2.nextState.entInstance.getEquipmentInSlot(k).isItemEqual(info2.player.getEquipmentInSlot(k)))) {
                                    info2.nextState.entInstance.setCurrentItemOrArmor(k, (info2.player.getEquipmentInSlot(k) != null) ? info2.player.getEquipmentInSlot(k).copy() : null);
                                }
                            }
                            if (info2.nextState.entInstance instanceof EntityPlayer && ((EntityPlayer)info2.nextState.entInstance).getItemInUse() != info2.player.getItemInUse()) {
                                ((EntityPlayer)info2.nextState.entInstance).setItemInUse((info2.player.getItemInUse() == null) ? null : info2.player.getItemInUse().copy(), info2.player.getItemInUseCount());
                            }
                        }
                        if (info2.flying && info2.firstUpdate) {
                            info2.player.capabilities.isFlying = true;
                            info2.player.capabilities.allowFlying = true;
                            info2.player.sendPlayerAbilities();
                        }
                    }
                    info2.firstUpdate = false;
                }
            }
            if (Morph.config.getSessionInt("allowMorphSelection") == 0 && this.selectorShow) {
                this.selectorShow = false;
                this.selectorTimer = 0;
            }
        }
    }
    
    public void drawEntityOnScreen(final MorphState state, final EntityLivingBase ent, final int posX, final int posY, final float scale, final float par4, final float par5, final float renderTick, final boolean selected, final boolean text) {
        this.forceRender = true;
        if (ent != null) {
            final boolean hideGui = Minecraft.getMinecraft().gameSettings.hideGUI;
            Minecraft.getMinecraft().gameSettings.hideGUI = true;
            GL11.glEnable(2903);
            GL11.glPushMatrix();
            GL11.glDisable(3008);
            GL11.glTranslatef((float)posX, (float)posY, 50.0f);
            GL11.glScalef(-scale, scale, scale);
            GL11.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
            final float f2 = ent.renderYawOffset;
            final float f3 = ent.rotationYaw;
            final float f4 = ent.rotationPitch;
            final float f5 = ent.rotationYawHead;
            GL11.glRotatef(135.0f, 0.0f, 1.0f, 0.0f);
            RenderHelper.enableStandardItemLighting();
            GL11.glRotatef(-135.0f, 0.0f, 1.0f, 0.0f);
            GL11.glRotatef(-(float)Math.atan(par5 / 40.0f) * 20.0f, 1.0f, 0.0f, 0.0f);
            GL11.glRotatef(15.0f, 1.0f, 0.0f, 0.0f);
            GL11.glRotatef(25.0f, 0.0f, 1.0f, 0.0f);
            ent.renderYawOffset = (float)Math.atan(par4 / 40.0f) * 20.0f;
            ent.rotationYaw = (float)Math.atan(par4 / 40.0f) * 40.0f;
            ent.rotationPitch = -(float)Math.atan(par5 / 40.0f) * 20.0f;
            ent.rotationYawHead = ent.renderYawOffset;
            GL11.glTranslatef(0.0f, ent.yOffset, 0.0f);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            if (ent instanceof EntityDragon) {
                GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
            }
            final float viewY = RenderManager.instance.playerViewY;
            RenderManager.instance.playerViewY = 180.0f;
            RenderManager.instance.renderEntityWithPosYaw((Entity)ent, 0.0, 0.0, 0.0, 0.0f, 1.0f);
            if (ent instanceof EntityDragon) {
                GL11.glRotatef(180.0f, 0.0f, -1.0f, 0.0f);
            }
            GL11.glTranslatef(0.0f, -0.22f, 0.0f);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 204.0f, 204.0f);
            Tessellator.instance.setBrightness(240);
            RenderManager.instance.playerViewY = viewY;
            ent.renderYawOffset = f2;
            ent.rotationYaw = f3;
            ent.rotationPitch = f4;
            ent.rotationYawHead = f5;
            GL11.glPopMatrix();
            RenderHelper.disableStandardItemLighting();
            GL11.glPushMatrix();
            GL11.glTranslatef((float)posX, (float)posY, 50.0f);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            final MorphInfoClient info = this.playerMorphInfo.get(Minecraft.getMinecraft().thePlayer.getCommandSenderName());
            GL11.glTranslatef(0.0f, 0.0f, 100.0f);
            if (text) {
                if (this.radialShow) {
                    GL11.glPushMatrix();
                    final float scaleee = 0.75f;
                    GL11.glScalef(scaleee, scaleee, scaleee);
                    final String name = (selected ? EnumChatFormatting.YELLOW : (((info != null && info.nextState.identifier.equalsIgnoreCase(state.identifier)) || (info == null && state.playerMorph.equalsIgnoreCase(Minecraft.getMinecraft().thePlayer.getCommandSenderName()))) ? EnumChatFormatting.GOLD : "")) + ent.getCommandSenderName();
                    Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(name, (int)(-3.0f - Minecraft.getMinecraft().fontRenderer.getStringWidth(name) / 2 * scaleee), 5, 16777215);
                    GL11.glPopMatrix();
                }
                else {
                    Minecraft.getMinecraft().fontRenderer.drawStringWithShadow((selected ? EnumChatFormatting.YELLOW : (((info != null && info.nextState.entInstance.getCommandSenderName().equalsIgnoreCase(state.entInstance.getCommandSenderName())) || (info == null && ent.getCommandSenderName().equalsIgnoreCase(Minecraft.getMinecraft().thePlayer.getCommandSenderName()))) ? EnumChatFormatting.GOLD : "")) + ent.getCommandSenderName(), 26, -32, 16777215);
                }
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            }
            if (state != null && !state.playerMorph.equalsIgnoreCase(state.playerName) && state.isFavourite) {
                final double pX = 9.5;
                final double pY = -33.5;
                final double size = 9.0;
                Minecraft.getMinecraft().getTextureManager().bindTexture(TickHandlerClient.rlFavourite);
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                final Tessellator tessellator = Tessellator.instance;
                tessellator.setColorRGBA(255, 255, 255, 255);
                tessellator.startDrawingQuads();
                double iconX = pX;
                double iconY = pY;
                tessellator.addVertexWithUV(iconX, iconY + size, 0.0, 0.0, 1.0);
                tessellator.addVertexWithUV(iconX + size, iconY + size, 0.0, 1.0, 1.0);
                tessellator.addVertexWithUV(iconX + size, iconY, 0.0, 1.0, 0.0);
                tessellator.addVertexWithUV(iconX, iconY, 0.0, 0.0, 0.0);
                tessellator.draw();
                GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.6f);
                tessellator.startDrawingQuads();
                iconX = pX + 1.0;
                iconY = pY + 1.0;
                tessellator.addVertexWithUV(iconX, iconY + size, -1.0, 0.0, 1.0);
                tessellator.addVertexWithUV(iconX + size, iconY + size, -1.0, 1.0, 1.0);
                tessellator.addVertexWithUV(iconX + size, iconY, -1.0, 1.0, 0.0);
                tessellator.addVertexWithUV(iconX, iconY, -1.0, 0.0, 0.0);
                tessellator.draw();
            }
            if (Morph.config.getSessionInt("showAbilitiesInGui") == 1) {
                final ArrayList<Ability> abilities = AbilityHandler.getEntityAbilities(ent.getClass());
                int abilitiesSize = abilities.size();
                for (int i = abilities.size() - 1; i >= 0; --i) {
                    if (!abilities.get(i).entityHasAbility(ent) || (abilities.get(i).getIcon() == null && !(abilities.get(i) instanceof AbilityPotionEffect)) || (abilities.get(i) instanceof AbilityPotionEffect && Potion.potionTypes[abilities.get(i).potionId] != null && !Potion.potionTypes[abilities.get(i).potionId].hasStatusIcon())) {
                        --abilitiesSize;
                    }
                }
                boolean shouldScroll = false;
                final int stencilBit = MinecraftForgeClient.reserveStencilBit();
                if (stencilBit >= 0 && abilitiesSize > 3) {
                    MorphState selectedState = null;
                    int j = 0;
                    for (final Map.Entry<String, ArrayList<MorphState>> e : this.playerMorphCatMap.entrySet()) {
                        if (j == this.selectorSelected) {
                            final ArrayList<MorphState> states = e.getValue();
                            for (int k = 0; k < states.size(); ++k) {
                                if (k == this.selectorSelectedHori) {
                                    selectedState = states.get(k);
                                    break;
                                }
                            }
                            break;
                        }
                        ++j;
                    }
                    if (state != null && selectedState == state) {
                        shouldScroll = true;
                    }
                    if (shouldScroll) {
                        final int stencilMask = 1 << stencilBit;
                        GL11.glEnable(2960);
                        GL11.glDepthMask(false);
                        GL11.glColorMask(false, false, false, false);
                        GL11.glStencilFunc(519, stencilMask, stencilMask);
                        GL11.glStencilOp(7680, 7680, 7681);
                        GL11.glStencilMask(stencilMask);
                        GL11.glClear(1024);
                        RendererHelper.drawColourOnScreen(255, 255, 255, 255, -20.5, -32.5, 40.0, 35.0, -10.0);
                        GL11.glStencilMask(0);
                        GL11.glStencilFunc(514, stencilMask, stencilMask);
                        GL11.glDepthMask(true);
                        GL11.glColorMask(true, true, true, true);
                    }
                }
                int offsetX = 0;
                int offsetY = 0;
                int renders = 0;
                for (int l = 0; l < ((abilitiesSize > 3 && stencilBit >= 0 && abilities.size() > 3) ? (abilities.size() * 2) : abilities.size()); ++l) {
                    final Ability ability = abilities.get((l >= abilities.size()) ? (l - abilities.size()) : l);
                    if (ability.entityHasAbility(ent) && (ability.getIcon() != null || ability instanceof AbilityPotionEffect) && (!(ability instanceof AbilityPotionEffect) || Potion.potionTypes[((AbilityPotionEffect)ability).potionId] == null || Potion.potionTypes[((AbilityPotionEffect)ability).potionId].hasStatusIcon())) {
                        if (abilitiesSize <= 3 || stencilBit < 0 || abilities.size() <= 3 || shouldScroll || renders < 3) {
                            final ResourceLocation loc = ability.getIcon();
                            if (loc != null || ability instanceof AbilityPotionEffect) {
                                final double pX2 = -20.5;
                                double pY2 = -33.5;
                                double size2 = 12.0;
                                if (stencilBit >= 0 && abilities.size() > 3 && shouldScroll) {
                                    final int round = this.abilityScroll % (30 * abilities.size());
                                    pY2 -= (size2 + 1.0) * (round + renderTick) / 30.0;
                                }
                                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                                final Tessellator tessellator2 = Tessellator.instance;
                                tessellator2.setColorRGBA(255, 255, 255, 255);
                                double iconX2 = pX2 + offsetX * (size2 + 1.0);
                                double iconY2 = pY2 + offsetY * (size2 + 1.0);
                                if (loc != null) {
                                    Minecraft.getMinecraft().getTextureManager().bindTexture(loc);
                                    tessellator2.startDrawingQuads();
                                    tessellator2.addVertexWithUV(iconX2, iconY2 + size2, 0.0, 0.0, 1.0);
                                    tessellator2.addVertexWithUV(iconX2 + size2, iconY2 + size2, 0.0, 1.0, 1.0);
                                    tessellator2.addVertexWithUV(iconX2 + size2, iconY2, 0.0, 1.0, 0.0);
                                    tessellator2.addVertexWithUV(iconX2, iconY2, 0.0, 0.0, 0.0);
                                    tessellator2.draw();
                                }
                                else {
                                    Minecraft.getMinecraft().getTextureManager().bindTexture(TickHandlerClient.rlGuiInventory);
                                    final int m = Potion.potionTypes[((AbilityPotionEffect)ability).potionId].getStatusIconIndex();
                                    final float f6 = 0.00390625f;
                                    final float f7 = 0.00390625f;
                                    final int xStart = m % 8 * 18;
                                    final int yStart = 198 + m / 8 * 18;
                                    tessellator2.startDrawingQuads();
                                    tessellator2.addVertexWithUV(iconX2, iconY2 + size2, 0.0, (double)(xStart * f6), (double)((yStart + 18) * f7));
                                    tessellator2.addVertexWithUV(iconX2 + size2, iconY2 + size2, 0.0, (double)((xStart + 18) * f6), (double)((yStart + 18) * f7));
                                    tessellator2.addVertexWithUV(iconX2 + size2, iconY2, 0.0, (double)((xStart + 18) * f6), (double)(yStart * f7));
                                    tessellator2.addVertexWithUV(iconX2, iconY2, 0.0, (double)(xStart * f6), (double)(yStart * f7));
                                    tessellator2.draw();
                                }
                                GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.6f);
                                size2 = 12.0;
                                iconX2 = pX2 + 1.0 + offsetX * (size2 + 1.0);
                                iconY2 = pY2 + 1.0 + offsetY * (size2 + 1.0);
                                if (loc != null) {
                                    tessellator2.startDrawingQuads();
                                    tessellator2.addVertexWithUV(iconX2, iconY2 + size2, -1.0, 0.0, 1.0);
                                    tessellator2.addVertexWithUV(iconX2 + size2, iconY2 + size2, -1.0, 1.0, 1.0);
                                    tessellator2.addVertexWithUV(iconX2 + size2, iconY2, -1.0, 1.0, 0.0);
                                    tessellator2.addVertexWithUV(iconX2, iconY2, -1.0, 0.0, 0.0);
                                    tessellator2.draw();
                                }
                                else {
                                    Minecraft.getMinecraft().getTextureManager().bindTexture(TickHandlerClient.rlGuiInventory);
                                    final int m = Potion.potionTypes[((AbilityPotionEffect)ability).potionId].getStatusIconIndex();
                                    final float f6 = 0.00390625f;
                                    final float f7 = 0.00390625f;
                                    final int xStart = m % 8 * 18;
                                    final int yStart = 198 + m / 8 * 18;
                                    tessellator2.startDrawingQuads();
                                    tessellator2.addVertexWithUV(iconX2, iconY2 + size2, -1.0, (double)(xStart * f6), (double)((yStart + 18) * f7));
                                    tessellator2.addVertexWithUV(iconX2 + size2, iconY2 + size2, -1.0, (double)((xStart + 18) * f6), (double)((yStart + 18) * f7));
                                    tessellator2.addVertexWithUV(iconX2 + size2, iconY2, -1.0, (double)((xStart + 18) * f6), (double)(yStart * f7));
                                    tessellator2.addVertexWithUV(iconX2, iconY2, -1.0, (double)(xStart * f6), (double)(yStart * f7));
                                    tessellator2.draw();
                                }
                                if (++offsetY == 3 && stencilBit < 0) {
                                    offsetY = 0;
                                    ++offsetX;
                                }
                            }
                            ++renders;
                        }
                    }
                }
                if (stencilBit >= 0 && abilities.size() > 3 && shouldScroll) {
                    GL11.glDisable(2960);
                }
                MinecraftForgeClient.releaseStencilBit(stencilBit);
            }
            GL11.glTranslatef(0.0f, 0.0f, -100.0f);
            GL11.glDisable(3042);
            GL11.glPopMatrix();
            GL11.glEnable(3008);
            GL11.glDisable(32826);
            OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
            GL11.glDisable(3553);
            OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
            Minecraft.getMinecraft().gameSettings.hideGUI = hideGui;
        }
        this.forceRender = false;
    }
    
    public void selectRadialMenu() {
        double mag = Math.sqrt(Morph.proxy.tickHandlerClient.radialDeltaX * Morph.proxy.tickHandlerClient.radialDeltaX + Morph.proxy.tickHandlerClient.radialDeltaY * Morph.proxy.tickHandlerClient.radialDeltaY);
        final double magAcceptance = 0.8;
        double radialAngle = -720.0;
        if (mag > magAcceptance) {
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
            if (mag > 0.9999999) {
                mag = Math.round(mag);
            }
            for (int i = 0; i < this.favouriteStates.size(); ++i) {
                double angle = 6.283185307179586 * i / this.favouriteStates.size();
                angle -= Math.toRadians(90.0);
                final int radius = 80;
                final float leeway = 360.0f / this.favouriteStates.size();
                final boolean selected = false;
                if (mag > magAcceptance * 0.75 && ((i == 0 && ((radialAngle < leeway / 2.0f && radialAngle >= 0.0) || radialAngle > 360.0f - leeway / 2.0f)) || (i != 0 && radialAngle < leeway * i + leeway / 2.0f && radialAngle > leeway * i - leeway / 2.0f))) {
                    this.favouriteStates.get(i);
                    final MorphInfoClient info = this.playerMorphInfo.get(Minecraft.getMinecraft().thePlayer.getCommandSenderName());
                    if ((info != null && !info.nextState.identifier.equalsIgnoreCase(this.favouriteStates.get(i).identifier)) || (info == null && !this.favouriteStates.get(i).playerMorph.equalsIgnoreCase(Minecraft.getMinecraft().thePlayer.getCommandSenderName()))) {
                        PacketHandler.sendToServer((EnumMap)Morph.channels, (AbstractPacket)new PacketGuiInput(0, this.favouriteStates.get(i).identifier, false));
                        break;
                    }
                }
            }
        }
    }
    
    static {
        rlFavourite = new ResourceLocation("morph", "textures/gui/fav.png");
        rlSelected = new ResourceLocation("morph", "textures/gui/guiSelected.png");
        rlUnselected = new ResourceLocation("morph", "textures/gui/guiUnselected.png");
        rlUnselectedSide = new ResourceLocation("morph", "textures/gui/guiUnselectedSide.png");
        rlGuiInventory = new ResourceLocation("textures/gui/container/inventory.png");
    }
}
