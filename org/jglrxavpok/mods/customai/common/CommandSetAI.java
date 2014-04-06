package org.jglrxavpok.mods.customai.common;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

/**
 * TODO: Finish it
 * @author jglrxavpok
 *
 */
public class CommandSetAI implements ICommand
{

    private List<String> emptyList =new ArrayList<String>();

    @Override
    public int compareTo(Object arg0)
    {
        return 0;
    }

    @Override
    public String getCommandName()
    {
        return "setai";
    }

    @Override
    public String getCommandUsage(ICommandSender var1)
    {
        return "setai <Selector> <TagToApply> [SelectorTag]";
    }

    @Override
    public List<String> getCommandAliases()
    {
        return emptyList ;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void processCommand(ICommandSender var1, String[] var2)
    {
        if(var2.length < 2)
        {
            String s = "Error: Too few arguments, correct usage:";
            if(var1 instanceof Entity)
                var1.addChatMessage(new ChatComponentText(EnumChatFormatting.RED+""+EnumChatFormatting.BOLD+s));
            else
                var1.addChatMessage(new ChatComponentText(s));
            
            s = "/"+this.getCommandUsage(var1);
            if(var1 instanceof Entity)
                var1.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA+s));
            else
                var1.addChatMessage(new ChatComponentText(s));
        }
        else
        {
            String s = "Set AI - Warning: WIP feature, might not work";
            if(var1 instanceof Entity)
            {
                var1.addChatMessage(new ChatComponentText(EnumChatFormatting.RED+""+EnumChatFormatting.BOLD+s));
            }
            else
            {
                var1.addChatMessage(new ChatComponentText(s));
            }
            String[] array = getCorrectArray(var2);
            String selector = array[0];
            if(selector.startsWith("@"))
            {
                if(selector.charAt(1) == 'e')
                {
                    List<Entity> e = var1.getEntityWorld().getLoadedEntityList();
                    if(var1 instanceof Entity && !e.contains(var1))
                        e.add((Entity)var1);
                    if(selector.contains("[") && selector.contains("]"))
                    {
//                        try
                        {
                            String parts[] = selector.substring(selector.indexOf("[")+1,selector.lastIndexOf("]")).split(","); // between []
                            var1.addChatMessage(new ChatComponentText(selector));
                            
                            double x = var1.getPlayerCoordinates().posX;
                            double y = var1.getPlayerCoordinates().posY;
                            double z = var1.getPlayerCoordinates().posZ;
                            if(var1 instanceof Entity)
                            {
                                x = ((Entity)var1).posX;
                                y = ((Entity)var1).posY;
                                z = ((Entity)var1).posZ;
                            }
                            
                            double r = Double.POSITIVE_INFINITY;
                            double rm = 0;
                            String type = "All";
                            int nbrWithoutKey = 0;
                            String name = null;
                            for(String criteria : parts)
                            {
                                if(criteria.length() == 0)
                                    continue;
                                if(criteria.contains("="))
                                {
                                    String key = criteria.split("=")[0];
                                    String value = criteria.split("=")[1];
                                    if(key.equalsIgnoreCase("x"))
                                    {
                                        x= Double.parseDouble(value);
                                    }
                                    else if(key.equalsIgnoreCase("y"))
                                    {
                                        y = Double.parseDouble(value);
                                    }
                                    else if(key.equalsIgnoreCase("z"))
                                    {
                                        z = Double.parseDouble(value);
                                    }
                                    else if(key.equalsIgnoreCase("r"))
                                    {
                                        r = Double.parseDouble(value);
                                    }
                                    else if(key.equalsIgnoreCase("rm"))
                                    {
                                        rm = Double.parseDouble(value);
                                    }
                                    else if(key.equalsIgnoreCase("type"))
                                    {
                                        type = value;
                                        System.err.println("Type is now "+value);
                                    }
                                    else if(key.equalsIgnoreCase("n"))
                                    {
                                        name = value;
                                    }
                                }
                                else
                                {
                                    int tmp = nbrWithoutKey & 3;
                                    if(tmp == 0)
                                        x = Double.parseDouble(criteria);
                                    else if(tmp == 1)
                                        y = Double.parseDouble(criteria);
                                    else if(tmp == 2)
                                        z = Double.parseDouble(criteria);
                                    else if(tmp == 3)
                                        r = Double.parseDouble(criteria);
                                    nbrWithoutKey++;   
                                }
                            }
                            e = var1.getEntityWorld().getEntitiesWithinAABBExcludingEntity(null, AxisAlignedBB.getBoundingBox(x, y, z, 1, 1, 1).expand(r, r, r));
                            ArrayList<Entity> toRemove = new ArrayList<Entity>();
                            for(Entity ent : e)
                            {
                                if(type != null && !type.equals("All"))
                                {
                                    boolean ofType = true;
                                    String t = type;
                                    if(type.startsWith("!"))
                                    {
                                        ofType = false;
                                        t = t.replaceFirst("!", "");
                                    }
                                    Class<? extends Entity> clazz = (Class<? extends Entity>) EntityList.stringToClassMapping.get(t);
                                    if(t.equals("Player"))
                                        clazz = EntityPlayer.class;
                                    if(!Reflect.isInstanceof(ent, clazz) == ofType)
                                    {
                                        toRemove.add(ent);
                                    }
                                }
                                
                                if(name != null)
                                {
                                    boolean notOfName = false;
                                    String n = name;
                                    if(name.startsWith("!"))
                                    {
                                        notOfName = true;
                                        n = n.substring(1);
                                    }
                                    if(ent instanceof EntityLiving)
                                    {
                                        if(((EntityLiving)ent).hasCustomNameTag())
                                        {
                                            if(((EntityLiving)ent).getCustomNameTag().equals(n) == notOfName)
                                            {
                                                toRemove.add(ent);
                                            }
                                        }
                                        else if(!notOfName)
                                            toRemove.add(ent);
                                    }
                                    else if(ent instanceof EntityPlayer)
                                    {
                                        if(((EntityPlayer)ent).getDisplayName().equals(n) == notOfName)
                                        {
                                            toRemove.add(ent);
                                        }
                                    }
                                    else if(!notOfName)
                                        toRemove.add(ent);
                                }
                                
                                double dist = ent.getDistance(x, y, z);
                                if(dist < rm || dist > r)
                                {
                                    toRemove.add(ent);
                                }
                                
                            }
                            e.removeAll(toRemove);
                            toRemove.clear();
                        }
                    }
                    
                    String list = "";
                    int index = 0;
                    for(Entity ent : e)
                    {
                        if(index != 0 && index != e.size()-1)
                            list+=", ";
                        else if(index != 0 && index == e.size()-1)
                            list+=" and ";
                        if(ent instanceof EntityLiving)
                        {
                            if(((EntityLiving)ent).hasCustomNameTag())
                            {
                                list+=((EntityLiving)ent).getCustomNameTag();
                            }
                            else
                                list+=ent.getCommandSenderName();
                        }
                        else if(ent instanceof EntityPlayer)
                        {
                            list+=((EntityPlayer)ent).getDisplayName();
                        }
                        else
                            list+=ent.getCommandSenderName();
                        index++;
                    }
                    var1.addChatMessage(new ChatComponentText("Found "+list));
                }
                else
                {
                    throw new CommandException("Error: Wrong selector: "+selector);
                }
            }
            else
            {
                throw new CommandException("Error: You can't change a player's AI!");
            }
        }
    }

    /**
     * If a string is present (eg. "The lazy dog"), it won't be split
     * @param var2
     * @return
     */
    private String[] getCorrectArray(String[] var2)
    {
        String string = new String();
        int index = 0;
        for(String s : var2)
        {
            string+=(index == 0 ? "" : " ")+s;
            index++;
        }
        boolean isInString = false;
        boolean isInQuote = false;
        ArrayList<String> parts = new ArrayList<String>();
        String current = new String();
        int squareBrackets = 0;
        int brackets = 0;
        for(int i = 0;i<string.length();i++)
        {
            char c = string.charAt(i);
            if(c == ' ')
            {
                if(!isInString && !isInQuote && brackets == 0 && squareBrackets == 0)
                {
                    parts.add(current);
                    current = new String();
                    continue;
                }
            }
            if(c == '[')
            {
                squareBrackets++;
            }
            else if(c == ']')
            {
                squareBrackets--;
            }
            else if(c == '(')
            {
                brackets++;
            }
            else if(c == ')')
            {
                brackets--;
            }
            else if(c == '"')
            {
                isInString=!isInString;
            }
            else if(c == '\'')
            {
                isInQuote=!isInQuote;
            }
            current=current+c;
        }
        parts.add(current);
        return parts.toArray(new String[0]);
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender var1)
    {
        return var1.canCommandSenderUseCommand(getPermissionLevel(), getCommandName());
    }

    private int getPermissionLevel()
    {
        return 2;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender var1, String[] var2)
    {
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] var1, int var2)
    {
        return false;
    }

}
