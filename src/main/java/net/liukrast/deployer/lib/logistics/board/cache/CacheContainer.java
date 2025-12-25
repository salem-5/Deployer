package net.liukrast.deployer.lib.logistics.board.cache;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import net.createmod.catnip.platform.CatnipServices;
import net.liukrast.deployer.lib.DeployerConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

import java.util.Map;

/**
 * A generic cache container used by factory gauge and panel behaviors to store,
 * serialize and synchronize position-indexed data between server and client.
 *
 * <p>This interface defines:
 * <ul>
 *     <li>a {@link Codec} for encoding and decoding individual cached values,</li>
 *     <li>a map associating {@link BlockPos} coordinates with their cached data,</li>
 *     <li>utilities for reading and writing the cache to NBT,</li>
 *     <li>a method for synchronizing the serialized cache to all clients
 *     tracking the relevant chunk.</li>
 * </ul>
 *
 * <p>The cache is intended to store lightweight, derived or frequently-changing
 * data that needs to be visible on the client (e.g., readings for factory gauges)
 * without requiring the block entity to store or sync large structures directly.</p>
 *
 * <p>Keys in the serialized form are stored as comma-separated block positions
 * (<code>x,y,z</code>).
 * Values are encoded/decoded using the provided codec.</p>
 *
 * <p>Implementors are responsible for supplying their own codec and cache map.</p>
 *
 * @param <T> the type of values stored inside the cache
 *
 * @implNote This interface provides default implementations for serialization,
 * deserialization and network sync, but does not manage lifecycle or invalidation
 * of cached entries.
 * Implementors should clear or update the cache whenever the
 * underlying monitored data changes.
 */
public interface CacheContainer<T> {

    /**
     * @return the codec used to serialize and deserialize cached values
     */
    Codec<T> cacheCodec();
    /**
     * @return the internal map storing cached values indexed by {@link BlockPos}
     */
    Map<BlockPos, T> cacheMap();

    /**
     * Loads cached values from the given tag, decoding each entry using the provided {@link DynamicOps}.
     * Existing cache entries are cleared before loading.
     *
     * <p>Each tag key is expected to be a comma-separated block position in the format:
     * <code>x,y,z</code>.
     * Keys that fail to parse are logged and ignored.</p>
     *
     * @param tag        the compound tag containing serialized cache entries
     * @param dynamicOps the OPS instance used to decode values via the codec
     *
     * @implNote Only entries that successfully decode are inserted into the cache.
     */
    default void setCache(CompoundTag tag, DynamicOps<Tag> dynamicOps) {
        var cache = cacheMap();
        var codec = cacheCodec();
        cache.clear();
        for(String key : tag.getAllKeys()) {
            String[] t = key.split(",");
            try {
                BlockPos pos = new BlockPos(Integer.parseInt(t[0]), Integer.parseInt(t[1]), Integer.parseInt(t[2]));
                codec.parse(dynamicOps, tag.get(key))
                        .resultOrPartial(DeployerConstants.LOGGER::error)
                        .ifPresent(r -> cache.put(pos, r));
            } catch (NumberFormatException e) {
                DeployerConstants.LOGGER.error("Unable to parse compound tag {}", tag.get(key), e);
            }
        }

    }

    /**
     * Serializes the current cache into the given tag using the provided {@link DynamicOps}.
     * Each cache entry is stored using a key formatted as <code>x,y,z</code>.
     *
     * @param tag        the compound tag to write serialized entries into
     * @param dynamicOps the op instance used to encode values via the codec
     *
     * @implNote Entries that fail to encode are logged and omitted.
     */
    default void getCache(CompoundTag tag, DynamicOps<Tag> dynamicOps) {
        var cache = cacheMap();
        var codec = cacheCodec();
        for(BlockPos pos : cache.keySet()) {
            String key = pos.getX()+","+pos.getY()+","+pos.getZ();
            codec.encodeStart(dynamicOps, cache.get(pos))
                    .resultOrPartial(DeployerConstants.LOGGER::error)
                    .ifPresent(tag1 -> tag.put(key, tag1));
        }

    }

    /**
     * Sends the current cache to all clients tracking the chunk containing the
     * specified behavior's block entity.
     * The cache is first serialized and then
     * transmitted via a network packet.
     *
     * @param behaviour the panel behavior whose position determines the sync target
     *
     * @implNote This method performs no action on the client side or when the level is null.
     * @implNote The packet includes the panel position and the serialized cache.
     */
    @SuppressWarnings("unused")
    default void sendCache(FactoryPanelBehaviour behaviour) {
        var level = behaviour.blockEntity.getLevel();
        if(level == null || level.isClientSide) return;
        var tag = new CompoundTag();
        getCache(tag, level.registryAccess().createSerializationContext(NbtOps.INSTANCE));
        CatnipServices.NETWORK.sendToClientsTrackingChunk(
                (ServerLevel) level,
                new ChunkPos(behaviour.blockEntity.getBlockPos()),
                new PanelCacheUpdatePacket(behaviour.getPanelPosition(), tag)
        );
    }
}
