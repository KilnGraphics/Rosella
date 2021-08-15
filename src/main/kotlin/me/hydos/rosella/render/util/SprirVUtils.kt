package me.hydos.rosella.render.util

import me.hydos.rosella.render.resource.Resource
import me.hydos.rosella.render.shader.ShaderType
import org.lwjgl.system.MemoryUtil.NULL
import org.lwjgl.system.NativeResource
import org.lwjgl.util.shaderc.Shaderc.*
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer

var compiler: Long = 0

fun compileShaderFile(shader: Resource, shaderType: ShaderType): SpirV {
    val source = shader.openStream().readBytes().decodeToString()
    return compileShader(shader.identifier.file, source, shaderType)
}

fun init() {
    compiler = shaderc_compiler_initialize()
    if (compiler == NULL) {
        throw RuntimeException("Failed to create shader compiler")
    }
}

fun compileShader(filename: String, source: String, shaderType: ShaderType): SpirV {
    val result = shaderc_compile_into_spv(compiler, source, shaderType.shaderCType, filename, "main", NULL)
    if (result == NULL) {
        throw RuntimeException("Failed to compile shader $filename into SPIR-V")
    }

    if (shaderc_result_get_compilation_status(result) != shaderc_compilation_status_success) {
        error("Failed to compile shader $filename into SPIR-V: ${shaderc_result_get_error_message(result)}")
    }

    writeToFile(shaderc_result_get_bytes(result)!!, filename)
    return SpirV(result, shaderc_result_get_bytes(result))
}

fun free() {
    shaderc_compiler_release(compiler)
}

fun writeToFile(bytecode: ByteBuffer, filename: String) {
//    FIXME: gitignore wont work on these files so i guess ill leave this out for now
//    val file = File("$filename-" + bytecode.hashCode().toString() + ".spriv")
//    file.parentFile.mkdirs()
//
//    val channel = FileOutputStream(file, false).channel
//    channel.write(bytecode)
//    channel.close()
}

class SpirV(private val handle: Long, private var bytecode: ByteBuffer?) : NativeResource {
    fun bytecode(): ByteBuffer {
        return bytecode!!
    }

    override fun free() {
        shaderc_result_release(handle)
        bytecode = null
    }
}
