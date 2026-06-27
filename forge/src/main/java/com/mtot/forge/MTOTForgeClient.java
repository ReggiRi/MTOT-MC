package com.mtot.forge;

import com.mtot.actions.BuiltinActions;
import com.mtot.core.MTOTManager;
import com.mtot.gui.MTOTControlsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraftforge.client.event.ClientTickEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod("mtot")
public class MTOTForgeClient {

    private static final int SETTINGS_KEY = 295;
    private boolean wasSettingsKeyDown = false;

    public MTOTForgeClient() {
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

        MinecraftForge.EVENT_BUS.addListener(this::onClientTick);
    }

    private void onClientTick(ClientTickEvent.Post event) {
        MTOTManager manager = MTOTManager.getInstance();
        Minecraft mc = Minecraft.getInstance();
        ForgeKeyManager.update(manager);
        manager.tick();

        long window = mc.getWindow().getWindow();
        boolean down = org.lwjgl.glfw.GLFW.glfwGetKey(window, SETTINGS_KEY) == 1;
        if (down && !wasSettingsKeyDown && mc.screen == null) {
            mc.setScreen(new MTOTControlsScreen(null));
        }
        wasSettingsKeyDown = down;
    }
}
