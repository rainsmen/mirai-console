/*
 * Copyright 2020 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

@file:Suppress("PRE_RELEASE_CLASS", "ClassName")

package net.mamoe.mirai.console.codegen

object ValueSettingCodegen {
    /**
     * The interface
     */
    object PrimitiveValuesCodegen : RegionCodegen("Value.kt"), DefaultInvoke {
        @JvmStatic
        fun main(args: Array<String>) = super.startIndependent()

        override val defaultInvokeArgs: List<KtType>
            get() = KtPrimitives + KtString

        override fun StringBuilder.apply(ktType: KtType) {
            @Suppress("ClassName")
            appendKCode(
                """
                    /**
                     * Represents a non-null [$ktType] value.
                     */
                    interface ${ktType}Value : PrimitiveValue<$ktType>
                """
            )
        }
    }

    object BuiltInSerializerConstantsPrimitivesCodegen : RegionCodegen("_Setting.value.kt"), DefaultInvoke {
        @JvmStatic
        fun main(args: Array<String>) = super.startIndependent()

        override val defaultInvokeArgs: List<KtType> = KtPrimitives + KtString

        override fun StringBuilder.apply(ktType: KtType) {
            appendLine(
                kCode(
                    """
                @JvmStatic
                val ${ktType.standardName}SerializerDescriptor = ${ktType.standardName}.serializer().descriptor 
            """
                ).lines().joinToString("\n") { "    $it" }
            )
        }
    }

    object PrimitiveValuesImplCodegen : RegionCodegen("_PrimitiveValueDeclarations.kt"), DefaultInvoke {
        @JvmStatic
        fun main(args: Array<String>) = super.startIndependent()

        override val defaultInvokeArgs: List<KtType>
            get() = KtPrimitives + KtString

        override fun StringBuilder.apply(ktType: KtType) {
            appendKCode(
                """
internal abstract class ${ktType.standardName}ValueImpl : ${ktType.standardName}Value, SerializerAwareValue<${ktType.standardName}>, KSerializer<Unit> {
    constructor()
    constructor(default: ${ktType.standardName}) {
        _value = default
    }

    private var _value: ${ktType.standardName}? = null

    final override var value: ${ktType.standardName}
        get() = _value ?: error("${ktType.standardName}Value.value should be initialized before get.")
        set(v) {
            if (v != this._value) {
                this._value = v
                onChanged()
            }
        }

    protected abstract fun onChanged()

    final override val serializer: KSerializer<Unit> get() = this
    final override val descriptor: SerialDescriptor get() = BuiltInSerializerConstants.${ktType.standardName}SerializerDescriptor
    final override fun serialize(encoder: Encoder, value: Unit) = ${ktType.standardName}.serializer().serialize(encoder, this.value)
    final override fun deserialize(decoder: Decoder) {
        value = ${ktType.standardName}.serializer().deserialize(decoder)
    }
}
                """
            )
        }

    }

    object Setting_value_PrimitivesImplCodegen : RegionCodegen("_Setting.value.kt"), DefaultInvoke {
        @JvmStatic
        fun main(args: Array<String>) = super.startIndependent()

        override val defaultInvokeArgs: List<KtType>
            get() = KtPrimitives + KtString

        override fun StringBuilder.apply(ktType: KtType) {
            appendKCode(
                """
internal fun Setting.valueImpl(default: ${ktType.standardName}): SerializerAwareValue<${ktType.standardName}> {
    return object : ${ktType.standardName}ValueImpl(default) {
        override fun onChanged() = this@valueImpl.onValueChanged(this)
    }
}
                """
            )
        }
    }

    object Setting_valueImplPrimitiveCodegen : RegionCodegen("_Setting.value.kt"), DefaultInvoke {
        @JvmStatic
        fun main(args: Array<String>) = super.startIndependent()

        override val defaultInvokeArgs: List<KtType>
            get() = KtPrimitives + KtString

        override fun StringBuilder.apply(ktType: KtType) {
            appendKCode(
                """
                ${ktType.standardName}::class -> valueImpl(default as ${ktType.standardName})
                """.trimIndent()
            )
        }
    }


}