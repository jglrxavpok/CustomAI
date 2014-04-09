package org.jglrxavpok.mods.customai.client;

import java.awt.Color;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.jglrxavpok.mods.customai.ModCustomAI;
import org.jglrxavpok.mods.customai.common.CustomAIHelper;
import org.jglrxavpok.mods.customai.common.TileEntityAIEmitter;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

public class AIEmitterRenderer extends TileEntitySpecialRenderer implements IInventoryRenderer
{

    private static final ResourceLocation field_147523_b = new ResourceLocation("textures/entity/beacon_beam.png");
    private static Random random = new Random();
    private EntityItem entityItem = new EntityItem(null);
    private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");

    private ResourceLocation waveTexture = new ResourceLocation(ModCustomAI.MODID,"textures/misc/waves.png");
    
    public AIEmitterRenderer()
    {
    }
    
    @SuppressWarnings("unchecked")
    public void renderTileEntityAt(TileEntityAIEmitter tileEntity, double x, double y, double z, float partialTick)
    {
        float f1 = 1;//tileEntity.func_146002_i();
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
        boolean drawBeaconLight = true;
        GL11.glColor4f(1,1,1,1);
        
        block :{
            
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_CULL_FACE);

            int beamColor = (tileEntity == null ? 0xFFFFFFFF : tileEntity.getBeamColor());
            float beamColorA = ((beamColor >> 24) & 0xFF)/255f;
            float beamColorR = ((beamColor >> 16) & 0xFF)/255f;
            float beamColorG = ((beamColor >> 8) & 0xFF)/255f;
            float beamColorB = ((beamColor >> 0) & 0xFF)/255f;
            Tessellator tess = Tessellator.instance;
            bindTexture(TextureMap.locationBlocksTexture);
            GL11.glColor4f(beamColorR, beamColorG, beamColorB, 1);
            IIcon icon = Blocks.lapis_block.getIcon(0, 0);
            double yoffset = 1d/16d*4;
            tess.startDrawingQuads();
            tess.addVertexWithUV(x, y+yoffset, z, icon.getMinU(), icon.getMinV());
            tess.addVertexWithUV(x+1, y+yoffset, z, icon.getMaxU(), icon.getMinV());
            tess.addVertexWithUV(x+1, y+yoffset, z+1, icon.getMaxU(), icon.getMaxV());
            tess.addVertexWithUV(x, y+yoffset, z+1, icon.getMinU(), icon.getMaxV());
            tess.draw();
            
            World world = (tileEntity == null ? null : tileEntity.getWorldObj());
            icon = ModCustomAI.aiEmitterBlock.getIcon(0, 0);
            tess.startDrawingQuads();
            tess.addVertexWithUV(x, y+0.0001, z, icon.getMinU(), icon.getMinV());
            tess.addVertexWithUV(x+1, y+0.0001, z, icon.getMaxU(), icon.getMinV());
            tess.addVertexWithUV(x+1, y+0.0001, z+1, icon.getMaxU(), icon.getMaxV());
            tess.addVertexWithUV(x, y+0.0001, z+1, icon.getMinU(), icon.getMaxV());
            tess.draw();

            if(world == null || !(world.getBlock(tileEntity.xCoord, tileEntity.yCoord+1, tileEntity.zCoord) == Blocks.glass || Blocks.stained_glass == world.getBlock(tileEntity.xCoord, tileEntity.yCoord+1, tileEntity.zCoord)))
            {
                icon = ModCustomAI.aiEmitterBlock.getIcon(1, 0);
                tess.startDrawingQuads();
                tess.addVertexWithUV(x, y+1-0.0001, z, icon.getMinU(), icon.getMinV());
                tess.addVertexWithUV(x+1, y+1-0.0001, z, icon.getMaxU(), icon.getMinV());
                tess.addVertexWithUV(x+1, y+1-0.0001, z+1, icon.getMaxU(), icon.getMaxV());
                tess.addVertexWithUV(x, y+1-0.0001, z+1, icon.getMinU(), icon.getMaxV());
                tess.draw();
            }
            if(world == null || !(world.getBlock(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord-1) == Blocks.glass || Blocks.stained_glass == world.getBlock(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord-1) || world.getBlock(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord-1) == ModCustomAI.aiEmitterBlock))
            {
                icon = ModCustomAI.aiEmitterBlock.getIcon(4, 0);
            }
            else
            {
                icon = ModCustomAI.aiEmitterBlock.getIcon(-1, 0);
            }
            tess.startDrawingQuads();
            tess.addVertexWithUV(x, y+1-0.0001, z+0.001, icon.getMinU(), icon.getMinV());
            tess.addVertexWithUV(x+1, y+1-0.0001, z+0.001, icon.getMaxU(), icon.getMinV());
            tess.addVertexWithUV(x+1, y+0.0001, z+0.001, icon.getMaxU(), icon.getMaxV());
            tess.addVertexWithUV(x, y+0.0001, z+0.001, icon.getMinU(), icon.getMaxV());
            tess.draw();
            
            if(world == null || !(world.getBlock(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord+1) == Blocks.glass || Blocks.stained_glass == world.getBlock(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord+1) || world.getBlock(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord+1) == ModCustomAI.aiEmitterBlock))
            {
                icon = ModCustomAI.aiEmitterBlock.getIcon(4, 0);
            }
            else
            {
                icon = ModCustomAI.aiEmitterBlock.getIcon(-1, 0);
            }
            tess.startDrawingQuads();
            tess.addVertexWithUV(x, y+1-0.0001, z+1-0.001, icon.getMinU(), icon.getMinV());
            tess.addVertexWithUV(x+1, y+1-0.0001, z+1-0.001, icon.getMaxU(), icon.getMinV());
            tess.addVertexWithUV(x+1, y+0.0001, z+1-0.001, icon.getMaxU(), icon.getMaxV());
            tess.addVertexWithUV(x, y+0.0001, z+1-0.001, icon.getMinU(), icon.getMaxV());
            tess.draw();
            
                
            if(world == null || !(world.getBlock(tileEntity.xCoord+1, tileEntity.yCoord, tileEntity.zCoord) == Blocks.glass || Blocks.stained_glass == world.getBlock(tileEntity.xCoord+1, tileEntity.yCoord, tileEntity.zCoord) || world.getBlock(tileEntity.xCoord+1, tileEntity.yCoord, tileEntity.zCoord) == ModCustomAI.aiEmitterBlock))
            {
                icon = ModCustomAI.aiEmitterBlock.getIcon(4, 0);
            }
            else
            {
                icon = ModCustomAI.aiEmitterBlock.getIcon(-1, 0);
            }
            tess.startDrawingQuads();
            tess.addVertexWithUV(x+1-0.001, y+1-0.0001, z+1, icon.getMinU(), icon.getMinV());
            tess.addVertexWithUV(x+1-0.001, y+1-0.0001, z, icon.getMaxU(), icon.getMinV());
            tess.addVertexWithUV(x+1-0.001, y+0.0001, z, icon.getMaxU(), icon.getMaxV());
            tess.addVertexWithUV(x+1-0.001, y+0.0001, z+1, icon.getMinU(), icon.getMaxV());
            tess.draw();

            if(world == null || !(world.getBlock(tileEntity.xCoord-1, tileEntity.yCoord, tileEntity.zCoord) == Blocks.glass || Blocks.stained_glass == world.getBlock(tileEntity.xCoord-1, tileEntity.yCoord, tileEntity.zCoord) || world.getBlock(tileEntity.xCoord-1, tileEntity.yCoord, tileEntity.zCoord) == ModCustomAI.aiEmitterBlock))
            {
                icon = ModCustomAI.aiEmitterBlock.getIcon(4, 0);
            }
            else
            {
                icon = ModCustomAI.aiEmitterBlock.getIcon(-1, 0);
            }
            tess.startDrawingQuads();
            tess.addVertexWithUV(x+0.001, y+1-0.0001, z+1-0.0001, icon.getMinU(), icon.getMinV());
            tess.addVertexWithUV(x+0.001, y+1-0.0001, z+0.0001, icon.getMaxU(), icon.getMinV());
            tess.addVertexWithUV(x+0.001, y+0.0001, z+0.0001, icon.getMaxU(), icon.getMaxV());
            tess.addVertexWithUV(x+0.001, y+0.0001, z+1-0.0001, icon.getMinU(), icon.getMaxV());
            tess.draw();
            
            GL11.glColor4f(1,1,1,1);
        }
        
        if(tileEntity != null)
        {
            int beamColor = tileEntity.getBeamColor();
            if(tileEntity.getWorldObj().getBlock(tileEntity.xCoord, tileEntity.yCoord+1, tileEntity.zCoord) == Blocks.stained_glass)
            {
                beamColor = TileEntityAIEmitter.blend(new Color(beamColor), new Color(0xFF000000|ItemDye.field_150922_c[15-(tileEntity.getWorldObj().getBlockMetadata(tileEntity.xCoord, tileEntity.yCoord+1, tileEntity.zCoord))]), 0.5f).getRGB();
            }
            
            if(!tileEntity.getWorldObj().canBlockSeeTheSky(tileEntity.xCoord, tileEntity.yCoord+1, tileEntity.zCoord))
            {
                beamColor = 0x00FFFFFF;
                drawBeaconLight = false;
            }
                 
            float beamColorA = ((beamColor >> 24) & 0xFF)/255f;
            float beamColorR = ((beamColor >> 16) & 0xFF)/255f;
            float beamColorG = ((beamColor >> 8) & 0xFF)/255f;
            float beamColorB = ((beamColor >> 0) & 0xFF)/255f;
    
            if (f1 > 0.0F && drawBeaconLight)
            {
                float f2 = (float)tileEntity.getWorldObj().getTotalWorldTime() + partialTick;
    
                Tessellator tessellator = Tessellator.instance;
//                this.bindTexture(new ResourceLocation("textures/entity/enderdragon/dragon_exploding.png"));
                bindTexture(field_147523_b);

                GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, 10497.0F);
                GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, 10497.0F);
                GL11.glDepthMask(true);
                OpenGlHelper.glBlendFunc(770, 1, 1, 0);
                float f3 = -f2 * 0.2F - (float)MathHelper.floor_float(-f2 * 0.1F);
                byte b0 = 1;
                double d3 = (double)f2 * 0.025D * (1.0D - (double)(b0 & 1) * 2.5D);
                
                GL11.glEnable(GL11.GL_BLEND);
                OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                GL11.glDepthMask(false);

                tessellator.startDrawingQuads();
                
                tessellator.setColorRGBA_F(beamColorR, beamColorG, beamColorB, 120f/256f);
                double d5 = (double)b0 * 0.2D;
                double d7 = 0.5D + Math.cos(d3 + 2.356194490192345D) * d5;
                double d9 = 0.5D + Math.sin(d3 + 2.356194490192345D) * d5;
                double d11 = 0.5D + Math.cos(d3 + (Math.PI / 4D)) * d5;
                double d13 = 0.5D + Math.sin(d3 + (Math.PI / 4D)) * d5;
                double d15 = 0.5D + Math.cos(d3 + 3.9269908169872414D) * d5;
                double d17 = 0.5D + Math.sin(d3 + 3.9269908169872414D) * d5;
                double d19 = 0.5D + Math.cos(d3 + 5.497787143782138D) * d5;
                double d21 = 0.5D + Math.sin(d3 + 5.497787143782138D) * d5;
                double d23 = (double)(256.0F * f1);
                double d25 = 0.0D;
                double d27 = 1.0D;
                double d28 = (double)(-1.0F + f3);
                double d29 = (double)(256.0F * f1) * (0.5D / d5) + d28;
                tessellator.addVertexWithUV(x + d7, y + d23, z + d9, d27, d29);
                tessellator.addVertexWithUV(x + d7, y, z + d9, d27, d28);
                tessellator.addVertexWithUV(x + d11, y, z + d13, d25, d28);
                tessellator.addVertexWithUV(x + d11, y + d23, z + d13, d25, d29);
                tessellator.addVertexWithUV(x + d19, y + d23, z + d21, d27, d29);
                tessellator.addVertexWithUV(x + d19, y, z + d21, d27, d28);
                tessellator.addVertexWithUV(x + d15, y, z + d17, d25, d28);
                tessellator.addVertexWithUV(x + d15, y + d23, z + d17, d25, d29);
                tessellator.addVertexWithUV(x + d11, y + d23, z + d13, d27, d29);
                tessellator.addVertexWithUV(x + d11, y, z + d13, d27, d28);
                tessellator.addVertexWithUV(x + d19, y, z + d21, d25, d28);
                tessellator.addVertexWithUV(x + d19, y + d23, z + d21, d25, d29);
                tessellator.addVertexWithUV(x + d15, y + d23, z + d17, d27, d29);
                tessellator.addVertexWithUV(x + d15, y, z + d17, d27, d28);
                tessellator.addVertexWithUV(x + d7, y, z + d9, d25, d28);
                tessellator.addVertexWithUV(x + d7, y + d23, z + d9, d25, d29);
                tessellator.draw();
                
                tessellator.startDrawingQuads();
                tessellator.setColorRGBA(255, 255, 255, 32);
                tessellator.setColorRGBA_F(beamColorR, beamColorG, beamColorB, 45f/256f);
                double d30 = 0.2D;
                double d4 = 0.2D;
                double d6 = 0.8D;
                double d8 = 0.2D;
                double d10 = 0.2D;
                double d12 = 0.8D;
                double d14 = 0.8D;
                double d16 = 0.8D;
                double d18 = (double)(256.0F * f1);
                double d20 = 0.0D;
                double d22 = 1.0D;
                double d24 = (double)(-1.0F + f3);
                double d26 = (double)(256.0F * f1) + d24;
                tessellator.addVertexWithUV(x + d30, y + d18, z + d4, d22, d26);
                tessellator.addVertexWithUV(x + d30, y, z + d4, d22, d24);
                tessellator.addVertexWithUV(x + d6, y, z + d8, d20, d24);
                tessellator.addVertexWithUV(x + d6, y + d18, z + d8, d20, d26);
                tessellator.addVertexWithUV(x + d14, y + d18, z + d16, d22, d26);
                tessellator.addVertexWithUV(x + d14, y, z + d16, d22, d24);
                tessellator.addVertexWithUV(x + d10, y, z + d12, d20, d24);
                tessellator.addVertexWithUV(x + d10, y + d18, z + d12, d20, d26);
                tessellator.addVertexWithUV(x + d6, y + d18, z + d8, d22, d26);
                tessellator.addVertexWithUV(x + d6, y, z + d8, d22, d24);
                tessellator.addVertexWithUV(x + d14, y, z + d16, d20, d24);
                tessellator.addVertexWithUV(x + d14, y + d18, z + d16, d20, d26);
                tessellator.addVertexWithUV(x + d10, y + d18, z + d12, d22, d26);
                tessellator.addVertexWithUV(x + d10, y, z + d12, d22, d24);
                tessellator.addVertexWithUV(x + d30, y, z + d4, d20, d24);
                tessellator.addVertexWithUV(x + d30, y + d18, z + d4, d20, d26);
                tessellator.draw();
                
                
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glDepthMask(true);
                
                double dx = -(tileEntity.xCoord - x);
                double dy = -(tileEntity.yCoord - y);
                double dz = -(tileEntity.zCoord - z);
//                bindTexture(field_147523_b);
                GL11.glDisable(GL11.GL_BLEND);

                double range = tileEntity.getRange();
                List<Entity> entities = tileEntity.getWorldObj().getEntitiesWithinAABBExcludingEntity(null, AxisAlignedBB.getBoundingBox(tileEntity.xCoord,tileEntity.yCoord,tileEntity.zCoord,tileEntity.xCoord+1,tileEntity.yCoord+1,tileEntity.zCoord+1).expand(range, 256, range));
                if(entities.size() > 0)
                {
                    this.bindTexture(waveTexture);
                    tessellator.startDrawingQuads();
                    tessellator.setColorRGBA_F(beamColorR, beamColorG, beamColorB, beamColorA);
                    for(int i = 0;i<entities.size();i++)
                    {
                        Entity e = entities.get(i);
                        if(!CustomAIHelper.hasEntityAI(e) || !tileEntity.isValid(e))
                            continue;
                        drawBeautifulCurve(e, tileEntity, dx, dy, dz, x,y,z, partialTick);
                    }
                    tessellator.draw();
                }
                GL11.glEnable(GL11.GL_LIGHTING);
            }
            

            if(tileEntity.getStackInSlot(0) != null)
            {
                GL11.glPushMatrix();
                GL11.glTranslated(x+0.5, y+0.55, z+0.5);
                double f = 0.55;
                GL11.glScaled(f, f, f);
                GL11.glTranslated(0, -0.25, 0);
                GL11.glTranslated(0, Math.sin((tileEntity.getWorldObj().getWorldTime()+partialTick)/4)*0.1, 0);
                GL11.glRotated(entityItem.rotationYaw, 0, 1, 0);
                entityItem.rotationYaw+=random.nextFloat()*(random.nextFloat()*1f);
                entityItem.setEntityItemStack(tileEntity.getStackInSlot(0));
                Tessellator tess = Tessellator.instance;
                tess.setColorRGBA_F(beamColorR, beamColorG, beamColorB, beamColorA);
                if(entityItem.getEntityItem().getItem().requiresMultipleRenderPasses())
                {
                    for(int i = 0;i<entityItem.getEntityItem().getItem().getRenderPasses(entityItem.getEntityItem().getItemDamage());i++)
                        renderDroppedItem(entityItem,entityItem.getEntityItem().getItem().getIcon(entityItem.getEntityItem(),i),1,0,beamColorR,beamColorG,beamColorB,0);
                }
                else
                    renderDroppedItem(entityItem,entityItem.getEntityItem().getItem().getIcon(entityItem.getEntityItem(),0),1,0,beamColorR,beamColorG,beamColorB,0);
                GL11.glPopMatrix();
            }
        }
        GL11.glColor4f(1, 1,1,1);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_LIGHTING);
    }
    
    private void renderDroppedItem(EntityItem par1EntityItem, IIcon par2Icon, int par3, float par4, float par5, float par6, float par7, int pass)
    {
        Tessellator tessellator = Tessellator.instance;
        if (par2Icon == null)
        {
            TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
            ResourceLocation resourcelocation = texturemanager.getResourceLocation(par1EntityItem.getEntityItem().getItemSpriteNumber());
            par2Icon = ((TextureMap)texturemanager.getTexture(resourcelocation)).getAtlasSprite("missingno");
        }

        float f14 = ((IIcon)par2Icon).getMinU();
        float f15 = ((IIcon)par2Icon).getMaxU();
        float f4 = ((IIcon)par2Icon).getMinV();
        float f5 = ((IIcon)par2Icon).getMaxV();
        float f6 = 1.0F;
        float f7 = 0.5F;
        float f8 = 0.25F;
        float f10;

        if (Minecraft.getMinecraft().gameSettings.fancyGraphics)
        {
            GL11.glPushMatrix();

            GL11.glRotatef((((float)par1EntityItem.age + par4) / 20.0F + par1EntityItem.hoverStart) * (180F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
            float f9 = 0.0625F;
            f10 = 0.021875F;
            ItemStack itemstack = par1EntityItem.getEntityItem();
            int j = itemstack.stackSize;
            byte b0;

            if (j < 2)
            {
                b0 = 1;
            }
            else if (j < 16)
            {
                b0 = 2;
            }
            else if (j < 32)
            {
                b0 = 3;
            }
            else
            {
                b0 = 4;
            }

            b0 = 1;

            GL11.glTranslatef(-f7, -f8, -((f9 + f10) * (float)b0 / 2.0F));

            for (int k = 0; k < b0; ++k)
            {
                // Makes items offset when in 3D, like when in 2D, looks much better. Considered a vanilla bug...
                if (k > 0)
                {
                    float x = (random.nextFloat() * 2.0F - 1.0F) * 0.3F / 0.5F;
                    float y = (random.nextFloat() * 2.0F - 1.0F) * 0.3F / 0.5F;
                    float z = (random.nextFloat() * 2.0F - 1.0F) * 0.3F / 0.5F;
                    GL11.glTranslatef(x, y, f9 + f10);
                }
                else
                {
                    GL11.glTranslatef(0f, 0f, f9 + f10);
                }

                if (itemstack.getItemSpriteNumber() == 0)
                {
                    this.bindTexture(TextureMap.locationBlocksTexture);
                }
                else
                {
                    this.bindTexture(TextureMap.locationItemsTexture);
                }

                GL11.glColor4f(par5, par6, par7, 1.0F);
                ItemRenderer.renderItemIn2D(tessellator, f15, f4, f14, f5, ((IIcon)par2Icon).getIconWidth(), ((IIcon)par2Icon).getIconHeight(), f9);

                if (itemstack.hasEffect(pass))
                {
                    GL11.glDepthFunc(GL11.GL_EQUAL);
                    GL11.glDisable(GL11.GL_LIGHTING);
                    Minecraft.getMinecraft().renderEngine.bindTexture(RES_ITEM_GLINT);
                    GL11.glEnable(GL11.GL_BLEND);
                    GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
                    float f11 = 0.76F;
                    GL11.glColor4f(0.5F * f11, 0.25F * f11, 0.8F * f11, 1.0F);
                    GL11.glMatrixMode(GL11.GL_TEXTURE);
                    GL11.glPushMatrix();
                    float f12 = 0.125F;
                    GL11.glScalef(f12, f12, f12);
                    float f13 = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F * 8.0F;
                    GL11.glTranslatef(f13, 0.0F, 0.0F);
                    GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
                    ItemRenderer.renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 255, 255, f9);
                    GL11.glPopMatrix();
                    GL11.glPushMatrix();
                    GL11.glScalef(f12, f12, f12);
                    f13 = (float)(Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
                    GL11.glTranslatef(-f13, 0.0F, 0.0F);
                    GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
                    ItemRenderer.renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 255, 255, f9);
                    GL11.glPopMatrix();
                    GL11.glMatrixMode(GL11.GL_MODELVIEW);
                    GL11.glDisable(GL11.GL_BLEND);
                    GL11.glEnable(GL11.GL_LIGHTING);
                    GL11.glDepthFunc(GL11.GL_LEQUAL);
                }
            }

            GL11.glPopMatrix();
        }
        else
        {
            
            for (int l = 0; l < par3; ++l)
            {
                GL11.glPushMatrix();

                if (l > 0)
                {
                    f10 = (AIEmitterRenderer.random.nextFloat() * 2.0F - 1.0F) * 0.3F;
                    float f17 = (AIEmitterRenderer.random.nextFloat() * 2.0F - 1.0F) * 0.3F;
                    float f16 = (AIEmitterRenderer.random.nextFloat() * 2.0F - 1.0F) * 0.3F;
                    GL11.glTranslatef(f10, f17, f16);
                }

                if (!false)
                {
                    GL11.glRotatef(180.0F - RenderManager.instance.playerViewY, 0.0F, 1.0F, 0.0F);
                }
                
                GL11.glColor4f(par5, par6, par7, 1.0F);
                tessellator.startDrawingQuads();
                tessellator.setNormal(0.0F, 1.0F, 0.0F);
                tessellator.addVertexWithUV((double)(0.0F - f7), (double)(0.0F - f8), 0.0D, (double)f14, (double)f5);
                tessellator.addVertexWithUV((double)(f6 - f7), (double)(0.0F - f8), 0.0D, (double)f15, (double)f5);
                tessellator.addVertexWithUV((double)(f6 - f7), (double)(1.0F - f8), 0.0D, (double)f15, (double)f4);
                tessellator.addVertexWithUV((double)(0.0F - f7), (double)(1.0F - f8), 0.0D, (double)f14, (double)f4);
                tessellator.draw();
                GL11.glPopMatrix();
            }
        }
    }

    private void drawBeautifulCurve(Entity e, TileEntityAIEmitter tileEntity, double dx, double dy, double dz, double x, double y, double z, float partialTick)
    {
        Tessellator tessellator = Tessellator.instance;
        ArrayList<Vector3f> controlsPoints = new ArrayList<Vector3f>();
        computeControlPoints(controlsPoints, tileEntity, e);
        double xValues[] = new double[controlsPoints.size()];
        double yValues[] = new double[controlsPoints.size()];
        double zValues[] = new double[controlsPoints.size()];
        int index = 0;
        for(Vector3f v : controlsPoints)
        {
            xValues[index] = v.x;
            yValues[index] = v.y;
            zValues[index] = v.z;
            
            index++;
        }
        CasteljauAlgorithm casteljau = new CasteljauAlgorithm(xValues,yValues,zValues,xValues.length);
        double last[] = new double[]{xValues[0],yValues[0],zValues[0]};
        double segments = 1d/ModCustomAI.config.getDouble("EmitterCurvesSegmentsNumber", 15);
        double minU = 0;
        double maxU = 1;
        double minV = 0+((double)e.ticksExisted/10D)+(segments/0.01d);
        double maxV = 1+((double)e.ticksExisted/10D)+(segments/0.01d);
        for(double t = 0;t<=1;t+=segments)
        {
            double[] values = casteljau.getXYZvalues(t);
            
            tessellator.addVertexWithUV(values[0]+dx, values[1]+dy, values[2]+dz,minU,minV);
            tessellator.addVertexWithUV(values[0]+dx, values[1]+dy+0.1, values[2]+dz,maxU,minV);
            tessellator.addVertexWithUV(last[0]+dx, last[1]+dy+0.1, last[2]+dz,maxU,maxV);
            tessellator.addVertexWithUV(last[0]+dx, last[1]+dy, last[2]+dz,minU,maxV);
            last = values;
        }
        
    }

    private void computeControlPoints(ArrayList<Vector3f> controlsPoints, TileEntityAIEmitter tileEntity, Entity e)
    {
        controlsPoints.add(new Vector3f(tileEntity.xCoord+0.5f, tileEntity.yCoord+0.5f, tileEntity.zCoord+0.5f));
        controlsPoints.add(new Vector3f(tileEntity.xCoord+0.5f, tileEntity.yCoord+0.5f+5, tileEntity.zCoord+0.5f));
        controlsPoints.add(new Vector3f((float)e.posX, (float)e.posY+e.yOffset+e.getEyeHeight()+1, (float)e.posZ));
        controlsPoints.add(new Vector3f((float)e.posX, (float)e.posY+e.yOffset+e.getEyeHeight(), (float)e.posZ));
    }

    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTick)
    {
        this.renderTileEntityAt((TileEntityAIEmitter)tileEntity, x, y, z, partialTick);
    }

    @Override
    public void renderInventory(double x, double y, double z)
    {
        this.renderTileEntityAt(null, x, y, z, 0.0F);
    }

}
