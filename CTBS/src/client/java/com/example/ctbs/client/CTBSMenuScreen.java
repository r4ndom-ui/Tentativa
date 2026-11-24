package com.example.ctbs.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.Locale;

public final class CTBSMenuScreen extends Screen {
    private static final int PANEL = 0xF0181B27;
    private static final int PANEL_INNER = 0xFF222838;
    private static final int BORDER = 0xFF59657A;
    private static final int ACCENT = 0xFFC69A58;
    private static final int MUTED = 0xFFADB5C4;

    private TextFieldWidget speciesField;
    private TextFieldWidget capacityField;
    private CapacitySlider capacitySlider;
    private boolean syncingCapacity;

    public CTBSMenuScreen() {
        super(Text.translatable("screen.ctbs.title"));
    }

    @Override
    protected void init() {
        int center = width / 2;
        int left = center - 150;
        int top = Math.max(24, height / 2 - 121);

        speciesField = addDrawableChild(new TextFieldWidget(textRenderer, left + 18, top + 58, 174, 20, Text.literal("Espécie")));
        speciesField.setText("eevee");
        speciesField.setMaxLength(48);

        capacityField = addDrawableChild(new TextFieldWidget(textRenderer, left + 204, top + 58, 76, 20, Text.literal("Capacidade")));
        capacityField.setText("2");
        capacityField.setMaxLength(6);
        capacityField.setChangedListener(this::onCapacityTextChanged);

        capacitySlider = addDrawableChild(new CapacitySlider(left + 18, top + 91, 262, 20, 2));

        addDrawableChild(ButtonWidget.builder(Text.literal("Aplicar capacidade"), button -> saveCapacity())
            .dimensions(left + 18, top + 119, 262, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("RETER"), button -> send("ctbs stomach mode hold"))
            .dimensions(left + 18, top + 157, 82, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("CURAR"), button -> send("ctbs stomach mode heal"))
            .dimensions(left + 109, top + 157, 82, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("DIGERIR"), button -> send("ctbs stomach mode digest"))
            .dimensions(left + 200, top + 157, 80, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("SENTAR"), button -> send("ctbs action sit"))
            .dimensions(left + 18, top + 185, 82, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("DEITAR"), button -> send("ctbs action lie"))
            .dimensions(left + 109, top + 185, 82, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("REPOUSAR"), button -> send("ctbs action nap"))
            .dimensions(left + 200, top + 185, 80, 20).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Fechar"), button -> close())
            .dimensions(left + 18, top + 213, 262, 20).build());
    }

    private void onCapacityTextChanged(String value) {
        if (syncingCapacity || capacitySlider == null) return;
        if (value.matches("[0-9]{1,6}")) {
            int parsed = Integer.parseInt(value);
            if (parsed <= 100000) {
                syncingCapacity = true;
                capacitySlider.setCapacity(parsed);
                syncingCapacity = false;
            }
        }
    }

    private void onSliderChanged(int value) {
        if (capacityField == null || syncingCapacity) return;
        syncingCapacity = true;
        capacityField.setText(Integer.toString(value));
        syncingCapacity = false;
    }

    private void saveCapacity() {
        String species = speciesField.getText().trim().toLowerCase(Locale.ROOT);
        String value = capacityField.getText().trim();
        if (!species.matches("[a-z0-9_]+") || !value.matches("[0-9]{1,6}")) return;
        int parsed = Integer.parseInt(value);
        if (parsed > 100000) return;
        send("ctbs capacity set " + species + " " + parsed);
    }

    private void send(String command) {
        if (client != null && client.player != null) client.player.networkHandler.sendChatCommand(command);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        int center = width / 2;
        int left = center - 150;
        int top = Math.max(24, height / 2 - 121);

        context.fill(left, top, left + 300, top + 242, PANEL);
        context.fill(left + 1, top + 1, left + 299, top + 31, PANEL_INNER);
        context.fill(left + 1, top + 32, left + 299, top + 33, ACCENT);
        context.drawBorder(left, top, 300, 242, BORDER);

        context.drawCenteredTextWithShadow(textRenderer, Text.literal("C T B S  •  Santuário"), center, top + 10, 0xFFF1E6D2);
        context.drawTextWithShadow(textRenderer, Text.literal("CONFIGURAÇÃO DA CRIATURA"), left + 18, top + 43, ACCENT);
        context.drawTextWithShadow(textRenderer, Text.literal("Espécie"), left + 18, top + 81, MUTED);
        context.drawTextWithShadow(textRenderer, Text.literal("Capacidade"), left + 204, top + 81, MUTED);
        context.drawTextWithShadow(textRenderer, Text.literal("Arraste ou digite o valor exato."), left + 18, top + 114, MUTED);
        context.drawTextWithShadow(textRenderer, Text.literal("POLÍTICA DO ESTÔMAGO"), left + 18, top + 145, ACCENT);
        context.drawTextWithShadow(textRenderer, Text.literal("POSTURA"), left + 18, top + 173, ACCENT);

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() { return false; }

    private final class CapacitySlider extends SliderWidget {
        CapacitySlider(int x, int y, int width, int height, int initial) {
            super(x, y, width, height, Text.literal("Capacidade"), initial / 100000.0);
            updateMessage();
        }

        int getCapacity() {
            return (int) Math.round(value * 100000.0);
        }

        void setCapacity(int capacity) {
            setValue(Math.max(0, Math.min(100000, capacity)) / 100000.0);
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            setMessage(Text.literal("Capacidade: " + getCapacity()));
        }

        @Override
        protected void applyValue() {
            onSliderChanged(getCapacity());
        }
    }
}
