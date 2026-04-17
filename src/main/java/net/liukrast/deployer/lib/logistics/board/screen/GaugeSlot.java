package net.liukrast.deployer.lib.logistics.board.screen;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.foundation.utility.CreateLang;
import net.createmod.catnip.config.ui.BaseConfigScreen;
import net.liukrast.deployer.lib.helper.ClientRegisterHelpers;
import net.liukrast.deployer.lib.logistics.board.AbstractPanelBehaviour;
import net.liukrast.deployer.lib.logistics.board.PanelType;
import net.liukrast.deployer.lib.logistics.board.connection.PanelConnection;
import net.liukrast.deployer.lib.registry.DeployerRegistries;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Base interface for defining and rendering a slot within a "Gauge" GUI.
 * <p>
 * These slots are registered via {@link net.liukrast.deployer.lib.helper.ClientRegisterHelpers#registerGaugeSlot(PanelType, ClientRegisterHelpers.PanelFactory)}.
 * They handle the visual representation and user interaction for panel connections.
 * </p>
 *
 * @param <A> The type of the panel behavior associated with this slot.
 */
public abstract class GaugeSlot<A extends AbstractPanelBehaviour> {
    protected final A panel;
    protected final PanelConnection<?> connection;

    public GaugeSlot(A panel, PanelConnection<?> connection) {
        this.panel = panel;
        this.connection = connection;
    }

    /**
     * Renders the input slot.
     * If no special rendering logic is required for the current connection,
     * it should delegate to {@link #renderDefaultSlot}.
     * * @param graphics The GuiGraphics instance for rendering.
     * @param count The current connection amount/value.
     * @param mouseX Current mouse X position.
     * @param mouseY Current mouse Y position.
     * @param offsetX The X coordinate origin of the slot.
     * @param offsetY The Y coordinate origin of the slot.
     */
    public abstract void renderInputSlot(GuiGraphics graphics, int count, int mouseX, int mouseY, int offsetX, int offsetY);
    /**
     * Optional: Renders the output slot if the panel logic requires a visual output representation.
     */
    public void renderOutputSlot(GuiGraphics graphics, int count, int mouseX, int mouseY, int offsetX, int offsetY) {

    }

    /**
     * Handles mouse scrolling to modify the connection amount.
     *
     * @return The updated 'count' value after processing the scroll delta.
     */
    public int mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY, boolean ctrlDown, boolean shiftDown, boolean altDown, int count) {
        return 1;
    }

    /**
     * Determines if this slot affects the crafting recipe result.
     * <p>
     * For example, a slot representing a fluid gauge might alter the recipe (true),
     * while a slot just emitting redstone signals does not (false).
     * </p>
     * * @return {@code true} if the slot locks or modifies the crafting recipe.
     */
    public boolean locksCrafting() {
        return false;
    }

    @NotNull
    public ItemStack asOutputStack() {
        return ItemStack.EMPTY;
    }

    /**
     * Static utility to render a default item-based slot when no custom logic is needed.
     */
    public static void renderDefaultSlot(GuiGraphics graphics, AbstractPanelBehaviour panel, PanelConnection<?> connection, int mouseX, int mouseY, int x, int y) {
        var stack = panel == null ? AllBlocks.FACTORY_GAUGE.asStack() : panel.getItem().getDefaultInstance();
        graphics.renderItem(stack, x, y);
        if (mouseX >= x - 1 && mouseX < x - 1 + 18 && mouseY >= y - 1
                && mouseY < y - 1 + 18) {

            MutableComponent c1 = CreateLang.builder().add(stack.getHoverName().copy())
                    .color(BaseConfigScreen.COLOR_TITLE_C)
                    .component();

            var id = DeployerRegistries.PANEL_CONNECTION.getKey(connection);
            MutableComponent c2;
            if(id == null) c2 = null;
            else {
                String key = "panel_connection." + id.getNamespace() + "." + id.getPath();
                c2 = Component.translatable("deployer.factory_panel.transferring", Component.translatable(key))
                        .withStyle(BaseConfigScreen.COLOR_TITLE_A.asStyle());
            }
            MutableComponent c3 = Component.translatable("deployer.gui.factory_panel.not_affecting_recipe_tip_0")
                    .withStyle(ChatFormatting.GRAY);
            MutableComponent c4 = Component.translatable("deployer.gui.factory_panel.not_affecting_recipe_tip_1")
                    .withStyle(ChatFormatting.GRAY);
            graphics.renderComponentTooltip(Minecraft.getInstance().font, c2 == null ? List.of(c1, c3, c4) : List.of(c1, c2, c3, c4),
                    mouseX, mouseY);
        }
    }
}
