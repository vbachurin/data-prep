package org.talend.dataprep.info;

import java.io.IOException;
import java.util.Scanner;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.elasticsearch.common.lang3.StringUtils;

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

    /**
     * default message when some version info is missing
     */
    public static final String UNKNOWN = "???";

    /**
     * The unique instance of this singleton class
     */
    private static ManifestInfo uniqueInstance;

    /**
     * Constructor
     *
     * @param versionId the version ID
     * @param buildId the build ID
     */
    public ManifestInfo(String versionId, String buildId) {
        this.versionId = versionId;
        this.buildId = buildId;
    }

    /**
     * Retrieve the attributes from the jar manifest file.
     * 
     * @return
     */
    private static Attributes retrieveManifestAttributes() {
        Manifest mf = new Manifest();
        Attributes attributes = null;
        try {
            mf.read((new Version()).getClass().getClassLoader().getResourceAsStream("META-INF/MANIFEST.MF"));
            attributes = mf.getMainAttributes();
        } catch (IOException ie) {

        }
        return attributes;
    }

    /**
     * static initializer
     */
    static {
        Attributes attributes = retrieveManifestAttributes();
        String versionId = null;
        String buildId = null;

        if (attributes != null) {
            versionId = attributes.getValue("Implementation-Version");
            buildId = attributes.getValue("SHA-Build");
            // remove the hyphen if present
            if (StringUtils.isNotEmpty(versionId) && StringUtils.contains(versionId, '-')) {
                Scanner scanner = new Scanner(versionId).useDelimiter("-");
                if (scanner.hasNext()) {
                    versionId = scanner.next();
                }
            }
        }
        if (StringUtils.isEmpty(versionId)) {
            versionId = UNKNOWN;
        }
        if (StringUtils.isEmpty(buildId)) {
            buildId = UNKNOWN;
        }
        uniqueInstance = new ManifestInfo(versionId, buildId);
    }

    /**
     * Return the unique instance.
     *
     * @return
     */
    public static ManifestInfo getUniqueInstance() {
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