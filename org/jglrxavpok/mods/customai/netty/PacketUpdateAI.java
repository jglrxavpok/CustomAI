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

public class PacketUpdateAI extends AbstractPacket
{

    private int entityID;
    private List<EntityAITaskEntry> tasks;
    private List<EntityAITaskEntry> targetTasks;
    private String[] tasksAsJSON;
    private String[] targetTasksAsJSON;

    PacketUpdateAI()
    {}
    
    public PacketUpdateAI(int entityID, List<EntityAITaskEntry> tasks,List<EntityAITaskEntry> targetTasks)
    {
        this.entityID = entityID;
        this.tasks = tasks;
        this.targetTasks = targetTasks;
    }
    
    @Override
    public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer)
    {
        EntityAITaskEntry[] tasksEntries = tasks.toArray(new EntityAITaskEntry[0]);
        EntityAITaskEntry[] targetTasksEntries = targetTasks.toArray(new EntityAITaskEntry[0]);
        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream outputStream = new DataOutputStream(baos);

            outputStream.writeInt(entityID);

            outputStream.writeInt(tasksEntries.length);
            for(int i = 0;i<tasksEntries.length;i++)
                outputStream.writeUTF(CustomAIHelper.generateJSONFromAI(null, tasksEntries[i]).toString());
            
            outputStream.writeInt(targetTasksEntries.length);
            for(int i = 0;i<targetTasksEntries.length;i++)
                outputStream.writeUTF(CustomAIHelper.generateJSONFromAI(null, targetTasksEntries[i]).toString());
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
        tasks = new ArrayList<EntityAITaskEntry>();
        targetTasks = new ArrayList<EntityAITaskEntry>();
        for(String taskAsJSON : tasksAsJSON)
        {
            EntityAITaskEntry entry = CustomAIHelper.generateAIFromJSON(player.worldObj.getEntityByID(entityID), taskAsJSON);
            if(entry != null)
                tasks.add(entry);
        }
        for(String targetTaskAsJSON : targetTasksAsJSON)
        {
            EntityAITaskEntry entry = CustomAIHelper.generateAIFromJSON(player.worldObj.getEntityByID(entityID), targetTaskAsJSON);
            if(entry != null)
                targetTasks.add(entry);
        }
        CustomAIHelper.applyLists(player.worldObj.getEntityByID(entityID), this.tasks, this.targetTasks);
    }

    @Override
    public void handleServerSide(EntityPlayer player)
    {
        tasks = new ArrayList<EntityAITaskEntry>();
        targetTasks = new ArrayList<EntityAITaskEntry>();
        if(tasksAsJSON != null)
        {
            for(String taskAsJSON : tasksAsJSON)
            {
                EntityAITaskEntry entry = CustomAIHelper.generateAIFromJSON(player.worldObj.getEntityByID(entityID), taskAsJSON);
                if(entry != null)
                    tasks.add(entry);
            }
            for(String targetTaskAsJSON : targetTasksAsJSON)
            {
                EntityAITaskEntry entry = CustomAIHelper.generateAIFromJSON(player.worldObj.getEntityByID(entityID), targetTaskAsJSON);
                if(entry != null)
                    targetTasks.add(entry);
            }
            CustomAIHelper.applyLists(player.worldObj.getEntityByID(entityID), this.tasks, this.targetTasks);
            ModCustomAI.packetPipeline.sendToAll(this);
        }
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
