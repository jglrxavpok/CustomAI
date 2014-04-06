package org.jglrxavpok.mods.customai.client;

import org.jglrxavpok.mods.customai.ModCustomAI;

public class GuiAddToListSlot extends GuiListSlot
{

    public GuiAddToListSlot(int par1)
    {
        super(par1);
        this.displayString =ModCustomAI.getTranslation("ai.addNewTask");
    }

}
