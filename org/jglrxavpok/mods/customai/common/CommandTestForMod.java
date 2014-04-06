package org.jglrxavpok.mods.customai.common;

import java.util.List;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class CommandTestForMod implements ICommand
{

    @Override
    public int compareTo(Object arg0)
    {
        return 0;
    }

    @Override
    public String getCommandName()
    {
        return "testforcustomai";
    }

    @Override
    public String getCommandUsage(ICommandSender var1)
    {
        return "/testforcustomai";
    }

    @Override
    public List getCommandAliases()
    {
        return null;
    }

    @Override
    public void processCommand(ICommandSender var1, String[] var2)
    {
        var1.addChatMessage(new ChatComponentText("The mod is here, don't worry!"));
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender var1)
    {
        return true;
    }

    @Override
    public List addTabCompletionOptions(ICommandSender var1, String[] var2)
    {
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] var1, int var2)
    {
        return false;
    }

}
