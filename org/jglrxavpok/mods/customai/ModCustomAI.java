package org.jglrxavpok.mods.customai;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraftforge.common.MinecraftForge;

import org.jglrxavpok.mods.customai.common.CustomAIGuiHandler;
import org.jglrxavpok.mods.customai.common.EntityEvents;
import org.jglrxavpok.mods.customai.items.AwesomeAIRewriterItem;
import org.jglrxavpok.mods.customai.netty.PacketPipeline;

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
