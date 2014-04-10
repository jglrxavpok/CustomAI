package org.jglrxavpok.mods.customai.ai;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;

public class ArrowProjectile extends Projectile
{

    public ArrowProjectile(String id)
    {
        super(id);
    }
    
    public void attackEntityWithRangedAttack(EntityLiving user, EntityLivingBase par1EntityLivingBase, float par2)
    {
        EntityArrow entityarrow = new EntityArrow(user.worldObj, user, par1EntityLivingBase, 1.0F, (float)(14 - user.worldObj.difficultySetting.getDifficultyId() * 4));
        int i = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, user.getHeldItem());
        int j = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, user.getHeldItem());
        entityarrow.setDamage((double)(par2 * 2.0F) + user.getRNG().nextGaussian() * 0.25D + (double)((float)user.worldObj.difficultySetting.getDifficultyId() * 0.11F));

        if (i > 0)
        {
            entityarrow.setDamage(entityarrow.getDamage() + (double)i * 0.5D + 0.5D);
        }

        if (j > 0)
        {
            entityarrow.setKnockbackStrength(j);
        }

        if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, user.getHeldItem()) > 0)
        {
            entityarrow.setFire(100);
        }

        user.playSound("random.bow", 1.0F, 1.0F / (user.getRNG().nextFloat() * 0.4F + 0.8F));
        user.worldObj.spawnEntityInWorld(entityarrow);
    }
}
