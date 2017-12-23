package morph.common.thread;

import morph.common.*;
import com.google.gson.*;
import com.google.gson.reflect.*;
import com.google.common.io.*;
import java.net.*;
import morph.api.*;
import morph.common.ability.*;
import net.minecraft.entity.*;
import java.lang.reflect.*;
import java.io.*;
import java.util.*;
import morph.common.morph.*;

public class ThreadGetOnlineResources extends Thread
{
    public String sitePrefix;
    
    public ThreadGetOnlineResources(final String prefix) {
        this.sitePrefix = "https://raw.github.com/iChun/Morph/legacy1710/src/main/resources/assets/morph/mod/";
        if (!prefix.isEmpty()) {
            this.sitePrefix = prefix;
        }
        this.setName("Morph Online Resource Thread");
        this.setDaemon(true);
    }
    
    @Override
    public void run() {
        try {
            if (Morph.config.getInt("abilities") == 1) {
                final Gson gson = new Gson();
                final Type mapType = new TypeToken<Map<String, String[]>>() {}.getType();
                Map<String, String[]> json;
                try {
                    if (Morph.config.getInt("useLocalResources") == 1) {
                        final InputStream con = new FileInputStream(new File(Morph.configFolder, "AbilitySupport.json"));
                        final String data = new String(ByteStreams.toByteArray(con));
                        con.close();
                        json = (Map<String, String[]>)gson.fromJson(data, mapType);
                    }
                    else {
                        final Reader fileIn = new InputStreamReader(new URL(this.sitePrefix + "AbilitySupport.json").openStream());
                        json = (Map<String, String[]>)gson.fromJson(fileIn, mapType);
                        fileIn.close();
                    }
                }
                catch (Exception e) {
                    if (Morph.config.getInt("useLocalResources") == 1) {
                        Morph.console("Failed to retrieve local mod mob ability mappings.", true);
                    }
                    else {
                        Morph.console("Failed to retrieve mod mob ability mappings from " + (Morph.config.getString("customPatchLink").isEmpty() ? "GitHub!" : this.sitePrefix), true);
                    }
                    e.printStackTrace();
                    final Reader fileIn2 = new InputStreamReader(Morph.class.getResourceAsStream("/assets/morph/mod/AbilitySupport.json"));
                    json = (Map<String, String[]>)gson.fromJson(fileIn2, mapType);
                    fileIn2.close();
                }
                if (json != null) {
                    int mcMappings = 0;
                    for (final Map.Entry<String, String[]> e2 : json.entrySet()) {
                        try {
                            Class.forName(e2.getKey());
                            final ArrayList<Ability> abilityObjs = new ArrayList<Ability>();
                            for (String ability : e2.getValue()) {
                                boolean hasArgs = false;
                                if (ability != null) {
                                    if (ability.contains("|")) {
                                        final ArrayList<String> argVars = new ArrayList<String>();
                                        hasArgs = true;
                                        final String args = ability.split("\\|")[1];
                                        ability = ability.split("\\|")[0];
                                        if (args.contains(",")) {
                                            for (final String arg : args.split(",")) {
                                                argVars.add(arg.trim());
                                            }
                                        }
                                        else {
                                            argVars.add(args.trim());
                                        }
                                        try {
                                            final Class abilityClass = AbilityHandler.stringToClassMap.get(ability);
                                            if (abilityClass != null) {
                                                final Ability ab = abilityClass.getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
                                                try {
                                                    ab.parse(argVars.toArray(new String[0]));
                                                }
                                                catch (Exception e7) {
                                                    Morph.console("Mappings are erroring! These mappings are probably invalid or outdated: " + abilityClass.getName() + ", " + ability + ", args: " + args, true);
                                                }
                                                abilityObjs.add(ab);
                                            }
                                            else {
                                                Morph.console("Ability \"" + ability + "\" does not exist for: " + e2.getKey() + ", args: " + args, true);
                                            }
                                        }
                                        catch (Exception e3) {
                                            e3.printStackTrace();
                                        }
                                    }
                                    if (!ability.isEmpty() && !hasArgs) {
                                        final Class abilityClass2 = AbilityHandler.stringToClassMap.get(ability);
                                        if (abilityClass2 != null) {
                                            try {
                                                abilityObjs.add(abilityClass2.getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]));
                                            }
                                            catch (Exception e4) {
                                                e4.printStackTrace();
                                            }
                                        }
                                        else {
                                            Morph.console("Ability \"" + ability + "\" does not exist for: " + e2.getKey(), true);
                                        }
                                    }
                                }
                            }
                            if (abilityObjs.size() <= 0) {
                                continue;
                            }
                            final Class<? extends EntityLivingBase> entityClass = (Class<? extends EntityLivingBase>)Class.forName(e2.getKey());
                            if (entityClass == null) {
                                continue;
                            }
                            if (AbilityHandler.abilityMap.containsKey(entityClass)) {
                                Morph.console("Ignoring ability mapping for " + e2.getKey() + "! Already has abilities mapped!", true);
                            }
                            else {
                                if (entityClass.getName().startsWith("net.minecraft")) {
                                    ++mcMappings;
                                }
                                else {
                                    final StringBuilder sb = new StringBuilder();
                                    sb.append("Adding ability mappings ");
                                    for (int i = 0; i < abilityObjs.size(); ++i) {
                                        final Ability a = abilityObjs.get(i);
                                        sb.append(a.getType());
                                        if (i != abilityObjs.size() - 1) {
                                            sb.append(", ");
                                        }
                                    }
                                    sb.append(" to ");
                                    sb.append(entityClass);
                                    Morph.console(sb.toString(), false);
                                }
                                AbilityHandler.mapAbilities(entityClass, (Ability[])abilityObjs.toArray(new Ability[0]));
                            }
                        }
                        catch (ClassNotFoundException ex) {}
                    }
                    Morph.console("Found and mapped ability mappings for " + mcMappings + " presumably Minecraft mobs.", false);
                }
            }
            if (Morph.config.getInt("NBTStripper") == 1) {
                final Gson gson = new Gson();
                final Type mapType = new TypeToken<Map<String, String[]>>() {}.getType();
                Map<String, String[]> json;
                try {
                    if (Morph.config.getInt("useLocalResources") == 1) {
                        final InputStream con = new FileInputStream(new File(Morph.configFolder, "NBTStripper.json"));
                        final String data = new String(ByteStreams.toByteArray(con));
                        con.close();
                        json = (Map<String, String[]>)gson.fromJson(data, mapType);
                    }
                    else {
                        final Reader fileIn = new InputStreamReader(new URL(this.sitePrefix + "NBTStripper.json").openStream());
                        json = (Map<String, String[]>)gson.fromJson(fileIn, mapType);
                        fileIn.close();
                    }
                }
                catch (Exception e) {
                    if (Morph.config.getInt("useLocalResources") == 1) {
                        Morph.console("Failed to retrieve local NBT stripper mappings.", true);
                    }
                    else {
                        Morph.console("Failed to retrieve NBT stripper mappings from " + (Morph.config.getString("customPatchLink").isEmpty() ? "GitHub!" : this.sitePrefix), true);
                    }
                    e.printStackTrace();
                    final Reader fileIn2 = new InputStreamReader(Morph.class.getResourceAsStream("/assets/morph/mod/NBTStripper.json"));
                    json = (Map<String, String[]>)gson.fromJson(fileIn2, mapType);
                    fileIn2.close();
                }
                if (json != null) {
                    for (final Map.Entry<String, String[]> e5 : json.entrySet()) {
                        try {
                            addStripperMappings((Class<? extends EntityLivingBase>)Class.forName(e5.getKey()), (String[])e5.getValue());
                        }
                        catch (Exception ex2) {}
                    }
                }
            }
        }
        catch (Exception e6) {
            e6.printStackTrace();
        }
    }
    
    public static void addStripperMappings(final Class<? extends EntityLivingBase> clz, final String... tagNames) {
        final ArrayList<String> mappings = MorphHandler.getNBTTagsToStrip(clz);
        for (final String tagName : tagNames) {
            if (!mappings.contains(tagName)) {
                mappings.add(tagName);
            }
        }
    }
}
