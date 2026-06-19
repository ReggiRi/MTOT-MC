package com.mtot.gui;

import com.mtot.api.KeyCombination;
import com.mtot.api.Modifier;
import com.mtot.core.MTOTManager;
import java.util.EnumSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

/**
 * Виджет строки привязки в списке настроек.
 *
 * <p>Отображает ID действия, текущую комбинацию и кнопку [Change].
 * При клике переходит в режим захвата клавиш.
 *
 * @rationale Кастомный виджет вместо готового Button для точного контроля
 *     расположения элементов в строке (ID слева, комбинация по центру, Change справа).
 *
 * @complexity Time: O(1), Memory: O(1)
 */
public class BindingEntryWidget extends AbstractWidget {

    private static final int LABEL_LEFT = 5;
    private static final int COMBO_LEFT = 120;
    private static final int CHANGE_BTN_RIGHT = 5;
    private static final int CHANGE_BTN_WIDTH = 60;
    private static final int TEXT_COLOR = 0xFFFFFF;
    private static final int TEXT_COLOR_DIM = 0x888888;
    private static final int CONFLICT_COLOR = 0xFF5555;
    private static final int CAPTURE_BG = 0x55FFAA00;

    private final String actionId;
    private final MTOTControlsScreen parent;

    private boolean capturing;
    private KeyCombination currentCombo;

    public BindingEntryWidget(
            int x, int y, int width, int height,
            String actionId, KeyCombination combo,
            MTOTControlsScreen parent) {
        super(x, y, width, height, Component.literal(actionId));
        this.actionId = actionId;
        this.currentCombo = combo;
        this.parent = parent;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        int btnLeft = getX() + width - CHANGE_BTN_RIGHT - CHANGE_BTN_WIDTH;
        if (mouseX >= btnLeft && mouseX <= btnLeft + CHANGE_BTN_WIDTH) {
            capturing = true;
            parent.startCapture(this);
        }
    }

    /**
     * Завершает захват комбинацией.
     *
     * @param keyCode  GLFW код нажатой клавиши
     * @param glfwMods GLFW mod-флаги
     */
    public void completeCapture(int keyCode, int glfwMods) {
        EnumSet<Modifier> mods = EnumSet.noneOf(Modifier.class);
        if ((glfwMods & 0x0001) != 0) mods.add(Modifier.SHIFT);
        if ((glfwMods & 0x0002) != 0) mods.add(Modifier.CONTROL);
        if ((glfwMods & 0x0004) != 0) mods.add(Modifier.ALT);

        KeyCombination newCombo = KeyCombination.of(
            keyCode, mods.toArray(new Modifier[0])
        );

        MTOTManager.getInstance().bind(actionId, newCombo);
        currentCombo = newCombo;
        capturing = false;
    }

    /**
     * Отменяет захват без сохранения.
     */
    public void cancelCapture() {
        capturing = false;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        int y = getY();
        int right = getX() + width;

        if (capturing) {
            graphics.fill(getX(), y, right, y + height, CAPTURE_BG);
        }

        Font font = Minecraft.getInstance().font;

        String idLabel = actionId.length() > 20
            ? actionId.substring(0, 18) + "..."
            : actionId;
        graphics.drawString(font, idLabel, getX() + LABEL_LEFT,
            y + (height - 8) / 2, TEXT_COLOR_DIM);

        String comboLabel = currentCombo != null
            ? currentCombo.getDisplayName()
            : "—";
        if (capturing) {
            comboLabel = "Press a key...";
        }
        graphics.drawString(font, comboLabel, getX() + COMBO_LEFT,
            y + (height - 8) / 2,
            capturing ? CONFLICT_COLOR : TEXT_COLOR);

        int btnLeft = right - CHANGE_BTN_RIGHT - CHANGE_BTN_WIDTH;
        boolean hovered = mouseX >= btnLeft && mouseX <= right - CHANGE_BTN_RIGHT
            && mouseY >= y && mouseY <= y + height;
        int btnColor = hovered ? 0xFF4444EE : 0xFF3333CC;
        graphics.fill(btnLeft, y + 2, right - CHANGE_BTN_RIGHT,
            y + height - 2, btnColor);
        String btnLabel = I18n.get(capturing
            ? "mtot.screen.cancel" : "mtot.screen.change");
        graphics.drawString(font, btnLabel,
            btnLeft + (CHANGE_BTN_WIDTH - font.width(btnLabel)) / 2,
            y + (height - 8) / 2, TEXT_COLOR);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        output.add(NarratedElementType.TITLE,
            Component.literal(actionId + " " + currentCombo));
    }
}
