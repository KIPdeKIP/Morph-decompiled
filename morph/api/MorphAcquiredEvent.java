package morph.api;

import net.minecraftforge.event.entity.player.*;
import cpw.mods.fml.common.eventhandler.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;

@Cancelable
public class MorphAcquiredEvent extends PlayerEvent
{
    public final EntityLivingBase acquiredMorph;
    
    public MorphAcquiredEvent(final EntityPlayer player, final EntityLivingBase acquired) {
        super(player);
        this.acquiredMorph = acquired;
    }
}
