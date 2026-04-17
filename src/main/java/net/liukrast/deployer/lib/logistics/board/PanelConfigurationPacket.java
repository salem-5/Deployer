package net.liukrast.deployer.lib.logistics.board;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelPosition;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import net.minecraft.server.level.ServerPlayer;

public abstract class PanelConfigurationPacket<A extends AbstractPanelBehaviour> extends BlockEntityConfigurationPacket<FactoryPanelBlockEntity> {

    private final PanelType<A> type;
    public final FactoryPanelPosition position;

    public PanelConfigurationPacket(FactoryPanelPosition position, PanelType<A> type) {
        super(position.pos());
        this.position = position;
        this.type = type;
    }

    @Override
    protected void applySettings(ServerPlayer player, FactoryPanelBlockEntity be) {
        FactoryPanelBehaviour behaviour = be.panels.get(position.slot());
        if(!(behaviour instanceof AbstractPanelBehaviour apb)) return;
        if(apb.getPanelType() != type) return;
        //noinspection unchecked
        applySettings(player, (A)apb);
    }

    protected abstract void applySettings(ServerPlayer player, A panel);
}
