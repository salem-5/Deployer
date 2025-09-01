package net.liukrast.deployer.lib.blockEntity.behaviour;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.CapManipulationBehaviourBase;

public class VersionedTrackerBehaviour<A, B extends CapManipulationBehaviourBase<?, ?>> extends BlockEntityBehaviour {

    //TODO: Make per-implementation type
    public static final BehaviourType<VersionedTrackerBehaviour<?, ?>> TYPE = new BehaviourType<>();

    private int ignoredId;
    private int ignoredVersion;

    public VersionedTrackerBehaviour(SmartBlockEntity be) {
        super(be);
        reset();
    }

    public boolean stillWaiting(CapManipulationBehaviourBase<A, B> behaviour) {
        return behaviour.hasInventory() && stillWaiting(behaviour.getInventory());
    }

    public boolean stillWaiting(A handler) {
        if (handler instanceof VersionedWrapper viw)
            return viw.getId() == ignoredId && viw.getVersion() == ignoredVersion;
        return false;
    }

    public void awaitNewVersion(CapManipulationBehaviourBase<A, B> behaviour) {
        if (behaviour.hasInventory())
            awaitNewVersion(behaviour.getInventory());
    }

    public void awaitNewVersion(A handler) {
        if (handler instanceof VersionedWrapper viw) {
            ignoredId = viw.getId();
            ignoredVersion = viw.getVersion();
        }
    }

    public void reset() {
        ignoredVersion = -1;
        ignoredId = -1;
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }

}
