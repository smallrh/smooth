package com.mice.smooth.util

class TagUtil {

    fun converseTags(tags: List<String>): String {
        // 定义一个映射表，将英文标签映射到中文标签
        val tagMapping = mapOf(
            "yanqing" to "言情",
            "kehuan" to "科幻",
            "xianxia" to "仙侠",
            "zhanzheng" to "战争",
            "qihuan" to "奇幻",
            "xuanhuan" to "玄幻",
            "dushi" to "都市",
            "" to "",
            "qita" to "其他"
        )

        // 使用映射表转换标签
        val convertedTags = tags.map { tag -> tagMapping[tag] ?: tag } // 如果没有找到对应的中文标签，则返回原标签

        // 将转换后的标签列表重新组合成字符串
        return convertedTags.joinToString(",") // 使用逗号连接
    }

}