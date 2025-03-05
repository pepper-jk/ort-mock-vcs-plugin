package org.ossreviewtoolkit.plugins.versioncontrolsystems.mock

/** MockVCS-specific [org.ossreviewtoolkit.downloader.VersionControlSystem] configuration. */
import org.ossreviewtoolkit.downloader.VersionControlSystem
import org.ossreviewtoolkit.plugins.api.OrtPluginOption

const val DEFAULT_HISTORY_DEPTH = 1

/** MockVCS-specific [VersionControlSystem] configuration. */
data class MockConfig(
    /** Version of Mock VCS. Useful in case of unsupported VCS. */
    @OrtPluginOption(defaultValue = "")
    val mockVersion: String,

    /** Branch name on Mock VCS. Useful in case of unsupported VCS. */
    @OrtPluginOption(defaultValue = "")
    val mockDefaultBranchName: String,
)
