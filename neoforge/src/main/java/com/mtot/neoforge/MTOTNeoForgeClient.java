package com.mtot.neoforge;

import com.mtot.core.MTOTManager;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;

/**
 * Точка входа NeoForge — инициализирует мод и подключает тиковый цикл.
 *
 * @rationale NeoForge использует шину событий (EventBus). Подписываемся на
 *     ClientTickEvent.Post для проверки состояний клавиш каждый тик.
 */
@Mod("mtot")
public class MTOTNeoForgeClient {

    public MTOTNeoForgeClient(IEventBus modBus) {
        NeoForge.EVENT_BUS.addListener(this::onClientTick);
    }

    private void onClientTick(ClientTickEvent.Post event) {
        MTOTManager manager = MTOTManager.getInstance();
        NeoForgeKeyManager.update(manager);
        manager.tick();
    }
}
