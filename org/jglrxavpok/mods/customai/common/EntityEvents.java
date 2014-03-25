package org.jglrxavpok.mods.customai.common;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;

import org.jglrxavpok.mods.customai.ModCustomAI;
import org.jglrxavpok.mods.customai.netty.PacketGetAI;
import org.jglrxavpok.mods.customai.netty.PacketUpdateAI;

public class EntityEvents
{

    @SubscribeEvent
    public void onEntityConstructing(EntityConstructing e)
    {
        if(e.entity instanceof EntityPlayer)
        {
            IExtendedEntityProperties props = e.entity.getExtendedProperties(ModCustomAI.MODID+"_Player");
            if(props == null)
            {
                e.entity.registerExtendedProperties(ModCustomAI.MODID, new CustomAIPlayerExtendedProperties());
            }
        }
        if(CustomAIHelper.hasEntityAI(e.entity))
        {
            IExtendedEntityProperties props = e.entity.getExtendedProperties(ModCustomAI.MODID+"_Entity");
            if(props == null)
            {
                e.entity.registerExtendedProperties(ModCustomAI.MODID, new CustomAIEntityExtendedProperties());
            }
        }
    }
    
    @SubscribeEvent
    public void onEntityInteraction(EntityInteractEvent e)
    {
        ItemStack stack = e.entityPlayer.getCurrentEquippedItem();
        if(stack != null && stack.getItem() == ModCustomAI.rewriterItem)
        {
            e.setCanceled(true);
            CustomAIPlayerExtendedProperties props = (CustomAIPlayerExtendedProperties)e.entityPlayer.getExtendedProperties(ModCustomAI.MODID);
            if(props == null)
            {
                e.entityPlayer.registerExtendedProperties(ModCustomAI.MODID, new CustomAIPlayerExtendedProperties());
            }
            props.setEntityInteractingWith(e.target.getEntityId(), e.target.getClass());
            FMLNetworkHandler.openGui(e.entityPlayer, ModCustomAI.instance, 0, e.entityPlayer.worldObj, (int)Math.floor(e.entityPlayer.posX), (int)Math.floor(e.entityPlayer.posY), (int)Math.floor(e.entityPlayer.posZ));
        }
    }
    
    @SubscribeEvent
    public void onEntitySpawn(EntityJoinWorldEvent e)
    {
        if(e.world.isRemote)
        {
            ModCustomAI.packetPipeline.sendToServer(new PacketGetAI(e.entity.getEntityId()));
        }
    }
}
