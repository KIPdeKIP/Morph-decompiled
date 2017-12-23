package morph.common.morph;

import net.minecraft.world.*;
import ichun.common.core.*;
import net.minecraftforge.common.util.*;
import morph.common.*;
import net.minecraft.entity.player.*;
import net.minecraft.client.entity.*;
import cpw.mods.fml.relauncher.*;
import net.minecraft.nbt.*;
import net.minecraft.entity.*;
import java.util.*;

public class MorphState implements Comparable
{
    public final int NBT_PROTOCOL = 2;
    public String playerName;
    public String playerMorph;
    public boolean isFavourite;
    public boolean isRemote;
    public EntityLivingBase entInstance;
    public String identifier;
    
    public MorphState(final World world, final String name, final String player, final NBTTagCompound tag, final boolean remote) {
        this.playerName = name;
        this.playerMorph = player;
        this.isFavourite = name.equalsIgnoreCase(player);
        this.isRemote = remote;
        if (!player.equalsIgnoreCase("")) {
            this.entInstance = (EntityLivingBase)(this.isRemote ? this.createPlayer(world, player) : new FakePlayer((WorldServer)world, EntityHelperBase.getSimpleGameProfileFromName(player)));
        }
        else if (tag != null) {
            this.entInstance = (EntityLivingBase)EntityList.createEntityFromNBT(tag, world);
        }
        if (this.entInstance != null) {
            final NBTTagCompound fakeTag = new NBTTagCompound();
            this.entInstance.writeEntityToNBT(fakeTag);
            this.writeFakeTags(this.entInstance, fakeTag);
            if (this.playerMorph.equalsIgnoreCase("")) {
                this.identifier = this.entInstance.getClass().toString() + parseTag(fakeTag);
            }
            else {
                this.identifier = "playerMorphState::player_" + this.playerMorph;
            }
        }
    }
    
    public NBTTagCompound getTag() {
        final NBTTagCompound tag = new NBTTagCompound();
        tag.setString("playerName", this.playerName);
        tag.setString("playerMorph", this.playerMorph);
        tag.setBoolean("isFavourite", this.isFavourite);
        final NBTTagCompound tag2 = new NBTTagCompound();
        if (this.entInstance != null) {
            try {
                this.entInstance.writeToNBTOptional(tag2);
            }
            catch (Exception e) {
                Morph.console(this.entInstance.toString() + " threw an exception when trying to save!", true);
                e.printStackTrace();
            }
            this.writeFakeTags(this.entInstance, tag2);
        }
        tag.setTag("entInstanceTag", (NBTBase)tag2);
        tag.setString("identifier", this.identifier);
        return tag;
    }
    
    public void readTag(final World world, final NBTTagCompound tag) {
        this.playerName = tag.getString("playerName");
        this.playerMorph = tag.getString("playerMorph");
        this.isFavourite = (tag.getBoolean("isFavourite") || this.playerName.equals(this.playerMorph));
        final NBTTagCompound tag2 = tag.getCompoundTag("entInstanceTag");
        boolean invalid = false;
        if (this.playerName.equalsIgnoreCase("") || (this.playerMorph.equalsIgnoreCase("") && tag2.getString("id").equalsIgnoreCase(""))) {
            invalid = true;
        }
        if (!invalid) {
            if (!this.playerMorph.equalsIgnoreCase("")) {
                this.entInstance = (EntityLivingBase)(this.isRemote ? this.createPlayer(world, this.playerMorph) : new FakePlayer((WorldServer)world, EntityHelperBase.getSimpleGameProfileFromName(this.playerMorph)));
                this.identifier = "playerMorphState::player_" + this.playerMorph;
            }
            else {
                try {
                    this.entInstance = (EntityLivingBase)EntityList.createEntityFromNBT(tag2, world);
                    this.identifier = tag.getString("identifier");
                    if (this.entInstance != null && ((this.identifier.contains(":[") && this.identifier.endsWith("]")) || tag2.getInteger("MorphNBTProtocolNumber") < 2) && !this.attemptRepairs(this.entInstance, tag2, tag2.getInteger("MorphNBTProtocolNumber"))) {
                        this.identifier = "";
                        invalid = true;
                    }
                }
                catch (Throwable e) {
                    Morph.console("A mob (as a morph) is throwing an error when being read from NBT! You should report this to the mod author of the mob!", true);
                    e.printStackTrace();
                    invalid = true;
                }
            }
            if (this.entInstance == null) {
                invalid = true;
            }
            else if (this.identifier.equalsIgnoreCase("")) {
                final NBTTagCompound fakeTag = new NBTTagCompound();
                this.entInstance.writeEntityToNBT(fakeTag);
                this.writeFakeTags(this.entInstance, fakeTag);
                this.identifier = this.entInstance.getClass().toString() + parseTag(fakeTag);
            }
        }
        if (invalid) {
            this.entInstance = (EntityLivingBase)EntityList.createEntityByName("Pig", world);
            final NBTTagCompound fakeTag = new NBTTagCompound();
            this.entInstance.writeEntityToNBT(fakeTag);
            this.writeFakeTags(this.entInstance, fakeTag);
            this.identifier = this.entInstance.getClass().toString() + parseTag(fakeTag);
        }
    }
    
    @SideOnly(Side.CLIENT)
    private EntityPlayer createPlayer(final World world, final String player) {
        return (EntityPlayer)new EntityOtherPlayerMP(world, EntityHelperBase.getFullGameProfileFromName(player));
    }
    
    public void writeFakeTags(final EntityLivingBase living, final NBTTagCompound tag) {
        tag.setFloat("HealF", 32767.0f);
        tag.setShort("Health", (short)32767);
        tag.setShort("HurtTime", (short)0);
        tag.setShort("DeathTime", (short)0);
        tag.setShort("AttackTime", (short)0);
        tag.setTag("ActiveEffects", (NBTBase)new NBTTagList());
        tag.setTag("Attributes", (NBTBase)new NBTTagList());
        tag.setShort("Fire", (short)0);
        tag.setShort("Anger", (short)0);
        tag.setInteger("Age", living.isChild() ? -24000 : 0);
        if (living instanceof EntityLiving) {
            final EntityLiving living2 = (EntityLiving)living;
            final NBTTagList tagList = new NBTTagList();
            for (int i = 0; i < living2.getLastActiveItems().length; ++i) {
                tagList.appendTag((NBTBase)new NBTTagCompound());
            }
            tag.setBoolean("CanPickUpLoot", true);
            tag.setTag("Equipment", (NBTBase)tagList);
            tag.setBoolean("Leashed", false);
            tag.setBoolean("PersistenceRequired", true);
        }
        final ArrayList<String> stripList = MorphHandler.getNBTTagsToStrip(living);
        for (final String s : stripList) {
            tag.removeTag(s);
        }
        tag.removeTag("bukkit");
        tag.removeTag("InLove");
        tag.setInteger("MorphNBTProtocolNumber", 2);
    }
    
    public static String parseTag(final NBTTagCompound tag) {
        final StringBuilder sb = new StringBuilder();
        final ArrayList<String> tags = new ArrayList<String>();
        final HashMap tagMap = (HashMap)tag.tagMap;
        for (final Object obj : tagMap.entrySet()) {
            final Map.Entry e = (Map.Entry)obj;
            tags.add(e.getKey().toString() + ":" + tagMap.get(e.getKey()));
        }
        Collections.sort(tags);
        sb.append("{");
        for (int i = 0; i < tags.size(); ++i) {
            sb.append(tags.get(i));
            if (i != tags.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("}");
        return sb.toString();
    }
    
    public boolean attemptRepairs(final EntityLivingBase living, final NBTTagCompound tag, int nbtProtocol) {
        while (nbtProtocol < 2) {
            if (nbtProtocol == 1) {
                tag.setInteger("MorphNBTProtocolNumber", 2);
                final NBTTagCompound fakeTag = new NBTTagCompound();
                living.writeEntityToNBT(fakeTag);
                this.writeFakeTags(living, fakeTag);
                tag.setString("identifier", this.identifier = living.getClass().toString() + parseTag(fakeTag));
            }
            ++nbtProtocol;
        }
        tag.setInteger("MorphNBTProtocolNumber", nbtProtocol);
        return nbtProtocol >= 2;
    }
    
    @Override
    public int compareTo(final Object arg0) {
        if (arg0 instanceof MorphState) {
            final MorphState state = (MorphState)arg0;
            return this.identifier.compareTo(state.identifier);
        }
        return 0;
    }
}
