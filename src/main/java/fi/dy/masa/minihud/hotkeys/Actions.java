package fi.dy.masa.minihud.hotkeys;

import fi.dy.masa.malilib.action.ActionUtils;
import fi.dy.masa.malilib.action.NamedAction;
import fi.dy.masa.malilib.config.option.HotkeyedBooleanConfig;
import fi.dy.masa.malilib.listener.EventListener;
import fi.dy.masa.minihud.Reference;
import fi.dy.masa.minihud.config.Configs;
import fi.dy.masa.minihud.config.InfoLine;
import fi.dy.masa.minihud.config.RendererToggle;
import fi.dy.masa.minihud.config.StructureToggle;
import fi.dy.masa.minihud.data.DataStorage;
import fi.dy.masa.minihud.gui.ConfigScreen;
import fi.dy.masa.minihud.gui.GuiShapeEditor;

public class Actions
{
    public static final NamedAction OPEN_CONFIG_SCREEN              = register("openConfigScreen", ConfigScreen::open);
    public static final NamedAction OPEN_SHAPE_EDITOR               = register("openShapeEditor", GuiShapeEditor::openShapeEditor);
    public static final NamedAction SET_DISTANCE_REFERENCE_POINT    = register("setDistanceReferencePoint", DataStorage.getInstance()::setDistanceReferencePoint);

    public static void init()
    {
        register("toggleInfoLinesRendering",  Configs.Generic.INFO_LINES_RENDERING_TOGGLE);
        register("toggleOverlaysRendering",   Configs.Generic.OVERLAYS_RENDERING_TOGGLE);

        for (InfoLine line : InfoLine.VALUES)
        {
            ActionUtils.registerToggle(Reference.MOD_INFO, line.getName(), line.getBooleanConfig());
        }

        for (RendererToggle toggle : RendererToggle.VALUES)
        {
            ActionUtils.registerToggle(Reference.MOD_INFO, toggle.getName(), toggle.getBooleanConfig());
        }

        for (StructureToggle toggle : StructureToggle.VALUES)
        {
            ActionUtils.registerToggle(Reference.MOD_INFO, toggle.getName(), toggle.getBooleanConfig());
        }
    }

    private static NamedAction register(String name, EventListener action)
    {
        return ActionUtils.register(Reference.MOD_INFO, name, action);
    }

    private static NamedAction register(String name, HotkeyedBooleanConfig config)
    {
        return ActionUtils.registerToggleKey(Reference.MOD_INFO, name, config);
    }
}
