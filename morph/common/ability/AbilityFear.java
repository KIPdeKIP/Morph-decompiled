package morph.common.ability;

import morph.api.*;
import net.minecraft.entity.player.*;
import net.minecraft.entity.*;
import net.minecraft.util.*;
import net.minecraft.entity.ai.*;
import net.minecraft.pathfinding.*;
import java.util.*;
import net.minecraft.nbt.*;

public class AbilityFear extends Ability
{
    public ArrayList<Class<?>> classList;
    public int radius;
    public double runSpeed;
    public static final ResourceLocation iconResource;
    
    public AbilityFear() {
        this.classList = new ArrayList<Class<?>>();
    }
    
    public AbilityFear(final int radius, final double speed, final Class<?>... entityClass) {
        this.classList = new ArrayList<Class<?>>();
        this.radius = radius;
        this.runSpeed = speed;
        for (final Class<?> clazz : entityClass) {
            if (EntityCreature.class.isAssignableFrom(clazz)) {
                this.classList.add(clazz);
            }
        }
    }
    
    @Override
    public Ability parse(final String[] args) {
        this.radius = Integer.parseInt(args[0]);
        this.runSpeed = Double.parseDouble(args[1]);
        for (int i = 2; i < args.length; ++i) {
            try {
                final Class clz = Class.forName(args[i]);
                if (EntityCreature.class.isAssignableFrom(clz)) {
                    this.classList.add(clz);
                }
            }
            catch (ClassNotFoundException ex) {}
        }
        return this;
    }
    
    @Override
    public String getType() {
        return "fear";
    }
    
    @Override
    public void tick() {
        if (this.getParent().worldObj.getWorldTime() % 22L == 0L) {
            final List<Entity> entityList = (List<Entity>)((EntityPlayer)this.getParent()).getEntityWorld().getEntitiesWithinAABBExcludingEntity((Entity)this.getParent(), this.getParent().boundingBox.expand((double)this.radius, (double)this.radius, (double)this.radius));
            if (!entityList.isEmpty()) {
                for (final Entity entity : entityList) {
                    if (entity instanceof EntityCreature) {
                        final EntityCreature creature = (EntityCreature)entity;
                        for (final Class clz : this.classList) {
                            if (clz.isInstance(creature)) {
                                boolean canRun = false;
                                final Vec3 vec3 = RandomPositionGenerator.findRandomTargetBlockAwayFrom(creature, 16, 7, Vec3.createVectorHelper(this.getParent().posX, this.getParent().posY, this.getParent().posZ));
                                if (vec3 != null && this.getParent().getDistanceSq(vec3.xCoord, vec3.yCoord, vec3.zCoord) >= this.getParent().getDistanceSqToEntity((Entity)creature)) {
                                    final PathEntity newPath = new PathEntity(new PathPoint[] { new PathPoint((int)vec3.xCoord, (int)vec3.yCoord, (int)vec3.zCoord) });
                                    creature.getNavigator().setPath(newPath, 1.0);
                                    canRun = true;
                                }
                                if (!canRun) {
                                    continue;
                                }
                                creature.getNavigator().setSpeed(this.runSpeed);
                            }
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public void kill() {
    }
    
    @Override
    public Ability clone() {
        if (this.classList.isEmpty()) {
            return new AbilityFear();
        }
        final Class<?>[] classArray = (Class<?>[])new Class[this.classList.size()];
        for (int i = 0; i < classArray.length; ++i) {
            classArray[i] = this.classList.get(i);
        }
        return new AbilityFear(this.radius, this.runSpeed, classArray);
    }
    
    @Override
    public void save(final NBTTagCompound tag) {
    }
    
    @Override
    public void load(final NBTTagCompound tag) {
    }
    
    @Override
    public void postRender() {
    }
    
    @Override
    public ResourceLocation getIcon() {
        return AbilityFear.iconResource;
    }
    
    static {
        iconResource = new ResourceLocation("morph", "textures/icon/fear.png");
    }
}
