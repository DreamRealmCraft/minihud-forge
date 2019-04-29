package fi.dy.masa.minihud.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import fi.dy.masa.minihud.util.DataStorage;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.packet.BlockUpdateS2CPacket;
import net.minecraft.client.network.packet.ChatMessageS2CPacket;
import net.minecraft.client.network.packet.ChunkDataS2CPacket;
import net.minecraft.client.network.packet.ChunkDeltaUpdateS2CPacket;
import net.minecraft.client.network.packet.PlayerListHeaderS2CPacket;
import net.minecraft.client.network.packet.WorldTimeUpdateS2CPacket;
import net.minecraft.world.chunk.ChunkPos;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler
{
    @Inject(method = "onChatMessage", at = @At("RETURN"))
    private void onChatMessage(ChatMessageS2CPacket packet, CallbackInfo ci)
    {
        DataStorage.getInstance().onChatMessage(packet.getMessage());
    }

    @Inject(method = "onWorldTimeUpdate", at = @At("RETURN"))
    private void onTimeUpdate(WorldTimeUpdateS2CPacket packetIn, CallbackInfo ci)
    {
        DataStorage.getInstance().onServerTimeUpdate(packetIn.getTime());
    }

    @Inject(method = "onPlayerListHeader", at = @At("RETURN"))
    private void onHandlePlayerListHeaderFooter(PlayerListHeaderS2CPacket packetIn, CallbackInfo ci)
    {
        DataStorage.getInstance().handleCarpetServerTPSData(packetIn.getFooter());
    }

    @Inject(method = "onChunkData", at = @At("RETURN"))
    private void markChunkChangedFullChunk(ChunkDataS2CPacket packet, CallbackInfo ci)
    {
        DataStorage.getInstance().markChunkForHeightmapCheck(packet.getX(), packet.getZ());
    }

    @Inject(method = "onBlockUpdate", at = @At("RETURN"))
    private void markChunkChangedBlockChange(BlockUpdateS2CPacket packet, CallbackInfo ci)
    {
        DataStorage.getInstance().markChunkForHeightmapCheck(packet.getPos().getX() >> 4, packet.getPos().getZ() >> 4);
    }

    @Inject(method = "onChunkDeltaUpdate", at = @At("RETURN"))
    private void markChunkChangedMultiBlockChange(ChunkDeltaUpdateS2CPacket packet, CallbackInfo ci)
    {
        ChunkPos pos = ((IMixinChunkDeltaUpdateS2CPacket) packet).getChunkPos();
        DataStorage.getInstance().markChunkForHeightmapCheck(pos.x, pos.z);
    }
}
