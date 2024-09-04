# About

This Mock VCS plugin acts as a fallback, should no supported VCS be detected,
as it is of `VcsType.UNKOWN` and has priority `0`. However, it provides the all
required interfaces of a `VersionControlSystem` for ORT to function as if a VCS
were present. This makes it possible to scan software projects with no version
control or unsupported VCSs.

The plugin is kept simple and provides mostly hard coded return values.
- It's `DummyWorkingTree` is empty and only knows revision `main`.
- As the `RemoteURL` the tree returns the local `workingDir` (given via `-i`).
- Upon calling `initWorkingTree`, the plugin copies content of the `vcs.url`
  into the appropriate cache directory. So ORT can scan the project as is.

The template Gradle project was forked from [ort-package-manager-plugin],
which can be used to create a plugin for the [OSS Review Toolkit].

# Usage

1. Clone this repository
2. Execute `InstallDist` job in gradle
3. Take the resulting `build/install/MockVcsPlugin/MockVcsPlugin.jar` and place it in the `ort/plugin` directory of your ORT installation.
4. Execute ort from `ort/bin/ort`.

[ort-package-manager-plugin]: https://github.com/oss-review-toolkit/ort-package-manager-plugin
[OSS Review Toolkit]: https://github.com/oss-review-toolkit/ort
