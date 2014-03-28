package org.jglrxavpok.mods.customai.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

import org.jglrxavpok.mods.customai.ModCustomAI;
import org.jglrxavpok.mods.customai.json.JSONObject;
import org.lwjgl.opengl.GL11;

public class GuiSpinnerButton extends GuiButton
{


    static final ResourceLocation spinnerTextures = new ResourceLocation(ModCustomAI.MODID, "textures/gui/spinners.png");
    private GuiButton toModify;
    private double value;
    private String valueName;
    private boolean plus;
    private JSONObject data;
    private double min;
    private double max;
    private Class<?> type;
    private double step;

    public GuiSpinnerButton(JSONObject data, String string, GuiButton guiButton, int x, int y, Class<?> type, double step, boolean ascending)
    {
        super(-200,x,y,11,7,string);
        this.data = data;
        this.type = type;
        this.step = step;
        this.toModify = guiButton;
        this.valueName = string;
        this.value = data.getInt(string);
        this.plus = ascending;
        min = Integer.MIN_VALUE;
        max = Integer.MAX_VALUE;
    }
    
    public void drawButton(Minecraft p_146112_1_, int p_146112_2_, int p_146112_3_)
    {
        if (this.visible)
        {
            FontRenderer fontrenderer = p_146112_1_.fontRenderer;
            p_146112_1_.getTextureManager().bindTexture(spinnerTextures);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.field_146123_n = p_146112_2_ >= this.xPosition && p_146112_3_ >= this.yPosition && p_146112_2_ < this.xPosition + this.width && p_146112_3_ < this.yPosition + this.height;
            int k = this.getHoverState(this.field_146123_n);
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            func_146110_a(xPosition, yPosition, plus ? 11 : 0, field_146123_n ? 7 : 0, 11, 7, 22, 14);
            this.mouseDragged(p_146112_1_, p_146112_2_, p_146112_3_);
            int l = 14737632;

            if (packedFGColour != 0)
            {
                l = packedFGColour;
            }
            else if (!this.enabled)
            {
                l = 10526880;
            }
            else if (this.field_146123_n)
            {
                l = 16777120;
            }

//            this.drawCenteredString(fontrenderer, plus ? "A" : "V", this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, l);
        }
    }

    public void handleClicked()
    {
        if(type == Integer.class)
            value = data.getInt(valueName);
        else if(type == Float.class)
            value = (float)data.getDouble(valueName);
        else if(type == Double.class)
            value = data.getDouble(valueName);
        value+=(plus ? step : -step);
        if(value < min)
            value = min;
        if(value > max)
            value = max;
        if(type == Integer.class)
            data.put(valueName, (int)value);
        else if(type == Float.class)
            data.put(valueName, (float)value);
        else if(type == Double.class)
            data.put(valueName, (double)value);
        
        if(toModify != null)
        {
            if(type == Integer.class)
                toModify.displayString = ""+(int)value;
            else if(type == Float.class)
                toModify.displayString = ""+(float)value;
            else if(type == Double.class)
                toModify.displayString = ""+(double)value;
        }
    }

    public GuiSpinnerButton setValueBounds(double min2, double max2)
    {
        this.min = min2;
        this.max = max2;
        return this;
    }

}
