package org.jglrxavpok.mods.customai.common;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIDoorInteract;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;

import org.jglrxavpok.mods.customai.common.aifactory.EntityAIFactory;
import org.jglrxavpok.mods.customai.common.aifactory.EntityAIWorker;
import org.jglrxavpok.mods.customai.json.JSONObject;

public final class CustomAIHelper
{

    public static HashMap<Class<? extends EntityAIBase>, String> map = new HashMap<Class<? extends EntityAIBase>, String>();
    public static HashMap<Class<? extends EntityAIBase>, Boolean> isTarget = new HashMap<Class<? extends EntityAIBase>, Boolean>();
    public static HashMap<Class<? extends EntityAIBase>, Integer> ownerFields = new HashMap<Class<? extends EntityAIBase>, Integer>();
    public static HashMap<Class<? extends EntityAIBase>, Class<? extends EntityAIBase>> ownerFieldClasses = new HashMap<Class<? extends EntityAIBase>, Class<? extends EntityAIBase>>();
    
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
        EntityAIWorker worker = EntityAIFactory.instance().getWorkerFromClass(c);
        if(worker == null)
            return false;
        return worker.isSuitableForEntity(entity, c);
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
        return EntityAIFactory.instance().createDefaultJSON(ai);
    }

    public static EntityAITaskEntry generateAIFromJSON(EntityLiving entity, JSONObject json)
    {
        return EntityAIFactory.instance().generateAIBase(entity, json);
    }

    /**
     * Only for testing/map making purpose, never used actually by the mod
     * @return
     */
    public static String getCVSList()
    {
        String s = "Name;Class;Field1=DefaultValue1|Field2=DefaultValue2\n";
        Set<Class<? extends EntityAIBase>> keyset = map.keySet();
        Iterator<Class<? extends EntityAIBase>> it = keyset.iterator();
        final HashMap<String, String> tmpMap = new HashMap<String, String>();
        ArrayList<String> tmpList = new ArrayList<String>();
        while(it.hasNext())
        {
            Class<? extends EntityAIBase> c = it.next();
            String name = map.get(c);
            String list = createList(EntityAIFactory.instance().createDefaultJSON(c));
            String s1 = name+";"+c.getCanonicalName()+";"+list;
            tmpMap.put(s1, name);
            tmpList.add(s1);
        }
        Collections.sort(tmpList, new Comparator<String>()
                {
                    @Override
                    public int compare(String o1, String o2)
                    {
                        String s1 = tmpMap.get(o1);
                        String s2 = tmpMap.get(o2);
                        return s1.compareTo(s2);
                    }
                });
        for(String t : tmpList)
        {
            s+=t+"\n";
        }
        return s;
    }

    @SuppressWarnings("unchecked")
    private static String createList(JSONObject json)
    {
        String list = "";
        Iterator<String> it = json.keys();
        int index = 0;
        while(it.hasNext())
        {
            String key = it.next();
            if(index++ != 0)
            {
                list+=" | ";
            }
            list+=key+"="+json.get(key);
        }
        return list;
    }

    public static Class<? extends EntityAIBase> tryToGetAITaskFromName(String clazz)
    {
        Set<Class<? extends EntityAIBase>> tasks = map.keySet();
        Iterator<Class<? extends EntityAIBase>> it = tasks.iterator();
        while(it.hasNext())
        {
            Class<? extends EntityAIBase> c = it.next();
            String s = map.get(c);
            if(s.equalsIgnoreCase(clazz))
            {
                return c;
            }
        }
        return null;
    }
}
