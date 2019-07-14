package fi.dy.masa.minihud;

import java.io.File;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.google.common.collect.ImmutableList;
import com.mumfrey.liteloader.Configurable;
import com.mumfrey.liteloader.LiteMod;
import com.mumfrey.liteloader.PluginChannelListener;
import com.mumfrey.liteloader.modconfig.ConfigPanel;
import fi.dy.masa.malilib.event.InitializationHandler;
import fi.dy.masa.minihud.config.gui.MiniHudConfigPanel;
import fi.dy.masa.minihud.util.DataStorage;
import net.minecraft.network.PacketBuffer;

public class LiteModMiniHud implements LiteMod, Configurable, PluginChannelListener
{
    public static final Logger logger = LogManager.getLogger(Reference.MOD_ID);

    public static final String CHANNEL_CARPET_CLIENT = "CarpetClient";

    private final ImmutableList<String> pluginChannels = ImmutableList.of(CHANNEL_CARPET_CLIENT);

    public LiteModMiniHud()
    {
    }

    @Override
    public String getName()
    {
        return Reference.MOD_NAME;
    }

    @Override
    public String getVersion()
    {
        return Reference.MOD_VERSION;
    }

    @Override
    public Class<? extends ConfigPanel> getConfigPanelClass()
    {
        return MiniHudConfigPanel.class;
    }

    @Override
    public void init(File configPath)
    {
        InitializationHandler.getInstance().registerInitializationHandler(new InitHandler());
    }

    @Override
    public void upgradeSettings(String version, File configPath, File oldConfigPath)
    {
    }

    @Override
    public void onCustomPayload(String channel, PacketBuffer data)
    {
        if (CHANNEL_CARPET_CLIENT.equals(channel))
        {
            DataStorage.getInstance().updateStructureDataFromServer(data);
        }
    }

    @Override
    public List<String> getChannels()
    {
        return this.pluginChannels;
    }
}