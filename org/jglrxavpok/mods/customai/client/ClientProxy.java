package org.jglrxavpok.mods.customai.client;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

import org.jglrxavpok.mods.customai.common.Proxy;
import org.jglrxavpok.mods.customai.common.TileEntityAIEmitter;

public class ClientProxy extends Proxy
{

    public static AIEmitterRenderer aiEmitterRenderer;
    public static int renderID;

    public void registerStuff()
    {
        renderID = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new AIEmitterRenderHandler());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAIEmitter.class, aiEmitterRenderer = new AIEmitterRenderer());
    }
}
