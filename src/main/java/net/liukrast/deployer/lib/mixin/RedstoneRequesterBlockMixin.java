package net.liukrast.deployer.lib.mixin;

import com.simibubi.create.content.logistics.redstoneRequester.RedstoneRequesterBlock;
import com.simibubi.create.content.logistics.redstoneRequester.RedstoneRequesterBlockEntity;
import net.liukrast.deployer.lib.logistics.packager.StockInventoryType;
import net.liukrast.deployer.lib.logistics.stockTicker.GenericOrderContained;
import net.liukrast.deployer.lib.mixinExtensions.RRBEExtension;
import net.liukrast.deployer.lib.registry.DeployerDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RedstoneRequesterBlock.class)
public class RedstoneRequesterBlockMixin {
    @SuppressWarnings("unchecked")
    @Inject(method = "lambda$setPlacedBy$1", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/logistics/redstoneRequester/AutoRequestData;encodedRequest()Lcom/simibubi/create/content/logistics/stockTicker/PackageOrderWithCrafts;"))
    private static void lambda$setPlacedBy$1(Level pLevel, Player player, BlockPos requesterPos, ItemStack pStack, RedstoneRequesterBlockEntity rrbe, CallbackInfo ci) {
        var extraData = pStack.get(DeployerDataComponents.EXTRA_REQUEST_DATA.get());
        if(extraData == null) return;
        extraData.forEach((key, val) -> ((RRBEExtension)rrbe).deployer$setEncodedRequest((StockInventoryType<?, Object, ?>) key,(GenericOrderContained<Object>) val));
    }
}
