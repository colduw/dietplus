package com.illusivesoulworks.diet.update;

public class UpdateResult {
    public VerStates verStates;
    public String latestVersion;
    public String recommendedVersion;
    public String currentVersion;

    public UpdateResult(VerStates verStates, String latestVersion, String recommendedVersion, String currentVersion) {
        this.verStates = verStates;
        this.latestVersion = latestVersion;
        this.recommendedVersion = recommendedVersion;
        this.currentVersion = currentVersion;
    }
}
