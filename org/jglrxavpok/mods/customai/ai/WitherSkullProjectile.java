package org.jglrxavpok.mods.customai.ai;

import java.util.Random;

import org.jglrxavpok.mods.customai.common.EntityWitherSkull2;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.util.MathHelper;

/**
 * Almost 90% from EntityWither.class
 * @author jglrxavpok
 *
 */
public class WitherSkullProjectile extends Projectile
{

    private Random rand;

    public WitherSkullProjectile(String id)
    {
        super(id);
        rand = new Random();
    }

    private double func_82214_u(EntityLiving user, int par1)
    {
        if (par1 <= 0)
        {
            return user.posX;
        }
        else
        {
            float f = (user.renderYawOffset + (float)(180 * (par1 - 1))) / 180.0F * (float)Math.PI;
            float f1 = MathHelper.cos(f);
            return user.posX + (double)f1 * 1.3D;
        }
    }
   
    private double func_82208_v(EntityLiving user, int par1)
    {
        return par1 <= 0 ? user.posY + 3.0D : user.posY + 2.2D;
    }

    private double func_82213_w(EntityLiving user, int par1)
    {
        if (par1 <= 0)
        {
            return user.posZ;
        }
        else
        {
            float f = (user.renderYawOffset + (float)(180 * (par1 - 1))) / 180.0F * (float)Math.PI;
            float f1 = MathHelper.sin(f);
            return user.posZ + (double)f1 * 1.3D;
        }
    }
    
    private void func_82209_a(EntityLiving user, EntityLivingBase par1EntityLivingBase, int par1, double par2, double par4, double par6, boolean par8)
    {
        user.worldObj.playAuxSFXAtEntity((EntityPlayer)null, 1014, (int)user.posX, (int)user.posY, (int)user.posZ, 0);
        EntityWitherSkull2 entitywitherskull = new EntityWitherSkull2(user.worldObj, user, 0, 0, 0);

        if (par8)
        {
            entitywitherskull.setInvulnerable(true);
        }

        double d0 = par1EntityLivingBase.posX - user.posX;
        double d1 = par1EntityLivingBase.boundingBox.minY + (double)(user.height / 3.0F) - user.posY;
        double d2 = par1EntityLivingBase.posZ - user.posZ;
        double d3 = (double)MathHelper.sqrt_double(d0 * d0 + d2 * d2);

        if (d3 >= 1.0E-7D)
        {
            float f2 = (float)(Math.atan2(d2, d0) * 180.0D / Math.PI) - 90.0F;
            float f3 = (float)(-(Math.atan2(d1, d3) * 180.0D / Math.PI));
            double d4 = d0 / d3;
            double d5 = d2 / d3;
            user.setLocationAndAngles(user.posX + d4, user.posY+user.getEyeHeight()*2, user.posZ + d5, f2, f3);
            user.yOffset = 0.0F;
            float f4 = (float)d3 * 0.2F;
            this.setThrowableHeading(entitywitherskull, d0, d1 + (double)f4, d2, 1.5f, 1f);
        }
        user.worldObj.spawnEntityInWorld(entitywitherskull);
    }

    public void setThrowableHeading(EntityWitherSkull skull, double par1, double par3, double par5, float par7, float par8)
    {
        float f2 = MathHelper.sqrt_double(par1 * par1 + par3 * par3 + par5 * par5);
        par1 /= (double)f2;
        par3 /= (double)f2;
        par5 /= (double)f2;
        par1 += rand.nextGaussian() * (double)(rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * (double)par8;
        par3 += rand.nextGaussian() * (double)(rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * (double)par8;
        par5 += rand.nextGaussian() * (double)(rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * (double)par8;
        par1 *= (double)par7;
        par3 *= (double)par7;
        par5 *= (double)par7;
        skull.motionX = par1;
        skull.motionY = par3;
        skull.motionZ = par5;
        float f3 = MathHelper.sqrt_double(par1 * par1 + par5 * par5);
        skull.prevRotationYaw = skull.rotationYaw = (float)(Math.atan2(par1, par5) * 180.0D / Math.PI);
        skull.prevRotationPitch = skull.rotationPitch = (float)(Math.atan2(par3, (double)f3) * 180.0D / Math.PI);
    }

    /**
     * Attack the specified entity using a ranged attack.
     */
    public void attackEntityWithRangedAttack(EntityLiving user, EntityLivingBase par1EntityLivingBase, float par2)
    {
        this.func_82209_a(user, par1EntityLivingBase, 0, user.posX, user.posY + (double)user.getEyeHeight() * 0.5D, user.posZ, user.getRNG().nextFloat() < 0.001F);
    }

}
