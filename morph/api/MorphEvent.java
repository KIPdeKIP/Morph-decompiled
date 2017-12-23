package morph.api;

import net.minecraftforge.event.entity.player.*;
import cpw.mods.fml.common.eventhandler.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;

@Cancelable
public class MorphEvent extends PlayerEvent
{
    public final EntityLivingBase prevMorph;
    public final EntityLivingBase morph;
    
    public MorphEvent(final EntityPlayer player, final EntityLivingBase prevMorph, final EntityLivingBase morph) {
        super(player);
        this.prevMorph = prevMorph;
        this.morph = morph;
    }
}
