package org.example.models;

import java.util.UUID;

public class Package {
    private UUID packageId;

    public Package() {}

    public Package(UUID packageId) {
        this.packageId = packageId;
    }

    // Getter und Setter
    public UUID getPackageId() {
        return packageId;
    }

    public void setPackageId(UUID packageId) {
        this.packageId = packageId;
    }
    
}