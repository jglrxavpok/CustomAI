package org.jglrxavpok.mods.customai.common;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityWitherSkull2 extends EntityWitherSkull
{

    private EntityLiving owner;

    public EntityWitherSkull2(World par1World, EntityLivingBase par2EntityLivingBase, double par3, double par5, double par7)
    {
        super(par1World, par2EntityLivingBase, par3, par5, par7);
    }
    
    public void setOwner(EntityLiving owner)
    {
        this.owner = owner;
    }
    
    public EntityLiving getOwner()
    {
        return owner;
    }
    
    protected void onImpact(MovingObjectPosition par1MovingObjectPosition)
    {
        if (!this.worldObj.isRemote)
        {
            if (par1MovingObjectPosition.entityHit != null)
            {
                if(this.shootingEntity == par1MovingObjectPosition.entityHit)
                {
                    return;
                }
                if (this.shootingEntity != null)
                {
                    if (par1MovingObjectPosition.entityHit.attackEntityFrom(DamageSource.causeMobDamage(this.shootingEntity), 8.0F) && !par1MovingObjectPosition.entityHit.isEntityAlive())
                    {
                        this.shootingEntity.heal(5.0F);
                    }
                }
                else
                {
                    par1MovingObjectPosition.entityHit.attackEntityFrom(DamageSource.magic, 5.0F);
                }

                if (par1MovingObjectPosition.entityHit instanceof EntityLivingBase)
                {
                    byte b0 = 0;

                    if (this.worldObj.difficultySetting == EnumDifficulty.NORMAL)
                    {
                        b0 = 10;
                    }
                    else if (this.worldObj.difficultySetting == EnumDifficulty.HARD)
                    {
                        b0 = 40;
                    }

                    if (b0 > 0)
                    {
                        ((EntityLivingBase)par1MovingObjectPosition.entityHit).addPotionEffect(new PotionEffect(Potion.wither.id, 20 * b0, 1));
                    }
                }
            }

            this.worldObj.newExplosion(shootingEntity, this.posX, this.posY, this.posZ, 1.0F, false, this.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing"));
            this.setDead();
        }
    }

}
