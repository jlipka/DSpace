/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.app.rest.repository;
import java.io.IOException;
import java.sql.SQLException;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.dspace.app.rest.exception.DSpaceBadRequestException;
import org.dspace.app.rest.exception.DSpaceFeedbackNotFoundException;
import org.dspace.app.rest.exception.RepositoryMethodNotImplementedException;
import org.dspace.app.rest.exception.UnprocessableEntityException;
import org.dspace.app.rest.model.FeedbackRest;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.service.FeedbackService;
import org.dspace.core.Context;
import org.dspace.services.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

/**
 * This is the Repository that takes care of the operations on the {@link FeedbackRest} objects
 * 
 * @author Mykhaylo Boychuk (mykhaylo.boychuk@4science.com)
 */
@Component(FeedbackRest.CATEGORY + "." + FeedbackRest.PLURAL_NAME)
public class FeedbackRestRepository extends DSpaceRestRepository<FeedbackRest, Integer> {

    @Autowired
    private FeedbackService feedbackService;
    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private ObjectMapper mapper;

    @Override
    @PreAuthorize("hasAuthority('AUTHENTICATED')")
    public Page<FeedbackRest> findAll(Context context, Pageable pageable) {
        throw new RepositoryMethodNotImplementedException(FeedbackRest.NAME, "findAll");
    }

    @Override
    @PreAuthorize("hasAuthority('AUTHENTICATED')")
    public FeedbackRest findOne(Context context, Integer id) {
        throw new RepositoryMethodNotImplementedException(FeedbackRest.NAME, "findOne");
    }

    @Override
    @PreAuthorize("permitAll()")
    protected FeedbackRest createAndReturn(Context context) throws AuthorizeException, SQLException {
        HttpServletRequest req = getRequestService().getCurrentRequest().getHttpServletRequest();
        FeedbackRest feedbackRest = null;

        String recipientEmail = configurationService.getProperty("feedback.recipient");
        if (StringUtils.isBlank(recipientEmail)) {
            throw new DSpaceFeedbackNotFoundException("Feedback cannot be sent at this time, Feedback recipient " +
                "is disabled");
        }

        try {
            feedbackRest = mapper.readValue(req.getInputStream(), FeedbackRest.class);
        } catch (IOException exIO) {
            throw new UnprocessableEntityException("error parsing the body " + exIO.getMessage(), exIO);
        }

        String senderEmail = feedbackRest.getEmail();
        String message = feedbackRest.getMessage();

        if (StringUtils.isBlank(senderEmail) || StringUtils.isBlank(message)) {
            throw new DSpaceBadRequestException("e-mail and message fields are mandatory!");
        }

        String pageUrl = feedbackRest.getPage();
        String urlPrefix = configurationService.getProperty("dspace.ui.url");
        if (StringUtils.isNotBlank(pageUrl) && ! StringUtils.startsWith(pageUrl, urlPrefix)) {
            throw new DSpaceBadRequestException("unexpected page url was submitted");
        }

        try {
            feedbackService.sendEmail(context, req, recipientEmail, senderEmail, message, pageUrl);
        } catch (IOException | MessagingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Class<FeedbackRest> getDomainClass() {
        return FeedbackRest.class;
    }

    public FeedbackService getFeedbackService() {
        return feedbackService;
    }

    public void setFeedbackService(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

}
