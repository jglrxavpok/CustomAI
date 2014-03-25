package org.jglrxavpok.mods.customai.client;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;

import org.jglrxavpok.mods.customai.ModCustomAI;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector4f;

public class GuiAIChoiceButton extends GuiButton
{

    protected static final ResourceLocation newButtonTextures = new ResourceLocation(ModCustomAI.MODID,"textures/gui/buttons.png");
    private int listSize;
    private ArrayList<Vector4f> numberVectors;
    private ArrayList<Float> numberSpeeds;
    private Random rand;

    public GuiAIChoiceButton(int par1, int par2, int par3, int par4, int par5, String par6Str)
    {
        super(par1, par2, par3, par4, par5, par6Str);
        numberVectors = new ArrayList<Vector4f>();
        numberSpeeds = new ArrayList<Float>();
        rand = new Random();
    }
    
    public void drawButton(Minecraft mc, int x, int y)
    {
        if (this.visible)
        {
            FontRenderer fontrenderer = mc.fontRenderer;
            mc.getTextureManager().bindTexture(newButtonTextures);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.field_146123_n = x >= this.xPosition && y >= this.yPosition && x < this.xPosition + this.width && y < this.yPosition + this.height;
            int k = this.getHoverState(this.field_146123_n);
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 66 + (k-1) * 30, this.width / 2, this.height);
            this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 66 + (k-1) * 30, this.width / 2, this.height);
            this.mouseDragged(mc, x, y);
            int l = 14737632;

            boolean drawNumbers = false;
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
                drawNumbers = true;
            }

            if(drawNumbers)
            {
                updateNumbersPositions();
                renderNumbers(fontrenderer);
            }
            this.drawCenteredString(fontrenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, l);
            this.drawString(fontrenderer, ""+this.listSize, this.xPosition + this.width -fontrenderer.getStringWidth(""+listSize)-10, this.yPosition + (this.height - 8) / 2, l);
            
        }
    }

    private void renderNumbers(FontRenderer f)
    {
        for(Vector4f vector : numberVectors)
        {
            f.drawString(""+(char)vector.z, (int)Math.floor(vector.x+xPosition), (int)Math.floor(vector.y+yPosition), (int) vector.w);
        }
    }

    private void updateNumbersPositions()
    {
        for(int i = 0;i<numberVectors.size();i++)
        {
            Vector4f vector = numberVectors.get(i);
            vector.y+=numberSpeeds.get(i);
        }
        
        for(int i= 0 ;i<numberVectors.size();i++)
        {
            if(numberVectors.get(i).y > height-8)
            {
                numberVectors.remove(i);
                numberSpeeds.remove(i);
            }
        }
        if(numberVectors.size() < 15 || rand.nextBoolean())
        {
            int colorR = rand.nextInt(0x20);
            int colorG = rand.nextInt(0xFF);
            int colorB = rand.nextInt(0x20);
            // 0x007A00
            int color = (colorR << 16) | (colorG << 8) | (colorB << 0);
            numberVectors.add(new Vector4f(rand.nextInt(width-8), 0, (rand.nextBoolean() ? rand.nextInt('9'-'0')+'0' : (rand.nextBoolean() ? rand.nextInt('Z'-'A')+'A' : rand.nextInt('z'-'a')+'a')), color));
            numberSpeeds.add(rand.nextFloat()*1f+0.50f);
        }
    }

    public void setListSize(int size)
    {
        this.listSize = size;
    }
    

}
