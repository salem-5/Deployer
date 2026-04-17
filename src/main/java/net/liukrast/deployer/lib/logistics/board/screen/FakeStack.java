package net.liukrast.deployer.lib.logistics.board.screen;

import com.simibubi.create.content.logistics.BigItemStack;
import net.liukrast.deployer.lib.helper.ClientRegisterHelpers;
import net.liukrast.deployer.lib.logistics.board.AbstractPanelBehaviour;
import net.liukrast.deployer.lib.logistics.board.connection.PanelConnection;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public class FakeStack<APB extends AbstractPanelBehaviour> extends BigItemStack {
    private final GaugeSlot<APB> slot;
    // APB null -> Normal factory gauge in non-stock mode

    public FakeStack(@Nullable APB value, int amount, PanelConnection<?> panelConnection, boolean out) {
        super(ItemStack.EMPTY, amount);
        GaugeSlot<APB> slot = ClientRegisterHelpers.getSlot(value, panelConnection);
        this.slot = slot == null ? createDefault(value, panelConnection) : slot;
        if(out)
            this.stack = this.slot.asOutputStack();
    }

    public void renderAsInput(GuiGraphics graphics, int mouseX, int mouseY, int x, int y) {
        slot.renderInputSlot(graphics, count, mouseX, mouseY, x, y);
    }

    public void renderAsOutput(GuiGraphics graphics, int mouseX, int mouseY, int x, int y) {
        slot.renderOutputSlot(graphics, count, mouseX, mouseY, x, y);
    }

    public int mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY, boolean shiftDown, boolean controlDown, boolean altDown) {
        return slot.mouseScrolled(mouseX, mouseY, scrollX, scrollY, controlDown, shiftDown, altDown, this.count);
    }

    private static <APB extends AbstractPanelBehaviour> GaugeSlot<APB> createDefault(APB value, PanelConnection<?> connection) {
        return new GaugeSlot<>(value, connection) {

            @Override
            public void renderInputSlot(GuiGraphics graphics, int count, int mouseX, int mouseY, int x, int y) {
                renderDefaultSlot(graphics, panel, connection, mouseX, mouseY, x, y);
            }
        };
    }

    public boolean locksCrafting() {
        return slot.locksCrafting();
    }
}
