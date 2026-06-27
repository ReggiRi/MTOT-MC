package com.mtot.neoforge;

import com.mtot.actions.BuiltinActions;
import com.mtot.core.MTOTManager;
import com.mtot.gui.MTOTControlsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod("mtot")
public class MTOTNeoForgeClient {

    private static final int SETTINGS_KEY = 295;
    private boolean wasSettingsKeyDown = false;

    public MTOTNeoForgeClient(IEventBus modBus) {
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

        NeoForge.EVENT_BUS.addListener(this::onClientTick);
    }

    private void onClientTick(ClientTickEvent.Post event) {
        MTOTManager manager = MTOTManager.getInstance();
        Minecraft mc = Minecraft.getInstance();
        NeoForgeKeyManager.update(manager);
        manager.tick();

        long window = mc.getWindow().getWindow();
        boolean down = org.lwjgl.glfw.GLFW.glfwGetKey(window, SETTINGS_KEY) == 1;
        if (down && !wasSettingsKeyDown && mc.screen == null) {
            mc.setScreen(new MTOTControlsScreen(null));
        }
        wasSettingsKeyDown = down;
    }
}
