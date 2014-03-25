package org.jglrxavpok.mods.customai.items;

import org.jglrxavpok.mods.customai.ModCustomAI;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class AwesomeAIRewriterItem extends Item
{

    public AwesomeAIRewriterItem()
    {
        super();
        this.setUnlocalizedName("ai_rewriter");
        this.setTextureName("ai_rewriter");
        this.setCreativeTab(CreativeTabs.tabTools);
        this.setMaxStackSize(1);
    }
    
    public void registerIcons(IIconRegister register)
    {
        this.itemIcon = register.registerIcon(ModCustomAI.MODID+":ai_rewriter");
    }
}
