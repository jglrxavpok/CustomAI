package org.jglrxavpok.mods.customai;

import static org.jglrxavpok.mods.customai.common.CustomAIHelper.registerAI;

import java.util.ArrayList;
import java.util.Iterator;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.command.CommandException;
import net.minecraft.command.server.CommandSummon;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIBeg;
import net.minecraft.entity.ai.EntityAIBreakDoor;
import net.minecraft.entity.ai.EntityAIControlledByPlayer;
import net.minecraft.entity.ai.EntityAICreeperSwell;
import net.minecraft.entity.ai.EntityAIDefendVillage;
import net.minecraft.entity.ai.EntityAIDoorInteract;
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
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAITradePlayer;
import net.minecraft.entity.ai.EntityAIVillagerMate;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CommandEvent;

import org.jglrxavpok.mods.customai.ai.ArrowProjectile;
import org.jglrxavpok.mods.customai.ai.EntityAIFleeSunEvenNotBurning;
import org.jglrxavpok.mods.customai.ai.EntityAIFollowEntity;
import org.jglrxavpok.mods.customai.ai.EntityAIRangeAttack;
import org.jglrxavpok.mods.customai.ai.EntityAITeleportRandomly;
import org.jglrxavpok.mods.customai.ai.Projectile;
import org.jglrxavpok.mods.customai.ai.WitherSkullProjectile;
import org.jglrxavpok.mods.customai.common.BlockAIEmitter;
import org.jglrxavpok.mods.customai.common.CommandTestForMod;
import org.jglrxavpok.mods.customai.common.ConfigHandler;
import org.jglrxavpok.mods.customai.common.CustomAIGuiHandler;
import org.jglrxavpok.mods.customai.common.CustomAIHelper;
import org.jglrxavpok.mods.customai.common.EntityEvents;
import org.jglrxavpok.mods.customai.common.Proxy;
import org.jglrxavpok.mods.customai.common.TileEntityAIEmitter;
import org.jglrxavpok.mods.customai.common.aifactory.EntityAIFactory;
import org.jglrxavpok.mods.customai.common.aifactory.EntityAIVanillaWorker;
import org.jglrxavpok.mods.customai.common.aifactory.EntityCustomAIAddedWorker;
import org.jglrxavpok.mods.customai.items.AICopierItem;
import org.jglrxavpok.mods.customai.items.AwesomeAIRewriterItem;
import org.jglrxavpok.mods.customai.json.JSONObject;
import org.jglrxavpok.mods.customai.netty.AbstractPacket;
import org.jglrxavpok.mods.customai.netty.PacketPipeline;

/**
 * Version number changed automatically by Gradle
 * @author jglrxavpok
 *
 */
@Mod(modid=ModCustomAI.MODID, name = "Custom AI", version = "@@MOD.VERSION@@")
public class ModCustomAI
{

    public static final PacketPipeline packetPipeline = new PacketPipeline("org.jglrxavpok.mods.customai.netty", AbstractPacket.class);
    public static final String MODID = "customai";
    private IGuiHandler guiHandler = new CustomAIGuiHandler();
    public static ConfigHandler config;
    
    @Instance(MODID)
    public static ModCustomAI instance;
    
    @SidedProxy(clientSide="org.jglrxavpok.mods.customai.client.ClientProxy", serverSide="org.jglrxavpok.mods.customai.common.Proxy")
    public static Proxy proxy;
    
    public static BlockAIEmitter aiEmitterBlock;
    public static AwesomeAIRewriterItem rewriterItem;
    public static AICopierItem copierItem;
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        registerAI(EntityAISwimming.class, "Swim",false,0);
        registerAI(EntityAICreeperSwell.class, "Creeper swell",false,0);
        registerAI(EntityAIAvoidEntity.class, "Avoid entity",false,1);
        registerAI(EntityAIAttackOnCollide.class, "Attack by collision",false,1);
        registerAI(EntityAILookIdle.class, "Look idle",false,0);
        registerAI(EntityAINearestAttackableTarget.class, "Attack nearest target",true,0,EntityAITarget.class);
        registerAI(EntityAIHurtByTarget.class, "Hurt by target",true,0,EntityAITarget.class);
        registerAI(EntityAIArrowAttack.class, "Special range attack",false,0);
        registerAI(EntityAIBeg.class, "Beg for bone",false,0);
        registerAI(EntityAIBreakDoor.class, "Breaks doors",false,0,EntityAIDoorInteract.class);
        registerAI(EntityAIControlledByPlayer.class, "Controlled by player",false,0);
        registerAI(EntityAIDefendVillage.class, "Defend village",true,0);
        registerAI(EntityAIEatGrass.class, "Eat grass",false,0);
        registerAI(EntityAIFleeSun.class, "Flee sun",false,0);
        registerAI(EntityAIFollowGolem.class, "Follow Golem",false,0);
        registerAI(EntityAIFollowOwner.class, "Follow Owner",false,0);
        registerAI(EntityAIFollowParent.class, "Follow Parent",false,0);
        registerAI(EntityAILeapAtTarget.class, "Leap when attacking",false,0);
        registerAI(EntityAILookAtTradePlayer.class, "Look at trading player",false,0,EntityAIWatchClosest.class);
        registerAI(EntityAILookAtVillager.class, "Look at villager",false,0);
        registerAI(EntityAIMate.class, "Mating",false,0);
        registerAI(EntityAIPanic.class,"Panic",false,0);
        registerAI(EntityAITempt.class,"Tempted by item",false,0);
        registerAI(EntityAIMoveIndoors.class,"Move indoors",false,0);
        registerAI(EntityAIMoveThroughVillage.class,"Move through village",false,0);
        registerAI(EntityAIMoveTowardsRestriction.class,"Move towards' restriction",false,0);
        registerAI(EntityAIMoveTowardsTarget.class,"Move towards target",false,0);
        registerAI(EntityAIOcelotAttack.class, "Ocelot's attack",false,1);
        registerAI(EntityAIOcelotSit.class, "Ocelot sitting",false,0);
        registerAI(EntityAIOpenDoor.class, "Open doors",false,0,EntityAIDoorInteract.class);
        registerAI(EntityAIOwnerHurtTarget.class, "On owner hurting target",true,0,EntityAITarget.class);
        registerAI(EntityAIOwnerHurtByTarget.class, "On owner hurt",true,0,EntityAITarget.class);
        registerAI(EntityAIPlay.class, "Play",false,0);
        registerAI(EntityAIRestrictOpenDoor.class, "Open door restriction",false,0);
        registerAI(EntityAIRestrictSun.class, "Sun restriction",false,0);
        registerAI(EntityAIRunAroundLikeCrazy.class, "Run around like crazy",false,0);
        registerAI(EntityAISit.class, "Sit",false,0);
        registerAI(EntityAITargetNonTamed.class, "Target when non-tamed",true,0);
        registerAI(EntityAITradePlayer.class, "Trade with player",false,0);
        registerAI(EntityAIVillagerMate.class, "Villager mating",false,0);
        registerAI(EntityAIWander.class, "Wander",false,0);
        registerAI(EntityAIWatchClosest.class, "Watch closest entity",false,0);
        registerAI(EntityAIWatchClosest2.class, "Watch closest entity (2)",false,0,EntityAIWatchClosest.class);
        
        // =============================
        // Start of custom AI
        // =============================
        registerAI(EntityAIFleeSunEvenNotBurning.class, "Flee sun even not burning",false,0);
        registerAI(EntityAIFollowEntity.class, "Follow entity", false,1);
        registerAI(EntityAITeleportRandomly.class, "Teleport randomly", false,0);
        registerAI(EntityAIRangeAttack.class, "General range attack", false,0);
        // =============================
        // End of custom AI
        // =============================

        TileEntityAIEmitter.registerItems(EntityChicken.class, new Object[]{Items.feather, Items.chicken, Items.cooked_chicken});
        TileEntityAIEmitter.registerItems(EntityCow.class, new Object[]{Items.leather, Items.beef, Items.cooked_beef});
        TileEntityAIEmitter.registerItems(EntityCreeper.class, new Object[]{Items.gunpowder});
        TileEntityAIEmitter.registerItems(EntityPig.class, new Object[]{Items.porkchop, Items.cooked_porkchop,Items.baked_potato, Items.potato});
        TileEntityAIEmitter.registerItems(EntitySheep.class, new Object[]{Blocks.wool, Blocks.carpet});
        TileEntityAIEmitter.registerItems(EntitySkeleton.class, new Object[]{Items.bow, Items.arrow});
        TileEntityAIEmitter.registerItems(EntityWolf.class, new Object[]{Items.bone});
        TileEntityAIEmitter.registerItems(EntityWitch.class, new Object[]{Items.potionitem});
        TileEntityAIEmitter.registerItems(EntityZombie.class, new Object[]{Items.rotten_flesh, Items.poisonous_potato});
        
        Projectile.register("Arrows", ArrowProjectile.class);
        Projectile.register("Wither Skulls", WitherSkullProjectile.class);
        
        MinecraftForge.EVENT_BUS.register(new EntityEvents());
        MinecraftForge.EVENT_BUS.register(this);
        NetworkRegistry.INSTANCE.registerGuiHandler(this, guiHandler);
        packetPipeline.initialise();
        
        rewriterItem = new AwesomeAIRewriterItem();
        GameRegistry.registerItem(rewriterItem, "ai_rewriter");
        
        aiEmitterBlock = new BlockAIEmitter();
        GameRegistry.registerBlock(aiEmitterBlock, "ai_emitter");
        GameRegistry.registerTileEntity(TileEntityAIEmitter.class, "AIEmitterTileEntity");
        
        copierItem = new AICopierItem();
        GameRegistry.registerItem(copierItem, "ai_copier");
        Object[] rewriterCraft = new Object[]
                {
                ""+' '+'P'+' ',
                ""+'P'+'N'+'P',
                ""+' '+'P'+' ',
                
                'N', Items.nether_star,
                'P', Items.paper
                };
        
        Object[] emitterCraft = new Object[]
                {
                ""+'G'+'G'+'G',
                ""+'G'+'X'+'G',
                ""+'L'+'L'+'L',
                
                'G', Blocks.glass,
                'L', Blocks.lapis_block,
                'X', Blocks.glowstone
                };
        
        GameRegistry.addRecipe(new ItemStack(rewriterItem,1), rewriterCraft);
        GameRegistry.addRecipe(new ItemStack(aiEmitterBlock,1), emitterCraft);
        
        proxy.registerStuff();
        Class[] vanillaClasses = new Class[]
            {
                EntityAISwimming.class,
                EntityAICreeperSwell.class,
                EntityAIAvoidEntity.class,
                EntityAIAttackOnCollide.class,
                EntityAILookIdle.class,
                EntityAINearestAttackableTarget.class,
                EntityAIArrowAttack.class,
                EntityAIBeg.class,
                EntityAIBreakDoor.class,
                EntityAIControlledByPlayer.class,
                EntityAIDefendVillage.class,
                EntityAIEatGrass.class,
                EntityAIFleeSun.class,
                EntityAIFollowGolem.class,
                EntityAIFollowOwner.class,
                EntityAIFollowParent.class,
                EntityAIHurtByTarget.class,
                EntityAILeapAtTarget.class,
                EntityAILookAtTradePlayer.class,
                EntityAIMate.class,
                EntityAIPanic.class,
                EntityAITempt.class,
                EntityAIMoveIndoors.class,
                EntityAIMoveThroughVillage.class,
                EntityAIMoveTowardsRestriction.class,
                EntityAIMoveTowardsTarget.class,
                EntityAIOcelotAttack.class,
                EntityAIOcelotSit.class,
                EntityAIOpenDoor.class,
                EntityAIOwnerHurtTarget.class,
                EntityAIOwnerHurtByTarget.class,
                EntityAIPlay.class,
                EntityAIRestrictOpenDoor.class,
                EntityAIRestrictSun.class,
                EntityAIRunAroundLikeCrazy.class,
                EntityAISit.class,
                EntityAITargetNonTamed.class,
                EntityAITradePlayer.class,
                EntityAIVillagerMate.class,
                EntityAIWander.class,
                EntityAIWatchClosest.class,
                EntityAIWatchClosest2.class,
            };
        EntityAIFactory.hireWorker(new EntityAIVanillaWorker(), vanillaClasses);
        
        Class[] addedClasses = new Class[]
            {
                EntityAIFleeSunEvenNotBurning.class,
                EntityAIFollowEntity.class,
                EntityAITeleportRandomly.class,
                EntityAIRangeAttack.class
            };
        EntityAIFactory.hireWorker(new EntityCustomAIAddedWorker(), addedClasses);
        
      /*  if(event.getSide().isClient())
        {
            File file = new File(event.getSuggestedConfigurationFile().getParentFile(), "AITasks.cvs");
            try
            {
                if(!file.exists())
                    file.createNewFile();
                FileOutputStream out = new FileOutputStream(file);
                String content = CustomAIHelper.getCVSList();
                out.write(content.getBytes());
                out.flush();
                out.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }*/
        
        config = new ConfigHandler(event.getSuggestedConfigurationFile());
        config.save();
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent evt) 
    {
        packetPipeline.postInitialise();
    }
    
    public static String getTranslation(String string)
    {
        return getTranslation(string, true);
    }
    
    public static String getTranslation(String string, boolean format)
    {
        String s = StatCollector.translateToLocal(string);
        if(s == null)
            s = StatCollector.translateToFallback(string);
        
        if(format)
            return format(s);
        return s;
    }

    public static String format(String s)
    {
        return s.replace("${AIEmitter}", getTranslation("tile.ai_emitter.name", false));
    }
    
    @SuppressWarnings("unchecked")
    @SubscribeEvent
    public void onCommandEvent(CommandEvent e)
    {
        if(e.command instanceof CommandSummon)
        {
            NBTTagCompound nbttagcompound = new NBTTagCompound();

            if (e.parameters.length >= 5)
            {
                IChatComponent ichatcomponent = CommandSummon.func_147178_a(e.sender, e.parameters, 4);

                try
                {
                    NBTBase nbtbase = JsonToNBT.func_150315_a(ichatcomponent.getUnformattedText());

                    if (!(nbtbase instanceof NBTTagCompound))
                    {
                        CommandSummon.notifyAdmins(e.sender, "commands.summon.tagError", new Object[] {"Not a valid tag"});
                        return;
                    }

                    nbttagcompound = (NBTTagCompound)nbtbase;
                    
                    if(nbttagcompound.hasKey("CustomAITasks"))
                    {
                        try
                        {
                            NBTTagList array = (NBTTagList) nbttagcompound.getTag("CustomAITasks");
                            for(int i = 0;i<array.tagCount();i++)
                            {
                                NBTTagCompound current = array.getCompoundTagAt(i);
                                if(!current.hasKey("type"))
                                {
                                    e.exception = new CommandException("[Custom AI] Missing AI task type");
                                    e.setCanceled(true);
                                    return;
                                }
                                Class<? extends EntityAIBase> clazz = CustomAIHelper.tryToGetAITaskFromName(current.getString("type"));
                                if(clazz == null)
                                {
                                    clazz = (Class<? extends EntityAIBase>) Class.forName(current.getString("type"));
                                }
                                JSONObject object = CustomAIHelper.createDummyJSON(clazz);
                                Iterator<String> it = object.keys();
                                boolean flag1 = true;
                                ArrayList<String> missing = new ArrayList<String>();
                                while(it.hasNext())
                                {
                                    String obj = it.next();
                                    if(!current.hasKey(obj))
                                    {
                                        flag1 = false;
                                        missing.add(obj);
                                    }
                                }
                                if(!flag1)
                                {
                                    String s1 = "";
                                    int index = 0;
                                    for(String s2 : missing)
                                    {
                                        s1 += (index == 0 ? "" : ",") + s2;
                                        index++;
                                    }
                                    e.exception = new CommandException("[Custom AI] Missing tags in tasks: "+s1+" for tag \""+CustomAIHelper.getNameForTask(clazz)+"\"");
                                    e.setCanceled(true);
                                }
                            }
                        }
                        catch(Exception ex)
                        {
                            e.exception = ex;
                            e.setCanceled(true);
                        }
                    }
                    if(nbttagcompound.hasKey("CustomAITargetTasks"))
                    {
                        try
                        {
                            NBTTagList array = (NBTTagList) nbttagcompound.getTag("CustomAITargetTasks");
                            for(int i = 0;i<array.tagCount();i++)
                            {
                                NBTTagCompound current = array.getCompoundTagAt(i);
                                if(!current.hasKey("type"))
                                {
                                    e.exception = new CommandException("[Custom AI] Missing AI task type");
                                    e.setCanceled(true);
                                    return;
                                }
                                Class<? extends EntityAIBase> clazz = (Class<? extends EntityAIBase>) Class.forName(current.getString("type"));
                                JSONObject object = CustomAIHelper.createDummyJSON(clazz);
                                Iterator<String> it = object.keys();
                                boolean flag1 = true;
                                ArrayList<String> missing = new ArrayList<String>();
                                while(it.hasNext())
                                {
                                    String obj = it.next();
                                    if(!current.hasKey(obj))
                                    {
                                        flag1 = false;
                                        missing.add(obj);
                                    }
                                }
                                if(!flag1)
                                {
                                    String s1 = "";
                                    int index = 0;
                                    for(String s2 : missing)
                                    {
                                        s1 += (index == 0 ? "" : ",") + s2;
                                        index++;
                                    }
                                    e.exception = new CommandException("[Custom AI] Missing tags in target tasks: "+s1+" for tag \""+CustomAIHelper.getNameForTask(clazz)+"\"");
                                    e.setCanceled(true);
                                }
                            }
                        }
                        catch(Exception ex)
                        {
                            e.exception = ex;
                            e.setCanceled(true);
                        }
                    }
                }
                catch (NBTException nbtexception)
                {
                    e.exception = nbtexception;
                    e.setCanceled(true);
                }
            }
        }
    }
    
    @EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
        // event.registerServerCommand(new CommandSetAI()); // TODO: Finish SetAI command (or scrap it ?)
        event.registerServerCommand(new CommandTestForMod());
    }
    
}
