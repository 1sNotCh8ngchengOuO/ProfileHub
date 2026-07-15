package org.profilehub.parser;

import java.util.Map;

/**
 * options.txt key 的白名单分类。
 *
 * 采用白名单模式：只有明确列出的 key 才会被同步，
 * 不在白名单中的 key 一律忽略。
 *
 * 分类依据设计文档第十二章"同步范围"：
 * - video:        视频设置（FOV、亮度、渲染距离、粒子等）
 * - audio:        音量设置
 * - language:     语言
 * - accessibility: 辅助功能（字幕、鼠标灵敏度、聊天透明度等）
 */
public final class OptionsCategory {

    private OptionsCategory() {}

    public static final String VIDEO = "video";
    public static final String AUDIO = "audio";
    public static final String LANGUAGE = "language";
    public static final String ACCESSIBILITY = "accessibility";

    /**
     * 白名单：只有在这张表里的 key 才会被同步。
     * key 不在表中 → 直接忽略，不做任何处理。
     */
    private static final Map<String, String> WHITELIST = Map.ofEntries(
            // ========== language ==========
            Map.entry("lang", LANGUAGE),

            // ========== video ==========
            Map.entry("fov", VIDEO),
            Map.entry("fovEffectScale", VIDEO),
            Map.entry("gamma", VIDEO),
            Map.entry("renderDistance", VIDEO),
            Map.entry("simulationDistance", VIDEO),
            Map.entry("entityDistanceScaling", VIDEO),
            Map.entry("graphicsMode", VIDEO),
            Map.entry("ao", VIDEO),
            Map.entry("particles", VIDEO),
            Map.entry("maxFps", VIDEO),
            Map.entry("enableVsync", VIDEO),
            Map.entry("fullscreen", VIDEO),
            Map.entry("guiScale", VIDEO),
            Map.entry("entityShadows", VIDEO),
            Map.entry("renderClouds", VIDEO),
            Map.entry("forceUnicodeFont", VIDEO),
            Map.entry("bobView", VIDEO),
            Map.entry("biomeBlendRadius", VIDEO),
            Map.entry("mipmapLevels", VIDEO),
            Map.entry("screenEffectScale", VIDEO),
            Map.entry("darknessEffectScale", VIDEO),
            Map.entry("darkMojangStudiosBackground", VIDEO),
            Map.entry("hideLightningFlashes", VIDEO),
            Map.entry("prioritizeChunkUpdates", VIDEO),
            Map.entry("showAutosaveIndicator", VIDEO),

            // ========== audio ==========
            Map.entry("soundDevice", AUDIO),
            Map.entry("soundCategory_master", AUDIO),
            Map.entry("soundCategory_music", AUDIO),
            Map.entry("soundCategory_record", AUDIO),
            Map.entry("soundCategory_weather", AUDIO),
            Map.entry("soundCategory_block", AUDIO),
            Map.entry("soundCategory_hostile", AUDIO),
            Map.entry("soundCategory_neutral", AUDIO),
            Map.entry("soundCategory_player", AUDIO),
            Map.entry("soundCategory_ambient", AUDIO),
            Map.entry("soundCategory_voice", AUDIO),
            Map.entry("showSubtitles", AUDIO),
            Map.entry("directionalAudio", AUDIO),

            // ========== accessibility ==========
            Map.entry("narrator", ACCESSIBILITY),
            Map.entry("autoJump", ACCESSIBILITY),
            Map.entry("autoSuggestions", ACCESSIBILITY),
            Map.entry("chatColors", ACCESSIBILITY),
            Map.entry("chatLinks", ACCESSIBILITY),
            Map.entry("chatLinksPrompt", ACCESSIBILITY),
            Map.entry("chatVisibility", ACCESSIBILITY),
            Map.entry("chatOpacity", ACCESSIBILITY),
            Map.entry("chatLineSpacing", ACCESSIBILITY),
            Map.entry("chatHeightFocused", ACCESSIBILITY),
            Map.entry("chatHeightUnfocused", ACCESSIBILITY),
            Map.entry("chatScale", ACCESSIBILITY),
            Map.entry("chatWidth", ACCESSIBILITY),
            Map.entry("chatDelay", ACCESSIBILITY),
            Map.entry("textBackgroundOpacity", ACCESSIBILITY),
            Map.entry("backgroundForChatOnly", ACCESSIBILITY),
            Map.entry("hideServerAddress", ACCESSIBILITY),
            Map.entry("reducedDebugInfo", ACCESSIBILITY),
            Map.entry("mouseSensitivity", ACCESSIBILITY),
            Map.entry("mouseWheelSensitivity", ACCESSIBILITY),
            Map.entry("discrete_mouse_scroll", ACCESSIBILITY),
            Map.entry("rawMouseInput", ACCESSIBILITY),
            Map.entry("invertYMouse", ACCESSIBILITY),
            Map.entry("touchscreen", ACCESSIBILITY),
            Map.entry("toggleCrouch", ACCESSIBILITY),
            Map.entry("toggleSprint", ACCESSIBILITY),
            Map.entry("highContrast", ACCESSIBILITY),
            Map.entry("glintSpeed", ACCESSIBILITY),
            Map.entry("glintStrength", ACCESSIBILITY),
            Map.entry("damageTiltStrength", ACCESSIBILITY),
            Map.entry("notificationDisplayTime", ACCESSIBILITY),
            Map.entry("operatorItemsTab", ACCESSIBILITY)
    );

    /**
     * 查询一个 key 是否在白名单中（即是否应该被同步）。
     */
    public static boolean isSyncable(String key) {
        return WHITELIST.containsKey(key);
    }

    /**
     * 返回 key 所属的分类。
     * 不在白名单中的 key 返回 null。
     */
    public static String categorize(String key) {
        return WHITELIST.get(key);
    }
}