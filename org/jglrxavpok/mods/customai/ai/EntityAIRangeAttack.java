package org.jglrxavpok.mods.customai.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.MathHelper;

public class EntityAIRangeAttack extends EntityAIBase
{
    /**
     * The entity the AI instance has been applied to
     */
    private final EntityLiving entityHost;
    private EntityLivingBase attackTarget;
    /**
     * A decrementing tick that spawns a ranged attack once this value reaches 0. It is then set back to the
     * maxRangedAttackTime.
     */
    private int rangedAttackTime;
    private double entityMoveSpeed;
    private int field_75318_f;
    private int reloadTime;
    /**
     * The maximum time the AI has to wait before performing another ranged attack.
     */
    private int maxRangedAttackTime;
    private float maxDist;
    private float maxDistSquared;
    private Projectile projectile;

    public EntityAIRangeAttack(EntityLiving living, double par2, int par4, int par5, float par6, Projectile projectileType)
    {
        this.rangedAttackTime = -1;

        if (!(living instanceof EntityLivingBase))
        {
            throw new IllegalArgumentException("RangeAttackGoal requires EntityLiving implements RangedAttackMob");
        }
        else
        {
            this.entityHost = living;
            this.entityMoveSpeed = par2;
            this.reloadTime = par4;
            this.maxRangedAttackTime = par5;
            this.maxDist = par6;
            this.maxDistSquared = par6 * par6;
            this.projectile = projectileType;
            this.setMutexBits(3);
        }
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        EntityLivingBase entitylivingbase = this.entityHost.getAttackTarget();

        if (entitylivingbase == null)
        {
            return false;
        }
        else
        {
            this.attackTarget = entitylivingbase;
            return true;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return this.shouldExecute() || !this.entityHost.getNavigator().noPath();
    }

    /**
     * Resets the task
     */
    public void resetTask()
    {
        this.attackTarget = null;
        this.field_75318_f = 0;
        this.rangedAttackTime = -1;
    }

    /**
     * Updates the task
     */
    public void updateTask()
    {
        double d0 = this.entityHost.getDistanceSq(this.attackTarget.posX, this.attackTarget.boundingBox.minY, this.attackTarget.posZ);
        boolean flag = this.entityHost.getEntitySenses().canSee(this.attackTarget);

        if (flag)
        {
            ++this.field_75318_f;
        }
        else
        {
            this.field_75318_f = 0;
        }

        if (d0 <= (double)this.maxDistSquared && this.field_75318_f >= 20)
        {
            this.entityHost.getNavigator().clearPathEntity();
        }
        else
        {
            this.entityHost.getNavigator().tryMoveToEntityLiving(this.attackTarget, this.entityMoveSpeed);
        }

        this.entityHost.getLookHelper().setLookPositionWithEntity(this.attackTarget, 30.0F, 30.0F);
        float f;

        if (--this.rangedAttackTime == 0)
        {
            if (d0 > (double)this.maxDistSquared || !flag)
            {
                return;
            }

            f = MathHelper.sqrt_double(d0) / this.maxDist;
            float f1 = f;

            if (f < 0.1F)
            {
                f1 = 0.1F;
            }

            if (f1 > 1.0F)
            {
                f1 = 1.0F;
            }

            projectile.attackEntityWithRangedAttack(this.entityHost,this.attackTarget, f1);
            this.rangedAttackTime = MathHelper.floor_float(f * (float)(this.maxRangedAttackTime - this.reloadTime) + (float)this.reloadTime);
        }
        else if (this.rangedAttackTime < 0)
        {
            f = MathHelper.sqrt_double(d0) / this.maxDist;
            this.rangedAttackTime = MathHelper.floor_float(f * (float)(this.maxRangedAttackTime - this.reloadTime) + (float)this.reloadTime);
        }
    }
}