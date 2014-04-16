package org.jglrxavpok.mods.customai.common.aifactory;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.passive.EntitySheep;

import org.jglrxavpok.mods.customai.ai.EntityAIFleeSunEvenNotBurning;
import org.jglrxavpok.mods.customai.ai.EntityAIFollowEntity;
import org.jglrxavpok.mods.customai.ai.EntityAIRangeAttack;
import org.jglrxavpok.mods.customai.ai.EntityAITeleportRandomly;
import org.jglrxavpok.mods.customai.ai.Projectile;
import org.jglrxavpok.mods.customai.common.CustomAIException;
import org.jglrxavpok.mods.customai.common.CustomAIHelper;
import org.jglrxavpok.mods.customai.json.JSONObject;

public class EntityCustomAIAddedWorker extends EntityAIWorker
{

    @SuppressWarnings("unchecked")
    @Override
    public EntityAITaskEntry generateAIBaseWithExceptions(EntityLiving entity, JSONObject json) throws Exception
    {
        if(!json.has("type"))
            return null;
        String clazz = json.getString("type");
        Class<? extends EntityAIBase> c = null;
        if(clazz.contains("."))
        {
            c = (Class<? extends EntityAIBase>) Class.forName(clazz);
        }
        else
        {
            c = CustomAIHelper.tryToGetAITaskFromName(clazz);
            if(c == null)
                throw new CustomAIException("Couldn't find correct AI class for \""+clazz+"\"");
        }
        if(entity == null || CustomAIHelper.isSuitableForEntity(entity, c))
        {
            if(testSheep == null)
                testSheep = new EntitySheep(null);
            EntityAITaskEntry entry = (entity == null ? testSheep.tasks : entity.tasks).new EntityAITaskEntry(json.getInt("priority"), null);
            if(c == EntityAIFleeSunEvenNotBurning.class)
            {
                entry.action = new EntityAIFleeSunEvenNotBurning((EntityCreature)entity, json.getDouble("Move speed"));
            }
            else if(c == EntityAIFollowEntity.class)
            {
                Class<? extends Entity> clazz1 = null;
                Class<? extends Entity> entClass = getEntityClass(json.getString("Entity class name"));
                if(entClass != null)
                    clazz1 = entClass;
                else if(checkIfClassExists(clazz))
                    clazz1 = (Class<? extends Entity>) Class.forName(clazz);
                else
                    clazz1 = EntityLiving.class;
                entry.action = new EntityAIFollowEntity(entity, clazz1, json.getDouble("Move speed"), (float)json.getDouble("Max distance"));
            }
            else if(c == EntityAITeleportRandomly.class)
            {
                entry.action = new EntityAITeleportRandomly(entity, json.getInt("Tick rate"), json.getBoolean("Play teleport sound"), json.getBoolean("Avoid liquids"));
            }
            else if(c == EntityAIRangeAttack.class)
            {
                entry.action = new EntityAIRangeAttack(entity, json.getDouble("Move speed"), json.getInt("Reload time"), json.getInt("Max reload time"), (float) json.getDouble("Distance from target"), Projectile.fromString(json.getString("Projectile type")));
            }
            return entry;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public JSONObject generateJSON(EntityLiving entity, EntityAITaskEntry entry)
    {
        if(entity == null)
            entity = testSheep;
        if(entity == null)
            entity = new EntitySheep(null);
        JSONObject object = new JSONObject();
        if(entry == null || entry.action == null)
            return object;
        object.put("type", entry.action.getClass().getCanonicalName());
        object.put("priority", entry.priority);
        
        Class<? extends EntityAIBase> clazz = entry.action.getClass();
        if(clazz == EntityAIFleeSunEvenNotBurning.class)
        {
            object.put("Move speed", (Double)ObfuscationReflectionHelper.getPrivateValue(EntityAIFleeSunEvenNotBurning.class, (EntityAIFleeSunEvenNotBurning)entry.action, 4));
        }
        else if(clazz == EntityAIFollowEntity.class)
        {
            object.put("Entity class name", getEntityName(((Class<? extends Entity>)ObfuscationReflectionHelper.getPrivateValue(EntityAIFollowEntity.class, (EntityAIFollowEntity)entry.action, 0))));
            object.put("Move speed", (Double)ObfuscationReflectionHelper.getPrivateValue(EntityAIFollowEntity.class, (EntityAIFollowEntity)entry.action, 3));
            object.put("Max distance", (Float)ObfuscationReflectionHelper.getPrivateValue(EntityAIFollowEntity.class, (EntityAIFollowEntity)entry.action, 4));
        }
        else if(clazz == EntityAITeleportRandomly.class)
        {
            object.put("Tick rate", (Integer)ObfuscationReflectionHelper.getPrivateValue(EntityAITeleportRandomly.class, (EntityAITeleportRandomly)entry.action, 1));
            object.put("Play teleport sound", (Boolean)ObfuscationReflectionHelper.getPrivateValue(EntityAITeleportRandomly.class, (EntityAITeleportRandomly)entry.action, 4));
            object.put("Avoid liquids", (Boolean)ObfuscationReflectionHelper.getPrivateValue(EntityAITeleportRandomly.class, (EntityAITeleportRandomly)entry.action, 5));
        }
        else if(clazz == EntityAIRangeAttack.class)
        {
            object.put("Move speed", (Double)ObfuscationReflectionHelper.getPrivateValue(EntityAIRangeAttack.class, (EntityAIRangeAttack)entry.action, 3));
            object.put("Reload time", (Integer)ObfuscationReflectionHelper.getPrivateValue(EntityAIRangeAttack.class, (EntityAIRangeAttack)entry.action, 5));
            object.put("Max reload time", (Integer)ObfuscationReflectionHelper.getPrivateValue(EntityAIRangeAttack.class, (EntityAIRangeAttack)entry.action, 6));
            object.put("Distance from target", (Float)ObfuscationReflectionHelper.getPrivateValue(EntityAIRangeAttack.class, (EntityAIRangeAttack)entry.action, 7));
            object.put("Projectile type", ((Projectile)ObfuscationReflectionHelper.getPrivateValue(EntityAIRangeAttack.class, (EntityAIRangeAttack)entry.action, 9)).getID());
        }
        return object;
    }

    @Override
    public JSONObject createDefaultJSON(Class<? extends EntityAIBase> clazz)
    {
        JSONObject object = new JSONObject();
        object.put("type", clazz.getCanonicalName());
        object.put("priority", 1);
        if(clazz == EntityAIFleeSunEvenNotBurning.class)
        {
            object.put("Move speed", 0.9);
        }
        else if(clazz == EntityAIFollowEntity.class)
        {
            object.put("Entity class name", "none");
            object.put("Move speed", 0.9);
            object.put("Max distance", 10f);
        }
        else if(clazz == EntityAITeleportRandomly.class)
        {
            object.put("Tick rate", 60);
            object.put("Play teleport sound", true);
            object.put("Avoid liquids", true);
        }
        else if(clazz == EntityAIRangeAttack.class)
        {
            object.put("Move speed", 0.9);
            object.put("Reload time", 20);
            object.put("Max reload time", 60);
            object.put("Distance from target", 10f);
            object.put("Projectile type", "Arrows");
        }
        return object;
    }

    @Override
    public boolean isSuitableForEntity(EntityLiving living,
            Class<? extends EntityAIBase> clazz)
    {
        if(clazz == EntityAIFleeSunEvenNotBurning.class && ! (living instanceof EntityCreature))
            return false;
        return true;
    }

}
