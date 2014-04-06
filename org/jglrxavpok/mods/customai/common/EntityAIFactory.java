package org.jglrxavpok.mods.customai.common;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIBeg;
import net.minecraft.entity.ai.EntityAIBreakDoor;
import net.minecraft.entity.ai.EntityAIControlledByPlayer;
import net.minecraft.entity.ai.EntityAICreeperSwell;
import net.minecraft.entity.ai.EntityAIDefendVillage;
import net.minecraft.entity.ai.EntityAIEatGrass;
import net.minecraft.entity.ai.EntityAIFleeSun;
import net.minecraft.entity.ai.EntityAIFollowGolem;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookAtTradePlayer;
import net.minecraft.entity.ai.EntityAILookAtVillager;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIMoveIndoors;
import net.minecraft.entity.ai.EntityAIMoveThroughVillage;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAIMoveTowardsTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIOcelotAttack;
import net.minecraft.entity.ai.EntityAIOcelotSit;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAIOwnerHurtByTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtTarget;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAIPlay;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAIRestrictSun;
import net.minecraft.entity.ai.EntityAIRunAroundLikeCrazy;
import net.minecraft.entity.ai.EntityAISit;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.ai.EntityAITargetNonTamed;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAITradePlayer;
import net.minecraft.entity.ai.EntityAIVillagerMate;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.item.Item;

import org.jglrxavpok.mods.customai.ai.EntityAIFleeSunEvenNotBurning;
import org.jglrxavpok.mods.customai.ai.EntityAIFollowEntity;
import org.jglrxavpok.mods.customai.ai.EntityAITeleportRandomly;
import org.jglrxavpok.mods.customai.json.JSONException;
import org.jglrxavpok.mods.customai.json.JSONObject;

public final class EntityAIFactory
{

    private static EntityAIFactory instance;

    public static EntityAIFactory instance()
    {
        if(instance == null)
            instance = new EntityAIFactory();
        return instance;
    }

    private EntitySheep testSheep;
    private EntitySpider testSpider;
    
    @SuppressWarnings("unchecked")
    public JSONObject generateJSON(EntityLiving entity, EntityAITaskEntry entry)
    {
        if(entity == null)
            entity = this.testSheep;
        if(entity == null)
            entity = new EntitySheep(null);
        JSONObject object = new JSONObject();
        if(entry == null || entry.action == null)
            return object;
        object.put("type", entry.action.getClass().getCanonicalName());
        object.put("priority", entry.priority);
        
        Class<? extends EntityAIBase> clazz = entry.action.getClass();
        if(clazz == EntityAIAvoidEntity.class)
        {
            Class<? extends Entity> entityToAvoid = ObfuscationReflectionHelper.getPrivateValue(EntityAIAvoidEntity.class, (EntityAIAvoidEntity)entry.action, 8);
            object.put("Entity class name", entityToAvoid == null ? "none" : getEntityName(entityToAvoid));
            object.put("Distance from entity", (Float)ObfuscationReflectionHelper.getPrivateValue(EntityAIAvoidEntity.class, (EntityAIAvoidEntity)entry.action, 5));
            object.put("Near speed", (Double)ObfuscationReflectionHelper.getPrivateValue(EntityAIAvoidEntity.class, (EntityAIAvoidEntity)entry.action, 3));
            object.put("Far speed", (Double)ObfuscationReflectionHelper.getPrivateValue(EntityAIAvoidEntity.class, (EntityAIAvoidEntity)entry.action, 2));
        }
        else if(clazz == EntityAIAttackOnCollide.class)
        {
            Class<? extends Entity> entityToAvoid = ObfuscationReflectionHelper.getPrivateValue(EntityAIAttackOnCollide.class, (EntityAIAttackOnCollide)entry.action, 6);
            object.put("Entity class name", entityToAvoid == null ? "none" : getEntityName(entityToAvoid));
            object.put("Speed towards target", (Double)ObfuscationReflectionHelper.getPrivateValue(EntityAIAttackOnCollide.class, (EntityAIAttackOnCollide)entry.action, 3));
            object.put("Long memory", (Boolean)ObfuscationReflectionHelper.getPrivateValue(EntityAIAttackOnCollide.class, (EntityAIAttackOnCollide)entry.action, 4));
        }
        else if(clazz == EntityAIWander.class)
        {
            object.put("Speed", (Double)ObfuscationReflectionHelper.getPrivateValue(EntityAIWander.class, (EntityAIWander)entry.action, 4));
        }
        else if(clazz == EntityAIWatchClosest.class)
        {
            object.put("Entity class name", getEntityName(((Class<? extends Entity>)ObfuscationReflectionHelper.getPrivateValue(EntityAIWatchClosest.class, (EntityAIWatchClosest)entry.action, 5))));
            object.put("Max distance", (Float)ObfuscationReflectionHelper.getPrivateValue(EntityAIWatchClosest.class, (EntityAIWatchClosest)entry.action, 2));
            object.put("Look probability", (Float)ObfuscationReflectionHelper.getPrivateValue(EntityAIWatchClosest.class, (EntityAIWatchClosest)entry.action, 4));
        }
        else if(clazz == EntityAINearestAttackableTarget.class)
        {
            object.put("Entity class name", getEntityName(((Class<? extends Entity>)ObfuscationReflectionHelper.getPrivateValue(EntityAINearestAttackableTarget.class, (EntityAINearestAttackableTarget)entry.action, 0))));
            object.put("Target chance", (Integer)ObfuscationReflectionHelper.getPrivateValue(EntityAINearestAttackableTarget.class, (EntityAINearestAttackableTarget)entry.action, 1));
            IEntitySelector selector = ObfuscationReflectionHelper.getPrivateValue(EntityAINearestAttackableTarget.class, (EntityAINearestAttackableTarget)entry.action, 3);
            object.put("Selector", testForSelector(entity, selector));
            object.put("On sight", (Boolean)ObfuscationReflectionHelper.getPrivateValue(EntityAITarget.class, (EntityAINearestAttackableTarget)entry.action, 1));
            object.put("Nearby only", (Boolean)ObfuscationReflectionHelper.getPrivateValue(EntityAITarget.class, (EntityAINearestAttackableTarget)entry.action, 2));
        }
        else if(clazz == EntityAIHurtByTarget.class)
        {
            object.put("Calls for help", (Boolean)ObfuscationReflectionHelper.getPrivateValue(EntityAIHurtByTarget.class, (EntityAIHurtByTarget)entry.action, 0));
        }
        else if(clazz == EntityAIArrowAttack.class)
        {
            object.put("Move speed", (Double)ObfuscationReflectionHelper.getPrivateValue(EntityAIArrowAttack.class, (EntityAIArrowAttack)entry.action, 4));
            object.put("Reload time", (Integer)ObfuscationReflectionHelper.getPrivateValue(EntityAIArrowAttack.class, (EntityAIArrowAttack)entry.action, 6));
            object.put("Max reload time", (Integer)ObfuscationReflectionHelper.getPrivateValue(EntityAIArrowAttack.class, (EntityAIArrowAttack)entry.action, 7));
            object.put("Distance from target", (Float)ObfuscationReflectionHelper.getPrivateValue(EntityAIArrowAttack.class, (EntityAIArrowAttack)entry.action, 8));
        }
        else if(clazz == EntityAIBeg.class)
        {
            object.put("Min distance", (Float)ObfuscationReflectionHelper.getPrivateValue(EntityAIBeg.class, (EntityAIBeg)entry.action, 3));
        }
        else if(clazz == EntityAIControlledByPlayer.class)
        {
            object.put("Max speed", (Float)ObfuscationReflectionHelper.getPrivateValue(EntityAIControlledByPlayer.class, (EntityAIControlledByPlayer)entry.action, 1));
        }
        else if(clazz == EntityAIFleeSun.class)
        {
            object.put("Move speed", (Double)ObfuscationReflectionHelper.getPrivateValue(EntityAIFleeSun.class, (EntityAIFleeSun)entry.action, 4));
        }
        else if(clazz == EntityAIFollowOwner.class)
        {
            object.put("Move speed", (Double)ObfuscationReflectionHelper.getPrivateValue(EntityAIFollowOwner.class, (EntityAIFollowOwner)entry.action, 3));
            object.put("Min distance", (Float)ObfuscationReflectionHelper.getPrivateValue(EntityAIFollowOwner.class, (EntityAIFollowOwner)entry.action, 7));
            object.put("Max distance", (Float)ObfuscationReflectionHelper.getPrivateValue(EntityAIFollowOwner.class, (EntityAIFollowOwner)entry.action, 6));
        }
        else if(clazz == EntityAIFollowParent.class)
        {
            object.put("Move speed", (Double)ObfuscationReflectionHelper.getPrivateValue(EntityAIFollowParent.class, (EntityAIFollowParent)entry.action, 2));
        }
        else if(clazz == EntityAILeapAtTarget.class)
        {
            object.put("Motion Y modifier", (Float)ObfuscationReflectionHelper.getPrivateValue(EntityAILeapAtTarget.class, (EntityAILeapAtTarget)entry.action,2));
        }
        else if(clazz == EntityAIMate.class)
        {
            object.put("Move speed", (Double)ObfuscationReflectionHelper.getPrivateValue(EntityAIMate.class, (EntityAIMate)entry.action, 4));
        }
        else if(clazz == EntityAIPanic.class)
        {
            object.put("Move speed", (Double)ObfuscationReflectionHelper.getPrivateValue(EntityAIPanic.class, (EntityAIPanic)entry.action, 1));
        }
        else if(clazz == EntityAITempt.class)
        {
            object.put("Move speed", (Double)ObfuscationReflectionHelper.getPrivateValue(EntityAITempt.class, (EntityAITempt)entry.action, 1));
            Item item = ((Item)ObfuscationReflectionHelper.getPrivateValue(EntityAITempt.class, (EntityAITempt)entry.action, 10));
            String s = item == null ? "missingno" : item.getUnlocalizedName().replaceFirst("item.","");
            object.put("Item name", s);
            object.put("Scared by player's movements", (Boolean)ObfuscationReflectionHelper.getPrivateValue(EntityAITempt.class, (EntityAITempt)entry.action, 11));
        }
        else if(clazz == EntityAIMoveThroughVillage.class)
        {
            object.put("Move speed", (Double)ObfuscationReflectionHelper.getPrivateValue(EntityAIMoveThroughVillage.class, (EntityAIMoveThroughVillage)entry.action, 1));
            object.put("Nocturnal", (Boolean)ObfuscationReflectionHelper.getPrivateValue(EntityAIMoveThroughVillage.class, (EntityAIMoveThroughVillage)entry.action, 4));
        }
        else if(clazz == EntityAIMoveTowardsRestriction.class)
        {
            object.put("Move speed", (Double)ObfuscationReflectionHelper.getPrivateValue(EntityAIMoveTowardsRestriction.class, (EntityAIMoveTowardsRestriction)entry.action, 4));
        }
        else if(clazz == EntityAIMoveTowardsTarget.class)
        {
            object.put("Move speed", (Double)ObfuscationReflectionHelper.getPrivateValue(EntityAIMoveTowardsTarget.class, (EntityAIMoveTowardsTarget)entry.action, 5));
        }
        else if(clazz == EntityAIOcelotSit.class)
        {
            object.put("Move speed", (Double)ObfuscationReflectionHelper.getPrivateValue(EntityAIOcelotSit.class, (EntityAIOcelotSit)entry.action, 1));
        }
        else if(clazz == EntityAIPlay.class)
        {
            object.put("Move speed", (Double)ObfuscationReflectionHelper.getPrivateValue(EntityAIPlay.class, (EntityAIPlay)entry.action, 2));   
        }
        else if(clazz == EntityAIRunAroundLikeCrazy.class)
        {
            object.put("Move speed", (Double)ObfuscationReflectionHelper.getPrivateValue(EntityAIRunAroundLikeCrazy.class, (EntityAIRunAroundLikeCrazy)entry.action, 1));
        }
        else if(clazz == EntityAITargetNonTamed.class)
        {
            object.put("Entity class name", getEntityName(((Class<? extends Entity>)ObfuscationReflectionHelper.getPrivateValue(EntityAINearestAttackableTarget.class, (EntityAITargetNonTamed)entry.action, 0))));
            object.put("Target chance", (Integer)ObfuscationReflectionHelper.getPrivateValue(EntityAINearestAttackableTarget.class, (EntityAINearestAttackableTarget)entry.action, 1));
            object.put("On sight", (Boolean)ObfuscationReflectionHelper.getPrivateValue(EntityAITarget.class, (EntityAITarget)entry.action, 1));
        }
        else if(clazz == EntityAIOpenDoor.class)
        {
            object.put("Open", (Boolean)ObfuscationReflectionHelper.getPrivateValue(EntityAIOpenDoor.class, (EntityAIOpenDoor)entry.action, 0));
        }
        // =============================
        // Start of custom AI
        // =============================
        else if(clazz == EntityAIFleeSunEvenNotBurning.class)
        {
            object.put("Move speed", (Double)ObfuscationReflectionHelper.getPrivateValue(EntityAIFleeSunEvenNotBurning.class, (EntityAIFleeSunEvenNotBurning)entry.action, 4));
        }
        else if(clazz == EntityAIFollowEntity.class)
        {
            object.put("Entity class name", getEntityName(((Class<? extends Entity>)ObfuscationReflectionHelper.getPrivateValue(EntityAIFollowEntity.class, (EntityAIFollowEntity)entry.action, 0))));
            object.put("Move speed", (Double)ObfuscationReflectionHelper.getPrivateValue(EntityAIFollowEntity.class, (EntityAIFollowEntity)entry.action, 3));
            object.put("Max distance", (Float)ObfuscationReflectionHelper.getPrivateValue(EntityAIFollowEntity.class, (EntityAIFollowEntity)entry.action, 4));
        }
        else if(clazz == EntityAITeleportRandomly.class)
        {
            object.put("Tick rate", (Integer)ObfuscationReflectionHelper.getPrivateValue(EntityAITeleportRandomly.class, (EntityAITeleportRandomly)entry.action, 1));
            object.put("Play teleport sound", (Boolean)ObfuscationReflectionHelper.getPrivateValue(EntityAITeleportRandomly.class, (EntityAITeleportRandomly)entry.action, 4));
            object.put("Avoid liquids", (Boolean)ObfuscationReflectionHelper.getPrivateValue(EntityAITeleportRandomly.class, (EntityAITeleportRandomly)entry.action, 5));
        }
        return object;
    }

    private String getEntityName(Class<? extends Entity> c)
    {
        String s = (String) EntityList.classToStringMapping.get(c);
        if(s == null)
            return c.getCanonicalName();
        else
            return s;
    }
    
    @SuppressWarnings("unchecked")
    private Class<? extends Entity> getEntityClass(String name)
    {
        Class<? extends Entity> clazz = (Class<? extends Entity>) EntityList.stringToClassMapping.get(name);
        if(clazz == null)
            try
            {
                return (Class<? extends Entity>) Class.forName(name);
            }
            catch (ClassNotFoundException e)
            {
                return null;
            }
        return clazz;
    }

    private String testForSelector(Entity e, IEntitySelector selector)
    {
        String s = "UNKNOWN";
        if(testSheep == null)
            testSheep = new EntitySheep(null);
        if(testSpider == null)
            testSpider = new EntitySpider(null);
        if(selector == null)
            return s;
        if(selector.isEntityApplicable(testSheep) && !selector.isEntityApplicable(testSpider))
        {
            s = "Passive animals";
        }
        else if(!selector.isEntityApplicable(testSheep) && selector.isEntityApplicable(testSpider))
        {
            s = "Mobs";
        }
        return s;
    }

    public EntityAITaskEntry generateAIBase(EntityLiving entity, String jsonData)
    {
        return generateAIBase(entity, new JSONObject(jsonData));
    }

    @SuppressWarnings("unchecked")
    public EntityAITaskEntry generateAIBaseWithExceptions(EntityLiving entity, JSONObject json) throws ClassNotFoundException, JSONException
    {
        if(!json.has("type"))
            return null;
        String clazz = json.getString("type");
        Class<? extends EntityAIBase> c = (Class<? extends EntityAIBase>) Class.forName(clazz);
        if(entity == null || CustomAIHelper.isSuitableForEntity(entity, c))
        {
            if(testSheep == null)
                testSheep = new EntitySheep(null);
            EntityAITaskEntry entry = (entity == null ? testSheep.tasks : entity.tasks).new EntityAITaskEntry(json.getInt("priority"), null);
            if(c == EntityAISwimming.class)
            {
                entry.action = new EntityAISwimming(entity);
            }
            else if(c == EntityAICreeperSwell.class)
            {
                entry.action = new EntityAICreeperSwell((EntityCreeper) entity);
            }
            else if(c == EntityAIAvoidEntity.class)
            {
                Class<? extends Entity> clazz1 = null;
                Class<? extends Entity> entClass = getEntityClass(json.getString("Entity class name"));
                if(entClass != null)
                    clazz1 = entClass;
                else if(checkIfClassExists(clazz))
                    clazz1 = (Class<? extends Entity>) Class.forName(clazz);
                else
                    clazz1 = EntityLiving.class;
                entry.action = new EntityAIAvoidEntity((EntityCreature)entity, clazz1, (float)json.getDouble("Distance from entity"),json.getDouble("Near speed"),json.getDouble("Far speed"));
            }
            else if(c == EntityAIAttackOnCollide.class)
            {
                if(json.getString("Entity class name").equals("none") || !checkIfClassExists(json.getString("Entity class name")))
                {
                    entry.action = new EntityAIAttackOnCollide((EntityCreature)entity, json.getDouble("Speed towards target"), json.getBoolean("Long memory"));
                }
                else
                {
                    entry.action = new EntityAIAttackOnCollide((EntityCreature)entity, getEntityClass(json.getString("Entity class name")), json.getDouble("Speed towards target"), json.getBoolean("Long memory"));
                }
            }
            else if(c == EntityAIWander.class)
            {
                entry.action = new EntityAIWander((EntityCreature)entity, json.getDouble("Speed"));
            }
            else if(c == EntityAIWatchClosest.class)
            {
                Class<? extends Entity> clazz1 = null;
                Class<? extends Entity> entClass = getEntityClass(json.getString("Entity class name"));
                if(entClass != null)
                    clazz1 = entClass;
                else if(checkIfClassExists(clazz))
                    clazz1 = (Class<? extends Entity>) Class.forName(clazz);
                else
                    clazz1 = EntityLiving.class;
                entry.action = new EntityAIWatchClosest(entity, clazz1, (float) json.getDouble("Max distance"), (float)json.getDouble("Look probability"));
            }
            else if(c == EntityAILookIdle.class)
            {
                entry.action = new EntityAILookIdle(entity);
            }
            else if(c == EntityAINearestAttackableTarget.class)
            {
                String s = json.getString("Selector");
                IEntitySelector selector = null;
                if(s.equals("Mobs"))
                {
                    selector = IMob.mobSelector;
                }
                else if(s.equals("Passive animals"))
                {
                    /**
                     * From the WitherBoss
                     */
                    selector = new IEntitySelector()
                    {
                        /**
                         * Return whether the specified entity is applicable to this filter.
                         */
                        public boolean isEntityApplicable(Entity par1Entity)
                        {
                            return par1Entity instanceof EntityLivingBase && ((EntityLivingBase)par1Entity).getCreatureAttribute() != EnumCreatureAttribute.UNDEAD;
                        }
                    };
                }
                else
                {
                    selector = null;
                }
                Class<? extends Entity> clazz1 = null;
                Class<? extends Entity> entClass = getEntityClass(json.getString("Entity class name"));
                if(entClass != null)
                    clazz1 = entClass;
                else if(checkIfClassExists(clazz))
                    clazz1 = (Class<? extends Entity>) Class.forName(clazz);
                else
                    clazz1 = EntityLiving.class;
                entry.action = new EntityAINearestAttackableTarget((EntityCreature)entity, clazz1, json.getInt("Target chance"), json.getBoolean("On sight"), json.getBoolean("Nearby only"), selector);
            }
            else if(c == EntityAIHurtByTarget.class)
            {
                entry.action = new EntityAIHurtByTarget((EntityCreature) entity, json.getBoolean("Calls for help"));
            }
            else if(c == EntityAIArrowAttack.class)
            {
                entry.action = new EntityAIArrowAttack((IRangedAttackMob)entity, json.getDouble("Move speed"), json.getInt("Reload time"), json.getInt("Max reload time"), (float) json.getDouble("Distance from target"));
            }
            else if(c == EntityAIBeg.class)
            {
                entry.action = new EntityAIBeg((EntityWolf)entity, (float) json.getDouble("Min distance"));
            }
            else if(c == EntityAIBreakDoor.class)
            {
                entry.action = new EntityAIBreakDoor(entity);
            }
            else if(c == EntityAIControlledByPlayer.class)
            {
                entry.action = new EntityAIControlledByPlayer(entity, (float)json.getDouble("Max speed"));
            }
            else if(c == EntityAIDefendVillage.class)
            {
                entry.action = new EntityAIDefendVillage((EntityIronGolem)entity);
            }
            else if(c == EntityAIEatGrass.class)
            {
                entry.action = new EntityAIEatGrass(entity);
            }
            else if(c == EntityAIFleeSun.class)
            {
                entry.action = new EntityAIFleeSun((EntityCreature)entity, json.getDouble("Move speed"));
            }
            else if(c == EntityAIFollowGolem.class)
            {
                entry.action = new EntityAIFollowGolem((EntityVillager)entity);
            }
            else if(c == EntityAIFollowOwner.class)
            {
                entry.action = new EntityAIFollowOwner((EntityTameable)entity,json.getDouble("Move speed"),(float)json.getDouble("Min distance"),(float)json.getDouble("Max distance"));
            }
            else if(c == EntityAIFollowParent.class)
            {
                entry.action = new EntityAIFollowParent((EntityAnimal)entity, json.getDouble("Move speed"));
            }
            else if(c == EntityAILeapAtTarget.class)
            {
                entry.action = new EntityAILeapAtTarget(entity, (float)json.getDouble("Motion Y modifier"));
            }
            else if(c == EntityAILookAtTradePlayer.class)
            {
                entry.action = new EntityAILookAtTradePlayer((EntityVillager) entity);
            }
            else if(c == EntityAILookAtVillager.class)
            {
                entry.action = new EntityAILookAtVillager((EntityIronGolem)entity);
            }
            else if(c == EntityAIMate.class)
            {
                entry.action = new EntityAIMate((EntityAnimal) entity, json.getDouble("Move speed"));
            }
            else if(c == EntityAIPanic.class)
            {
                entry.action = new EntityAIPanic((EntityCreature) entity, json.getDouble("Move speed"));
            }
            else if(c == EntityAITempt.class)
            {
                entry.action = new EntityAITempt((EntityCreature)entity, json.getDouble("Move speed"), (Item)Item.itemRegistry.getObject(json.getString("Item name").replaceFirst("item.", "")), json.getBoolean("Scared by player's movements"));
            }
            else if(c == EntityAIMoveIndoors.class)
            {
                entry.action = new EntityAIMoveIndoors((EntityCreature)entity);
            }
            else if(c == EntityAIMoveThroughVillage.class)
            {
                entry.action = new EntityAIMoveThroughVillage((EntityCreature)entity, json.getDouble("Move speed"), json.getBoolean("Nocturnal"));
            }
            else if(c == EntityAIMoveTowardsRestriction.class)
            {
                entry.action = new EntityAIMoveTowardsRestriction((EntityCreature)entity, json.getDouble("Move speed"));
            }
            else if(c == EntityAIMoveTowardsTarget.class)
            {
                entry.action = new EntityAIMoveTowardsTarget((EntityCreature)entity, json.getDouble("Move speed"), (float)json.getDouble("Max distance"));
            }
            else if(c == EntityAIOcelotAttack.class)
            {
                entry.action = new EntityAIOcelotAttack(entity);
            }
            else if(c == EntityAIOcelotSit.class)
            {
                entry.action = new EntityAIOcelotSit((EntityOcelot)entity, json.getDouble("Move speed"));
            }
            else if(c == EntityAIPlay.class)
            {
                entry.action = new EntityAIPlay((EntityVillager)entity, json.getDouble("Move speed"));
            }
            else if(c == EntityAIRestrictOpenDoor.class)
            {
                entry.action = new EntityAIRestrictOpenDoor((EntityCreature)entity);
            }
            else if(c == EntityAIRestrictSun.class)
            {
                entry.action = new EntityAIRestrictSun((EntityCreature)entity);
            }
            else if(c == EntityAIRunAroundLikeCrazy.class)
            {
                entry.action = new EntityAIRunAroundLikeCrazy((EntityHorse)entity, json.getDouble("Move speed"));
            }
            else if(c == EntityAISit.class)
            {
                entry.action = new EntityAISit((EntityTameable)entity);
            }
            else if(c == EntityAITargetNonTamed.class)
            {
                Class<? extends Entity> clazz1 = null;
                Class<? extends Entity> entClass = getEntityClass(json.getString("Entity class name"));
                if(entClass != null)
                    clazz1 = entClass;
                else if(checkIfClassExists(clazz))
                    clazz1 = (Class<? extends Entity>) Class.forName(clazz);
                else
                    clazz1 = EntityLiving.class;
                entry.action = new EntityAITargetNonTamed((EntityTameable)entity, clazz1, json.getInt("Target chance"), json.getBoolean("On sight"));
            }
            else if(c == EntityAITradePlayer.class)
            {
                entry.action = new EntityAITradePlayer((EntityVillager)entity);
            }
            else if(c == EntityAIVillagerMate.class)
            {
                entry.action = new EntityAIVillagerMate((EntityVillager)entity);
            }
            else if(c == EntityAIWatchClosest2.class)
            {
                Class<? extends Entity> clazz1 = null;
                Class<? extends Entity> entClass = getEntityClass(json.getString("Entity class name"));
                if(entClass != null)
                    clazz1 = entClass;
                else if(checkIfClassExists(clazz))
                    clazz1 = (Class<? extends Entity>) Class.forName(clazz);
                else
                    clazz1 = EntityLiving.class;
                entry.action = new EntityAIWatchClosest2(entity, clazz1, (float) json.getDouble("Max distance"), (float)json.getDouble("Look probability"));
            }
            else if(c == EntityAIOpenDoor.class)
            {
                entry.action = new EntityAIOpenDoor(entity, json.getBoolean("Open"));
            }
            else if(c == EntityAIOwnerHurtByTarget.class)
            {
                entry.action = new EntityAIOwnerHurtByTarget((EntityTameable)entity);
            }
            else if(c == EntityAIOwnerHurtTarget.class)
            {
                entry.action = new EntityAIOwnerHurtTarget((EntityTameable)entity);
            }
            
            // =============================
            // Start of custom AI
            // =============================
            else if(c == EntityAIFleeSunEvenNotBurning.class)
            {
                entry.action = new EntityAIFleeSunEvenNotBurning((EntityCreature)entity, json.getDouble("Move speed"));
            }
            else if(c == EntityAIFollowEntity.class)
            {
                Class<? extends Entity> clazz1 = null;
                Class<? extends Entity> entClass = getEntityClass(json.getString("Entity class name"));
                if(entClass != null)
                    clazz1 = entClass;
                else if(checkIfClassExists(clazz))
                    clazz1 = (Class<? extends Entity>) Class.forName(clazz);
                else
                    clazz1 = EntityLiving.class;
                entry.action = new EntityAIFollowEntity(entity, clazz1, json.getDouble("Move speed"), (float)json.getDouble("Max distance"));
            }
            else if(c == EntityAITeleportRandomly.class)
            {
                entry.action = new EntityAITeleportRandomly(entity, json.getInt("Tick rate"), json.getBoolean("Play teleport sound"), json.getBoolean("Avoid liquids"));
            }
            if(entry.action == null)
                return null;
            return entry;
        }
        return null;
    }
    public EntityAITaskEntry generateAIBase(EntityLiving entity, JSONObject json)
    {
        try
        {
            return generateAIBaseWithExceptions(entity, json);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private boolean checkIfClassExists(String string)
    {
        if(string == null)
            return false;
        try
        {
            Class.forName(string);
            return true;
        }
        catch(Exception e)
        {
            ;
        }
        return false;
    }

    public JSONObject createDummyJSON(Class<? extends EntityAIBase> clazz)
    {
        JSONObject object = new JSONObject();
        object.put("type", clazz.getCanonicalName());
        object.put("priority", 1);
        if(clazz == EntityAIAvoidEntity.class)
        {
            object.put("Entity class name", "none");
            object.put("Distance from entity", 10f);
            object.put("Near speed", 1.2);
            object.put("Far speed", 1.2);
        }
        else if(clazz == EntityAIAttackOnCollide.class)
        {
            object.put("Entity class name", "none");
            object.put("Speed towards target", 1);
            object.put("Long memory", false);
        }
        else if(clazz == EntityAIWander.class)
        {
            object.put("Speed", 0.8);
        }
        else if(clazz == EntityAIWatchClosest.class)
        {
            object.put("Entity class name", "none");
            object.put("Max distance", 10f);
            object.put("Look probability", 0.5f);
        }
        else if(clazz == EntityAINearestAttackableTarget.class)
        {
            object.put("Entity class name", "none");
            object.put("Target chance", 10);
            object.put("Selector", "UNKNOWN");
            object.put("On sight", false);
            object.put("Nearby only", true);
        }
        else if(clazz == EntityAIHurtByTarget.class)
        {
            object.put("Calls for help", false);
        }
        else if(clazz == EntityAIArrowAttack.class)
        {
            object.put("Move speed", 0.9);
            object.put("Reload time", 20);
            object.put("Max reload time", 60);
            object.put("Distance from target", 10f);
        }
        else if(clazz == EntityAIBeg.class)
        {
            object.put("Min distance", 6f);
        }
        else if(clazz == EntityAIControlledByPlayer.class)
        {
            object.put("Max speed", 1.5f);
        }
        else if(clazz == EntityAIFleeSun.class)
        {
            object.put("Move speed", 1.1);
        }
        else if(clazz == EntityAIFollowOwner.class)
        {
            object.put("Move speed", 0.9);
            object.put("Min distance", 2f);
            object.put("Max distance", 15f);
        }
        else if(clazz == EntityAIFollowParent.class)
        {
            object.put("Move speed", 0.9);
        }
        else if(clazz == EntityAILeapAtTarget.class)
        {
            object.put("Motion Y modifier", 0.5f);
        }
        else if(clazz == EntityAIMate.class)
        {
            object.put("Move speed", 0.9);
        }
        else if(clazz == EntityAIPanic.class)
        {
            object.put("Move speed", 0.9);
        }
        else if(clazz == EntityAITempt.class)
        {
            object.put("Move speed", 0.9);
            object.put("Item name", "wheat");
            object.put("Scared by player's movements", false);
        }
        else if(clazz == EntityAIMoveThroughVillage.class)
        {
            object.put("Move speed", 0.9);
            object.put("Nocturnal", false);
        }
        else if(clazz == EntityAIMoveTowardsRestriction.class)
        {
            object.put("Move speed", 0.9);
        }
        else if(clazz == EntityAIMoveTowardsTarget.class)
        {
            object.put("Move speed", 0.9);
        }
        else if(clazz == EntityAIOcelotSit.class)
        {
            object.put("Move speed", 0.9);
        }
        else if(clazz == EntityAIPlay.class)
        {
            object.put("Move speed", 0.9);   
        }
        else if(clazz == EntityAIRunAroundLikeCrazy.class)
        {
            object.put("Move speed", 0.9);
        }
        else if(clazz == EntityAITargetNonTamed.class)
        {
            object.put("Entity class name", "none");
            object.put("Target chance", 10);
            object.put("On sight", false);
        }
        else if(clazz == EntityAIOpenDoor.class)
        {
            object.put("Open", true);
        }
        
        // =============================
        // Start of custom AI
        // =============================
        else if(clazz == EntityAIFleeSunEvenNotBurning.class)
        {
            object.put("Move speed", 0.9);
        }
        else if(clazz == EntityAIFollowEntity.class)
        {
            object.put("Entity class name", "none");
            object.put("Move speed", 0.9);
            object.put("Max distance", 10f);
        }
        else if(clazz == EntityAITeleportRandomly.class)
        {
            object.put("Tick rate", 60);
            object.put("Play teleport sound", true);
            object.put("Avoid liquids", true);
        }

        return object;
    }

}
