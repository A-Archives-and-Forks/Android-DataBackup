package com.xayah.databackup.util

import android.system.Os
import com.xayah.databackup.App
import com.xayah.databackup.rootservice.RemoteRootService
import com.xayah.databackup.util.PathHelper.TMP_FIFO_PREFIX
import com.xayah.databackup.util.PathHelper.TMP_SUFFIX
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream

object ZstdHelper {
    const val TAG = "ZstdHelper"

    suspend fun packageAndCompress(outputPath: String, vararg inputArgs: String): Pair<Int, String> {
        val args = mutableListOf("tar", "--totals", "-cpf", "-")
        var status = 0
        var info = ""

        val stdOut = File.createTempFile(TMP_FIFO_PREFIX, TMP_SUFFIX, App.application.cacheDir)
        stdOut.delete()
        Os.mkfifo(stdOut.path, 420)
        val stdErr = File.createTempFile(TMP_FIFO_PREFIX, TMP_SUFFIX, App.application.cacheDir)
        stdErr.delete()
        Os.mkfifo(stdErr.path, 420)
        runCatching {
            withContext(Dispatchers.IO) {
                val getStdErr = async(Dispatchers.IO) {
                    runCatching {
                        FileInputStream(stdErr).use { fileInputStream ->
                            fileInputStream.bufferedReader().use { bufferedReader ->
                                info = bufferedReader.readText()
                            }
                        }
                    }.onFailure {
                        val msg = "Failed to get std err."
                        LogHelper.e(TAG, "packageAndCompress", msg, it)
                        ShellHelper.killRootService()
                        status = -1
                        info = msg
                        throw IllegalStateException()
                    }
                }

                val getStdOut = async(Dispatchers.IO) {
                    var result: String? = null
                    var tr: Throwable? = null
                    runCatching {
                        result = RemoteRootService.compress(1, stdOut.path, outputPath)
                    }.onFailure {
                        val msg = "Failed to get std out."
                        result = msg
                        tr = it
                    }
                    if (result != null) {
                        ShellHelper.killRootService()
                        LogHelper.e(TAG, "packageAndCompress", result, tr)
                        status = -1
                        info = result
                        throw IllegalStateException()
                    }
                }

                val callTarCli = async(Dispatchers.IO) {
                    runCatching {
                        args.addAll(inputArgs)
                        status = RemoteRootService.callTarCli(
                            stdOut = stdOut.path,
                            stdErr = stdErr.path,
                            argv = args.toTypedArray()
                        )
                    }.onFailure {
                        val msg = "Failed to call tar cli."
                        LogHelper.e(TAG, "packageAndCompress", msg, it)
                        ShellHelper.killRootService()
                        status = -1
                        info = msg
                        throw IllegalStateException()
                    }
                }

                getStdErr.await()
                getStdOut.await()
                callTarCli.await()
            }
        }

        stdOut.delete()
        stdErr.delete()

        LogHelper.i(TAG, "packageAndCompress", "args:\n$args\nstatus: $status\ninfo:\n$info")

        return status to info
    }
}
