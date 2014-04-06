package org.jglrxavpok.mods.customai.ai;

import java.util.Random;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityAIFleeSunEvenNotBurning extends EntityAIBase
{
    private EntityCreature theCreature;
    private double shelterX;
    private double shelterY;
    private double shelterZ;
    private double movementSpeed;

    public EntityAIFleeSunEvenNotBurning(EntityCreature par1EntityCreature, double par2)
    {
        this.theCreature = par1EntityCreature;
        this.movementSpeed = par2;
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        if (!this.theCreature.worldObj.isDaytime())
        {
            return false;
        }
        else if (!this.theCreature.worldObj.canBlockSeeTheSky(MathHelper.floor_double(this.theCreature.posX), (int)this.theCreature.boundingBox.minY, MathHelper.floor_double(this.theCreature.posZ)))
        {
            return false;
        }
        else
        {
            Vec3 vec3 = this.findPossibleShelter();

            if (vec3 == null)
            {
                return false;
            }
            else
            {
                this.shelterX = vec3.xCoord;
                this.shelterY = vec3.yCoord;
                this.shelterZ = vec3.zCoord;
                return true;
            }
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return !this.theCreature.getNavigator().noPath();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.theCreature.getNavigator().tryMoveToXYZ(this.shelterX, this.shelterY, this.shelterZ, this.movementSpeed);
    }

    private Vec3 findPossibleShelter()
    {
        Random random = this.theCreature.getRNG();

        for (int i = 0; i < 10; ++i)
        {
            int j = MathHelper.floor_double(this.theCreature.posX + (double)random.nextInt(20) - 10.0D);
            int k = MathHelper.floor_double(this.theCreature.boundingBox.minY + (double)random.nextInt(6) - 3.0D);
            int l = MathHelper.floor_double(this.theCreature.posZ + (double)random.nextInt(20) - 10.0D);

            if (!this.theCreature.worldObj.canBlockSeeTheSky(j, k, l) && this.theCreature.getBlockPathWeight(j, k, l) < 0.0F)
            {
                return this.theCreature.worldObj.getWorldVec3Pool().getVecFromPool((double)j, (double)k, (double)l);
            }
        }

        return null;
    }
}