package org.jglrxavpok.mods.customai.common.aifactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.passive.EntitySheep;

import org.jglrxavpok.mods.customai.common.CustomAIException;
import org.jglrxavpok.mods.customai.common.CustomAIHelper;
import org.jglrxavpok.mods.customai.json.JSONObject;

public final class EntityAIFactory
{

    private static EntityAIFactory instance;
    private static final HashMap<Class<? extends EntityAIBase>[], EntityAIWorker> workerRegistry = new HashMap<Class<? extends EntityAIBase>[], EntityAIWorker>(); 

    public static EntityAIFactory instance()
    {
        if(instance == null)
            instance = new EntityAIFactory();
        return instance;
    }

    private static EntitySheep testSheep;
    
    public JSONObject generateJSON(EntityLiving entity, EntityAITaskEntry entry)
    {
        Class<? extends EntityAIBase> clazz = entry.action.getClass();
        EntityAIWorker worker = this.getWorkerFromClass(clazz);
        if(worker == null)
            System.out.println(clazz);
        if(worker != null)
        {
            return worker.generateJSON(entity, entry);
        }
        return new JSONObject();
    }

    public EntityAITaskEntry generateAIBase(EntityLiving entity, String jsonData)
    {
        return generateAIBase(entity, new JSONObject(jsonData));
    }

    @SuppressWarnings("unchecked")
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
            EntityAIWorker worker = getWorkerFromClass(c);
            if(worker != null)
            {
                EntityAITaskEntry entry = worker.generateAIBaseWithExceptions(entity, json);
                if(entry.action == null)
                    return null;
                return entry;
            }
            return null;
        }
        return null;
    }
   
    private EntityAIWorker getWorkerFromClass(Class<? extends EntityAIBase> c)
    {
        Set<Class<? extends EntityAIBase>[]> keys = workerRegistry.keySet();
        Iterator<Class<? extends EntityAIBase>[]> it = keys.iterator();
        while(it.hasNext())
        {
            Class<? extends EntityAIBase>[] classes = it.next();
            for(Class<? extends EntityAIBase> clazz : classes)
            {
                if(clazz == c)
                {
                    return workerRegistry.get(classes);
                }
            }
        }
        return null;
    }

    public EntityAITaskEntry generateAIBase(EntityLiving entity, JSONObject json)
    {
        try
        {
            return generateAIBaseWithExceptions(entity, json);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject createDefaultJSON(Class<? extends EntityAIBase> clazz)
    {
        EntityAIWorker worker = getWorkerFromClass(clazz);
        if(worker != null)
        {
            return worker.createDefaultJSON(clazz);
        }
        return new JSONObject();
    }
    
    public static void hireWorker(EntityAIWorker worker, Class<? extends EntityAIBase>... classes)
    {
        workerRegistry.put(classes, worker);
    }

}
