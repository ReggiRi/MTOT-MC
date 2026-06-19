package com.mtot.fabric;

import com.mtot.core.MTOTManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

/**
 * Точка входа Fabric — инициализирует мод и подключает тиковый цикл.
 *
 * @rationale Fabric API предоставляет ClientTickEvents для hook'а в клиентский
 *     тик. Других Fabric-специфичных зависимостей не требуется — вся логика в common.
 */
public class MTOTFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        MTOTManager manager = MTOTManager.getInstance();
        FabricKeyManager keyManager = new FabricKeyManager(manager);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            keyManager.update();
            manager.tick();
        });
    }
}
