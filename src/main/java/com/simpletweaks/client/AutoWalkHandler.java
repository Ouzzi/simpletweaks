package com.simpletweaks.client;

import com.simpletweaks.Simpletweaks;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class AutoWalkHandler {

    private static KeyBinding autoWalkKey;
    private static boolean autoWalkEnabled = false;

    // Kategorie einmalig erstellen, um Abstürze durch doppelte Registrierung zu vermeiden
    private static final KeyBinding.Category CATEGORY = KeyBinding.Category.create(Identifier.of(Simpletweaks.MOD_ID, "general"));

    public static void register() {
        // 1. Keybinding registrieren (Standard: R)
        // Wir nutzen den 4-Argumente-Konstruktor und übergeben unser Category-Objekt
        autoWalkKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.simpletweaks.autowalk",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                CATEGORY
        ));

        // 2. Tick Event registrieren
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            // Config Check
            if (!Simpletweaks.getConfig().tweaks.enableAutowalk) {
                autoWalkEnabled = false;
                return;
            }

            // Toggle Logik
            while (autoWalkKey.wasPressed()) {
                autoWalkEnabled = !autoWalkEnabled;

                // Optional: Statusnachricht an Spieler
                Text message = Text.literal("Auto-Walk: ")
                        .append(Text.literal(autoWalkEnabled ? "ON" : "OFF")
                                .formatted(autoWalkEnabled ? Formatting.GREEN : Formatting.RED));
                client.player.sendMessage(message, true);

                // FIX: Wenn ausgeschaltet, Taste explizit loslassen!
                // Sonst "klebt" die Taste fest, bis man sie manuell drückt.
                if (!autoWalkEnabled) {
                    client.options.forwardKey.setPressed(false);
                }
            }

            // Bewegung erzwingen
            if (autoWalkEnabled) {
                // Wenn der Spieler manuell rückwärts drückt, stoppen wir AutoWalk oft zur Sicherheit,
                // aber hier überschreiben wir einfach die Vorwärts-Taste.
                client.options.forwardKey.setPressed(true);
            }
        });
    }
}