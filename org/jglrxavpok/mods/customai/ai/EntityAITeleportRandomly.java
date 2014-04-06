package org.jglrxavpok.mods.customai.ai;

import org.jglrxavpok.mods.customai.common.CustomAIHelper;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;

public class EntityAITeleportRandomly extends EntityAIBase
{

    private EntityLiving entity;
    private int tickRate;

    private int teleportDelay;
    private World worldObj;
    private boolean playSound;
    private boolean avoidLiquids;
    
    public EntityAITeleportRandomly(EntityLiving living, int tickRate, boolean playSound, boolean avoidLiquids)
    {
        this.entity = living;
        if(tickRate <= 0)
            tickRate = 1;
        this.tickRate = tickRate;
        worldObj = living.worldObj;
        this.playSound = playSound;
        this.avoidLiquids = avoidLiquids;
        this.setMutexBits(CustomAIHelper.MOVE_MUTEX_BITS);
    }
    
    public void startExecuting()
    {
        teleportRandomly();
    }
    
    /**
     * Teleport the entity to a random nearby position
     */
    protected boolean teleportRandomly()
    {
        double d0 = entity.posX + (entity.getRNG().nextDouble() - 0.5D) * 64.0D;
        double d1 = entity.posY + (double)(entity.getRNG().nextInt(64) - 32);
        double d2 = entity.posZ + (entity.getRNG().nextDouble() - 0.5D) * 64.0D;
        return this.teleportTo(d0, d1, d2);
    }

    /**
     * Teleport the entity to another entity
     */
    protected boolean teleportToEntity(Entity par1Entity)
    {
        Vec3 vec3 = this.worldObj.getWorldVec3Pool().getVecFromPool(entity.posX - par1Entity.posX, entity.boundingBox.minY + (double)(entity.height / 2.0F) - par1Entity.posY + (double)par1Entity.getEyeHeight(), entity.posZ - par1Entity.posZ);
        vec3 = vec3.normalize();
        double d0 = 16.0D;
        double d1 = entity.posX + (entity.getRNG().nextDouble() - 0.5D) * 8.0D - vec3.xCoord * d0;
        double d2 = entity.posY + (double)(entity.getRNG().nextInt(16) - 8) - vec3.yCoord * d0;
        double d3 = entity.posZ + (entity.getRNG().nextDouble() - 0.5D) * 8.0D - vec3.zCoord * d0;
        return this.teleportTo(d1, d2, d3);
    }

    /**
     * Teleport the entity to a specified location
     */
    protected boolean teleportTo(double par1, double par3, double par5)
    {
        EnderTeleportEvent event = new EnderTeleportEvent(entity, par1, par3, par5, 0);
        if (MinecraftForge.EVENT_BUS.post(event))
        {
            return false;
        }
        double d3 = entity.posX;
        double d4 = entity.posY;
        double d5 = entity.posZ;
        entity.posX = event.targetX;
        entity.posY = event.targetY;
        entity.posZ = event.targetZ;
        boolean flag = false;
        int i = MathHelper.floor_double(entity.posX);
        int j = MathHelper.floor_double(entity.posY);
        int k = MathHelper.floor_double(entity.posZ);

        if (this.worldObj.blockExists(i, j, k))
        {
            boolean flag1 = false;

            while (!flag1 && j > 0)
            {
                Block block = this.worldObj.getBlock(i, j - 1, k);

                if (block.getMaterial().blocksMovement())
                {
                    flag1 = true;
                }
                else
                {
                    --entity.posY;
                    --j;
                }
            }

            if (flag1)
            {
                entity.setPosition(entity.posX, entity.posY, entity.posZ);

                if (this.worldObj.getCollidingBoundingBoxes(entity, entity.boundingBox).isEmpty() && (!avoidLiquids || !this.worldObj.isAnyLiquid(entity.boundingBox)))
                {
                    flag = true;
                }
            }
        }

        if (!flag)
        {
            entity.setPosition(d3, d4, d5);
            return false;
        }
        else
        {
            short short1 = 128;

            for (int l = 0; l < short1; ++l)
            {
                double d6 = (double)l / ((double)short1 - 1.0D);
                float f = (entity.getRNG().nextFloat() - 0.5F) * 0.2F;
                float f1 = (entity.getRNG().nextFloat() - 0.5F) * 0.2F;
                float f2 = (entity.getRNG().nextFloat() - 0.5F) * 0.2F;
                double d7 = d3 + (entity.posX - d3) * d6 + (entity.getRNG().nextDouble() - 0.5D) * (double)entity.width * 2.0D;
                double d8 = d4 + (entity.posY - d4) * d6 + entity.getRNG().nextDouble() * (double)entity.height;
                double d9 = d5 + (entity.posZ - d5) * d6 + (entity.getRNG().nextDouble() - 0.5D) * (double)entity.width * 2.0D;
                this.worldObj.spawnParticle("portal", d7, d8, d9, (double)f, (double)f1, (double)f2);
            }

            if(playSound)
            {
                this.worldObj.playSoundEffect(d3, d4, d5, "mob.endermen.portal", 1.0F, 1.0F);
                entity.playSound("mob.endermen.portal", 1.0F, 1.0F);
            }
            return true;
        }
    }
    
    public boolean continueExecuting()
    {
        return false;
    }
    
    @Override
    public boolean shouldExecute()
    {
        boolean flag = entity.getRNG().nextInt(tickRate) <= 2;
        return flag;
    }

}
