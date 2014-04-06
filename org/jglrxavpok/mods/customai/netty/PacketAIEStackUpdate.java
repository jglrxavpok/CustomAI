package org.jglrxavpok.mods.customai.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;

import org.jglrxavpok.mods.customai.ModCustomAI;
import org.jglrxavpok.mods.customai.common.TileEntityAIEmitter;

public class PacketAIEStackUpdate extends AbstractPacket
{

    private int x;
    private int y;
    private int z;
    private ItemStack stack;

    PacketAIEStackUpdate()
    {}
    
    public PacketAIEStackUpdate(int xCoord, int yCoord, int zCoord, ItemStack stack)
    {
        this.x = xCoord;
        this.y = yCoord;
        this.z = zCoord;
        this.stack = stack;
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
    {
        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream outputStream = new DataOutputStream(baos);

            outputStream.writeInt(x);
            outputStream.writeInt(y);
            outputStream.writeInt(z);

            if(stack == null)
            {
                outputStream.writeUTF("null");
            }
            else
            {
                NBTTagCompound nbt = new NBTTagCompound(); 
                stack.writeToNBT(nbt);
                outputStream.writeUTF(nbt.toString());
            }
            
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

            x = inputStream.readInt();
            y = inputStream.readInt();
            z = inputStream.readInt();
            
            String str = inputStream.readUTF();
            if(!str.equals("null"))
                stack = ItemStack.loadItemStackFromNBT((NBTTagCompound) JsonToNBT.func_150315_a(str));
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
    {
        handleBothSides(player);
    }

    private void handleBothSides(EntityPlayer player)
    {
        TileEntityAIEmitter tileEntity = ((TileEntityAIEmitter)player.worldObj.getTileEntity(x, y, z));
        if(tileEntity != null)
            tileEntity.setInventorySlotContents(0, stack);
        else
            System.out.println("Tile Entity null at ("+x+";"+y+";"+z+")");
    }

    @Override
    public void handleServerSide(EntityPlayer player)
    {
        handleBothSides(player);
        ModCustomAI.packetPipeline.sendToAll(this);
    }

}
