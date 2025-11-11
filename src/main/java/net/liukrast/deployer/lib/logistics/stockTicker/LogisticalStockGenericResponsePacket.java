package net.liukrast.deployer.lib.logistics.stockTicker;

import com.simibubi.create.content.logistics.stockTicker.StockTickerBlockEntity;
import net.createmod.catnip.net.base.ClientboundPacketPayload;
import net.liukrast.deployer.lib.DeployerConstants;
import net.liukrast.deployer.lib.logistics.packager.StockInventoryType;
import net.liukrast.deployer.lib.mixinExtensions.STBEExtension;
import net.liukrast.deployer.lib.registry.DeployerPackets;
import net.liukrast.deployer.lib.registry.DeployerRegistries;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class LogisticalStockGenericResponsePacket<V> implements ClientboundPacketPayload {
    @SuppressWarnings("unchecked")
    public static final StreamCodec<RegistryFriendlyByteBuf, LogisticalStockGenericResponsePacket<?>> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public @NotNull LogisticalStockGenericResponsePacket<?> decode(@NotNull RegistryFriendlyByteBuf buf) {
            boolean lastPacket = buf.readBoolean();
            BlockPos pos = BlockPos.STREAM_CODEC.decode(buf);
            StockInventoryType<?,Object,?> type = (StockInventoryType<?, Object, ?>) DeployerRegistries.STOCK_INVENTORY.get(buf.readResourceLocation());
            assert type != null;
            List<Object> list = type.valueHandler().streamCodec().apply(ByteBufCodecs.list()).decode(buf);
            return new LogisticalStockGenericResponsePacket<>(lastPacket, pos, type, list);
        }

        @Override
        public void encode(@NotNull RegistryFriendlyByteBuf buf, @NotNull LogisticalStockGenericResponsePacket<?> p) {
            buf.writeBoolean(p.lastPacket);
            buf.writeBlockPos(p.pos);
            buf.writeResourceLocation(DeployerRegistries.STOCK_INVENTORY.getKey(p.type));
            ((StreamCodec<RegistryFriendlyByteBuf, Object>)p.type.valueHandler().streamCodec()).apply(ByteBufCodecs.list()).encode(buf, (List<Object>)p.items);
        }
    };

    private final boolean lastPacket;
    private final BlockPos pos;
    private final StockInventoryType<?,V,?> type;
    private final List<V> items;

    public LogisticalStockGenericResponsePacket(boolean lastPacket, BlockPos pos, StockInventoryType<?,V,?> type, List<V> items) {
        this.lastPacket = lastPacket;
        this.pos = pos;
        this.type = type;
        this.items = items;
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return DeployerPackets.LOGISTICS_STOCK_RESPONSE;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void handle(LocalPlayer player) {
        var level = Minecraft.getInstance().level;
        if(level == null || !(level.getBlockEntity(pos) instanceof StockTickerBlockEntity stockTicker)) return;
        ((STBEExtension)stockTicker).deployer$receiveStockPacket(type, items, lastPacket);
    }
}
