package org.jglrxavpok.mods.customai.common;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

import org.jglrxavpok.mods.customai.common.aifactory.EntityAIFactory;
import org.jglrxavpok.mods.customai.json.JSONObject;

public class CustomAIEntityExtendedProperties implements IExtendedEntityProperties
{

    private EntityLiving entity;
    private boolean shouldSave = true;

    @Override
    public void saveNBTData(NBTTagCompound compound)
    {
        if(!shouldSave || entity == null)
        {
            return;
        }
        List<EntityAITaskEntry> targetTasks = CustomAIHelper.getTargetTasksList(entity);
        List<EntityAITaskEntry> tasks = CustomAIHelper.getTasksList(entity);
        NBTTagList targetTasksNBT = new NBTTagList();
        NBTTagList tasksNBT = new NBTTagList();
        for(EntityAITaskEntry entry : targetTasks)
        {
            JSONObject json = CustomAIHelper.generateJSONFromAI((EntityLiving) entity, entry);
            if(json != null)
            targetTasksNBT.appendTag(new NBTTagString(json.toString()));
        }
        for(EntityAITaskEntry entry : tasks)
        {
            JSONObject json = CustomAIHelper.generateJSONFromAI((EntityLiving) entity, entry);
            if(json != null)
            tasksNBT.appendTag(new NBTTagString(json.toString()));
        }
        NBTTagCompound nbt = compound;
        nbt.setTag("CustomAITargetTasks", targetTasksNBT);
        nbt.setTag("CustomAITasks", tasksNBT);
    }

    @Override
    public void loadNBTData(NBTTagCompound compound)
    {
        NBTTagCompound nbt = compound;
        
        NBTTagList targetTasksNBT = (NBTTagList) nbt.getTag("CustomAITargetTasks");
        NBTTagList tasksNBT = (NBTTagList) nbt.getTag("CustomAITasks");
        if(entity != null)
        {
            if(tasksNBT == null)
            {
                ;
            }
            else
            {
                CustomAIHelper.getTasksList(entity).clear();
                for(int i = 0;i<tasksNBT.tagCount();i++)
                {
                    try
                    {
                        EntityAITaskEntry e = EntityAIFactory.instance().generateAIBaseWithExceptions(entity, new JSONObject(tasksNBT.getStringTagAt(i)));
                        if(e != null && e.action != null)
                            CustomAIHelper.getTasksList(entity).add(e);
                    }
                    catch(Exception e)
                    {
                        ;
                    }
                }
            }
            if(targetTasksNBT == null)
            {
                ;
            }
            else
            {
                CustomAIHelper.getTargetTasksList(entity).clear();
                for(int i = 0;i<targetTasksNBT.tagCount();i++)
                {
                    try
                    {
                        EntityAITaskEntry e = CustomAIHelper.generateAIFromJSON(entity, targetTasksNBT.getStringTagAt(i));
                        if(e != null && e.action != null)
                            CustomAIHelper.getTargetTasksList(entity).add(e);
                    }
                    catch(Exception e)
                    {
                        ;
                    }
                }
            }
        }
    }

    @Override
    public void init(Entity entity, World world)
    {
        if(CustomAIHelper.hasEntityAI(entity))
            this.entity = (EntityLiving)entity;
    }
    
    public void setShouldSave(boolean s)
    {
        this.shouldSave = s;
    }

}
