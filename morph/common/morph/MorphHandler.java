package morph.common.morph;

import net.minecraft.entity.*;
import cpw.mods.fml.common.*;
import morph.common.*;
import net.minecraftforge.common.util.*;
import net.minecraft.entity.player.*;
import morph.common.packet.*;
import ichun.common.core.network.*;
import java.util.*;

public class MorphHandler
{
    public static HashMap<Class<? extends EntityLivingBase>, ArrayList<String>> stripperMappings;
    
    public static MorphState addOrGetMorphState(final ArrayList<MorphState> states, final MorphState state) {
        int pos = -1;
        boolean isFavourite = state.isFavourite;
        for (int i = states.size() - 1; i >= 0; --i) {
            final MorphState mState = states.get(i);
            if (mState.identifier.equalsIgnoreCase(state.identifier) || (!state.playerMorph.equalsIgnoreCase("") && mState.playerMorph.equalsIgnoreCase(state.playerMorph))) {
                isFavourite = mState.isFavourite;
                states.remove(i);
                pos = i;
            }
        }
        if (state.playerName.equalsIgnoreCase(state.playerMorph) && !state.playerMorph.equalsIgnoreCase("")) {
            isFavourite = true;
            pos = 0;
        }
        if (pos != -1) {
            states.add(pos, state);
        }
        else {
            states.add(state);
        }
        state.isFavourite = isFavourite;
        if (FMLCommonHandler.instance().getEffectiveSide().isClient() && Morph.config.getInt("sortMorphs") == 2) {
            Collections.sort(states);
        }
        return state;
    }
    
    public static void updatePlayerOfMorphStates(final EntityPlayerMP player, final MorphState morphState, final boolean clear) {
        if (player.playerNetServerHandler == null || player.getClass() == FakePlayer.class) {
            return;
        }
        ArrayList<MorphState> states;
        if (morphState == null) {
            states = Morph.proxy.tickHandlerServer.getPlayerMorphs(player.worldObj, (EntityPlayer)player);
        }
        else {
            states = new ArrayList<MorphState>();
            states.add(morphState);
        }
        PacketHandler.sendToPlayer((EnumMap)Morph.channels, (AbstractPacket)new PacketMorphStates(clear, states), (EntityPlayer)player);
    }
    
    public static MorphState getMorphState(final EntityPlayerMP player, final String identifier) {
        final ArrayList<MorphState> states = Morph.proxy.tickHandlerServer.getPlayerMorphs(player.worldObj, (EntityPlayer)player);
        for (final MorphState state : states) {
            if (state.identifier.equalsIgnoreCase(identifier)) {
                return state;
            }
        }
        return null;
    }
    
    public static void reorderMorphs(final String starter, final LinkedHashMap<String, ArrayList<MorphState>> morphMap) {
        if (Morph.config.getInt("sortMorphs") == 1 || Morph.config.getInt("sortMorphs") == 2) {
            final ArrayList<String> order = new ArrayList<String>();
            final Iterator<String> ite = morphMap.keySet().iterator();
            while (ite.hasNext()) {
                order.add(ite.next());
            }
            Collections.sort(order);
            order.remove(starter);
            order.add(0, starter);
            final LinkedHashMap<String, ArrayList<MorphState>> bufferList = new LinkedHashMap<String, ArrayList<MorphState>>(morphMap);
            morphMap.clear();
            for (int i = 0; i < order.size(); ++i) {
                morphMap.put(order.get(i), bufferList.get(order.get(i)));
            }
        }
    }
    
    public static ArrayList<String> getNBTTagsToStrip(final EntityLivingBase ent) {
        final ArrayList<String> tagsToStrip = new ArrayList<String>();
        for (Class clz = ent.getClass(); clz != EntityLivingBase.class; clz = clz.getSuperclass()) {
            tagsToStrip.addAll(getNBTTagsToStrip(clz));
        }
        return tagsToStrip;
    }
    
    public static ArrayList<String> getNBTTagsToStrip(final Class<? extends EntityLivingBase> clz) {
        ArrayList<String> list = MorphHandler.stripperMappings.get(clz);
        if (list == null) {
            list = new ArrayList<String>();
            MorphHandler.stripperMappings.put(clz, list);
        }
        return list;
    }
    
    static {
        MorphHandler.stripperMappings = new HashMap<Class<? extends EntityLivingBase>, ArrayList<String>>();
    }
}
