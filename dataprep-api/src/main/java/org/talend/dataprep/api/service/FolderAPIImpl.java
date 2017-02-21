// ============================================================================
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// https://github.com/Talend/data-prep/blob/master/LICENSE
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package org.talend.dataprep.api.service;

import static org.talend.daikon.exception.ExceptionContext.build;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Queue;
import java.io.OutputStream;

import org.apache.commons.lang.StringUtils;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.talend.daikon.annotation.ServiceImplementation;
import org.talend.dataprep.api.dataset.DataSetMetadata;
import org.talend.dataprep.api.folder.Folder;
import org.talend.dataprep.api.service.api.EnrichedPreparation;
import org.talend.dataprep.api.service.command.folder.*;
import org.talend.dataprep.api.service.command.preparation.PreparationListByFolder;
import org.talend.dataprep.command.CommandHelper;
import org.talend.dataprep.command.GenericCommand;
import org.talend.dataprep.command.dataset.DataSetGetMetadata;
import org.talend.dataprep.dataset.DataSetMetadataBuilder;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.APIErrorCodes;
import org.talend.dataprep.preparation.service.UserPreparation;
import org.talend.dataprep.security.SecurityProxy;
import org.talend.dataprep.util.SortAndOrderHelper;
import org.talend.dataprep.util.SortAndOrderHelper.Order;
import org.talend.dataprep.util.SortAndOrderHelper.Sort;
import org.talend.services.dataprep.api.FolderAPI;

import com.fasterxml.jackson.core.JsonGenerator;
import com.netflix.hystrix.HystrixCommand;

import reactor.core.publisher.Flux;

@ServiceImplementation
public class FolderAPIImpl extends APIService implements FolderAPI {

    @Autowired
    private DataSetMetadataBuilder metadataBuilder;

    /** Security proxy let the current thread to borrow another identity for a while. */
    @Autowired
    private SecurityProxy securityProxy;

    private static <T> void writeFluxToJsonArray(Flux<T> flux, String arrayElement, JsonGenerator generator) {
        flux.subscribe(new WriteJsonArraySubscriber<>(generator, arrayElement));
    }

    @InitBinder
    private void initBinder(WebDataBinder binder) {
        // This allow to bind Sort and Order parameters in lower-case even if the key is uppercase.
        // URLs are cleaner in lowercase.
        binder.registerCustomEditor(Sort.class, SortAndOrderHelper.getSortPropertyEditor());
        binder.registerCustomEditor(Order.class, SortAndOrderHelper.getOrderPropertyEditor());
    }

    @Override
    public ResponseEntity<StreamingResponseBody> children(String parentId) {
        try {
            final GenericCommand<InputStream> foldersList = getCommand(FolderChildrenList.class, parentId);
            return CommandHelper.toStreaming(foldersList);
        } catch (Exception e) {
            throw new TDPException(APIErrorCodes.UNABLE_TO_LIST_FOLDERS, e);
        }
    }

    @Override
    public StreamingResponseBody getTree() {
        try {
            final HystrixCommand<InputStream> foldersList = getCommand(FolderTree.class);
            return CommandHelper.toStreaming(foldersList);
        } catch (Exception e) {
            throw new TDPException(APIErrorCodes.UNABLE_TO_LIST_FOLDERS, e);
        }
    }

    @Override
    public StreamingResponseBody getFolderAndHierarchyById(String id) {
        try {
            final HystrixCommand<InputStream> foldersList = getCommand(GetFolder.class, id);
            return CommandHelper.toStreaming(foldersList);
        } catch (Exception e) {
            throw new TDPException(APIErrorCodes.UNABLE_TO_GET_FOLDERS, e);
        }
    }

    @Override
    public StreamingResponseBody addFolder(String parentId, String path) {
        try {
            final HystrixCommand<InputStream> createChildFolder = getCommand(CreateChildFolder.class, parentId, path);
            return CommandHelper.toStreaming(createChildFolder);
        } catch (Exception e) {
            throw new TDPException(APIErrorCodes.UNABLE_TO_CREATE_FOLDER, e);
        }
    }

    @Override
    public ResponseEntity<String> removeFolder(String id) {
        try {
            return getCommand(RemoveFolder.class, id).execute();
        } catch (Exception e) {
            throw new TDPException(APIErrorCodes.UNABLE_TO_DELETE_FOLDER, e);
        }
    }

    @Override
    public void renameFolder(String id, String newName) {
        if (StringUtils.isEmpty(id) || StringUtils.isEmpty(newName)) {
            throw new TDPException(APIErrorCodes.UNABLE_TO_RENAME_FOLDER);
        }

        try {
            final HystrixCommand<Void> renameFolder = getCommand(RenameFolder.class, id, newName);
            renameFolder.execute();
        } catch (Exception e) {
            throw new TDPException(APIErrorCodes.UNABLE_TO_RENAME_FOLDER, e);
        }
    }

    @Override
    public ResponseEntity<StreamingResponseBody> search(String name, boolean strict) {
        try {
            final GenericCommand<InputStream> searchFolders = getCommand(SearchFolders.class, name, strict);
            return CommandHelper.toStreaming(searchFolders);
        } catch (Exception e) {
            throw new TDPException(APIErrorCodes.UNABLE_TO_LIST_FOLDERS, e);
        }
    }

    @Override
    public StreamingResponseBody listPreparationsByFolder(String id, Sort sort, Order order) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Listing preparations in destination {} (pool: {} )...", id, getConnectionStats());
        }

        return output -> {
            try (final JsonGenerator generator = mapper.getFactory().createGenerator(output)) {
                generator.writeStartObject();
                // Folder list
                final FolderChildrenList commandListFolders = getCommand(FolderChildrenList.class, id, sort, order);
                final Flux<Folder> folders = Flux.from(CommandHelper.toPublisher(Folder.class, mapper, commandListFolders));
                writeFluxToJsonArray(folders, "folders", generator);
                // Preparation list
                final PreparationListByFolder listPreparations = getCommand(PreparationListByFolder.class, id, sort, order);

                final Flux<UserPreparation> preparations = Flux
                        .from(CommandHelper.toPublisher(UserPreparation.class, mapper, listPreparations)) // From preparation list
                        .map(preparation -> {
                            UserPreparation ep;
                            if (preparation.getDataSetId() == null) {
                                ep = preparation;
                            } else {
                                // get the dataset metadata
                                try {
                                    securityProxy.asTechnicalUser(); // because dataset are not shared
                                    ep = new EnrichedPreparation(preparation, getCommand(DataSetGetMetadata.class, preparation.getDataSetId()).execute());
                                } catch (Exception e) {
                                    ep = preparation;
                                    LOG.debug("error reading dataset metadata {} : {}", preparation.getId(), e);
                                } finally {
                                    securityProxy.releaseIdentity();
                                }
                            }
                            return ep;
                        });
                writeFluxToJsonArray(preparations, "preparations", generator);
                generator.writeEndObject();
            } catch (EOFException e) {
                LOG.debug("Output stream has been closed before finishing preparation writing.", e);
            } catch (IOException e) {
                throw new TDPException(APIErrorCodes.UNABLE_TO_LIST_FOLDER_ENTRIES, e, build().put("destination", id));
            }
        };
    }

    private static class WriteJsonArraySubscriber<T> implements Subscriber<T> {

        private final JsonGenerator generator;

        private final String arrayElement;

        private Subscription subscription;

        public WriteJsonArraySubscriber(JsonGenerator generator, String arrayElement) {
            this.generator = generator;
            this.arrayElement = arrayElement;
        }

        @Override
        public void onSubscribe(Subscription s) {
            try {
                generator.writeArrayFieldStart(arrayElement);
            } catch (EOFException eofe) {
                LOG.debug("JsonGenerator was closed before finish streaming.", eofe);
                subscription.cancel();
            } catch (IOException e) {
                LOG.error("Unable to write content.", e);
            }
            subscription = s;
            s.request(Long.MAX_VALUE);
        }

        @Override
        public void onNext(T aLong) {
            try {
                generator.writeObject(aLong);
            } catch (EOFException eofe) {
                LOG.debug("JsonGenerator was closed before finish streaming.", eofe);
                subscription.cancel();
            } catch (IOException e) {
                LOG.error("Unable to write content.", e);
            }
        }

        @Override
        public void onError(Throwable t) {
            onComplete();
        }

        @Override
        public void onComplete() {
            try {
                generator.writeEndArray();
            } catch (EOFException eofe) {
                LOG.debug("JsonGenerator was closed before finish streaming.", eofe);
            } catch (IOException e) {
                LOG.error("Unable to write content.", e);
            }
        }
    }
}
