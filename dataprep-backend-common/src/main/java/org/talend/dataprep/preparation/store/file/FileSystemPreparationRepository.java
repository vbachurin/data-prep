package org.talend.dataprep.preparation.store.file;

import static org.talend.dataprep.api.preparation.PreparationActions.ROOT_CONTENT;
import static org.talend.dataprep.api.preparation.Step.ROOT_STEP;

import java.io.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.talend.dataprep.api.preparation.Identifiable;
import org.talend.dataprep.api.preparation.Preparation;
import org.talend.dataprep.exception.TDPException;
import org.talend.daikon.exception.TalendExceptionContext;
import org.talend.dataprep.exception.error.CommonErrorCodes;
import org.talend.dataprep.preparation.store.PreparationRepository;

/**
 * File system implementation of preparation repository.
 */
@Component
@ConditionalOnProperty(name = "preparation.store", havingValue = "file")
public class FileSystemPreparationRepository implements PreparationRepository {

    /** This class' logger. */
    private static final Logger LOG = LoggerFactory.getLogger(FileSystemPreparationRepository.class);

    /** Where to store the dataset metadata */
    @Value("${preparation.store.file.location}")
    private String preparationsLocation;

    /**
     * Make sure the root folder is there.
     */
    @PostConstruct
    private void init() {
        getRootFolder().mkdirs();
        add(ROOT_CONTENT);
        add(ROOT_STEP);
    }

    /**
     * @see PreparationRepository#add(Identifiable)
     */
    @Override
    public void add(Identifiable object) {

        // defensive programming
        if (object == null) {
            LOG.warn("cannot save null...");
            return;
        }

        final File outputFile = getPreparationFile(object.id());

        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(outputFile))) {
            output.writeObject(object);
        } catch (IOException e) {
            LOG.error("Error saving {}", object, e);
            throw new TDPException(CommonErrorCodes.UNABLE_TO_SAVE_PREPARATION, e,
                    TalendExceptionContext.build().put("id", object.id()));
        }
        LOG.debug("preparation #{} saved", object.id());

    }

    /**
     * @see PreparationRepository#get(String, Class)
     */
    @Override
    public <T extends Identifiable> T get(String id, Class<T> clazz) {

        final File from = getPreparationFile(id);
        if (!from.exists()) {
            LOG.info("preparation #{} not found in file system", id);
            return null;
        }

        try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(from))) {
            final Object result = input.readObject();

            if (clazz == null) {
                return (T) result;
            } else if (clazz.isAssignableFrom(result.getClass())) {
                return clazz.cast(result);
            } else {
                // shit happens...
                return null;
            }

        } catch (ClassNotFoundException | IOException e) {
            throw new TDPException(CommonErrorCodes.UNABLE_TO_PREPARATION, e, TalendExceptionContext.build().put("id", id));
        }
    }

    /**
     * @see PreparationRepository#getByDataSet(String)
     */
    @Override
    public Collection<Preparation> getByDataSet(String dataSetId) {
        // defensive programming
        if (StringUtils.isEmpty(dataSetId)) {
            return Collections.emptyList();
        }

        // first filter on the class (listAll()) and then second filter on the dataset id
        return listAll(Preparation.class) //
                .stream() //
                .filter(preparation -> dataSetId.equals(preparation.getDataSetId())) //
                .collect(Collectors.toList());
    }

    /**
     * @see PreparationRepository#listAll(Class)
     */
    @Override
    public <T extends Identifiable> Collection<T> listAll(Class<T> clazz) {
        //@formatter:off
        Collection<T> result =  Arrays.stream(getRootFolder().listFiles())
                .map(file ->  readRaw(file))                                // read all files
                .filter(entry -> clazz.isAssignableFrom(entry.getClass()))  // filter out the unwanted objects
                .map(entry -> (T) entry)                                    // cast to wanted class
                .collect(Collectors.toSet());                               // and put it in a set
        //@formatter:on
        LOG.debug("There are {} for class {}", result.size(), clazz.getName());
        return result;
    }

    /**
     * Return the raw object from the given file.
     *
     * @param source where to read.
     * @return the raw object from the given file
     */
    private Object readRaw(File source) {
        try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(source))) {
            return input.readObject();
        } catch (ClassNotFoundException | IOException e) {
            LOG.error("error reading preparation file {}", source.getAbsolutePath(), e);
            return null;
        }
    }

    /**
     * @see PreparationRepository#clear()
     */
    @Override
    public void clear() {

        // clear all files
        final File[] preparations = getRootFolder().listFiles();
        for (File file : preparations) {
            if (!isNFSSpecificFile(file)) {
                file.delete();
            }
        }

        // add the default files
        add(ROOT_CONTENT);
        add(ROOT_STEP);

        LOG.debug("preparation repository cleared");
    }

    /**
     * Specific operating system NFS files must be left to the OS and cannot be deleted.
     * 
     * @param file the file to inspect.
     * @return True if the given file is NFS specific (starts with ".nfs")
     */
    private boolean isNFSSpecificFile(File file) {
        return file.getName().startsWith(".nfs");
    }

    /**
     * @see PreparationRepository#remove(Identifiable)
     */
    @Override
    public void remove(Identifiable object) {
        if (object == null) {
            return;
        }
        final File file = getPreparationFile(object.id());
        if (file.exists() && !isNFSSpecificFile(file)) {
            file.delete();
        }
        LOG.debug("preparation #{} removed", object.id());
    }

    /**
     * Return the file that matches the given identifiable id.
     *
     * @param id the identifiable... id !
     * @return the file where to read/write the identifiable object.
     */
    private File getPreparationFile(String id) {
        return new File(preparationsLocation + '/' + id);
    }

    /**
     * Return the root folder where the preparations are stored.
     * 
     * @return the root folder.
     */
    private File getRootFolder() {
        return new File(preparationsLocation);
    }
}
