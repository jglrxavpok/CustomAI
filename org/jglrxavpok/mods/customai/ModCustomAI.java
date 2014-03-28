package org.jglrxavpok.mods.customai;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
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
import net.minecraft.entity.ai.EntityAITargetNonTamed;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAITradePlayer;
import net.minecraft.entity.ai.EntityAIVillagerMate;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraftforge.common.MinecraftForge;

import org.jglrxavpok.mods.customai.ai.EntityAIFleeSunEvenNotBurning;
import org.jglrxavpok.mods.customai.ai.EntityAIFollowEntity;
import org.jglrxavpok.mods.customai.common.CustomAIGuiHandler;
import org.jglrxavpok.mods.customai.common.EntityEvents;
import org.jglrxavpok.mods.customai.items.AwesomeAIRewriterItem;
import org.jglrxavpok.mods.customai.netty.PacketPipeline;

import static org.jglrxavpok.mods.customai.common.CustomAIHelper.registerAI;

@Mod(modid=ModCustomAI.MODID, name = "Custom AI", version = "0.1")
public class ModCustomAI
{

    public static final PacketPipeline packetPipeline = new PacketPipeline();
    public static final String MODID = "customai";
    private IGuiHandler guiHandler = new CustomAIGuiHandler();
    public static AwesomeAIRewriterItem rewriterItem;
    
    @Instance(MODID)
    public static ModCustomAI instance;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        registerAI(EntityAISwimming.class, "Swim",false);
        registerAI(EntityAICreeperSwell.class, "Creeper swell",false);
        registerAI(EntityAIAvoidEntity.class, "Avoid entity",false);
        registerAI(EntityAIAttackOnCollide.class, "Attack by collision",false);
        registerAI(EntityAIWander.class, "Wander",false);
        registerAI(EntityAIWatchClosest.class, "Watch closest entity",false);
        registerAI(EntityAILookIdle.class, "Look idle",false);
        registerAI(EntityAINearestAttackableTarget.class, "Attack nearest target",true);
        registerAI(EntityAIHurtByTarget.class, "Hurt by target",true);
        registerAI(EntityAIArrowAttack.class, "Range attack",false);
        registerAI(EntityAIBeg.class, "Beg for bone",false);
        registerAI(EntityAIBreakDoor.class, "Breaks doors",false);
        registerAI(EntityAIControlledByPlayer.class, "Controlled by player",false);
        registerAI(EntityAIDefendVillage.class, "Defend village",true);
        registerAI(EntityAIEatGrass.class, "Eat grass",false);
        registerAI(EntityAIFleeSun.class, "Flee sun",false);
        registerAI(EntityAIFollowGolem.class, "Follow Golem",false);
        registerAI(EntityAIFollowOwner.class, "Follow Owner",false);
        registerAI(EntityAIFollowParent.class, "Follow Parent",false);
        registerAI(EntityAILeapAtTarget.class, "Leap when attacking",false);
        registerAI(EntityAILookAtTradePlayer.class, "Look at trading player",false);
        registerAI(EntityAILookAtVillager.class, "Look at villager",false);
        registerAI(EntityAIMate.class, "Mating",false);
        registerAI(EntityAIPanic.class,"Panic",false);
        registerAI(EntityAITempt.class,"Tempted by item",false);
        registerAI(EntityAIMoveIndoors.class,"Move indoors",false);
        registerAI(EntityAIMoveThroughVillage.class,"Move through village",false);
        registerAI(EntityAIMoveTowardsRestriction.class,"Move towards' restriction",false);
        registerAI(EntityAIMoveTowardsTarget.class,"Move towards target",false);
        registerAI(EntityAIOcelotAttack.class, "Ocelot's attack",false);
        registerAI(EntityAIOcelotSit.class, "Ocelot sitting",false);
        registerAI(EntityAIPlay.class, "Play",false);
        registerAI(EntityAIRestrictOpenDoor.class, "Open door restriction",false);
        registerAI(EntityAIRestrictSun.class, "Sun restriction",false);
        registerAI(EntityAIRunAroundLikeCrazy.class, "Run around like crazy",false);
        registerAI(EntityAISit.class, "Sit",false);
        registerAI(EntityAITargetNonTamed.class, "Target when non-tamed",true);
        registerAI(EntityAITradePlayer.class, "Trade with player",false);
        registerAI(EntityAIVillagerMate.class, "Villager mating",false);
        registerAI(EntityAIWatchClosest2.class, "Watch closest entity (2)",false);
        registerAI(EntityAIOpenDoor.class, "Open doors",false);
        registerAI(EntityAIOwnerHurtTarget.class, "On owner hurting target",true);
        registerAI(EntityAIOwnerHurtByTarget.class, "On owner hurt",true);
        
        // =============================
        // Start of custom AI
        // =============================
        registerAI(EntityAIFleeSunEvenNotBurning.class, "Flee sun even not burning",false);
        registerAI(EntityAIFollowEntity.class, "Follow entity", false);
        
        MinecraftForge.EVENT_BUS.register(new EntityEvents());
        NetworkRegistry.INSTANCE.registerGuiHandler(this, guiHandler);
        packetPipeline.initialise();
        
        rewriterItem = new AwesomeAIRewriterItem();
        GameRegistry.registerItem(rewriterItem, "ai_rewriter");
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent evt) 
    {
        packetPipeline.postInitialise();
    }
    
}
