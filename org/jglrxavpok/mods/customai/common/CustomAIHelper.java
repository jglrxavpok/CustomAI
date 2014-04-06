package org.jglrxavpok.mods.customai.common;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIBeg;
import net.minecraft.entity.ai.EntityAICreeperSwell;
import net.minecraft.entity.ai.EntityAIDefendVillage;
import net.minecraft.entity.ai.EntityAIDoorInteract;
import net.minecraft.entity.ai.EntityAIFleeSun;
import net.minecraft.entity.ai.EntityAIFollowGolem;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookAtTradePlayer;
import net.minecraft.entity.ai.EntityAILookAtVillager;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIMoveIndoors;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIOcelotSit;
import net.minecraft.entity.ai.EntityAIOwnerHurtByTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtTarget;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAIPlay;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAIRestrictSun;
import net.minecraft.entity.ai.EntityAIRunAroundLikeCrazy;
import net.minecraft.entity.ai.EntityAISit;
import net.minecraft.entity.ai.EntityAITargetNonTamed;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAITradePlayer;
import net.minecraft.entity.ai.EntityAIVillagerMate;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;

import org.jglrxavpok.mods.customai.ai.EntityAIFleeSunEvenNotBurning;
import org.jglrxavpok.mods.customai.json.JSONObject;

public final class CustomAIHelper
{

    public static HashMap<Class<? extends EntityAIBase>, String> map = new HashMap<Class<? extends EntityAIBase>, String>();
    public static HashMap<Class<? extends EntityAIBase>, Boolean> isTarget = new HashMap<Class<? extends EntityAIBase>, Boolean>();
    public static HashMap<Class<? extends EntityAIBase>, Integer> ownerFields = new HashMap<Class<? extends EntityAIBase>, Integer>();
    public static HashMap<Class<? extends EntityAIBase>, Class<? extends EntityAIBase>> ownerFieldClasses = new HashMap<Class<? extends EntityAIBase>, Class<? extends EntityAIBase>>();
    private static EntitySheep testSheep;
    
    public static final int TARGET_MUTEX_BITS = 1;
    public static final int MOVE_MUTEX_BITS = 1;
    public static final int WATCH_MUTEX_BITS = 2;
    public static final int ENTITY_INTERACT_MUTEX_BITS = 3;
    public static final int SWIMMING_MUTEX_BITS = 4;
    public static final int SIT_MUTEX_BITS = 5;
    public static final int SPECIAL_MUTEX_BITS = 7;
    
    public static void registerAI(Class<? extends EntityAIBase> ai, String name, boolean isTarget, int ownerFieldPos)
    {
        map.put(ai,name);
        CustomAIHelper.isTarget.put(ai,isTarget);
        ownerFields.put(ai, ownerFieldPos);
    }
    
    public static void registerAI(Class<? extends EntityAIBase> ai, String name, boolean isTarget, int ownerFieldPos, Class<? extends EntityAIBase> ownerFieldClass)
    {
        registerAI(ai, name, isTarget, ownerFieldPos);
        ownerFieldClasses.put(ai, ownerFieldClass);
    }
    
    public static boolean hasEntityAI(Entity entity)
    {
        if(entity != null)
        {
            if(entity instanceof EntityLiving)
            {
                EntityLiving living = (EntityLiving)entity;
                Method method = findMethod(EntityLiving.class.getDeclaredMethods(),"isAIEnabled","func_70650_aV");
                method.setAccessible(true);
                try
                {
                    Boolean result = (Boolean) method.invoke(living, new Object[]{});
                    if(result)
                    {
                       return true; 
                    }
                    else
                    {
                        return false;
                    }
                }
                catch (IllegalAccessException e)
                {
                    e.printStackTrace();
                }
                catch (IllegalArgumentException e)
                {
                    e.printStackTrace();
                }
                catch (InvocationTargetException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    private static Method findMethod(Method[] declaredMethods, String nonObfuscatedName, String obfuscatedName)
    {
        for(Method m : declaredMethods)
        {
            if(m.getName().equals(nonObfuscatedName) || m.getName().equals(obfuscatedName))
                return m;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static List<EntityAITaskEntry> getTasksList(Entity entity)
    {
        if(hasEntityAI(entity))
        {
            EntityLiving living = (EntityLiving)entity;
            return living.tasks.taskEntries;
        }
        return null;
    }
    
    @SuppressWarnings("unchecked")
    public static List<EntityAITaskEntry> getTargetTasksList(Entity entity)
    {
        if(hasEntityAI(entity))
        {
            EntityLiving living = (EntityLiving)entity;
            return living.targetTasks.taskEntries;
        }
        return null;
    }

    public static String getNameForTask(Class<? extends EntityAIBase> action)
    {
        if(map.containsKey(action))
        {
            return map.get(action);
        }
        String s = action.getCanonicalName();
        if(s.contains("."))
        {
            return s.substring(s.lastIndexOf('.')+1);
        }
        return s;
    }
    
    public static String getNameForTask(EntityAIBase action)
    {
        if(map.containsKey(action.getClass()))
        {
            return map.get(action.getClass());
        }
        String s = action.getClass().getCanonicalName();
        if(s.contains("."))
        {
            return s.substring(s.lastIndexOf('.')+1);
        }
        return s;
    }

    public static JSONObject generateJSONFromAI(EntityLiving entity, EntityAITaskEntry entry)
    {
        return EntityAIFactory.instance().generateJSON(entity, entry);
    }
    
    public static void applyLists(Entity entity, List<EntityAITaskEntry> tasks, List<EntityAITaskEntry> targetTasks)
    {
        if(hasEntityAI(entity))
        {
            clearExecutingTasks(entity);
            clearExecutingTargetTasks(entity);
            getTasksList(entity).clear();
            getTargetTasksList(entity).clear();
            getTargetTasksList(entity).addAll(targetTasks);
            getTasksList(entity).addAll(tasks);
            
            for(EntityAITaskEntry entry : tasks)
            {
                if(entry != null && entry.action != null)
                {
                    setOwner(entry.action, entity);
                    if(entry.action instanceof EntityAIDoorInteract)
                    {
                        ((EntityLiving)entity).getNavigator().setEnterDoors(true);
                        ((EntityLiving)entity).getNavigator().setBreakDoors(true);
                    }
                }
            }
        }
    }
    
    private static void setOwner(EntityAIBase action, Entity entity)
    {
        Class<? extends EntityAIBase> clazz = (Class<? extends EntityAIBase>) action.getClass();
        Integer i = ownerFields.get(clazz);
        Class<? extends EntityAIBase> c = ownerFieldClasses.get(clazz);
        if(i != null && i >= 0)
        {
            try
            {
                Field f = (c == null ? clazz : c).getDeclaredFields()[i];
                f.setAccessible(true);
                f.set(action, entity);
            }
            catch (IllegalArgumentException e)
            {
                e.printStackTrace();
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }
    }

    private static void clearExecutingTargetTasks(Entity entity)
    {
        if(hasEntityAI(entity))
        {
            EntityLiving living = (EntityLiving)entity;
            EntityAITasks targetTasks = living.tasks;
            ((List<?>)ObfuscationReflectionHelper.getPrivateValue(EntityAITasks.class, targetTasks, 2)).clear();
        }
    }

    private static void clearExecutingTasks(Entity entity)
    {
        if(hasEntityAI(entity))
        {
            EntityLiving living = (EntityLiving)entity;
            EntityAITasks tasks = living.tasks;
            ((List<?>)ObfuscationReflectionHelper.getPrivateValue(EntityAITasks.class, tasks, 2)).clear();
        }
    }

    public static EntityAITaskEntry generateAIFromJSON(Entity entity, String jsonData)
    {
        if(!hasEntityAI(entity) && entity  != null)
            return null;
        return EntityAIFactory.instance().generateAIBase((EntityLiving)entity,jsonData);
    }

    public static boolean isSuitableForEntity(EntityLiving entity, Class<? extends EntityAIBase> c)
    {
        if(c == EntityAICreeperSwell.class && !(entity instanceof EntityCreeper))
            return false;
        else if(c == EntityAIAvoidEntity.class && !(entity instanceof EntityCreature))
            return false;
        else if(c == EntityAIAttackOnCollide.class && !(entity instanceof EntityCreature))
            return false;
        else if(c == EntityAIWander.class && !(entity instanceof EntityCreature))
            return false;
        else if(c == EntityAINearestAttackableTarget.class && !(entity instanceof EntityCreature))
            return false;
        else if(c == EntityAIHurtByTarget.class && !(entity instanceof EntityCreature))
            return false;
        else if(c == EntityAIArrowAttack.class && !(entity instanceof IRangedAttackMob))
            return false;
        else if(c == EntityAIBeg.class && !(entity instanceof EntityWolf))
            return false;
        else if(c == EntityAIDefendVillage.class && !(entity instanceof EntityIronGolem))
            return false;
        else if(c == EntityAIFleeSun.class && !(entity instanceof EntityCreature))
            return false;
        else if(c == EntityAIFollowGolem.class && !(entity instanceof EntityVillager))
            return false;
        else if(c == EntityAIFollowOwner.class && !(entity instanceof EntityTameable))
            return false;
        else if(c == EntityAIFollowParent.class && !(entity instanceof EntityAnimal))
            return false;
        else if(c == EntityAILookAtTradePlayer.class && !(entity instanceof EntityVillager))
            return false;
        else if(c == EntityAILookAtVillager.class && !(entity instanceof EntityIronGolem))
            return false;
        else if(c == EntityAIMate.class && !(entity instanceof EntityAnimal))
            return false;
        else if(c == EntityAIPanic.class && !(entity instanceof EntityCreature))
            return false;
        else if(c == EntityAITempt.class && !(entity instanceof EntityCreature))
            return false;
        else if(c == EntityAIMoveIndoors.class && !(entity instanceof EntityCreature))
            return false;
        else if(c == EntityAIMoveThroughVillage.class && !(entity instanceof EntityCreature))
            return false;
        else if(c == EntityAIOcelotSit.class && !(entity instanceof EntityOcelot))
            return false;
        else if(c == EntityAIPlay.class && !(entity instanceof EntityVillager))
            return false;
        else if(c == EntityAIRestrictOpenDoor.class && !(entity instanceof EntityCreature))
            return false;
        else if(c == EntityAIRestrictSun.class && !(entity instanceof EntityCreature))
            return false;
        else if(c == EntityAIRunAroundLikeCrazy.class && !(entity instanceof EntityHorse))
            return false;
        else if(c == EntityAISit.class && !(entity instanceof EntityTameable))
            return false;
        else if(c == EntityAITargetNonTamed.class && !(entity instanceof EntityTameable))
            return false;
        else if(c == EntityAITradePlayer.class && !(entity instanceof EntityVillager))
            return false;
        else if(c == EntityAIVillagerMate.class && !(entity instanceof EntityVillager))
            return false;
        else if(c == EntityAIOwnerHurtTarget.class && !(entity instanceof EntityTameable))
            return false;
        else if(c == EntityAIOwnerHurtByTarget.class && !(entity instanceof EntityTameable))
            return false;
        else if(c == EntityAIFleeSunEvenNotBurning.class && ! (entity instanceof EntityCreature))
            return false;
        return true;
    }

    public static String getNameFromClass(Class<? extends EntityAIBase> c)
    {
        return map.get(c);
    }
    
    public static Class<? extends EntityAIBase> getClassFromName(String name)
    {
        Iterator<Class<? extends EntityAIBase>> it = map.keySet().iterator();
        while(it.hasNext())
        {
            Class<? extends EntityAIBase> c = it.next();
            if(map.get(c).equals(name))
            {
                return c;
            }
        }
        return null;
    }

    public static EntityAITaskEntry createDummyAITask(EntityLiving e, String value)
    {
        JSONObject json = new JsonDummyObject(getClassFromName(value),1);
        return generateAIFromJSON(e, json);
    }
    
    public static JSONObject createDummyJSON(Class<? extends EntityAIBase> ai)
    {
        return EntityAIFactory.instance().createDummyJSON(ai);
    }

    public static EntityAITaskEntry generateAIFromJSON(EntityLiving entity, JSONObject json)
    {
        return EntityAIFactory.instance().generateAIBase(entity, json);
    }
}
