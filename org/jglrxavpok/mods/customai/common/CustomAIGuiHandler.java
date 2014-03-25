package org.jglrxavpok.mods.customai.common;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

import org.jglrxavpok.mods.customai.ModCustomAI;
import org.jglrxavpok.mods.customai.client.GuiCustomAI;

public class CustomAIGuiHandler implements IGuiHandler
{

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        if(ID == 0)
        {
            CustomAIPlayerExtendedProperties props = (CustomAIPlayerExtendedProperties) player.getExtendedProperties(ModCustomAI.MODID);
            if(props == null)
            {
                System.err.println("Server: props are null!");
                return null;
            }
            else
            {
                if(CustomAIHelper.hasEntityAI(world.getEntityByID(props.getEntityClickedID())))
                {
                }
                else
                    player.addChatMessage(new ChatComponentText("§4This mob doesn't support AI tasks. Sorry :("));
            }
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        if(ID == 0)
        {
            CustomAIPlayerExtendedProperties props = (CustomAIPlayerExtendedProperties) player.getExtendedProperties(ModCustomAI.MODID);
            if(props == null)
            {
                System.err.println("Client: props are null!");
                return null;
            }
            else
            {
                if(CustomAIHelper.hasEntityAI(world.getEntityByID(props.getEntityClickedID())))
                {
                    return new GuiCustomAI(player, world, world.getEntityByID(props.getEntityClickedID()));
                }
            }
        }
        return null;
    }

}
