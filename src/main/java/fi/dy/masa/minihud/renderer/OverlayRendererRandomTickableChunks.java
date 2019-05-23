package fi.dy.masa.minihud.renderer;

import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nullable;
import org.lwjgl.opengl.GL11;
import fi.dy.masa.minihud.config.Configs;
import fi.dy.masa.minihud.config.RendererToggle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class OverlayRendererRandomTickableChunks extends OverlayRendererBase
{
    @Nullable public static Vec3d newPos;
    private static final Direction[] HORIZONTALS = new Direction[] { Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST };

    protected final RendererToggle toggle;
    protected Vec3d pos = Vec3d.ZERO;

    public OverlayRendererRandomTickableChunks(RendererToggle toggle)
    {
        this.toggle = toggle;
    }

    @Override
    public boolean shouldRender(MinecraftClient mc)
    {
        return this.toggle.getBooleanValue();
    }

    @Override
    public boolean needsUpdate(Entity entity, MinecraftClient mc)
    {
        if (this.toggle == RendererToggle.OVERLAY_RANDOM_TICKS_FIXED)
        {
            return newPos != null;
        }
        // Player-following renderer
        else if (this.toggle == RendererToggle.OVERLAY_RANDOM_TICKS_PLAYER)
        {
            return entity.x != this.pos.x || entity.z != this.pos.z;
        }

        return false;
    }

    @Override
    public void update(Entity entity, MinecraftClient mc)
    {
        if (this.toggle == RendererToggle.OVERLAY_RANDOM_TICKS_PLAYER)
        {
            this.pos = entity.getPosVector();
        }
        else if (newPos != null)
        {
            this.pos = newPos;
            newPos = null;
        }

        final int color = this.toggle == RendererToggle.OVERLAY_RANDOM_TICKS_PLAYER ?
                Configs.Colors.RANDOM_TICKS_PLAYER_OVERLAY_COLOR.getIntegerValue() :
                Configs.Colors.RANDOM_TICKS_FIXED_OVERLAY_COLOR.getIntegerValue();

        RenderObjectBase renderQuads = this.renderObjects.get(0);
        RenderObjectBase renderLines = this.renderObjects.get(1);
        BUFFER_1.begin(renderQuads.getGlMode(), VertexFormats.POSITION_COLOR);
        BUFFER_2.begin(renderLines.getGlMode(), VertexFormats.POSITION_COLOR);

        Set<ChunkPos> chunks = this.getRandomTickableChunks(this.pos);

        for (ChunkPos pos : chunks)
        {
            this.renderChunkEdgesIfApplicable(pos, chunks, entity, color);
        }

        BUFFER_1.end();
        BUFFER_2.end();

        renderQuads.uploadData(BUFFER_1);
        renderLines.uploadData(BUFFER_2);

        this.lastUpdatePos = new BlockPos(entity);
    }

    protected Set<ChunkPos> getRandomTickableChunks(Vec3d posCenter)
    {
        Set<ChunkPos> set = new HashSet<>();
        final int centerChunkX = ((int) Math.floor(posCenter.x)) >> 4;
        final int centerChunkZ = ((int) Math.floor(posCenter.z)) >> 4;
        final double maxRange = 128D * 128D;
        final int r = 9;

        for (int cz = centerChunkZ - r; cz <= centerChunkZ + r; ++cz)
        {
            for (int cx = centerChunkX - r; cx <= centerChunkX + r; ++cx)
            {
                double dx = (double) (cx * 16 + 8) - posCenter.x;
                double dz = (double) (cz * 16 + 8) - posCenter.z;

                if ((dx * dx + dz * dz) < maxRange)
                {
                    set.add(new ChunkPos(cx, cz));
                }
            }
        }

        return set;
    }

    protected void renderChunkEdgesIfApplicable(ChunkPos pos, Set<ChunkPos> chunks, Entity entity, int color)
    {
        for (Direction side : HORIZONTALS)
        {
            ChunkPos posTmp = new ChunkPos(pos.x + side.getOffsetX(), pos.z + side.getOffsetZ());

            if (chunks.contains(posTmp) == false)
            {
                RenderUtils.renderVerticalWallsOfLinesWithinRange(BUFFER_1, BUFFER_2,
                        side.getAxis(), this.getStartPos(pos, side), this.getEndPos(pos, side),
                        512, 256, 16, 16, entity, color);
            }
        }
    }

    protected BlockPos getStartPos(ChunkPos chunkPos, Direction side)
    {
        switch (side)
        {
            case NORTH:     return new BlockPos( chunkPos.x << 4      , 0,  chunkPos.z << 4      );
            case SOUTH:     return new BlockPos( chunkPos.x << 4      , 0, (chunkPos.z << 4) + 16);
            case WEST:      return new BlockPos( chunkPos.x << 4      , 0,  chunkPos.z << 4      );
            case EAST:      return new BlockPos((chunkPos.x << 4) + 16, 0,  chunkPos.z << 4      );
            default:
        }

        return BlockPos.ORIGIN;
    }

    protected BlockPos getEndPos(ChunkPos chunkPos, Direction side)
    {
        switch (side)
        {
            case NORTH:     return new BlockPos((chunkPos.x << 4) + 16, 256,  chunkPos.z << 4      );
            case SOUTH:     return new BlockPos((chunkPos.x << 4) + 16, 256, (chunkPos.z << 4) + 16);
            case WEST:      return new BlockPos( chunkPos.x << 4      , 256, (chunkPos.z << 4) + 16);
            case EAST:      return new BlockPos((chunkPos.x << 4) + 16, 256, (chunkPos.z << 4) + 16);
            default:
        }

        return BlockPos.ORIGIN;
    }

    @Override
    public void allocateGlResources()
    {
        this.allocateBuffer(GL11.GL_QUADS);
        this.allocateBuffer(GL11.GL_LINES);
    }
}
