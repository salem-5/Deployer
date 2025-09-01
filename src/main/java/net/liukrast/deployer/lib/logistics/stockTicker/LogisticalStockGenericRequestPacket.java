package net.liukrast.deployer.lib.logistics.stockTicker;

import com.simibubi.create.content.logistics.stockTicker.StockCheckingBlockEntity;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import io.netty.buffer.ByteBuf;
import net.liukrast.deployer.lib.mixinExtensions.SCBEExtension;
import net.liukrast.deployer.lib.registry.DeployerPackets;
import net.liukrast.deployer.lib.registry.DeployerRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

public class LogisticalStockGenericRequestPacket extends BlockEntityConfigurationPacket<StockCheckingBlockEntity> {
    public static final StreamCodec<ByteBuf, LogisticalStockGenericRequestPacket> STREAM_CODEC = BlockPos.STREAM_CODEC
            .map(LogisticalStockGenericRequestPacket::new, packet -> packet.pos);

    public LogisticalStockGenericRequestPacket(BlockPos pos) {
        super(pos);
    }

    @Override
    public PacketTypeProvider getTypeProvider() {
        return DeployerPackets.LOGISTICS_STOCK_GENERIC_REQUEST;
    }

    @Override
    protected void applySettings(ServerPlayer player, StockCheckingBlockEntity be) {
        for(var type : DeployerRegistries.STOCK_INVENTORY)
            ((SCBEExtension)be).deployer$getRecentSummary(type)
                    .divideAndSendTo(player, pos);
    }

    @Override
    protected int maxRange() {
        return 4096;
    }
}
