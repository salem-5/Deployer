package net.liukrast.deployer.lib.logistics;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.logistics.packagerLink.WiFiParticle;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * A simple interface that must be added to a {@link BlockEntity}
 * It will be flagged as a valid block for the {@link com.simibubi.create.content.logistics.packagerLink.WiFiEffectPacket} which is hardcoded to several blocks
 * */
public interface WiFiParticleProvider {
    default BlockEntity self() {
        return (BlockEntity)this;
    }

    default void playWifiEffect() {
        var pos = self().getBlockPos();
        var level = self().getLevel();
        AllSoundEvents.STOCK_LINK.playAt(level, pos, 1.0f, 1.0f, false);
        assert level != null;
        level.addParticle(new WiFiParticle.Data(), pos.getX(), pos.getY()+0.75f, pos.getZ()+0.5f, 1, 1, 1);
    }
}
