package morph.common.ability;

import net.minecraft.entity.*;
import morph.api.*;
import morph.common.*;
import java.util.*;

public class AbilityHandler
{
    public static final HashMap<Class<? extends EntityLivingBase>, ArrayList<Ability>> abilityMap;
    public static final HashMap<String, Class<? extends Ability>> stringToClassMap;
    public static final ArrayList<Class<? extends EntityLivingBase>> abilityClassList;
    
    public static void registerAbility(final String name, final Class<? extends Ability> clz) {
        AbilityHandler.stringToClassMap.put(name, clz);
    }
    
    public static void mapAbilities(final Class<? extends EntityLivingBase> entClass, final Ability... abilities) {
        ArrayList<Ability> abilityList = AbilityHandler.abilityMap.get(entClass);
        if (abilityList == null) {
            abilityList = new ArrayList<Ability>();
            AbilityHandler.abilityMap.put(entClass, abilityList);
            if (!AbilityHandler.abilityClassList.contains(entClass)) {
                AbilityHandler.abilityClassList.add(entClass);
            }
        }
        for (final Ability ability : abilities) {
            if (ability != null) {
                boolean added = false;
                if (!AbilityHandler.stringToClassMap.containsKey(ability.getType())) {
                    registerAbility(ability.getType(), ability.getClass());
                    Morph.console("Ability type \"" + ability.getType() + "\" is not registered! Registering.", true);
                }
                for (int i = 0; i < abilityList.size(); ++i) {
                    final Ability ab = abilityList.get(i);
                    if (ab.getType().equals(ability.getType())) {
                        abilityList.remove(i);
                        abilityList.add(i, ability);
                        added = true;
                    }
                }
                if (!added) {
                    abilityList.add(ability);
                }
            }
        }
    }
    
    public static void removeAbility(final Class<? extends EntityLivingBase> entClass, final String type) {
        final ArrayList<Ability> abilityList = AbilityHandler.abilityMap.get(entClass);
        if (abilityList != null) {
            for (int i = abilityList.size() - 1; i >= 0; --i) {
                final Ability ability = abilityList.get(i);
                if (ability.getType().equalsIgnoreCase(type)) {
                    abilityList.remove(i);
                }
            }
        }
    }
    
    public static boolean hasAbility(final Class<? extends EntityLivingBase> entClass, final String type) {
        final ArrayList<Ability> abilities = getEntityAbilities(entClass);
        for (final Ability ability : abilities) {
            if (ability.getType().equalsIgnoreCase(type)) {
                return true;
            }
        }
        return false;
    }
    
    public static ArrayList<Ability> getEntityAbilities(final Class<? extends EntityLivingBase> entClass) {
        if (Morph.config.getSessionInt("abilities") == 1) {
            final ArrayList<Ability> abilities = AbilityHandler.abilityMap.get(entClass);
            if (abilities != null) {
                final String[] disabledAbilities = Morph.config.getSessionString("disabledAbilities").split(",");
                for (int i = abilities.size() - 1; i >= 0; --i) {
                    final Ability ab = abilities.get(i);
                    for (final String s : disabledAbilities) {
                        if (!s.isEmpty() && ab.getType().equals(s)) {
                            abilities.remove(i);
                            break;
                        }
                    }
                }
                return abilities;
            }
            final Class superClz = entClass.getSuperclass();
            if (superClz != EntityLivingBase.class) {
                AbilityHandler.abilityMap.put(entClass, getEntityAbilities(superClz));
                return getEntityAbilities(entClass);
            }
        }
        return new ArrayList<Ability>();
    }
    
    public static Ability createNewAbilityByType(final String type, final String[] arguments) {
        try {
            final Class abilityClass = AbilityHandler.stringToClassMap.get(type);
            if (abilityClass != null) {
                final Ability ab = abilityClass.getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
                try {
                    ab.parse(arguments);
                }
                catch (Exception e2) {
                    Morph.console(("Arguments are erroring when trying to create ability by type: " + abilityClass.getName() + ", " + type + ", args: " + arguments == null) ? "none" : ("arg list of size " + arguments.length), true);
                    e2.printStackTrace();
                }
                return ab;
            }
            Morph.console("Ability type \"" + type + "\" does not exist!", true);
        }
        catch (Exception e3) {
            Morph.console("Error creating ability by type: " + type, true);
            e3.printStackTrace();
        }
        return null;
    }
    
    static {
        abilityMap = new HashMap<Class<? extends EntityLivingBase>, ArrayList<Ability>>();
        stringToClassMap = new HashMap<String, Class<? extends Ability>>();
        abilityClassList = new ArrayList<Class<? extends EntityLivingBase>>();
        registerAbility("climb", AbilityClimb.class);
        registerAbility("fallNegate", AbilityFallNegate.class);
        registerAbility("fear", AbilityFear.class);
        registerAbility("fly", AbilityFly.class);
        registerAbility("float", AbilityFloat.class);
        registerAbility("fireImmunity", AbilityFireImmunity.class);
        registerAbility("hostile", AbilityHostile.class);
        registerAbility("poisonResistance", AbilityPoisonResistance.class);
        registerAbility("potionEffect", AbilityPotionEffect.class);
        registerAbility("sink", AbilitySink.class);
        registerAbility("step", AbilityStep.class);
        registerAbility("sunburn", AbilitySunburn.class);
        registerAbility("swim", AbilitySwim.class);
        registerAbility("waterAllergy", AbilityWaterAllergy.class);
        registerAbility("witherResistance", AbilityWitherResistance.class);
    }
}
