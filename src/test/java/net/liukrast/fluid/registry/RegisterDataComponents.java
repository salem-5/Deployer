package net.liukrast.fluid.registry;

import net.liukrast.deployer.lib.logistics.GenericPackageOrderData;
import net.liukrast.deployer.lib.logistics.stockTicker.GenericOrderContained;
import net.liukrast.fluid.FluidConstants;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RegisterDataComponents {
    private RegisterDataComponents() {}

    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, FluidConstants.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SimpleFluidContent>> BOTTLE_CONTENTS = DATA_COMPONENTS.register("bottle_contents", () -> DataComponentType.<SimpleFluidContent>builder()
            .persistent(SimpleFluidContent.CODEC)
            .networkSynchronized(SimpleFluidContent.STREAM_CODEC)
            .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<GenericPackageOrderData<FluidStack>>> BOTTLE_ORDER_DATA = DATA_COMPONENTS.register("bottle_order_data", () -> DataComponentType.<GenericPackageOrderData<FluidStack>>builder()
            .persistent(GenericPackageOrderData.simpleCodec(FluidStack.CODEC))
            .networkSynchronized(GenericPackageOrderData.simpleStreamCodec(FluidStack.STREAM_CODEC))
            .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<GenericOrderContained<FluidStack>>> BOTTLE_ORDER_CONTEXT = DATA_COMPONENTS.register("bottle_order_context", () -> DataComponentType.<GenericOrderContained<FluidStack>>builder()
            .persistent(GenericOrderContained.simpleCodec(FluidStack.CODEC))
            .networkSynchronized(GenericOrderContained.simpleStreamCodec(FluidStack.STREAM_CODEC))
            .build());

    public static void register(IEventBus eventBus) {
        DATA_COMPONENTS.register(eventBus);
    }
}
