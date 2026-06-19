package com.mtot.forge;

import com.mtot.core.MTOTManager;
import net.minecraftforge.client.event.ClientTickEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

/**
 * Точка входа Forge — инициализирует мод и подключает тиковый цикл.
 *
 * @rationale Forge 1.20.1 использует MinecraftForge.EVENT_BUS для событий
 *     клиентского тика. Подписываемся на ClientTickEvent.Post.
 */
@Mod("mtot")
public class MTOTForgeClient {

    public MTOTForgeClient() {
        MinecraftForge.EVENT_BUS.addListener(this::onClientTick);
    }

    private void onClientTick(ClientTickEvent.Post event) {
        MTOTManager manager = MTOTManager.getInstance();
        ForgeKeyManager.update(manager);
        manager.tick();
    }
}
