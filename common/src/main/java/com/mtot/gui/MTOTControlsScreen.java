package com.mtot.gui;

import com.mtot.api.KeyCombination;
import com.mtot.core.MTOTManager;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Экран настроек привязок MTOT.
 *
 * <p>Отображает все зарегистрированные действия с их комбинациями.
 * Позволяет изменять привязки и сбрасывать к значениям по умолчанию.
 *
 * <p>Открывается через: {@code Options → Controls → More Than One Touch}
 *
 * @rationale Наследует Minecraft Screen для нативной интеграции с ванильными
 *     настройками управления. Список строится динамически из BindingRegistry.
 *
 * @complexity Time: O(n) на инициализацию, O(1) на рендер, где n — число привязок
 */
public class MTOTControlsScreen extends Screen {

    private static final int TITLE_COLOR = 0xFFFFFF;
    private static final int HEADER_Y = 30;
    private static final int LIST_TOP = 50;
    private static final int ROW_HEIGHT = 25;
    private static final int CONTENT_WIDTH = 340;

    private final Screen parent;
    private final List<BindingEntryWidget> entries = new ArrayList<>();
    private BindingEntryWidget capturingWidget;

    public MTOTControlsScreen(Screen parent) {
        super(Component.translatable("mtot.screen.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        entries.clear();
        int centerX = width / 2;
        int y = LIST_TOP;
        MTOTManager manager = MTOTManager.getInstance();

        for (var binding : manager.getAllBindings().entrySet()) {
            String id = binding.getKey();
            KeyCombination combo = binding.getValue();
            Runnable action = manager.getBinding(id).isPresent()
                ? () -> {}
                : null;

            BindingEntryWidget widget = new BindingEntryWidget(
                centerX - CONTENT_WIDTH / 2, y, CONTENT_WIDTH, ROW_HEIGHT,
                id, combo, this
            );
            entries.add(widget);
            addRenderableWidget(widget);
            y += ROW_HEIGHT;
        }

        int resetY = Math.max(y + 10, height - 50);
        addRenderableWidget(
            Button.builder(
                Component.translatable("mtot.screen.reset"),
                btn -> {
                    manager.resetToDefaults();
                    rebuildList();
                }
            )
            .bounds(centerX - 100, resetY, 100, 20)
            .build()
        );

        addRenderableWidget(
            Button.builder(
                Component.translatable("gui.done"),
                btn -> onClose()
            )
            .bounds(centerX, resetY, 100, 20)
            .build()
        );
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        renderBackground(graphics, mouseX, mouseY, delta);
        graphics.drawCenteredString(
            font, title, width / 2, HEADER_Y, TITLE_COLOR
        );
        super.render(graphics, mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (capturingWidget != null) {
            if (keyCode == 256) { // ESC
                capturingWidget.cancelCapture();
                capturingWidget = null;
                return true;
            }
            int glfwMods = captureModifiers();
            capturingWidget.completeCapture(keyCode, glfwMods);
            capturingWidget = null;
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(parent);
    }

    void startCapture(BindingEntryWidget widget) {
        if (capturingWidget != null) {
            capturingWidget.cancelCapture();
        }
        capturingWidget = widget;
    }

    private int captureModifiers() {
        long window = Minecraft.getInstance().getWindow().getWindow();
        int mods = 0;
        if (org.lwjgl.glfw.GLFW.glfwGetKey(window, 340) == 1
            || org.lwjgl.glfw.GLFW.glfwGetKey(window, 344) == 1) {
            mods |= 0x0001;
        }
        if (org.lwjgl.glfw.GLFW.glfwGetKey(window, 341) == 1
            || org.lwjgl.glfw.GLFW.glfwGetKey(window, 345) == 1) {
            mods |= 0x0002;
        }
        if (org.lwjgl.glfw.GLFW.glfwGetKey(window, 342) == 1
            || org.lwjgl.glfw.GLFW.glfwGetKey(window, 346) == 1) {
            mods |= 0x0004;
        }
        return mods;
    }

    private void rebuildList() {
        clearWidgets();
        init();
    }
}
