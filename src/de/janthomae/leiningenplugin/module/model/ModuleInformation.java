package de.janthomae.leiningenplugin.module.model;

/**
 * Java Bean to represent data which we present to the user for Leiningen Modules.
 */
public class ModuleInformation {
    private String groupId;
    private String artifactId;
    private String version;
    private String projectFilePath;

    public ModuleInformation() {
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(final String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(final String artifactId) {
        this.artifactId = artifactId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    public String getProjectFilePath() {
        return projectFilePath;
    }

    public void setProjectFilePath(final String projectFilePath) {
        this.projectFilePath = projectFilePath;
    }
}