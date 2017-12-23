package morph.common.morph;

import morph.api.*;
import morph.common.packet.*;
import net.minecraft.nbt.*;
import net.minecraftforge.common.*;
import morph.common.ability.*;
import net.minecraft.world.*;
import java.util.*;

public class MorphInfo
{
    public String playerName;
    public MorphState prevState;
    public MorphState nextState;
    private boolean morphing;
    public int morphProgress;
    public ArrayList<Ability> morphAbilities;
    public boolean flying;
    public boolean sleeping;
    public double healthOffset;
    public double preMorphHealth;
    public boolean firstUpdate;
    
    public MorphInfo() {
        this.morphAbilities = new ArrayList<Ability>();
        this.playerName = "";
    }
    
    public MorphInfo(final String name, final MorphState prev, final MorphState next) {
        this.morphAbilities = new ArrayList<Ability>();
        this.playerName = name;
        this.prevState = prev;
        this.nextState = next;
        this.morphing = false;
        this.morphProgress = 0;
        this.flying = false;
        this.sleeping = false;
        this.firstUpdate = true;
        this.healthOffset = 0.0;
        this.preMorphHealth = 20.0;
    }
    
    public void setMorphing(final boolean flag) {
        this.morphing = flag;
    }
    
    public boolean getMorphing() {
        return this.morphing;
    }
    
    public PacketMorphInfo getMorphInfoAsPacket() {
        return new PacketMorphInfo(this.playerName, this.morphing, this.morphProgress, this.prevState != null, this.nextState != null, (this.prevState != null) ? this.prevState.getTag() : null, (this.nextState != null) ? this.nextState.getTag() : null, this.flying);
    }
    
    public void writeNBT(final NBTTagCompound tag) {
        tag.setString("playerName", this.playerName);
        tag.setInteger("dimension", this.nextState.entInstance.dimension);
        tag.setTag("nextState", (NBTBase)this.nextState.getTag());
        tag.setBoolean("isFlying", this.flying);
        tag.setDouble("healthOffset", this.healthOffset);
        tag.setDouble("preMorphHealth", this.preMorphHealth);
    }
    
    public void readNBT(final NBTTagCompound tag) {
        this.playerName = tag.getString("playerName");
        World dimension = (World)DimensionManager.getWorld(tag.getInteger("dimension"));
        if (dimension == null) {
            dimension = (World)DimensionManager.getWorld(0);
        }
        (this.nextState = new MorphState(dimension, this.playerName, this.playerName, null, false)).readTag(dimension, tag.getCompoundTag("nextState"));
        this.morphing = true;
        this.morphProgress = 80;
        final ArrayList<Ability> newAbilities = AbilityHandler.getEntityAbilities(this.nextState.entInstance.getClass());
        this.morphAbilities = new ArrayList<Ability>();
        for (final Ability ability : newAbilities) {
            try {
                this.morphAbilities.add(ability.clone());
            }
            catch (Exception ex) {}
        }
        this.flying = tag.getBoolean("isFlying");
        this.healthOffset = tag.getDouble("healthOffset");
        this.preMorphHealth = tag.getDouble("preMorphHealth");
    }
}
