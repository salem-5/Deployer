package net.liukrast.deployer.lib.logistics.board.connection;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelSupportBehaviour;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.*;
import java.util.function.Supplier;

public abstract class AbstractPanelSupportBehaviour extends FactoryPanelSupportBehaviour implements ProvidesConnection {
    private final Set<PanelConnection<?>> connectionsIn = new LinkedHashSet<>();
    private final Map<PanelConnection<?>, Supplier<?>> connectionsOut = new LinkedHashMap<>();

    public AbstractPanelSupportBehaviour(SmartBlockEntity be, Supplier<Boolean> isOutput, Runnable onNotify) {
        super(be, isOutput, () -> false, onNotify);
        var builder = new PanelConnectionBuilder(this.connectionsOut, this.connectionsIn);
        addConnections(builder);
    }

    /**
     * @return The set (ordered) containing all connections this panel outputs.
     * */
    public Set<PanelConnection<?>> getInputConnections() {
        return connectionsIn;
    }

    /**
     * @return The set (ordered) containing all connections this panel reads.
     * */
    public Set<PanelConnection<?>> getOutputConnections() {
        return connectionsOut.keySet();
    }

    @Override
    public <T> Optional<T> getConnectionValue(PanelConnection<T> connection) {
        if(!connectionsOut.containsKey(connection)) return Optional.empty();
        // We can safely cast here.
        //noinspection unchecked
        return Optional.ofNullable((T) connectionsOut.get(connection).get());
    }
}
