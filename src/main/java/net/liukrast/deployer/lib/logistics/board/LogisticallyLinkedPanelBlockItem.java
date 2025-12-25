package net.liukrast.deployer.lib.logistics.board;

import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

import static com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockItem.fixCtrlCopiedStack;
import static com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBlockItem.assignFrequency;
import static com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBlockItem.isTuned;

/**
 * Represents a panel block item that must be network-linked before placement.
 * Functions similarly to {@link PanelBlockItem}, but enforces a tuning requirement.
 * Players must link the panel to a network before it can be properly placed.
 */
@SuppressWarnings("unused")
public class LogisticallyLinkedPanelBlockItem extends PanelBlockItem {
    /**
     * Constructs a new logistically linked panel block item.
     *
     * @param type the type of panel this item represents
     * @param properties item properties to apply
     */
    public LogisticallyLinkedPanelBlockItem(Supplier<PanelType<?>> type, Properties properties) {
        super(type, properties);
    }

    /**
     * Determines if the item has a glowing (foil) effect.
     * The item glows if it is already tuned to a network.
     *
     * @param stack the item stack to check
     * @return true if the item should render as foil
     */
    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return isTuned(stack);
    }

    /**
     * Handles right-click interaction with a block.
     * Requires the panel to be network-linked for proper placement.
     * Displays messages to the player depending on whether the network is new or already tuned.
     *
     * @param pContext the use context of the interaction
     * @return the interaction result
     */
    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext pContext) {
        ItemStack stack = pContext.getItemInHand();
        BlockPos pos = pContext.getClickedPos();
        Level level = pContext.getLevel();
        Player player = pContext.getPlayer();

        if (player == null)
            return InteractionResult.FAIL;
        if (player.isShiftKeyDown())
            return super.useOn(pContext);

        LogisticallyLinkedBehaviour link = BlockEntityBehaviour.get(level, pos, LogisticallyLinkedBehaviour.TYPE);
        boolean tuned = isTuned(stack);

        if (link != null) {
            if (level.isClientSide)
                return InteractionResult.SUCCESS;
            if (!link.mayInteractMessage(player))
                return InteractionResult.SUCCESS;

            assignFrequency(stack, player, link.freqId);
            return InteractionResult.SUCCESS;
        }

        InteractionResult useOn = super.useOn(pContext);
        if (level.isClientSide || useOn == InteractionResult.FAIL)
            return useOn;

        player.displayClientMessage(tuned ? CreateLang.translateDirect("logistically_linked.connected")
                : CreateLang.translateDirect("logistically_linked.new_network_started"), true);
        return useOn;
    }

    /**
     * Updates the block entity tag when placing the panel.
     * Ensures that a copy of the item stack is used for block entity initialization.
     *
     * @param pos the position of the block
     * @param level the level where the block is placed
     * @param player the player placing the block
     * @param stack the item stack used
     * @param state the block state
     * @return true if the block entity tag was updated successfully
     */
    @Override
    protected boolean updateCustomBlockEntityTag(@NotNull BlockPos pos, @NotNull Level level, Player player, @NotNull ItemStack stack,
                                                 @NotNull BlockState state) {
        return super.updateCustomBlockEntityTag(pos, level, player, fixCtrlCopiedStack(stack), state);
    }

    /**
     * Checks if the panel item is ready for placement.
     * Returns null if the item is tuned.
     * Otherwise, returns a message instructing the player to tune the panel first.
     *
     * @param stack the item stack to place
     * @param level the level where the block will be placed
     * @param pos the target block position
     * @param player the player attempting placement
     * @return a component message if not ready, or null if ready
     */
    @Override
    public Component isReadyForPlacement(ItemStack stack, Level level, BlockPos pos, Player player) {
        return isTuned(stack) ? null : CreateLang.translate("factory_panel.tune_before_placing").component();
    }
}
