package org.ossreviewtoolkit.plugins.versioncontrolsystems.mock

import java.io.File

import org.ossreviewtoolkit.downloader.VersionControlSystem
import org.ossreviewtoolkit.downloader.VersionControlSystemFactory
import org.ossreviewtoolkit.downloader.WorkingTree
import org.ossreviewtoolkit.model.VcsInfo
import org.ossreviewtoolkit.model.VcsType
import org.ossreviewtoolkit.plugins.api.OrtPlugin
import org.ossreviewtoolkit.plugins.api.PluginDescriptor
import org.ossreviewtoolkit.utils.common.CommandLineTool
import org.ossreviewtoolkit.utils.common.ProcessCapture

object MockCommand : CommandLineTool {
    override fun command(workingDir: File?) = ""

    // we do not need this, but override it, so nothing gets executed by accident
    override fun transformVersion(output: String): String = ""

    override fun displayName(): String = "Mock"

    override fun isInPath(): Boolean = true

    override fun run(vararg args: CharSequence, workingDir: File?, environment: Map<String, String>) = ProcessCapture()

    override fun getVersion(workingDir: File?): String = "1.0"

    override fun checkVersion(workingDir: File?) {
        return
    }
}

@OrtPlugin(
    displayName = "MockVCS",
    description = "A VCS implementation, that acts as a fallback, should no supported VCS be detected.",
    factory = VersionControlSystemFactory::class
)
class MockVcs(
    override val descriptor: PluginDescriptor,
    private val config: MockConfig
) : VersionControlSystem() {
    override val type = VcsType.UNKNOWN
    override val priority = 0
    override val latestRevisionNames = listOf("HEAD", "@")

    override fun getVersion() = config.mockVersion

    override fun getDefaultBranchName(url: String) = config.mockDefaultBranchName

    override fun getWorkingTree(vcsDirectory: File): WorkingTree = MockWorkingTree(vcsDirectory, type)

    override fun isAvailable(): Boolean = MockCommand.isInPath()

    override fun isApplicableUrlInternal(vcsUrl: String): Boolean = true

    override fun initWorkingTree(targetDir: File, vcs: VcsInfo): WorkingTree {
        File(vcs.url).copyRecursively(targetDir, overwrite = true)
        return getWorkingTree(targetDir)
    }

    override fun updateWorkingTree(
        workingTree: WorkingTree,
        revision: String,
        path: String,
        recursive: Boolean
    ): Result<String> = Result.success(revision)
}
