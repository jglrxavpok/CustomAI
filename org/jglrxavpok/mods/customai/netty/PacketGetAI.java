package org.jglrxavpok.mods.customai.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

import org.jglrxavpok.mods.customai.ModCustomAI;
import org.jglrxavpok.mods.customai.common.CustomAIHelper;

public class PacketGetAI extends AbstractPacket
{

    private int entityID;

    PacketGetAI()
    {} 
    
    public PacketGetAI(int entityId)
    {
        this.entityID = entityId;
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
    {
        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream outputStream = new DataOutputStream(baos);
            outputStream.writeInt(entityID);
            outputStream.flush();
            baos.flush();
            outputStream.close();
            baos.close();
            byte[] data = baos.toByteArray();
            buffer.writeBytes(data);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
    {
        try
        {
            byte[] readBytes = new byte[buffer.readableBytes()];
            buffer.readBytes(readBytes);
            ByteArrayInputStream bais = new ByteArrayInputStream(readBytes);
            DataInputStream inputStream = new DataInputStream(bais);
            entityID = inputStream.readInt();
            inputStream.close();
            bais.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void handleClientSide(EntityPlayer player)
    {}

    @Override
    public void handleServerSide(EntityPlayer player)
    {
        Entity e = player.worldObj.getEntityByID(entityID);
        if(e != null)
        {
            if(CustomAIHelper.hasEntityAI(e))
            {
                NBTTagCompound nbt = new NBTTagCompound();
                e.writeToNBT(nbt);
                ModCustomAI.packetPipeline.sendTo(new PacketUpdateAI(entityID, CustomAIHelper.getTasksList(e), CustomAIHelper.getTargetTasksList(e)), (EntityPlayerMP) player);
            }
        }
    }

}
