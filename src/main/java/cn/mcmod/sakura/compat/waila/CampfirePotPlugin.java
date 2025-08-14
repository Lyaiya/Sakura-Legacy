package cn.mcmod.sakura.compat.waila;

import cn.mcmod.sakura.block.BlockCampfirePot;
import cn.mcmod.sakura.tileentity.TileEntityCampfirePot;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

import java.util.List;

public class CampfirePotPlugin implements IWailaDataProvider {
    public static void register(IWailaRegistrar registrar) {
        registrar.registerBodyProvider(new CampfirePotPlugin(), BlockCampfirePot.class);
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> tooltip, IWailaDataAccessor accessor,
                                     IWailaConfigHandler config) {
        if (accessor.getTileEntity() instanceof TileEntityCampfirePot te) {
            tooltip.add(I18n.format("sakura.tooltip.campfire.fire_remain", te.getField(TileEntityCampfirePot.ID_BURN_TIME)));
        }
        return tooltip;
    }

}
