package com.example.ctbs.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/** Server-authoritative snapshot of stored creature record IDs for the stomach UI. */
public record StomachContentsPayload(String recordIds) implements CustomPayload {
    public static final Id<StomachContentsPayload> ID =
        new Id<>(Identifier.of("ctbs", "stomach_contents"));
    public static final PacketCodec<PacketByteBuf, StomachContentsPayload> CODEC =
        PacketCodec.ofStatic(
            (buf, value) -> buf.writeString(value.recordIds(), 32767),
            buf -> new StomachContentsPayload(buf.readString(32767))
        );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
