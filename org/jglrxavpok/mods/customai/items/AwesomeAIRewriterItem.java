package org.jglrxavpok.mods.customai.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

import org.jglrxavpok.mods.customai.ModCustomAI;

public class AwesomeAIRewriterItem extends Item
{

    public AwesomeAIRewriterItem()
    {
        super();
        this.setUnlocalizedName("ai_rewriter");
        this.setTextureName(ModCustomAI.MODID+":ai_rewriter");
        this.setCreativeTab(CreativeTabs.tabTools);
        this.setMaxStackSize(1);
    }
}
