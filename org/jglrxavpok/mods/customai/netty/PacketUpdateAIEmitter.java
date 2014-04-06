package org.jglrxavpok.mods.customai.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.player.EntityPlayer;

import org.jglrxavpok.mods.customai.ModCustomAI;
import org.jglrxavpok.mods.customai.common.CustomAIHelper;
import org.jglrxavpok.mods.customai.common.TileEntityAIEmitter;
import org.jglrxavpok.mods.customai.json.JSONObject;

public class PacketUpdateAIEmitter extends AbstractPacket
{

    private List<JSONObject> tasks;
    private List<JSONObject> targetTasks;
    private String[] tasksAsJSON;
    private String[] targetTasksAsJSON;
    private int x;
    private int y;
    private int z;

    PacketUpdateAIEmitter()
    {
        tasks = new ArrayList<JSONObject>();
        targetTasks = new ArrayList<JSONObject>();
    }
    
    public PacketUpdateAIEmitter(int xCoord, int yCoord, int zCoord, List<JSONObject> tasks,List<JSONObject> targetTasks)
    {
        this.x = xCoord;
        this.y = yCoord;
        this.z = zCoord;
        this.tasks = tasks;
        this.targetTasks = targetTasks;
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

            outputStream.writeInt(tasks.size());
            for(int i = 0;i<tasks.size();i++)
                outputStream.writeUTF(tasks.get(i).toString());
            
            outputStream.writeInt(targetTasks.size());
            for(int i = 0;i<targetTasks.size();i++)
                outputStream.writeUTF(targetTasks.get(i).toString());
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
            
            int nbr = inputStream.readInt();
            tasksAsJSON = new String[nbr];
            
            for(int i =0;i<nbr;i++)
            {
                tasksAsJSON[i] = inputStream.readUTF();
            }
            nbr = inputStream.readInt();
            targetTasksAsJSON = new String[nbr];
            for(int i =0;i<nbr;i++)
            {
                targetTasksAsJSON[i] = inputStream.readUTF();
            }
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

    @Override
    public void handleServerSide(EntityPlayer player)
    {
        handleBothSides(player);
        ModCustomAI.packetPipeline.sendToAll(this);
    }
    
    private void handleBothSides(EntityPlayer player)
    {
        if(tasksAsJSON == null)
            return;
        for(String task : tasksAsJSON)
        {
            tasks.add(new JSONObject(task));
        }
        for(String targetTask : targetTasksAsJSON)
        {
            targetTasks.add(new JSONObject(targetTask));
        }
        TileEntityAIEmitter tileEntity = (TileEntityAIEmitter) player.worldObj.getTileEntity(x, y, z);
        tileEntity.getTargetTasks().clear();
        tileEntity.getTargetTasks().addAll(targetTasks);
        tileEntity.getTasks().clear();
        tileEntity.getTasks().addAll(tasks);
    }

    public static byte[] compress(byte[] data) throws IOException
    {  
        Deflater deflater = new Deflater();  
        deflater.setLevel(Deflater.BEST_COMPRESSION);
        deflater.setInput(data);  
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);   
            
        deflater.finish();  
        byte[] buffer = new byte[1024];   
        while (!deflater.finished()) {  
            int count = deflater.deflate(buffer); // returns the generated code... index  
            outputStream.write(buffer, 0, count);   
        }  
        outputStream.close();  
        byte[] output = outputStream.toByteArray();  
        return output;
    }
    
    public static byte[] decompress(byte[] data) throws IOException, DataFormatException 
    {  
        Inflater inflater = new Inflater(); 
        inflater.setInput(data);  
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);  
        byte[] buffer = new byte[1024];  
        while (!inflater.finished()) 
        {  
            int count = inflater.inflate(buffer);
            outputStream.write(buffer, 0, count);
        }  
        outputStream.close();  
        byte[] output = outputStream.toByteArray();  
        return output;  
    }  

}
