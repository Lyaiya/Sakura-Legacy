package cn.mcmod.sakura.block.fluid;

import cn.mcmod.sakura.SakuraMain;
import cn.mcmod.sakura.util.RLUtil;
import net.minecraftforge.fluids.Fluid;

public class FluidBasic extends Fluid {
    public FluidBasic(String name) {
        super(SakuraMain.MODID + "." + name, RLUtil.of("blocks/" + name + "_still"), RLUtil.of("blocks/" + name + "_flow"));
    }
}