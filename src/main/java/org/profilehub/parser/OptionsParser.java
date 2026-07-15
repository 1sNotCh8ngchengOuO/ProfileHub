package org.profilehub.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * options.txt 解析器。
 *
 * 读取 Minecraft 原版 options.txt 文件，
 * 将其解析为 key-value 对，并按分类整理。
 * 只有白名单中的 key 才会被保留。
 *
 * options.txt 格式：每行一条 "key:value"
 * 例如：fov:0.8、lang:zh_cn、gamma:1.0
 */
public final class OptionsParser {

    private OptionsParser() {}

    /**
     * 解析结果。
     * 包含全量原始数据 + 按白名单分类整理好的数据。
     */
    public static final class Result {
        public final Map<String, String> all;
        public final Map<String, String> video;
        public final Map<String, String> audio;
        public final Map<String, String> language;
        public final Map<String, String> accessibility;

        private Result(Map<String, String> all,
                       Map<String, String> video,
                       Map<String, String> audio,
                       Map<String, String> language,
                       Map<String, String> accessibility) {
            this.all = all;
            this.video = video;
            this.audio = audio;
            this.language = language;
            this.accessibility = accessibility;
        }
    }

    /**
     * 解析 options.txt 并返回白名单分类结果。
     *
     * @param optionsFile options.txt 的路径
     * @return 解析后的分类结果（仅包含白名单中的 key）
     * @throws IOException 如果文件读取失败
     */
    public static Result parse(Path optionsFile) throws IOException {
        // LinkedHashMap 保持 options.txt 中 key 的原始顺序，方便调试
        Map<String, String> all = new LinkedHashMap<>();

        // 文件不存在时返回空结果，避免 NPE
        if (!Files.exists(optionsFile)) {
            return emptyResult();
        }

        // 逐行读取，Files.readAllLines 会一次性把整个文件读入内存
        for (String line : Files.readAllLines(optionsFile)) {
            // 去除首尾空白字符（空格、tab 等）
            String trimmed = line.trim();
            // 跳过空行和以 # 开头的注释行
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                continue;
            }

            // 找到第一个冒号的位置
            // 例如 "fov:0.8" → colonIndex = 3
            int colonIndex = trimmed.indexOf(':');
            // 没有冒号的行不是有效的键值对，跳过
            if (colonIndex < 0) {
                continue;
            }

            // 冒号前是 key，冒号后是 value
            // 例如 "fov:0.8" → key="fov", value="0.8"
            String key = trimmed.substring(0, colonIndex);
            String value = trimmed.substring(colonIndex + 1);

            // 白名单过滤：只保留需要同步的 key
            if (OptionsCategory.isSyncable(key)) {
                all.put(key, value);
            }
        }

        // 解析完成后，按分类整理并返回
        return categorize(all);
    }

    /**
     * 将已解析的 key-value 按白名单分类整理。
     */
    private static Result categorize(Map<String, String> all) {
        Map<String, String> video = new LinkedHashMap<>();
        Map<String, String> audio = new LinkedHashMap<>();
        Map<String, String> language = new LinkedHashMap<>();
        Map<String, String> accessibility = new LinkedHashMap<>();

        for (Map.Entry<String, String> entry : all.entrySet()) {
            String key = entry.getKey();
            String category = OptionsCategory.categorize(key);

            if (category == null) {
                continue;
            }

            switch (category) {
                case OptionsCategory.VIDEO -> video.put(key, entry.getValue());
                case OptionsCategory.AUDIO -> audio.put(key, entry.getValue());
                case OptionsCategory.LANGUAGE -> language.put(key, entry.getValue());
                case OptionsCategory.ACCESSIBILITY -> accessibility.put(key, entry.getValue());
            }
        }

        return new Result(all, video, audio, language, accessibility);
    }

    /**
     * 返回空结果。
     */
    private static Result emptyResult() {
        Map<String, String> empty = Map.of();
        return new Result(empty, empty, empty, empty, empty);
    }
}