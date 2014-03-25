package org.jglrxavpok.mods.customai.client;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

public class GuiTextField2 extends GuiTextField
{

    private String valueName;

    public GuiTextField2(FontRenderer par1FontRenderer, int par2, int par3,
            int par4, int par5)
    {
        super(par1FontRenderer, par2, par3, par4, par5);
    }

    public void addToX(float f)
    {
        int value = ObfuscationReflectionHelper.getPrivateValue(GuiTextField.class, this,  1); 
        ObfuscationReflectionHelper.setPrivateValue(GuiTextField.class, this, (int)(value+f), 1);
    }

    public void setValueName(String key)
    {
        valueName = key;
    }

    public String getValueName()
    {
        return valueName;
    }
}
