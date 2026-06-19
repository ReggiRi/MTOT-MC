package com.mtot.neoforge;

import static org.lwjgl.glfw.GLFW.*;

import com.mtot.core.MTOTManager;
import net.minecraft.client.Minecraft;

/**
 * Управляет состоянием клавиш через GLFW на NeoForge.
 *
 * @rationale Аналогичен FabricKeyManager, но вызывается из NeoForge EventBus.
 *     Статический метод для простоты — экземпляр не требуется.
 */
public final class NeoForgeKeyManager {

    private static final int[] MODIFIER_KEYS = {340, 341, 342, 344, 345, 346};

    private NeoForgeKeyManager() {}

    /**
     * Опрашивает GLFW и обновляет состояния клавиш.
     *
     * @param manager экземпляр MTOTManager
     */
    public static void update(MTOTManager manager) {
        long window = Minecraft.getInstance().getWindow().getWindow();
        for (var entry : manager.getAllBindings().entrySet()) {
            int keyCode = entry.getValue().getKeyCode();
            manager.setKeyState(keyCode, glfwGetKey(window, keyCode) == GLFW_PRESS);
        }
        for (int modKey : MODIFIER_KEYS) {
            manager.setKeyState(modKey, glfwGetKey(window, modKey) == GLFW_PRESS);
        }
    }
}
