package net.liukrast.deployer.lib.logistics.board;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsBoard;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueSettingsFormatter;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

/**
 * A panel behaviour representing a scrollable selection of options.
 * <p>
 * Each option is an enum value implementing {@link INamedIconOptions}, allowing
 * the panel to display a selectable icon and label for each value.
 *
 * @param <E> the enum type of options
 */
public abstract class ScrollOptionPanelBehaviour<E extends Enum<E> & INamedIconOptions> extends ScrollPanelBehaviour {
    private final E[] options;

    /**
     * Constructs a new scroll option panel behavior.
     *
     * @param label the display label of the panel
     * @param type the type of the panel
     * @param be the factory panel block entity
     * @param slot the slot to assign the panel to
     * @param enum_ the class of the enum representing selectable options
     */
    public ScrollOptionPanelBehaviour(Component label, PanelType<?> type, FactoryPanelBlockEntity be, FactoryPanelBlock.PanelSlot slot, Class<E> enum_) {
        super(label, type, be, slot);
        options = enum_.getEnumConstants();
        between(0, options.length - 1);
    }

    /**
     * Creates a {@link ValueSettingsBoard} for interacting with this panel.
     *
     * @param player the player interacting with the panel
     * @param hitResult the hit result of the interaction
     * @return a new {@link ValueSettingsBoard} configured for this panel
     */
    @Override
    public ValueSettingsBoard createBoard(Player player, BlockHitResult hitResult) {
        return new ValueSettingsBoard(label, max, 1, ImmutableList.of(Component.literal("Select")),
                new ValueSettingsFormatter.ScrollOptionSettingsFormatter(options));
    }

    /**
     * Returns a key for clipboard operations specific to this panel.
     *
     * @return the clipboard key, based on the enum type name
     */
    @Override
    public String getClipboardKey() {
        return options[0].getClass().getSimpleName();
    }

    /**
     * Gets the option currently selected by this panel.
     *
     * @return the currently selected option implementing {@link INamedIconOptions}
     */
    public INamedIconOptions getIconForSelected() {
        return get();
    }

    /**
     * Gets the enum value currently selected by this panel.
     *
     * @return the selected enum value
     */
    public E get() {
        return options[getValue()];
    }
}
