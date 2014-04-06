package org.jglrxavpok.mods.customai.common;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.jglrxavpok.mods.customai.ModCustomAI;
import org.jglrxavpok.mods.customai.client.ClientProxy;

public class BlockAIEmitter extends BlockContainer
{

    private IIcon topBlock;
    private IIcon bottom;
    private IIcon tinySide;

    public BlockAIEmitter()
    {
        super(Material.glass);
        this.setHardness(3.0F);
        this.setBlockName("ai_emitter");
        this.setCreativeTab(CreativeTabs.tabMisc);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata)
    {
        return new TileEntityAIEmitter(world);
    }

    /**
     * Called upon block activation (right click on the block.)
     */
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_)
    {
        if(player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == Items.dye)
        {
            TileEntityAIEmitter tileAIEmitter = (TileEntityAIEmitter)world.getTileEntity(x, y, z);
            
            if (tileAIEmitter != null)
            {
                tileAIEmitter.setBeamColor(ItemDye.field_150922_c[player.getCurrentEquippedItem().getItemDamage()]);
            }
            return true;
        }
        else
        {
            TileEntityAIEmitter tileAIEmitter = (TileEntityAIEmitter)world.getTileEntity(x, y, z);
            if(player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == ModCustomAI.rewriterItem)
            {
                if (tileAIEmitter != null)
                {
                    player.openGui(ModCustomAI.instance, 1, world, x, y, z);
                }
            }
            else if(player.getCurrentEquippedItem() != null)
            {
                if (tileAIEmitter != null)
                {
                    tileAIEmitter.setStack(player.getCurrentEquippedItem());
                }
            }
            else if(player.getCurrentEquippedItem() == null)
            {
                if (tileAIEmitter != null)
                {
                    tileAIEmitter.setStack(null);;
                }
            }
            return true;
        }
    }

    /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    public boolean isOpaqueCube()
    {
        return false;
    }

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    /**
     * The type of render function that is called for this block
     */
    @SideOnly(Side.CLIENT)
    public int getRenderType()
    {
        return ClientProxy.renderID;
    }
    
    public boolean shouldSideBeRendered(IBlockAccess access, int x, int y, int z, int side)
    {
        return true;
    }
    
    public IIcon getIcon(int side, int metadata)
    {
        return (IIcon) (side == -1 ? tinySide : side == 1 ? this.topBlock : (side == 0 ? bottom : (this.blockIcon)));
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register)
    {
        topBlock = Blocks.glass.getIcon(0, 0);
        bottom = Blocks.lapis_block.getIcon(0, 0);
        blockIcon = register.registerIcon(ModCustomAI.MODID+":ai_emitter_side");
        tinySide = register.registerIcon(ModCustomAI.MODID+":ai_emitter_side2");
    }

    /**
     * Called when the block is placed in the world.
     */
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack)
    {
        super.onBlockPlacedBy(world, x, y, z, entity, stack);

        if (stack.hasDisplayName())
        {
            ((TileEntityBeacon)world.getTileEntity(x, y, z)).func_145999_a(stack.getDisplayName());
        }
    }
}
