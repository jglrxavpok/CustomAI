package org.jglrxavpok.mods.customai.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;

import org.lwjgl.opengl.GL11;

public class GuiLabel extends GuiButton
{

    public GuiLabel(int x, int y, String par4Str)
    {
        super(-1, x,y, 0,0,par4Str);
    }
    
    public void drawButton(Minecraft p_146112_1_, int p_146112_2_, int p_146112_3_)
    {
        if (this.visible)
        {
            FontRenderer fontrenderer = p_146112_1_.fontRenderer;
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.field_146123_n = p_146112_2_ >= this.xPosition && p_146112_3_ >= this.yPosition && p_146112_2_ < this.xPosition + this.width && p_146112_3_ < this.yPosition + this.height;
            int l = 14737632;

            if (packedFGColour != 0)
            {
                l = packedFGColour;
            }

            this.drawString(fontrenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, l);
        }
    }

    public boolean mousePressed(Minecraft mc, int x, int y)
    {
        return false;
    }
}
