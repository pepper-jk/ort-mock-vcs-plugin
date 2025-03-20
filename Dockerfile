ARG ort_rev=latest
FROM ghcr.io/oss-review-toolkit/ort:${ort_rev}
COPY build/libs/MockVcsPlugin.jar /opt/ort/plugin/
