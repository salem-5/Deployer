package net.liukrast.repackage.registry;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import net.liukrast.repackage.RepackagedConstants;

public class RepackagedPartialModels {
    private RepackagedPartialModels() {}

    public static final PartialModel
            FLUID_PACKAGER_TRAY = block("fluid_packager/tray"),
            FLUID_PACKAGER_HATCH_OPEN = block("fluid_packager/hatch_open"), PACKAGER_HATCH_CLOSED = block("fluid_packager/hatch_closed");


    private static PartialModel block(String path) {
        return PartialModel.of(RepackagedConstants.id("block/" + path));
    }

    public static void init() {}
}
