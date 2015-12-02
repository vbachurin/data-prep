package org.talend.dataprep.info;

import org.elasticsearch.common.lang3.StringUtils;

import java.io.IOException;
import java.util.Scanner;
import java.util.jar.Manifest;
import java.util.jar.Attributes;

/**
 * Contains the information info the version of running application
 */
public class ManifestInfo {

    /**
     * The version ID
     */
    private final String versionId;

    /**
     * The ID (from the SCM) of the source's version of the running application
     */
    private final String buildId;

    public static final String UNKNOWN = "???";

    /**
     * The unique instance of this singleton class
     */
    private static ManifestInfo uniqueInstance;

    /**
     * Constructor
     * @param versionId the version ID
     * @param buildId the build ID
     */
    public ManifestInfo(String versionId, String buildId) {
        this.versionId = versionId;
        this.buildId = buildId;
    }

    /**
     * static initializer
     */
    static {
        Manifest mf = new Manifest();
        try {
            mf.read((new ManifestInfo("", "")).getClass().getClassLoader().getResourceAsStream("META-INF/MANIFEST.MF"));
            Attributes attributes = mf.getMainAttributes();
            String versionId = attributes.getValue("Implementation-Version");
            // remove the hyphen if present
            if (StringUtils.isNotEmpty(versionId)&& StringUtils.contains(versionId, '-')){
                Scanner scanner = new Scanner(versionId).useDelimiter("-");
                versionId = scanner.next();
            }
            String buildId = attributes.getValue("SHA-Build");
            uniqueInstance = new ManifestInfo(versionId,buildId);
        }
        catch (IOException ie){
            uniqueInstance = new ManifestInfo(UNKNOWN, UNKNOWN);
            System.out.println("version error");
        }
    }

    /**
     * Return the unique instance
     * @return
     */
    public static ManifestInfo getUniqueInstance(){
        return uniqueInstance;
    }

    /**
     *
     * @return the version of this running application
     */
    public String getVersionId() {
        return versionId;
    }

    /**
     *
     * @return the SHA build
     */
    public String getBuildId() {
        return buildId;
    }

}