package cn.mcmod.sakura.packet;

import cn.mcmod.sakura.item.katana.ItemSheath;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.jetbrains.annotations.Nullable;

public class PacketKeyMessageHandler implements IMessageHandler<PacketKeyMessage, IMessage> {

    @Nullable
    @Override
    public IMessage onMessage(PacketKeyMessage message, MessageContext ctx) {
        final EntityPlayer player = ctx.getServerHandler().player;

        ItemStack itemStack;
        ItemSheath itemSheath;

        itemStack = player.getHeldItemMainhand();
        itemSheath = getItemSheath(itemStack);
        if (itemSheath != null) {
            itemSheath.sheathIn(player);
            return null;
        }

        itemStack = player.getHeldItemOffhand();
        itemSheath = getItemSheath(itemStack);
        if (itemSheath != null) {
            itemSheath.sheathIn(player);
            return null;
        }
        return null;
    }

    @Nullable
    private ItemSheath getItemSheath(ItemStack stack) {
        final Item item = stack.getItem();
        if (item instanceof ItemSheath) {
            return (ItemSheath) item;
        } else {
            return null;
        }
    }
}