package cn.mcmod.sakura.packet;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import org.jetbrains.annotations.UnknownNullability;

public class PacketKeyMessage implements IMessage {
    @UnknownNullability
    private String sender;

    public PacketKeyMessage() {
    }

    public PacketKeyMessage(String sender) {
        this.sender = sender;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        sender = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, sender);
    }
}