package morph.common.core;

import net.minecraft.world.*;
import net.minecraft.nbt.*;

public class MorphSaveData extends WorldSavedData
{
    public boolean hasTravelledToNether;
    public boolean hasKilledWither;
    
    public MorphSaveData(final String par1Str) {
        super(par1Str);
    }
    
    public void readFromNBT(final NBTTagCompound tag) {
        this.hasTravelledToNether = tag.getBoolean("hasTravelledToNether");
        this.hasKilledWither = tag.getBoolean("hasKilledWither");
    }
    
    public void writeToNBT(final NBTTagCompound tag) {
        tag.setBoolean("hasTravelledToNether", this.hasTravelledToNether);
        tag.setBoolean("hasKilledWither", this.hasKilledWither);
    }
}
