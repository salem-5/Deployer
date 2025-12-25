package net.liukrast.deployer.lib.logistics.board;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlock;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockItem;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBlockItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Represents an item used to place a factory panel.
 * Handles panel creation, placement validation, and optional network linking.
 * Provides hooks for customizing messages and extra placement data.
 */
public class PanelBlockItem extends BlockItem {

    private final Supplier<PanelType<?>> type;

    /**
     * Constructs a new PanelBlockItem with a panel type and item properties.
     *
     * @param type the supplier of the panel type
     * @param properties the item properties
     */
    public PanelBlockItem(Supplier<PanelType<?>> type, Properties properties) {
        super(AllBlocks.FACTORY_GAUGE.get(), properties);
        this.type = type;
    }

    /**
     * Checks whether the item stack is ready to be placed.
     * For example, a panel may require network tuning before it can be placed.
     *
     * @param stack the item stack to place
     * @param level the level in which it will be placed
     * @param pos the target block position
     * @param player the player placing the item
     * @return a component with an error message if placement is not allowed, null if placement is allowed
     */
    public Component isReadyForPlacement(ItemStack stack, Level level, BlockPos pos, @Nullable Player player) {
        return null;
    }

    /**
     * Gets the message to display to the player after the panel is placed.
     * Can be overridden to provide custom feedback.
     *
     * @return the component containing the placed message
     */
    @NotNull
    public Component getPlacedMessage() {
        return Component.empty();
    }

    @ApiStatus.Internal
    @Override
    public @NotNull InteractionResult place(BlockPlaceContext context) {
        var player = context.getPlayer();
        Component error = isReadyForPlacement(context.getItemInHand(), context.getLevel(), context.getClickedPos(), player);
        if(error == null) return super.place(context);
        AllSoundEvents.DENY.playOnServer(context.getLevel(), context.getClickedPos());
        if(player != null) player.displayClientMessage(error, true);
        return InteractionResult.FAIL;
    }

    @ApiStatus.Internal
    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        InteractionResult interactionResult = this.place(new BlockPlaceContext(context));
        var player = context.getPlayer();
        if (!interactionResult.consumesAction() && context.getItemInHand().has(DataComponents.FOOD) && player != null) {
            InteractionResult result = super.use(context.getLevel(), player, context.getHand()).getResult();
            return result == InteractionResult.CONSUME ? InteractionResult.CONSUME_PARTIAL : result;
        } else {
            return interactionResult;
        }
    }

    /**
     * Creates a new panel behavior instance for a given block entity and slot.
     *
     * @param blockEntity the factory panel block entity
     * @param slot the slot the panel occupies
     * @return the new behavior instance
     */
    protected AbstractPanelBehaviour getNewBehaviourInstance(FactoryPanelBlockEntity blockEntity, FactoryPanelBlock.PanelSlot slot) {
        return type.get().create(blockEntity, slot);
    }

    /**
     * Applies extra placement data to a newly placed panel.
     * Handles network linking and notifies the player with a placement message.
     *
     * @param context the block place context
     * @param blockEntity the factory panel block entity
     * @param targetedSlot the slot being targeted
     */
    public void applyExtraPlacementData(BlockPlaceContext context, FactoryPanelBlockEntity blockEntity, FactoryPanelBlock.PanelSlot targetedSlot) {
        var stack = context.getItemInHand();
        applyToSlot(blockEntity, targetedSlot, LogisticallyLinkedBlockItem.networkFromStack(FactoryPanelBlockItem.fixCtrlCopiedStack(stack)));
        var message = getPlacedMessage();
        var player = context.getPlayer();
        if(player == null) return;
        if(!context.getPlayer().isCreative()) {
            stack.shrink(1);
            if(stack.isEmpty())
                player.setItemInHand(context.getHand(), ItemStack.EMPTY);
        }
        player.displayClientMessage(message, true);
    }

    /**
     * Applies a network ID to a specific panel slot if the slot is empty or inactive.
     * Attaches a new behavior and plays a placement sound if needed.
     *
     * @param blockEntity the factory panel block entity
     * @param slot the slot to apply to
     * @param networkId the network ID to assign, or null
     * @return true if a new behavior was applied, false otherwise
     */
    public boolean applyToSlot(FactoryPanelBlockEntity blockEntity, FactoryPanelBlock.PanelSlot slot, @Nullable UUID networkId) {
        var oldBehaviour = blockEntity.panels.get(slot);
        if(oldBehaviour == null || !oldBehaviour.isActive()) {
            var newBehaviour = getNewBehaviourInstance(blockEntity, slot);
            newBehaviour.active = true;
            if(networkId != null) newBehaviour.setNetwork(networkId);
            blockEntity.attachBehaviourLate(newBehaviour);
            blockEntity.panels.put(slot, newBehaviour);
            blockEntity.redraw = true;
            blockEntity.lastShape = null;
            blockEntity.notifyUpdate();

            if (blockEntity.activePanels() > 1) {
                var level = blockEntity.getLevel();
                if(level == null) return true;
                SoundType soundType = blockEntity.getBlockState().getSoundType(level, blockEntity.getBlockPos(), null);
                level.playSound(null, blockEntity.getBlockPos(), soundType.getPlaceSound(), SoundSource.BLOCKS,
                        (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
            }
            return true;
        }
        return false;
    }

    // We want to ignore the registration to the map so that the creative tab won't crash
    @Override
    public void registerBlocks(@NotNull Map<Block, Item> blockToItemMap, @NotNull Item item) {}

    @Override
    public @NotNull String getDescriptionId() {
        return getOrCreateDescriptionId();
    }
}
