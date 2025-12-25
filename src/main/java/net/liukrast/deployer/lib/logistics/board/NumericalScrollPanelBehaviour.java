package net.liukrast.deployer.lib.logistics.board;

import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

/**
 * Represents a scroll panel that handles numerical values.
 * Extends {@link ScrollPanelBehaviour} with built-in number formatting.
 * Provides default value settings and clipboard support for numeric data.
 */
@SuppressWarnings("unused")
public abstract class NumericalScrollPanelBehaviour extends ScrollPanelBehaviour {
    /**
     * Constructs a new numerical scroll panel behavior.

     * Sets the label, type, block entity, and panel slot.
     * Initializes the formatter to convert values to strings.
     *
     * @param label the label to display on the panel
     * @param type the type of panel
     * @param be the block entity this behavior is attached to
     * @param slot the panel slot
     */
    public NumericalScrollPanelBehaviour(Component label, PanelType<?> type, FactoryPanelBlockEntity be, FactoryPanelBlock.PanelSlot slot) {
        super(label, type, be, slot);
        withFormatter(String::valueOf);
    }

    /**
     * Returns the current value settings for this numerical panel.
     * Ensures the minimum value is at least 1 when the value is negative.
     *
     * @return the value settings object
     */
    @Override
    public ValueSettings getValueSettings() {
        return new ValueSettings(value < 0 ? 1 : 0, Math.abs(value));
    }

    /**
     * Formats the value settings for display.
     * Converts the numerical value to a localized string component.
     *
     * @param settings the value settings to format
     * @return the formatted component
     */
    public MutableComponent formatSettings(ValueSettings settings) {
        return CreateLang.number(settings.value())
                .component();
    }

    /**
     * Returns the clipboard key for this panel.
     * Used to identify this panel type in copy-paste operations.
     *
     * @return the clipboard key string
     */
    @Override
    public String getClipboardKey() {
        return "Numerical";
    }
}
