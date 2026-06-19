package com.mtot.fabric.mixin;

import com.mtot.api.KeyCombination;
import com.mtot.api.Modifier;
import com.mtot.core.MTOTManager;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {

    @Shadow
    protected EditBox input;

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void onKeyPressed(int keyCode, int scanCode, int modifiers,
                              CallbackInfoReturnable<Boolean> cir) {
        if (keyCode != 257 || (modifiers & 1) == 0) return;

        MTOTManager manager = MTOTManager.getInstance();
        KeyCombination shiftEnter = KeyCombination.of(257, Modifier.SHIFT);

        for (var entry : manager.getAllBindings().entrySet()) {
            if (entry.getValue().equals(shiftEnter)) {
                String current = input.getValue();
                int cursor = input.getCursorPosition();
                input.setValue(current.substring(0, cursor) + "\n"
                    + current.substring(cursor));
                input.setCursorPosition(cursor + 1);
                cir.setReturnValue(true);
                return;
            }
        }
    }
}
