package net.liukrast.deployer.lib.logistics.board.screen;

import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.utility.CreateLang;
import net.liukrast.deployer.lib.logistics.board.AbstractPanelBehaviour;
import net.liukrast.deployer.lib.logistics.board.connection.PanelConnection;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

public abstract class OutputSlot<V, A extends AbstractPanelBehaviour> extends GaugeSlot<A> {
    public OutputSlot(A panel, PanelConnection<?> connection) {
        super(panel, connection);
    }

    @Override
    public void renderInputSlot(GuiGraphics graphics, int count, int mouseX, int mouseY, int offsetX, int offsetY) {

    }

    @Override
    public void renderOutputSlot(GuiGraphics graphics, int count, int mouseX, int mouseY, int offsetX, int offsetY) {
        renderSlot(graphics, count, mouseX, mouseY, offsetX, offsetY);
        if (mouseX >= offsetX - 1 && mouseX < offsetX - 1 + 18 && mouseY >= offsetY - 1 && mouseY < offsetY - 1 + 18) {
            graphics.renderComponentTooltip(Minecraft.getInstance().font, getTooltip(count), mouseX, mouseY);
        }
    }

    public abstract void renderSlot(GuiGraphics graphics, int count, int mouseX, int mouseY, int offsetX, int offsetY);

    public abstract Component getTitle();

    public List<Component> getTooltip(int count) {
        MutableComponent c1 = CreateLang
                .translate("gui.factory_panel.expected_output", CreateLang.builder().add(getTitle())
                        .add(CreateLang.text(" x" + count))
                        .string())
                .color(ScrollInput.HEADER_RGB)
                .component();
        MutableComponent c2 = CreateLang.translate("gui.factory_panel.expected_output_tip")
                .style(ChatFormatting.GRAY)
                .component();
        MutableComponent c3 = CreateLang.translate("gui.factory_panel.expected_output_tip_1")
                .style(ChatFormatting.GRAY)
                .component();
        MutableComponent c4 = CreateLang.translate("gui.factory_panel.expected_output_tip_2")
                .style(ChatFormatting.DARK_GRAY)
                .style(ChatFormatting.ITALIC)
                .component();
        return List.of(c1, c2, c3, c4);
    }

    public abstract int scrollAmount(boolean ctrlDown, boolean shiftDown, boolean altDown);

    @Override
    public int mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY, boolean ctrlDown, boolean shiftDown, boolean altDown, int count) {
        int delta = (int)(Math.signum(scrollY) * scrollAmount(ctrlDown, shiftDown, altDown));
        if(delta > 0) return (Integer.MAX_VALUE - count < delta) ? Integer.MAX_VALUE : count + delta;
        else return Math.max(count + delta, 1);
    }

    public static class Item<A extends AbstractPanelBehaviour> extends OutputSlot<ItemStack, A> {

        private final Function<A, ItemStack> stackFunction;
        public Item(A panel, PanelConnection<?> connection, Function<A, ItemStack> stackFunction) {
            super(panel, connection);
            this.stackFunction = stackFunction;
        }

        @Override
        public void renderSlot(GuiGraphics graphics, int count, int mouseX, int mouseY, int offsetX, int offsetY) {
            var stack = stackFunction.apply(panel).copyWithCount(count);
            graphics.renderItem(stack, offsetX, offsetY);
            graphics.renderItemDecorations(Minecraft.getInstance().font, stack, offsetX, offsetY, count + "");
        }

        @Override
        public Component getTitle() {
            return stackFunction.apply(panel).getHoverName();
        }

        @Override
        public @NotNull ItemStack asOutputStack() {
            return stackFunction.apply(panel);
        }

        @Override
        public int scrollAmount(boolean ctrlDown, boolean shiftDown, boolean altDown) {
            return shiftDown ? 10 : 1;
        }
    }
}
