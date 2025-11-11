package net.liukrast.deployer.lib.mixin;

import com.simibubi.create.foundation.blockEntity.behaviour.inventory.VersionedInventoryTrackerBehaviour;
import net.liukrast.deployer.lib.blockEntity.behaviour.VersionedWrapper;
import net.liukrast.deployer.lib.mixinExtensions.VITBExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(VersionedInventoryTrackerBehaviour.class)
public class VersionedInventoryTrackerBehaviourMixin implements VITBExtension {
    @Shadow private int ignoredId;
    @Shadow private int ignoredVersion;

    @Override
    public boolean deployer$stillWaiting(Object i) {
        if(i instanceof VersionedWrapper wrapper) return wrapper.getId() == ignoredId && wrapper.getVersion() == ignoredVersion;
        return false;
    }

    @Override
    public void deployer$awaitNewVersion(Object i) {
        if(!(i instanceof VersionedWrapper wrapper)) return;
        ignoredId = wrapper.getId();
        ignoredVersion = wrapper.getVersion();
    }
}
