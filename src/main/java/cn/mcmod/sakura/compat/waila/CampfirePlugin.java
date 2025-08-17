package cn.mcmod.sakura.compat.waila;

import cn.mcmod.sakura.block.BlockCampfire;
import cn.mcmod.sakura.tileentity.TileEntityCampfire;
import cn.mcmod.sakura.util.I18nUtil;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.item.ItemStack;

import java.util.List;

public class CampfirePlugin implements IWailaDataProvider {
    public static void register(IWailaRegistrar registrar) {
        registrar.registerBodyProvider(new CampfirePlugin(), BlockCampfire.class);
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> tooltip, IWailaDataAccessor accessor,
                                     IWailaConfigHandler config) {
        if (accessor.getTileEntity() instanceof TileEntityCampfire te) {
            if (te.isBurning()) {
                final String burnTime = I18nUtil.format("tooltip.campfire.fire_remain", te.getBurnTime());
                tooltip.add(burnTime);
            }
        }
        return tooltip;
    }
}
