package net.liukrast.fluid.content;

import com.mojang.serialization.Codec;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.logistics.box.PackageStyles;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import com.simibubi.create.foundation.fluid.FluidHelper;
import com.simibubi.create.foundation.item.ItemHelper;
import net.liukrast.deployer.lib.logistics.GenericPackageOrderData;
import net.liukrast.deployer.lib.logistics.packager.AbstractInventorySummary;
import net.liukrast.deployer.lib.logistics.packager.GenericUnpackingHandler;
import net.liukrast.deployer.lib.logistics.packager.StockInventoryType;
import net.liukrast.deployer.lib.logistics.packagerLink.GenericRequestPromise;
import net.liukrast.deployer.lib.logistics.stockTicker.GenericOrderContained;
import net.liukrast.fluid.registry.RegisterDataComponents;
import net.liukrast.fluid.registry.RegisterItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FluidStockInventoryType extends StockInventoryType<Fluid, FluidStack, IFluidHandler> {
    private static final Codec<GenericRequestPromise<FluidStack>> REQUEST_CODEC =  GenericRequestPromise.simpleCodec(FluidStack.CODEC);
    private static final IValueHandler<Fluid, FluidStack, IFluidHandler> VALUE_HANDLER = new IValueHandler<>() {
        @Override
        public Codec<FluidStack> codec() {
            return FluidStack.CODEC;
        }

        @Override
        public Fluid fromValue(FluidStack key) {
            return key.getFluid();
        }

        @Override
        public boolean equals(FluidStack a, FluidStack b) {
            return FluidStack.isSameFluidSameComponents(a, b);
        }

        @Override
        public boolean test(FilterItemStack filter, Level level, FluidStack value) {
            return filter.test(level, value);
        }

        @Override
        public int getCount(FluidStack value) {
            return value.getAmount();
        }

        @Override
        public void setCount(FluidStack value, int count) {
            value.setAmount(count);
        }

        @Override
        public boolean isEmpty(FluidStack stack) {
            return stack.isEmpty();
        }

        @Override
        public FluidStack create(Fluid key, int amount) {
            return new FluidStack(key, amount);
        }

        @Override
        public void shrink(FluidStack stack, int amount) {
            stack.shrink(amount);
        }

        @Override
        public FluidStack copyWithCount(FluidStack stack, int amount) {
            return stack.copyWithAmount(amount);
        }

        @Override
        public FluidStack copy(FluidStack stack) {
            return stack.copy();
        }

        @Override
        public boolean isStackable(FluidStack stack) {
            return true; //TODO: Check this but it should be correct
        }

        @Override
        public FluidStack empty() {
            return FluidStack.EMPTY;
        }

        @Override
        public int getMaxStackSize(FluidStack stack) {
            return 1000; //TODO: Check this but should be correct
        }
    };
    private static final IStorageHandler<Fluid, FluidStack, IFluidHandler> STORAGE_HANDLER = new IStorageHandler<>() {
        @Override
        public int getSlots(IFluidHandler handler) {
            return handler.getTanks();
        }

        @Override
        public FluidStack getStackInSlot(IFluidHandler handler, int slot) {
            return handler.getFluidInTank(slot);
        }

        @Override
        public int maxCountPerSlot() {
            return 1000; //TODO: Check
        }

        @Override
        public FluidStack extract(IFluidHandler handler, FluidStack value, boolean simulate) {
            return handler.drain(value, simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE);
        }

        @Override
        public int fill(IFluidHandler handler, FluidStack stack, boolean simulate) {
            return stack.getAmount() - handler.fill(stack.copy(), simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE);
        }

        @Override
        public FluidStack setInSlot(IFluidHandler handler, int slot, FluidStack value, boolean simulate) {
            //TODO: Check if this is correct
            int result = fill(handler, value, simulate);
            return new FluidStack(value.getFluid(), result);
        }

        @Override
        public boolean isBulky(Fluid key) {
            return false;
        }

        @Override
        public IFluidHandler create(int slots) {
            return new FluidTank(1000);
        }

        @Override
        public int getMaxPackageSlots() {
            return 1;
        }

        @Override
        public FluidStack insertItem(IFluidHandler handler, int i, FluidStack value, boolean simulate) {
            return new FluidStack(value.getFluid(), fill(handler, value, simulate));
        }
    };

    private static final INetworkHandler<Fluid, FluidStack, IFluidHandler> NETWORK_HANDLER = new INetworkHandler<>() {
        @Override
        public Codec<GenericRequestPromise<FluidStack>> requestCodec() {
            return REQUEST_CODEC;
        }

        @Override
        public AbstractInventorySummary<Fluid, FluidStack> create() {
            return new FluidInventorySummary();
        }

        @Override
        public AbstractInventorySummary<Fluid, FluidStack> empty() {
            return FluidInventorySummary.EMPTY.get();
        }

        @Override
        public DataComponentType<? super GenericPackageOrderData<FluidStack>> getComponent() {
            return RegisterDataComponents.BOTTLE_ORDER_DATA.get();
        }
    };

    private static final IPackageHandler<Fluid, FluidStack, IFluidHandler> PACKAGE_HANDLER = new IPackageHandler<>() {
        @Override
        public void setBoxContent(ItemStack stack, IFluidHandler inventory) {
            stack.set(RegisterDataComponents.BOTTLE_CONTENTS, SimpleFluidContent.copyOf(inventory.getFluidInTank(0))); //TODO: Check this
        }

        @Override
        public ItemStack getRandomBox() {
            return PackageStyles.getRandomBox(); //TODO: Change with only fluid packages!!!
        }

        @Override
        public IFluidHandler getContents(ItemStack box) {
            FluidTank newInv = new FluidTank(1000); //TODO: Check this
            SimpleFluidContent contents = box.getOrDefault(RegisterDataComponents.BOTTLE_CONTENTS.get(), SimpleFluidContent.EMPTY);
            newInv.fill(contents.copy(), IFluidHandler.FluidAction.EXECUTE); //TODO: Check this again
            return newInv;
        }

        @Override
        public DataComponentType<GenericPackageOrderData<FluidStack>> packageOrderData() {
            return RegisterDataComponents.BOTTLE_ORDER_DATA.get();
        }

        @Override
        public DataComponentType<GenericOrderContained<FluidStack>> packageOrderContext() {
            return RegisterDataComponents.BOTTLE_ORDER_CONTEXT.get();
        }
    };

    @Override
    public @NotNull IValueHandler<Fluid, FluidStack, IFluidHandler> valueHandler() {
        return VALUE_HANDLER;
    }

    @Override
    public @NotNull IStorageHandler<Fluid, FluidStack, IFluidHandler> storageHandler() {
        return STORAGE_HANDLER;
    }

    @Override
    public @NotNull INetworkHandler<Fluid, FluidStack, IFluidHandler> networkHandler() {
        return NETWORK_HANDLER;
    }

    @Override
    public @NotNull IPackageHandler<Fluid, FluidStack, IFluidHandler> packageHandler() {
        return PACKAGE_HANDLER;
    }

    @Override
    public BlockCapability<IFluidHandler, @Nullable Direction> getCapability() {
        return Capabilities.FluidHandler.BLOCK;
    }
}
