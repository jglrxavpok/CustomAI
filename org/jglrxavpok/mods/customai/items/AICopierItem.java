package org.jglrxavpok.mods.customai.items;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.command.server.CommandSummon;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import org.jglrxavpok.mods.customai.ModCustomAI;
import org.jglrxavpok.mods.customai.common.CustomAIHelper;
import org.jglrxavpok.mods.customai.common.TileEntityAIEmitter;
import org.jglrxavpok.mods.customai.common.aifactory.EntityAIFactory;
import org.jglrxavpok.mods.customai.json.JSONObject;
import org.jglrxavpok.mods.customai.netty.PacketUpdateAI;
import org.jglrxavpok.mods.customai.netty.PacketUpdateAIEmitter;

public class AICopierItem extends Item
{

    private IIcon emptyIcon;
    private IIcon usedIcon;

    public AICopierItem()
    {
        this.setUnlocalizedName("ai_copier");
        setCreativeTab(CreativeTabs.tabTools);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean flag)
    {
        if(stack.getTagCompound() == null || !stack.getTagCompound().hasKey("CustomAITasks"))
        {
            list.add(EnumChatFormatting.ITALIC+"Empty");
            return;
        }
        NBTTagCompound nbt = stack.getTagCompound();
        NBTTagList targetTasksNBT = (NBTTagList) nbt.getTag("CustomAITargetTasks");
        NBTTagList tasksNBT = (NBTTagList) nbt.getTag("CustomAITasks");
        ArrayList<String> tasks = new ArrayList<String>();
        ArrayList<String> targetTasks = new ArrayList<String>();
        if(tasksNBT == null)
        {
            ;
        }
        else
        {
            for(int i = 0;i<tasksNBT.tagCount();i++)
            {
                try
                {
                    JSONObject o = new JSONObject(tasksNBT.getStringTagAt(i));
                    if(o.has("type"))
                    {
                        tasks.add(CustomAIHelper.getNameFromClass((Class<? extends EntityAIBase>) Class.forName(o.getString("type"))));
                    }
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
            for(int i = 0;i<targetTasksNBT.tagCount();i++)
            {
                try
                {
                    JSONObject o = new JSONObject(targetTasksNBT.getStringTagAt(i));
                    if(o.has("type"))
                    {
                        targetTasks.add(CustomAIHelper.getNameFromClass((Class<? extends EntityAIBase>) Class.forName(o.getString("type"))));
                    }
                }
                catch(Exception e)
                {
                    ;
                }
            }
        }
        
        list.add((flag ? EnumChatFormatting.BOLD : "")+"Tasks: "+tasks.size());
        if(flag)
        {
            for(String task : tasks)
            {
                list.add("   -"+EnumChatFormatting.ITALIC+task);
            }
        }
        list.add((flag ? EnumChatFormatting.BOLD : "")+"Target Tasks: "+targetTasks.size());
        if(flag)
        {
            for(String task : targetTasks)
            {
                list.add("   -"+EnumChatFormatting.ITALIC+task);
            }
        }
    }
    
    private void applyFromStackToEntity(ItemStack stack, Entity entity, EntityPlayer entityPlayer)
    {
        NBTTagCompound nbt = stack.getTagCompound();
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
                        EntityAITaskEntry e = EntityAIFactory.instance().generateAIBaseWithExceptions((EntityLiving) entity, new JSONObject(tasksNBT.getStringTagAt(i)));
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
        entityPlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.ITALIC+"Successfully transfered AI data to the entity"));
        
        ModCustomAI.packetPipeline.sendToAll(new PacketUpdateAI(entity.getEntityId(), CustomAIHelper.getTasksList(entity), CustomAIHelper.getTargetTasksList(entity)));
    }
    
    private void copyFromCommandBlock(NBTTagCompound nbt, ItemStack stack, TileEntityCommandBlock commandBlock, EntityPlayer player)
    {
        NBTTagCompound tmp = new NBTTagCompound();
        commandBlock.writeToNBT(tmp);
        String command = tmp.getString("Command");
        while(!command.isEmpty() && command.startsWith(" "))
            command = command.substring(0);
        String[] args = command.split(" ");
        if(!args[0].startsWith("summon") && !args[0].startsWith("/summon"))
        {
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED+"The command provided isn't a summon command!"));
            return;
        }
        if(args.length >= 5)
        {
            try
            {
                IChatComponent ichatcomponent = CommandSummon.func_147178_a(commandBlock.func_145993_a(), args, 5);
    
                NBTBase nbtbase = JsonToNBT.func_150315_a(ichatcomponent.getUnformattedText());
                if(!(nbtbase instanceof NBTTagCompound))
                {
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED+"Can't parse NBT data!"));
                    return;
                }
                NBTTagCompound compound = (NBTTagCompound)nbtbase;
                NBTTagList tasksList = compound.hasKey("CustomAITasks") ? (NBTTagList) compound.getTag("CustomAITasks") : null;
                NBTTagList targetTasksList = compound.hasKey("CustomAITargetTasks") ?  (NBTTagList) compound.getTag("CustomAITargetTasks") : null;
                
                if(tasksList != null)
                    nbt.setTag("CustomAITasks", tasksList);
                if(targetTasksList != null)
                    nbt.setTag("CustomAITargetTasks", targetTasksList);
                
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.ITALIC+"Successfully transfered AI data from the command block"));
                stack.setItemDamage(1);
            }
            catch(Exception e)
            {
                ;
            }
        }
        else
        {
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED+"Failed to copy from command block: no AI tasks to fetch"));
        }
    }
    
    private void copyFromEmitter(NBTTagCompound nbt, ItemStack stack, TileEntityAIEmitter aiEmitter, EntityPlayer player)
    {
        NBTTagList tasksListNBT = new NBTTagList();
        NBTTagList targetTasksListNBT = new NBTTagList();
        for(JSONObject o : aiEmitter.getTasks())
        {
            tasksListNBT.appendTag(new NBTTagString(o.toString()));
        }
        for(JSONObject o : aiEmitter.getTargetTasks())
        {
            targetTasksListNBT.appendTag(new NBTTagString(o.toString()));
        }
        nbt.setTag("CustomAITasks", tasksListNBT);
        nbt.setTag("CustomAITargetTasks", targetTasksListNBT);
        stack.setItemDamage(1);
        
        player.addChatMessage(new ChatComponentText(EnumChatFormatting.ITALIC+"Successfully transfered AI data from the emitter"));
    }

    private void copyFromEntityToStack(ItemStack stack, Entity entity, EntityPlayer entityPlayer)
    {
        stack.setItemDamage(1);
        NBTTagCompound nbt = stack.getTagCompound();
        if(nbt == null)
        {
            nbt = new NBTTagCompound();
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
        nbt.setTag("CustomAITargetTasks", targetTasksNBT);
        nbt.setTag("CustomAITasks", tasksNBT);
        
        stack.setTagCompound(nbt);
        
        entityPlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.ITALIC+"Successfully copied AI data from the entity"));
    }

    private void copyFromSpawner(NBTTagCompound nbt, ItemStack stack, TileEntityMobSpawner mobSpawner, EntityPlayer player)
    {
        NBTTagCompound tmp = new NBTTagCompound();
        mobSpawner.writeToNBT(tmp);
        if(tmp.hasKey("SpawnData"))
        {
            NBTTagCompound data = tmp.getCompoundTag("SpawnData");
            if(data.hasKey("CustomAITasks"))
            {
                nbt.setTag("CustomAITasks", data.getTag("CustomAITasks"));
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.ITALIC+"Successfully transfered AI data from the spawner"));
            }
            else
            {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED+"No spawn data"));
            }
            if(data.hasKey("CustomAITargetTasks"))
            {
                nbt.setTag("CustomAITargetTasks", data.getTag("CustomAITargetTasks"));
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.ITALIC+"Successfully transfered AI data from the spawner"));
            }
            else
            {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED+"No spawn data"));
            }
        }
        else
        {
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED+"No spawn data"));
        }
        stack.setItemDamage(1);
    }

    private void copyToCommandBlock(NBTTagCompound nbt, ItemStack stack, TileEntityCommandBlock commandBlock, EntityPlayer player)
    {
        player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED+"ddd"));
        NBTTagCompound tmp = new NBTTagCompound();
        commandBlock.writeToNBT(tmp);
        String oldCommand = tmp.getString("Command");
        while(!oldCommand.isEmpty() && oldCommand.startsWith(" "))
            oldCommand = oldCommand.substring(0);
        String[] args = oldCommand.split(" ");
        if(!args[0].startsWith("summon") && !args[0].startsWith("/summon"))
        {
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED+"The command provided isn't a summon command!"));
            return;
        }
        if(args.length >= 6)
        {
            try
            {
                IChatComponent ichatcomponent = CommandSummon.func_147178_a(commandBlock.func_145993_a(), args, 5);
    
                NBTBase nbtbase = JsonToNBT.func_150315_a(ichatcomponent.getUnformattedText());
                if(!(nbtbase instanceof NBTTagCompound))
                {
                    player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED+"Can't parse NBT data!"));
                    return;
                }
                NBTTagCompound compound = (NBTTagCompound)nbtbase;
                NBTTagList tasksList = (NBTTagList) nbt.getTag("CustomAITasks");
                NBTTagList targetTasksList = (NBTTagList) nbt.getTag("CustomAITargetTasks");
                if(tasksList != null)
                {
                    compound.setTag("CustomAITasks", nbtStringListToCompoundList(tasksList));
                }
                if(targetTasksList != null)
                {
                    compound.setTag("CustomAITargetTasks", nbtStringListToCompoundList(targetTasksList));
                }
                String newCommand= oldCommand.replace(ichatcomponent.getUnformattedText(), compound.toString());
                commandBlock.func_145993_a().func_145752_a(newCommand);
                commandBlock.func_145993_a().func_145756_e();

                player.addChatMessage(new ChatComponentText(EnumChatFormatting.ITALIC+"Successfully transfered AI data to the command block"));
            }
            catch(Exception e)
            {
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED+"Error: "+e.getMessage()));
                ;
            }
        }
        else if(args.length >= 4)
        {
            NBTTagCompound compound = new NBTTagCompound();
            NBTTagList tasksList = (NBTTagList) nbt.getTag("CustomAITasks");
            NBTTagList targetTasksList = (NBTTagList) nbt.getTag("CustomAITargetTasks");
            if(tasksList != null)
            {
                compound.setTag("CustomAITasks", nbtStringListToCompoundList(tasksList));
            }
            if(targetTasksList != null)
            {
                compound.setTag("CustomAITargetTasks", nbtStringListToCompoundList(targetTasksList));
            }
            
            String newCommand = oldCommand+" "+compound.toString();
            commandBlock.func_145993_a().func_145752_a(newCommand);
            commandBlock.func_145993_a().func_145756_e();

            player.addChatMessage(new ChatComponentText(EnumChatFormatting.ITALIC+"Successfully transfered AI data to the command block"));

        }
    }
    
    private void copyToEmitter(NBTTagCompound nbt, ItemStack stack, TileEntityAIEmitter aiEmitter, EntityPlayer player)
    {
        NBTTagList targetTasksNBT = (NBTTagList) nbt.getTag("CustomAITargetTasks");
        NBTTagList tasksNBT = (NBTTagList) nbt.getTag("CustomAITasks");
        List<JSONObject> tasks = aiEmitter.getTasks();
        List<JSONObject> targetTasks = aiEmitter.getTasks();
        tasks.clear();
        targetTasks.clear();
        if(tasksNBT != null)
        for(int index = 0;index<tasksNBT.tagCount();index++)
        {
            tasks.add(new JSONObject(tasksNBT.getStringTagAt(index)));
        }
        if(targetTasksNBT != null)
        for(int index = 0;index<targetTasksNBT.tagCount();index++)
        {
            targetTasks.add(new JSONObject(targetTasksNBT.getStringTagAt(index)));
        }
        ModCustomAI.packetPipeline.sendToAll(new PacketUpdateAIEmitter(aiEmitter.xCoord, aiEmitter.yCoord, aiEmitter.zCoord, tasks, targetTasks));
        player.addChatMessage(new ChatComponentText(EnumChatFormatting.ITALIC+"Successfully transfered AI data to the emitter"));
    }
    
    private void copyToSpawner(NBTTagCompound nbt, ItemStack stack, TileEntityMobSpawner mobSpawner, EntityPlayer player)
    {
        if(mobSpawner.func_145881_a().getRandomEntity() == null)
        {
            mobSpawner.func_145881_a().setRandomEntity(mobSpawner.func_145881_a().new WeightedRandomMinecart((NBTTagCompound) nbt.copy(), mobSpawner.func_145881_a().getEntityNameToSpawn()));
        }
        else
        {
            NBTTagCompound old = mobSpawner.func_145881_a().getRandomEntity().field_98222_b;
            if(nbt.hasKey("CustomAITasks"))
                old.setTag("CustomAITasks", nbt.getTag("CustomAITasks"));
            if(nbt.hasKey("CustomAITargetTasks"))
                old.setTag("CustomAITargetTasks", nbt.getTag("CustomAITargetTasks"));
        }
        player.addChatMessage(new ChatComponentText(EnumChatFormatting.ITALIC+"Successfully transfered AI data to the spawner"));
//        ModCustomAI.packetPipeline.sendToAll(new PacketUpdateMobSpawnerData(mobSpawner, fromNBTListToList((NBTTagList)nbt.getTag("CustomAITasks")), fromNBTListToList((NBTTagList)nbt.getTag("CustomAITargetTasks"))));
        mobSpawner.getWorldObj().markBlockForUpdate(mobSpawner.xCoord, mobSpawner.yCoord, mobSpawner.zCoord);
    }
    
    public IIcon getIcon(ItemStack stack, int pass)
    {
        if(stack.getTagCompound() == null)
        {
            stack.setTagCompound(new NBTTagCompound());
            return stack.getItemDamage() == 1 ? usedIcon : emptyIcon;
        }
        else
        {
            if(stack.getTagCompound().hasKey("CustomAITasks"))
            {
                return usedIcon;
            }
        }
        return stack.getItemDamage() == 1 ? usedIcon : emptyIcon;
    }

    public IIcon getIconFromDamage(int damage)
    {
        return damage == 1 ? usedIcon : emptyIcon;
    }

    @SuppressWarnings("unchecked")
    private NBTTagList nbtStringListToCompoundList(NBTTagList list)
    {
        NBTTagList result = new NBTTagList();
        for(int i = 0;i<list.tagCount();i++)
        {
            String s = list.getStringTagAt(i);
            NBTTagCompound compound = new NBTTagCompound();
            JSONObject object = new JSONObject(s);
            Iterator<String> keys = object.keys();
            while(keys.hasNext())
            {
                String key = keys.next();
                compound.setString(key, ""+object.get(key));
            }
            result.appendTag(compound);
        }
        return result;
    }
    
    public boolean onInteract(World w, ItemStack stack, EntityPlayer entityPlayer, Entity target)
    {
        entityPlayer.swingItem();
        if(w.isRemote)
            return CustomAIHelper.hasEntityAI(target);
        if(CustomAIHelper.hasEntityAI(target))
        {
            if(stack.getItemDamage() == 0)
            {
                copyFromEntityToStack(stack, target, entityPlayer);
            }
            else
            {
                if(entityPlayer.isSneaking())
                {
                    copyFromEntityToStack(stack, target, entityPlayer);
                }
                else
                {
                    applyFromStackToEntity(stack, target, entityPlayer);
                }
            }
            return true;
        }
        else
        {
            entityPlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED+"This entity doesn't use the new AI system. You can't change "+EnumChatFormatting.RED+"anything, sorry :("));
            return false;
        }
    }

    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World w, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        if(w.isRemote)
            return false;
        Block block = w.getBlock(x, y, z);
        NBTTagCompound nbt = stack.getTagCompound();
        if(nbt == null)
        {
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }
        if(block == ModCustomAI.aiEmitterBlock)
        {
            TileEntityAIEmitter aiEmitter = (TileEntityAIEmitter) w.getTileEntity(x, y, z);
            if(aiEmitter == null)
                return true;
            if(nbt.hasKey("CustomAITasks"))
            {
                if(player.isSneaking())
                {
                    copyFromEmitter(nbt, stack, aiEmitter, player);
                }
                else
                {
                    copyToEmitter(nbt, stack, aiEmitter, player);
                }
            }
            else
            {
                copyFromEmitter(nbt, stack, aiEmitter, player);
            }
            return true;
        }
        else if(block == Blocks.mob_spawner)
        {
            TileEntityMobSpawner mobSpawner = (TileEntityMobSpawner) w.getTileEntity(x, y, z);
            if(mobSpawner == null)
                return true;
            if(nbt.hasKey("CustomAITasks"))
            {
                if(player.isSneaking())
                {
                    copyFromSpawner(nbt, stack, mobSpawner, player);
                }
                else
                {
                    copyToSpawner(nbt, stack, mobSpawner, player);
                }
            }
            else
            {
                copyFromSpawner(nbt, stack, mobSpawner, player);
            }
            return true;
        }
        else if(block == Blocks.command_block)
        {
            TileEntityCommandBlock commandBlock = (TileEntityCommandBlock) w.getTileEntity(x, y, z);
            
            if(commandBlock == null)
                return true;
            if(nbt.hasKey("CustomAITasks"))
            {
                if(player.isSneaking())
                {
                    copyFromCommandBlock(nbt, stack, commandBlock, player);
                }
                else
                {
                    copyToCommandBlock(nbt, stack, commandBlock, player);
                }
            }
            else
            {
                copyFromCommandBlock(nbt, stack, commandBlock, player);
            }
            return true;
        }
        return true;
    }

    public void registerIcons(IIconRegister register)
    {
        emptyIcon = register.registerIcon(ModCustomAI.MODID+":copier_empty");
        usedIcon = register.registerIcon(ModCustomAI.MODID+":copier_used");
    }
}
