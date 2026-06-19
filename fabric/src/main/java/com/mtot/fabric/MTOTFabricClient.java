package com.mtot.fabric;

import com.mtot.actions.BuiltinActions;
import com.mtot.core.MTOTManager;
import com.mtot.gui.MTOTControlsScreen;
import java.lang.reflect.Field;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;

public class MTOTFabricClient implements ClientModInitializer {

    private static final int SETTINGS_KEY = 292;

    private static final Field CHAT_INPUT_FIELD;

    static {
        Field f;
        try {
            f = ChatScreen.class.getDeclaredField("input");
            f.setAccessible(true);
        } catch (NoSuchFieldException e) {
            f = null;
        }
        CHAT_INPUT_FIELD = f;
    }

    private boolean wasSettingsKeyDown = false;

    @Override
    public void onInitializeClient() {
        MTOTManager manager = MTOTManager.getInstance();
        Minecraft mc = Minecraft.getInstance();

        BuiltinActions.init(
            manager.getRegistry(),
            () -> insertNewline(mc),
            () -> {
                if (mc.gui != null && mc.gui.getChat() != null) {
                    mc.gui.getChat().clearMessages(false);
                }
            },
            () -> repeatLast(mc)
        );

        FabricKeyManager keyManager = new FabricKeyManager(manager);
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

    private static void insertNewline(Minecraft mc) {
        if (!(mc.screen instanceof ChatScreen chat)) return;
        if (CHAT_INPUT_FIELD == null) return;
        try {
            EditBox box = (EditBox) CHAT_INPUT_FIELD.get(chat);
            String current = box.getValue();
            int cursor = box.getCursorPosition();
            box.setValue(current.substring(0, cursor) + "\n" + current.substring(cursor));
            box.setCursorPosition(cursor + 1);
        } catch (IllegalAccessException e) {
            // ignore
        }
    }

    private static void repeatLast(Minecraft mc) {
        if (!(mc.screen instanceof ChatScreen)) return;
        if (CHAT_INPUT_FIELD == null) return;
        var recent = mc.gui.getChat().getRecentChat();
        if (recent.isEmpty()) return;
        String last = recent.getLast();
        try {
            EditBox box = (EditBox) CHAT_INPUT_FIELD.get(mc.screen);
            box.setValue(last);
            box.setCursorPosition(last.length());
        } catch (IllegalAccessException e) {
            // ignore
        }
    }
}
