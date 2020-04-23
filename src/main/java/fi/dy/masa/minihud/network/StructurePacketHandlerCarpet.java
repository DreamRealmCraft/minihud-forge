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

public class StructurePacketHandlerCarpet implements IPluginChannelHandler
{
    public static final List<Identifier> CHANNELS = ImmutableList.of(new Identifier("carpet:structures"));
    public static final int PACKET_S2C_DATA = 0;
    public static final int VERSION = 1;

    public static final StructurePacketHandlerCarpet INSTANCE = new StructurePacketHandlerCarpet();

    private boolean registered;
    private int timeout;

    public void reset()
    {
        this.registered = false;
    }

    @Override
    public List<Identifier> getChannels()
    {
        return CHANNELS;
    }

    @Override
    public void onPacketReceived(PacketByteBuf buf)
    {
        int id = buf.readVarInt();

        if (id == PACKET_S2C_DATA)
        {
            CompoundTag tag = buf.readCompoundTag();

            if (tag != null)
            {
                // Normal structure data packet
                if (this.registered && tag.containsKey("Structures", Constants.NBT.TAG_LIST))
                {
                    ListTag structures = tag.getList("Structures", Constants.NBT.TAG_COMPOUND);
                    DataStorage.getInstance().addOrUpdateStructuresFromServer(structures, this.timeout, false);
                }
                // Metadata packet upon channel registration
                else if (tag.containsKey("Version", Constants.NBT.TAG_INT) &&
                         tag.containsKey("Timeout", Constants.NBT.TAG_INT) &&
                         tag.getInt("Version") == VERSION)
                {
                    this.timeout = tag.getInt("Timeout");
                    this.registered = true;
                }
            }
        }
    }
}
