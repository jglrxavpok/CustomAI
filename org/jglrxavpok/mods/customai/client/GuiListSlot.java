package org.jglrxavpok.mods.customai.client;

import net.minecraft.client.gui.GuiButton;

public class GuiListSlot extends GuiButton
{

    public GuiList parent;

    public GuiListSlot(int par1)
    {
        super(par1, 0,0,Math.random()+"");
    }

    public void setWidth(int width)
    {
        this.width = width;
    }

    public void setHeight(int height)
    {
        this.height = height;
    }
}
