package org.jglrxavpok.mods.customai.common.aifactory;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;

import org.jglrxavpok.mods.customai.json.JSONObject;

public abstract class EntityAIWorker
{

    public abstract EntityAITaskEntry generateAIBaseWithExceptions(EntityLiving entity, JSONObject json) throws Exception;
    
    public abstract JSONObject generateJSON(EntityLiving entity, EntityAITaskEntry entry);
    
    public abstract JSONObject createDefaultJSON(Class<? extends EntityAIBase> clazz);
    
    protected static EntitySheep testSheep;
    protected static EntitySpider testSpider;
    
    protected boolean checkIfClassExists(String string)
    {
        if(string == null)
            return false;
        try
        {
            Class.forName(string);
            return true;
        }
        catch(Exception e)
        {
            ;
        }
        return false;
    }

    protected String getEntityName(Class<? extends Entity> c)
    {
        String s = (String) EntityList.classToStringMapping.get(c);
        if(c == EntityPlayer.class)
        {
            return "Player";
        }
        if(s == null)
            return c.getCanonicalName();
        else
            return s;
    }
    
    @SuppressWarnings("unchecked")
    protected Class<? extends Entity> getEntityClass(String name)
    {
        if(name.equals("Player"))
            return EntityPlayer.class;
        Class<? extends Entity> clazz = (Class<? extends Entity>) EntityList.stringToClassMapping.get(name);
        if(clazz == null)
            try
            {
                return (Class<? extends Entity>) Class.forName(name);
            }
            catch (ClassNotFoundException e)
            {
                return null;
            }
        return clazz;
    }
    
    protected String testForSelector(Entity e, IEntitySelector selector)
    {
        String s = "UNKNOWN";
        if(testSheep == null)
            testSheep = new EntitySheep(null);
        if(testSpider == null)
            testSpider = new EntitySpider(null);
        if(selector == null)
            return s;
        if(selector.isEntityApplicable(testSheep) && !selector.isEntityApplicable(testSpider))
        {
            s = "Passive animals";
        }
        else if(!selector.isEntityApplicable(testSheep) && selector.isEntityApplicable(testSpider))
        {
            s = "Mobs";
        }
        return s;
    }
}
