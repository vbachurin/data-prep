package org.talend.dataprep.api.service;

import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.*;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.HttpClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.talend.dataprep.api.folder.FolderContent;
import org.talend.dataprep.api.folder.FolderEntry;
import org.talend.dataprep.api.service.command.folder.AllFoldersList;
import org.talend.dataprep.api.service.command.folder.CreateChildFolder;
import org.talend.dataprep.api.service.command.folder.CreateFolderEntry;
import org.talend.dataprep.api.service.command.folder.FolderDataSetList;
import org.talend.dataprep.api.service.command.folder.FolderEntriesList;
import org.talend.dataprep.api.service.command.folder.FoldersList;
import org.talend.dataprep.api.service.command.folder.RemoveFolder;
import org.talend.dataprep.api.service.command.folder.RemoveFolderEntry;
import org.talend.dataprep.api.service.command.folder.RenameFolder;
import org.talend.dataprep.api.service.mail.FeedbackInfo;
import org.talend.dataprep.api.service.mail.SendFeedback;
import org.talend.dataprep.exception.TDPException;
import org.talend.dataprep.exception.error.APIErrorCodes;
import org.talend.dataprep.exception.error.CommonErrorCodes;
import org.talend.dataprep.metrics.Timed;
import org.talend.dataprep.metrics.VolumeMetered;

import com.netflix.hystrix.HystrixCommand;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@RestController @Api(value = "api", basePath = "/api", description = "Send feedback to Talend") public class MailServiceAPI
        extends APIService {

    @RequestMapping(value = "/api/mail", method = PUT) @ApiOperation(value = "Send feedback to Talend") @Timed public void mailTo(
            @RequestBody FeedbackInfo feedBack) {
        try {
            final HystrixCommand<Void> sendFeedback = getCommand(SendFeedback.class, feedBack);
            sendFeedback.execute();

        } catch (Exception e) {
            throw new TDPException(APIErrorCodes.UNABLE_TO_FIND_COMMAND, e);
        }
    }

}
