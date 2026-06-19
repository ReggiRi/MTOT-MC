package com.mtot.fabric;

import com.mtot.actions.BuiltinActions;
import com.mtot.core.MTOTManager;
import com.mtot.gui.MTOTControlsScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;

public class MTOTFabricClient implements ClientModInitializer {

    private static final int SETTINGS_KEY = 295;

    private boolean wasSettingsKeyDown = false;

    @Override
    public void onInitializeClient() {
        MTOTManager manager = MTOTManager.getInstance();
        Minecraft mc = Minecraft.getInstance();

        BuiltinActions.init(
            manager.getRegistry(),
            () -> {},
            () -> {
                if (mc.gui != null && mc.gui.getChat() != null) {
                    mc.gui.getChat().clearMessages(false);
                }
            },
            () -> {
                if (!(mc.screen instanceof ChatScreen)) return;
                var recent = mc.gui.getChat().getRecentChat();
                if (recent.isEmpty()) return;
                String last = recent.getLast();
                try {
                    var field = ChatScreen.class.getDeclaredField("input");
                    field.setAccessible(true);
                    var box = (net.minecraft.client.gui.components.EditBox) field.get(mc.screen);
                    box.setValue(last);
                    box.setCursorPosition(last.length());
                } catch (Exception ignored) {}
            }
        );

        var keyManager = new FabricKeyManager(manager);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            keyManager.update();
            manager.tick();

            long window = mc.getWindow().getWindow();
            boolean down = org.lwjgl.glfw.GLFW.glfwGetKey(window, SETTINGS_KEY) == 1;
            if (down && !wasSettingsKeyDown && mc.screen == null) {
                mc.setScreen(new MTOTControlsScreen(null));
            }
            wasSettingsKeyDown = down;
        });
    }
}
