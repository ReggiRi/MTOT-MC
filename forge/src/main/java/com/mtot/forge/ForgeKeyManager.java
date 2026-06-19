package com.mtot.forge;

import static org.lwjgl.glfw.GLFW.*;

import com.mtot.core.MTOTManager;
import net.minecraft.client.Minecraft;

/**
 * Управляет состоянием клавиш через GLFW на Forge.
 *
 * @rationale Аналогичен NeoForgeKeyManager, но для Forge 1.20.1.
 *     Использует Minecraft.getInstance() как точку входа.
 */
public final class ForgeKeyManager {

    private static final int[] MODIFIER_KEYS = {340, 341, 342, 344, 345, 346};

    private ForgeKeyManager() {}

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
