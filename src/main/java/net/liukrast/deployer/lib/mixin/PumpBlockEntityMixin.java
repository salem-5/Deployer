package net.liukrast.deployer.lib.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.fluids.pump.PumpBlockEntity;
import net.createmod.catnip.math.BlockFace;
import net.liukrast.deployer.lib.DeployerConfig;
import net.liukrast.deployer.lib.registry.DeployerTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PumpBlockEntity.class)
public class PumpBlockEntityMixin {
    @Definition(id = "blockEntity", local = @Local(type = BlockEntity.class, name = "blockEntity"))
    @Expression("blockEntity != null")
    @Inject(method = "hasReachedValidEndpoint", at = @At("MIXINEXTRAS:EXPRESSION"), cancellable = true)
    private void hasReachedValidEndpoint(LevelAccessor world, BlockFace blockFace, boolean pull, CallbackInfoReturnable<Boolean> cir, @Local(name = "blockEntity") BlockEntity blockEntity, @Local(name = "connectedPos") BlockPos connectedPos, @Local(name = "face") Direction face) {
        if(blockEntity != null) return;
        if(!(world instanceof Level level)) return;
        if(!DeployerConfig.Server.BLOCK_CAPABILITY_FIX.getAsBoolean() && !world.getBlockState(connectedPos).is(DeployerTags.Blocks.OVERRIDE_BLOCK_CAPABILITY_FIX)) return;
        IFluidHandler capability = level.getCapability(Capabilities.FluidHandler.BLOCK, connectedPos, face.getOpposite());
        if(capability == null) return;
        cir.setReturnValue(true);
        cir.cancel();
    }
}
