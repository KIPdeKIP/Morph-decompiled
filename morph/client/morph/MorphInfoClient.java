package morph.client.morph;

import net.minecraft.entity.player.*;
import morph.common.morph.*;
import morph.client.model.*;

public class MorphInfoClient extends MorphInfo
{
    public EntityPlayer player;
    public ModelInfo prevModelInfo;
    public ModelInfo nextModelInfo;
    public ModelMorph interimModel;
    
    public MorphInfoClient(final String name, final MorphState prev, final MorphState next) {
        super(name, prev, next);
        if (prev.entInstance != null) {
            this.prevModelInfo = ModelList.getModelInfo(prev.entInstance.getClass());
        }
        if (next.entInstance != null) {
            this.nextModelInfo = ModelList.getModelInfo(next.entInstance.getClass());
        }
        this.interimModel = new ModelMorph(this);
    }
}
