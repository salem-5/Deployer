package net.liukrast.deployer.lib.logistics;

import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBehaviour;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBlockItem;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;

public interface LogisticallyLinked {


    static void appendHoverText(ItemStack stack,
                                List<Component> tooltipComponents) {
        var uuid = LogisticallyLinkedBlockItem.networkFromStack(stack);
        if(uuid == null) return;

        CreateLang.translate("logistically_linked.tooltip")
                .style(ChatFormatting.GOLD)
                .addTo(tooltipComponents);

        CreateLang.translate("logistically_linked.tooltip_clear")
                .style(ChatFormatting.GRAY)
                .addTo(tooltipComponents);
    }

    static @NotNull InteractionResult useOn(@NotNull UseOnContext pContext, Function<UseOnContext, InteractionResult> super$useOn) {
        ItemStack stack = pContext.getItemInHand();
        BlockPos pos = pContext.getClickedPos();
        Level level = pContext.getLevel();
        Player player = pContext.getPlayer();

        if (player == null)
            return InteractionResult.FAIL;
        if (player.isShiftKeyDown())
            return super$useOn.apply(pContext);

        LogisticallyLinkedBehaviour link = BlockEntityBehaviour.get(level, pos, LogisticallyLinkedBehaviour.TYPE);
        boolean tuned = LogisticallyLinkedBlockItem.isTuned(stack);

        if (link != null) {
            if (level.isClientSide)
                return InteractionResult.SUCCESS;
            if (!link.mayInteractMessage(player))
                return InteractionResult.SUCCESS;

            LogisticallyLinkedBlockItem.assignFrequency(stack, player, link.freqId);
            return InteractionResult.SUCCESS;
        }

        InteractionResult useOn = super$useOn.apply(pContext);
        if (level.isClientSide || useOn == InteractionResult.FAIL)
            return useOn;

        player.displayClientMessage(tuned ? CreateLang.translateDirect("logistically_linked.connected")
                : CreateLang.translateDirect("logistically_linked.new_network_started"), true);
        return useOn;
    }

    static InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand, TriFunction<Level, Player, InteractionHand, InteractionResultHolder<ItemStack>> super$use) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (LogisticallyLinkedBlockItem.isTuned(stack)) {
            if (level.isClientSide) {
                level.playSound(player, player.blockPosition(), SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 0.75f, 1.0f);
            } else {
                player.displayClientMessage(CreateLang.translateDirect("logistically_linked.cleared"), true);
                stack.remove(DataComponents.BLOCK_ENTITY_DATA);
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        } else {
            return super$use.apply(level, player, usedHand);
        }
    }

}
