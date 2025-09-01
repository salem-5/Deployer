package net.liukrast.deployer.lib.logistics.stockTicker;

import com.mojang.serialization.Codec;
import com.simibubi.create.content.logistics.stockTicker.StockTickerBlockEntity;
import net.createmod.catnip.codecs.CatnipCodecUtils;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.liukrast.deployer.lib.DeployerConstants;
import net.liukrast.deployer.lib.logistics.packager.StockInventoryType;
import net.liukrast.deployer.lib.mixinExtensions.STBEExtension;
import net.liukrast.deployer.lib.registry.DeployerPackets;
import net.liukrast.deployer.lib.registry.DeployerRegistries;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;
import java.util.Objects;

public class LogisticalStockGenericResponsePacket implements ClientboundPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, LogisticalStockGenericResponsePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, p -> p.lastPacket,
            BlockPos.STREAM_CODEC, p -> p.pos,
            ResourceLocation.STREAM_CODEC, p -> p.id,
            ByteBufCodecs.TAG, p -> p.tag,
            LogisticalStockGenericResponsePacket::new
    );
    private final boolean lastPacket;
    private final BlockPos pos;
    private final ResourceLocation id;
    private final ListTag tag;

    public <T> LogisticalStockGenericResponsePacket(boolean lastPacket, BlockPos pos, StockInventoryType<?,T,?> type, List<T> items) {
        this(lastPacket, pos, DeployerRegistries.STOCK_INVENTORY.getKey(type), CatnipCodecUtils.encode(Codec.list(type.valueHandler().codec()), items).orElseThrow());
    }

    private LogisticalStockGenericResponsePacket(boolean lastPacket, BlockPos pos, ResourceLocation id, Tag tag) {
        this.lastPacket = lastPacket;
        this.pos = pos;
        this.id = id;
        if(!(tag instanceof ListTag)) {
            this.tag = new ListTag();
            DeployerConstants.LOGGER.error("Unable to cast tag into listTag {} when receiving logistical stock generic response", tag);
        } else this.tag = (ListTag) tag;
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return DeployerPackets.LOGISTICS_STOCK_RESPONSE;
    }

    @SuppressWarnings("unchecked")
    @Override
    @OnlyIn(Dist.CLIENT)
    public void handle(LocalPlayer player) {
        var level = Minecraft.getInstance().level;
        if(level == null || !(level.getBlockEntity(pos) instanceof StockTickerBlockEntity stockTicker)) return;
        var type = DeployerRegistries.STOCK_INVENTORY.get(id);
        if(type == null) {
            DeployerConstants.LOGGER.error("Unable to find stock inventory {}", id);
            return;
        }
        var ops = level.registryAccess().createSerializationContext(NbtOps.INSTANCE);
        ((STBEExtension)stockTicker).deployer$receiveStockPacket(
                (StockInventoryType<?, Object,?>)type,
                (List<Object>) tag
                        .stream()
                        .map(tag -> type
                                .valueHandler().codec()
                                .parse(ops, tag)
                                .resultOrPartial(DeployerConstants.LOGGER::error)
                                .orElse(null))
                        .filter(Objects::nonNull)
                        .toList(),
                lastPacket);
    }
}
