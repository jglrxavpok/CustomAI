package org.jglrxavpok.mods.customai.ai;

import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAIFollowEntity extends EntityAIBase
{
    private Class<? extends Entity> target;
    private EntityLiving follower;
    private EntityLivingBase theTarget;
    private double followSpeed;
    private float max;

    public EntityAIFollowEntity(EntityLiving taskOwner, Class<? extends Entity> entityToFollow, double speed, float maxDist)
    {
        follower = taskOwner;
        this.target = entityToFollow;
        this.setMutexBits(3);
        followSpeed = speed;
        max = maxDist;
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        List<?> list = this.follower.worldObj.getEntitiesWithinAABB(this.target, this.follower.boundingBox.expand(this.max, this.max, this.max));

        if (list.isEmpty())
        {
            return false;
        }
        else
        {
            Iterator<?> iterator = list.iterator();

            while (iterator.hasNext())
            {
                theTarget = (EntityLivingBase)iterator.next();
            }

            return this.theTarget != null;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return this.follower.getDistanceSqToEntity(this.theTarget) < max && !theTarget.isDead;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        if(theTarget instanceof EntityLiving)
        ((EntityLiving)theTarget).getNavigator().clearPathEntity();
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        this.theTarget = null;
        this.follower.getNavigator().clearPathEntity();
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        this.follower.getLookHelper().setLookPositionWithEntity(this.theTarget, 30.0F, 30.0F);
        this.follower.getNavigator().tryMoveToEntityLiving(this.theTarget, followSpeed);
    }
}