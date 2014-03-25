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
        currentScroll += Mouse.getDWheel()/120 * speed;
        correctScrollValue();
        for(int i =0;i<list.length;i++)
        {
            list[i].xPosition = this.xPos;
            list[i].yPosition = this.yPos+currentScroll+i*slotHeight;
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
        int min = (list.length-(height/slotHeight))*this.slotHeight;
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
           if(list[i].mousePressed(mc, mouseX, mouseY))
           {
               button1 = list[i];
           }
        }
        return button1;
    }
}