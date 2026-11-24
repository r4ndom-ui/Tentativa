package com.example.ctbs.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public final class CTBSMenuScreen extends Screen {
    private TextFieldWidget speciesField;
    private TextFieldWidget capacityField;

    public CTBSMenuScreen() {
        super(Text.translatable("screen.ctbs.title"));
    }

    @Override
    protected void init() {
        int center = width / 2;
        speciesField = addDrawableChild(new TextFieldWidget(textRenderer, center - 100, 58, 200, 20, Text.literal("Species")));
        speciesField.setText("lucario");
        speciesField.setMaxLength(48);

        capacityField = addDrawableChild(new TextFieldWidget(textRenderer, center - 100, 84, 200, 20, Text.literal("Capacity")));
        capacityField.setText("4");
        capacityField.setMaxLength(6);

        addDrawableChild(ButtonWidget.builder(Text.translatable("screen.ctbs.save_capacity"), button -> saveCapacity())
            .dimensions(center - 100, 110, 200, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.translatable("screen.ctbs.hold"), button -> send("ctbs stomach mode hold"))
            .dimensions(center - 100, 140, 64, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.translatable("screen.ctbs.heal"), button -> send("ctbs stomach mode heal"))
            .dimensions(center - 32, 140, 64, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.translatable("screen.ctbs.digest"), button -> send("ctbs stomach mode digest"))
            .dimensions(center + 36, 140, 64, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.translatable("screen.ctbs.sit"), button -> send("ctbs action sit"))
            .dimensions(center - 100, 170, 64, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.translatable("screen.ctbs.lie"), button -> send("ctbs action lie"))
            .dimensions(center - 32, 170, 64, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.translatable("screen.ctbs.nap"), button -> send("ctbs action nap"))
            .dimensions(center + 36, 170, 64, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.translatable("screen.ctbs.close"), button -> close())
            .dimensions(center - 100, 204, 200, 20).build());
    }

    private void saveCapacity() {
        String species = speciesField.getText().trim().toLowerCase();
        String value = capacityField.getText().trim();
        if (!species.matches("[a-z0-9_]+") || !value.matches("[0-9]{1,6}")) return;
        send("ctbs capacity set " + species + " " + value);
    }

    private void send(String command) {
        if (client != null && client.player != null) client.player.networkHandler.sendChatCommand(command);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 28, 0xFFFFFF);
        context.drawCenteredTextWithShadow(textRenderer, Text.translatable("screen.ctbs.capacity_hint"), width / 2, 44, 0xAAAAAA);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() { return false; }
}
