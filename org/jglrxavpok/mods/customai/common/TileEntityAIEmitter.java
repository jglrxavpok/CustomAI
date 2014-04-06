package org.jglrxavpok.mods.customai.common;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import org.jglrxavpok.mods.customai.ModCustomAI;
import org.jglrxavpok.mods.customai.json.JSONObject;
import org.jglrxavpok.mods.customai.netty.PacketAIEStackUpdate;

public class TileEntityAIEmitter extends TileEntity implements IInventory
{

    private ItemStack stack;
    private Random rand;
    private List<JSONObject> tasks;
    private List<JSONObject> targetTasks;
    private int beamColor;
    private int range;
    public static HashMap<Item, Class<? extends Entity>> itemMap = new HashMap<Item, Class<? extends Entity>>();
    public static HashMap<Block, Class<? extends Entity>> blockMap = new HashMap<Block, Class<? extends Entity>>();

    public TileEntityAIEmitter()
    {
        this.range = 40;
        beamColor = 0xFFFFFFFF;
        tasks = new ArrayList<JSONObject>();
        targetTasks = new ArrayList<JSONObject>();
//        tasks.add(new JSONObject("{type:net.minecraft.entity.ai.EntityAIBreakDoor,priority:5}"));
    }
    
    public TileEntityAIEmitter(World world)
    {
        this();
        this.worldObj = world;
        this.rand = new Random();
    }

    @Override
    public int getSizeInventory()
    {
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(int var1)
    {
        return stack;
    }

    @Override
    public ItemStack decrStackSize(int var1, int var2)
    {
        stack.stackSize--;
        return stack;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int var1)
    {
        return stack;
    }
    
    public void setStack(ItemStack s)
    {
        if(this.worldObj.isRemote)
        {
            ModCustomAI.packetPipeline.sendToServer(new PacketAIEStackUpdate(xCoord,yCoord,zCoord,s));
        }
        else
        {
            ModCustomAI.packetPipeline.sendToAll(new PacketAIEStackUpdate(xCoord,yCoord,zCoord,s));
        }
    }

    @Override
    public void setInventorySlotContents(int var1, ItemStack var2)
    {
        stack = var2;
    }

    @Override
    public String getInventoryName()
    {
        return "AI Emitter";
    }

    @Override
    public boolean hasCustomInventoryName()
    {
        return true;
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 1;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer var1)
    {
        return true;
    }
    
    @SuppressWarnings("unchecked")
    public void updateEntity()
    {
        super.updateEntity();
        List<Entity> entities = worldObj.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord+1, yCoord+1, zCoord+1).expand(range, 256, range));
        for(Entity e : entities)
        {
            if(CustomAIHelper.hasEntityAI(e) && isValid(e))
            {
                tryAddAITasks(e, tasks, targetTasks);
            }
        }
    }

    public boolean isValid(Entity e)
    {
        if(stack == null)
            return true;
        Item item = stack.getItem();
        Class<? extends Entity> c = itemMap.get(item);
        if(c == null)
        {
            if(item instanceof ItemBlock)
            {
                ItemBlock itemBlock = (ItemBlock)item;
                c = blockMap.get(itemBlock.field_150939_a);
                if(c == null)
                    return true;
                else
                    return Reflect.isInstanceof(e.getClass(), c);
            }
            return true;
        }
        else 
        {
            return Reflect.isInstanceof(e.getClass(), c);
        }
    }

    private void tryAddAITasks(Entity e, List<JSONObject> tasksAsJSON, List<JSONObject> targetTasksAsJSON)
    {
        List<EntityAITaskEntry> tasks = new ArrayList<EntityAITaskEntry>();
        List<EntityAITaskEntry> targetTasks = new ArrayList<EntityAITaskEntry>();
        for(JSONObject t : tasksAsJSON)
        {
            EntityAITaskEntry entry = CustomAIHelper.generateAIFromJSON((EntityLiving)e, t);
            if(entry != null && entry.action != null)
                tasks.add(entry);
        }
        for(JSONObject t : targetTasksAsJSON)
        {
            EntityAITaskEntry entry = CustomAIHelper.generateAIFromJSON((EntityLiving)e, t);
            if(entry != null && entry.action != null)
                targetTasks.add(entry);
        }
        List<EntityAITaskEntry> entTasks = (List<EntityAITaskEntry>)clone(CustomAIHelper.getTasksList(e));
        List<EntityAITaskEntry> entTargetTasks = (List<EntityAITaskEntry>)clone(CustomAIHelper.getTargetTasksList(e));
        List<EntityAITaskEntry> tasksToRemove = new ArrayList<EntityAITaskEntry>();
        List<EntityAITaskEntry> targetTasksToRemove = new ArrayList<EntityAITaskEntry>();
        for(EntityAITaskEntry entTask : entTasks)
        {
            for(EntityAITaskEntry task : tasks)
            {
                if(areTasksEqual(e,task, entTask))
                    tasksToRemove.add(entTask);
            }
        }
        for(EntityAITaskEntry entTargetTask : entTargetTasks)
        {
            for(EntityAITaskEntry targetTask : targetTasks)
            {
                if(areTasksEqual(e,targetTask, entTargetTask))
                    targetTasksToRemove.add(entTargetTask);
            }
        }
        entTasks.removeAll(tasksToRemove);
        entTargetTasks.removeAll(targetTasksToRemove);
        for(EntityAITaskEntry task : tasks)
        {
            if(task != null && task.action != null)
            {
                if(CustomAIHelper.isSuitableForEntity((EntityLiving) e, task.action.getClass()))
                {
                    entTasks.add(task);
                }
            }
        }
        for(EntityAITaskEntry targetTask : targetTasks)
        {
            if(targetTask != null && targetTask.action != null)
                if(CustomAIHelper.isSuitableForEntity((EntityLiving) e, targetTask.action.getClass()))
                {
                    entTargetTasks.add(targetTask);
                }
        }
        CustomAIHelper.applyLists(e, entTasks, entTargetTasks);
    }

    private List<EntityAITaskEntry> clone(List<EntityAITaskEntry> list)
    {
        ArrayList<EntityAITaskEntry> result = new ArrayList<EntityAITaskEntry>();
        result.addAll(list);
        return result;
    }

    /**
     * Check if two tasks are basically equal. Doesn't care about priority of each task
     * @param e : Entity used to process the instances of JSONObject 
     * @param task : First task of the couple to check
     * @param task2 : Second task of the couple to check
     * @return true if tasks are equal
     */
    @SuppressWarnings("unchecked")
    private boolean areTasksEqual(Entity e, EntityAITaskEntry task, EntityAITaskEntry task2)
    {
        if(task == null || task2 == null || task.action == null || task2.action == null)
            return false;
        JSONObject firstTask = CustomAIHelper.generateJSONFromAI((EntityLiving) e, task);
        JSONObject secondTask = CustomAIHelper.generateJSONFromAI((EntityLiving) e, task2);
        if(firstTask.keySet().size() != secondTask.keySet().size())
            return false;
        else
        {
            if(!firstTask.getString("type").equals(secondTask.getString("type")))
                return false;
            Iterator<String> keys = firstTask.keys();
            while(keys.hasNext())
            {
                String key = keys.next(); 
                if(!key.equals("priority"))
                {
                    try
                    {
                        if(!firstTask.get(key).equals(secondTask.get(key)))
                        {
                            return false;
                        }
                    }
                    catch(Exception exception)
                    {
                        return false;
                    }
                }
            }
            return true;
        }
    }
    
    public int getBeamColor()
    {
        return beamColor;
    }
    
    public void setBeamColor(int color)
    {
        this.beamColor = blend(new Color(beamColor), new Color(0xFF000000|color),0.20f).getRGB();
    }

    public static Color blend( Color c1, Color c2, float ratio ) {
        if ( ratio > 1f ) ratio = 1f;
        else if ( ratio < 0f ) ratio = 0f;
        float iRatio = 1.0f - ratio;

        int i1 = c1.getRGB();
        int i2 = c2.getRGB();

        int a1 = (i1 >> 24 & 0xff);
        int r1 = ((i1 & 0xff0000) >> 16);
        int g1 = ((i1 & 0xff00) >> 8);
        int b1 = (i1 & 0xff);

        int a2 = (i2 >> 24 & 0xff);
        int r2 = ((i2 & 0xff0000) >> 16);
        int g2 = ((i2 & 0xff00) >> 8);
        int b2 = (i2 & 0xff);

        int a = (int)((a1 * iRatio) + (a2 * ratio));
        int r = (int)((r1 * iRatio) + (r2 * ratio));
        int g = (int)((g1 * iRatio) + (g2 * ratio));
        int b = (int)((b1 * iRatio) + (b2 * ratio));

        return new Color( a << 24 | r << 16 | g << 8 | b );
    }
    
    @Override
    public void openInventory()
    {
    }

    @Override
    public void closeInventory()
    {
        
    }

    @Override
    public boolean isItemValidForSlot(int var1, ItemStack var2)
    {
        return var2 != null && var2.getItem() != null && var2.getItem() == ModCustomAI.rewriterItem;
    }

    /**
     * Overriden in a sign to provide the text.
     */
    public Packet getDescriptionPacket()
    {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        this.writeToNBT(nbttagcompound);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 3, nbttagcompound);
    }

    @SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared()
    {
        return 65536.0D;
    }

    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.beamColor = nbt.getInteger("BeamColor");
        NBTTagCompound itemNBT = nbt.getCompoundTag("Item");
        stack = ItemStack.loadItemStackFromNBT(itemNBT);
        NBTTagList targetTasksNBT = (NBTTagList) nbt.getTag("CustomAITargetTasks");
        NBTTagList tasksNBT = (NBTTagList) nbt.getTag("CustomAITasks");
        if(tasksNBT != null)
        {
            int n = tasksNBT.tagCount();
            for(int i = 0;i<n;i++)
            {
                tasks.add(new JSONObject(tasksNBT.getStringTagAt(i)));
            }
        }
        
        if(targetTasksNBT != null)
        {
            int n = targetTasksNBT.tagCount();
            for(int i = 0;i<n;i++)
            {
                targetTasks.add(new JSONObject(targetTasksNBT.getStringTagAt(i)));
            }
        }
    }

    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setInteger("BeamColor", this.beamColor);
        NBTTagCompound itemNBT = new NBTTagCompound();
        if(stack != null)
            stack.writeToNBT(itemNBT);
        nbt.setTag("Item", itemNBT);
        NBTTagList tasksNBT = new NBTTagList();
        NBTTagList targetTasksNBT = new NBTTagList();
        for(int i = 0;i<tasks.size();i++)
        {
            tasksNBT.appendTag(new NBTTagString(tasks.get(i).toString()));
        }
        for(int i = 0;i<targetTasks.size();i++)
        {
            targetTasksNBT.appendTag(new NBTTagString(targetTasks.get(i).toString()));
        }
        nbt.setTag("CustomAITasks", tasksNBT);
        nbt.setTag("CustomAITargetTasks", targetTasksNBT);
    }
    
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
    {
        this.readFromNBT(pkt.func_148857_g());
    }
    
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        AxisAlignedBB bb = INFINITE_EXTENT_AABB;
        return bb;
    }

    public double getRange()
    {
        return range;
    }

    public List<JSONObject> getTasks()
    {
        return tasks;
    }
    
    public List<JSONObject> getTargetTasks()
    {
        return targetTasks;
    }

    public static void registerItems(Class<? extends Entity> class1, Object[] objects)
    {
        for(Object o : objects)
        {
            if(o instanceof Item)
            {
                itemMap.put((Item)o, class1);
            }
            else if(o instanceof Block)
            {
                blockMap.put((Block)o, class1);
            }
        }
    }
}
