package com.example.ctbs.client;

import java.util.List;

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

    private enum Tab { CREATURE, STOMACH, ACTIONS, STATUS }
    private enum ContentTab { MOBS, ITEMS }
    private Tab selected = Tab.CREATURE;
    private ContentTab contentTab = ContentTab.MOBS;
    private boolean contentsOpen;
    private int mobsPage;
    private TextFieldWidget speciesField;
    private TextFieldWidget capacityField;
    private CapacitySlider capacitySlider;
    private boolean syncingCapacity;
    private String selectedSpecies = "eevee";

    public CTBSMenuScreen() {
        super(Text.translatable("screen.ctbs.title"));
    }

    @Override
    protected void init() {
        rebuild();
    }

    private void rebuild() {
        clearChildren();
        int left = width / 2 - 190;
        int top = Math.max(18, height / 2 - 135);

        if (contentsOpen) {
            addDrawableChild(ButtonWidget.builder(Text.literal("‹ VOLTAR"), b -> {
                contentsOpen = false;
                rebuild();
            }).dimensions(left + 14, top + 40, 86, 20).build());
            addDrawableChild(ButtonWidget.builder(Text.literal((contentTab == ContentTab.MOBS ? "◆ " : "") + "MOBS"), b -> {
                contentTab = ContentTab.MOBS;
                mobsPage = 0;
                rebuild();
            }).dimensions(left + 108, top + 40, 110, 20).build());
            addDrawableChild(ButtonWidget.builder(Text.literal((contentTab == ContentTab.ITEMS ? "◆ " : "") + "ITENS"), b -> {
                contentTab = ContentTab.ITEMS;
                rebuild();
            }).dimensions(left + 226, top + 40, 124, 20).build());
            buildContentsTab(left, top);
        } else {
            addTab(left + 14, top + 40, "CRIATURA", Tab.CREATURE);
            addTab(left + 108, top + 40, "ESTÔMAGO", Tab.STOMACH);
            addTab(left + 202, top + 40, "AÇÕES", Tab.ACTIONS);
            addTab(left + 296, top + 40, "ESTADO", Tab.STATUS);

            switch (selected) {
                case CREATURE -> buildCreatureTab(left, top);
                case STOMACH -> buildStomachTab(left, top);
                case ACTIONS -> buildActionsTab(left, top);
                case STATUS -> buildStatusTab(left, top);
            }
        }
        addDrawableChild(ButtonWidget.builder(Text.literal("FECHAR"), b -> close())
            .dimensions(left + 14, top + 235, 356, 20).build());
    }

    private void addTab(int x, int y, String label, Tab tab) {
        addDrawableChild(ButtonWidget.builder(Text.literal((selected == tab ? "◆ " : "") + label), b -> {
            selected = tab;
            rebuild();
        }).dimensions(x, y, 86, 20).build());
    }

    private void buildCreatureTab(int left, int top) {
        speciesField = addDrawableChild(new TextFieldWidget(textRenderer, left + 18, top + 100, 220, 20, Text.literal("Espécie")));
        speciesField.setText(selectedSpecies);
        speciesField.setMaxLength(48);
        speciesField.setChangedListener(value -> selectedSpecies = value.trim().toLowerCase(Locale.ROOT));
        capacityField = addDrawableChild(new TextFieldWidget(textRenderer, left + 258, top + 100, 92, 20, Text.literal("Capacidade")));
        capacityField.setText("2");
        capacityField.setMaxLength(6);
        capacityField.setChangedListener(this::onCapacityTextChanged);
        capacitySlider = addDrawableChild(new CapacitySlider(left + 18, top + 137, 332, 20, 2));
        addDrawableChild(ButtonWidget.builder(Text.literal("APLICAR"), b -> saveCapacity())
            .dimensions(left + 18, top + 174, 160, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("RESTAURAR PADRÃO"), b -> resetCapacity())
            .dimensions(left + 190, top + 174, 160, 20).build());
    }

    private void buildStomachTab(int left, int top) {
        addDrawableChild(ButtonWidget.builder(Text.literal("VER CONTEÚDO DO ESTÔMAGO"), b -> {
            contentsOpen = true;
            contentTab = ContentTab.MOBS;
            rebuild();
        }).dimensions(left + 18, top + 105, 332, 24).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("RETER CONTEÚDO"), b -> send("ctbs stomach mode hold"))
            .dimensions(left + 18, top + 137, 332, 22).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("MODO DE CURA"), b -> send("ctbs stomach mode heal"))
            .dimensions(left + 18, top + 165, 158, 22).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("DIGESTÃO"), b -> send("ctbs stomach mode digest"))
            .dimensions(left + 192, top + 165, 158, 22).build());
    }

    private void buildContentsTab(int left, int top) {
        if (contentTab != ContentTab.MOBS) return;
        List<CTBSClient.StoredPokemon> ids = CTBSClient.getStoredPokemon();
        int pages = Math.max(1, (ids.size() + 6) / 7);
        mobsPage = Math.max(0, Math.min(mobsPage, pages - 1));
        addDrawableChild(ButtonWidget.builder(Text.literal("‹ ANTERIOR"), b -> {
            mobsPage = Math.max(0, mobsPage - 1);
            rebuild();
        }).dimensions(left + 24, top + 215, 150, 18).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("PRÓXIMA ›"), b -> {
            mobsPage = Math.min(pages - 1, mobsPage + 1);
            rebuild();
        }).dimensions(left + 196, top + 215, 160, 18).build());
    }

    private void buildActionsTab(int left, int top) {
        addDrawableChild(ButtonWidget.builder(Text.literal("FICAR EM PÉ"), b -> send("ctbs action stand"))
            .dimensions(left + 18, top + 105, 158, 22).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("SENTAR"), b -> send("ctbs action sit"))
            .dimensions(left + 192, top + 105, 158, 22).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("DEITAR"), b -> send("ctbs action lie"))
            .dimensions(left + 18, top + 137, 158, 22).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("COCHILAR"), b -> send("ctbs action nap"))
            .dimensions(left + 192, top + 137, 158, 22).build());
    }

    private void buildStatusTab(int left, int top) {
        addDrawableChild(ButtonWidget.builder(Text.literal("CONSULTAR ESTADO NO CHAT"), b -> send("ctbs stomach status"))
            .dimensions(left + 18, top + 112, 332, 22).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("CONSULTAR CAPACIDADE DA ESPÉCIE"), b -> {
            String species = speciesField == null ? selectedSpecies : speciesField.getText().trim().toLowerCase(Locale.ROOT);
            if (species.matches("[a-z0-9_]+")) send("ctbs capacity get " + species);
        }).dimensions(left + 18, top + 145, 332, 22).build());
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
        selectedSpecies = species;
        send("ctbs capacity set " + species + " " + parsed);
    }

    private void resetCapacity() {
        String species = speciesField.getText().trim().toLowerCase(Locale.ROOT);
        if (!species.matches("[a-z0-9_]+")) return;
        selectedSpecies = species;
        send("ctbs capacity reset " + species);
    }

    private void send(String command) {
        if (client != null && client.player != null) client.player.networkHandler.sendChatCommand(command);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        int left = width / 2 - 190;
        int top = Math.max(18, height / 2 - 135);
        context.fill(left, top, left + 380, top + 264, PANEL);
        context.fill(left + 1, top + 1, left + 379, top + 32, PANEL_INNER);
        context.fill(left + 1, top + 33, left + 379, top + 34, ACCENT);
        context.drawBorder(left, top, 380, 264, BORDER);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("C T B S  •  SANTUÁRIO"), width / 2, top + 11, 0xFFF1E6D2);
        context.drawTextWithShadow(textRenderer, Text.literal("CONFIGURAÇÃO / " + selected.name()), left + 18, top + 75, ACCENT);
        if (selected == Tab.CREATURE) {
            context.drawTextWithShadow(textRenderer, Text.literal("Espécie"), left + 18, top + 89, MUTED);
            context.drawTextWithShadow(textRenderer, Text.literal("Valor exato"), left + 258, top + 89, MUTED);
            context.drawTextWithShadow(textRenderer, Text.literal("Arraste o controle ou digite qualquer valor entre 0 e 100000."), left + 18, top + 164, MUTED);
        } else if (contentsOpen) {
            context.drawTextWithShadow(textRenderer,
                Text.literal(contentTab == ContentTab.MOBS ? "POKÉMON ARMAZENADOS" : "ITENS ARMAZENADOS"),
                left + 18, top + 75, ACCENT);
            context.fill(left + 18, top + 94, left + 362, top + 214, PANEL_INNER);
            context.drawBorder(left + 18, top + 94, 344, 120, BORDER);
            if (contentTab == ContentTab.MOBS) {
                List<String> ids = CTBSClient.getStoredPokemonIds();
                if (ids.isEmpty()) {
                    context.drawCenteredTextWithShadow(textRenderer, Text.literal("Nenhum Pokémon armazenado."), width / 2, top + 139, 0xFFF1E6D2);
                    context.drawCenteredTextWithShadow(textRenderer, Text.literal("A lista é atualizada automaticamente pelo servidor."), width / 2, top + 156, MUTED);
                } else {
                    int pages = Math.max(1, (ids.size() + 6) / 7);
                    int start = mobsPage * 7;
                    int visible = Math.min(7, ids.size() - start);
                    for (int i = 0; i < visible; i++) {
                        CTBSClient.StoredPokemon pokemon = ids.get(start + i);
                        context.fill(left + 24, top + 100 + i * 15, left + 356, top + 113 + i * 15, i % 2 == 0 ? 0xFF293244 : PANEL_INNER);
                        String species = pokemon.species().equals("unknown") ? "Espécie desconhecida" : pokemon.species();
                        String level = pokemon.level() > 0 ? "Nv. " + pokemon.level() : "Nível indisponível";
                        context.drawTextWithShadow(textRenderer, Text.literal(species), left + 29, top + 102 + i * 15, ACCENT);
                        context.drawTextWithShadow(textRenderer, Text.literal(level), left + 175, top + 102 + i * 15, 0xFFE8EAF0);
                        context.drawTextWithShadow(textRenderer, Text.literal(pokemon.id().substring(0, 8)), left + 270, top + 102 + i * 15, MUTED);
                    }
                    context.drawTextWithShadow(textRenderer,
                        Text.literal("Página " + (mobsPage + 1) + " / " + pages + "  •  " + ids.size() + " registros"),
                        left + 24, top + 207, MUTED);
                }
            } else {
                context.drawCenteredTextWithShadow(textRenderer, Text.literal("Armazenamento de itens ainda não implementado."), width / 2, top + 139, 0xFFF1E6D2);
                context.drawCenteredTextWithShadow(textRenderer, Text.literal("A aba será conectada quando o inventário persistente existir."), width / 2, top + 156, MUTED);
            }
        } else if (selected == Tab.STOMACH) {
            context.drawTextWithShadow(textRenderer, Text.literal("Abra a lista de conteúdos ou escolha um modo."), left + 18, top + 88, MUTED);
        } else if (selected == Tab.ACTIONS) {
            context.drawTextWithShadow(textRenderer, Text.literal("Posturas: normal, sentado, deitado e cochilando."), left + 18, top + 88, MUTED);
            context.drawTextWithShadow(textRenderer, Text.literal("Bônus previstos: 0%, 20%, 40% e 60%."), left + 18, top + 177, MUTED);
        } else {
            context.drawTextWithShadow(textRenderer, Text.literal("Os comandos exibem os dados atuais no chat."), left + 18, top + 88, MUTED);
        }
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() { return false; }

    private final class CapacitySlider extends SliderWidget {
        CapacitySlider(int x, int y, int width, int height, int initial) {
            super(x, y, width, height, Text.literal("Capacidade"), initial / 100000.0);
            updateMessage();
        }
        int getCapacity() { return (int) Math.round(value * 100000.0); }
        void setCapacity(int capacity) {
            this.value = Math.max(0, Math.min(100000, capacity)) / 100000.0;
            updateMessage();
        }
        @Override protected void updateMessage() { setMessage(Text.literal("Capacidade: " + getCapacity())); }
        @Override protected void applyValue() { onSliderChanged(getCapacity()); }
    }
}
