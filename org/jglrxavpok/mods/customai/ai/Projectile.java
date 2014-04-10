package org.jglrxavpok.mods.customai.ai;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;

public abstract class Projectile
{

    private String id;
    private final static HashMap<String, Projectile> projectileRegistry = new HashMap<String, Projectile>();

    protected Projectile(String id)
    {
        this.id = id;
    }
    
    public String getID()
    {
        return id;
    }
    
    public abstract void attackEntityWithRangedAttack(EntityLiving user, EntityLivingBase par1EntityLivingBase, float par2);

    public static void register(String id, Class<? extends Projectile> c)
    {
        try
        {
            Constructor<? extends Projectile> cons = c.getConstructor(new Class<?>[]{String.class});
            cons.setAccessible(true);
            projectileRegistry.put(id, cons.newInstance(new Object[]{id}));
        }
        catch (InstantiationException e)
        {
            e.printStackTrace();
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
        catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        }
        catch (SecurityException e)
        {
            e.printStackTrace();
        }
    }

    public static Projectile fromString(String string)
    {
        return projectileRegistry.get(string);
    }

}
