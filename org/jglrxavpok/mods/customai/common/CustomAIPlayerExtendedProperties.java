package org.jglrxavpok.mods.customai.common;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class CustomAIPlayerExtendedProperties implements IExtendedEntityProperties
{

    public static final String INTERACTING_WITH_CLASS_NAME = "interactingFullClassName";
    public static final String INTERACTING_WITH_ENTITY_ID = "interactingEntityID";
    private int entityInteractingWithID = Integer.MIN_VALUE;
    private Class<? extends Entity> entityInteractingWithClass;

    @Override
    public void saveNBTData(NBTTagCompound nbt)
    {
        if(entityInteractingWithID != Integer.MIN_VALUE && entityInteractingWithClass != null)
        {
            nbt.setString(INTERACTING_WITH_CLASS_NAME, entityInteractingWithClass.getCanonicalName());
            nbt.setInteger(INTERACTING_WITH_ENTITY_ID, entityInteractingWithID);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void loadNBTData(NBTTagCompound nbt)
    {
        String className = nbt.getString(INTERACTING_WITH_CLASS_NAME);
        int id = nbt.getInteger(INTERACTING_WITH_ENTITY_ID);
        if(className != null && !className.trim().equals(""))
        {
            try
            {
                this.entityInteractingWithClass = (Class<? extends Entity>) Class.forName(className);
                this.entityInteractingWithID = id;
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void init(Entity entity, World world)
    {
    }

    public void setEntityInteractingWith(int entityId, Class<? extends Entity> class1)
    {
        this.entityInteractingWithID = entityId;
        this.entityInteractingWithClass = class1;
    }

    public String getEntityClickedClassName()
    {
        if(entityInteractingWithClass == null)
            return null;
        return entityInteractingWithClass.getCanonicalName();
    }
    
    public int getEntityClickedID()
    {
        return entityInteractingWithID;
    }

}
