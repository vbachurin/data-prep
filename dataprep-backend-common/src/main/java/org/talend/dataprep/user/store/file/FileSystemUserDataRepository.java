package org.talend.dataprep.user.store.file;

import java.io.*;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.user.UserData;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.TDPExceptionContext;
import org.talend.dataprep.exception.error.CommonErrorCodes;
import org.talend.dataprep.user.store.UserDataRepository;

/**
 * User data repository implementation backed on the file system.
 */
@Component
@ConditionalOnProperty(name = "user.data.store", havingValue = "file")
public class FileSystemUserDataRepository implements UserDataRepository {

    /** This class' logger. */
    private static final Logger LOG = LoggerFactory.getLogger(FileSystemUserDataRepository.class);

    /** Where to store the dataset metadata */
    @Value("${user.data.store.file.location}")
    private String storeLocation;

    /**
     * Make sure the root folder is there.
     */
    @PostConstruct
    private void init() {
        new File(storeLocation).mkdirs();
    }

    /**
     * @see UserDataRepository#get(String)
     */
    @Override
    public UserData get(String userId) {

        final File inputFile = getFile(userId);
        if (!inputFile.exists()) {
            LOG.info("user data #{} not found in file system", userId);
            return null;
        }

        if (!inputFile.canRead()) {
            LOG.info("user data #{} not available in file system, it is perhaps used by another thread ?", userId);
            return null;
        }

        try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(inputFile))) {
            return (UserData) input.readObject();
        } catch (ClassNotFoundException | IOException e) {
            throw new TDPException(CommonErrorCodes.UNABLE_TO_READ_USER_DATA, e, TDPExceptionContext.build().put("id", userId));
        }

    }

    /**
     * @see UserDataRepository#save(UserData)
     */
    @Override
    public void save(UserData userData) {
        // defensive programming
        if (userData == null) {
            LOG.warn("cannot save a null user data.");
            return;
        }
        final File outputFile = getFile(userData.getUserId());

        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(outputFile))) {
            output.writeObject(userData);
        } catch (IOException e) {
            LOG.error("Error saving {}", userData, e);
            throw new TDPException(CommonErrorCodes.UNABLE_TO_SAVE_USER_DATA, e);
        }
        LOG.debug("user data #{} saved", userData.getUserId());
    }

    /**
     * @see UserDataRepository#remove(String)
     */
    @Override
    public void remove(String userId) {
        final File userDataFile = getFile(userId);
        if (userDataFile.exists()) {
            userDataFile.delete();
        }
        LOG.debug("user data {} successfully deleted", userId);
    }

    /**
     * @see UserDataRepository#clear()
     */
    @Override
    public void clear() {
        final File rootFolder = new File(storeLocation);
        final File[] files = rootFolder.listFiles();
        for (File file : files) {
            file.delete();
        }
        LOG.debug("user data repository cleared");
    }

    /**
     * Return the file that matches the given metadata user id.
     *
     * @param userId the muser id.
     * @return the file where to read/write the user.
     */
    private File getFile(String userId) {
        return new File(storeLocation + '/' + userId);
    }

}
