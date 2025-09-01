package net.liukrast.deployer.lib.registry;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.logistics.box.PackageStyles;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.liukrast.deployer.lib.DeployerConstants;
import net.minecraft.resources.ResourceLocation;

public class DeployerPartialModels {
    private DeployerPartialModels() {}

    static {
        for(PackageStyles.PackageStyle style : DeployerPackages.STYLES) {
            ResourceLocation key = DeployerConstants.id(style.getItemId().getPath());
            PartialModel model = PartialModel.of(DeployerConstants.id("item/" + key.getPath()));
            AllPartialModels.PACKAGES.put(key, model);
            if (!style.rare())
                AllPartialModels.PACKAGES_TO_HIDE_AS.add(model);
            AllPartialModels.PACKAGE_RIGGING.put(key, PartialModel.of(style.getRiggingModel()));
        }

    }

    public static void init() {}
}
