package net.liukrast.deployer.lib.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.simibubi.create.AllItems;
import com.simibubi.create.api.equipment.goggles.IHaveCustomOverlayIcon;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.api.equipment.goggles.IHaveHoveringInformation;
import com.simibubi.create.content.contraptions.IDisplayAssemblyExceptions;
import com.simibubi.create.content.equipment.goggles.GoggleOverlayRenderer;
import com.simibubi.create.content.equipment.goggles.GogglesItem;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBox;
import net.createmod.catnip.outliner.Outline;
import net.createmod.catnip.outliner.Outliner;
import net.liukrast.deployer.lib.helper.ClientRegisterHelpers;
import net.liukrast.deployer.lib.helper.GuiRenderingHelpers;
import net.liukrast.deployer.lib.helper.client.DeployerGoggleInformation;
import net.liukrast.deployer.lib.helper.client.DeployerHoveringInformation;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Allows Blocks and Entities to render the goggle overlay
 * */
@Mixin(GoggleOverlayRenderer.class)
public abstract class GoggleOverlayRendererMixin {

    @Unique
    private static int deployer$hoverTicks4Special = 0;

    @Unique
    private static int deployer$hoverTicks4Entities = 0;

    @Shadow
    @Final
    private static Map<Object, Outliner.OutlineEntry> outlines;

    @Unique
    private static boolean deployer$renderOverlaySpecial(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        assert mc.gameMode != null;
        if (mc.options.hideGui || mc.gameMode.getPlayerMode() == GameType.SPECTATOR)
            return false;

        IHaveHoveringInformation info = null;
        for(var pair : ClientRegisterHelpers.getSpecialHovers()) {
            if(!pair.getA().getAsBoolean()) continue;
            info = pair.getB();
        }

        if(info == null) {
            deployer$hoverTicks4Special = 0;
            return false;
        }

        for (Outliner.OutlineEntry entry : outlines.values()) {
            if (!entry.isAlive())
                continue;
            Outline outline = entry.getOutline();
            if (outline instanceof ValueBox && !((ValueBox) outline).isPassive)
                return false;
        }

        deployer$hoverTicks4Special++;
        boolean wearingGoggles = GogglesItem.isWearingGoggles(mc.player);
        assert mc.player != null;
        boolean isShifting = mc.player.isShiftKeyDown();

        boolean hasGoggleInformation = info instanceof IHaveGoggleInformation;

        ItemStack item = info.getIcon(isShifting);
        List<Component> tooltip = new ArrayList<>();

        if (hasGoggleInformation && wearingGoggles) {
            IHaveGoggleInformation gte = (IHaveGoggleInformation) info;
            gte.addToGoggleTooltip(tooltip, isShifting);
        }

        if (!hasGoggleInformation) {
            info.addToTooltip(tooltip, isShifting);
        }

        if (tooltip.isEmpty()) {
            deployer$hoverTicks4Special = 0;
            return false;
        }

        GuiRenderingHelpers.renderHoveringArea(guiGraphics, deployer$hoverTicks4Special, deltaTracker, item, tooltip);
        return true;
    }

    @Unique
    private static void deployer$renderOverlay4Entities(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        assert mc.gameMode != null;
        if (mc.options.hideGui || mc.gameMode.getPlayerMode() == GameType.SPECTATOR)
            return;

        HitResult objectMouseOver = mc.hitResult;
        if (!(objectMouseOver instanceof EntityHitResult result)) {
            deployer$hoverTicks4Entities = 0;
            return;
        }

        for (Outliner.OutlineEntry entry : outlines.values()) {
            if (!entry.isAlive())
                continue;
            Outline outline = entry.getOutline();
            if (outline instanceof ValueBox && !((ValueBox) outline).isPassive)
                return;
        }

        Entity entity = result.getEntity();

        deployer$hoverTicks4Entities++;

        boolean wearingGoggles = GogglesItem.isWearingGoggles(mc.player);
        assert mc.player != null;
        boolean isShifting = mc.player.isShiftKeyDown();

        boolean hasGoggleInformation = entity instanceof DeployerGoggleInformation;
        boolean hasHoveringInformation = entity instanceof DeployerHoveringInformation;

        boolean goggleAddedInformation = false;
        boolean hoverAddedInformation = false;

        ItemStack item = AllItems.GOGGLES.asStack();
        List<Component> tooltip = new ArrayList<>();

        if (entity instanceof IHaveCustomOverlayIcon customOverlayIcon)
            item = customOverlayIcon.getIcon(isShifting);

        if (hasGoggleInformation && wearingGoggles) {
            IHaveGoggleInformation gte = (IHaveGoggleInformation) entity;
            goggleAddedInformation = gte.addToGoggleTooltip(tooltip, isShifting);
        }

        if (hasHoveringInformation) {
            if (!tooltip.isEmpty())
                tooltip.add(CommonComponents.EMPTY);
            IHaveHoveringInformation hte = (IHaveHoveringInformation) entity;
            hoverAddedInformation = hte.addToTooltip(tooltip, isShifting);

            if (goggleAddedInformation && !hoverAddedInformation)
                tooltip.removeLast();
        }

        // break early if goggle or hover returned false when present
        if ((hasGoggleInformation && !goggleAddedInformation) && (hasHoveringInformation && !hoverAddedInformation)) {
            deployer$hoverTicks4Entities = 0;
            return;
        }

        if (tooltip.isEmpty()) {
            deployer$hoverTicks4Entities = 0;
            return;
        }

        GuiRenderingHelpers.renderHoveringArea(guiGraphics, deployer$hoverTicks4Entities, deltaTracker, item, tooltip);
    }

    @Inject(method = "renderOverlay", at = @At("HEAD"), cancellable = true)
    private static void renderOverlay(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if(deployer$renderOverlaySpecial(guiGraphics, deltaTracker)) ci.cancel();
        else deployer$renderOverlay4Entities(guiGraphics, deltaTracker);
    }

    @Inject(method = "renderOverlay", at = @At(value = "INVOKE", target = "Ljava/util/ArrayList;<init>()V", shift = At.Shift.AFTER))
    private static void renderOverlay$1(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci, @Local(name = "pos") BlockPos pos, @Local(name = "world") ClientLevel world, @Local(name = "hasGoggleInformation") boolean hasGoggleInformation, @Local(name = "item") LocalRef<ItemStack> item, @Local(name = "isShifting") boolean isShifting) {
        if(!hasGoggleInformation && world.getBlockState(pos).getBlock() instanceof DeployerGoggleInformation info)
            item.set(info.getIcon(isShifting));
    }

    @Definition(id = "be", local = @Local(type = BlockEntity.class, name = "be"))
    @Definition(id = "IDisplayAssemblyExceptions", type = IDisplayAssemblyExceptions.class)
    @Expression("be instanceof IDisplayAssemblyExceptions")
    @Inject(method = "renderOverlay", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static void renderOverlay$2(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci, @Local(name = "pos") BlockPos pos, @Local(name = "world") ClientLevel world, @Local(name = "isShifting") boolean isShifting, @Local(name = "goggleAddedInformation") LocalBooleanRef goggleAddedInformation, @Local(name = "tooltip") List<Component> tooltip, @Local(name = "wearingGoggles") boolean wearingGoggles) {
        if(wearingGoggles && world.getBlockState(pos).getBlock() instanceof DeployerGoggleInformation info)
            goggleAddedInformation.set(goggleAddedInformation.get() || info.addToGoogleTooltip(world, pos, world.getBlockState(pos), tooltip, isShifting));
    }
}
