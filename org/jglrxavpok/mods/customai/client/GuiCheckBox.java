package org.jglrxavpok.mods.customai.client;

import org.jglrxavpok.mods.customai.json.JSONObject;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;

public class GuiCheckBox extends GuiButton
{

    private JSONObject data;
    private String key;
    private boolean value;

    public GuiCheckBox(JSONObject entryData, String key, int par2, int par3)
    {
        super(-202, par2, par3, 20,20,"");
        this.data = entryData;
        this.key = key;
        value = data.getBoolean(key);
    }
    
    public void drawButton(Minecraft mc, int x, int y)
    {
        if (this.visible)
        {
            FontRenderer fontrenderer = mc.fontRenderer;
            mc.getTextureManager().bindTexture(GuiAIChoiceButton.newButtonTextures);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.field_146123_n = x >= this.xPosition && y >= this.yPosition && x < this.xPosition + this.width && y < this.yPosition + this.height;
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            
            int k = this.getHoverState(field_146123_n);
            this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 126+(k-1)*20 , this.width, this.height);
            if(value)
            {
                this.drawTexturedModalRect(this.xPosition, this.yPosition, (2)*20, 126, this.width, this.height);   
            }
            this.mouseDragged(mc, x, y);
        }
    }
    
    public void handleClicked()
    {
        value = !value;
        data.put(key, value);
    }

}
