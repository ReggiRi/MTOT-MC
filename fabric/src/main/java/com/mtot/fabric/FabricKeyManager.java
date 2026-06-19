package com.mtot.fabric;

import static org.lwjgl.glfw.GLFW.*;

import com.mtot.core.MTOTManager;
import net.minecraft.client.MinecraftClient;

/**
 * Управляет состоянием клавиш через GLFW на Fabric.
 *
 * <p>На каждый тик опрашивает GLFW о состоянии клавиш, используемых в
 * зарегистрированных привязках, и обновляет {@link MTOTManager}.
 *
 * @rationale Fabric не предоставляет событий нажатия клавиш в удобном виде,
 *     поэтому используем прямой опрос GLFW. Это эффективно (O(n) на тик) и
 *     совместимо со всеми версиями Fabric API.
 */
public class FabricKeyManager {

    private static final int[] MODIFIER_KEYS = {340, 341, 342, 344, 345, 346};

    private final MTOTManager manager;

    public FabricKeyManager(MTOTManager manager) {
        this.manager = manager;
    }

    /**
     * Опрашивает GLFW и обновляет состояния клавиш.
     */
    public void update() {
        long window = MinecraftClient.getInstance().getWindow().getHandle();
        for (var entry : manager.getAllBindings().entrySet()) {
            int keyCode = entry.getValue().getKeyCode();
            boolean pressed = glfwGetKey(window, keyCode) == GLFW_PRESS;
            manager.setKeyState(keyCode, pressed);
        }
        for (int modKey : MODIFIER_KEYS) {
            boolean pressed = glfwGetKey(window, modKey) == GLFW_PRESS;
            manager.setKeyState(modKey, pressed);
        }
    }
}
