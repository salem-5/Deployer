package net.liukrast.deployer.lib.logistics.board;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;

/**
 * Represents a type of panel that can be instantiated in a factory panel block entity.
 * Encapsulates the constructor and the class of the panel behavior.<br><br>
 * Should be registered through {@link net.liukrast.deployer.lib.registry.DeployerRegistries#PANEL}
 *
 * @param <T> the type of the panel behavior
 */
@SuppressWarnings("ClassCanBeRecord")
public class PanelType<T extends AbstractPanelBehaviour> {
    private final Constructor<T> constructor;
    private final Class<T> clazz;

    /**
     * Constructs a new PanelType with a constructor function and its class.
     *
     * @param constructor the constructor function for creating panel behaviors
     * @param clazz the class object of the panel behavior
     */
    public PanelType(Constructor<T> constructor, Class<T> clazz) {
        this.constructor = constructor;
        this.clazz = clazz;
    }

    /**
     * Creates a new instance of the panel behavior for a specific block entity and slot.
     *
     * @param be the factory panel block entity
     * @param slot the slot to assign the panel to
     * @return a new instance of the panel behavior
     */
    public AbstractPanelBehaviour create(FactoryPanelBlockEntity be, FactoryPanelBlock.PanelSlot slot) {
        return constructor.apply(this, be, slot);
    }

    /**
     * Returns the class object of the panel behavior.
     *
     * @return the panel behavior class
     */
    public Class<T> asClass() {
        return clazz;
    }

    /**
     * Functional interface representing a constructor for a panel behavior.
     *
     * @param <T> the type of the panel behavior
     */
    @FunctionalInterface
    public interface Constructor<T extends AbstractPanelBehaviour> {
        /**
         * Applies the constructor to create a new panel behavior instance.
         *
         * @param panelType the panel type
         * @param be the factory panel block entity
         * @param slot the panel slot
         * @return a new panel behavior instance
         */
        T apply(PanelType<T> panelType, FactoryPanelBlockEntity be, FactoryPanelBlock.PanelSlot slot);
    }
}
