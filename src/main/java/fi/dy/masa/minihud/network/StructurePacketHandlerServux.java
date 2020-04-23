package fi.dy.masa.minihud.network;

import java.util.List;
import com.google.common.collect.ImmutableList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import fi.dy.masa.malilib.network.IPluginChannelHandler;
import fi.dy.masa.malilib.util.Constants;
import fi.dy.masa.minihud.util.DataStorage;

public class StructurePacketHandlerServux implements IPluginChannelHandler
{
    public static final int PROTOCOL_VERSION = 1;
    public static final int PACKET_S2C_METADATA = 1;
    public static final int PACKET_S2C_STRUCTURE_DATA = 2;

    public static final StructurePacketHandlerServux INSTANCE = new StructurePacketHandlerServux();

    private final Identifier channel = new Identifier("servux:structures");
    private final List<Identifier> channels = ImmutableList.of(this.channel);
    private boolean registered;
    private int timeout;

    public void reset()
    {
        this.registered = false;
    }

    @Override
    public List<Identifier> getChannels()
    {
        return this.channels;
    }

    @Override
    public void onPacketReceived(PacketByteBuf buf)
    {
        int id = buf.readVarInt();

        if (id == PACKET_S2C_STRUCTURE_DATA && this.registered)
        {
            CompoundTag tag = buf.readCompoundTag();

            if (tag != null)
            {
                ListTag structures = tag.getList("Structures", Constants.NBT.TAG_COMPOUND);
                DataStorage.getInstance().addOrUpdateStructuresFromServer(structures, this.timeout);
            }
        }
        else if (id == PACKET_S2C_METADATA)
        {
            CompoundTag tag = buf.readCompoundTag();

            if (tag != null &&
                tag.getInt("version") == PROTOCOL_VERSION &&
                tag.getString("id").equals(this.channel.toString()))
            {
                this.timeout = tag.getInt("timeout");
                this.registered = true;
            }
        }
    }
}