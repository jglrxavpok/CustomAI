package org.jglrxavpok.mods.customai.client;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.OpenGlHelper;

import org.jglrxavpok.mods.customai.json.JSONObject;
import org.lwjgl.opengl.GL11;

public class GuiComboBox extends GuiButton implements ActionListener, IPostRenderable
{

    public class GuiComboBoxList extends GuiList
    {
        public GuiComboBoxList(GuiListSlot[] list, int x, int y, int width, int height, int slotHeight)
        {
            super(list, x, y, width, height, slotHeight);
        }
        
        public void actionPerformed(GuiListSlot slot, Minecraft mc, int mx,int my)
        {
            GuiComboBox.this.actionPerformed(mx,my,slot);
        }
    }

    private GuiTextField2 field;
    private String[] values;
    private GuiComboBoxList list;
    private boolean drawList;
    private int selectedIndex;
    private JSONObject data;
    private String dataKey;

    public GuiComboBox(int par1, int par2, int par3, int par4, int par5, String par6Str, String[] values)
    {
        super(par1, par2, par3, par4, par5, par6Str);
        this.field = new GuiTextField2(Minecraft.getMinecraft().fontRenderer,par2,par3,par4,par5);
        field.setText(par6Str);
        this.values = values;
        list = new GuiComboBoxList(GuiList.createSlotsFromArray(values,par1+1),par2,par3+par5,par4,60, 20);
        list.setOutBoundsMethod(1);
        int index = 0;
        for(String v : values)
        {
            if(v.equals(par6Str))
            {
                selectedIndex = index;
                break;
            }
            index++;
        }
        drawList = false;
    }
    
    public void attachJson(JSONObject toAttach, String key)
    {
        this.data = toAttach;
        dataKey = key;
        if(data != null && data.has(key))
        {
            this.field.setText(toAttach.getString(key));
        }
    }
    
    public String getValue()
    {
        if(selectedIndex != -1)
            return values[selectedIndex];
        else
            return null;
    }

    public void drawButton(Minecraft mc, int mx, int my)
    {
        if (this.visible)
        {
//            if(drawList)
//            {
//                list.render(mc, mx, my);
//            }
            ObfuscationReflectionHelper.setPrivateValue(GuiTextField.class, field, xPosition, 1);
            ObfuscationReflectionHelper.setPrivateValue(GuiTextField.class, field, yPosition, 2);
            list.xPos = this.xPosition;
            list.yPos = this.yPosition+height;
            field.drawTextBox();
            mc.getTextureManager().bindTexture(GuiSpinnerButton.spinnerTextures);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.field_146123_n = mx >= this.xPosition && my >= this.yPosition && mx < this.xPosition + this.width && my < this.yPosition + this.height;
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            
            boolean arrowHovered = mx >= this.xPosition +width-12 && mx <= this.xPosition+width && my >= yPosition && my <= yPosition + height;
            func_146110_a(xPosition+width-12, yPosition+height/2-7/2, 0, arrowHovered ? 7 : 0, 11, 7, 22, 14);
            this.mouseDragged(mc, mx, my);
            
            int k = this.getHoverState(this.field_146123_n);
            int l = 14737632;
            
        }
    }

    public void handleClick(int mx, int my, int button)
    {
        boolean arrowHovered = /*mx >= this.xPosition +width-12 && mx <= this.xPosition+width && my >= yPosition && my <= yPosition + height*/true;
        if(arrowHovered)
        {
            field.setFocused(false);
            this.drawList = !drawList;
        }
        else
        {
            field.mouseClicked(mx, my, button);
            field.setFocused(true);
        }
    }
    
    public void actionPerformed(int mx, int my, GuiButton b)
    {
        if(b != null && b != this && b instanceof GuiStringListSlot)
        {
            field.setText(b.displayString);
            if(data != null)
            {
                data.put(dataKey, b.displayString);
            }
            field.setCursorPosition(0);
            this.selectedIndex = b.id-(id+1);
            this.drawList = false;
        }
        else
            field.setFocused(false);
    }

    public GuiButton getSelectedButton(Minecraft mc, int mouseX, int mouseY)
    {
        if(this.mousePressed(mc, mouseX, mouseY))
            return this;
        if(this.drawList)
            return list.handleClick(mc, mouseX, mouseY);
        else
            return null;
    }

    @Override
    public void postRender(Minecraft mc, int mx, int my)
    {
        if(drawList)
        {
            this.list.render(mc, mx, my);
            field.drawTextBox(); // Hide parts of the list
        }
    }

    public void setValue(String value)
    {
        this.actionPerformed(-1,-1,getButtonFromText(value));
    }

    private GuiButton getButtonFromText(String value)
    {
        for(GuiButton b : list.getData())
        {
            if(value.equals(b.displayString))
                return b;
        }
        return null;
    }
    
}
