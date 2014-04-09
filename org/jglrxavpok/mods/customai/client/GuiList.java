package org.jglrxavpok.mods.customai.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class GuiList
{

    private GuiListSlot[] list;
    public int xPos;
    public int yPos;
    private int height;
    private int width;
    private int slotHeight;
    private int currentScroll;
    private int outBoundsMethod;
    private boolean inverted;
    public static int MOUSE_WHEEL = 0;

    public GuiList(GuiListSlot[] list, int x, int y, int width, int height, int slotHeight)
    {
        this.list = list;
        this.xPos = x;
        this.yPos = y;
        this.height = height;
        this.width = width;
        this.slotHeight = slotHeight;
        currentScroll = 0;
        for(GuiListSlot slot : list)
        {
            if(slot == null)
                continue;
            slot.setWidth(width);
            slot.setHeight(slotHeight);
            slot.parent = this;
        }
    }
    
    public void setOutBoundsMethod(int i)
    {
        this.outBoundsMethod = i;
    }
    
    public void render(Minecraft mc, int mouseX, int mouseY)
    {
        int speed = 6;
        if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
            speed = 10;
        if(this.handleClick(mc, mouseX, mouseY) != null)
        {
            MOUSE_WHEEL= Mouse.getDWheel()/120;
        }
        currentScroll += MOUSE_WHEEL * speed;
        correctScrollValue();
        int index = 0;
        for(int i =0;i<list.length;i++)
        {
            if(list[i] == null)
                continue;
            list[i].xPosition = this.xPos;
            list[i].yPosition = this.yPos+currentScroll+(index++)*slotHeight;
            if(isInBounds(list[i],currentScroll))
            {
                list[i].visible = true;
                list[i].drawButton(mc, mouseX, mouseY);
            }
            else
            {
                list[i].visible = false;
            }
        }
    }
    
    private void correctScrollValue()
    {
        if(currentScroll >= 0)
            currentScroll = 0;
        int nbr = 0;
        for(GuiListSlot slot : list)
        {
            if(slot != null)
                nbr++;
        }
        int min = (nbr-(height/slotHeight))*this.slotHeight;
        if(currentScroll <= -min)
        {
            currentScroll = -min;
        }
    }

    public void postRender(Minecraft mc, int mouseX, int mouseY)
    {
        Tessellator tess = Tessellator.instance;
        tess.setColorOpaque_I(0);
        tess.startDrawingQuads();
        tess.setTranslation(0, 0, 0);
        tess.addVertexWithUV(mouseX, mouseY, 0,0,0);
        tess.addVertexWithUV(mouseX+100, mouseY, 0,1,0);
        tess.addVertexWithUV(mouseX+100, mouseY+100, 0,1,1);
        tess.addVertexWithUV(mouseX, mouseY+100, 0,0,1);
        tess.draw();
        tess.setColorOpaque_I(0xFFFFFF);
    }
    
    private boolean isInBounds(GuiListSlot slot, int currentScroll2)
    {
        if(outBoundsMethod == 0)
        {
            if(slot.yPosition+slotHeight <= yPos+height
            && slot.yPosition >= yPos)
                return true;
            return false;
        }
        else if(outBoundsMethod == 1)
        {
            if(slot.yPosition <= yPos+height
                    && slot.yPosition+slotHeight >= yPos)
                        return true;
            return false;
        }
        return true;
    }

    public void setData(GuiListSlot[] data)
    {
        list = data;
    }
    
    public GuiListSlot[] getData()
    {
        return list;
    }

    public GuiButton handleClick(Minecraft mc, int mouseX, int mouseY)
    {
        GuiButton button1 = null;
        for(int i = 0;i<list.length;i++)
        {
           if(list[i] != null)
           if(list[i].mousePressed(mc, mouseX, mouseY))
           {
               button1 = list[i];
           }
        }
        return button1;
    }

    public static GuiListSlot[] createSlotsFromArray(String[] values, int firstIndex)
    {
        GuiListSlot[] slots = new GuiStringListSlot[values.length];
        for(int i = 0;i<slots.length;i++)
        {
            slots[i] = new GuiStringListSlot(firstIndex+i, values[i]);
        }
        return slots;
    }

    public void actionPerformed(GuiListSlot slot, Minecraft mc, int mx, int my)
    {}
}
